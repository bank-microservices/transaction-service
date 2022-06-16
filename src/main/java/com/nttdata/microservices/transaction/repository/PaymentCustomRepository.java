package com.nttdata.microservices.transaction.repository;

import reactor.core.publisher.Mono;

public interface PaymentCustomRepository {

  Mono<Double> getSumByCreditId(String creditId);

}
