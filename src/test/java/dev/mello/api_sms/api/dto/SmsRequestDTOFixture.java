package dev.mello.api_sms.api.dto;

import dev.mello.api_sms.api.dtos.SmsRequestDTO;

public record SmsRequestDTOFixture(
        String phoneNumber,
        String message
) {
    public static SmsRequestDTO build(String phoneNumber, String message) {
        return new SmsRequestDTO(phoneNumber, message);
    }
}
