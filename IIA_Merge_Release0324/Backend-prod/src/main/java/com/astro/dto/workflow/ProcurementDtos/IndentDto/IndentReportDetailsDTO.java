// package com.astro.dto.workflow.ProcurementDtos.IndentDto;


// import lombok.Data;
// import java.util.Date;

// @Data
// public class IndentReportDetailsDTO {

//     private String indentId;
//     private Date approvedDate;
//     private String assignedTo;
//     private String tenderRequest;
//     private String modeOfTendering;
//     private String correspondingPoSo;
//     private String statusOfPoSo;
//     private Date submittedDate;
//     private String pendingApprovalWithAndPendingFrom;
//     private Date poSoApprovedDate;
//     private String material;
//     private String materialCategory;
//     private String materialSubCategory;
//     private String vendorName;
//     private String indentorName;
//     private Double valueOfIndent;
//     private Double valueOfPo;
//     private String gemContractFileName;
//     private String Project;
//   //  private String grinNo;
//     private String invoiceNo;
//     private String gissNo;
//     private Double valuePendingToBePaid;
//     private String currentStageOfIndent;
//     private String shortClosedAndCancelledThroughAmendment;
//     private String reasonForShortClosureAndCancellation;
//     private Date indentDate;
// private String tenderEvaluationStatus;
// private String grinNo;

//     // public IndentReportDetailsDTO(
//     //         String indentId, Date approvedDate, String assignedTo, String tenderRequest, String modeOfTendering,
//     //         String correspondingPoSo, String statusOfPoSo, Date submittedDate, String pendingApprovalWithAndPendingFrom,
//     //         Date poSoApprovedDate, String material, String materialCategory, String materialSubCategory,
//     //         String vendorName, String indentorName, Double valueOfIndent, Double valueOfPo,String gemContractFileName, String Project,
//     //         String invoiceNo, String gissNo, Double valuePendingToBePaid, String currentStageOfIndent,
//     //         String shortClosedAndCancelledThroughAmendment, String reasonForShortClosureAndCancellation) {
//     //     // Assign fields here
//     //    // Double valueOfPo, String grinNo,
//     //     this.indentId = indentId;
//     //     this.approvedDate = approvedDate;
//     //     this.assignedTo = assignedTo;
//     //     this.tenderRequest = tenderRequest;
//     //     this.modeOfTendering = modeOfTendering;
//     //     this.correspondingPoSo = correspondingPoSo;
//     //     this.statusOfPoSo = statusOfPoSo;
//     //     this.submittedDate = submittedDate;
//     //     this.pendingApprovalWithAndPendingFrom = pendingApprovalWithAndPendingFrom;
//     //     this.poSoApprovedDate = poSoApprovedDate;
//     //     this.material = material;
//     //     this.materialCategory = materialCategory;
//     //     this.materialSubCategory = materialSubCategory;
//     //     this.vendorName = vendorName;
//     //     this.indentorName = indentorName;
//     //     this.valueOfIndent = valueOfIndent;
//     //     this.valueOfPo = valueOfPo;
//     //     this.gemContractFileName= gemContractFileName;
//     //     this.Project = Project;
//     //    // this.grinNo = grinNo;
//     //     this.invoiceNo = invoiceNo;
//     //     this.gissNo = gissNo;
//     //     this.valuePendingToBePaid = valuePendingToBePaid;
//     //     this.currentStageOfIndent = currentStageOfIndent;
//     //     this.shortClosedAndCancelledThroughAmendment = shortClosedAndCancelledThroughAmendment;
//     //     this.reasonForShortClosureAndCancellation = reasonForShortClosureAndCancellation;

//     // }

// }



package com.astro.dto.workflow.ProcurementDtos.IndentDto;


import lombok.Data;
import java.util.Date;

@Data
public class IndentReportDetailsDTO {

    private String indentId;
    private Date approvedDate;
    private String assignedTo;
    private String tenderRequest;
    private String modeOfTendering;
    private String correspondingPoSo;
    private String statusOfPoSo;
    private Date submittedDate;
    private String pendingApprovalWithAndPendingFrom;
    private Date poSoApprovedDate;
    private String material;
    private String materialCategory;
    private String materialSubCategory;
    private String vendorName;
    private String indentorName;
    private Double valueOfIndent;
    private Double valueOfPo;
    private String gemContractFileName;
    private String Project;
    private String invoiceNo;
    private String gissNo;
    private Double valuePendingToBePaid;
    private String currentStageOfIndent;
    private String shortClosedAndCancelledThroughAmendment;
    private String reasonForShortClosureAndCancellation;
    private Date indentDate;
    private String tenderEvaluationStatus;
    private String grinNo;

    public IndentReportDetailsDTO(
            String indentId, Date approvedDate, String assignedTo, String tenderRequest, String modeOfTendering,
            String correspondingPoSo, String statusOfPoSo, Date submittedDate, String pendingApprovalWithAndPendingFrom,
            Date poSoApprovedDate, String material, String materialCategory, String materialSubCategory,
            String vendorName, String indentorName, Double valueOfIndent, Double valueOfPo,String gemContractFileName, String Project,
            String invoiceNo, String gissNo, Double valuePendingToBePaid, String currentStageOfIndent,
            String shortClosedAndCancelledThroughAmendment, String reasonForShortClosureAndCancellation,
            Date indentDate, String tenderEvaluationStatus, String grinNo) {
        // Assign fields here
        this.indentId = indentId;
        this.approvedDate = approvedDate;
        this.assignedTo = assignedTo;
        this.tenderRequest = tenderRequest;
        this.modeOfTendering = modeOfTendering;
        this.correspondingPoSo = correspondingPoSo;
        this.statusOfPoSo = statusOfPoSo;
        this.submittedDate = submittedDate;
        this.pendingApprovalWithAndPendingFrom = pendingApprovalWithAndPendingFrom;
        this.poSoApprovedDate = poSoApprovedDate;
        this.material = material;
        this.materialCategory = materialCategory;
        this.materialSubCategory = materialSubCategory;
        this.vendorName = vendorName;
        this.indentorName = indentorName;
        this.valueOfIndent = valueOfIndent;
        this.valueOfPo = valueOfPo;
        this.gemContractFileName= gemContractFileName;
        this.Project = Project;
        this.invoiceNo = invoiceNo;
        this.gissNo = gissNo;
        this.valuePendingToBePaid = valuePendingToBePaid;
        this.currentStageOfIndent = currentStageOfIndent;
        this.shortClosedAndCancelledThroughAmendment = shortClosedAndCancelledThroughAmendment;
        this.reasonForShortClosureAndCancellation = reasonForShortClosureAndCancellation;
        this.indentDate = indentDate;
        this.tenderEvaluationStatus = tenderEvaluationStatus;
        this.grinNo = grinNo;

    }

}