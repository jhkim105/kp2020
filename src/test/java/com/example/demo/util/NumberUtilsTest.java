package com.example.demo.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Slf4j
class NumberUtilsTest {

  @Test
  @ParameterizedTest
  @CsvSource({"1000,3", "1000,4", "1000,5", "1000,6", "1000,7", "1000,8"})
  void randomNumbers(int sum, int count) {
    int[] arr = NumberUtils.randomNumbers(sum, count);
    int total = Arrays.stream(arr).reduce(0, Integer::sum);
    log.debug("{}", ArrayUtils.toString(arr));
    assertAll(
        () -> assertThat(total).isEqualTo(sum),
        () -> assertThat(count).isEqualTo(arr.length)
    );
  }

  @Test

  void random() {
    log.debug("{}", Math.random() * 10);
  }

}