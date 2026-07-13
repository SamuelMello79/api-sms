package dev.mello.api_sms.infrastructure.gateway;

import dev.mello.api_sms.infrastructure.config.twilio.TwilioConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


@Component
@RequiredArgsConstructor
public class TwilioSmsGateway implements SmsGateway {

    public final TwilioConfiguration twilioConfiguration;

    @Override
    public void sendSms(String phoneNumber, String message) {
        Message.creator(
                new PhoneNumber(formatPhoneNumber(phoneNumber)),
                new PhoneNumber(twilioConfiguration.getTrialNumber()),
                message
        ).create();
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+55")) {
            return phoneNumber;
        }

        return "+55" + phoneNumber;
    }
}
