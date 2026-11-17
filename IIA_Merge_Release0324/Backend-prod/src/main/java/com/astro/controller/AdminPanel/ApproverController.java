package com.astro.controller.AdminPanel;

import com.astro.dto.AdminPanel.ApprovalLimitDTO;
import com.astro.dto.AdminPanel.DepartmentApproverMappingDTO;
import com.astro.dto.AdminPanel.FieldStationApproverDTO;
import com.astro.dto.AdminPanel.WorkflowFullConfigDTO;
import com.astro.entity.AdminPanel.ApproverMaster;
import com.astro.entity.AdminPanel.WorkflowBranchMaster;
import com.astro.repository.AdminPanel.ApproverMasterRepository;
import com.astro.repository.AdminPanel.WorkflowBranchMasterRepository;
import com.astro.service.AdminPanel.ApprovalLimitService;
import com.astro.service.AdminPanel.DepartmentApproverService;
import com.astro.service.AdminPanel.FieldStationApproverService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/approvers")
@CrossOrigin
public class ApproverController {

    @Autowired
    private ApproverMasterRepository approverRepository;

    @Autowired
    private WorkflowBranchMasterRepository branchRepository;

    @Autowired
    private ApprovalLimitService approvalLimitService;

    @Autowired
    private DepartmentApproverService departmentApproverService;

    @Autowired
    private FieldStationApproverService fieldStationApproverService;

