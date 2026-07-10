package dev.mello.api_sms.api.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequestDTO {

    @NotEmpty(message = "The phone number can't be empty")
    @Size(max = 12, message = "The phone number needs to have at least twelve digits")
    private String phoneNumber;

    @NotEmpty(message = "The message can't be empty")
    @Max(value = 155, message = "The message can't exceed the limit of 155 characters")
    private String message;
}
