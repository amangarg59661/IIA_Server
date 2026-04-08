package com.astro.controller;

import com.astro.dto.workflow.*;
import com.astro.dto.workflow.ProcurementDtos.approvedTenderIdWithTitle;
import com.astro.entity.VendorMaster;
import com.astro.service.VendorMasterService;
import com.astro.util.ResponseBuilder;
// modifed by Aman 
import com.astro.util.APIResponse;
import java.util.LinkedHashMap;
// end
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor-master")
public class VendorMasterController {

    @Autowired
    private VendorMasterService vendorMasterService;


    @PostMapping
    public ResponseEntity<Object> createVendorMaster(@RequestBody VendorMasterRequestDto requestDTO) {
       VendorMasterResponseDto material = vendorMasterService.createVendorMaster(requestDTO);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(material), HttpStatus.OK);
    }

    @PutMapping("/{vendorId}")
    public ResponseEntity<Object> updateVendorMaster(@PathVariable String vendorId,
                                                       @RequestBody VendorMasterRequestDto requestDTO) {
        VendorMasterResponseDto response = vendorMasterService.updateVendorMaster(vendorId, requestDTO);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<Object> getAllVendorMaster() {
        List<VendorMasterResponseDto> response = vendorMasterService.getAllVendorMasters();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<Object> getVendorMasterById(@PathVariable String vendorId) {
        VendorMasterResponseDto responseDTO = vendorMasterService.getVendorMasterById(vendorId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }


    @DeleteMapping("/{vendorId}")
    public ResponseEntity<String> deleteVendorMaster(@PathVariable String vendorId) {
        vendorMasterService.deleteVendorMaster(vendorId);
        return ResponseEntity.ok("material master deleted successfully. materialCode:"+" " +vendorId);
    }
/*
    @GetMapping("/not-approved")
    public ResponseEntity<Object> getAllNotApprovedVendors() {
        List<VendorMasterResponseDto> response = vendorMasterService.getAllNotApprovedVendors();
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(response));
    }

 */

    @GetMapping("/approvedVendorData/{tenderId}")
    public ResponseEntity<Object> getAllVendorData(@PathVariable String tenderId) {
        RegisteredVendorsDataDto response = vendorMasterService.getVendorPurchaseOrders(tenderId);
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(response));
    }
    // Modified by Aman
    @GetMapping("/approvedtenderIDs/{vendorId}")
public ResponseEntity<Object> getAllTenderIdsByVendor(@PathVariable String vendorId) {

    List<approvedTenderIdWithTitle> tenders = vendorMasterService.getTenderIds(vendorId);
    VendorMaster vendor = vendorMasterService.getVendorByVendorId(vendorId);

    APIResponse builtResponse = ResponseBuilder.getSuccessResponse(tenders);

    Map<String, Object> finalResponse = new LinkedHashMap<>();
    finalResponse.put("responseStatus", builtResponse.getResponseStatus());
    finalResponse.put("vendorData", Map.of(
        "vendorId", vendor.getVendorId(),
        "vendorName", vendor.getVendorName(),
        "primaryBusiness", vendor.getPrimaryBusiness()
    ));
    finalResponse.put("responseData", tenders);

    return ResponseEntity.ok(finalResponse);
}
    // @GetMapping("/approvedtenderIDs/{vendorId}")
    // public ResponseEntity<Object> getAllTenderIdsByVendor(@PathVariable String vendorId) {
    //     List<approvedTenderIdWithTitle> response = vendorMasterService.getTenderIds(vendorId);
    //     return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(response));
    // }
// End
    @GetMapping("/vendorIdVendorName")
    public ResponseEntity<Object> getAllvendorIdVendorNames() {
        List<VendorIdNameDTO> response = vendorMasterService.getAllVendorIdAndName();
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(response));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<Object> getVendorMasterByVendorId(@PathVariable String vendorId) {
        VendorMaster responseDTO = vendorMasterService.getVendorByVendorId(vendorId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }

    @PutMapping("/update/{vendorId}")
    public ResponseEntity<Object> updateVendor(@PathVariable String vendorId,
                                                     @RequestBody VendorMasterUpdateDto requestDTO) {
        VendorMaster response = vendorMasterService.updateVendor(vendorId, requestDTO);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

}
