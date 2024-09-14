package com.gerarecibos.recibos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication (scanBasePackages = "com.gerarecibos.recibos")
public class RecibosApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecibosApplication.class, args);
	}

}
