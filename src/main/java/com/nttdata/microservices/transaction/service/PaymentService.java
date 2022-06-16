package com.nttdata.microservices.transaction.service;

import com.nttdata.microservices.transaction.service.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentService {

  Flux<PaymentDto> findAll();

  Mono<PaymentDto> findById(String id);

  Flux<PaymentDto> findByCreditAccountId(String creditId);

  Flux<PaymentDto> findByCreditAccountNumber(String accountNumber);

  Mono<PaymentDto> creditPayment(PaymentDto paymentDto);
}
