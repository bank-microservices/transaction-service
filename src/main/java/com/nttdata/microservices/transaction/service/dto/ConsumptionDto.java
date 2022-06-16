package com.nttdata.microservices.transaction.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nttdata.microservices.transaction.entity.credit.CreditCard;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@NoArgsConstructor
public class ConsumptionDto {

  @NotBlank(message = "accountNumber is required")
  @JsonProperty(access = WRITE_ONLY)
  private String accountNumber;

  @NotNull(message = "amount is required")
  private Double amount;

  @JsonProperty(access = READ_ONLY)
  private String id;

  @JsonProperty(access = READ_ONLY)
  private String transactionCode;

  @JsonProperty(access = READ_ONLY)
  private CreditDto credit;

  @JsonProperty(access = READ_ONLY)
  private CreditCard creditCard;

  @JsonProperty(access = READ_ONLY)
  private LocalDateTime registerDate;


}
