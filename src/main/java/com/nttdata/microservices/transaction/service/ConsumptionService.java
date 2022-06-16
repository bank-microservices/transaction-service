package com.nttdata.microservices.transaction.service;

import com.nttdata.microservices.transaction.service.dto.ConsumptionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConsumptionService {

  Mono<ConsumptionDto> findById(String id);

  Flux<ConsumptionDto> findByCreditId(String creditId);

  Mono<ConsumptionDto> addConsumption(ConsumptionDto consumptionDto);

}
