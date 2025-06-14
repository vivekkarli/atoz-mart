package com.atozmart.gatewayserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "atozmart.admin")
public class AtozmartAdminDetails {

	private String username;

	private String password;

}
