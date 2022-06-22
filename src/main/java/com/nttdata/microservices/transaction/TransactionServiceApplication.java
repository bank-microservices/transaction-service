package com.nttdata.microservices.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableEurekaClient
public class TransactionServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(TransactionServiceApplication.class, args);
  }

}
