package com.example.support_ticket_management_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import com.example.support_ticket_management_platform.dto.UpdateTicketStatusRequest;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import com.example.support_ticket_management_platform.dto.CreateTicketRequest;
import com.example.support_ticket_management_platform.dto.TicketResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.support_ticket_management_platform.entity.Ticket;
import com.example.support_ticket_management_platform.service.TicketService;


@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:3000")
public class TicketController {
    private final TicketService ticketService;

    

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable("id") Long id) {
        return ticketService.findByid(id);
    }
    @GetMapping
    public List<Ticket> allTickets() {
        return ticketService.getAllTickets();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @PatchMapping("/{id}/status")
    public TicketResponse updateStatus(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ticketService.updateStatus(id, request.status());
    }
    
    
}
