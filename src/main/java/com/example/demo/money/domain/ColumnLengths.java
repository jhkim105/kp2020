package com.example.demo.money.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ColumnLengths {
  public final static int UUID = 50;
  public static final int MONEY_TOKEN_LENGTH = 3;
}
