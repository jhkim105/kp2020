package com.example.demo.money.repository;

import com.example.demo.money.domain.MoneyGive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyGiveRepositoryCustom {
  String buildToken();
}
