package com.astro.controller.AdminPanel;

import com.astro.entity.AdminPanel.BudgetMaster;
import com.astro.entity.ProjectMaster;
import com.astro.repository.AdminPanel.BudgetMasterRepository;
import com.astro.repository.ProjectMasterRepository;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/budget")
@CrossOrigin
public class BudgetController {

    @Autowired
    private BudgetMasterRepository budgetRepository;

    @Autowired
    private ProjectMasterRepository projectMasterRepository;

    @PostMapping
    public ResponseEntity<Object> createBudget(@RequestBody BudgetMaster budget) {
        try {
            // Check for duplicate budget code
            if (budget.getBudgetCode() != null &&
                budgetRepository.findByBudgetCode(budget.getBudgetCode()).isPresent()) {
                throw new RuntimeException("Budget code already exists: " + budget.getBudgetCode());
            }

            // Set default values if not provided
            if (budget.getOnHoldAmount() == null) {
                budget.setOnHoldAmount(BigDecimal.ZERO);
            }
            if (budget.getSpentAmount() == null) {
                budget.setSpentAmount(BigDecimal.ZERO);
            }

            BudgetMaster saved = budgetRepository.save(budget);
            return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create budget: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllBudgets() {
        List<BudgetMaster> budgets = budgetRepository.findAll();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(budgets), HttpStatus.OK);
    }

    @GetMapping("/{budgetCode}")
    public ResponseEntity<Object> getBudgetByCode(@PathVariable String budgetCode) {
        BudgetMaster budget = budgetRepository.findByBudgetCode(budgetCode)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(budget), HttpStatus.OK);
    }

