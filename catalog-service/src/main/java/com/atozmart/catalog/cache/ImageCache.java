package com.atozmart.catalog.cache;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageCache(@JsonProperty("bytes") byte[] bytes) {

}
