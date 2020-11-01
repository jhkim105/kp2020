package com.example.demo.money.service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCodes;
import com.example.demo.money.controller.MoneyDto.Money;
import com.example.demo.money.domain.MoneyGive;
import com.example.demo.money.domain.MoneyTake;
import com.example.demo.money.repository.MoneyGiveRepository;
import com.example.demo.money.repository.MoneyTakeRepository;
import java.util.Optional;
import java.util.stream.IntStream;
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
    MoneyGive moneyGive = moneyCreateDto.toMoneyGive(token);
    return moneyGive;
  }

  private void createMoneyTake(MoneyGive moneyGive) {
    IntStream.range(0, moneyGive.getCount()).forEach( i -> {
        MoneyTake moneyTake = MoneyTake.of(moneyGive);
        moneyGive.getMoneyTakes().add(moneyTake);
    });
  }


  private void checkScatteringPossible(long amount, int count) {
    if(amount < count) {
      throw new BusinessException(ErrorCodes.AMOUNT_LESS_THAN_COUNT);
    }

    if ( amount % count !=0 ) {
      throw new BusinessException(ErrorCodes.REMAINDER_MUST_ZERO);
    }

  }


  @Transactional
  public void take(MoneyTakeDto moneyTakeDto) {
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository.findByTokenAndFinishedDateIsNotNull(moneyTakeDto.getToken());
    moneyGiveOptional.orElseThrow(() -> new BusinessException(ErrorCodes.NOT_EXISTS_MONEY_GIVE));
    Optional<MoneyTake> optionalMoneyTake = moneyGiveOptional.get().getAvailableMoneyTake();
    optionalMoneyTake.orElseThrow(() -> new BusinessException(ErrorCodes.NOT_EXISTS_MONEY_TAKE));
    MoneyTake moneyTake = optionalMoneyTake.get();
    moneyTake.receive(moneyTakeDto.getUserId());
    moneyTakeRepository.save(moneyTake);
  }

  public MoneyGive getMoney(String token) {
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository.findByToken(token);
    moneyGiveOptional.orElseThrow(() -> new BusinessException(ErrorCodes.NOT_EXISTS_MONEY_GIVE));
    return moneyGiveOptional.get();
  }
}
