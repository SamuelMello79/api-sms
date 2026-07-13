package dev.mello.api_sms.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.mello.api_sms.api.dto.SmsRequestDTOFixture;
import dev.mello.api_sms.api.dto.SmsResponseDTOFixture;
import dev.mello.api_sms.api.dtos.SmsRequestDTO;
import dev.mello.api_sms.api.dtos.SmsResponseDTO;
import dev.mello.api_sms.business.services.SmsService;
import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SmsControllerTest {

    @InjectMocks
    private SmsController smsController;

    @Mock
    private SmsService smsService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private String url;

    private SmsRequestDTO smsRequestDTO;
    private SmsResponseDTO smsResponseDTO;
    private SmsResponseDTO smsResponseDTO2;
    private SmsResponseDTO smsResponseDTOUpdated;
    private List<SmsResponseDTO> smsResponseDTOList;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(smsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        url = "";

        smsRequestDTO = SmsRequestDTOFixture.build(
                "15996669999",
                "Hello"
        );


        smsResponseDTO = SmsResponseDTOFixture.build(
                1L,
                "15996669999",
                "Hello",
                SmsStatusEnum.SENT,
                LocalDateTime.parse("2026-07-11T14:36:00")
        );

        smsResponseDTO2 = SmsResponseDTOFixture.build(
                2L,
                "16996669999",
                "Hello",
                SmsStatusEnum.SENT,
                LocalDateTime.parse("2026-07-11T15:13:00")
        );

        smsResponseDTOUpdated = SmsResponseDTOFixture.build(
                1L,
                "15996669999",
                "Hello",
                SmsStatusEnum.SEND_ERROR,
                LocalDateTime.parse("2026-07-11T14:36:00")
        );

        smsResponseDTOList = List.of(
                smsResponseDTO,
                smsResponseDTO2
        );
    }

    @Test
    @DisplayName("POST - Must sent a SMS message and return 200")
    void mustSentSmsMessageAndReturn200() throws Exception {
        when(smsService.sendSms(any(SmsRequestDTO.class))).thenReturn(smsResponseDTO);

        String json = objectMapper.writeValueAsString(smsRequestDTO);
        System.out.println(json);

        mockMvc.perform(post("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.phoneNumber").value("15996669999"))
                .andExpect(jsonPath("$.message").value("Hello"))
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.sentAt").value("2026-07-11T14:36:00"));

        verify(smsService).sendSms(any(SmsRequestDTO.class));
    }

    @Test
    @DisplayName("POST - Must return 400 when message is invalid")
    void mustReturn400WhenMessageIsInvalid() throws Exception {

        SmsRequestDTO invalidRequest =
                SmsRequestDTOFixture.build(
                        "15996669999",
                        "a".repeat(156));


        mockMvc.perform(post("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH - Must return 400 when status is invalid")
    void mustReturn400WhenStatusInvalid() throws Exception {

        mockMvc.perform(patch("/{id}/update", 1L)
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /{id}/update - Must update status on SMS message and return 200")
    void mustUpdateStatusSmsMessageAndReturn200() throws Exception {
        when(smsService.updateStatus(1L, SmsStatusEnum.SEND_ERROR)).thenReturn(smsResponseDTOUpdated);

        mockMvc.perform(patch(url + "/{id}/update", 1L)
                .param("status", "SEND_ERROR")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value( 1L))
                .andExpect(jsonPath("$.phoneNumber").value("15996669999"))
                .andExpect(jsonPath("$.message").value("Hello"))
                .andExpect(jsonPath("$.status").value("SEND_ERROR"))
                .andExpect(jsonPath("$.sentAt").value("2026-07-11T14:36:00"));

        verify(smsService).updateStatus(1L, SmsStatusEnum.SEND_ERROR);
    }

    @Test
    @DisplayName("GET /period - Must list in period and return 200")
    void mustListSmsMessageInLast24AndReturn200() throws Exception {
        when(smsService.findMessagesSentInLast24HoursByStatus(SmsStatusEnum.SENT)).thenReturn(smsResponseDTOList);

        mockMvc.perform(get(url + "/period")
                        .param("status", "SENT")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].phoneNumber").value("15996669999"))
                .andExpect(jsonPath("$[0].status").value("SENT"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].phoneNumber").value("16996669999"))
                .andExpect(jsonPath("$[1].status").value("SENT"));

        verify(smsService).findMessagesSentInLast24HoursByStatus(SmsStatusEnum.SENT);
    }
}
