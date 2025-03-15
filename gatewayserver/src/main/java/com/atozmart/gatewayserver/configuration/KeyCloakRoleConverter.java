package com.atozmart.gatewayserver.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyCloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(Jwt token) {

		List<String> keyCloakUserRoles = new ArrayList<>();
		
		Map<String, Object> realmAccess = token.getClaimAsMap("realm_access");
		
		if(realmAccess != null)
			keyCloakUserRoles = (List<String>) realmAccess.get("roles");
		
		log.info("keyCloakUserRoles: {}",keyCloakUserRoles);
		if (!keyCloakUserRoles.isEmpty()) {
			return keyCloakUserRoles.stream()
					.map(roleName -> roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName.toUpperCase())
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
		}
		
		

		return Collections.emptyList();

	}

}
