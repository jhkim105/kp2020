package com.example.demo.money.repository;

import com.example.demo.money.domain.MoneyGive;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyGiveRepository extends JpaRepository<MoneyGive, String>, MoneyGiveRepositoryCustom {

//  @Lock(LockModeType.OPTIMISTIC)
//  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  Optional<MoneyGive> findByTokenAndFinishedDateIsNull(String token);

  Optional<MoneyGive> findByToken(String token);
}
