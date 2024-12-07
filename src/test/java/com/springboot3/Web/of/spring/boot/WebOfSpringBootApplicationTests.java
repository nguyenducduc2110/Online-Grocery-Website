package com.springboot3.Web.of.spring.boot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

@SpringBootTest
class WebOfSpringBootApplicationTests {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) throws JsonProcessingException, ParseException {
		int age = 18;

	}
}
