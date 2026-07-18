package dev.mello.api_sms.business.mappers;

import dev.mello.api_sms.api.dtos.SmsRequestDTO;
import dev.mello.api_sms.api.dtos.SmsResponseDTO;
import dev.mello.api_sms.infrastructure.entities.SmsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SmsMapper {
    SmsEntity toEntity(SmsRequestDTO request);
    SmsResponseDTO toDTO(SmsEntity entity);
}
