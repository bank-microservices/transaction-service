package com.nttdata.microservices.transaction.entity;

import com.nttdata.microservices.transaction.entity.credit.Credit;
import com.nttdata.microservices.transaction.entity.credit.CreditCard;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "consumption")
@AllArgsConstructor
@NoArgsConstructor
public class Consumption extends AbstractAuditingEntity {

  @Id
  private String id;

  private String transactionCode;

  private Double amount;

  private CreditCard creditCard;

  private Credit credit;

  private LocalDateTime registerDate;

}
