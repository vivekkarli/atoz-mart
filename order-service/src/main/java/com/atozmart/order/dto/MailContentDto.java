package com.atozmart.order.dto;

import java.io.File;

public record MailContentDto(String to, String cc, String content, File attachment) {

}
