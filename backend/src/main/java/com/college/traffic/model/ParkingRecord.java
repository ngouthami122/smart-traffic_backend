package com.college.traffic.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** One parking visit. exitTime is null while the vehicle is still parked. */
@Entity
@Table(name = "parking_records", indexes = @Index(name = "idx_record_vehicle", columnList = "vehicle_number"))
public class ParkingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ParkingSlot slot;
    @Column(nullable = false, length = 15)
    private String vehicleNumber;
    @Column(nullable = false)
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    @Column(precision = 10, scale = 2)
    private BigDecimal charge;

    public Long getId() { return id; }
    public ParkingSlot getSlot() { return slot; }
    public void setSlot(ParkingSlot slot) { this.slot = slot; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public BigDecimal getCharge() { return charge; }
    public void setCharge(BigDecimal charge) { this.charge = charge; }
}
