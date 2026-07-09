package dev.mello.api_sms.api.dtos;

import dev.mello.api_sms.infrastructure.enums.ShippingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsMessageDTOResponse {
    private Long id;
    private String phoneNumber;
    private String message;
    private ShippingStatus status;
    private LocalDateTime sentAt;
}
