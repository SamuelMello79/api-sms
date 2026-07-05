package dev.mello.api_sms.infrastructure.repositories;

import dev.mello.api_sms.infrastructure.model.SmsMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsMessageRepository extends JpaRepository<SmsMessage, Long> {
}
