package com.nttdata.microservices.transaction.service.dto;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionTransferRequestDto {

  @NotBlank(message = "sourceAccountNumber is required")
  @JsonProperty(access = WRITE_ONLY)
  private String sourceAccountNumber;

  @NotBlank(message = "sourceAccountNumber is required")
  @JsonProperty(access = WRITE_ONLY)
  private String targetAccountNumber;

  @NotNull(message = "amount is required")
  @Positive(message = "amount must be greater than zero (0)")
  private Double amount;

}
