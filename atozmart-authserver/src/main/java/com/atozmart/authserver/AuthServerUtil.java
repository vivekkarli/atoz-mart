package com.atozmart.authserver;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthServerUtil {

	public final String VERIFY_EMAIL_CONTENT = """
			<html>
			<body>
			hi %s,
			please verify your email!
			<a href=http://localhost:8072/atozmart/authserver/confirm-email?token=%s>Confirm Email</a>"
			</body>
			</html>
			""";
	
	public final String PASSWORD_RESET_CONTENT = """
			<html>
			<body>
			hi %s,
			use this link to reset your password!
			<a href=http://front-end-dns/reset-password?token=%s>reset password</a>"
			</body>
			</html>
			""";

	public static String getConfirmEmailContent(String username, String code) {
		return VERIFY_EMAIL_CONTENT.formatted(username, code);
	}
	
	public static String getPasswordResetEmailContent(String username, String code) {
		return PASSWORD_RESET_CONTENT.formatted(username, code);
	}
	
	

}
