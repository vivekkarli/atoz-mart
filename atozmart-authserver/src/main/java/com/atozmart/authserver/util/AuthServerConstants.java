package com.atozmart.authserver.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthServerConstants {

	public final String VERIFY_EMAIL_CONTENT = """
			<html>
			<body>
			hi %s,
			please verify your email!
			<a href=%s>Confirm Email</a>"
			</body>
			</html>
			""";
	
	public final String PASSWORD_RESET_CONTENT = """
			<html>
			<body>
			hi %s,
			use this link to reset your password!
			<a href= %s>reset password</a>"
			</body>
			</html>
			""";

	public static String getConfirmEmailContent(String username, String code) {
		return VERIFY_EMAIL_CONTENT.formatted(username, code);
	}
	
	public static String getPasswordResetEmailContent(String username, String resetLink) {
		return PASSWORD_RESET_CONTENT.formatted(username, resetLink);
	}
	
	

}
