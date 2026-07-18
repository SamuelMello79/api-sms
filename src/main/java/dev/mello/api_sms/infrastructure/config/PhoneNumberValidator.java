package dev.mello.api_sms.infrastructure.config;

import dev.mello.api_sms.infrastructure.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberValidator {

    public String normalize(String phoneNumber) {
        String digits = phoneNumber.replaceAll("\\D", "");

        isValid(digits);

        if (!digits.startsWith("55") && !digits.startsWith("+55")) {
            return "+55" + digits;
        }

        return "+" + digits;
    }

    private void isValid(String digits) {
        if (digits.isBlank()) {
            throw new BadRequestException("Phone number cannot be empty");
        }

        if (digits.length() != 11 && digits.length() != 13) {
            throw new BadRequestException("The phone number is invalid");
        }

        if (digits.length() == 13 && !digits.startsWith("55")) {
            throw new BadRequestException("The country code is invalid");
        }

        String nationalNumber = digits.length() == 13 ? digits.substring(2) : digits;

        if (nationalNumber.charAt(2) != '9') {
            throw new BadRequestException("The phone number is invalid");
        }
    }

}
