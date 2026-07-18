package dev.mello.api_sms.business;

import dev.mello.api_sms.api.dto.SmsRequestDTOFixture;
import dev.mello.api_sms.api.dto.SmsResponseDTOFixture;
import dev.mello.api_sms.api.dtos.SmsRequestDTO;
import dev.mello.api_sms.api.dtos.SmsResponseDTO;
import dev.mello.api_sms.business.mappers.SmsMapper;
import dev.mello.api_sms.business.services.SmsService;
import dev.mello.api_sms.infrastructure.config.DateTimeProvider;
import dev.mello.api_sms.infrastructure.config.PhoneNumberValidator;
import dev.mello.api_sms.infrastructure.entities.SmsEntity;
import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import dev.mello.api_sms.infrastructure.exceptions.NotFoundException;
import dev.mello.api_sms.infrastructure.exceptions.SmsGatewayException;
import dev.mello.api_sms.infrastructure.gateway.SmsGateway;
import dev.mello.api_sms.infrastructure.repositories.SmsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SmsServiceTest {

    @InjectMocks
    private SmsService smsService;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Mock
    private SmsGateway smsGateway;

    @Mock
    private SmsRepository smsRepository;

    @Mock
    private SmsMapper smsMapper;

    private SmsEntity smsEntitySaved;
    private SmsEntity smsEntitySaved2;
    private SmsEntity smsEntityMapped;
    private SmsEntity smsEntityUpdated;
    private List<SmsEntity> smsEntityList;

    private SmsRequestDTO smsRequestDTO;
    private SmsResponseDTO smsResponseDTO;
    private SmsResponseDTO smsResponseDTO2;
    private SmsResponseDTO smsResponseDTOUpdated;
    private SmsResponseDTO smsErrorResponseDTO;
    private List<SmsResponseDTO> smsResponseDTOList;

    @BeforeEach
    public void setup() {
        smsEntityMapped = SmsEntity.builder()
                .phoneNumber("15996669999")
                .message("Hello")
                .build();

        smsEntitySaved = SmsEntity.builder()
                .id(1L)
                .phoneNumber("+5515996669999")
                .message("Hello")
                .status(SmsStatusEnum.SENT)
                .sentAt(LocalDateTime.parse("2026-07-11T14:36:00"))
                .build();

        smsEntitySaved2 = SmsEntity.builder()
                .id(2L)
                .phoneNumber("+5516996669999")
                .message("Hello")
                .status(SmsStatusEnum.SENT)
                .sentAt(LocalDateTime.parse("2026-07-11T15:13:00"))
                .build();

        smsEntityUpdated = SmsEntity.builder()
                .id(1L)
                .phoneNumber("+5515996669999")
                .message("Hello")
                .status(SmsStatusEnum.SEND_ERROR)
                .sentAt(LocalDateTime.parse("2026-07-11T14:36:00"))
                .build();

        smsRequestDTO = SmsRequestDTOFixture.build(
                "15996669999",
                "Hello"
        );


        smsResponseDTO = SmsResponseDTOFixture.build(
                1L,
                "+5515996669999",
                "Hello",
                SmsStatusEnum.SENT,
                LocalDateTime.parse("2026-07-11T14:36:00")
        );

        smsResponseDTO2 = SmsResponseDTOFixture.build(
                2L,
                "+5516996669999",
                "Hello",
                SmsStatusEnum.SENT,
                LocalDateTime.parse("2026-07-11T15:13:00")
        );

        smsResponseDTOUpdated = SmsResponseDTOFixture.build(
                1L,
                "+5515996669999",
                "Hello",
                SmsStatusEnum.SEND_ERROR,
                LocalDateTime.parse("2026-07-11T14:36:00")
        );

        smsErrorResponseDTO = SmsResponseDTOFixture.build(
                1L,
                "+5515996669999",
                "Hello",
                SmsStatusEnum.SEND_ERROR,
                LocalDateTime.parse("2026-07-11T14:36:00")
        );

        smsEntityList = List.of(
                smsEntitySaved,
                smsEntitySaved2
        );

        smsResponseDTOList = List.of(
                smsResponseDTO,
                smsResponseDTO2
        );
    }

    @Test
    @DisplayName("Must sent SMS message with success")
    void mustSendSmsMessageWithSuccess() {
        when(smsMapper.toEntity(smsRequestDTO)).thenReturn(smsEntityMapped);
        when(dateTimeProvider.now()).thenReturn(LocalDateTime.parse("2026-07-07T14:36:00"));
        when(phoneNumberValidator.normalize(smsEntityMapped.getPhoneNumber()))
                .thenReturn("+55" + smsEntityMapped.getPhoneNumber());
        when(smsRepository.save(smsEntityMapped)).thenReturn(smsEntitySaved);
        when(smsMapper.toDTO(smsEntitySaved)).thenReturn(smsResponseDTO);


        doNothing()
                .when(smsGateway)
                .sendSms(
                        "+55" + smsEntityMapped.getPhoneNumber(),
                        smsEntityMapped.getMessage()
                );

        SmsResponseDTO dto = smsService.sendSms(smsRequestDTO);

        assertAll(
                "Test",
                () -> assertEquals(smsResponseDTO.getId(), dto.getId()),
                () -> assertEquals(smsResponseDTO.getPhoneNumber(), dto.getPhoneNumber()),
                () -> assertEquals(smsResponseDTO.getMessage(), dto.getMessage()),
                () -> assertEquals(smsResponseDTO.getStatus(), dto.getStatus()),
                () -> assertEquals(smsResponseDTO.getSentAt(), dto.getSentAt())
        );

        verify(smsGateway)
                .sendSms(
                        smsEntityMapped.getPhoneNumber(),
                        smsEntityMapped.getMessage()
                );
        verify(smsRepository).save(smsEntityMapped);
        verify(smsMapper).toDTO(smsEntitySaved);
    }

    @Test
    @DisplayName("Must save SMS with SEND_ERROR when gateway fails")
    void mustSaveSmsWithErrorWhenGatewayFails() {
        when(smsMapper.toEntity(smsRequestDTO)).thenReturn(smsEntityMapped);
        when(dateTimeProvider.now()).thenReturn(LocalDateTime.parse("2026-07-07T14:36:00"));
        when(phoneNumberValidator.normalize(smsEntityMapped.getPhoneNumber()))
                .thenReturn("+55" + smsEntityMapped.getPhoneNumber());

        doThrow(new SmsGatewayException("Twilio unavailable"))
                .when(smsGateway)
                .sendSms(
                        "+55" + smsEntityMapped.getPhoneNumber(),
                        smsEntityMapped.getMessage()
                );

        when(smsRepository.save(smsEntityMapped)).thenReturn(smsEntityUpdated);
        when(smsMapper.toDTO(smsEntityUpdated)).thenReturn(smsErrorResponseDTO);

        SmsResponseDTO dto = smsService.sendSms(smsRequestDTO);


        assertAll(
                () -> assertEquals(SmsStatusEnum.SEND_ERROR, smsEntityMapped.getStatus()),
                () -> assertEquals(SmsStatusEnum.SEND_ERROR, dto.getStatus()),
                () -> assertEquals(smsErrorResponseDTO.getMessage(), dto.getMessage())
        );

        verify(smsGateway)
                .sendSms(
                        smsEntityMapped.getPhoneNumber(),
                        smsEntityMapped.getMessage()
                );
        verify(smsRepository).save(smsEntityMapped);
        verify(smsMapper).toDTO(smsEntityUpdated);
        verify(phoneNumberValidator).normalize(smsRequestDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("Must throw NotFoundException when SMS does not exist")
    void mustThrowNotFoundWhenSmsDoesNotExist() {
        Long unavailableId = -1L;
        when(smsRepository.findById(unavailableId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> smsService.updateStatus(unavailableId,SmsStatusEnum.SEND_ERROR)
        );

        verify(smsRepository).findById(unavailableId);
        verify(smsRepository, never()).save(any());
        verify(smsMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Must update status of sms message with success")
    void mustUpdateStatusSmsMessageWithSuccess() {
        when(smsRepository.findById(1L)).thenReturn(Optional.of(smsEntitySaved));
        when(smsRepository.save(smsEntitySaved)).thenReturn(smsEntityUpdated);
        when(smsMapper.toDTO(smsEntityUpdated)).thenReturn(smsResponseDTOUpdated);


        SmsResponseDTO dto = smsService.updateStatus(1L, SmsStatusEnum.SEND_ERROR);

        assertAll(
                "Must update status on entity with success",
                () -> assertEquals(smsResponseDTOUpdated.getId(), dto.getId()),
                () -> assertEquals(smsResponseDTOUpdated.getStatus(), dto.getStatus()),
                () -> assertEquals(smsResponseDTOUpdated.getPhoneNumber(), dto.getPhoneNumber()),
                () -> assertEquals(smsResponseDTOUpdated.getMessage(), dto.getMessage())
        );
    }

    @Test
    @DisplayName("Must list sms's sent in last 24h filtering by status")
    void mustListSmsSentInLast24hByStatus() {
        when(dateTimeProvider.now()).thenReturn(LocalDateTime.parse("2026-07-11T17:34:00"));

        LocalDateTime now = dateTimeProvider.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        SmsStatusEnum status = SmsStatusEnum.SENT;

        when(smsRepository.findBySentAtBetweenAndStatus(
                twentyFourHoursAgo,
                now,
                status)).thenReturn(smsEntityList);

        when(smsMapper.toDTO(smsEntityList.get(0))).thenReturn(smsResponseDTOList.get(0));
        when(smsMapper.toDTO(smsEntityList.get(1))).thenReturn(smsResponseDTOList.get(1));

        List<SmsResponseDTO> dtos = smsService.findMessagesSentInLast24HoursByStatus(status);

        assertAll(
                () -> assertEquals(smsResponseDTOList, dtos),
                () -> assertEquals(2, dtos.size()),
                () -> assertTrue(dtos.stream()
                        .allMatch(dto -> dto.getStatus() == SmsStatusEnum.SENT))
        );

        verify(smsRepository).findBySentAtBetweenAndStatus(twentyFourHoursAgo, now, status);
        verify(smsMapper, times(2)).toDTO(any(SmsEntity.class));
    }

    // TODO: implement a new module to test the checks of the PhoneNumberValidator
}
