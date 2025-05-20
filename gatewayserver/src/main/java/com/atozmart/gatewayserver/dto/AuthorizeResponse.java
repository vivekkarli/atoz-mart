package com.atozmart.gatewayserver.dto;

import java.util.Date;
import java.util.List;

public record AuthorizeResponse(boolean valid, String username, String email, Date expiresAt, List<String> roles) {
}
