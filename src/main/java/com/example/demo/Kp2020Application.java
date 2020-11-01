package com.example.demo;

import com.example.demo.config.JpaConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class Kp2020Application {

  public static void main(String[] args) {
    SpringApplication.run(Kp2020Application.class, args);
  }

}

