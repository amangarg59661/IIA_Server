package com.astro.controller.AdminPanel;

import com.astro.dto.AdminPanel.ApprovalLimitDTO;
import com.astro.entity.AdminPanel.ApprovalLimitMaster;
import com.astro.service.AdminPanel.ApprovalLimitService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for managing approval limits configuration.
 *
 * Approval limits define the maximum amount each role can approve without escalation.
 * Key thresholds per the workflow:
 * - Purchase Head: Up to ₹50,000 (Non-Computer)
 * - Head SEG: ₹50,001 to ₹1,00,000 (Non-Computer)
 * - Dean: ₹50,001 to ₹1,50,000 (Non-Computer)
 * - Project Head: Within available project budget
 * - Director: Final authority for all escalations
 */
@RestController
@RequestMapping("/api/admin/approval-limits")
@CrossOrigin
public class ApprovalLimitController {

    @Autowired
    private ApprovalLimitService approvalLimitService;

    /**
     * Get all approval limits
     */
    @GetMapping
    public ResponseEntity<Object> getAllApprovalLimits(@RequestParam Integer workflowId) {
        List<ApprovalLimitDTO> limits = approvalLimitService.getAll(workflowId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(limits), HttpStatus.OK);
    }

    /**
     * Get approval limit by ID
     */
    @GetMapping("/{limitId}")
    public ResponseEntity<Object> getApprovalLimitById(@PathVariable Long limitId) {
        ApprovalLimitDTO limit = approvalLimitService.getById(limitId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(limit), HttpStatus.OK);
    }

    /**
     * Get approval limits by role name
     */
    @GetMapping("/role/{roleName}")
    public ResponseEntity<Object> getApprovalLimitsByRole(@PathVariable String roleName) {
        List<ApprovalLimitDTO> limits = approvalLimitService.getByRoleName(roleName);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(limits), HttpStatus.OK);
    }

    /**
     * Get approval limits by category (COMPUTER, NON_COMPUTER, PROJECT, ALL)
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Object> getApprovalLimitsByCategory(@PathVariable String category) {
        List<ApprovalLimitDTO> limits = approvalLimitService.getByCategory(category);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(limits), HttpStatus.OK);
    }

    /**
     * Get applicable limit for a specific role, category, department, and location
     */
    @GetMapping("/applicable")
    public ResponseEntity<Object> getApplicableLimit(
            @RequestParam String roleName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String location) {
        ApprovalLimitMaster limit = approvalLimitService.getApplicableLimit(roleName, category, departmentName, location);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(limit), HttpStatus.OK);
    }

    /**
     * Check if escalation is required for a given role and amount
     */
    @GetMapping("/check-escalation")
    public ResponseEntity<Object> checkEscalation(
            @RequestParam String roleName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String location,
            @RequestParam BigDecimal amount) {
        var escalationResult = approvalLimitService.checkEscalation(roleName, category, departmentName, location, amount);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(escalationResult), HttpStatus.OK);
    }

    /**
     * Create new approval limit
     */
    @PostMapping
    public ResponseEntity<Object> createApprovalLimit(@RequestBody ApprovalLimitDTO dto) {
        try {
            ApprovalLimitDTO created = approvalLimitService.create(dto);
            return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(created), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create approval limit: " + e.getMessage());
        }
    }

    /**
     * Update approval limit
     */
    @PutMapping("/{limitId}")
    public ResponseEntity<Object> updateApprovalLimit(
            @PathVariable Long limitId,
            @RequestBody ApprovalLimitDTO dto) {
        try {
            ApprovalLimitDTO updated = approvalLimitService.update(limitId, dto);
            return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update approval limit: " + e.getMessage());
        }
    }

    /**
     * Delete approval limit (soft delete)
     */
    @DeleteMapping("/{limitId}")
    public ResponseEntity<Object> deleteApprovalLimit(@PathVariable Long limitId) {
        approvalLimitService.delete(limitId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Approval limit deleted successfully"), HttpStatus.OK);
    }

    /**
     * Activate/Deactivate approval limit
     */
    @PutMapping("/{limitId}/status")
    public ResponseEntity<Object> updateStatus(
            @PathVariable Long limitId,
            @RequestParam Boolean isActive,
            @RequestParam String updatedBy) {
        ApprovalLimitDTO updated = approvalLimitService.updateStatus(limitId, isActive, updatedBy);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
    }

    /**
     * Get all distinct role names with configured limits
     */
    @GetMapping("/roles")
    public ResponseEntity<Object> getDistinctRoles() {
        List<String> roles = approvalLimitService.getDistinctRoles();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(roles), HttpStatus.OK);
    }

    /**
     * Get all distinct categories
     */
    @GetMapping("/categories")
    public ResponseEntity<Object> getDistinctCategories() {
        List<String> categories = approvalLimitService.getDistinctCategories();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(categories), HttpStatus.OK);
    }
}
