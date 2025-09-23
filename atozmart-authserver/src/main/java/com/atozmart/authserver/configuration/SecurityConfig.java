package com.atozmart.authserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.atozmart.authserver.dao.AppUserDao;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
	
	private final AppUserDao appUserDao;
	
	private final PasswordEncoder passwordEncoder;

	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> 
		authorize
		.requestMatchers("/admin/**").hasAnyRole("admin", "app")
		.anyRequest().permitAll());
		http.csrf(csrfSpec-> csrfSpec.disable());
		http.httpBasic(Customizer.withDefaults());
		
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(appUserDao);
		provider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(provider);
	}

}
