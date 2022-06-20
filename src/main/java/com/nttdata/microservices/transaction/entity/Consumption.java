package com.nttdata.microservices.transaction.entity;

import com.nttdata.microservices.transaction.entity.credit.CreditProduct;
import java.time.LocalDateTime;
import javax.validation.Valid;
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

  @Valid
  private CreditProduct creditCard;

  private LocalDateTime registerDate;

}
