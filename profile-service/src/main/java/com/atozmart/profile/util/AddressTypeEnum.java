package com.atozmart.profile.util;

public enum AddressTypeEnum {

	HOME("home"), WORK("work"), OTHERS("others");

	private String addressType;

	AddressTypeEnum(String addressType) {
		this.addressType = addressType;
	}

	public String getAddressType() {
		return addressType;
	}

	// Custom method to convert string to enum
	public static AddressTypeEnum fromString(String addressType) {
		if (addressType != null) {
			for (AddressTypeEnum addressTypeEnum : AddressTypeEnum.values()) {
				if (addressType.equalsIgnoreCase(addressTypeEnum.getAddressType())) {
					return addressTypeEnum;
				}
			}
		}
		throw new IllegalArgumentException("No enum constant with addressType: " + addressType);
	}

}
