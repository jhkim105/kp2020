package com.example.demo.money.service;

import com.example.demo.money.domain.MoneyGive;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class MoneyCreateDto {

  private String userId;
  private String roomId;
  private long amount;
  private int count;

  @Builder
  public MoneyCreateDto(String userId, String roomId, long amount, int count) {
    this.userId = userId;
    this.roomId = roomId;
    this.amount = amount;
    this.count = count;
  }

  public MoneyGive toMoneyGive(String token) {
    return MoneyGive.builder()
        .createdBy(this.userId)
        .roomId(this.roomId)
        .amount(this.amount)
        .count(this.count)
        .token(token)
        .build();
  }

}