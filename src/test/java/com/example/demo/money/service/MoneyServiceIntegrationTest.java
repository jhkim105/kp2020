package com.example.demo.money.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.money.domain.MoneyGive;
import com.example.demo.money.repository.MoneyGiveRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MoneyServiceIntegrationTest {

  @Autowired
  MoneyService moneyService;

  @Autowired
  MoneyGiveRepository moneyGiveRepository;

  @Test
  @Transactional
  void create() {
    // given
    String userId = "user01";
    String roomId = "room01";
    long amount = 10000l;
    int count = 5;
    String testToken = "XXX";
    MoneyCreateDto moneyCreateDto = MoneyCreateDto.builder()
        .userId(userId)
        .roomId(roomId)
        .amount(amount)
        .count(count)
        .build();

    MoneyGive moneyGive = moneyCreateDto.toMoneyGive(testToken);

    // when
    String token = moneyService.create(moneyCreateDto);

    // then
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository .findByToken(token);

    assertAll(
        () -> assertThat(token.length()).isEqualTo(3),
        () -> assertTrue(moneyGiveOptional.isPresent()),
        () -> assertThat(moneyGiveOptional.get().getMoneyTakes().size()).isEqualTo(count)
    );
  }


}