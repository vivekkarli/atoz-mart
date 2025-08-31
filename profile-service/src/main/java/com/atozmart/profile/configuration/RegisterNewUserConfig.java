package com.atozmart.profile.configuration;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atozmart.profile.dto.ProfileDetailsDto;
import com.atozmart.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RegisterNewUserConfig {

	private final ProfileService profileService;

	@Bean
	public Consumer<ProfileDetailsDto> registerNewUser() {
		return profileDetails -> {
			log.info("registering new user");
			log.info("profileDetails: {}", profileDetails);
			try {
				profileService.addNewProfile(profileDetails.basicDetailsDto().username(), profileDetails);
			} catch (Exception e) {
				log.error("failed: registering new user: {}", e.getMessage());
			}

		};

	}

}
