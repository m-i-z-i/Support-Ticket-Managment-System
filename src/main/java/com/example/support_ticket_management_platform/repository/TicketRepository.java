package com.example.support_ticket_management_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.support_ticket_management_platform.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>{
}