package com.example.demo.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCodes errorCodes;


  public BusinessException(ErrorCodes errorCodes) {
    this.errorCodes = errorCodes;
  }

  public BusinessException(ErrorCodes errorCodes, Throwable cause) {
    super(cause);
    this.errorCodes = errorCodes;
  }

  public BusinessException(ErrorCodes errorCodes, String debugMessage) {
    super(debugMessage);
    this.errorCodes = errorCodes;
  }

  public BusinessException(ErrorCodes errorCodes, Throwable cause, String debugMessage) {
    super(debugMessage, cause);
    this.errorCodes = errorCodes;
  }


}
