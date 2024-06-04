package com.streamlined.emailsender.dto;

import lombok.Builder;

@Builder
public record ContactDto(String name, String email) {
}
