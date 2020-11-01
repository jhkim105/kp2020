package com.example.demo;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@TestConfiguration
public class MockMvcTestConfig {

  @Bean
  protected MockMvc mockMvc(WebApplicationContext ctx) {
    return MockMvcBuilders.webAppContextSetup(ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))
        .alwaysDo(print())
        .build();
  }

}
