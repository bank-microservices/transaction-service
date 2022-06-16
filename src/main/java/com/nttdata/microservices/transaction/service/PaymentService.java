package com.nttdata.microservices.transaction.service;

import com.nttdata.microservices.transaction.service.dto.PaymentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentService {

  Mono<PaymentDto> findById(String id);

  Flux<PaymentDto> findByCreditId(String creditId);

  Mono<PaymentDto> creditPayment(PaymentDto paymentDto);
}
