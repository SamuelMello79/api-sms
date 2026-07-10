package dev.mello.api_sms.api;

import dev.mello.api_sms.api.dtos.SmsRequestDTO;
import dev.mello.api_sms.api.dtos.SmsResponseDTO;
import dev.mello.api_sms.business.services.SmsService;
import dev.mello.api_sms.infrastructure.enums.SmsStatusEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService service;

    @PostMapping
    public ResponseEntity<SmsResponseDTO> send(@RequestBody @Valid SmsRequestDTO request) {
        return ResponseEntity.ok(service.sendSms(request));
    }

    @GetMapping("/period")
    public ResponseEntity<List<SmsResponseDTO>> findMessagesSentInLast24HoursByStatus(
            @RequestParam("status") SmsStatusEnum status) {
        return ResponseEntity.ok(service.findMessagesSentInLast24HoursByStatus(status));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<SmsResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") SmsStatusEnum status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}
