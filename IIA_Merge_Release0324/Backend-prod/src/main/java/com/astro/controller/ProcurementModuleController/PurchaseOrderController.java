package com.astro.controller.ProcurementModuleController;



import com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.materialHistoryDto;
import com.astro.dto.workflow.ProcurementDtos.PoFormateDto;
import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.*;

import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.dto.workflow.poMaterialHistoryDto;
import com.astro.entity.UserMaster;
import com.astro.service.PurchaseOrderService;
import com.astro.service.UserService;
import com.astro.service.WorkflowService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService poService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private UserService userService;

    // Create a new PO
    @PostMapping
    public ResponseEntity<Object> createPurchaseOrder(@RequestBody @Valid PurchaseOrderRequestDTO purchaseOrderRequestDTO) {
        PurchaseOrderResponseDTO createdPO = poService.createPurchaseOrder(purchaseOrderRequestDTO);
      // Initiateing the workflow after saving the indent
        String requestId = createdPO.getPoId(); // Useing the indent ID as the request ID
        String workflowName = "PO Workflow";
      //  String createdBy = purchaseOrderRequestDTO.getCreatedBy();
      //  Optional<UserMaster> userMaster = userService.getUserMasterByCreatedBy(createdBy);
      //  Integer userId = userMaster.get().getUserId();
        Integer userId = purchaseOrderRequestDTO.getCreatedBy();

        // Call initiateWorkflow API
        WorkflowTransitionDto workflowTransitionDto = workflowService.initiateWorkflow(requestId, workflowName, userId);

        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(createdPO), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Object> updatePurchaseOrder(
            @RequestParam String poId,
            @RequestBody @Valid PurchaseOrderRequestDTO purchaseOrderRequestDTO) {
        PurchaseOrderResponseDTO updatedPO = poService.updatePurchaseOrder(poId, purchaseOrderRequestDTO);

        // added by abhinav new line
        String requestId = updatedPO.getPoId();
        String workflowName = "PO Workflow";
        Integer userId = purchaseOrderRequestDTO.getCreatedBy();

        workflowService.initiateWorkflow(requestId, workflowName, userId);

        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(updatedPO), HttpStatus.OK);
    }
  // Get all POs
    @GetMapping
    public ResponseEntity<Object> getAllPurchaseOrders() {
        List<PurchaseOrderResponseDTO> poList = poService.getAllPurchaseOrders();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(poList), HttpStatus.OK);
    }




    // Get a PO by ID
    @GetMapping("/byId")
    public ResponseEntity<Object> getPurchaseOrderById(@RequestParam String poId)  {
        poWithTenderAndIndentResponseDTO po = poService.getPurchaseOrderById(poId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(po), HttpStatus.OK);
    }

    @GetMapping("/base64Files")
    public ResponseEntity<Object> getPurchaseOrderByIdWithBase64Files(@RequestParam String poId) throws IOException {
        PoWithTenderAndIndentBase64FilesDto po = poService.getPurchaseOrderBase64FilesById(poId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(po), HttpStatus.OK);
    }

    // Delete a PO by ID
    @DeleteMapping
        public ResponseEntity<String> deletePurchaseOrder(@RequestParam String poId) {
        poService.deletePurchaseOrder(poId);
        return ResponseEntity.ok("Purchase Order deleted successfully."+" " +poId);
    }

    @GetMapping("/version-history")
public ResponseEntity<Object> getPoVersionHistory(@RequestParam String poId) {
    List<PurchaseOrderResponseDTO> history = poService.getPoVersionHistory(poId);
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(history), HttpStatus.OK);
}

    @GetMapping("/search")
    public ResponseEntity<Object> searchPoIds(
            @RequestParam String type,
            @RequestParam String value
    ) {
        List<SearchPOIdDto> result = poService.searchPOIds(type, value);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(result), HttpStatus.OK);

    }

    @GetMapping("/materialHistoryPo/{materialCode}")
    public ResponseEntity<Object> getMaterialHistory(@PathVariable String materialCode) {
        List<poMaterialHistoryDto> responseDTO = poService.getLatestPurchaseOrders(materialCode);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }

    @GetMapping("/data/po-internal-format")
    public ResponseEntity<Object> getPoFormatPage(@RequestParam("poId") String poId) throws IOException {
        PoFormateDto poData = poService.getPoFormatDetails(poId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(poData), HttpStatus.OK);

    }



}
