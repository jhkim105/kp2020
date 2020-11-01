package com.example.demo.money.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.money.domain.MoneyGive;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@DataJpaTest
class MoneyGiveRepositoryTest {

  @Autowired
  MoneyGiveRepository moneyGiveRepository;

  @Test
  @Sql(scripts = "/MoneyGiveRepositoryTest.sql", config = @SqlConfig(encoding = "UTF8"))
  void findByTokenAndFinishedDateIsNotNull() {
    Optional<MoneyGive> moneyGiveOptional1 = moneyGiveRepository.findByTokenAndFinishedDateIsNotNull("T01");
    Optional<MoneyGive> moneyGiveOptional2 = moneyGiveRepository.findByTokenAndFinishedDateIsNotNull("T02");
    assertAll(
        () -> assertFalse(moneyGiveOptional1.isPresent()),
        () -> assertTrue(moneyGiveOptional2.isPresent())
    );

  }

  @Test
  @Sql(scripts = "/MoneyGiveRepositoryTest.sql", config = @SqlConfig(encoding = "UTF8"))
  void findByToken() {
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository.findByToken("T01");
    assertTrue(moneyGiveOptional.isPresent());
  }
}