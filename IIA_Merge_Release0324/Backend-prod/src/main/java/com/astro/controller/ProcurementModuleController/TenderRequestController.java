package com.astro.controller.ProcurementModuleController;

import com.astro.dto.workflow.ProcurementDtos.*;
import javax.validation.Valid; // added by abhinav
import com.astro.dto.workflow.ProcurementDtos.IndentDto.CancelIndentRequestDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.MaterialDetailsResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.SearchPOIdDto;
import com.astro.constant.WorkflowName; // added by abhinav
import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.service.TenderRequestService;

import com.astro.service.WorkflowService;
import com.astro.util.ResponseBuilder;
import com.astro.util.UtilProcurementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ui.ModelMap;

@RestController
@RequestMapping("/api/tender-requests")
public class TenderRequestController {

    private static final Logger log = LoggerFactory.getLogger(TenderRequestController.class);

    @Autowired
    private TenderRequestService TRService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UtilProcurementService cancelTender;
    @PostMapping
    public ResponseEntity<Object> createTenderRequest(@Valid @RequestBody TenderRequestDto tenderRequestDTO) { // updated by abhinav the method signature to accept DTO instead of individual parameters

        TenderResponseDto created = TRService.createTenderRequest(tenderRequestDTO);

        String requestId = created.getTenderId(); // Useing the indent ID as the request ID
        // String workflowName = "Tender Approver Workflow";    // Need to change the workflow name

        // String workflowName = WorkflowName.TENDER_APPROVER.getValue();  // Use enum for workflow name
        String workflowName = WorkflowName.TENDER_APPROVER.getKey(); // updated by abhinav  to match to db

        Integer userId = created.getCreatedBy();
        //initiateing Workflow API
        WorkflowTransitionDto workflowTransitionDto = workflowService.initiateWorkflow(requestId, workflowName, userId);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(created), HttpStatus.OK);
    }


    // @PutMapping(value = "/{tenderId}")
    // public ResponseEntity<Object> updateTenderRequest(@PathVariable String tenderId, @RequestBody TenderRequestDto tenderRequestDTO) {// Set files in DTO if provided

    //     // Call service to update tender request
    //     TenderResponseDto updated = TRService.updateTenderRequest(tenderId, tenderRequestDTO);

    //     // Return success response
    //     return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
    // }
    // With:
@PutMapping
public ResponseEntity<Object> updateTenderRequest(@RequestParam String tenderId, @RequestBody TenderRequestDto tenderRequestDTO) {
    TenderResponseDto updated = TRService.updateTenderRequest(tenderId, tenderRequestDTO);

    // Re-initiate workflow for new version
    workflowService.initiateWorkflow(
            updated.getTenderId(),
            WorkflowName.TENDER_APPROVER.getKey(),
            updated.getCreatedBy()
    );

    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
}
// @GetMapping("/version-history/{tenderId}")
// public ResponseEntity<Object> getTenderVersionHistory(@PathVariable String tenderId) {
//     List<TenderResponseDto> history = TRService.getTenderVersionHistory(tenderId);
//     return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(history), HttpStatus.OK);
// }
    @PutMapping(value = "/update")
    public ResponseEntity<Object> updateTender(@RequestParam String tenderId, @RequestBody tenderUpdateDto tenderRequestDTO) {// Set files in DTO if provided

        // Call service to update tender request
        TenderResponseDto updated = TRService.updateTender(tenderId, tenderRequestDTO);

        // Return success response
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAllTenderRequests() {

        List<TenderResponseDto>  tenderRequest = TRService.getAllTenderRequests();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(tenderRequest), HttpStatus.OK);
    }

    @GetMapping("vendor")
    public ResponseEntity<Object> vendorCheck(@RequestParam String tenderId,@RequestParam String vendorId) {

        VendorQualificationResponseDto status = TRService.vendorCheck(tenderId, vendorId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @GetMapping("/byId")
    public ResponseEntity<Object> getTenderRequestById(@RequestParam String tenderId) {

        TenderWithIndentResponseDTO tenderRequest = TRService.getTenderRequestById(tenderId);


        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(tenderRequest), HttpStatus.OK);
    }
    @GetMapping("/base64Files")
    public ResponseEntity<Object> getTenderDataAndBase64FilesById(@RequestParam String tenderId) throws IOException {

        TenderResponseBase64FilesDto tenderRequest = TRService.getTenderDataWithBase64Files(tenderId);


        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(tenderRequest), HttpStatus.OK);
    }


    @GetMapping("/data")
    public ResponseEntity<Object> getTenderDataById(@RequestParam String tenderId) {

        TenderResponseDto tenderRequest = TRService.getTenderData(tenderId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(tenderRequest), HttpStatus.OK);

    }

    @DeleteMapping("/{tenderId}")
    public ResponseEntity<String> deleteTenderRequest(@PathVariable String tenderId) {
        TRService.deleteTenderRequest(tenderId);
        return ResponseEntity.ok("Tender Request deleted successfully. Id:"+" " +tenderId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchTenderIds(
            @RequestParam String type,
            @RequestParam String value
    ) {
        List<SearchTenderIdDto> result = TRService.searchTenderIds(type, value);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(result), HttpStatus.OK);

    }

    @GetMapping("/approvedTender/TenderEvaluation")
    public ResponseEntity<Object> getAllApprovedTenderIdsWithTitle(
    ) {
        List<ApprovedTenderIdDtos> result = TRService.getApprovedTenderIdsForTenderEvaluation();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(result), HttpStatus.OK);

    }

    @GetMapping("/approvedGemTender/TenderEvaluation")
    public ResponseEntity<Object> getAllApprovedGemTenderIdsWithTitle(
    ) {
        List<ApprovedTenderIdDtos> result = TRService.getApprovedTenderIdsForGemTenderEvaluation();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(result), HttpStatus.OK);

    }


    @PutMapping("tender/cancel")
    public ResponseEntity<?> cancelTender(@RequestBody CancelTenderRequestDto request) {

        String  response = cancelTender.cancelTender(request);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);

    }



}
