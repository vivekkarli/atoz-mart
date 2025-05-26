package com.atozmart.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = { "com.atozmart.authserver", "com.atozmart.commons" })
public class AtozmartAuthserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtozmartAuthserverApplication.class, args);
	}

}
