package com.astro.entity.AdminPanel;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "designator_master",
       uniqueConstraints = @UniqueConstraint(columnNames = {"form_id", "designator_name"}))
@Data
@EntityListeners(AuditingEntityListener.class)
public class DesignatorMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designator_id")
    private Long designatorId;

    @Column(name = "form_id", nullable = false)
    private Long formId;

    @Column(name = "designator_name", nullable = false, length = 100)
    private String designatorName;

    @Column(name = "designator_display_name", nullable = false, length = 200)
    private String designatorDisplayName;

    @Column(name = "designator_description", columnDefinition = "TEXT")
    private String designatorDescription;

    @Column(name = "data_type", length = 50)
    private String dataType = "STRING"; // STRING, NUMBER, DATE, BOOLEAN

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "created_by", length = 100)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;
}
