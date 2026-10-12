package com.college.traffic.service;

import com.college.traffic.dto.Reports.*;
import com.college.traffic.model.*;
import com.college.traffic.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read-only numbers for the dashboard and the Reports page. */
@Service
@Transactional(readOnly = true)
public class ReportService {
    private final ChallanRepository challans;
    private final ParkingSlotRepository slots;
    private final ParkingRecordRepository records;

    public ReportService(ChallanRepository challans, ParkingSlotRepository slots, ParkingRecordRepository records) {
        this.challans = challans; this.slots = slots; this.records = records;
    }

    public DashboardSummary summary() {
        long total = slots.count(), busy = slots.countByOccupied(true);
        return new DashboardSummary(challans.count(), challans.countByPaymentStatus(PaymentStatus.PENDING),
                challans.countByPaymentStatus(PaymentStatus.PAID), challans.sumByStatus(PaymentStatus.PENDING),
                challans.sumByStatus(PaymentStatus.PAID), total, total - busy, busy);
    }

    public List<StatusRow> challansByStatus() {
        List<StatusRow> rows = new ArrayList<>();
        for (PaymentStatus s : PaymentStatus.values())
            rows.add(new StatusRow(s, challans.countByPaymentStatus(s), challans.sumByStatus(s)));
        return rows;
    }

    public FineCollection fineCollection() {
        BigDecimal collected = challans.sumByStatus(PaymentStatus.PAID), pending = challans.sumByStatus(PaymentStatus.PENDING);
        BigDecimal total = collected.add(pending);
        double rate = total.signum() == 0 ? 0 : collected.multiply(BigDecimal.valueOf(100)).divide(total, 1, RoundingMode.HALF_UP).doubleValue();
        // Last 6 months, oldest first, grouped by violation date.
        Map<YearMonth, BigDecimal[]> months = new LinkedHashMap<>();
        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) months.put(now.minusMonths(i), new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
        for (Challan c : challans.findAll()) {
            BigDecimal[] sums = months.get(YearMonth.from(c.getViolationDate()));
            if (sums == null) continue;
            int idx = c.getPaymentStatus() == PaymentStatus.PAID ? 0 : 1;
            sums[idx] = sums[idx].add(c.getFineAmount());
        }
        List<MonthRow> byMonth = new ArrayList<>();
        months.forEach((m, v) -> byMonth.add(new MonthRow(m.toString(), v[0], v[1])));
        List<TypeCount> byType = new ArrayList<>();
        for (Object[] row : challans.countByViolation()) byType.add(new TypeCount((ViolationType) row[0], (Long) row[1]));
        return new FineCollection(total, collected, pending, rate, byMonth, byType);
    }

    public ParkingOccupancy parkingOccupancy() {
        List<ParkingSlot> all = slots.findAll();
        long busy = all.stream().filter(ParkingSlot::isOccupied).count();
        List<SlotTypeRow> byType = new ArrayList<>();
        for (SlotType t : SlotType.values()) {
            long n = all.stream().filter(s -> s.getSlotType() == t).count();
            long o = all.stream().filter(s -> s.getSlotType() == t && s.isOccupied()).count();
            byType.add(new SlotTypeRow(t, n, o));
        }
        double pct = all.isEmpty() ? 0 : Math.round(busy * 1000.0 / all.size()) / 10.0;
        return new ParkingOccupancy(all.size(), busy, all.size() - busy, pct, records.totalRevenue(), byType);
    }
}
