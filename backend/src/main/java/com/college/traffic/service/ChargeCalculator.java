package com.college.traffic.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/** Parking charge rule: hourly rate x hours parked, rounded UP to a full hour, minimum 1 hour. */
public final class ChargeCalculator {
    private ChargeCalculator() { }

    public static BigDecimal calculate(LocalDateTime entry, LocalDateTime exit, BigDecimal hourlyRate) {
        if (exit.isBefore(entry)) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }
        long seconds = Duration.between(entry, exit).getSeconds();
        long hours = Math.max(1, (seconds + 3599) / 3600);
        return hourlyRate.multiply(BigDecimal.valueOf(hours)).setScale(2);
    }
}
