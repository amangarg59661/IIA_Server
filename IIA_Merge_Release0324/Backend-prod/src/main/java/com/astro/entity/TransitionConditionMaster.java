package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "TRANSITION_CONDITION_MASTER")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TransitionConditionMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONDITIONID")
    private Integer conditionId;

    @Column(name = "WORKFLOWID")
    private Integer workflowId;

    @Column(name = "CONDITIONKEY")
    private String conditionKey;

    @Column(name = "CONDITIONVALUE")
    private String conditionValue;

    @Column(name = "CREATEDBY")
    @CreatedBy
    private String createdBy;

    @Column(name = "CREATEDDATE")
    private Date createdDate;
}
