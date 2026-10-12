package com.college.traffic.repository;

import com.college.traffic.model.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ChallanRepository extends JpaRepository<Challan, Long>, JpaSpecificationExecutor<Challan> {
    long countByPaymentStatus(PaymentStatus status);

    @Query("select coalesce(sum(c.fineAmount), 0) from Challan c where c.paymentStatus = :status")
    BigDecimal sumByStatus(@Param("status") PaymentStatus status);

    @Query("select c.violationType, count(c) from Challan c group by c.violationType order by count(c) desc")
    List<Object[]> countByViolation();
}
