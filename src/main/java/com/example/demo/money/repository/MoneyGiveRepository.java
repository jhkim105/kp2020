package com.example.demo.money.repository;

import com.example.demo.money.domain.MoneyGive;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface MoneyGiveRepository extends JpaRepository<MoneyGive, String>, MoneyGiveRepositoryCustom {

//  @Lock(LockModeType.PESSIMISTIC_READ)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
//  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  Optional<MoneyGive> findByTokenAndFinishedDateIsNull(String token);

  Optional<MoneyGive> findByToken(String token);
}
