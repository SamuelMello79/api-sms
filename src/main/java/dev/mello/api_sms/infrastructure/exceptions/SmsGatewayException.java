package dev.mello.api_sms.infrastructure.exceptions;

import com.twilio.exception.ApiException;

public class SmsGatewayException extends ApiException {
    public SmsGatewayException(String message) {
        super(message);
    }
}
