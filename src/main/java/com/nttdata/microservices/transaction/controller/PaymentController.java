package com.nttdata.microservices.transaction.controller;

import com.nttdata.microservices.transaction.service.PaymentService;
import com.nttdata.microservices.transaction.service.dto.PaymentDto;
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
@RequestMapping("api/v1/transaction/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping
  public Flux<PaymentDto> getAll() {
    log.info("List of Payments");
    return paymentService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<PaymentDto> findById(@PathVariable("id") String id) {
    log.info("find by Id of Payments");
    return paymentService.findById(id);
  }

  @GetMapping("/credit/{credit-id}")
  public Flux<PaymentDto> findByCreditAccountId(@PathVariable("credit-id") String creditId) {
    log.info("find by creditId of Payments");
    return paymentService.findByCreditAccountId(creditId);
  }

  @GetMapping("/credit-number/{account-number}")
  public Flux<PaymentDto> findByCreditAccountNumber(
      @PathVariable("account-number") String accountNumber) {
    log.info("find by accountNumber of Payments");
    return paymentService.findByCreditAccountNumber(accountNumber);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<PaymentDto>> paidCredit(@Valid @RequestBody PaymentDto paymentDto) {
    log.info("Request of paid Credit");
    return paymentService.creditPayment(paymentDto)
        .map(ResponseEntity::ok)
        .onErrorReturn(WebClientResponseException.class, ResponseEntity.badRequest().build())
        .onErrorReturn(WebClientRequestException.class,
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
  }

}
