package com.atozmart.gatewayserver.dto;

import java.util.List;

public record AuthorizeResponse (boolean valid, List<String> roles) {}
