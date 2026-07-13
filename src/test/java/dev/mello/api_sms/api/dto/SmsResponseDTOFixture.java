package dev.mello.api_sms.api.dto;

import dev.mello.api_sms.api.dtos.SmsResponseDTO;
import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;

import java.time.LocalDateTime;

public record SmsResponseDTOFixture(
        Long id,
        String phoneNumber,
        String message,
        SmsStatusEnum status,
        LocalDateTime sentAt
) {
    public static SmsResponseDTO build(
            Long id, String phoneNumber, String message, SmsStatusEnum status,
            LocalDateTime sentAt) {
        return new SmsResponseDTO(id, phoneNumber, message, status, sentAt);
    }
}
