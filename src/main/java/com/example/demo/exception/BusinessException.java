package com.example.demo.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;


  public BusinessException(ErrorCode errorCode) {
    super(errorMessage(errorCode));
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, Throwable cause) {
    super(errorMessage(errorCode), cause);
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, String debugMessage) {
    super(errorMessage(errorCode, debugMessage));
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, Throwable cause, String debugMessage) {
    super(errorMessage(errorCode, debugMessage), cause);
    this.errorCode = errorCode;
  }

  private static String errorMessage(ErrorCode errorCode, String debugMessage) {
    int status = errorCode.getCode();
    int code = errorCode.getCode();
    String message = StringUtils.defaultString(debugMessage, errorCode.getMessage());
    return String.format("status:%s, code:%s, message:%s", status, code, message);
  }

  private static String errorMessage(ErrorCode errorCode) {
    return errorMessage(errorCode, null);
  }

}
