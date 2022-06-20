package com.nttdata.microservices.transaction.entity.credit;

import com.nttdata.microservices.transaction.entity.client.Client;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

/**
 * Entity that includes Credit Card and Credit
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditProduct {

  @NotBlank(message = "is required")
  private String id;

  @NotBlank(message = "is required")
  private String accountNumber;
  private Client client;
  private CreditProductType creditProductType;

  @ReadOnlyProperty
  private String cci;
  @ReadOnlyProperty
  private Double creditLimit;
  @ReadOnlyProperty
  private Double amount;
  @ReadOnlyProperty
  private String cardNumber;
  @ReadOnlyProperty
  private String cvv;
  @ReadOnlyProperty
  private boolean status;
  @ReadOnlyProperty
  private LocalDate expirationDate;


}
