package com.example.demo.money.service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.money.domain.MoneyGive;
import com.example.demo.money.domain.MoneyTake;
import com.example.demo.money.repository.MoneyGiveRepository;
import com.example.demo.money.repository.MoneyTakeRepository;
import com.example.demo.util.NumberUtils;
import java.util.Arrays;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MoneyService {


  @Autowired
  private MoneyGiveRepository moneyGiveRepository;

  @Autowired
  private MoneyTakeRepository moneyTakeRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public String create(MoneyCreateDto moneyCreateDto) {
    checkScatteringPossible(moneyCreateDto.getAmount(), moneyCreateDto.getCount());
    MoneyGive moneyGive = createMoneyGive(moneyCreateDto);
    createMoneyTake(moneyGive);
    moneyGive = moneyGiveRepository.save(moneyGive);
    return moneyGive.getToken();
  }

  private MoneyGive createMoneyGive(MoneyCreateDto moneyCreateDto) {
    String token = moneyGiveRepository.buildToken();
    return moneyCreateDto.toMoneyGive(token);
  }

  private void createMoneyTake(MoneyGive moneyGive) {
    long[] scatteredMoneyArray = NumberUtils.randomNumbers(moneyGive.getAmount(), moneyGive.getCount());
    Arrays.stream(scatteredMoneyArray).forEach(amount -> {
      MoneyTake moneyTake = MoneyTake.of(moneyGive, amount);
      moneyGive.getMoneyTakes().add(moneyTake);
    });
  }


  private void checkScatteringPossible(long amount, int count) {
    if (amount <= 0 ) {
      throw new IllegalArgumentException(String.format("amount:%s must be bigger than zero.", amount));
    }

    if (count <= 0 ) {
      throw new IllegalArgumentException(String.format("count:%s must be bigger than zero.", count));
    }

    if(amount < count) {
      throw new BusinessException(ErrorCode.AMOUNT_LESS_THAN_COUNT);
    }

  }


  @Transactional
  public MoneyTake take(MoneyTakeDto moneyTakeDto) {
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository.findByTokenAndFinishedDateIsNull(moneyTakeDto.getToken());
    moneyGiveOptional.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_MONEY_GIVE));

    MoneyGive moneyGive = moneyGiveOptional.get();
    Optional<MoneyTake> optionalMoneyTake = moneyGive.getAvailableMoneyTake();
    optionalMoneyTake.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_MONEY_TAKE));
    MoneyTake moneyTake = optionalMoneyTake.get();
    entityManager.lock(moneyTake, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    moneyTake.receive(moneyTakeDto.getUserId());
    return moneyTakeRepository.save(moneyTake);
  }

  @Transactional(readOnly = true)
  public MoneyGive getMoney(String token) {
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository.findByToken(token);
    moneyGiveOptional.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_MONEY_GIVE));
    return moneyGiveOptional.get();
  }
}
