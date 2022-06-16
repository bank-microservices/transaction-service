package com.nttdata.microservices.transaction.entity;

public enum TransactionType {
  DEPOSIT, WITHDRAWAL;

  public boolean isDeposit() {
    return this.equals(DEPOSIT);
  }
}
