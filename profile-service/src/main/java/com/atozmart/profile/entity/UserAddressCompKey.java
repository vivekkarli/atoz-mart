package com.atozmart.profile.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressCompKey {
	
	private String username;

	private AddressTypeEnum addressType;

}
