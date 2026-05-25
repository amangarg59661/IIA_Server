package com.astro.entity.InventoryModule;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "igp_detail")
@Data
@EntityListeners(AuditingEntityListener.class)
public class IgpDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    @Column(name = "igp_process_id")
    private String igpProcessId;

    @Column(name = "igp_sub_process_id")
    private Integer igpSubProcessId;

// @Column(name = "ogp_process_id")
// private String ogpProcessId;

    @Column(name = "ogp_sub_process_id")
    private Integer ogpSubProcessId;

    // @Column(name = "issue_note_id")
    // private Integer issueNoteId;

    @Column(name = "asset_id")
    private Integer assetId;

    @Column(name = "locator_id")
    private Integer locatorId;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}