package com.nttdata.microservices.transaction.proxy.impl;


import static com.nttdata.microservices.transaction.util.MessageUtils.getMsg;

import com.nttdata.microservices.transaction.exception.AccountNotFoundException;
import com.nttdata.microservices.transaction.proxy.AccountProxy;
import com.nttdata.microservices.transaction.service.dto.AccountDto;
import com.nttdata.microservices.transaction.util.RestUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AccountProxyImpl implements AccountProxy {

  private static final String STATUS_CODE = "Status code : {}";

  private final WebClient webClient;

  public AccountProxyImpl(@Value("${service.account.uri}") String url,
                          WebClient.Builder loadBalancedWebClientBuilder) {
    this.webClient = loadBalancedWebClientBuilder
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url)
        .build();
  }

  @Override
  public Flux<AccountDto> findByAccountNumberAndClientDocument(String accountNumber,
                                                               String documentNumber) {

    Map<String, String> params =
        Map.of("accountNumber", accountNumber, "documentNumber", documentNumber);
    String errorMessage = getMsg("account.not.available.for.client", params.values().toArray());
    return this.webClient.get()
        .uri("/number/{accountNumber}/client/{documentNumber}", params)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToFlux(AccountDto.class);
  }

  @Override
  public Mono<AccountDto> findByAccountNumber(String accountNumber) {
    String errorMessage = getMsg("account.number.not.available", accountNumber);
    return this.webClient.get()
        .uri("/account-number/{accountNumber}", accountNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(AccountDto.class);
  }

  @Override
  public Mono<AccountDto> updateAccountAmount(String accountId, double amount) {
    Map<String, Object> params = Map.of("account-id", accountId, "amount", amount);
    String errorMessage = getMsg("account.id.not.available", accountId);
    return this.webClient.put()
        .uri("/amount/{account-id}/{amount}", params)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(AccountDto.class);
  }

  private Mono<? extends Throwable> applyError4xx(ClientResponse creditResponse,
                                                  String errorMessage) {
    log.info(STATUS_CODE, creditResponse.statusCode().value());
    if (creditResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
      return Mono.error(
          new AccountNotFoundException(errorMessage, creditResponse.statusCode().value()));
    }
    return creditResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new AccountNotFoundException(response, creditResponse.statusCode().value())));
  }

  private Mono<? extends Throwable> applyError5xx(ClientResponse clientResponse) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(new AccountNotFoundException(response)));
  }
}
