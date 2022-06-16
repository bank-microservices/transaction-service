package com.nttdata.microservices.transaction.entity.credit;

import com.nttdata.microservices.transaction.entity.client.Client;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CreditCard {

  private String id;

  private String cardNumber;

  @ReadOnlyProperty
  private Client client;

  @ReadOnlyProperty
  private LocalDate expirationDate;

}
