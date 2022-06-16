package com.nttdata.microservices.transaction.entity;

import com.nttdata.microservices.transaction.entity.credit.Credit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "payment")
public class Payment extends AbstractAuditingEntity {

  @Id
  private String id;

  private String transactionCode;

  private Double amount;

  private Credit credit;

  private LocalDateTime paymentDate;

}
