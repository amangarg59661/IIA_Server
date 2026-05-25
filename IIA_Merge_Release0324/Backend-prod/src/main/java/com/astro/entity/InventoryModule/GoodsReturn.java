package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name = "goods_return")
@EntityListeners(AuditingEntityListener.class)
public class GoodsReturn {

    @Id
    @Column(name = "goods_return_id")
    private String goodsReturnId;

    @Column(name = "goods_return_note_no")
    private String goodsReturnNoteNo;

    @Column(name = "rejected_quantity")
    private Integer rejectedQuantity;

    @Column(name = "return_quantity")
    private Integer returnQuantity;

    @Column(name = "type_of_return")
    private String typeOfReturn;

    @Column(name = "reason_of_return")
    private String reasonOfReturn;

    @LastModifiedBy
    private String updatedBy;
    @CreatedBy
    private String createdBy;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

}
