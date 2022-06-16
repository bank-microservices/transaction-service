package com.nttdata.microservices.transaction.service.dto;

import lombok.Data;

@Data
public class BalanceDto {

  private String creditId;
  private String type;
  private Double creditLimit;
  private Double used;
  private Double available;

}
