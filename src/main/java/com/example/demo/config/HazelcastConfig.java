package com.example.demo.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HazelcastConfig {

  @Bean
  public HazelcastInstance hazelcastInstance() {
    return Hazelcast.newHazelcastInstance();
  }


}
