package com.college.traffic.controller;

import com.college.traffic.dto.Requests.*;
import com.college.traffic.model.*;
import com.college.traffic.service.ParkingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ParkingController {
    private final ParkingService service;

    public ParkingController(ParkingService service) { this.service = service; }

    @GetMapping("/slots")
    public List<ParkingSlot> slots(@RequestParam(required = false) String status) { return service.listSlots(status); }

    @PostMapping("/slots") @ResponseStatus(HttpStatus.CREATED)
    public ParkingSlot createSlot(@Valid @RequestBody SlotRequest r) { return service.createSlot(r); }

    @PutMapping("/slots/{id}")
    public ParkingSlot updateSlot(@PathVariable Long id, @Valid @RequestBody SlotRequest r) { return service.updateSlot(id, r); }

    @DeleteMapping("/slots/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlot(@PathVariable Long id) { service.deleteSlot(id); }

    @PostMapping("/parking/entry") @ResponseStatus(HttpStatus.CREATED)
    public ParkingRecord entry(@Valid @RequestBody EntryRequest r) { return service.entry(r); }

    @PostMapping("/parking/exit")
    public ParkingRecord exit(@Valid @RequestBody ExitRequest r) { return service.exit(r); }

    @GetMapping("/parking/active")
    public List<ParkingRecord> active() { return service.active(); }

    @GetMapping("/parking/history")
    public List<ParkingRecord> history() { return service.history(); }
}
