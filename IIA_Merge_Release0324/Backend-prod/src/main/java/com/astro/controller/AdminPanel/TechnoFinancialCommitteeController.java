package com.astro.controller.AdminPanel;

import com.astro.dto.workflow.TechnoFinancialCommitteeDto;
import com.astro.service.TechnoFinancialCommitteeService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/techno-financial-committee")
@CrossOrigin
public class TechnoFinancialCommitteeController {

    @Autowired
    private TechnoFinancialCommitteeService committeeService;

    @GetMapping
    public ResponseEntity<Object> getAllMembers() {
        List<TechnoFinancialCommitteeDto> members = committeeService.getAllActiveMembers();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(members), HttpStatus.OK);
    }

    @GetMapping("/chairman")
    public ResponseEntity<Object> getChairman() {
        TechnoFinancialCommitteeDto chairman = committeeService.getChairman();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(chairman), HttpStatus.OK);
    }

    /** GET /api/admin/techno-financial-committee/chairman/{type} — e.g., STEC_I or STEC_II */
    @GetMapping("/chairman/{type}")
    public ResponseEntity<Object> getChairmanByType(@PathVariable String type) {
        TechnoFinancialCommitteeDto chairman = committeeService.getChairmanByType(type);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(chairman), HttpStatus.OK);
    }

    /** GET /api/admin/techno-financial-committee/type/{type} — get all members of a specific STEC */
    @GetMapping("/type/{type}")
    public ResponseEntity<Object> getMembersByType(@PathVariable String type) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(committeeService.getMembersByType(type)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> addMember(@RequestBody TechnoFinancialCommitteeDto dto) {
        TechnoFinancialCommitteeDto saved = committeeService.addMember(dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMember(@PathVariable Long id,
                                               @RequestBody TechnoFinancialCommitteeDto dto) {
        TechnoFinancialCommitteeDto updated = committeeService.updateMember(id, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(updated), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deactivateMember(@PathVariable Long id) {
        committeeService.deactivateMember(id);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Member deactivated successfully"), HttpStatus.OK);
    }
}