package com.nttdata.microservices.transaction.entity.account;

import com.nttdata.microservices.transaction.entity.client.Client;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;

@Data
public class Account {
  private String id;
  private String accountNumber;

  @ReadOnlyProperty
  private String cci;

  @ReadOnlyProperty
  private Double maintenanceFee;

  @ReadOnlyProperty
  private Double amount;

  private Client client;

  private AccountType accountType;
}
