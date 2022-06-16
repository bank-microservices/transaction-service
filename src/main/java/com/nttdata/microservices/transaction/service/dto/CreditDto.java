package com.nttdata.microservices.transaction.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditDto {

  private String id;

  private String accountNumber;

  private ClientDto client;

  private double amount;

  private double creditLimit;

}
