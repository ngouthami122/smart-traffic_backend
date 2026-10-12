package com.college.traffic.dto;

import com.college.traffic.model.*;
import com.college.traffic.util.VehicleNumber;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Request bodies. Validation messages are shown to the user by the React forms. */
public final class Requests {
    private Requests() { }

    public record ChallanRequest(
            @NotBlank(message = "Vehicle number is required")
            @Pattern(regexp = VehicleNumber.INPUT_REGEX, message = "Use a format like TS09EA1234") String vehicleNumber,
            @NotNull(message = "Choose a violation type") ViolationType violationType,
            @NotNull(message = "Violation date is required") @PastOrPresent(message = "Date cannot be in the future") LocalDate violationDate,
            @NotNull(message = "Fine amount is required") @DecimalMin(value = "1", message = "Fine must be at least 1")
            @Digits(integer = 8, fraction = 2, message = "Fine allows up to 2 decimals") BigDecimal fineAmount,
            PaymentStatus paymentStatus) { }

    public record SlotRequest(
            @NotBlank(message = "Slot number is required") @Size(max = 20, message = "Max 20 characters") String slotNumber,
            @NotNull(message = "Choose a slot type") SlotType slotType) { }

    public record EntryRequest(
            @NotBlank(message = "Vehicle number is required")
            @Pattern(regexp = VehicleNumber.INPUT_REGEX, message = "Use a format like TS09EA1234") String vehicleNumber,
            @NotNull(message = "Choose a slot") Long slotId) { }

    public record ExitRequest(
            @NotBlank(message = "Vehicle number is required")
            @Pattern(regexp = VehicleNumber.INPUT_REGEX, message = "Use a format like TS09EA1234") String vehicleNumber) { }
}
