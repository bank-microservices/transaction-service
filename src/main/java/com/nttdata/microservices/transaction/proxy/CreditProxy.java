package com.nttdata.microservices.transaction.proxy;

import com.nttdata.microservices.transaction.entity.credit.Credit;
import com.nttdata.microservices.transaction.entity.credit.CreditCard;
import com.nttdata.microservices.transaction.service.dto.CreditDto;
import reactor.core.publisher.Mono;

public interface CreditProxy {

  Mono<Credit> findById(String id);

  Mono<Credit> findByAccountNumber(String accountNumber);

  Mono<CreditCard> findCreditCardByAccountNumber(String accountNumber);

  Mono<CreditDto> updateAmount(String creditId, double amount);
}
