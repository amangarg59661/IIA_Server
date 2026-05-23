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
@Table(name = "WORKFLOW_MASTER")
@Data
@EntityListeners(AuditingEntityListener.class)
public class WorkflowMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORKFLOWID")
    private Integer workflowId;

    @Column(name = "WORKFLOWNAME")
    private String workflowName;

    @Column(name = "CREATEDBY")
    @CreatedBy
    private String createdBy;

    @Column(name = "CREATEDDATE")
    private Date createdDate;
}
