package org.example.hackathon_de05.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hackathon_de05.model.entity.*;
import org.example.hackathon_de05.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializeService {

    private final BusRouteRepository busRouteRepository;
    private final BusTripRepository busTripRepository;
    private final PassengerRepository passengerRepository;

    @PostConstruct
    public void initializeDatabase() {
        if (busRouteRepository.count() == 0) {
            System.out.println("Initializing generic data for De05...");
            BusRoute c1 = busRouteRepository.save(new BusRoute(null, "Type A", "Description A"));
            BusRoute c2 = busRouteRepository.save(new BusRoute(null, "Type B", "Description B"));
            
            busTripRepository.saveAll(List.of(
                new BusTrip(null, "Item 1", "Desc 1", new BigDecimal("100000"), 50, null, c1),
                new BusTrip(null, "Item 2", "Desc 2", new BigDecimal("200000"), 30, null, c2)
            ));
            
            passengerRepository.saveAll(List.of(
                new Passenger(null, "User A", "0901234567", "a@example.com", "Address A"),
                new Passenger(null, "User B", "0912345678", "b@example.com", "Address B")
            ));
        }
    }
}
