package com.college.traffic.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** A traffic challan (fine) issued to a vehicle. */
@Entity
@Table(name = "challans", indexes = @Index(name = "idx_challan_vehicle", columnList = "vehicle_number"))
public class Challan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String vehicleNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private ViolationType violationType;
    @Column(nullable = false)
    private LocalDate violationDate;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fineAmount;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private LocalDate paidDate;

    public Long getId() { return id; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public ViolationType getViolationType() { return violationType; }
    public void setViolationType(ViolationType violationType) { this.violationType = violationType; }
    public LocalDate getViolationDate() { return violationDate; }
    public void setViolationDate(LocalDate violationDate) { this.violationDate = violationDate; }
    public BigDecimal getFineAmount() { return fineAmount; }
    public void setFineAmount(BigDecimal fineAmount) { this.fineAmount = fineAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }
}
