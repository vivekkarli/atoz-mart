package com.atozmart.order.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "atozmart.admin")
public class AtozmartAdminDetails {

	private String username;

	private String password;

}
