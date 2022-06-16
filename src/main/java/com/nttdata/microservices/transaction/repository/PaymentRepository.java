package com.nttdata.microservices.transaction.repository;

import com.nttdata.microservices.transaction.entity.Payment;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, String>, PaymentCustomRepository {

  @Aggregation(pipeline = {"{'$match':{'credit._id':?0}}"})
  Flux<Payment> findByCreditId(String id);

  @Aggregation(pipeline = {"{'$match':{'credit.accountNumber':?0}}"})
  Flux<Payment> findByCreditAccountNumber(String accountNumber);
}
