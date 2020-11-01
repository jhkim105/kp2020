package com.example.demo.money.repository;

import com.example.demo.money.domain.MoneyTake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyTakeRepository extends JpaRepository<MoneyTake, String> {

}
