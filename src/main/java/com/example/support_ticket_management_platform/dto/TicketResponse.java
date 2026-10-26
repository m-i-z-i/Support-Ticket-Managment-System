package com.example.support_ticket_management_platform.dto;

import java.time.LocalDateTime;

import com.example.support_ticket_management_platform.enums.TicketStatus;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        LocalDateTime createdAt
        ) {

}
