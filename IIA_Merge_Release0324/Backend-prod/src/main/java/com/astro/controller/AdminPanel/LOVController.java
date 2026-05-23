package com.astro.controller.AdminPanel;

import com.astro.dto.AdminPanel.DropdownResponseDto;
import com.astro.dto.AdminPanel.LOVRequestDto;
import com.astro.dto.AdminPanel.LOVResponseDto;
import com.astro.entity.AdminPanel.*;
import com.astro.service.AdminPanel.LOVService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for managing List of Values (LOV) from Admin Panel.
 * Provides CRUD operations for Forms, Designators, and LOV values.
 */
@RestController
@RequestMapping("/api/admin/lov")
@CrossOrigin
public class LOVController {

    @Autowired
    private LOVService lovService;

    // ========== FORM MASTER ENDPOINTS ==========

    /**
     * Get all active forms
     * GET /api/admin/lov/forms
     */
    @GetMapping("/forms")
    public ResponseEntity<Object> getAllForms() {
        List<FormMaster> forms = lovService.getActiveForms();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(forms), HttpStatus.OK);
    }

    /**
     * Get form by ID
     * GET /api/admin/lov/forms/{formId}
     */
    @GetMapping("/forms/{formId}")
    public ResponseEntity<Object> getFormById(@PathVariable Long formId) {
        FormMaster form = lovService.getFormById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + formId));
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(form), HttpStatus.OK);
    }

    /**
     * Create new form
     * POST /api/admin/lov/forms
     */
    @PostMapping("/forms")
    public ResponseEntity<Object> createForm(@RequestBody FormMaster form) {
        FormMaster saved = lovService.createForm(form);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.CREATED);
    }

    /**
     * Update existing form
     * PUT /api/admin/lov/forms/{formId}
     */
    @PutMapping("/forms/{formId}")
    public ResponseEntity<Object> updateForm(@PathVariable Long formId, @RequestBody FormMaster form) {
        FormMaster updated = lovService.updateForm(formId, form);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
    }

    // ========== DESIGNATOR MASTER ENDPOINTS ==========

    /**
     * Get all active designators for a form
     * GET /api/admin/lov/forms/{formId}/designators
     */
    @GetMapping("/forms/{formId}/designators")
    public ResponseEntity<Object> getDesignators(@PathVariable Long formId) {
        List<DesignatorMaster> designators = lovService.getActiveDesignatorsByFormId(formId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(designators), HttpStatus.OK);
    }

    /**
     * Get designator by ID
     * GET /api/admin/lov/designators/{designatorId}
     */
    @GetMapping("/designators/{designatorId}")
    public ResponseEntity<Object> getDesignatorById(@PathVariable Long designatorId) {
        DesignatorMaster designator = lovService.getDesignatorById(designatorId)
                .orElseThrow(() -> new RuntimeException("Designator not found with ID: " + designatorId));
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(designator), HttpStatus.OK);
    }

    /**
     * Create new designator
     * POST /api/admin/lov/designators
     */
    @PostMapping("/designators")
    public ResponseEntity<Object> createDesignator(@RequestBody DesignatorMaster designator) {
        DesignatorMaster saved = lovService.createDesignator(designator);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.CREATED);
    }

    /**
     * Update existing designator
     * PUT /api/admin/lov/designators/{designatorId}
     */
    @PutMapping("/designators/{designatorId}")
    public ResponseEntity<Object> updateDesignator(@PathVariable Long designatorId, @RequestBody DesignatorMaster designator) {
        DesignatorMaster updated = lovService.updateDesignator(designatorId, designator);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
    }

    // ========== LOV MASTER ENDPOINTS ==========

    /**
     * Get all active LOV values for a designator
     * GET /api/admin/lov/designators/{designatorId}/values
     */
    @GetMapping("/designators/{designatorId}/values")
    public ResponseEntity<Object> getLOVValues(@PathVariable Long designatorId) {
        List<LOVMaster> values = lovService.getLOVsByDesignatorId(designatorId);
        List<LOVResponseDto> response = values.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    /**
     * Get LOV value by ID
     * GET /api/admin/lov/values/{lovId}
     */
    @GetMapping("/values/{lovId}")
    public ResponseEntity<Object> getLOVById(@PathVariable Long lovId) {
        LOVMaster lov = lovService.getLOVById(lovId)
                .orElseThrow(() -> new RuntimeException("LOV not found with ID: " + lovId));
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(lov), HttpStatus.OK);
    }

    /**
     * Create new LOV value
     * POST /api/admin/lov/values
     */
    @PostMapping("/values")
    public ResponseEntity<Object> addLOVValue(@RequestBody LOVRequestDto lovRequest) {
        LOVMaster lov = convertToEntity(lovRequest);
        // Modified by aman on 23 feb for depart price limit
        LOVMaster saved = lovService.createLOV(lov , lovRequest);
        // LOVMaster saved = lovService.createLOV(lov);
        // End
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.CREATED);
    }

    /**
     * Update existing LOV value
     * PUT /api/admin/lov/values/{lovId}
     */
    @PutMapping("/values/{lovId}")
    public ResponseEntity<Object> updateLOVValue(@PathVariable Long lovId, @RequestBody LOVRequestDto lovRequest) {
        LOVMaster lov = convertToEntity(lovRequest);
         // Modified by Aman 
        // LOVMaster updated = lovService.updateLOV(lovId, lov);
        LOVMaster updated = lovService.updateLOV(lovId, lov, lovRequest);
        // End
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
    }

    /**
     * Delete LOV value (soft delete)
     * DELETE /api/admin/lov/values/{lovId}
     */
    @DeleteMapping("/values/{lovId}")
    public ResponseEntity<Object> deleteLOVValue(@PathVariable Long lovId) {
        lovService.deleteLOV(lovId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("LOV deleted successfully"), HttpStatus.OK);
    }

    // ========== CONVENIENCE ENDPOINTS ==========

    /**
     * Get LOV values by form name and field name
     * GET /api/admin/lov/forms/{formName}/field/{fieldName}/values
     * Example: /api/admin/lov/forms/MaterialMaster/field/category/values
     */
    @GetMapping("/forms/{formName}/field/{fieldName}/values")
    public ResponseEntity<Object> getLOVsByFormAndField(
            @PathVariable String formName,
            @PathVariable String fieldName) {
        List<LOVMaster> values = lovService.getLOVsByFormAndField(formName, fieldName);
        List<LOVResponseDto> response = values.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    /**
     * Get all dropdowns for a specific form (returns all fields with their LOV values)
     * GET /api/admin/lov/forms/{formName}/all-dropdowns
     * Example: /api/admin/lov/forms/MaterialMaster/all-dropdowns
     */
    @GetMapping("/forms/{formName}/all-dropdowns")
    public ResponseEntity<Object> getAllDropdownsForForm(@PathVariable String formName) {
        Map<String, List<LOVMaster>> dropdowns = lovService.getAllDropdownsForForm(formName);

        Optional<FormMaster> form = lovService.getFormByName(formName);
        if (!form.isPresent()) {
            throw new RuntimeException("Form not found: " + formName);
        }

        // Convert to DTO format
        DropdownResponseDto response = new DropdownResponseDto();
        response.setFormName(formName);
        response.setFormDisplayName(form.get().getFormDisplayName());

        List<DropdownResponseDto.FieldDropdownDto> fieldDropdowns = new ArrayList<>();
        for (Map.Entry<String, List<LOVMaster>> entry : dropdowns.entrySet()) {
            DropdownResponseDto.FieldDropdownDto fieldDto = new DropdownResponseDto.FieldDropdownDto();
            fieldDto.setFieldName(entry.getKey());

            List<LOVResponseDto> lovDtos = entry.getValue().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            fieldDto.setValues(lovDtos);

            fieldDropdowns.add(fieldDto);
        }
        response.setDropdowns(fieldDropdowns);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    /**
     * Get dependent LOV values (for cascading dropdowns)
     * GET /api/admin/lov/dependent/{parentLovId}
     */
    @GetMapping("/dependent/{parentLovId}")
    public ResponseEntity<Object> getDependentLOVs(@PathVariable Long parentLovId) {
        List<LOVMaster> dependentValues = lovService.getDependentLOVs(parentLovId);
        List<LOVResponseDto> response = dependentValues.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    /**
     * Get multiple LOV lists in a single call (bulk fetch)
     * POST /api/admin/lov/bulk
     * Body: ["MaterialMaster.category", "MaterialMaster.subCategory", "JobMaster.uom"]
     */
    @PostMapping("/bulk")
    public ResponseEntity<Object> getBulkLOVs(@RequestBody List<String> formFieldPairs) {
        Map<String, List<LOVMaster>> bulkLOVs = lovService.getBulkLOVs(formFieldPairs);

        // Convert to DTO format
        Map<String, List<LOVResponseDto>> response = new HashMap<>();
        for (Map.Entry<String, List<LOVMaster>> entry : bulkLOVs.entrySet()) {
            List<LOVResponseDto> lovDtos = entry.getValue().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            response.put(entry.getKey(), lovDtos);
        }

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    /**
     * Bulk import LOV values
     * POST /api/admin/lov/designators/{designatorId}/bulk-import
     */
    @PostMapping("/designators/{designatorId}/bulk-import")
    public ResponseEntity<Object> bulkImportLOVs(
            @PathVariable Long designatorId,
            @RequestBody List<LOVRequestDto> lovRequests) {
        List<LOVMaster> lovs = lovRequests.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        List<LOVMaster> imported = lovService.bulkImportLOVs(designatorId, lovs);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(imported), HttpStatus.CREATED);
    }

    /**
     * Reorder LOV values
     * PUT /api/admin/lov/designators/{designatorId}/reorder
     * Body: [3, 1, 5, 2, 4] (LOV IDs in desired order)
     */
    @PutMapping("/designators/{designatorId}/reorder")
    public ResponseEntity<Object> reorderLOVs(
            @PathVariable Long designatorId,
            @RequestBody List<Long> lovIdsInOrder) {
        lovService.reorderLOVs(designatorId, lovIdsInOrder);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("LOVs reordered successfully"), HttpStatus.OK);
    }

    /**
     * Get total count of all active LOV values
     * GET /api/admin/lov/values/count
     * Returns the total number of active LOV entries across all forms and designators
     */
    @GetMapping("/values/count")
    public ResponseEntity<Object> getTotalLOVCount() {
        long totalCount = lovService.getTotalActiveLOVCount();
        Map<String, Long> response = new HashMap<>();
        response.put("totalCount", totalCount);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    // ========== HELPER METHODS ==========

    private LOVResponseDto convertToDto(LOVMaster lov) {
        LOVResponseDto dto = new LOVResponseDto();
        dto.setLovId(lov.getLovId());
        dto.setValue(lov.getLovValue());
        dto.setDisplayValue(lov.getLovDisplayValue());
        dto.setDescription(lov.getLovDescription());
        dto.setIsActive(lov.getIsActive());
        dto.setIsDefault(lov.getIsDefault());
        dto.setDisplayOrder(lov.getDisplayOrder());
        dto.setColorCode(lov.getColorCode());
        dto.setIconName(lov.getIconName());
        dto.setParentLovId(lov.getParentLovId());
        return dto;
    }

    private LOVMaster convertToEntity(LOVRequestDto dto) {
        LOVMaster lov = new LOVMaster();
        lov.setDesignatorId(dto.getDesignatorId());
        lov.setLovValue(dto.getLovValue());
        lov.setLovDisplayValue(dto.getLovDisplayValue());
        lov.setLovDescription(dto.getLovDescription());
        lov.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        lov.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
        lov.setDisplayOrder(dto.getDisplayOrder());
        lov.setColorCode(dto.getColorCode());
        lov.setIconName(dto.getIconName());
        lov.setParentLovId(dto.getParentLovId());
        lov.setCreatedBy(dto.getCreatedBy());
        lov.setUpdatedBy(dto.getUpdatedBy());
        return lov;
    }
}
