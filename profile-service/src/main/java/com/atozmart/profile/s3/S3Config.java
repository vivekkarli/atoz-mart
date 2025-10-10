package com.atozmart.profile.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atozmart.profile.configuration.ProfileConfig;
import com.atozmart.profile.configuration.ProfileConfig.AwsDetails;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

	private AwsDetails awsDetails;

	public S3Config(ProfileConfig profileConfig) {
		this.awsDetails = profileConfig.aws();
	}

	@Bean
	public S3Client s3Client() {
		AwsBasicCredentials basicCredentials = AwsBasicCredentials.builder().accessKeyId(awsDetails.accessKey())
				.secretAccessKey(awsDetails.secretKey()).build();

		return S3Client.builder().region(Region.of(awsDetails.region()))
				.credentialsProvider(StaticCredentialsProvider.create(basicCredentials)).build();
	}

}
