package com.atozmart.profile.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "profile-service")
public record ProfileConfig(AwsDetails aws) {
	public record AwsDetails(String accessKey, String secretKey, String region, String bucketName) {
	}
}
