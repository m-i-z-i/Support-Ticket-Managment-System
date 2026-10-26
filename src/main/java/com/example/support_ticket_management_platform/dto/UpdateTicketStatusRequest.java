package com.example.support_ticket_management_platform.dto;

import com.example.support_ticket_management_platform.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(@NotNull TicketStatus status) {}
