package dev.mello.api_sms.api.mappers;

import dev.mello.api_sms.api.dto.SmsRequestDTOFixture;
import dev.mello.api_sms.api.dto.SmsResponseDTOFixture;
import dev.mello.api_sms.api.dtos.SmsRequestDTO;
import dev.mello.api_sms.api.dtos.SmsResponseDTO;
import dev.mello.api_sms.business.mappers.SmsMapper;
import dev.mello.api_sms.infrastructure.entities.SmsEntity;
import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmsMapperTest {
    private SmsMapper mapper;
    private SmsEntity smsEntity;
    private SmsRequestDTO requestDTO;
    private SmsResponseDTO responseDTO;

    @BeforeEach
    void setup(){

        mapper = Mappers.getMapper(SmsMapper.class);

        smsEntity = SmsEntity.builder()
                .id(1L)
                .phoneNumber("15996669999")
                .message("Hello")
                .status(SmsStatusEnum.SENT)
                .sentAt(LocalDateTime.parse("2026-07-11T14:36:00"))
                .build();


        requestDTO = SmsRequestDTOFixture.build(
                "15996669999",
                "Hello"
        );


        responseDTO = SmsResponseDTOFixture.build(
                1L,
                "15996669999",
                "Hello",
                SmsStatusEnum.SENT,
                LocalDateTime.parse("2026-07-11T14:36:00")
        );
    }

    @Test
    @DisplayName("Must convert Entity to ResponseDTO with success")
    void mustConvertEntityToResponseDTO() {
        SmsResponseDTO dto = mapper.toDTO(smsEntity);

        assertAll(
                "Must convert all fields correctly",
                () -> assertEquals(responseDTO.getId(), dto.getId()),
                () -> assertEquals(responseDTO.getPhoneNumber(), dto.getPhoneNumber()),
                () -> assertEquals(responseDTO.getMessage(), dto.getMessage()),
                () -> assertEquals(responseDTO.getStatus(), dto.getStatus()),
                () -> assertEquals(responseDTO.getSentAt(), dto.getSentAt())
        );
    }

    @Test
    @DisplayName("Must convert RequestDTO to Entity with success")
    void mustConvertRequestDTOToEntity() {
        SmsEntity entity = mapper.toEntity(requestDTO);

        assertAll(
                "Must convert all fields correctly",
                () -> assertEquals(smsEntity.getPhoneNumber(), entity.getPhoneNumber()),
                () -> assertEquals(smsEntity.getMessage(), entity.getMessage())
        );
    }

}
