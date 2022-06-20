package com.nttdata.microservices.transaction.service.dto;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nttdata.microservices.transaction.entity.client.Client;
import com.nttdata.microservices.transaction.entity.credit.CreditProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditProductDto {

  private String id;
  private String accountNumber;
  private Client client;
  private CreditProductType creditProductType;

  @JsonProperty(access = READ_ONLY)
  private String cci;
  @JsonProperty(access = READ_ONLY)
  private Double creditLimit;
  @JsonProperty(access = READ_ONLY)
  private Double amount;
  @JsonProperty(access = READ_ONLY)
  private String cardNumber;
  @JsonProperty(access = READ_ONLY)
  private String cvv;
  @JsonProperty(access = READ_ONLY)
  private boolean status;

}
