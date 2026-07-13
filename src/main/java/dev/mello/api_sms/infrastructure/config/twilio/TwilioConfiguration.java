package dev.mello.api_sms.infrastructure.config.twilio;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TwilioConfiguration {
    private final String accountSid;
    private final String authToken;
    private final String trialNumber;

    public TwilioConfiguration(
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken,
            @Value("${twilio.trial-number}") String trialNumber) {

        this.accountSid = accountSid;
        this.authToken = authToken;
        this.trialNumber = trialNumber;
    }
}
