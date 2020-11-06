package com.example.demo.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

  public static long[] randomNumbers(long sum, int count) {
    long[] ret = new long[count];
    for (long i = 0; i < sum; i++) {
      ret[(int) (Math.random() * count)]++;
    }
    return ret;
  }
}
