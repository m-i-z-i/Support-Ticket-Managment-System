package com.example.support_ticket_management_platform;

import com.example.support_ticket_management_platform.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class TicketApiTests {
    @Autowired WebApplicationContext context;
    @Autowired TicketRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void createsAndRetrievesPersistedTicket() throws Exception {
        mvc.perform(post("/api/tickets").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Cannot log in","description":"Password reset fails"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.status").value("OPEN"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty());

        var tickets = repository.findAll();
        assertThat(tickets).hasSize(1);
        mvc.perform(get("/api/tickets/{id}", tickets.getFirst().getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Cannot log in"));
        mvc.perform(get("/api/tickets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void rejectsInvalidRequestsWithoutSaving() throws Exception {
        for (String body : new String[] {
                "{\"title\":\" \",\"description\":\"Issue\"}",
                "{\"title\":\"Issue\",\"description\":\"\"}",
                "{\"title\":\"" + "x".repeat(151) + "\",\"description\":\"Issue\"}"}) {
            mvc.perform(post("/api/tickets").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        }
        assertThat(repository.count()).isZero();
    }

    @Test
    void updatesStatusAndPersistsIt() throws Exception {
        var ticket = repository.save(new com.example.support_ticket_management_platform.entity.Ticket(
                "Login issue", "Cannot log in"));
        for (String value : new String[] {"IN_PROGRESS", "RESOLVED", "CLOSED", "OPEN"}) {
            mvc.perform(patch("/api/tickets/{id}/status", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"" + value + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(value))
                .andExpect(jsonPath("$.title").value("Login issue"));
            assertThat(repository.findById(ticket.getId()).orElseThrow().getStatus().name())
                .isEqualTo(value);
        }
    }

    @Test
    void rejectsInvalidStatusAndMissingTickets() throws Exception {
        var ticket = repository.save(new com.example.support_ticket_management_platform.entity.Ticket(
                "Login issue", "Cannot log in"));
        for (String body : new String[] {"{}", "{\"status\":null}", "{\"status\":\"INVALID\"}"}) {
            mvc.perform(patch("/api/tickets/{id}/status", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        }
        assertThat(repository.findById(ticket.getId()).orElseThrow().getStatus().name()).isEqualTo("OPEN");
        mvc.perform(patch("/api/tickets/999999/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CLOSED\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void missingTicketReturns404AndMalformedIdReturns400() throws Exception {
        mvc.perform(get("/api/tickets/999999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/tickets/invalid")).andExpect(status().isBadRequest());
    }
}
