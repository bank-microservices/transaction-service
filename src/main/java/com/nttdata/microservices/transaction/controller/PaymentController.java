package com.nttdata.microservices.transaction.controller;

import com.nttdata.microservices.transaction.service.PaymentService;
import com.nttdata.microservices.transaction.service.dto.PaymentDto;
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
@RequestMapping("api/v1/transaction/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<PaymentDto>> paidCredit(@Valid @RequestBody PaymentDto paymentDto) {
    return paymentService.creditPayment(paymentDto)
            .map(ResponseEntity::ok)
            .onErrorReturn(WebClientResponseException.class, ResponseEntity.badRequest().build())
            .onErrorReturn(WebClientRequestException.class, ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
  }

  @GetMapping("/credit/{credit-id}")
  public Flux<PaymentDto> findByCreditId(@PathVariable("credit-id") String creditId) {
    return paymentService.findByCreditId(creditId);
  }

  @GetMapping("/{id}")
  public Mono<PaymentDto> findById(@PathVariable("id") String id) {
    return paymentService.findById(id);
  }

}
