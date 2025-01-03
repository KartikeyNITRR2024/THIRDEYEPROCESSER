package com.thirdeye.processer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThirdeyeprocesserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThirdeyeprocesserApplication.class, args);
	}

}
