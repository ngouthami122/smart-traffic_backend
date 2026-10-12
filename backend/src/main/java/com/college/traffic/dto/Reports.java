package com.college.traffic.dto;

import com.college.traffic.model.*;
import java.math.BigDecimal;
import java.util.List;

/** Response shapes for the dashboard and report endpoints. */
public final class Reports {
    private Reports() { }

    public record DashboardSummary(long totalChallans, long pendingChallans, long paidChallans, BigDecimal pendingFines,
                                   BigDecimal collectedFines, long totalSlots, long availableSlots, long occupiedSlots) { }
    public record StatusRow(PaymentStatus status, long count, BigDecimal amount) { }
    public record MonthRow(String month, BigDecimal collected, BigDecimal pending) { }
    public record TypeCount(ViolationType type, long count) { }
    public record FineCollection(BigDecimal totalIssued, BigDecimal collected, BigDecimal pending, double collectionRatePercent,
                                 List<MonthRow> byMonth, List<TypeCount> byViolation) { }
    public record SlotTypeRow(SlotType type, long total, long occupied) { }
    public record ParkingOccupancy(long totalSlots, long occupied, long available, double occupancyPercent,
                                   BigDecimal parkingRevenue, List<SlotTypeRow> bySlotType) { }
}
