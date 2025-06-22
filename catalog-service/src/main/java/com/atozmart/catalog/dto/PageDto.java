package com.atozmart.catalog.dto;

public record PageDto(int pageNo, int size, String sortBy, String sortDirection, boolean isLastPage) {

}
