package com.example.demo.money.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.RestDocsConfiguration;
import com.example.demo.money.controller.MoneyController;
import com.example.demo.money.controller.MoneyDto;
import com.example.demo.money.controller.MoneyDto.Money;
import com.example.demo.money.controller.MoneyDto.Take;
import com.example.demo.money.domain.MoneyGive;
import com.example.demo.money.domain.MoneyTake;
import com.example.demo.money.service.MoneyCreateDto;
import com.example.demo.money.service.MoneyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(value = MoneyController.class)
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Slf4j
class MoneyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  private MoneyService moneyService;

  @Test
  void give() throws Exception {
    // give
    String token = UUID.randomUUID().toString();
    String userId = "user01";
    String roomId = "room01";
    long amount = 10000l;
    int count = 5;
    MoneyCreateDto moneyCreateDto = MoneyCreateDto.builder()
        .userId(userId)
        .roomId(roomId)
        .amount(amount)
        .count(count)
        .build();
    given(moneyService.create(moneyCreateDto)).willReturn(token);

    Map<String, String> params = new HashMap<>();
    params.put("count", "5");
    params.put("amount", "10000");
    String requestBody = objectMapper.writeValueAsString(params);

    // when
    ResultActions resultActions = mockMvc
        .perform(post("/money/give")
            .header(MoneyDto.HEADER_X_ROOM_ID, roomId)
            .header(MoneyDto.HEADER_X_USER_ID, userId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
        .andDo(print());

    // then
    resultActions.andExpect(status().isOk())
        .andDo(document("money-give",
            requestHeaders(
                headerWithName(MoneyDto.HEADER_X_ROOM_ID).description("사용자 아이디"),
                headerWithName(MoneyDto.HEADER_X_USER_ID).description("룸 아이디")
            ),
            requestFields(
                fieldWithPath("count").description("뿌릴 인원"),
                fieldWithPath("amount").description("뿌릴 금액")
            ),
            responseFields(
                fieldWithPath("token").description("")
            )
        ))
    ;

  }

  @Test
  void take() throws Exception {
    // give
    String token = UUID.randomUUID().toString();

    Map<String, String> params = new HashMap<>();
    params.put("token", token);
    String requestBody = objectMapper.writeValueAsString(params);

    String userId = "user01";
    String roomId = "room01";

    // when
    ResultActions resultActions = mockMvc
        .perform(post("/money/take")
            .header(MoneyDto.HEADER_X_ROOM_ID, roomId)
            .header(MoneyDto.HEADER_X_USER_ID, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andDo(print());

    // then
    resultActions.andExpect(status().isOk())
        .andDo(document("money-take",
            requestHeaders(
                headerWithName(MoneyDto.HEADER_X_ROOM_ID).description("사용자 아이디"),
                headerWithName(MoneyDto.HEADER_X_USER_ID).description("룸 아이디")
            ),
            requestFields(
                fieldWithPath("token").description("받기 식별자")
            )
        ))
    ;

  }

  @Test
  void get() throws Exception {
    // give
    String token = UUID.randomUUID().toString();

    String userId = "user01";
    String roomId = "room01";
    String receiveUserId01 = "user02";
    String receiveUserId02 = "user03";

    MoneyGive moneyGive = MoneyGive.builder()
        .roomId(roomId)
        .createdBy(userId)
        .amount(10000l)
        .count(2)
        .createdDate(LocalDateTime.now().minusMinutes(5))
        .token(token)
        .build();

    MoneyTake moneyTake1 = MoneyTake.builder()
        .moneyGive(moneyGive)
        .amount(5000l)
        .userId("user02")
        .build();

    MoneyTake moneyTake2 = MoneyTake.builder()
        .moneyGive(moneyGive)
        .amount(5000l)
        .userId("user03")
        .build();

    moneyGive.getMoneyTakes().add(moneyTake1);
    moneyGive.getMoneyTakes().add(moneyTake2);


    given(moneyService.getMoney(token)).willReturn(moneyGive);

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
        .andDo(document("money-get",
            requestHeaders(
                headerWithName(MoneyDto.HEADER_X_ROOM_ID).description("사용자 아이디"),/**/
                headerWithName(MoneyDto.HEADER_X_USER_ID).description("룸 아이디")
            ),
//            requestFields(
//                fieldWithPath("token").description("받기 식별자")
//            ),
            responseFields(
                fieldWithPath("scatteredTime").description("뿌린 시각"),
                fieldWithPath("amount").description("뿌린 금액"),
                fieldWithPath("amountDone").description("받기 완료된 금액"),
                fieldWithPath("takeList").description("받기 완료된 정보 리스트"),
                fieldWithPath("takeList[].userId").description("받은 사용자아이디"),
                fieldWithPath("takeList[].amount").description("받은 금액")
            )
        ))
    ;

  }
}