package com.atozmart.catalog.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catalog-service")
public record CatalogConfig(AwsDetails aws, String publicBaseUrl, Long cacheExpiry) {
	public record AwsDetails(String accessKey, String secretKey, String region, String bucketName) {
	}
}
