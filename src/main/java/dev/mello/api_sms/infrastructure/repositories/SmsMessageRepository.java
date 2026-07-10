package dev.mello.api_sms.infrastructure.repositories;

import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import dev.mello.api_sms.infrastructure.entities.SmsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmsMessageRepository extends JpaRepository<SmsEntity, Long> {
    List<SmsEntity> findBySentAtBetweenAndStatus(LocalDateTime init, LocalDateTime end, SmsStatusEnum status);
}
