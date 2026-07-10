package dev.mello.api_sms.business.services;

import dev.mello.api_sms.api.dtos.SmsRequestDTO;
import dev.mello.api_sms.api.dtos.SmsResponseDTO;
import dev.mello.api_sms.business.mappers.SmsMapper;
import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import dev.mello.api_sms.infrastructure.entities.SmsEntity;
import dev.mello.api_sms.infrastructure.gateway.SmsGateway;
import dev.mello.api_sms.infrastructure.repositories.SmsMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsMessageRepository repository;
    private final SmsGateway gateway;
    private final SmsMapper mapper;

    public SmsResponseDTO sendSms(SmsRequestDTO dto) {
        SmsEntity entity = mapper.toEntity(dto);

        entity.setSentAt(LocalDateTime.now());

        try {
            gateway.sendSms(
                    entity.getPhoneNumber(),
                    entity.getMessage()
            );

            entity.setStatus(SmsStatusEnum.SENT);
        } catch (Exception e) {
            entity.setStatus(SmsStatusEnum.SEND_ERROR);
        }

        return mapper.toDTO(repository.save(entity));
    }

    public SmsResponseDTO updateStatus(Long id, SmsStatusEnum status) {
        SmsEntity entity = existsById(id);
        entity.setStatus(status);
        return mapper.toDTO(repository.save(entity));
    }

    public List<SmsResponseDTO> findMessagesSentInLast24HoursByStatus(SmsStatusEnum status) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);

        return repository.findBySentAtBetweenAndStatus(
                twentyFourHoursAgo,
                now,
                status
                ).stream()
                .map(mapper::toDTO)
                .toList();
    }

    private SmsEntity existsById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SMS not founded"));
    }
}
