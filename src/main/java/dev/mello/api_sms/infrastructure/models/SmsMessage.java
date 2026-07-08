package dev.mello.api_sms.infrastructure.models;

import dev.mello.api_sms.infrastructure.enums.ShippingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SmsMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "message", nullable = false, length = 150)
    private String message;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShippingStatus status;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}
