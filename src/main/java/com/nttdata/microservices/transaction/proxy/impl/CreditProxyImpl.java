package com.nttdata.microservices.transaction.proxy.impl;

import com.nttdata.microservices.transaction.entity.credit.Credit;
import com.nttdata.microservices.transaction.entity.credit.CreditCard;
import com.nttdata.microservices.transaction.exception.CreditNotFoundException;
import com.nttdata.microservices.transaction.proxy.CreditProxy;
import com.nttdata.microservices.transaction.service.dto.CreditDto;
import com.nttdata.microservices.transaction.util.RestUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CreditProxyImpl implements CreditProxy {

  private static final String STATUS_CODE = "Status code : {}";
  private final WebClient webClient;

  public CreditProxyImpl(@Value("${service.credit.uri}") String url) {
    this.webClient = WebClient.builder()
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url).build();
  }

  @Override
  public Mono<Credit> findById(String id) {
    return getCreditMono("/{id}", id);
  }

  @Override
  public Mono<Credit> findByAccountNumber(String accountNumber) {
    return getCreditMono("/account-number/{number}", accountNumber);
  }

  @Override
  public Mono<CreditCard> findCreditCardByAccountNumber(String accountNumber) {
    String errorMessage =
        String.format("There is CreditCard not found with accountNumber: %s ", accountNumber);
    return this.webClient.get()
        .uri("/card/account/{number}", accountNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(CreditCard.class);
  }

  @Override
  public Mono<CreditDto> updateAmount(String creditId, double amount) {
    Map<String, Object> params = Map.of("creditId", creditId, "amount", amount);
    String errorMessage = String.format("There is Credit not found with creditId: %s ", creditId);
    return this.webClient.put()
        .uri("/amount/{creditId}/{amount}", params)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(CreditDto.class);
  }

  private Mono<Credit> getCreditMono(String uri, String findValue) {
    String errorMessage = String.format("There is Credit not available: %s ", findValue);
    return this.webClient.get()
        .uri(uri, findValue)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(Credit.class);
  }

  private Mono<? extends Throwable> applyError4xx(ClientResponse creditResponse,
                                                  String errorMessage) {
    log.info(STATUS_CODE, creditResponse.statusCode().value());
    if (creditResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
      return Mono.error(
          new CreditNotFoundException(errorMessage, creditResponse.statusCode().value()));
    }
    return creditResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new CreditNotFoundException(response, creditResponse.statusCode().value())));
  }

  private Mono<? extends Throwable> applyError5xx(ClientResponse clientResponse) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(new CreditNotFoundException(response)));
  }

}
