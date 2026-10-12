package com.college.traffic.service;

import com.college.traffic.model.SlotType;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Hourly parking rates (INR). Change them in application.properties. */
@Component
public class ParkingRates {
    @Value("${app.parking.rate.car}") private BigDecimal car;
    @Value("${app.parking.rate.bike}") private BigDecimal bike;
    @Value("${app.parking.rate.ev}") private BigDecimal ev;

    public BigDecimal rateFor(SlotType type) {
        return switch (type) { case CAR -> car; case BIKE -> bike; case EV -> ev; };
    }
}
