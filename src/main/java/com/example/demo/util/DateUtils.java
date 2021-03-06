package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

  public static long getEpochMilli(LocalDateTime localDateTime) {
    if (localDateTime == null)
      return 0;
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}
