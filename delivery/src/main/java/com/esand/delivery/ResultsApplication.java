package com.esand.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ResultsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResultsApplication.class, args);
	}

}
