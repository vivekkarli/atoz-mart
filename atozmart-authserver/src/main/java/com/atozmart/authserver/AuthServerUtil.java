package com.atozmart.authserver;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthServerUtil {

	public final String VERIFY_EMAIL_CONTENT = """
			<html>
			<body>
			hi %s,
			please verify your email!
			<a href=http://localhost:8072/atozmart/authserver/verify-email?code=%s>Confirm Email</a>"
			</body>
			</html>
			""";

	public static String getVerifyEmailContent(String username, String code) {
		return VERIFY_EMAIL_CONTENT.formatted(username, code);
	}
	
	

}
