package com.college.traffic.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** A parking bay. 'occupied' is kept in sync by ParkingService. */
@Entity
@Table(name = "parking_slots")
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String slotNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10)
    private SlotType slotType;
    @Column(nullable = false)
    private boolean occupied = false;

    public Long getId() { return id; }
    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }
    public SlotType getSlotType() { return slotType; }
    public void setSlotType(SlotType slotType) { this.slotType = slotType; }
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
}
