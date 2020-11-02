package com.example.demo.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

  public static int[] randomNumbers(int sum, int count) {
    int[] ret = new int[count];
    for (int i = 0; i < sum; i++) {
      ret[(int) (Math.random() * count)]++;
    }
    return ret;
  }
}
