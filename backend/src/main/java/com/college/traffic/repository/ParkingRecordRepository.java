package com.college.traffic.repository;

import com.college.traffic.model.ParkingRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {
    Optional<ParkingRecord> findFirstByVehicleNumberAndExitTimeIsNull(String vehicleNumber);
    List<ParkingRecord> findByExitTimeIsNullOrderByEntryTimeDesc();
    List<ParkingRecord> findTop50ByExitTimeIsNotNullOrderByExitTimeDesc();
    boolean existsBySlotId(Long slotId);

    @Query("select coalesce(sum(r.charge), 0) from ParkingRecord r where r.exitTime is not null")
    BigDecimal totalRevenue();
}
