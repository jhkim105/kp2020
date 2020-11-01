package com.example.demo.money.controller;

import com.example.demo.money.domain.MoneyGive;
import com.example.demo.money.domain.MoneyTake;
import com.example.demo.util.DateUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class MoneyDto {

  public final static String HEADER_X_USER_ID = "X-USER-ID";
  public final static String HEADER_X_ROOM_ID = "X-ROOM-ID";

  @Getter
  @ToString
  public static class GiveRequest {
    private int count;
    private long amount;
  }

  @Getter
  @ToString
  public static class GiveResponse {
    private String token;

    public static GiveResponse of(String token ) {
      GiveResponse giveResponse = new GiveResponse();
      giveResponse.token = token;
      return giveResponse;
    }
  }

  @Getter
  @ToString
  public static class TakeRequest {
    private String token;
  }

  @Getter
  @ToString
  public static class Money {
    //뿌린시각,뿌린금액,받기완료된금액,받기완료된정보([받은금액,받은사용자 아이디] 리스트)
    private long scatteredTime;
    private long amount;
    private long amountDone;
    private List<Take> takeList;

    @Builder
    public Money(long scatteredTime, long amount, long amountDone,
        List<Take> takeList) {
      this.scatteredTime = scatteredTime;
      this.amount = amount;
      this.amountDone = amountDone;
      this.takeList = takeList;
    }

    public static Money of(MoneyGive moneyGive) {
      List<Take> takeList = moneyGive.getMoneyTakes().stream().map(Take::of).collect(Collectors.toList());
      return Money.builder()
          .scatteredTime(DateUtils.getEpochMilli(moneyGive.getCreatedDate()))
          .amount(moneyGive.getAmount())
          .amountDone(moneyGive.getAmountDone())
          .takeList(takeList)
          .build();
    }
  }

  @Getter
  @ToString
  public static class Take {

    private String userId;
    private long amount;

    @Builder
    public Take(String userId, long amount) {
      this.userId = userId;
      this.amount = amount;
    }

    public static Take of(MoneyTake moneyTake) {
      return new Take(moneyTake.getUserId(), moneyTake.getAmount());
    }
  }


}
