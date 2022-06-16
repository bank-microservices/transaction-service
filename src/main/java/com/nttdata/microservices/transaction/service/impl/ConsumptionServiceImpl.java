package com.nttdata.microservices.transaction.service.impl;

import com.nttdata.microservices.transaction.entity.Consumption;
import com.nttdata.microservices.transaction.exception.BadRequestException;
import com.nttdata.microservices.transaction.exception.CreditCardNotFoundException;
import com.nttdata.microservices.transaction.exception.CreditNotFoundException;
import com.nttdata.microservices.transaction.proxy.CreditProxy;
import com.nttdata.microservices.transaction.repository.ConsumptionRepository;
import com.nttdata.microservices.transaction.service.ConsumptionService;
import com.nttdata.microservices.transaction.service.dto.ConsumptionDto;
import com.nttdata.microservices.transaction.service.mapper.ConsumptionMapper;
import com.nttdata.microservices.transaction.service.mapper.CreditMapper;
import com.nttdata.microservices.transaction.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.nttdata.microservices.transaction.util.MessageUtils.getMsg;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumptionServiceImpl implements ConsumptionService {

  private final ConsumptionRepository consumptionRepository;

  private final CreditProxy creditProxy;

  private final ConsumptionMapper consumptionMapper;
  private final CreditMapper creditMapper;

  @Override
  public Flux<ConsumptionDto> findAll() {
    return consumptionRepository.findAll()
            .map(consumptionMapper::toDto);
  }

  @Override
  public Mono<ConsumptionDto> findById(String id) {
    return consumptionRepository.findById(id)
            .map(consumptionMapper::toDto);
  }

  @Override
  public Flux<ConsumptionDto> findByCreditAccountId(String creditId) {
    return consumptionRepository.findByCreditId(creditId)
            .map(consumptionMapper::toDto);
  }

  @Override
  public Flux<ConsumptionDto> findByCreditAccountNumber(String accountNumber) {
    return consumptionRepository.findByCreditAccountNumber(accountNumber)
            .map(consumptionMapper::toDto);
  }

  @Override
  public Mono<ConsumptionDto> addConsumption(ConsumptionDto consumptionDto) {
    return Mono.just(consumptionDto)
            .flatMap(this::existCreditAccount)
            .flatMap(this::existCreditCard)
            .map(consumptionMapper::toEntity)
            .<Consumption>handle((consumption, sink) -> {
              log.debug("Consumption values: {}", consumption);
              Double creditAmountTotal = Double.sum(consumption.getCredit().getAmount(), consumption.getAmount());
              if (creditAmountTotal > consumption.getCredit().getCreditLimit()) {
                sink.error(new BadRequestException(getMsg("credit.exceeded.limit", creditAmountTotal, consumption.getAmount())));
              } else {
                sink.next(consumption);
              }
            })
            .map(consumption -> {
              consumption.setTransactionCode(NumberUtil.generateRandomNumber(8));
              consumption.setRegisterDate(LocalDateTime.now());
              return consumption;
            })
            .flatMap(consumptionRepository::save)
            .map(consumptionMapper::toDto)
            .flatMap(this::updateCreditAmount)
            .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<ConsumptionDto> existCreditCard(ConsumptionDto consumptionDto) {
    return this.creditProxy.findCreditCardByAccountNumber(consumptionDto.getAccountNumber())
            .switchIfEmpty(Mono.error(new CreditCardNotFoundException(getMsg("credit.card.not.found"))))
            .doOnNext(consumptionDto::setCreditCard)
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .thenReturn(consumptionDto);
  }

  private Mono<ConsumptionDto> existCreditAccount(ConsumptionDto consumptionDto) {
    return this.creditProxy.findByAccountNumber(consumptionDto.getAccountNumber())
            .switchIfEmpty(Mono.error(new CreditNotFoundException(getMsg("credit.not.found"))))
            .map(creditMapper::toDto)
            .doOnNext(consumptionDto::setCredit)
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .thenReturn(consumptionDto);
  }

  private Mono<ConsumptionDto> updateCreditAmount(ConsumptionDto consumptionDto) {
    return this.creditProxy.updateAmount(consumptionDto.getCredit().getId(), consumptionDto.getAmount())
            .doOnNext(consumptionDto::setCredit)
            .thenReturn(consumptionDto);
  }

}
