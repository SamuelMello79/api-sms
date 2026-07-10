package dev.mello.api_sms.infrastructure.gateway;

public interface SmsGateway {
    void sendSms(String phoneNumber, String message);
}
