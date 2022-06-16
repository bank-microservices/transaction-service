package com.nttdata.microservices.transaction.repository;

import com.nttdata.microservices.transaction.entity.Consumption;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ConsumptionRepository extends ReactiveMongoRepository<Consumption, String>, ConsumptionCustomRepository {

  Flux<Consumption> findByCreditId(String id);

}
