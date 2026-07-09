package dev.mello.api_sms.business.services;

import dev.mello.api_sms.api.dtos.SmsMessageDTORequest;
import dev.mello.api_sms.api.dtos.SmsMessageDTOResponse;
import dev.mello.api_sms.business.mappers.SmsMessageMapper;
import dev.mello.api_sms.infrastructure.enums.ShippingStatus;
import dev.mello.api_sms.infrastructure.models.SmsMessage;
import dev.mello.api_sms.infrastructure.repositories.SmsMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsMessageService {

    private final SmsMessageRepository repository;
    private final SmsMessageMapper mapper;

    public SmsMessageDTOResponse save(SmsMessageDTORequest request) {
        SmsMessage entity = repository.save(mapper.toEntity(request));
        return mapper.toDTO(entity);
    }

    public SmsMessageDTOResponse updateStatus(Long id, ShippingStatus status) {
        SmsMessage entity = existsById(id);
        entity.setStatus(status);

        if (status == ShippingStatus.SENT) {
            entity.setSentAt(LocalDateTime.now());
        }

        return mapper.toDTO(repository.save(entity));
    }

    public List<SmsMessageDTOResponse> findMessagesSentInLast24HoursByStatus(ShippingStatus status) {
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

    public SmsMessageDTOResponse findById(Long id) {
        SmsMessage entity = existsById(id);
        return mapper.toDTO(entity);
    }

    private SmsMessage existsById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("The id " + id + " doesn't exists"));
    }
}
