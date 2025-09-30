package com.atozmart.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching
@EnableAsync
@EnableTransactionManagement
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = { "com.atozmart.authserver", "com.atozmart.commons" })
public class AtozmartAuthserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtozmartAuthserverApplication.class, args);
	}

}
