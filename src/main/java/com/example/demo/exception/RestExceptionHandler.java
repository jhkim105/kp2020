package com.example.demo.exception;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestControllerAdvice
@EnableWebMvc
public class RestExceptionHandler {

  @Value("${server.error.include-stacktrace}")
  private String stacktrace;

  @Autowired private ErrorAttributes errorAttributes;

  @ExceptionHandler({ BusinessException.class })
  protected ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
    return handleException(ex.getErrorCode(), ex.getMessage(), request, ex);
  }

  @ExceptionHandler({ Exception.class })
  protected ResponseEntity<Object> handleException(Exception ex, WebRequest request) {
    return handleException(ErrorCode.SYSTEM_ERROR, "", request, ex);
  }

  protected ResponseEntity<Object> handleException(ErrorCode errorCode, String message, WebRequest request, Throwable cause) {
    HttpStatus status = errorCode.getStatus();
    request.setAttribute("javax.servlet.error.status_code", status.value(), WebRequest.SCOPE_REQUEST);
    Map<String, Object> errorAttributeMap = errorAttributes.getErrorAttributes(request, false);
    errorAttributeMap.put("code", errorCode.getCode());
    errorAttributeMap.put("message", errorCode.getMessage());
    if (StringUtils.equals("always", stacktrace))
      errorAttributes.getError(request).printStackTrace();
    return new ResponseEntity<>(errorAttributeMap, new HttpHeaders(), status);
  }
}
