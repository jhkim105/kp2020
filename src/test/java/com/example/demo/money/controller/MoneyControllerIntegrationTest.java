package com.example.demo.money.controller;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.MockMvcTestConfig;
import com.example.demo.config.JpaConfig;
import com.example.demo.money.repository.MoneyGiveRepository;
import com.example.demo.money.service.MoneyCreateDto;
import com.example.demo.money.service.MoneyService;
import com.example.demo.money.service.MoneyTakeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import({MockMvcTestConfig.class, JpaConfig.class})
public class MoneyControllerIntegrationTest {
  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MoneyService moneyService;

  @Autowired
  MoneyGiveRepository moneyGiveRepository;

  @Test
  void give() throws Exception {
    // when
    String token = UUID.randomUUID().toString();
    String userId = "user01";
    String roomId = "room01";
    long amount = 10000l;
    int count = 2;

    Map<String, Object> params = new HashMap<>();
    params.put("count", count);
    params.put("amount", amount);
    String requestBody = objectMapper.writeValueAsString(params);

    ResultActions resultActions = mockMvc
        .perform(post("/money/give")
            .header(MoneyDto.HEADER_X_ROOM_ID, roomId)
            .header(MoneyDto.HEADER_X_USER_ID, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andDo(print());

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("token").isNotEmpty()); // how to length check

    ;

  }

  @Test
  @Transactional
  void take() throws Exception {
    String userId = "user01";
    String roomId = "room01";

    // given
    MoneyCreateDto moneyCreateDto = MoneyCreateDto.builder()
        .roomId(roomId)
        .userId(userId)
        .amount(10000)
        .count(5)
        .build();

    String token = moneyService.create(moneyCreateDto);

    // when
    Map<String, String> params = new HashMap<>();
    params.put("token", token);
    String requestBody = objectMapper.writeValueAsString(params);
    ResultActions resultActions = mockMvc
        .perform(post("/money/take")
            .header(MoneyDto.HEADER_X_ROOM_ID, roomId)
            .header(MoneyDto.HEADER_X_USER_ID, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andDo(print());

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("amount", greaterThan(0)))
    ;

  }

  @Test
  void get() throws Exception {
    // given
    String roomId = "room01";
    String userId = "user01";
    long amount = 10000;
    MoneyCreateDto moneyCreateDto = MoneyCreateDto.builder()
        .roomId(roomId)
        .userId(userId)
        .amount(amount)
        .count(5)
        .build();

    String token = moneyService.create(moneyCreateDto);

    MoneyTakeDto dto = MoneyTakeDto.builder()
        .roomId(roomId)
        .userId("user02")
        .token(token)
        .build();
    moneyService.take(dto);

    // when
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders.get("/money")
            .param("token", token)
            .header(MoneyDto.HEADER_X_ROOM_ID, roomId)
            .header(MoneyDto.HEADER_X_USER_ID, userId)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
        .andDo(print());

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("amount").value(amount))
        .andExpect(jsonPath("takeList",hasSize(5)))

      ;
  }


}
