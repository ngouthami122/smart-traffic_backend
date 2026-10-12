package com.college.traffic.config;

import com.college.traffic.model.*;
import com.college.traffic.repository.*;
import com.college.traffic.service.ChargeCalculator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Fills an EMPTY database with demo data (set SEED_DEMO_DATA=false to turn this off). */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {
    private final ChallanRepository challans;
    private final ParkingSlotRepository slots;
    private final ParkingRecordRepository records;

    public DataSeeder(ChallanRepository c, ParkingSlotRepository s, ParkingRecordRepository r) { challans = c; slots = s; records = r; }

    @Override
    public void run(String... args) {
        if (challans.count() > 0 || slots.count() > 0) return;
        Object[][] rows = {
            {"TS09EA1234", ViolationType.NO_HELMET, 3, 1000, false}, {"MH12AB4521", ViolationType.SIGNAL_JUMPING, 8, 5000, false},
            {"KA01MJ7788", ViolationType.OVER_SPEEDING, 14, 2000, true}, {"DL08CAF5030", ViolationType.WRONG_PARKING, 20, 500, true},
            {"TN22BC9012", ViolationType.NO_SEATBELT, 33, 1000, true}, {"GJ05CD3344", ViolationType.MOBILE_USAGE, 41, 5000, false},
            {"TS09EA1234", ViolationType.TRIPLE_RIDING, 52, 1000, true}, {"UP16XY9090", ViolationType.NO_HELMET, 63, 1000, true},
            {"RJ14QW1122", ViolationType.OVER_SPEEDING, 75, 2000, true}, {"AP39TU5566", ViolationType.WRONG_PARKING, 88, 500, false},
            {"KL07RT5566", ViolationType.SIGNAL_JUMPING, 97, 5000, true}, {"MH12AB4521", ViolationType.NO_HELMET, 110, 1000, true},
            {"HR26DK8337", ViolationType.MOBILE_USAGE, 122, 5000, true}, {"TS10FB7788", ViolationType.NO_SEATBELT, 138, 1000, true},
            {"KA01MJ7788", ViolationType.WRONG_PARKING, 150, 500, true}};
        for (Object[] r : rows) {
            Challan c = new Challan();
            c.setVehicleNumber((String) r[0]); c.setViolationType((ViolationType) r[1]);
            c.setViolationDate(LocalDate.now().minusDays((int) r[2])); c.setFineAmount(BigDecimal.valueOf((int) r[3]));
            boolean paid = (boolean) r[4];
            c.setPaymentStatus(paid ? PaymentStatus.PAID : PaymentStatus.PENDING);
            if (paid) { LocalDate d = c.getViolationDate().plusDays(5); c.setPaidDate(d.isAfter(LocalDate.now()) ? LocalDate.now() : d); }
            challans.save(c);
        }
        String[] nums = {"A1", "A2", "A3", "A4", "A5", "A6", "B1", "B2", "B3", "B4", "E1", "E2"};
        SlotType[] types = {SlotType.CAR, SlotType.CAR, SlotType.CAR, SlotType.CAR, SlotType.CAR, SlotType.CAR,
                SlotType.BIKE, SlotType.BIKE, SlotType.BIKE, SlotType.BIKE, SlotType.EV, SlotType.EV};
        ParkingSlot[] saved = new ParkingSlot[nums.length];
        for (int i = 0; i < nums.length; i++) {
            ParkingSlot s = new ParkingSlot(); s.setSlotNumber(nums[i]); s.setSlotType(types[i]); saved[i] = slots.save(s);
        }
        park(saved[0], "TS09EA1234", 125); park(saved[2], "MH12AB4521", 45); park(saved[7], "KA01MJ7788", 190);
        BigDecimal[] rate = {BigDecimal.valueOf(30), BigDecimal.valueOf(10), BigDecimal.valueOf(40)};
        // {days ago, plate, slot index, rate index (0 car, 1 bike, 2 ev), minutes parked}
        Object[][] past = {{1, "DL08CAF5030", 1, 0, 95}, {3, "TN22BC9012", 6, 1, 140}, {5, "UP16XY9090", 3, 0, 400}, {9, "RJ14QW1122", 10, 2, 70}, {12, "HR26DK8337", 8, 1, 55}};
        for (Object[] p : past) {
            ParkingRecord rec = new ParkingRecord();
            rec.setSlot(saved[(int) p[2]]); rec.setVehicleNumber((String) p[1]);
            LocalDateTime out = LocalDateTime.now().minusDays((int) p[0]);
            rec.setEntryTime(out.minusMinutes((int) p[4])); rec.setExitTime(out);
            rec.setCharge(ChargeCalculator.calculate(rec.getEntryTime(), out, rate[(int) p[3]]));
            records.save(rec);
        }
    }

    private void park(ParkingSlot slot, String plate, int minutesAgo) {
        ParkingRecord rec = new ParkingRecord();
        rec.setSlot(slot); rec.setVehicleNumber(plate); rec.setEntryTime(LocalDateTime.now().minusMinutes(minutesAgo));
        slot.setOccupied(true); slots.save(slot); records.save(rec);
    }
}
