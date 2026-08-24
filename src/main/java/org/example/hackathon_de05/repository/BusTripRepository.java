package org.example.hackathon_de05.repository;

import org.example.hackathon_de05.model.entity.BusTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusTripRepository extends JpaRepository<BusTrip, Long> {
    @Query("select bt from BusTrip bt where bt.name = :keyword or bt.busRoute.id = :routeId ")
    List<BusTrip> searchByNameOrRoute(@Param("keyword") String keyword, @Param("routeId") Long routeId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    Optional<BusTrip> findById(Long id);

    List<BusTrip> findByNameContainingIgnoreCaseOrBusRouteNameContainingIgnoreCase(String nameKeyword, String routeKeyword);

    List<BusTrip> findByBusRouteNameContainingIgnoreCase(String routeName);
}

