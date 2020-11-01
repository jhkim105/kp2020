package com.example.demo.money.repository;

import com.example.demo.money.domain.ColumnLengths;
import com.example.demo.money.domain.MoneyGive;
import com.example.demo.money.domain.QMoneyGive;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class MoneyGiveRepositoryImpl implements MoneyGiveRepositoryCustom {

  public static final int TOKEN_MAX_RETRY_COUNT = 10;

  @Autowired
  private JPAQueryFactory jpaQueryFactory;

  @Override
  public String buildToken() {
    int retryCount = 0;
    String token = generateToken();
    while(existsByToken(token)) {
      token = generateToken();
      if (++retryCount >= TOKEN_MAX_RETRY_COUNT) {
        throw new IllegalStateException("Token generation max retry count over...");
      }
    }
    log.debug("retryCount:{}, token:{}", retryCount, token);
    return token;
  }

  private boolean existsByToken(String token) {
    QMoneyGive moneyGive = QMoneyGive.moneyGive;
    JPAQuery<MoneyGive> query = jpaQueryFactory.selectFrom(moneyGive);
    query.where(moneyGive.token.eq(token));
    return query.fetchCount() > 0;
  }

  private String generateToken() {
    return RandomStringUtils.randomAlphanumeric(ColumnLengths.MONEY_TOKEN_LENGTH);
  }
}
