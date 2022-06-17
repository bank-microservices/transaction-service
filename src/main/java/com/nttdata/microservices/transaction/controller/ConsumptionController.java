package com.nttdata.microservices.transaction.controller;

import com.nttdata.microservices.transaction.service.ConsumptionService;
import com.nttdata.microservices.transaction.service.dto.ConsumptionDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("api/v1/transaction/consumption")
@RequiredArgsConstructor
public class ConsumptionController {

  private final ConsumptionService consumptionService;

  @GetMapping
  public Flux<ConsumptionDto> getAll() {
    log.info("List of Consumptions");
    return consumptionService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<ConsumptionDto> findById(@PathVariable("id") String id) {
    log.info("find by Id of Consumptions");
    return consumptionService.findById(id);
  }

  @GetMapping("/credit/{credit-id}")
  public Flux<ConsumptionDto> findByCreditAccountId(@PathVariable("credit-id") String creditId) {
    log.info("find by creditId of Consumptions");
    return consumptionService.findByCreditAccountId(creditId);
  }

  @GetMapping("/credit-number/{account-number}")
  public Flux<ConsumptionDto> findByCreditAccountNumber(
      @PathVariable("account-number") String accountNumber) {
    log.info("find by accountNumber of Consumptions");
    return consumptionService.findByCreditAccountNumber(accountNumber);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<ConsumptionDto>> addConsumption(
      @Valid @RequestBody ConsumptionDto consumptionDto) {
    log.info("Request of add Consumption");
    return consumptionService.addConsumption(consumptionDto)
        .map(ResponseEntity::ok)
        .onErrorReturn(WebClientResponseException.class, ResponseEntity.badRequest().build())
        .onErrorReturn(WebClientRequestException.class,
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
  }

}