    // Get all approvers
    @GetMapping
    public ResponseEntity<Object> getAllApprovers() {
        List<ApproverMaster> approvers = approverRepository.findAll();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(approvers), HttpStatus.OK);
    }

    // Get approver by ID
    @GetMapping("/{approverId}")
    public ResponseEntity<Object> getApproverById(@PathVariable Long approverId) {

        if (approverId == null) {
            throw new IllegalArgumentException("Approver ID must not be null");
        }
        ApproverMaster approver = approverRepository.findById(approverId)  

                .orElseThrow(() -> new RuntimeException("Approver not found"));
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(approver), HttpStatus.OK);
    }

    // Get approvers by workflow and branch
    @GetMapping("/workflow/{workflowId}/branch/{branchId}")
    public ResponseEntity<Object> getApproversByWorkflowAndBranch(
            @PathVariable Integer workflowId,
            @PathVariable Long branchId) {
        List<ApproverMaster> approvers = approverRepository
                .findByWorkflowIdAndBranchIdOrderByApprovalLevelAscApprovalSequenceAsc(workflowId, branchId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(approvers), HttpStatus.OK);
    }

    // Get active approvers by workflow and branch
    @GetMapping("/workflow/{workflowId}/branch/{branchId}/active")
    public ResponseEntity<Object> getActiveApproversByWorkflowAndBranch(
            @PathVariable Integer workflowId,
            @PathVariable Long branchId) {
        List<ApproverMaster> approvers = approverRepository
                .findByWorkflowIdAndBranchIdAndStatus(workflowId, branchId, "Active");
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(approvers), HttpStatus.OK);
    }

    // Add approver
    @PostMapping
    public ResponseEntity<Object> addApprover(@RequestBody ApproverMaster approver) {
        try {
            // Auto-generate approver code: W{workflow_id}-B{branch_id}-{sequence}
            String code = String.format("W%d-B%d-%03d",
                    approver.getWorkflowId(),
                    approver.getBranchId(),
                    approver.getApprovalSequence());
            approver.setApproverCode(code);

            // Check if approver code already exists
            List<ApproverMaster> existing = approverRepository
                    .findByWorkflowIdAndBranchIdOrderByApprovalLevelAscApprovalSequenceAsc(
                            approver.getWorkflowId(),
                            approver.getBranchId());

            boolean codeExists = existing.stream()
                    .anyMatch(a -> a.getApproverCode().equals(code));

            if (codeExists) {
                throw new RuntimeException("Approver with sequence " + approver.getApprovalSequence() +
                        " already exists for this workflow and branch. Please use a different sequence number.");
            }

            ApproverMaster saved = approverRepository.save(approver);
            return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Duplicate entry")) {
                errorMsg = "Approver with this sequence already exists. Please use a different sequence number.";
            }
            throw new RuntimeException(errorMsg != null ? errorMsg : "Failed to create approver");
        }
    }

    // Update approver
    @PutMapping("/{approverId}")
    public ResponseEntity<Object> updateApprover(@PathVariable Long approverId, @RequestBody ApproverMaster approver) {
         if (approverId == null) {
            throw new IllegalArgumentException("Approver ID must not be null");
        }
        ApproverMaster existing = approverRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        existing.setRoleId(approver.getRoleId());
        existing.setRoleName(approver.getRoleName());
        existing.setApprovalLevel(approver.getApprovalLevel());
        existing.setApprovalSequence(approver.getApprovalSequence());
        existing.setIsParallelApproval(approver.getIsParallelApproval());
        existing.setIsMandatory(approver.getIsMandatory());
        existing.setStatus(approver.getStatus());
        existing.setConditionCheckType(approver.getConditionCheckType());
        existing.setLimitCheckConfig(approver.getLimitCheckConfig());
        existing.setSkipIfCondition(approver.getSkipIfCondition());
        existing.setEscalateIfCondition(approver.getEscalateIfCondition());
        existing.setUpdatedBy(approver.getUpdatedBy());

        // Re-generate code if level/sequence changed
        String code = String.format("W%d-B%d-%03d",
                existing.getWorkflowId(),
                existing.getBranchId(),
                existing.getApprovalSequence());
        existing.setApproverCode(code);

        ApproverMaster saved = approverRepository.save(existing);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
    }

    // Delete approver
    @DeleteMapping("/{approverId}")
    public ResponseEntity<Object> deleteApprover(@PathVariable Long approverId) {
         if (approverId == null) {
            throw new IllegalArgumentException("Approver ID must not be null");
        }
        ApproverMaster approver = approverRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        approverRepository.delete(approver);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Approver deleted successfully"), HttpStatus.OK);
    }

    // Activate/Deactivate approver
    @PutMapping("/{approverId}/status")
    public ResponseEntity<Object> updateApproverStatus(
            @PathVariable Long approverId,
            @RequestParam String status,
            @RequestParam String updatedBy) {
                 if (approverId == null) {
            throw new IllegalArgumentException("Approver ID must not be null");
        }
        ApproverMaster approver = approverRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        approver.setStatus(status);
        approver.setUpdatedBy(updatedBy);

        ApproverMaster saved = approverRepository.save(approver);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
    }

    // Get workflow branches
    @GetMapping("/workflows/{workflowId}/branches")
    public ResponseEntity<Object> getWorkflowBranches(@PathVariable Integer workflowId) {
        List<WorkflowBranchMaster> branches = branchRepository.findByWorkflowIdAndIsActiveTrue(workflowId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(branches), HttpStatus.OK);
    }

    // Get all branches for a workflow
    @GetMapping("/workflows/{workflowId}/branches/all")
    public ResponseEntity<Object> getAllWorkflowBranches(@PathVariable Integer workflowId) {
        List<WorkflowBranchMaster> branches = branchRepository.findByWorkflowId(workflowId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(branches), HttpStatus.OK);
    }

    // Create workflow branch
    @PostMapping("/workflows/{workflowId}/branches")
    public ResponseEntity<Object> createWorkflowBranch(@PathVariable Integer workflowId, @RequestBody WorkflowBranchMaster branch) {
        try {
            branch.setWorkflowId(workflowId);

            // Validate conditionConfig is valid JSON or null
            String conditionConfig = branch.getConditionConfig();
            if (conditionConfig != null && !conditionConfig.trim().isEmpty()) {
                // Check if it's valid JSON
                if (!isValidJSON(conditionConfig)) {
                    // If not valid JSON and it's DEFAULT type, set to null
                    if ("DEFAULT".equalsIgnoreCase(branch.getConditionType())) {
                        branch.setConditionConfig(null);
                    } else {
                        throw new RuntimeException("Condition Config must be valid JSON format. Example: {\"minAmount\": 50000}");
                    }
                }

                // Check for duplicate conditionConfig (only for non-DEFAULT types)
                if (!"DEFAULT".equalsIgnoreCase(branch.getConditionType())) {
                    String normalizedConfig = normalizeJSON(conditionConfig);
                    Optional<WorkflowBranchMaster> existingBranch = branchRepository
                            .findByWorkflowIdAndConditionConfig(workflowId, normalizedConfig);

                    if (existingBranch.isPresent()) {
                        throw new RuntimeException("A branch with the same condition configuration already exists: '"
                                + existingBranch.get().getBranchName() + "'. Please use a different condition.");
                    }

                    // Also check with original config in case normalization differs
                    existingBranch = branchRepository.findByWorkflowIdAndConditionConfig(workflowId, conditionConfig);
                    if (existingBranch.isPresent()) {
                        throw new RuntimeException("A branch with the same condition configuration already exists: '"
                                + existingBranch.get().getBranchName() + "'. Please use a different condition.");
                    }
                }
            } else if ("DEFAULT".equalsIgnoreCase(branch.getConditionType())) {
                // DEFAULT branches should have null config
                branch.setConditionConfig(null);
            }

            WorkflowBranchMaster saved = branchRepository.save(branch);
            return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create branch: " + e.getMessage());
        }
    }

    // Helper method to normalize JSON for comparison (removes whitespace)
    private String normalizeJSON(String json) {
        if (json == null) return null;
        return json.replaceAll("\\s+", "").trim();
    }

    // Helper method to validate JSON
    private boolean isValidJSON(String json) {
        if (json == null || json.trim().isEmpty()) {
            return true;
        }
        json = json.trim();
        return (json.startsWith("{") && json.endsWith("}")) ||
               (json.startsWith("[") && json.endsWith("]"));
    }

    // Update workflow branch
    @PutMapping("/branches/{branchId}")
    public ResponseEntity<Object> updateWorkflowBranch(@PathVariable Long branchId, @RequestBody WorkflowBranchMaster branch) {
           if (branchId == null) {
            throw new IllegalArgumentException("Branch ID must not be null");
        }
        if (branch == null) {
            throw new IllegalArgumentException("Branch payload must not be null");
        }
        WorkflowBranchMaster existing = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        existing.setBranchCode(branch.getBranchCode());
        existing.setBranchName(branch.getBranchName());
        existing.setBranchDescription(branch.getBranchDescription());
        existing.setConditionType(branch.getConditionType());
        existing.setConditionConfig(branch.getConditionConfig());
        existing.setDisplayOrder(branch.getDisplayOrder());
        existing.setIsActive(branch.getIsActive());

        WorkflowBranchMaster saved = branchRepository.save(existing);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
    }

    // Delete workflow branch
    @DeleteMapping("/branches/{branchId}")
    public ResponseEntity<Object> deleteWorkflowBranch(@PathVariable Long branchId) {
         if (branchId == null) {
            throw new IllegalArgumentException("Branch ID must not be null");
        }
        WorkflowBranchMaster branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        branchRepository.delete(branch);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Branch deleted successfully"), HttpStatus.OK);
    }

    // ==================== APPROVAL LEVEL AUTO-INCREMENT ENDPOINTS ====================

    /**
     * Get the next approval level for a branch (for auto-increment in UI)
     */
    @GetMapping("/workflow/{workflowId}/branch/{branchId}/next-level")
    public ResponseEntity<Object> getNextApprovalLevel(
            @PathVariable Integer workflowId,
            @PathVariable Long branchId) {
        Integer maxLevel = approverRepository.findMaxApprovalLevel(workflowId, branchId);
        Integer nextLevel = (maxLevel != null ? maxLevel : 0) + 1;

        Map<String, Object> result = new HashMap<>();
        result.put("nextApprovalLevel", nextLevel);
        result.put("nextApprovalSequence", approverRepository.findMaxApprovalSequence(workflowId, branchId) + 1);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(result), HttpStatus.OK);
    }

    /**
     * Add approver with auto-increment and level shifting
     * If inserting at a level that exists, shift all existing approvers at or above that level up by 1
     */
    @PostMapping("/with-shift")
    @Transactional
    public ResponseEntity<Object> addApproverWithShift(@RequestBody ApproverMaster approver) {
        try {
            Integer workflowId = approver.getWorkflowId();
            Long branchId = approver.getBranchId();
            Integer requestedLevel = approver.getApprovalLevel();

            // Check if there are existing approvers at this level or above
            List<ApproverMaster> existingAtLevel = approverRepository
                    .findByWorkflowIdAndBranchIdAndApprovalLevelGreaterThanEqual(workflowId, branchId, requestedLevel);

            if (!existingAtLevel.isEmpty()) {
                // Shift all approvers at or above this level up by 1
                approverRepository.incrementApprovalLevels(workflowId, branchId, requestedLevel);
            }

            // Get next sequence number
            Integer maxSequence = approverRepository.findMaxApprovalSequence(workflowId, branchId);
            approver.setApprovalSequence(maxSequence + 1);

            // Auto-generate approver code
            String code = String.format("W%d-B%d-%03d", workflowId, branchId, approver.getApprovalSequence());
            approver.setApproverCode(code);

            ApproverMaster saved = approverRepository.save(approver);
            return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add approver: " + e.getMessage());
        }
    }

    // ==================== CONDITION TYPES METADATA ENDPOINT ====================

    /**
     * Get all available condition types with examples
     * Includes MODE_OF_PROCUREMENT for routing based on procurement method
     */
    @GetMapping("/condition-types")
    public ResponseEntity<Object> getConditionTypes() {
        List<Map<String, Object>> conditionTypes = new ArrayList<>();

        // CATEGORY condition type
        Map<String, Object> category = new HashMap<>();
        category.put("type", "CATEGORY");
        category.put("label", "Category Based");
        category.put("description", "Route based on indent category (Computer/Non-Computer)");
        category.put("examples", Arrays.asList(
                "{\"category\": \"COMPUTER\"}",
                "{\"category\": \"NON_COMPUTER\"}"
        ));
        conditionTypes.add(category);

        // LOCATION condition type
        Map<String, Object> location = new HashMap<>();
        location.put("type", "LOCATION");
        location.put("label", "Location Based");
        location.put("description", "Route based on indent location (Bangalore/Non-Bangalore)");
        location.put("examples", Arrays.asList(
                "{\"location\": \"BANGALORE\"}",
                "{\"location\": \"NON_BANGALORE\"}",
                "{\"location\": \"FIELD_STATION\"}"
        ));
        conditionTypes.add(location);

        // AMOUNT condition type
        Map<String, Object> amount = new HashMap<>();
        amount.put("type", "AMOUNT");
        amount.put("label", "Amount Based");
        amount.put("description", "Route based on indent value thresholds");
        amount.put("examples", Arrays.asList(
                "{\"minAmount\": 0, \"maxAmount\": 50000}",
                "{\"minAmount\": 50001, \"maxAmount\": 100000}",
                "{\"minAmount\": 100001, \"maxAmount\": 150000}",
                "{\"minAmount\": 150001}"
        ));
        conditionTypes.add(amount);

        // PROJECT condition type
        Map<String, Object> project = new HashMap<>();
        project.put("type", "PROJECT");
        project.put("label", "Project Based");
        project.put("description", "Route based on project classification");
        project.put("examples", Arrays.asList(
                "{\"isProject\": true}",
                "{\"isProject\": false}"
        ));
        conditionTypes.add(project);

        // MODE_OF_PROCUREMENT condition type
        Map<String, Object> mop = new HashMap<>();
        mop.put("type", "MODE_OF_PROCUREMENT");
        mop.put("label", "Mode of Procurement Based");
        mop.put("description", "Route based on procurement method (GeM, Open Tender, etc.)");
        mop.put("examples", Arrays.asList(
                "{\"modeOfProcurement\": \"GEM\"}",
                "{\"modeOfProcurement\": \"OPEN_TENDER\"}",
                "{\"modeOfProcurement\": \"LIMITED_TENDER\"}",
                "{\"modeOfProcurement\": \"SINGLE_TENDER\"}",
                "{\"modeOfProcurement\": \"PROPRIETARY\"}",
                "{\"modeOfProcurement\": \"RATE_CONTRACT\"}",
                "{\"modeOfProcurement\": \"DIRECT_PURCHASE\"}"
        ));
        conditionTypes.add(mop);

        // COMPOUND condition type
        Map<String, Object> compound = new HashMap<>();
        compound.put("type", "COMPOUND");
        compound.put("label", "Compound Condition");
        compound.put("description", "Combine multiple conditions with AND/OR logic");
        compound.put("examples", Arrays.asList(
                "{\"category\": \"COMPUTER\", \"location\": \"BANGALORE\"}",
                "{\"category\": \"NON_COMPUTER\", \"minAmount\": 50001, \"maxAmount\": 100000}",
                "{\"isProject\": true, \"modeOfProcurement\": \"GEM\"}",
                "{\"category\": \"COMPUTER\", \"modeOfProcurement\": \"OPEN_TENDER\", \"location\": \"NON_BANGALORE\"}"
        ));
        conditionTypes.add(compound);

        // DEFAULT condition type
        Map<String, Object> defaultType = new HashMap<>();
        defaultType.put("type", "DEFAULT");
        defaultType.put("label", "Default/Fallback");
        defaultType.put("description", "Fallback branch when no other conditions match");
        defaultType.put("examples", Arrays.asList("null (no config needed)"));
        conditionTypes.add(defaultType);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(conditionTypes), HttpStatus.OK);
    }

    /**
     * Get all modes of procurement for dropdown
     */
    @GetMapping("/modes-of-procurement")
    public ResponseEntity<Object> getModesOfProcurement() {
        List<Map<String, String>> modes = Arrays.asList(
                createMode("GEM", "Government e-Marketplace (GeM)"),
                createMode("OPEN_TENDER", "Open Tender"),
                createMode("LIMITED_TENDER", "Limited Tender"),
                createMode("SINGLE_TENDER", "Single Tender"),
                createMode("PROPRIETARY", "Proprietary Purchase"),
                createMode("RATE_CONTRACT", "Rate Contract"),
                createMode("DIRECT_PURCHASE", "Direct Purchase"),
                createMode("REPEAT_ORDER", "Repeat Order"),
                createMode("EMERGENCY_PURCHASE", "Emergency Purchase")
        );
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(modes), HttpStatus.OK);
    }

    private Map<String, String> createMode(String code, String name) {
        Map<String, String> mode = new HashMap<>();
        mode.put("code", code);
        mode.put("name", name);
        return mode;
    }

    // ==================== FULL WORKFLOW CONFIGURATION ENDPOINT ====================

    /**
     * Get the complete workflow configuration in a single response.
     * This endpoint returns all configuration data needed to understand
     * the dynamic indent approval workflow:
     *
     * - Workflow branches and their conditions
     * - Approvers for each branch
     * - Approval limits by role
     * - Department-to-Dean/HeadSEG mappings
     * - Field station in-charges (Engineer/Professor)
     *
     * This is useful for:
     * 1. Admin panel overview
     * 2. Frontend to understand routing logic
     * 3. Documentation/audit purposes
     */
    @GetMapping("/full-config")
    public ResponseEntity<Object> getFullWorkflowConfiguration() {
        WorkflowFullConfigDTO config = new WorkflowFullConfigDTO();

        // Get all workflow branches for Indent Workflow (workflowId = 1)
        List<WorkflowBranchMaster> branches = branchRepository.findByWorkflowIdAndIsActiveTrue(1);
        config.setBranches(branches);

        // For each branch, get its approvers
        for (WorkflowBranchMaster branch : branches) {
            List<ApproverMaster> approvers = approverRepository
                    .findByWorkflowIdAndBranchIdAndStatus(1, branch.getBranchId(), "Active");
            config.addBranchApprovers(branch.getBranchId(), approvers);
        }

        // Get all approval limits
        List<ApprovalLimitDTO> approvalLimits = approvalLimitService.getAll();
        config.setApprovalLimits(approvalLimits);

        // Get all department-approver mappings
        List<DepartmentApproverMappingDTO> deptMappings = departmentApproverService.getAll();
        config.setDepartmentApproverMappings(deptMappings);

        // Get all field station in-charges
        List<FieldStationApproverDTO> fieldStations = fieldStationApproverService.getAll();
        config.setFieldStationApprovers(fieldStations);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(config), HttpStatus.OK);
    }

    /**
     * Get configuration for a specific workflow by ID
     */
    @GetMapping("/full-config/workflow/{workflowId}")
    public ResponseEntity<Object> getWorkflowConfiguration(@PathVariable Integer workflowId) {
        WorkflowFullConfigDTO config = new WorkflowFullConfigDTO();
        config.setWorkflowId(workflowId);

        // Get all workflow branches for the specified workflow
        List<WorkflowBranchMaster> branches = branchRepository.findByWorkflowIdAndIsActiveTrue(workflowId);
        config.setBranches(branches);

        // For each branch, get its approvers
        for (WorkflowBranchMaster branch : branches) {
            List<ApproverMaster> approvers = approverRepository
                    .findByWorkflowIdAndBranchIdAndStatus(workflowId, branch.getBranchId(), "Active");
            config.addBranchApprovers(branch.getBranchId(), approvers);
        }

        // Get all approval limits
        List<ApprovalLimitDTO> approvalLimits = approvalLimitService.getAll();
        config.setApprovalLimits(approvalLimits);

        // Get all department-approver mappings
        List<DepartmentApproverMappingDTO> deptMappings = departmentApproverService.getAll();
        config.setDepartmentApproverMappings(deptMappings);

        // Get all field station in-charges
        List<FieldStationApproverDTO> fieldStations = fieldStationApproverService.getAll();
        config.setFieldStationApprovers(fieldStations);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(config), HttpStatus.OK);
    }
}
