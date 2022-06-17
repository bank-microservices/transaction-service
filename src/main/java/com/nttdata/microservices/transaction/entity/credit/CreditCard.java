package com.nttdata.microservices.transaction.entity.credit;

import com.nttdata.microservices.transaction.entity.client.Client;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

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
