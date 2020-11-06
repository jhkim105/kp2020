package com.example.demo.money.service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.money.domain.MoneyTake;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyConcurrentService {

  private final MoneyService moneyService;

  private final HazelcastInstance hazelcastInstance;

  public MoneyTake take(MoneyTakeDto dto) {
    FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(dto.getToken());
    if (lock.tryLock(1, TimeUnit.SECONDS)) {
      try {
        return moneyService.take(dto);
      } finally {
        if (lock.isLocked())
          lock.unlock();
      }
    } else {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, String.format("Try later.Too many request. dto:%s", dto));
    }

  }
}
