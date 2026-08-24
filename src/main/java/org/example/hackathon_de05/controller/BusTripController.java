package org.example.hackathon_de05.controller;

import org.example.hackathon_de05.model.entity.BusTrip;
import org.example.hackathon_de05.repository.BusTripRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/busTrips")
public class BusTripController {

    private final BusTripRepository busTripRepository;

    public BusTripController(BusTripRepository busTripRepository) {
        this.busTripRepository = busTripRepository;
    }

    @GetMapping
    public List<BusTrip> getAllBusTrips() {
        return busTripRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusTrip> getBusTripById(@PathVariable Long id) {
        BusTrip busTrip = busTripRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm"));
        return ResponseEntity.ok(busTrip);
    }

    @GetMapping("/search")
    public List<BusTrip> searchBusTrips(@RequestParam String keyword) {
        return busTripRepository.searchByNameOrRoute(keyword, null, null, null);
    }
    @GetMapping("/searchByRoute")
    public List<BusTrip> searchBusTripsByRoute(@RequestParam Long routeId) {
        return busTripRepository.searchByNameOrRoute(null, routeId, null, null);
    }
}