    @PutMapping("/{budgetCode}")
    public ResponseEntity<Object> updateBudget(@PathVariable String budgetCode, @RequestBody BudgetMaster budget) {
        BudgetMaster existing = budgetRepository.findByBudgetCode(budgetCode)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

                // Block reduction below already committed (hold + spent)
    BigDecimal alreadyCommitted =
        (existing.getOnHoldAmount() != null ? existing.getOnHoldAmount() : BigDecimal.ZERO)
        .add(existing.getSpentAmount() != null ? existing.getSpentAmount() : BigDecimal.ZERO);

    if (budget.getAllocatedAmount().compareTo(alreadyCommitted) < 0) {
        throw new RuntimeException(
            "Cannot reduce allocated amount below committed amount. " +
            "On Hold: " + existing.getOnHoldAmount() +
            ", Spent: " + existing.getSpentAmount() +
            ", Minimum allowed: " + alreadyCommitted);
    }

        existing.setBudgetName(budget.getBudgetName());
        existing.setCategory(budget.getCategory());
        existing.setAllocatedAmount(budget.getAllocatedAmount());
        existing.setOnHoldAmount(budget.getOnHoldAmount());
        existing.setSpentAmount(budget.getSpentAmount());
        existing.setFiscalYear(budget.getFiscalYear());
        existing.setStartDate(budget.getStartDate());
        existing.setEndDate(budget.getEndDate());
        existing.setStatus(budget.getStatus());
        existing.setProjectCode(budget.getProjectCode());
        existing.setDepartmentName(budget.getDepartmentName());
        existing.setUpdatedBy(budget.getUpdatedBy());

        BudgetMaster saved = budgetRepository.save(existing);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(saved), HttpStatus.OK);
    }

    @DeleteMapping("/{budgetCode}")
    public ResponseEntity<Object> deleteBudget(@PathVariable String budgetCode) {
        BudgetMaster budget = budgetRepository.findByBudgetCode(budgetCode)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        budgetRepository.delete(budget);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Budget deleted successfully"), HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<Object> getBudgetSummary() {
        List<BudgetMaster> budgets = budgetRepository.findAll();

        BigDecimal totalAllocated = budgets.stream()
                .map(BudgetMaster::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOnHold = budgets.stream()
                .map(b -> b.getOnHoldAmount() != null ? b.getOnHoldAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent = budgets.stream()
                .map(b -> b.getSpentAmount() != null ? b.getSpentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRemaining = budgets.stream()
                .map(BudgetMaster::getRemainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalAllocated", totalAllocated);
        summary.put("totalOnHold", totalOnHold);
        summary.put("totalSpent", totalSpent);
        summary.put("totalRemaining", totalRemaining);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(summary), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> getBudgetsByStatus(@PathVariable String status) {
        List<BudgetMaster> budgets = budgetRepository.findByStatus(status);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(budgets), HttpStatus.OK);
    }

    @GetMapping("/fiscal-year/{fiscalYear}")
    public ResponseEntity<Object> getBudgetsByFiscalYear(@PathVariable String fiscalYear) {
        List<BudgetMaster> budgets = budgetRepository.findByFiscalYear(fiscalYear);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(budgets), HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Object> getBudgetsByCategory(@PathVariable String category) {
        List<BudgetMaster> budgets = budgetRepository.findByCategory(category);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(budgets), HttpStatus.OK);
    }

    @GetMapping("/department/{departmentName}")
    public ResponseEntity<Object> getBudgetsByDepartment(@PathVariable String departmentName) {
        List<BudgetMaster> budgets = budgetRepository.findByDepartmentName(departmentName);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(budgets), HttpStatus.OK);
    }

    /**
     * Get all budget codes for dropdown (used in Add New Project)
     * Returns a list of budget codes with their names for dropdown selection
     */
    @GetMapping("/dropdown")
    public ResponseEntity<Object> getBudgetCodesForDropdown() {
        List<BudgetMaster> budgets = budgetRepository.findAll();
        List<Map<String, String>> dropdownItems = budgets.stream()
                .filter(b -> b.getBudgetCode() != null)
                .map(b -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("budgetCode", b.getBudgetCode());
                    item.put("budgetName", b.getBudgetName() != null ? b.getBudgetName() : b.getBudgetCode());
                    item.put("displayValue", b.getBudgetCode() + " - " + (b.getBudgetName() != null ? b.getBudgetName() : ""));
                    return item;
                })
                .collect(java.util.stream.Collectors.toList());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(dropdownItems), HttpStatus.OK);
    }

    /**
     * Get budgets by project code (used in Indent Creation page)
     * Returns budgets linked to a specific project
     */
    @GetMapping("/project/{projectCode}")
    public ResponseEntity<Object> getBudgetsByProjectCode(@PathVariable String projectCode) {
        List<BudgetMaster> budgets = budgetRepository.findByProjectCode(projectCode);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(budgets), HttpStatus.OK);
    }

    /**
     * Get budget codes dropdown for a specific project (used in Indent Creation)
     * Returns budget codes linked to the selected project
     */
    @GetMapping("/project/{projectCode}/dropdown")
    public ResponseEntity<Object> getBudgetCodesForProjectDropdown(@PathVariable String projectCode) {
        List<Map<String, String>> dropdownItems = new ArrayList<>();

        Optional<ProjectMaster> projectOpt = projectMasterRepository.findById(projectCode);

        // Primary: project's stored budgetCode field
        if (projectOpt.isPresent()) {
            String budgetCode = projectOpt.get().getBudgetCode();
            if (budgetCode != null && !budgetCode.trim().isEmpty()) {
                budgetRepository.findByBudgetCode(budgetCode)
                        .ifPresent(b -> dropdownItems.add(toBudgetDropdownItem(b)));
            }
        }

        // Fallback 1: budgets where budget.projectCode = this project
        if (dropdownItems.isEmpty()) {
            budgetRepository.findByProjectCode(projectCode).stream()
                    .filter(b -> b.getBudgetCode() != null)
                    .forEach(b -> dropdownItems.add(toBudgetDropdownItem(b)));
        }

        // Fallback 2: budgets whose category matches the project's budgetType
        if (dropdownItems.isEmpty() && projectOpt.isPresent()) {
            String budgetType = projectOpt.get().getBudgetType();
            if (budgetType != null && !budgetType.trim().isEmpty()) {
                budgetRepository.findByCategory(budgetType).stream()
                        .filter(b -> b.getBudgetCode() != null)
                        .forEach(b -> dropdownItems.add(toBudgetDropdownItem(b)));
            }
        }

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(dropdownItems), HttpStatus.OK);
    }

    /**
     * Get budget codes for a project using the project's stored budgetCode (used in Indent Creation).
     * Looks up the project's budgetCode field, then returns matching budget(s) as a dropdown list.
     * GET /api/admin/budget/for-project/{projectCode}
     */
    @GetMapping("/for-project/{projectCode}")
    public ResponseEntity<Object> getBudgetCodesForProject(@PathVariable String projectCode) {
        List<Map<String, String>> dropdownItems = new ArrayList<>();

        Optional<ProjectMaster> projectOpt = projectMasterRepository.findById(projectCode);

        // Primary: look up budget via project's stored budgetCode field
        if (projectOpt.isPresent()) {
            String budgetCode = projectOpt.get().getBudgetCode();
            if (budgetCode != null && !budgetCode.trim().isEmpty()) {
                budgetRepository.findByBudgetCode(budgetCode)
                        .ifPresent(b -> dropdownItems.add(toBudgetDropdownItem(b)));
            }
        }

        // Fallback 1: check if any budget has its projectCode pointing to this project
        if (dropdownItems.isEmpty()) {
            budgetRepository.findByProjectCode(projectCode).stream()
                    .filter(b -> b.getBudgetCode() != null)
                    .forEach(b -> dropdownItems.add(toBudgetDropdownItem(b)));
        }

        // Fallback 2: match budgets by category = project's budgetType
        // (covers new projects where the explicit link was never stored)
        if (dropdownItems.isEmpty() && projectOpt.isPresent()) {
            String budgetType = projectOpt.get().getBudgetType();
            if (budgetType != null && !budgetType.trim().isEmpty()) {
                budgetRepository.findByCategory(budgetType).stream()
                        .filter(b -> b.getBudgetCode() != null)
                        .forEach(b -> dropdownItems.add(toBudgetDropdownItem(b)));
            }
        }

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(dropdownItems), HttpStatus.OK);
    }

    private Map<String, String> toBudgetDropdownItem(BudgetMaster b) {
        Map<String, String> item = new HashMap<>();
        item.put("budgetCode", b.getBudgetCode());
        item.put("budgetName", b.getBudgetName() != null ? b.getBudgetName() : b.getBudgetCode());
        item.put("category", b.getCategory());
        item.put("displayValue", b.getBudgetCode() + " - " + (b.getBudgetName() != null ? b.getBudgetName() : ""));
        return item;
    }
}
