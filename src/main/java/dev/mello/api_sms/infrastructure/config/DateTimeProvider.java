package dev.mello.api_sms.infrastructure.config;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeProvider {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
