package com.nttdata.microservices.transaction.proxy;

import com.nttdata.microservices.transaction.entity.credit.CreditProduct;
import com.nttdata.microservices.transaction.entity.credit.CreditProductType;
import reactor.core.publisher.Mono;

public interface CreditProductProxy {

  Mono<CreditProduct> updateCreditProductAmount(String creditProductId, double amount,
                                                CreditProductType productType);

  Mono<CreditProduct> findCreditOrCardByAccountNumber(String accountNumber);

  Mono<CreditProduct> findCreditByAccountNumber(String accountNumber);

  Mono<CreditProduct> findCreditCardByAccountNumber(String accountNumber);

  Mono<CreditProduct> updateCreditCardAmount(String creditId, double amount);
}
