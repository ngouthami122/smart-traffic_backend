package com.college.traffic.repository;

import com.college.traffic.model.ParkingSlot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findAllByOrderBySlotNumberAsc();
    List<ParkingSlot> findByOccupiedOrderBySlotNumberAsc(boolean occupied);
    long countByOccupied(boolean occupied);
    boolean existsBySlotNumberIgnoreCase(String slotNumber);
    boolean existsBySlotNumberIgnoreCaseAndIdNot(String slotNumber, Long id);
}
