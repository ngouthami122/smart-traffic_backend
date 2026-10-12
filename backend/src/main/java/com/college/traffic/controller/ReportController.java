package com.college.traffic.controller;

import com.college.traffic.dto.Reports.*;
import com.college.traffic.service.ReportService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) { this.service = service; }

    @GetMapping("/dashboard/summary") public DashboardSummary summary() { return service.summary(); }
    @GetMapping("/reports/challans-by-status") public List<StatusRow> byStatus() { return service.challansByStatus(); }
    @GetMapping("/reports/fine-collection") public FineCollection fines() { return service.fineCollection(); }
    @GetMapping("/reports/parking-occupancy") public ParkingOccupancy occupancy() { return service.parkingOccupancy(); }
}
