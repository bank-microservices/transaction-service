package com.nttdata.microservices.transaction.controller;

import com.nttdata.microservices.transaction.service.TransactionService;
import com.nttdata.microservices.transaction.service.dto.TransactionDto;
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

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping
  public Flux<TransactionDto> getAll() {
    log.info("List of Transactions");
    return transactionService.findAll();
  }

  @PostMapping("/deposit")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<TransactionDto>> deposit(@Valid @RequestBody TransactionDto transactionDto) {
    return transactionService.deposit(transactionDto)
            .map(ResponseEntity::ok)
            .onErrorReturn(WebClientResponseException.class, ResponseEntity.badRequest().build())
            .onErrorReturn(WebClientRequestException.class, ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
  }

  @PostMapping("/withdraw")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<TransactionDto>> withdraw(@Valid @RequestBody TransactionDto transactionDto) {
    return transactionService.withdraw(transactionDto)
            .map(ResponseEntity::ok)
            .onErrorReturn(WebClientResponseException.class, ResponseEntity.badRequest().build())
            .onErrorReturn(WebClientRequestException.class, ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
  }

  @GetMapping("/account/{account-id}")
  public Flux<TransactionDto> findByAccountId(@PathVariable("account-id") String accountId) {
    return transactionService.findByAccountId(accountId);
  }

  @GetMapping("/account-number/{account-number}")
  public Flux<TransactionDto> findByAccountNumber(@PathVariable("account-number") String accountNumber) {
    return transactionService.findByAccountNumber(accountNumber);
  }

}
