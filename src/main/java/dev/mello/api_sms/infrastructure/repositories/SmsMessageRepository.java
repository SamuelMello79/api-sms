package dev.mello.api_sms.infrastructure.repositories;

import dev.mello.api_sms.infrastructure.enums.ShippingStatus;
import dev.mello.api_sms.infrastructure.models.SmsMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmsMessageRepository extends JpaRepository<SmsMessage, Long> {
    List<SmsMessage> findBySentAtBetweenAndStatus(LocalDateTime initDate, LocalDateTime finalDate, ShippingStatus status);
}
