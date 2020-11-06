package com.example.demo.money.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.demo.exception.BusinessException;
import com.example.demo.money.domain.MoneyTake;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ConcurrencyTest {

  @Autowired
  MoneyService moneyService;

  @Autowired
  MoneyConcurrentService moneyConcurrentService;

  String token;
  String roomId;
  long amount;
  int count;

  @BeforeEach
  public void beforeEach() {
    roomId = "room01";
    amount = 10000l;
    count = 2;
    token = moneyService.create(MoneyCreateDto.builder().roomId(roomId).userId("user01").amount(amount).count(count).build());
  }

  @Test
  public void take_using_parallelStream() {
    int execCount = 10;
    List<MoneyTake> moneyTakeList = new ArrayList<>();
    IntStream.range(0, execCount).parallel().forEach(i -> {
      take(moneyTakeList, token, roomId, "user_" + i);
    });
    log.debug("End");
    assertAll(
        () -> assertThat(moneyTakeList.size()).isEqualTo(count),
        () -> assertThat(moneyTakeList.stream().map(MoneyTake::getAmount).reduce(0l, Long::sum)).isEqualTo(amount)
    );
  }

  private void take(List<MoneyTake> moneyTakeList, String token, String roomId, String userId) {
    try {
      MoneyTakeDto dto = MoneyTakeDto.builder().roomId(roomId).userId(userId).token(token).build();
      MoneyTake moneyTake = moneyConcurrentService.take(dto);
      moneyTakeList.add(moneyTake);
      log.debug("{}:{}", userId, moneyTake);
    } catch(BusinessException ex) {
      log.debug(ex.getMessage());
    }
  }


  @Test
  public void take_using_executorService() throws InterruptedException {
    int execCount = 10;
    ExecutorService service = Executors.newFixedThreadPool(execCount);
    CountDownLatch latch = new CountDownLatch(execCount);
    List<MoneyTake> moneyTakeList = new ArrayList<>();
    for (int i = 0; i < execCount; i++) {
      service.submit(() -> {
        take(moneyTakeList, token, roomId, UUID.randomUUID().toString());
        latch.countDown();
      });
    }
    latch.await();
    assertAll(
        () -> assertThat(moneyTakeList.size()).isEqualTo(this.count),
        () -> assertThat(moneyTakeList.stream().map(MoneyTake::getAmount).reduce(0l, Long::sum)).isEqualTo(amount)
    );

  }


  @Test
  public void take_using_forkJoinPool() throws InterruptedException {
    int execCount = 10;
    ForkJoinPool forkJoinPool = new ForkJoinPool(execCount);
    CountDownLatch latch = new CountDownLatch(execCount);
    List<MoneyTake> moneyTakeList = new ArrayList<>();
    for (int i = 0; i < execCount; i++) {
      forkJoinPool.submit(() -> {
        take(moneyTakeList, token, roomId, UUID.randomUUID().toString());
        latch.countDown();
      });
    }
    latch.await();
    assertAll(
        () -> assertThat(moneyTakeList.size()).isEqualTo(this.count),
        () -> assertThat(moneyTakeList.stream().map(MoneyTake::getAmount).reduce(0l, Long::sum)).isEqualTo(amount)
    );
  }

}
