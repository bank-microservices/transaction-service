package com.nttdata.microservices.transaction.proxy.impl;

import static com.nttdata.microservices.transaction.util.MessageUtils.getMsg;

import com.nttdata.microservices.transaction.entity.credit.CreditProduct;
import com.nttdata.microservices.transaction.entity.credit.CreditProductType;
import com.nttdata.microservices.transaction.exception.CreditNotFoundException;
import com.nttdata.microservices.transaction.proxy.CreditProductProxy;
import com.nttdata.microservices.transaction.util.RestUtils;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
public class CreditProductProxyImpl implements CreditProductProxy {

  private static final String STATUS_CODE = "Status code : {}";
  private final WebClient webClient;

  public CreditProductProxyImpl(@Value("${service.credit.uri}") String url) {
    this.webClient = WebClient.builder()
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url).build();
  }

  @Override
  public Mono<CreditProduct> updateCreditProductAmount(String creditProductId, double amount,
                                                       CreditProductType productType) {
    if (productType == CreditProductType.CREDIT) {
      return updateCreditAmount(creditProductId, amount);
    } else {
      return updateCreditCardAmount(creditProductId, amount);
    }

  }

  @Override
  public Mono<CreditProduct> findCreditOrCardByAccountNumber(String accountNumber) {
    return findCreditByAccountNumber(accountNumber)
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)))
        .onErrorResume(Exceptions::isRetryExhausted,
            t -> findCreditCardByAccountNumber(accountNumber))
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)));
  }

  @Override
  public Mono<CreditProduct> findCreditByAccountNumber(String accountNumber) {
    return this.webClient.get()
        .uri("/account-number/{number}", accountNumber)
        .retrieve()
        .bodyToMono(CreditProduct.class)
        .map(setCreditProductType(CreditProductType.CREDIT));
  }

  @Override
  public Mono<CreditProduct> findCreditCardByAccountNumber(String accountNumber) {
    final String errorMessage = getMsg("credit.card.not.found", accountNumber);
    return this.webClient.get()
        .uri("/card/account/{number}", accountNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(CreditProduct.class)
        .map(setCreditProductType(CreditProductType.CREDIT_CARD));
  }

  private Mono<CreditProduct> updateCreditAmount(String creditId, double amount) {
    final Map<String, Object> params = Map.of("creditId", creditId, "amount", amount);
    final String errorMessage = getMsg("credit.not.found", creditId);
    return this.webClient.put()
        .uri("/amount/{creditId}/{amount}", params)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(CreditProduct.class)
        .map(setCreditProductType(CreditProductType.CREDIT));
  }


  @Override
  public Mono<CreditProduct> updateCreditCardAmount(String creditId, double amount) {
    final Map<String, Object> params = Map.of("creditCardId", creditId, "amount", amount);
    final String errorMessage = getMsg("credit.card.not.found", creditId);
    return this.webClient.put()
        .uri("/card/amount/{creditCardId}/{amount}", params)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,
            clientResponse -> this.applyError4xx(clientResponse, errorMessage))
        .onStatus(HttpStatus::is5xxServerError, this::applyError5xx)
        .bodyToMono(CreditProduct.class)
        .map(setCreditProductType(CreditProductType.CREDIT_CARD));
  }

  private Function<CreditProduct, CreditProduct> setCreditProductType(CreditProductType credit) {
    return (creditProduct) -> {
      creditProduct.setCreditProductType(credit);
      return creditProduct;
    };
  }

  private Mono<? extends Throwable> applyError4xxEmpty(ClientResponse clientResponse,
                                                       String errorMessage) {
    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
      return Mono.empty();
    }
    return applyError4xx(clientResponse, errorMessage);
  }

  private Mono<? extends Throwable> applyError4xx(ClientResponse clientResponse,
                                                  String errorMessage) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
      return Mono.error(new CreditNotFoundException(errorMessage,
          clientResponse.statusCode().value()));
    }
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(new CreditNotFoundException(response,
            clientResponse.statusCode().value())));
  }

  private Mono<? extends Throwable> applyError5xx(ClientResponse clientResponse) {
    log.info(STATUS_CODE, clientResponse.statusCode().value());
    return clientResponse.bodyToMono(String.class)
        .flatMap(response -> Mono.error(
            new CreditNotFoundException(response, clientResponse.statusCode().value())));
  }

}
