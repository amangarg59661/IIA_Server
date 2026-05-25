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
@Table(name = "form_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class FormMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    private Long formId;

    @Column(name = "form_name", nullable = false, unique = true, length = 100)
    private String formName;

    @Column(name = "form_display_name", nullable = false, length = 200)
    private String formDisplayName;

    @Column(name = "form_description", columnDefinition = "TEXT")
    private String formDescription;

    @Column(name = "module_name", length = 100)
    private String moduleName;

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
