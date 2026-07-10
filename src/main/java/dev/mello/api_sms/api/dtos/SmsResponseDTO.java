package dev.mello.api_sms.api.dtos;

import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsResponseDTO {
    private Long id;
    private String phoneNumber;
    private String message;
    private SmsStatusEnum status;
    private LocalDateTime sentAt;
}
