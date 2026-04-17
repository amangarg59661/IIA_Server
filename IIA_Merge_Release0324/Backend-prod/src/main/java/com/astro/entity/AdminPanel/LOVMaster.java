package com.astro.entity.AdminPanel;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lov_master",
       uniqueConstraints = @UniqueConstraint(columnNames = {"designator_id", "lov_value"}))
@Data
public class LOVMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lov_id")
    private Long lovId;

    @Column(name = "designator_id", nullable = false)
    private Long designatorId;

    @Column(name = "lov_value", nullable = false, length = 200)
    private String lovValue;

    @Column(name = "lov_display_value", nullable = false, length = 200)
    private String lovDisplayValue;

    @Column(name = "lov_description", columnDefinition = "TEXT")
    private String lovDescription;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // Optional attributes
    @Column(name = "color_code", length = 20)
    private String colorCode;

    @Column(name = "icon_name", length = 50)
    private String iconName;

    // Hierarchical support
    @Column(name = "parent_lov_id")
    private Long parentLovId;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name="locator_location")
    private String locationCode;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
