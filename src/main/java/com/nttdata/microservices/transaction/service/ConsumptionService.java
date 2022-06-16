package com.nttdata.microservices.transaction.service;

import com.nttdata.microservices.transaction.service.dto.ConsumptionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConsumptionService {

  Flux<ConsumptionDto> findAll();

  Mono<ConsumptionDto> findById(String id);

  Flux<ConsumptionDto> findByCreditAccountId(String creditId);

  Flux<ConsumptionDto> findByCreditAccountNumber(String accountNumber);

  Mono<ConsumptionDto> addConsumption(ConsumptionDto consumptionDto);

}
