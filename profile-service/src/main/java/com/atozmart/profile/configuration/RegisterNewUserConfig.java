package com.atozmart.profile.configuration;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atozmart.profile.dto.ProfileDetails;
import com.atozmart.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RegisterNewUserConfig {

	private final ProfileService profileService;

	@Bean
	public Function<ProfileDetails, String> registerNewUser() {
		return profileDetails -> {
			log.info("registering new user");
			try {
				profileService.addNewProfile(profileDetails.getBasicDetails().getUsername(), profileDetails);
			} catch (Exception e) {
				log.error("failed: registering new user: {}", e.getMessage());
				return "registering new user failed";
			}

			return "registered new user successfully";
		};

	}

}
