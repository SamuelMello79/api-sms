package dev.mello.api_sms.business.services;

import dev.mello.api_sms.api.dtos.SmsRequestDTO;
import dev.mello.api_sms.api.dtos.SmsResponseDTO;
import dev.mello.api_sms.business.mappers.SmsMapper;
import dev.mello.api_sms.infrastructure.config.DateTimeProvider;
import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import dev.mello.api_sms.infrastructure.entities.SmsEntity;
import dev.mello.api_sms.infrastructure.exceptions.NotFoundException;
import dev.mello.api_sms.infrastructure.exceptions.SmsGatewayException;
import dev.mello.api_sms.infrastructure.gateway.SmsGateway;
import dev.mello.api_sms.infrastructure.repositories.SmsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsRepository repository;
    private final DateTimeProvider dateTimeProvider;
    private final SmsGateway gateway;
    private final SmsMapper mapper;

    public SmsResponseDTO sendSms(SmsRequestDTO dto) {
        SmsEntity entity = mapper.toEntity(dto);

        entity.setSentAt(dateTimeProvider.now());

        try {
            gateway.sendSms(
                    entity.getPhoneNumber(),
                    entity.getMessage()
            );

            entity.setStatus(SmsStatusEnum.SENT);
        } catch (SmsGatewayException e) {
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
        LocalDateTime now = dateTimeProvider.now();
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
                .orElseThrow(() -> new NotFoundException("SMS not founded"));
    }
}
