package com.college.traffic.controller;

import com.college.traffic.dto.Requests.ChallanRequest;
import com.college.traffic.model.*;
import com.college.traffic.service.ChallanService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challans")
public class ChallanController {
    private final ChallanService service;

    public ChallanController(ChallanService service) { this.service = service; }

    @GetMapping
    public List<Challan> list(@RequestParam(required = false) String search,
                              @RequestParam(required = false) PaymentStatus status,
                              @RequestParam(required = false) ViolationType type) {
        return service.search(search, status, type);
    }

    @GetMapping("/{id}")
    public Challan get(@PathVariable Long id) { return service.get(id); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Challan create(@Valid @RequestBody ChallanRequest r) { return service.create(r); }

    @PutMapping("/{id}")
    public Challan update(@PathVariable Long id, @Valid @RequestBody ChallanRequest r) { return service.update(id, r); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
