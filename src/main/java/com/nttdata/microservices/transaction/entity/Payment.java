package com.nttdata.microservices.transaction.entity;

import com.nttdata.microservices.transaction.entity.credit.CreditProduct;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "payment")
public class Payment extends AbstractAuditingEntity {

  @Id
  private String id;

  private String transactionCode;

  private Double amount;

  @Valid
  private CreditProduct creditProduct;

  private LocalDateTime registerDate;

}
