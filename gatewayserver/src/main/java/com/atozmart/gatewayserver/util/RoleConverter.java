package com.atozmart.gatewayserver.util;

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

/*
 * This class is used to extract roles from JWT token, convert them to GrantedAuthorities.
 */
@Slf4j
public class RoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(Jwt token) {
		List<String> roles = extractRoles(token);

		log.info("extracted roles from JWT: {}", roles);

		if (roles == null || roles.isEmpty()) {
			return Collections.emptyList();
		}

		return roles.stream().map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	private List<String> extractRoles(Jwt token) {
		Map<String, Object> realmAccess = token.getClaimAsMap("realm_access");

		if (realmAccess != null) {
			Object rolesObj = realmAccess.get("roles");
			if (rolesObj instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof String) {
				@SuppressWarnings("unchecked")
				List<String> roles = (List<String>) list;
				return roles;
			}
		}

		return token.getClaimAsStringList("roles");
	}

}
