package com.nttdata.microservices.transaction.proxy.impl;

import com.nttdata.microservices.transaction.exception.ClientNotFoundException;
import com.nttdata.microservices.transaction.proxy.ClientProxy;
import com.nttdata.microservices.transaction.service.dto.ClientDto;
import com.nttdata.microservices.transaction.util.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ClientProxyImpl implements ClientProxy {

  private static final String STATUS_CODE = "Status code : {}";
  private final WebClient webClient;

  public ClientProxyImpl(@Value("${service.client.uri}") String url,
                         WebClient.Builder loadBalancedWebClientBuilder) {
    this.webClient = loadBalancedWebClientBuilder
        .clientConnector(RestUtils.getDefaultClientConnector())
        .baseUrl(url)
        .build();
  }

  @Override
  public Mono<ClientDto> getClientByDocumentNumber(final String documentNumber) {
    return this.webClient.get()
        .uri("/documentNumber/{number}", documentNumber)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, (clientResponse -> {
          log.info(STATUS_CODE, clientResponse.statusCode().value());
          return clientResponse.bodyToMono(String.class)
              .flatMap(response -> Mono.error(
                  new ClientNotFoundException(response, clientResponse.statusCode().value())));
        }))
        .onStatus(HttpStatus::is5xxServerError, (clientResponse -> {
          log.info(STATUS_CODE, clientResponse.statusCode().value());
          return clientResponse.bodyToMono(String.class)
              .flatMap(response -> Mono.error(new ClientNotFoundException(response)));
        }))
        .bodyToMono(ClientDto.class)
        .log();
  }

  @Override
  public Mono<ClientDto> getClientById(final String id) {
    return this.webClient.get()
        .uri("/{id}", id)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, (clientResponse -> {
          log.info(STATUS_CODE, clientResponse.statusCode().value());
          return clientResponse.bodyToMono(String.class)
              .flatMap(response -> Mono.error(
                  new ClientNotFoundException(response, clientResponse.statusCode().value())));
        }))
        .onStatus(HttpStatus::is5xxServerError, (clientResponse -> {
          log.info(STATUS_CODE, clientResponse.statusCode().value());
          return clientResponse.bodyToMono(String.class)
              .flatMap(response -> Mono.error(new ClientNotFoundException(response)));
        }))
        .bodyToMono(ClientDto.class);
  }
}
