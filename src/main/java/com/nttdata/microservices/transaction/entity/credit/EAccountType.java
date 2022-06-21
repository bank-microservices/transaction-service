package com.nttdata.microservices.transaction.entity.credit;

public enum EAccountType {

  SAVING("01"), CURRENT("02"), FIXED_TERM("03");

  private final String value;

  EAccountType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  public boolean equalValue(final String value) {
    return this.value.equalsIgnoreCase(value);
  }

}