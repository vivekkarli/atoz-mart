package com.atozmart.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfilePhotoCache(@JsonProperty("bytes") byte[] bytes) {

}
