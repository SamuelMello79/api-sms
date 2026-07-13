package dev.mello.api_sms.infrastructure.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("test")
public class FakeGateway implements SmsGateway{

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("[Gateway] Fake gateway received message with success");
        log.info("[Gateway] phoneNumber: {}", formatPhoneNumber(phoneNumber));
        log.info("[Gateway] message: {}", message);
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+55")) {
            return phoneNumber;
        }

        return "+55" + phoneNumber;
    }
}
