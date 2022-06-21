package com.nttdata.microservices.transaction.service.dto;

import com.nttdata.microservices.transaction.entity.account.AccountType;
import lombok.Data;

@Data
public class AccountDto {
  private String id;
  private String accountNumber;
  private String cci;
  private Double maintenanceFee;
  private Double transactionFee;
  private Integer maxLimitMonthlyMovements;
  private Double amount;
  private ClientDto client;
  private AccountType accountType;
}
