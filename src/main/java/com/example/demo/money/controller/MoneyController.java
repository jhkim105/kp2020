package com.example.demo.money.controller;

import com.example.demo.money.controller.MoneyDto.GiveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
      MoneyDto.GiveRequest giveRequest) {

    String token = moneyService.create();

    return ResponseEntity.ok(GiveResponse.of(token));
  }


  @PostMapping("/take")
  public ResponseEntity<?> take(MoneyDto.TakeRequest takeRequest) {
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<?> get(@RequestParam("token") String token) {
    MoneyDto.Money money = moneyService.getMoney(token);
    return ResponseEntity.ok(money);
  }

}
