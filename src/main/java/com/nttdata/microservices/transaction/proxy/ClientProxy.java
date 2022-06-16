package com.nttdata.microservices.transaction.proxy;

import com.nttdata.microservices.transaction.service.dto.ClientDto;
import reactor.core.publisher.Mono;

public interface ClientProxy {
  Mono<ClientDto> getClientByDocumentNumber(String documentNumber);

  Mono<ClientDto> getClientById(String id);
}
