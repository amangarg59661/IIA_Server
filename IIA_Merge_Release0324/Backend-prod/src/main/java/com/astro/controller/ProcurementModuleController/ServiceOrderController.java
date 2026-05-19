package com.astro.controller.ProcurementModuleController;


import com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto.ServiceOrderRequestDTO;
import com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto.ServiceOrderResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto.soWithTenderAndIndentResponseDTO;
import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.entity.UserMaster;
import com.astro.service.ServiceOrderService;
import com.astro.service.UserService;
import com.astro.service.WorkflowService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service-orders")
public class ServiceOrderController {


    @Autowired
    private ServiceOrderService serviceOrder;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private UserService userService;
    @PostMapping
    public ResponseEntity<Object> createServiceOrder(@RequestBody ServiceOrderRequestDTO requestDTO) {
        ServiceOrderResponseDTO responseDTO = serviceOrder.createServiceOrder(requestDTO);
        // Initiateing the workflow after saving the indent
        String requestId = responseDTO.getSoId();
        String workflowName = "SO Workflow";
     //   String createdBy = responseDTO.getCreatedBy();
      //  Optional<UserMaster> userMaster = userService.getUserMasterByCreatedBy(createdBy);
       // Integer userId = userMaster.get().getUserId();

        Integer userId = requestDTO.getCreatedBy();

        // Call initiateWorkflow API
     //   WorkflowTransitionDto workflowTransitionDto = workflowService.initiateWorkflow(requestId, workflowName, userId);
        // Call initiateWorkflow API
        WorkflowTransitionDto workflowTransitionDto = workflowService.initiateWorkflow(requestId, workflowName, userId);

        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Object> updateServiceOrder(@RequestParam String soId,
                                                                  @RequestBody ServiceOrderRequestDTO requestDTO) {
        ServiceOrderResponseDTO responseDTO = serviceOrder.updateServiceOrder(soId, requestDTO);
          // Re-initiate workflow for new version
    workflowService.initiateWorkflow(
            responseDTO.getSoId(),   // new versioned ID e.g. SO1001/2
            "SO Workflow",
            requestDTO.getCreatedBy()
    );
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<Object> getAllServiceOrders() {
        List<ServiceOrderResponseDTO> responseDTOList = serviceOrder.getAllServiceOrders();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTOList), HttpStatus.OK);
    }

    @GetMapping("/byId")
    public ResponseEntity<Object> getServiceOrderById(@RequestParam String soId) {
        soWithTenderAndIndentResponseDTO responseDTO = serviceOrder.getServiceOrderById(soId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(responseDTO), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteServiceOrder(@RequestParam String soId) {
        serviceOrder.deleteServiceOrder(soId);
        return ResponseEntity.ok("Service Order deleted successfully. Id:"+" " +soId);
    }
    @GetMapping("/version-history")
public ResponseEntity<Object> getSoVersionHistory(@RequestParam String soId) {
    List<ServiceOrderResponseDTO> history = serviceOrder.getSoVersionHistory(soId);
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(history), HttpStatus.OK);
}


}
