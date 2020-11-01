package com.example.demo.money.service;

import com.example.demo.money.domain.MoneyGive;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class MoneyTakeDto {

  private String userId;
  private String roomId;
  private String token;

  @Builder
  public MoneyTakeDto(String userId, String roomId, String token) {
    this.userId = userId;
    this.roomId = roomId;
    this.token = token;
  }

}