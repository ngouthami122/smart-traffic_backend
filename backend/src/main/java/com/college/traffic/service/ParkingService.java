package com.college.traffic.service;

import com.college.traffic.dto.Requests.*;
import com.college.traffic.exception.ApiException;
import com.college.traffic.model.*;
import com.college.traffic.repository.*;
import com.college.traffic.util.VehicleNumber;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Parking slots, vehicle entry/exit and charge calculation. */
@Service
public class ParkingService {
    private final ParkingSlotRepository slots;
    private final ParkingRecordRepository records;
    private final ParkingRates rates;

    public ParkingService(ParkingSlotRepository slots, ParkingRecordRepository records, ParkingRates rates) {
        this.slots = slots; this.records = records; this.rates = rates;
    }

    /** @param status "AVAILABLE", "OCCUPIED" or null for all slots */
    @Transactional(readOnly = true)
    public List<ParkingSlot> listSlots(String status) {
        if (status == null || status.isBlank()) return slots.findAllByOrderBySlotNumberAsc();
        return switch (status.toUpperCase()) {
            case "AVAILABLE" -> slots.findByOccupiedOrderBySlotNumberAsc(false);
            case "OCCUPIED" -> slots.findByOccupiedOrderBySlotNumberAsc(true);
            default -> throw new IllegalArgumentException("status must be AVAILABLE or OCCUPIED");
        };
    }

    @Transactional
    public ParkingSlot createSlot(SlotRequest r) {
        String number = r.slotNumber().trim().toUpperCase();
        if (slots.existsBySlotNumberIgnoreCase(number)) throw ApiException.conflict("Slot " + number + " already exists");
        ParkingSlot s = new ParkingSlot();
        s.setSlotNumber(number); s.setSlotType(r.slotType());
        return slots.save(s);
    }

    @Transactional
    public ParkingSlot updateSlot(Long id, SlotRequest r) {
        ParkingSlot s = slot(id);
        String number = r.slotNumber().trim().toUpperCase();
        if (slots.existsBySlotNumberIgnoreCaseAndIdNot(number, id)) throw ApiException.conflict("Slot " + number + " already exists");
        if (s.isOccupied() && s.getSlotType() != r.slotType()) throw ApiException.conflict("Cannot change the type of an occupied slot");
        s.setSlotNumber(number); s.setSlotType(r.slotType());
        return s;
    }

    @Transactional
    public void deleteSlot(Long id) {
        ParkingSlot s = slot(id);
        if (s.isOccupied()) throw ApiException.conflict("Slot " + s.getSlotNumber() + " is occupied");
        if (records.existsBySlotId(id)) throw ApiException.conflict("Slot " + s.getSlotNumber() + " has parking history and cannot be deleted");
        slots.delete(s);
    }

    @Transactional
    public ParkingRecord entry(EntryRequest r) {
        String plate = VehicleNumber.normalize(r.vehicleNumber());
        ParkingSlot s = slot(r.slotId());
        if (s.isOccupied()) throw ApiException.conflict("Slot " + s.getSlotNumber() + " is already occupied");
        if (records.findFirstByVehicleNumberAndExitTimeIsNull(plate).isPresent())
            throw ApiException.conflict("Vehicle " + plate + " is already parked");
        ParkingRecord rec = new ParkingRecord();
        rec.setSlot(s); rec.setVehicleNumber(plate); rec.setEntryTime(LocalDateTime.now());
        s.setOccupied(true);
        return records.save(rec);
    }

    @Transactional
    public ParkingRecord exit(ExitRequest r) {
        String plate = VehicleNumber.normalize(r.vehicleNumber());
        ParkingRecord rec = records.findFirstByVehicleNumberAndExitTimeIsNull(plate)
                .orElseThrow(() -> ApiException.notFound("No parked vehicle with number " + plate));
        LocalDateTime now = LocalDateTime.now();
        rec.setExitTime(now);
        rec.setCharge(ChargeCalculator.calculate(rec.getEntryTime(), now, rates.rateFor(rec.getSlot().getSlotType())));
        rec.getSlot().setOccupied(false);
        return rec;
    }

    @Transactional(readOnly = true)
    public List<ParkingRecord> active() { return records.findByExitTimeIsNullOrderByEntryTimeDesc(); }

    @Transactional(readOnly = true)
    public List<ParkingRecord> history() { return records.findTop50ByExitTimeIsNotNullOrderByExitTimeDesc(); }

    private ParkingSlot slot(Long id) {
        return slots.findById(id).orElseThrow(() -> ApiException.notFound("Parking slot " + id + " not found"));
    }
}
