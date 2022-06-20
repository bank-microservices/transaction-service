package com.nttdata.microservices.transaction.service.impl;

import static com.nttdata.microservices.transaction.util.MessageUtils.getMsg;

import com.nttdata.microservices.transaction.exception.BadRequestException;
import com.nttdata.microservices.transaction.proxy.CreditProductProxy;
import com.nttdata.microservices.transaction.repository.PaymentRepository;
import com.nttdata.microservices.transaction.service.PaymentService;
import com.nttdata.microservices.transaction.service.dto.PaymentDto;
import com.nttdata.microservices.transaction.service.mapper.CreditProductMapper;
import com.nttdata.microservices.transaction.service.mapper.PaymentMapper;
import com.nttdata.microservices.transaction.util.NumberUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;

  private final CreditProductProxy creditProductProxy;

  private final PaymentMapper paymentMapper;
  private final CreditProductMapper creditProductMapper;

  @Override
  public Flux<PaymentDto> findAll() {
    return paymentRepository.findAll()
        .map(paymentMapper::toDto);
  }

  @Override
  public Mono<PaymentDto> findById(String id) {
    return paymentRepository.findById(id)
        .map(paymentMapper::toDto);
  }

  @Override
  public Flux<PaymentDto> findByCreditAccountId(String creditId) {
    return paymentRepository.findByCreditId(creditId)
        .map(paymentMapper::toDto);
  }

  @Override
  public Flux<PaymentDto> findByCreditAccountNumber(String accountNumber) {
    return paymentRepository.findByCreditAccountNumber(accountNumber)
        .map(paymentMapper::toDto);
  }

  @Override
  public Mono<PaymentDto> creditPayment(PaymentDto paymentDto) {
    return Mono.just(paymentDto)
        .flatMap(this::existCreditProduct)
        .<PaymentDto>handle((dto, sink) -> {
          final Double creditAmount = dto.getCreditProduct().getAmount();
          if (dto.getAmount() > creditAmount) {
            sink.error(new BadRequestException(getMsg("payment.amount.exceed",
                dto.getAmount(), creditAmount)));
          } else {
            sink.next(dto);
          }
        })
        .map(paymentMapper::toEntity)
        .map(payment -> {
          payment.setRegisterDate(LocalDateTime.now());
          payment.setTransactionCode(NumberUtil.generateRandomNumber(8));
          return payment;
        })
        .flatMap(paymentRepository::save)
        .map(paymentMapper::toDto)
        .flatMap(this::updateCreditAmount)
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<PaymentDto> existCreditProduct(PaymentDto paymentDto) {
    return creditProductProxy.findCreditOrCardByAccountNumber(paymentDto.getAccountNumber())
        .map(creditProductMapper::toDto)
        .doOnNext(paymentDto::setCreditProduct)
        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
        .thenReturn(paymentDto);
  }

  private Mono<PaymentDto> updateCreditAmount(PaymentDto paymentDto) {
    return Mono.just(paymentDto)
        .flatMap(
            dto -> this.creditProductProxy.updateCreditProductAmount(dto.getCreditProduct().getId(),
                paymentDto.getAmount() * -1,
                dto.getCreditProduct().getCreditProductType()))
        .map(creditProductMapper::toDto)
        .doOnNext(paymentDto::setCreditProduct)
        .thenReturn(paymentDto);
  }

}
