package dev.mello.api_sms.infrastructure.config;

import com.twilio.Twilio;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {

    public TwilioInitializer(TwilioConfiguration senderConfiguration) {
        Twilio.init(
                senderConfiguration.getAccountSid(),
                senderConfiguration.getAuthToken()
        );
    }
}
