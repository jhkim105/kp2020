package com.example.demo.money.controller;

import com.example.demo.money.controller.MoneyDto.GiveResponse;
import com.example.demo.money.controller.MoneyDto.Money;
import com.example.demo.money.controller.MoneyDto.TakeResponse;
import com.example.demo.money.domain.MoneyGive;
import com.example.demo.money.domain.MoneyTake;
import com.example.demo.money.service.MoneyCreateDto;
import com.example.demo.money.service.MoneyService;
import com.example.demo.money.service.MoneyTakeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/money")
@RequiredArgsConstructor
public class MoneyController {

  private final MoneyService moneyService;

  @PostMapping("/give")
  public ResponseEntity<MoneyDto.GiveResponse> give(
      @RequestHeader(MoneyDto.HEADER_X_USER_ID) String userId,
      @RequestHeader(MoneyDto.HEADER_X_ROOM_ID) String roomId,
      @RequestBody MoneyDto.GiveRequest giveRequest) {

    MoneyCreateDto moneyCreateDto = MoneyCreateDto.builder()
        .userId(userId)
        .roomId(roomId)
        .amount(giveRequest.getAmount())
        .count(giveRequest.getCount())
        .build();

    String token = moneyService.create(moneyCreateDto);
    return ResponseEntity.ok(GiveResponse.of(token));
  }


  @PostMapping("/take")
  public ResponseEntity<MoneyDto.TakeResponse> take(
      @RequestHeader(MoneyDto.HEADER_X_USER_ID) String userId,
      @RequestHeader(MoneyDto.HEADER_X_ROOM_ID) String roomId,
      @RequestBody MoneyDto.TakeRequest takeRequest) {

    MoneyTakeDto moneyTakeDto = MoneyTakeDto.builder()
        .userId(userId)
        .roomId(roomId)
        .token(takeRequest.getToken())
        .build();

    MoneyTake moneyTake = moneyService.take(moneyTakeDto);
    MoneyDto.TakeResponse takeResponse = TakeResponse.of(moneyTake);
    return ResponseEntity.ok(takeResponse);
  }

  @GetMapping
  public ResponseEntity<?> get(@RequestParam("token") String token) {
    MoneyGive moneyGive = moneyService.getMoney(token);
    Money money = Money.of(moneyGive);
    return ResponseEntity.ok(money);
  }

}
