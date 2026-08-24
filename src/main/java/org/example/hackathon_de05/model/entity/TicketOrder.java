package org.example.hackathon_de05.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hackathon_de05.model.constant.TicketOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ticketTicketTicketOrders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @Column(nullable = false)
    private LocalDateTime ticketTicketOrderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketOrderStatus status;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    // Ghi chú nguồn gốc đơn hàng, ví dụ: "Đặt qua AI Chatbot"
    @Column(length = 255)
    private String note;
}
