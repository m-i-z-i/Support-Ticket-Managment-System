package com.example.support_ticket_management_platform.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.support_ticket_management_platform.enums.TicketStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.support_ticket_management_platform.dto.CreateTicketRequest;
import com.example.support_ticket_management_platform.dto.TicketResponse;
import com.example.support_ticket_management_platform.entity.Ticket;
import com.example.support_ticket_management_platform.repository.TicketRepository;

@Service 
public class TicketService {

    private final TicketRepository repository;

    public TicketService(TicketRepository repository){
        this.repository=repository;
    }
    public List<Ticket> getAllTickets(){
        return repository.findAll();
    }
    public Ticket findByid(Long id){
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket is not found: " + id));
    }

    @Transactional
    public TicketResponse updateStatus(Long id, TicketStatus status) {
        Ticket ticket = findByid(id);
        ticket.setStatus(status);
        Ticket saved = repository.save(ticket);
        return new TicketResponse(saved.getId(), saved.getTitle(), saved.getDescription(),
                saved.getStatus(), saved.getCreatedAt());
    }

    public TicketResponse createTicket(CreateTicketRequest request){
        Ticket ticket = new Ticket(request.title(),request.description());

        Ticket saveTicket = repository.save(ticket);

        return new TicketResponse(
            saveTicket.getId(),
            saveTicket.getTitle(),
            saveTicket.getDescription(),
            saveTicket.getStatus(),
            saveTicket.getCreatedAt()
        );
    }


}
