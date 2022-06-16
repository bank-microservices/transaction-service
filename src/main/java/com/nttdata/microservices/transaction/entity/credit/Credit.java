package com.nttdata.microservices.transaction.entity.credit;

import com.nttdata.microservices.transaction.entity.client.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credit implements Serializable {

  private String id;

  private String accountNumber;

  private Client client;

  @ReadOnlyProperty
  private Double amount;

  @ReadOnlyProperty
  private Double creditLimit;

}
