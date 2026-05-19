package com.astro.entity.AdminPanel;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_ledger")
@Data
public class BudgetLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_code", nullable = false)
    private String budgetCode;

    @Column(name = "reference_id", nullable = false)
    private String referenceId; // e.g. IND1001, IND1001/2

    @Column(name = "reference_type", nullable = false)
    private String referenceType; // "INDENT"

    @Column(name = "hold_amount", precision = 15, scale = 2)
    private BigDecimal holdAmount = BigDecimal.ZERO;

    @Column(name = "spent_amount", precision = 15, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(name = "status")
    private String status = "ACTIVE_HOLD"; // "ACTIVE_HOLD" | "RELEASED"

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}