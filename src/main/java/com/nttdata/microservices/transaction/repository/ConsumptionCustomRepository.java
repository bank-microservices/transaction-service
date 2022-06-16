package com.nttdata.microservices.transaction.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ConsumptionCustomRepository {

  Mono<Double> getSumByCreditId(String creditId);
}
