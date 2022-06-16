package com.nttdata.microservices.transaction.controller;

import com.nttdata.microservices.transaction.service.ConsumptionService;
import com.nttdata.microservices.transaction.service.dto.ConsumptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/transaction/consumption")
@RequiredArgsConstructor
public class ConsumptionController {

  private final ConsumptionService consumptionService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<ConsumptionDto>> addConsumption(@Valid @RequestBody ConsumptionDto consumptionDto) {
    return consumptionService.addConsumption(consumptionDto)
            .map(ResponseEntity::ok)
            .onErrorReturn(WebClientResponseException.class, ResponseEntity.badRequest().build())
            .onErrorReturn(WebClientRequestException.class, ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
  }

  @GetMapping("/credit/{credit-id}")
  public Flux<ConsumptionDto> findByCreditId(@PathVariable("credit-id") String creditId) {
    return consumptionService.findByCreditId(creditId);
  }

  @GetMapping("/{id}")
  public Mono<ConsumptionDto> findById(@PathVariable("id") String id) {
    return consumptionService.findById(id);
  }

}
