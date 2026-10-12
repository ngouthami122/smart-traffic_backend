package com.college.traffic.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ChargeCalculatorTest {
    private static final LocalDateTime T0 = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
    private static final BigDecimal RATE = BigDecimal.valueOf(30);

    @Test void shortStayIsChargedOneFullHour() { assertEquals(new BigDecimal("30.00"), ChargeCalculator.calculate(T0, T0.plusMinutes(1), RATE)); }
    @Test void zeroMinutesIsStillOneHour() { assertEquals(new BigDecimal("30.00"), ChargeCalculator.calculate(T0, T0, RATE)); }
    @Test void exactlyOneHourIsOneHour() { assertEquals(new BigDecimal("30.00"), ChargeCalculator.calculate(T0, T0.plusHours(1), RATE)); }
    @Test void oneHourOneSecondRoundsUpToTwoHours() { assertEquals(new BigDecimal("60.00"), ChargeCalculator.calculate(T0, T0.plusHours(1).plusSeconds(1), RATE)); }
    @Test void ninetyMinutesIsTwoHours() { assertEquals(new BigDecimal("60.00"), ChargeCalculator.calculate(T0, T0.plusMinutes(90), RATE)); }
    @Test void exitBeforeEntryIsRejected() { assertThrows(IllegalArgumentException.class, () -> ChargeCalculator.calculate(T0, T0.minusMinutes(5), RATE)); }
}
