package org.example.hackathon_de05.repository;

import org.example.hackathon_de05.model.entity.TicketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketItemRepository extends JpaRepository<TicketItem, Long> {
}
