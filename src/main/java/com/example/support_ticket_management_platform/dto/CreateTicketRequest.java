package com.example.support_ticket_management_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record  CreateTicketRequest (
    @NotBlank(message = "Title is required")
    @Size(max = 150)
    String title,

    @NotBlank(message = "Description is required")
    String description
    )
{}
