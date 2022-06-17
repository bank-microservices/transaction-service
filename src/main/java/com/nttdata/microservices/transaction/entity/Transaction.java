package com.nttdata.microservices.transaction.entity;

import com.nttdata.microservices.transaction.entity.account.Account;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transactions")
public class Transaction extends AbstractAuditingEntity {

  @Id
  private String id;
  private String transactionCode;
  private TransactionType transactionType;
  private Double amount;
  private Account account;
  private LocalDateTime registerDate;

}
