package com.example.demo.money.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.config.JpaConfig;
import com.example.demo.money.domain.MoneyGive;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@DataJpaTest
@Import(JpaConfig.class)
class MoneyGiveRepositoryTest {

  @Autowired
  MoneyGiveRepository moneyGiveRepository;

  @Test
  @Sql(scripts = "/MoneyGiveRepositoryTest.sql", config = @SqlConfig(encoding = "UTF8"))
  void findByTokenAndFinishedDateIsNotNull() {
    Optional<MoneyGive> moneyGiveOptional1 = moneyGiveRepository.findByTokenAndFinishedDateIsNull("T01");
    Optional<MoneyGive> moneyGiveOptional2 = moneyGiveRepository.findByTokenAndFinishedDateIsNull("T02");
    assertAll(
        () -> assertTrue(moneyGiveOptional1.isPresent()),
        () -> assertFalse(moneyGiveOptional2.isPresent())
    );

  }

  @Test
  @Sql(scripts = "/MoneyGiveRepositoryTest.sql", config = @SqlConfig(encoding = "UTF8"))
  void findByToken() {
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository.findByToken("T01");
    assertTrue(moneyGiveOptional.isPresent());
  }
}