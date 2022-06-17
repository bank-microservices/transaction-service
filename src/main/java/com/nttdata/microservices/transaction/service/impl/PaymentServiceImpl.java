package com.nttdata.microservices.transaction.service.impl;

import static com.nttdata.microservices.transaction.util.MessageUtils.getMsg;

import com.nttdata.microservices.transaction.exception.CreditNotFoundException;
import com.nttdata.microservices.transaction.proxy.CreditProxy;
import com.nttdata.microservices.transaction.repository.PaymentRepository;
import com.nttdata.microservices.transaction.service.PaymentService;
import com.nttdata.microservices.transaction.service.dto.PaymentDto;
import com.nttdata.microservices.transaction.service.mapper.CreditMapper;
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

  private final CreditProxy creditProxy;

  private final PaymentMapper paymentMapper;
  private final CreditMapper creditMapper;

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
        .flatMap(this::existCreditAccount)
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

  private Mono<PaymentDto> existCreditAccount(PaymentDto paymentDto) {
    return this.creditProxy.findByAccountNumber(paymentDto.getAccountNumber())
        .switchIfEmpty(Mono.error(new CreditNotFoundException(getMsg("credit.not.found"))))
        .map(creditMapper::toDto)
        .doOnNext(paymentDto::setCredit)
        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
        .thenReturn(paymentDto);
  }

  private Mono<PaymentDto> updateCreditAmount(PaymentDto paymentDto) {
    return this.creditProxy.updateAmount(paymentDto.getCredit().getId(),
            paymentDto.getAmount() * -1)
        .doOnNext(paymentDto::setCredit)
        .thenReturn(paymentDto);
  }

}
