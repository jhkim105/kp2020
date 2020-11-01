package com.example.demo.money.repository;

import com.example.demo.money.domain.MoneyGive;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyGiveRepository extends JpaRepository<MoneyGive, String>, MoneyGiveRepositoryCustom {

  Optional<MoneyGive> findByTokenAndFinishedDateIsNotNull(String token);

  Optional<MoneyGive> findByToken(String token);
}
