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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoneyService {

  private final MoneyGiveRepository moneyGiveRepository;

  private final MoneyTakeRepository moneyTakeRepository;

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
    MoneyGive moneyGive = moneyGiveOptional.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_MONEY_GIVE));

    Optional<MoneyTake> optionalMoneyTake = moneyGive.getAvailableMoneyTake();
    MoneyTake moneyTake  = optionalMoneyTake.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_MONEY_TAKE));
    moneyTake.receive(moneyTakeDto.getUserId());
    return moneyTakeRepository.save(moneyTake);
  }

  @Transactional(readOnly = true)
  public MoneyGive getMoney(String token) {
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository.findByToken(token);
    return moneyGiveOptional.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_MONEY_GIVE));
  }
}
