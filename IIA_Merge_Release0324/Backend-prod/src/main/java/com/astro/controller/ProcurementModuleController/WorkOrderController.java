package com.astro.controller.ProcurementModuleController;


import com.astro.dto.workflow.ProcurementDtos.WorkOrderDto.WorkOrderRequestDTO;
import com.astro.dto.workflow.ProcurementDtos.WorkOrderDto.WorkOrderResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.WorkOrderDto.woWithTenderAndIndentResponseDTO;
import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.entity.UserMaster;
import com.astro.service.UserService;
import com.astro.service.WorkOrderService;
import com.astro.service.WorkflowService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrder;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private UserService userService;
    @PostMapping
    public ResponseEntity<Object> createWorkOrder(@RequestBody WorkOrderRequestDTO requestDTO) {
        WorkOrderResponseDTO responseDTO = workOrder.createWorkOrder(requestDTO);
        // Initiateing the workflow after saving the indent
        String requestId = responseDTO.getWoId(); // Useing the indent ID as the request ID
        String workflowName = "WO Workflow";
       // String createdBy = responseDTO.getCreatedBy();
      // Optional<UserMaster> userMaster = userService.getUserMasterByCreatedBy(createdBy);
        //Integer userId = userMaster.get().getUserId();
        String userId = requestDTO.getCreatedBy();

        // Call initiateWorkflow API
        WorkflowTransitionDto workflowTransitionDto = workflowService.initiateWorkflow(requestId, workflowName, userId);

        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }

    @PutMapping("/{woId}")
    public ResponseEntity<Object> updateWorkOrder(@PathVariable String woId,
                                                                      @RequestBody WorkOrderRequestDTO requestDTO) {
        WorkOrderResponseDTO response = workOrder.updateWorkOrder(woId, requestDTO);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<Object> getAllWorkOrders() {
        List<WorkOrderResponseDTO> response = workOrder.getAllWorkOrders();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @GetMapping("/{woId}")
    public ResponseEntity<Object> getWorkOrderById(@PathVariable String woId) {
        woWithTenderAndIndentResponseDTO responseDTO = workOrder.getWorkOrderById(woId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{woId}")
    public ResponseEntity<String> deleteWorkOrder(@PathVariable String woId) {
        workOrder.deleteWorkOrder(woId);
        return ResponseEntity.ok("Work Order deleted successfully. Id:"+" " +woId);
    }


}
