package com.nttdata.microservices.transaction.repository;

import com.nttdata.microservices.transaction.entity.Payment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, String>, PaymentCustomRepository {

  Flux<Payment> findByCreditId(String id);

}
