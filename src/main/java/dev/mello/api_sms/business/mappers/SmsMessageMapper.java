package dev.mello.api_sms.business.mappers;

import dev.mello.api_sms.api.dtos.SmsMessageDTORequest;
import dev.mello.api_sms.api.dtos.SmsMessageDTOResponse;
import dev.mello.api_sms.infrastructure.models.SmsMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SmsMessageMapper {
    SmsMessage toEntity(SmsMessageDTORequest request);
    SmsMessageDTOResponse toDTO(SmsMessage entity);
}
