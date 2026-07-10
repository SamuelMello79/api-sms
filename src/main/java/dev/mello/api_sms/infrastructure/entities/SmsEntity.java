package dev.mello.api_sms.infrastructure.entities;

import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "sms_message")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "message", nullable = false, length = 150)
    private String message;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SmsStatusEnum status;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}
