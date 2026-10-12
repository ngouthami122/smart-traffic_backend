package com.college.traffic.service;

import com.college.traffic.dto.Requests.ChallanRequest;
import com.college.traffic.exception.ApiException;
import com.college.traffic.model.*;
import com.college.traffic.repository.ChallanRepository;
import com.college.traffic.util.VehicleNumber;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for challans: CRUD, search and payment-date handling. */
@Service
public class ChallanService {
    private final ChallanRepository repo;

    public ChallanService(ChallanRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<Challan> search(String search, PaymentStatus status, ViolationType type) {
        Specification<Challan> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            String q = VehicleNumber.clean(search);
            if (!q.isEmpty()) p.add(cb.like(root.<String>get("vehicleNumber"), "%" + q + "%"));
            if (status != null) p.add(cb.equal(root.get("paymentStatus"), status));
            if (type != null) p.add(cb.equal(root.get("violationType"), type));
            query.orderBy(cb.desc(root.get("violationDate")), cb.desc(root.get("id")));
            return cb.and(p.toArray(new Predicate[0]));
        };
        return repo.findAll(spec);
    }

    @Transactional(readOnly = true)
    public Challan get(Long id) {
        return repo.findById(id).orElseThrow(() -> ApiException.notFound("Challan " + id + " not found"));
    }

    @Transactional
    public Challan create(ChallanRequest r) { return repo.save(apply(new Challan(), r)); }

    @Transactional
    public Challan update(Long id, ChallanRequest r) { return apply(get(id), r); }

    @Transactional
    public void delete(Long id) { repo.delete(get(id)); }

    private Challan apply(Challan c, ChallanRequest r) {
        c.setVehicleNumber(VehicleNumber.normalize(r.vehicleNumber()));
        c.setViolationType(r.violationType());
        c.setViolationDate(r.violationDate());
        c.setFineAmount(r.fineAmount());
        PaymentStatus st = r.paymentStatus() == null ? PaymentStatus.PENDING : r.paymentStatus();
        c.setPaymentStatus(st);
        if (st == PaymentStatus.PAID) { if (c.getPaidDate() == null) c.setPaidDate(LocalDate.now()); }
        else c.setPaidDate(null);
        return c;
    }
}
