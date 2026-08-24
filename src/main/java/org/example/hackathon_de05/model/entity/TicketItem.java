package org.example.hackathon_de05.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketTicketOrder_id", nullable = false)
    private TicketOrder ticketTicketOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "busTrip_id", nullable = false)
    private BusTrip busTrip;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
}
