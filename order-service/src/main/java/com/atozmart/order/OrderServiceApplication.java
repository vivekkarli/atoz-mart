package com.atozmart.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.atozmart.order.configuration.AtozmartAdminDetails;

@EnableAsync
@EnableConfigurationProperties(AtozmartAdminDetails.class)
@EnableTransactionManagement
@EnableFeignClients
@SpringBootApplication(scanBasePackages = { "com.atozmart.order", "com.atozmart.commons" })
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
