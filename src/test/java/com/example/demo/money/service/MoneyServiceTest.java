package com.example.demo.money.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.example.demo.money.domain.MoneyGive;
import com.example.demo.money.domain.MoneyTake;
import com.example.demo.money.repository.MoneyGiveRepository;
import com.example.demo.money.repository.MoneyTakeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MoneyServiceTest {

  @TestConfiguration
  static class TestContextConfiguration {
    @Bean
    public MoneyService moneyService() {
      return new MoneyService();
    }

  }

  @Autowired
  MoneyService moneyService;

  @MockBean
  MoneyGiveRepository moneyGiveRepository;

  @MockBean
  MoneyTakeRepository moneyTakeRepository;

  @Test
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
    given(moneyGiveRepository.buildToken()).willReturn(testToken);
    given(moneyGiveRepository.save(moneyGive)).willReturn(moneyGive);

    // when
    String token = moneyService.create(moneyCreateDto);

    // then
    assertThat(token.length()).isEqualTo(3);
  }

  @Test
  void take() {
    // given
    String userId = "user01";
    String roomId = "room01";
    long amount = 10000l;
    int count = 5;
    String testToken = "XXX";
    String takeUserId = "takerUser01";

    MoneyTakeDto moneyTakeDto = MoneyTakeDto.builder()
        .userId(takeUserId)
        .roomId(roomId)
        .token(testToken)
        .build();

    MoneyGive moneyGive = MoneyGive.builder()
        .roomId(roomId)
        .createdBy(userId)
        .amount(amount)
        .count(count)
        .token(testToken)
        .build();

    MoneyTake moneyTake = MoneyTake.of(moneyGive, 1000l);
    moneyGive.getMoneyTakes().add(moneyTake);

    given(moneyGiveRepository.findByTokenAndFinishedDateIsNull(testToken)).willReturn(Optional.of(moneyGive));
    given(moneyTakeRepository.save(moneyTake)).willReturn(moneyTake);

    // when
    moneyService.take(moneyTakeDto);
  }

}