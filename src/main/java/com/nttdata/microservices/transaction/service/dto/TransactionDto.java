package com.nttdata.microservices.transaction.service.dto;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nttdata.microservices.transaction.entity.TransactionType;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

  @NotBlank(message = "accountNumber is required")
  @JsonProperty(access = WRITE_ONLY)
  private String accountNumber;

  @NotNull(message = "amount is required")
  @Positive(message = "amount must be greater than zero (0)")
  private Double amount;

  @JsonProperty(access = READ_ONLY)
  private String id;

  @JsonProperty(access = READ_ONLY)
  private String transactionCode;

  @JsonProperty(access = READ_ONLY)
  private TransactionType transactionType;

  @JsonProperty(access = READ_ONLY)
  private AccountDto account;

  @JsonProperty(access = READ_ONLY)
  private LocalDateTime registerDate;

  @JsonProperty(access = READ_ONLY)
  private Double transactionFee;

  @JsonProperty(access = READ_ONLY)
  private Double totalAmount;

}
