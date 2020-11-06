package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 999, "시스템 오류가 발생했습니다."),
  NOT_EXISTS_MONEY_GIVE(HttpStatus.BAD_REQUEST, 101, "요청한 토큰으로 데이터가 없습니다."),
  AMOUNT_LESS_THAN_COUNT(HttpStatus.BAD_REQUEST, 102, "금액이 인원수보다 작습니다."),
  REMAINDER_MUST_ZERO(HttpStatus.BAD_REQUEST, 103, "금액이 나누어 떨어지지 않습니다."),
  NOT_EXISTS_MONEY_TAKE(HttpStatus.BAD_REQUEST, 105, "받기가 끝났습니다.")
  ;

  private final HttpStatus status;
  private final int code;
  private final String message;

  ErrorCode(HttpStatus status, int code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

}
