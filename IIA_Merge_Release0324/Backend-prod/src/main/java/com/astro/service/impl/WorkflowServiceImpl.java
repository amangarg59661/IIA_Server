// package com.astro.service.impl;

// import com.astro.constant.AppConstant;
// import com.astro.constant.WorkflowName;
// import com.astro.dto.workflow.*;
// import com.astro.dto.workflow.ProcurementDtos.ContigencyPurchaseResponseDto;
// import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
// import com.astro.dto.workflow.ProcurementDtos.IndentDto.MaterialDetailsResponseDTO;
// import com.astro.dto.workflow.ProcurementDtos.PoFormateDto;
// import com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto.soWithTenderAndIndentResponseDTO;
// import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
// import com.astro.dto.workflow.ProcurementDtos.WorkOrderDto.woWithTenderAndIndentResponseDTO;
// import com.astro.dto.workflow.ProcurementDtos.pendingRecordsDto;
// import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.poWithTenderAndIndentResponseDTO;
// import com.astro.entity.*;
// import com.astro.entity.ProcurementModule.*;
// import com.astro.exception.BusinessException;
// import com.astro.exception.ErrorDetails;
// import com.astro.exception.InvalidInputException;
// import com.astro.repository.*;
// import com.astro.repository.AdminPanel.WorkflowBranchMasterRepository;
// import com.astro.repository.InventoryModule.PaymentVoucherReposiotry;
// import com.astro.repository.ProcurementModule.ContigencyPurchaseRepository;
// import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
// import com.astro.repository.ProcurementModule.IndentCreation.MaterialDetailsRepository;
// import com.astro.repository.ProcurementModule.IndentIdRepository;
// import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;
// import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository;
// import com.astro.repository.ProcurementModule.TenderRequestRepository;
// import com.astro.service.*;
// import com.astro.util.CommonUtils;
// import com.astro.util.EmailService;
// import com.astro.util.TenderEmailService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Lazy;
// import org.springframework.stereotype.Service;

// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import javax.transaction.Transactional;
// import java.io.IOException;
// import java.math.BigDecimal;
// import java.time.Duration;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.*;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// @Service
// public class WorkflowServiceImpl implements WorkflowService {

//     @Autowired
//     WorkflowTransitionRepository workflowTransitionRepository;

//     @Autowired
//     WorkflowMasterRepository workflowMasterRepository;

//     @Autowired
//     UserService userService;

//     @Autowired
//     TransitionMasterRepository transitionMasterRepository;

//     @Autowired
//     RoleMasterRepository roleMasterRepository;

//     @Autowired
//     TransitionConditionMasterRepository transitionConditionMasterRepository;

//     @Autowired
//     UserRoleMasterRepository userRoleMasterRepository;

//     @Autowired
//     IndentCreationService indentCreationService;

//     @Autowired
//     ContigencyPurchaseService contigencyPurchaseService;

//     @Autowired
//     @Lazy // added by abhinav
//     TenderRequestService tenderRequestService;

//     @Autowired
//     @Lazy // added by abhinav
//     ServiceOrderService serviceOrderService;

//     @Autowired
//     WorkOrderService workOrderService;

//     @Autowired
//     PurchaseOrderService purchaseOrderService;

//     @Autowired
//     SubWorkflowTransitionRepository subWorkflowTransitionRepository;

//     @Autowired
//     private IndentCreationRepository indentCreationRepository;
//     @Autowired
//     private TenderRequestRepository tenderRequestRepository;
//     @Autowired
//     private PurchaseOrderRepository purchaseOrderRepository;
//     @Autowired
//     private ContigencyPurchaseRepository contigencyPurchaseRepository;

//     @Autowired
//     private BranchWorkflowService branchWorkflowService;

//     @Autowired
//     @Lazy
//     private com.astro.service.JobMasterService jobMasterService;

//     @Autowired
//     private ServiceOrderRepository serviceOrderRepository;
//     @Autowired
//     private IndentIdRepository indentIdRepository;
//     @Autowired
//     private VendorMasterUtilService vendorMasterUtilService;

//     @Autowired
//     private MaterialMasterUtilService materialMasterUtilService;
//     @Autowired
//     private MaterialMasterUtilRepository materialMasterUtilRepository;
//     @Autowired
//     private UserMasterRepository userMasterRepository;
//     @Autowired
//     private ProjectMasterRepository projectMasterRepository;
//     @Autowired
//     private MaterialDetailsRepository materialDetailsRepo;

//     @Autowired
//     private EmailService emailService;
//     @Autowired
//     private TenderEmailService tenderEmailService;

//     @Autowired
//     private TenderRequestService TRService;
//     @Autowired
//     private VendorMasterRepository vendorMasterRepository;
//     @Autowired
//     private IndentIdRepository indentIdtenderIdsRepository;
//     @Autowired
//     private PaymentVoucherReposiotry paymentVoucherReposiotry;
//     // added b y abhinav
//     @Autowired
//     private IndentAssignmentRepository indentAssignmentRepository;

//     @Autowired
//     private EmployeeDepartmentMasterRepository employeeDepartmentMasterRepository;

//     @Autowired
//     private WorkflowBranchMasterRepository workflowBranchMasterRepository;

//     private final ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     public WorkflowDto workflowByWorkflowName(String workflowName) {
//         WorkflowDto workflowDto = null;

//         if (Objects.nonNull(workflowName)) {
//             WorkflowMaster workflowMaster = workflowMasterRepository.findByWorkflowName(workflowName);
//             if (Objects.nonNull(workflowMaster)) {
//                 workflowDto = new WorkflowDto();
//                 workflowDto.setWorkflowId(workflowMaster.getWorkflowId());
//                 workflowDto.setWorkflowName(workflowMaster.getWorkflowName());
//                 workflowDto.setCreatedBy(workflowMaster.getCreatedBy());
//                 workflowDto.setCreatedDate(workflowMaster.getCreatedDate());
//             } else {
//                 throw new InvalidInputException(new ErrorDetails(AppConstant.WORKFLOW_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                         AppConstant.ERROR_TYPE_VALIDATION, "Workflow not found."));
//             }
//         } else {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Invalid input."));
//         }

//         return workflowDto;
//     }

//     @Override
//     public List<TransitionDto> transitionsByWorkflowId(Integer workflowId) {

//         List<TransitionDto> transitionDtoList = new ArrayList<>();
//         List<TransitionMaster> transitionMasterList = transitionMasterRepository.findByWorkflowId(workflowId);

//         if (Objects.nonNull(transitionMasterList) && !transitionMasterList.isEmpty()) {
//             transitionDtoList = transitionMasterList.stream().map(transitionMaster -> {
//                 TransitionDto transitionDto = new TransitionDto();
//                 transitionDto.setWorkflowId(transitionMaster.getWorkflowId());
//                 transitionDto.setTransitionId(transitionMaster.getTransitionId());
//                 transitionDto.setTransitionSubOrder(transitionMaster.getTransitionSubOrder());
//                 transitionDto.setCreatedDate(transitionMaster.getCreatedDate());
//                 transitionDto.setCreatedBy(transitionMaster.getCreatedBy());
//                 transitionDto.setTransitionOrder(transitionMaster.getTransitionOrder());
//                 transitionDto.setConditionId(transitionMaster.getConditionId());
//                 transitionDto.setCurrentRoleId(transitionMaster.getCurrentRoleId());
//                 transitionDto.setNextRoleId(transitionMaster.getNextRoleId());
//                 transitionDto.setPreviousRoleId(transitionMaster.getPreviousRoleId());
//                 transitionDto.setTransitionName(transitionMaster.getTransitionName());
//                 transitionDto.setWorkflowName(workflowNameById(transitionMaster.getWorkflowId()));
//                 transitionDto.setCurrentRoleName(roleNameById(transitionMaster.getCurrentRoleId()));
//                 transitionDto.setNextRoleName(roleNameById(transitionMaster.getNextRoleId()));
//                 transitionDto.setPreviousRoleName(roleNameById(transitionMaster.getPreviousRoleId()));
//                 TransitionConditionDto transitionConditionDto = transitionConditionById(transitionMaster.getConditionId());
//                 transitionDto.setConditionKey(transitionConditionDto.getConditionKey());
//                 transitionDto.setConditionValue(transitionConditionDto.getConditionValue());

//                 return transitionDto;
//             }).collect(Collectors.toList());
//         } else {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.WORKFLOW_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Workflow not found."));
//         }
//         return transitionDtoList;
//     }

//     private TransitionConditionDto transitionConditionById(Integer conditionId) {
//         TransitionConditionDto transitionConditionDto = new TransitionConditionDto();
//         if (Objects.nonNull(conditionId)) {
//             TransitionConditionMaster transitionConditionMaster = transitionConditionMasterRepository.findById(conditionId).orElse(null);
//             if (Objects.nonNull(transitionConditionMaster)) {
//                 transitionConditionDto.setConditionId(transitionConditionMaster.getConditionId());
//                 transitionConditionDto.setConditionKey(transitionConditionMaster.getConditionKey());
//                 transitionConditionDto.setWorkflowId(transitionConditionMaster.getWorkflowId());
//                 transitionConditionDto.setConditionValue(transitionConditionMaster.getConditionValue());
//                 transitionConditionDto.setCreatedDate(transitionConditionMaster.getCreatedDate());
//                 transitionConditionDto.setCreatedBy(transitionConditionMaster.getCreatedBy());
//             }
//         }

//         return transitionConditionDto;
//     }

//     private String roleNameById(Integer roleId) {
//         if (Objects.nonNull(roleId)) {
//             return roleMasterRepository.findById(roleId).orElse(new RoleMaster()).getRoleName();
//         } else {
//             return null;
//         }
//     }

//     private String roleNameByUserId(Integer userId) {
//         if (Objects.nonNull(userId)) {
//             UserRoleMaster userRoleMaster = userRoleMasterRepository.findByUserId(userId);
//             if (Objects.nonNull(userRoleMaster)) {
//                 return roleMasterRepository.findById(userRoleMaster.getRoleId()).orElse(new RoleMaster()).getRoleName();
//             }
//         }
//         return null;
//     }

//     private String workflowNameById(Integer workflowId) {
//         if (Objects.nonNull(workflowId)) {
//             return workflowMasterRepository.findById(workflowId).orElse(new WorkflowMaster()).getWorkflowName();
//         } else {
//             return null;
//         }
//     }

//     @Override
//     public TransitionDto transitionsByWorkflowIdAndOrder(Integer workflowId, Integer order, Integer subOrder) {
//         TransitionDto transitionDto = null;
//         TransitionMaster transitionMaster = transitionMasterRepository.findByWorkflowIdAndTransitionOrderAndTransitionSubOrder(workflowId, order, subOrder);
//         if (Objects.nonNull(transitionMaster)) {
//             transitionDto = new TransitionDto();
//             transitionDto.setWorkflowId(transitionMaster.getWorkflowId());
//             transitionDto.setTransitionId(transitionMaster.getTransitionId());
//             transitionDto.setTransitionSubOrder(transitionMaster.getTransitionSubOrder());
//             transitionDto.setCreatedDate(transitionMaster.getCreatedDate());
//             transitionDto.setCreatedBy(transitionMaster.getCreatedBy());
//             transitionDto.setTransitionOrder(transitionMaster.getTransitionOrder());
//             transitionDto.setConditionId(transitionMaster.getConditionId());
//             transitionDto.setCurrentRoleId(transitionMaster.getCurrentRoleId());
//             transitionDto.setNextRoleId(transitionMaster.getNextRoleId());
//             transitionDto.setPreviousRoleId(transitionMaster.getPreviousRoleId());
//             transitionDto.setTransitionName(transitionMaster.getTransitionName());
//             transitionDto.setWorkflowName(workflowNameById(transitionMaster.getWorkflowId()));
//             transitionDto.setCurrentRoleName(roleNameById(transitionMaster.getCurrentRoleId()));
//             transitionDto.setNextRoleName(roleNameById(transitionMaster.getNextRoleId()));
//             transitionDto.setPreviousRoleName(roleNameById(transitionMaster.getPreviousRoleId()));
//             TransitionConditionDto transitionConditionDto = transitionConditionById(transitionMaster.getConditionId());
//             transitionDto.setConditionKey(transitionConditionDto.getConditionKey());
//             transitionDto.setConditionValue(transitionConditionDto.getConditionValue());
//         }
//         return transitionDto;
//     }

//     @Override
//     @Transactional
//     public WorkflowTransitionDto initiateWorkflow(String requestId, String workflowName, Integer createdBy) {
//         System.err.println("🚀🚀🚀 WORKFLOW INITIATION - RequestID: " + requestId + ", Workflow: " + workflowName);
//         WorkflowTransitionDto workflowTransitionDto = null;
//         if (Objects.nonNull(requestId) && Objects.nonNull(workflowName) && Objects.nonNull(createdBy)) {
//             userService.validateUser(createdBy);
//             WorkflowDto workflowDto = workflowByWorkflowName(workflowName);

//             // validateWorkflowTransition(requestId, createdBy, workflowDto.getWorkflowId());
//             //updated by abhinav
//             List<WorkflowTransition> existingList
//                     = workflowTransitionRepository.findByWorkflowIdAndCreatedByAndRequestId(
//                             workflowDto.getWorkflowId(), createdBy, requestId);

//             WorkflowTransition existing = existingList.isEmpty() ? null : existingList.get(0);

//             if (existing != null) {
//                 // Workflow already initiated, return existing
//                 return mapToWorkflowTransitionDto(existing);
//             }

//             // ===== BRANCH-BASED WORKFLOW ROUTING =====
//             WorkflowTransition workflowTransition = initiateBranchBasedWorkflow(requestId, workflowDto, createdBy);

//             if (workflowTransition == null) {
//                 // Fallback to old TransitionMaster system if no branch found
//                 System.err.println("⚠️⚠️⚠️ NO BRANCH MATCHED - Falling back to old TransitionMaster system for " + requestId);
//                 System.err.println("⚠️ Check: 1) Active branches exist, 2) Conditions match request data, 3) Data types match");
//                 TransitionDto transitionDto = transitionsByWorkflowIdAndOrder(workflowDto.getWorkflowId(), 1, 1);
//                 if (Objects.isNull(transitionDto)) {
//                     throw new InvalidInputException(new ErrorDetails(AppConstant.TRANSITION_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                             AppConstant.ERROR_TYPE_VALIDATION, "Transition not found."));
//                 }
//                 workflowTransition = createWorkflowTransition(requestId, workflowDto, transitionDto, createdBy);
//             } else {
//                 System.out.println("BRANCH-BASED WORKFLOW - Branch ID: " + workflowTransition.getBranchId()
//                         + ", Next: " + workflowTransition.getNextRole());
//             }

//             workflowTransitionRepository.save(workflowTransition);
//             workflowTransitionDto = mapWorkflowTransitionDto(workflowTransition);

//             if (WorkflowName.TENDER_EVALUATOR.getValue().equalsIgnoreCase(workflowName)) {
//                 workflowTransition.setModifiedBy(createdBy);
//                 List<SubWorkflowTransitionDto> list = validateTenderWorkFlow(null, workflowTransition, null);
//                 list.forEach(dto -> {
//                     if (dto != null) {
//                         try {
//                             emailService.sendSubWorkflowEmail(dto);
//                         } catch (Exception e) {
//                             // log error
//                         }
//                     }
//                 });
//             }

//         } else {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Invalid input."));
//         }

//         if (workflowTransitionDto != null) {
//             try {
//                 emailService.sendWorkflowEmail(workflowTransitionDto);
//             } catch (Exception e) {
//                 // log error
//             }
//         }
//         return workflowTransitionDto;
//     }

//     /**
//      * Checks if the given role name represents a Reporting Officer approver
//      * type.
//      */
//     private boolean isReportingOfficerRole(String roleName) {
//         return roleName != null && roleName.trim().equalsIgnoreCase("Reporting Officer");
//     }

//     /**
//      * Resolves the reporting officer's user ID for the given indent creator.
//      * Flow: creatorUserId -> UserMaster.employeeId ->
//      * EmployeeDepartmentMaster.reportingOfficerId -> UserMaster.userId Returns
//      * a map with keys: userId (Integer), employeeId (String), employeeName
//      * (String) Throws BusinessException if the reporting officer or their user
//      * account cannot be found.
//      */
//     private Map<String, Object> resolveReportingOfficer(Integer creatorUserId) {
//         // Step 1: Find the creator's user record to get their employeeId
//         UserMaster creatorUser = userMasterRepository.findByUserId(creatorUserId);
//         if (creatorUser == null || creatorUser.getEmployeeId() == null || creatorUser.getEmployeeId().trim().isEmpty()) {
//             throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                     "Cannot resolve reporting officer: Indent creator (userId=" + creatorUserId
//                     + ") does not have a linked employee record."));
//         }

//         // Step 2: Find the creator's employee record to get the reporting officer's employeeId
//         EmployeeDepartmentMaster creatorEmployee = employeeDepartmentMasterRepository
//                 .findByEmployeeId(creatorUser.getEmployeeId())
//                 .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                 AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                 "Cannot resolve reporting officer: Employee record not found for employeeId="
//                 + creatorUser.getEmployeeId())));

//         String reportingOfficerEmpId = creatorEmployee.getReportingOfficerId();
//         if (reportingOfficerEmpId == null || reportingOfficerEmpId.trim().isEmpty()) {
//             throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                     "Cannot resolve reporting officer: No reporting officer assigned to employee "
//                     + creatorEmployee.getEmployeeName() + " (" + creatorEmployee.getEmployeeId() + ")."));
//         }

//         // Step 3: Find the reporting officer's employee record (for name)
//         EmployeeDepartmentMaster reportingOfficerEmployee = employeeDepartmentMasterRepository
//                 .findByEmployeeId(reportingOfficerEmpId)
//                 .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                 AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                 "Cannot resolve reporting officer: Employee record not found for reporting officer employeeId="
//                 + reportingOfficerEmpId)));

//         // Step 4: Find the reporting officer's user account
//         UserMaster reportingOfficerUser = userMasterRepository.findByEmployeeId(reportingOfficerEmpId)
//                 .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                 AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                 "Cannot resolve reporting officer: No user account found for reporting officer "
//                 + reportingOfficerEmployee.getEmployeeName() + " (" + reportingOfficerEmpId + "). "
//                 + "Please create a user account for this employee.")));

//         Map<String, Object> result = new HashMap<>();
//         result.put("userId", reportingOfficerUser.getUserId());
//         result.put("employeeId", reportingOfficerEmpId);
//         result.put("employeeName", reportingOfficerEmployee.getEmployeeName());

//         System.out.println("✅ Resolved reporting officer for creator userId=" + creatorUserId
//                 + " → " + reportingOfficerEmployee.getEmployeeName() + " (userId=" + reportingOfficerUser.getUserId() + ")");

//         return result;
//     }

//     /**
//      * Checks if the given role name represents a Project Head approver type.
//      */
//     private boolean isProjectHeadRole(String roleName) {
//         return roleName != null && roleName.trim().equalsIgnoreCase("Project Head");
//     }

//     /**
//      * Resolves the project head's user ID for the given indent request. Flow:
//      * requestId -> IndentCreation.projectCode -> ProjectMaster.projectHead
//      * (employeeId) -> UserMaster.userId Returns a map with keys: userId
//      * (Integer), employeeId (String), employeeName (String) Throws
//      * BusinessException if the project head or their user account cannot be
//      * found.
//      */
//     private Map<String, Object> resolveProjectHead(String requestId) {
//         // Step 1: Find the indent to get the project code
//         IndentCreation indent = indentCreationRepository.findByIndentId(requestId);
//         if (indent == null) {
//             throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                     "Cannot resolve project head: Indent not found for requestId=" + requestId));
//         }

//         if (indent.getProjectCode() == null || indent.getProjectCode().trim().isEmpty()) {
//             throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                     "Cannot resolve project head: Indent " + requestId + " is not associated with any project."));
//         }

//         // Step 2: Find the project master to get the project head's employee ID
//         ProjectMaster project = projectMasterRepository.findByProjectCode(indent.getProjectCode())
//                 .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                 AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                 "Cannot resolve project head: Project not found for projectCode=" + indent.getProjectCode())));

//         String projectHeadEmpId = project.getProjectHead();
//         if (projectHeadEmpId == null || projectHeadEmpId.trim().isEmpty()) {
//             throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                     "Cannot resolve project head: No project head assigned to project "
//                     + project.getProjectNameDescription() + " (" + project.getProjectCode() + ")."));
//         }

//         // Step 3: Find the project head's employee record (for name)
//         String projectHeadName = project.getProjectHeadName();
//         if (projectHeadName == null || projectHeadName.trim().isEmpty()) {
//             // Fallback: look up from employee table
//             EmployeeDepartmentMaster projectHeadEmployee = employeeDepartmentMasterRepository
//                     .findByEmployeeId(projectHeadEmpId)
//                     .orElse(null);
//             if (projectHeadEmployee != null) {
//                 projectHeadName = projectHeadEmployee.getEmployeeName();
//             } else {
//                 projectHeadName = projectHeadEmpId;
//             }
//         }

//         // Step 4: Find the project head's user account
//         UserMaster projectHeadUser = userMasterRepository.findByEmployeeId(projectHeadEmpId)
//                 .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                 AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                 "Cannot resolve project head: No user account found for project head "
//                 + project.getProjectHeadName() + " (" + projectHeadEmpId + "). "
//                 + "Please create a user account for this employee.")));

//         Map<String, Object> result = new HashMap<>();
//         result.put("userId", projectHeadUser.getUserId());
//         result.put("employeeId", projectHeadEmpId);
//         result.put("employeeName", projectHeadName);

//         System.out.println("Resolved project head for requestId=" + requestId
//                 + " → " + projectHeadName + " (userId=" + projectHeadUser.getUserId() + ")");

//         return result;
//     }

//     /**
//      * Checks if the given role name is a location-based role (Professor In
//      * Charge or Engineer In Charge). These roles are resolved based on the
//      * indent's consignee location matching the employee's location.
//      */
//     private boolean isLocationBasedRole(String roleName) {
//         if (roleName == null) {
//             return false;
//         }
//         String trimmed = roleName.trim().toLowerCase();
//         return trimmed.equals("professor in charge") || trimmed.equals("engineer in charge");
//     }

//     /**
//      * Resolves the location-based approver (Professor In Charge or Engineer In
//      * Charge) for the given indent. Flow: requestId ->
//      * IndentCreation.consignesLocation -> find User with matching role AND
//      * employee location Returns a map with keys: userId (Integer), employeeId
//      * (String), employeeName (String) Throws BusinessException if no matching
//      * user is found.
//      */
//     private Map<String, Object> resolveLocationBasedApprover(String requestId, String roleName) {
//         // Step 1: Find the indent to get the consignee location
//         IndentCreation indent = indentCreationRepository.findByIndentId(requestId);
//         if (indent == null) {
//             throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                     "Cannot resolve " + roleName + ": Indent not found for requestId=" + requestId));
//         }

//         String location = indent.getConsignesLocation();
//         if (location == null || location.trim().isEmpty()) {
//             throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                     "Cannot resolve " + roleName + ": Indent " + requestId + " has no consignee location set."));
//         }

//         // Step 2: Find user with the given role whose employee is at the same location
//         UserMaster matchingUser = userMasterRepository.findByRoleNameAndEmployeeLocation(roleName, location)
//                 .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
//                 AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
//                 "Cannot resolve " + roleName + ": No user with role '" + roleName
//                 + "' found at location '" + location + "'. "
//                 + "Please ensure an employee at this location has a user account with the '" + roleName + "' role.")));

//         // Step 3: Get the employee name for display
//         String employeeName = roleName;
//         if (matchingUser.getEmployeeId() != null) {
//             EmployeeDepartmentMaster employee = employeeDepartmentMasterRepository
//                     .findByEmployeeId(matchingUser.getEmployeeId())
//                     .orElse(null);
//             if (employee != null) {
//                 employeeName = employee.getEmployeeName();
//             }
//         }

//         Map<String, Object> result = new HashMap<>();
//         result.put("userId", matchingUser.getUserId());
//         result.put("employeeId", matchingUser.getEmployeeId());
//         result.put("employeeName", employeeName);

//         System.out.println("Resolved " + roleName + " for requestId=" + requestId
//                 + " (location=" + location + ") → " + employeeName + " (userId=" + matchingUser.getUserId() + ")");

//         return result;
//     }

//     /**
//      * BRANCH-BASED WORKFLOW INITIATION Matches workflow branch based on
//      * conditions and routes to first approver
//      */
//     private WorkflowTransition initiateBranchBasedWorkflow(String requestId, WorkflowDto workflowDto, Integer createdBy) {
//         try {
//             System.out.println("Initiating workflow for " + requestId);
//             System.out.println(" WORKFLOW NAME RECEIVED: " + workflowDto.getWorkflowName()); // updated by abhinav

//             // Build conditions based on workflow type
//             Map<String, Object> conditions = buildConditionsForWorkflow(workflowDto.getWorkflowName(), requestId);

//             // Resolve indentorDepartment BEFORE branch matching so department-filtered branches work correctly.
//             // Without this, branches with "indentorDepartment" conditions (e.g. B46 for Tech dept)
//             // would never match because the dept is null at the time findMatchingBranch runs.
//             String indentorDepartment = (String) conditions.get("indentorDepartment");
//             if ((indentorDepartment == null || indentorDepartment.trim().isEmpty()) && createdBy != null) {
//                 try {
//                     com.astro.entity.UserMaster creatorUser = userMasterRepository.findByUserId(createdBy);
//                     if (creatorUser != null && creatorUser.getEmployeeId() != null) {
//                         employeeDepartmentMasterRepository.findByEmployeeId(creatorUser.getEmployeeId())
//                                 .ifPresent(emp -> {
//                                     conditions.put("indentorDepartment", emp.getDepartmentName());
//                                     System.out.println("✅ Resolved indentorDepartment (pre-branch-match fallback): "
//                                             + emp.getDepartmentName());
//                                 });
//                         indentorDepartment = (String) conditions.get("indentorDepartment");
//                     }
//                 } catch (Exception e) {
//                     System.err.println("⚠️ Could not resolve indentorDepartment fallback: " + e.getMessage());
//                 }
//             }

//             System.out.println("📋 Conditions: " + conditions);

//             // Find matching branch (now with resolved department info)
//             com.astro.entity.AdminPanel.WorkflowBranchMaster matchedBranch
//                     = branchWorkflowService.findMatchingBranch(workflowDto.getWorkflowId(), conditions);

//             if (matchedBranch == null) {
//                 System.out.println("⚠️ No matching branch found for " + workflowDto.getWorkflowName());
//                 return null;
//             }

//             System.out.println("🔥 MATCHED BRANCH:");
//             System.out.println("   BranchId: " + matchedBranch.getBranchId());
//             System.out.println("   BranchCode: " + matchedBranch.getBranchCode());

//             Object totalAmountObj = conditions.get("totalAmount");
//             java.math.BigDecimal totalAmount = totalAmountObj != null
//                     ? new java.math.BigDecimal(totalAmountObj.toString())
//                     : java.math.BigDecimal.ZERO;

//             com.astro.entity.AdminPanel.ApproverMaster firstApprover
//                     = (indentorDepartment != null && !indentorDepartment.trim().isEmpty())
//                     ? branchWorkflowService.getFirstApproverForDepartment(matchedBranch.getBranchId(), indentorDepartment, totalAmount)
//                     : branchWorkflowService.getFirstApprover(matchedBranch.getBranchId());

//             if (firstApprover == null) {
//                 System.out.println("⚠️ No approvers configured for branch " + matchedBranch.getBranchCode());
//                 return null;
//             }

//             // Create workflow transition with branch info
//             WorkflowTransition transition = new WorkflowTransition();
//             transition.setRequestId(requestId);
//             transition.setWorkflowId(workflowDto.getWorkflowId());
//             transition.setWorkflowName(workflowDto.getWorkflowName());
//             transition.setCreatedBy(createdBy);
//             transition.setCreatedDate(new Date());
//             transition.setStatus(AppConstant.CREATED_TYPE);
//             transition.setAction(AppConstant.CREATED_TYPE);
//             // transition.setCurrentRole("Indent Creator");
//             // updated by abhinav
//             if (workflowDto.getWorkflowName().toUpperCase().contains("TENDER")) {
//                 transition.setCurrentRole("Tender Creator");
//             } else if (workflowDto.getWorkflowName().toUpperCase().contains("INDENT")) {
//                 transition.setCurrentRole("Indent Creator");
//             } else {
//                 transition.setCurrentRole("Request Creator");
//             }
//             // updated by abhinav end here
//             System.out.println("🔥 FIRST APPROVER DEBUG:");
//             System.out.println("   Role: " + firstApprover.getRoleName());
//             System.out.println("   Level: " + firstApprover.getApprovalLevel());
//             System.out.println("   Sequence: " + firstApprover.getApprovalSequence());
//             System.out.println("   BranchId: " + matchedBranch.getBranchId());

//             transition.setNextRole(firstApprover.getRoleName());
//             transition.setNextAction(AppConstant.PENDING_TYPE);
//             transition.setTransitionOrder(1);
//             transition.setTransitionSubOrder(1);
//             transition.setWorkflowSequence(1);

//             // Set branch-based fields
//             transition.setBranchId(matchedBranch.getBranchId());
//             transition.setApproverId(firstApprover.getApproverId());
//             transition.setApprovalLevel(firstApprover.getApprovalLevel());
//             transition.setApprovalSequence(firstApprover.getApprovalSequence());

//             // Reporting Officer: resolve and assign to specific user
//             if (isReportingOfficerRole(firstApprover.getRoleName())) {
//                 Map<String, Object> roInfo = resolveReportingOfficer(createdBy);
//                 transition.setAssignedToUserId((Integer) roInfo.get("userId"));
//                 transition.setAssignedToEmployeeId((String) roInfo.get("employeeId"));
//                 System.out.println("Reporting Officer assigned: " + roInfo.get("employeeName")
//                         + " (userId=" + roInfo.get("userId") + ")");
//             }

//             // Project Head: resolve and assign to specific project head user
//             if (isProjectHeadRole(firstApprover.getRoleName())) {
//                 Map<String, Object> phInfo = resolveProjectHead(requestId);
//                 transition.setAssignedToUserId((Integer) phInfo.get("userId"));
//                 transition.setAssignedToEmployeeId((String) phInfo.get("employeeId"));
//                 System.out.println("Project Head assigned: " + phInfo.get("employeeName")
//                         + " (userId=" + phInfo.get("userId") + ")");
//             }

//             // Location-based roles (Professor In Charge / Engineer In Charge):
//             // resolve by matching consignee location with employee location
//             if (isLocationBasedRole(firstApprover.getRoleName())) {
//                 Map<String, Object> locInfo = resolveLocationBasedApprover(requestId, firstApprover.getRoleName());
//                 transition.setAssignedToUserId((Integer) locInfo.get("userId"));
//                 transition.setAssignedToEmployeeId((String) locInfo.get("employeeId"));
//                 System.out.println(firstApprover.getRoleName() + " assigned: " + locInfo.get("employeeName")
//                         + " (userId=" + locInfo.get("userId") + ")");
//             }

//             System.out.println("✅ Workflow initiated with branch: " + matchedBranch.getBranchCode()
//                     + " (ID: " + matchedBranch.getBranchId() + ")"
//                     + ", First approver: " + firstApprover.getRoleName()
//                     + " (Level: " + firstApprover.getApprovalLevel()
//                     + ", Seq: " + firstApprover.getApprovalSequence() + ")");

//             return transition;

//         } catch (BusinessException e) {
//             // Re-throw business exceptions (e.g., reporting officer not found)
//             throw e;
//         } catch (Exception e) {
//             System.err.println("❌ Error in branch-based workflow initiation: " + e.getMessage());
//             e.printStackTrace();
//             return null;
//         }
//     }

//     private Map<String, Object> buildConditionsForWorkflow(String workflowName, String requestId) {
//         String workflowNameUpper = workflowName.toUpperCase();
//         if (workflowNameUpper.contains("INDENT")) {
//             return branchWorkflowService.buildIndentConditions(requestId);
//             // added by abhinav new from here
//         } //  else if (workflowNameUpper.contains("TENDER")) {
//         //     return branchWorkflowService.buildTenderConditions(requestId);
//         // } else if (workflowNameUpper.contains("PURCHASE") || workflowNameUpper.contains("PO")) {
//         //     return branchWorkflowService.buildPOConditions(requestId);
//         // } else if (workflowNameUpper.contains("CONTINGENCY")) {
//         //     return branchWorkflowService.buildPOConditions(requestId);
//         // }
//         // if ("INDENT APPROVAL WORKFLOW".equalsIgnoreCase(workflowName)) {
//         //     return branchWorkflowService.buildIndentConditions(requestId);
//         // } 
//         else if (workflowNameUpper.contains("TENDER")
//                 && workflowNameUpper.contains("APPROVER")) {

//             return branchWorkflowService.buildTenderConditions(requestId);
//             // Modified by Aman changed Purchase to PO 
//         } else if (workflowNameUpper.contains("PO")) {
 
//  return branchWorkflowService.buildPOConditions(requestId);
//         } else if (workflowNameUpper.contains("SO")){
//             return branchWorkflowService.buildSOConditions(requestId);
//         } else if (workflowNameUpper.contains("Payment")){
//             return branchWorkflowService.buildPaymentConditions(requestId);
//         }

//         System.out.println("❌ No matching workflow found for: " + workflowName);
//         return new HashMap<>();
//     }

//     // private void validateWorkflowTransition(String requestId, Integer createdBy, Integer workflowId) {
//     //     WorkflowTransition workflowTransition = workflowTransitionRepository.findByWorkflowIdAndCreatedByAndRequestId(workflowId, createdBy, requestId);
//     //     if (Objects.nonNull(workflowTransition)) {
//     //         throw new InvalidInputException(new ErrorDetails(AppConstant.WORKFLOW_ALREADY_EXISTS, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//     //                 AppConstant.ERROR_TYPE_VALIDATION, "Workflow with same request id and created by already exists."));
//     //     }
//     // }
//     @Override
//     public List<WorkflowTransitionDto> workflowTransitionHistory(String requestId) {

//         List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();
//         List<WorkflowTransition> workflowTransitionList = null;
//         workflowTransitionList = workflowTransitionRepository.findByRequestId(requestId);
//         if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
//             workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowSequence).reversed()).map(e -> {
//                 return mapWorkflowTransitionDto(e);
//             }).collect(Collectors.toList());
//         }

//         return workflowTransitionDtoList;
//     }

//     @Override
//     public List<WorkflowTransitionDto> allWorkflowTransition(String roleName) {
//         List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();

//         List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByNextRole(roleName);
//         if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
//             workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate)).map(e -> {
//                 return mapWorkflowTransitionDto(e);
//             }).collect(Collectors.toList());
//         }
//         return workflowTransitionDtoList;
//     }

//     @Override
//     public List<WorkflowTransitionDto> allPendingWorkflowTransition(String roleName) {
//         return allPendingWorkflowTransition(roleName, null);
//     }

//     @Override
//     public List<WorkflowTransitionDto> allPendingWorkflowTransition(String roleName, Integer userId) {
//         List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();

//         List<WorkflowTransition> workflowTransitionList;
//         if (userId != null) {
//             workflowTransitionList = workflowTransitionRepository.findPendingByRoleAndOptionalUser(AppConstant.PENDING_TYPE, roleName, userId);
//         } else {
//             workflowTransitionList = workflowTransitionRepository.findByNextActionAndNextRole(AppConstant.PENDING_TYPE, roleName);
//         }

//         // Filter by user's employee location against branch location condition
//         if (userId != null && workflowTransitionList != null && !workflowTransitionList.isEmpty()) {
//             UserMaster transUser = userMasterRepository.findByUserId(userId);
//             if (transUser != null && transUser.getEmployeeId() != null) {
//                 EmployeeDepartmentMaster transEmp = employeeDepartmentMasterRepository.findByEmployeeId(transUser.getEmployeeId()).orElse(null);
//                 if (transEmp != null && transEmp.getLocation() != null) {
//                     String userLocation = transEmp.getLocation().trim().toUpperCase();
//                     workflowTransitionList = workflowTransitionList.stream()
//                             .filter(wt -> {
//                                 if (wt.getBranchId() == null) {
//                                     return true;
//                                 }
//                                 return workflowBranchMasterRepository.findById(wt.getBranchId())
//                                         .map(branch -> {
//                                             if (branch.getConditionConfig() == null || branch.getConditionConfig().trim().isEmpty()) {
//                                                 return true;
//                                             }
//                                             try {
//                                                 Map<String, Object> config = objectMapper.readValue(branch.getConditionConfig(), new TypeReference<Map<String, Object>>() {
//                                                 });
//                                                 if (!config.containsKey("location")) {
//                                                     return true;
//                                                 }
//                                                 String branchLocation = String.valueOf(config.get("location")).trim().toUpperCase();
//                                                 return branchLocation.equals(userLocation);
//                                             } catch (Exception e) {
//                                                 return true;
//                                             }
//                                         })
//                                         .orElse(true);
//                             })
//                             .collect(Collectors.toList());
//                 }
//             }
//         }

//         if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
//             workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate)).map(e -> {
//                 return mapWorkflowTransitionDto(e);
//             }).collect(Collectors.toList());
//         }

//         return workflowTransitionDtoList;
//     }

//     /*   @Override
//        public List<QueueResponse> allCompletedWorkflowTransition(String roleName) {
//            List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();
//            List<QueueResponse> queueResponseList = new ArrayList<>();

//            int workflowId =1;
//         //   List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByStatusAndWorkflowId(AppConstant.COMPLETED_TYPE, workflowId);
//         /*   if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
//                workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate)).map(e -> {
//                    return mapWorkflowTransitionDto(e);
//                }).collect(Collectors.toList());
//            }*

//      */ /* List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findValidTransitions(AppConstant.COMPLETED_TYPE, workflowId);

//         if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
//             queueResponseList = workflowTransitionList.stream()
//                     .sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate))
//                     .map(this::mapToQueueResponse)
//                     .collect(Collectors.toList());
//         }

//         return queueResponseList;
//     }*/
//  /*public List<QueueResponse> allCompletedWorkflowTransition(String roleName) {
//      int workflowId = 1;

//      return workflowTransitionRepository.findValidTransitions(AppConstant.COMPLETED_TYPE, workflowId)
//              .stream()
//              .filter(transition -> !indentCreationRepository.isAssigned(transition.getRequestId())) // only not assigned
//              .sorted(Comparator.comparing(WorkflowTransition::getRequestId)
//                      .thenComparing(WorkflowTransition::getCreatedDate))
//              .map(this::mapToQueueResponse)
//              .collect(Collectors.toList());
//  }*/
//  /*
//  public List<QueueResponse> allCompletedWorkflowTransition(String roleName) {
//      int workflowId = 1; // fetch dynamically if needed
//      return workflowTransitionRepository.findValidTransitions(AppConstant.COMPLETED_TYPE, workflowId)
//              .stream()
//              .sorted(Comparator.comparing(WorkflowTransition::getRequestId)
//                      .thenComparing(WorkflowTransition::getCreatedDate))
//              .map(this::mapToQueueResponse)
//              .collect(Collectors.toList());

//  }*/
//     public List<CompletedIndentsQueueResponse> allCompletedWorkflowTransition(String roleName) {
//         int workflowId = 1; // fetch dynamically if needed
//         return workflowTransitionRepository.findCompletedIndents(AppConstant.COMPLETED_TYPE, workflowId);
//     }

//     public List<QueueResponse> allCancelledIndents() {
//         List<IndentCreation> cancelledIndents = indentCreationRepository.findAllByCancelStatusTrue();

//         List<QueueResponse> responses = cancelledIndents.stream()
//                 .filter(indent -> {
//                     // Fetch the latest workflow transition by workflowTransitionId
//                     Optional<WorkflowTransition> lastTransitionOpt
//                             = workflowTransitionRepository.findFirstByRequestIdOrderByWorkflowTransitionIdDesc(indent.getIndentId());

//                     // Include only if last transition is NOT "Canceled"
//                     return lastTransitionOpt.map(t -> !"Canceled".equalsIgnoreCase(t.getStatus()))
//                             .orElse(true); // If no transition exists, include it
//                 })
//                 .map(indent -> {
//                     QueueResponse response = new QueueResponse();
//                     response.setRequestId(indent.getIndentId());
//                     response.setIndentorName(indent.getIndentorName());
//                     response.setAction("Indentor Cancelled");
//                     response.setStatus("Indentor Cancelled");
//                     response.setWorkflowName("Indent Workflow");
//                     response.setAmount(indent.getTotalIntentValue());
//                     response.setProjectName(indent.getProjectName());
//                     response.setModeOfProcurement("");
//                     response.setConsignee(indent.getConsignesLocation());
//                     response.setCreatedDate(new Date());
//                     response.setWorkflowId(1);
//                     return response;
//                 })
//                 .collect(Collectors.toList());

//         return responses;
//     }

//     /* @Override
//     public List<String> allPreviousRoleWorkflowTransition(Integer workflowId, String requestId) {
//         List<String> allPreviousRole = new ArrayList<>();

//         List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByWorkflowIdAndRequestId(workflowId, requestId);
//         if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty() && workflowTransitionList.size() > 1) {
//             allPreviousRole = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowTransitionId)).limit(workflowTransitionList.size() - 1).map(e -> e.getCurrentRole()).distinct().collect(Collectors.toList());
//         }
//         return allPreviousRole;
//     }
//      */
//     public List<String> allPreviousRoleWorkflowTransition(Integer workflowId, String requestId) {
//         List<String> allPreviousRole = new ArrayList<>();

//         List<WorkflowTransition> workflowTransitionList
//                 = workflowTransitionRepository.findByWorkflowIdAndRequestId(workflowId, requestId);

//         if (workflowTransitionList != null && !workflowTransitionList.isEmpty()) {
//             allPreviousRole = workflowTransitionList.stream()
//                     .sorted(Comparator.comparing(WorkflowTransition::getWorkflowTransitionId))
//                     .map(WorkflowTransition::getCurrentRole)
//                     .filter(Objects::nonNull)
//                     .distinct()
//                     .collect(Collectors.toList());
//         }

//         return allPreviousRole;
//     }

//     private WorkflowTransitionDto mapWorkflowTransitionDto(WorkflowTransition workflowTransition) {
//         WorkflowTransitionDto workflowTransitionDto = new WorkflowTransitionDto();
//         workflowTransitionDto.setWorkflowTransitionId(workflowTransition.getWorkflowTransitionId());
//         workflowTransitionDto.setTransitionId(workflowTransition.getTransitionId());
//         workflowTransitionDto.setWorkflowId(workflowTransition.getWorkflowId());
//         workflowTransitionDto.setWorkflowName(workflowTransition.getWorkflowName());
//         workflowTransitionDto.setModificationDate(workflowTransition.getModificationDate());
//         workflowTransitionDto.setCreatedBy(workflowTransition.getCreatedBy());
//         workflowTransitionDto.setTransitionOrder(workflowTransition.getTransitionOrder());
//         workflowTransitionDto.setRequestId(workflowTransition.getRequestId());
//         workflowTransitionDto.setStatus(workflowTransition.getStatus());
//         workflowTransitionDto.setTransitionSubOrder(workflowTransition.getTransitionSubOrder());
//         workflowTransitionDto.setCreatedDate(workflowTransition.getCreatedDate());
//         workflowTransitionDto.setModifiedBy(workflowTransition.getModifiedBy());
//         workflowTransitionDto.setNextAction(workflowTransition.getNextAction());
//         workflowTransitionDto.setCreatedRole(roleNameById(workflowTransition.getCreatedBy()));
//         workflowTransitionDto.setModifiedRole(roleNameById(workflowTransition.getModifiedBy()));
//         workflowTransitionDto.setCurrentRole(workflowTransition.getCurrentRole());
//         workflowTransitionDto.setNextRole(workflowTransition.getNextRole());
//         workflowTransitionDto.setWorkflowSequence(workflowTransition.getWorkflowSequence());
//         workflowTransitionDto.setAction(workflowTransition.getAction());
//         workflowTransitionDto.setRemarks(workflowTransition.getRemarks());

//         // For branch-based workflows, transitionId is null
//         if (workflowTransition.getTransitionId() != null) {
//             TransitionMaster transitionMaster = transitionById(workflowTransition.getTransitionId());
//             if (Objects.nonNull(transitionMaster)) {
//                 workflowTransitionDto.setNextActionId(transitionMaster.getNextRoleId());
//                 workflowTransitionDto.setNextActionRole(roleNameById(transitionMaster.getNextRoleId()));
//             }
//         }

//         // Reporting Officer assignment fields
//         workflowTransitionDto.setAssignedToUserId(workflowTransition.getAssignedToUserId());
//         workflowTransitionDto.setAssignedToEmployeeId(workflowTransition.getAssignedToEmployeeId());
//         if (workflowTransition.getAssignedToEmployeeId() != null) {
//             employeeDepartmentMasterRepository.findByEmployeeId(workflowTransition.getAssignedToEmployeeId())
//                     .ifPresent(emp -> workflowTransitionDto.setAssignedToEmployeeName(emp.getEmployeeName()));
//         }

//         return workflowTransitionDto;
//     }

//     private TransitionMaster transitionById(Integer transitionId) {
//         return transitionMasterRepository.findById(transitionId).orElse(null);
//     }

//     private WorkflowTransition createWorkflowTransition(String requestId, WorkflowDto workflowDto, TransitionDto transitionDto, Integer createdBy) {
//         WorkflowTransition workflowTransition = new WorkflowTransition();
//         workflowTransition.setTransitionId(transitionDto.getTransitionId());
//         workflowTransition.setWorkflowId(workflowDto.getWorkflowId());
//         workflowTransition.setTransitionOrder(transitionDto.getTransitionOrder());
//         workflowTransition.setTransitionSubOrder(transitionDto.getTransitionSubOrder());
//         workflowTransition.setStatus(AppConstant.CREATED_TYPE);
//         workflowTransition.setAction(AppConstant.CREATED_TYPE);
//         workflowTransition.setNextAction(AppConstant.PENDING_TYPE);
//         workflowTransition.setCreatedDate(new Date());
//         workflowTransition.setCreatedBy(createdBy);
//         workflowTransition.setModifiedBy(null);
//         workflowTransition.setModificationDate(null);
//         workflowTransition.setRequestId(requestId);
//         workflowTransition.setWorkflowName(workflowDto.getWorkflowName());
//         workflowTransition.setCurrentRole(transitionDto.getCurrentRoleName());
//         workflowTransition.setNextRole(transitionDto.getNextRoleName());
//         workflowTransition.setWorkflowSequence(1);

//         return workflowTransition;
//     }

//     @Override
//     public TransitionDto nextTransition(Integer workflowId, String workflowName, String currentRole, String requestId) {
//         TransitionDto transitionDto = null;

//         if (Objects.nonNull(workflowId) && Objects.nonNull(currentRole)) {
//             List<TransitionDto> nextTransitionDtoList = transitionsByWorkflowId(workflowId).stream().filter(e -> currentRole.equalsIgnoreCase(e.getCurrentRoleName())).sorted(Comparator.comparing(s -> s.getTransitionSubOrder())).collect(Collectors.toList());
//             transitionDto = nextTransitionDto(nextTransitionDtoList, workflowName, requestId);
//         } else {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Invalid input."));
//         }
//         return transitionDto;
//     }

//     @Override
//     @Transactional
//     public WorkflowTransitionDto performTransitionAction(TransitionActionReqDto transitionActionReqDto) {
//         userService.validateUser(transitionActionReqDto.getActionBy());
//         WorkflowTransition workflowTransition = workflowTransitionRepository.findByWorkflowTransitionIdAndRequestId(transitionActionReqDto.getWorkflowTransitionId(), transitionActionReqDto.getRequestId());
//         if (Objects.isNull(workflowTransition)) {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.INVALID_WORKFLOW_TRANSITION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Workflow transition not found.With given workflow transition id and request id."));
//         }

//         // For branch-based workflows, transitionId is null
//         TransitionMaster currentTransition = null;
//         if (workflowTransition.getTransitionId() != null) {
//             currentTransition = transitionMasterRepository.findById(workflowTransition.getTransitionId()).orElse(null);
//         }

//         // Only validate user role for legacy (non-branch) workflows
//         if (currentTransition != null) {
//             if (workflowTransition.getWorkflowId() == 7 && workflowTransition.getCurrentRole().equalsIgnoreCase("Tender Evaluator") && workflowTransition.getNextRole().equalsIgnoreCase("Tender Evaluator")
//                     || workflowTransition.getWorkflowId() == 1 && workflowTransition.getAction().equalsIgnoreCase("Change requested") && workflowTransition.getNextRole().equalsIgnoreCase("Indent Creator")
//                     || workflowTransition.getWorkflowId() == 3 && workflowTransition.getAction().equalsIgnoreCase("Change requested") && workflowTransition.getNextRole().equalsIgnoreCase("PO Creator")
//                     || workflowTransition.getWorkflowId() == 4 && workflowTransition.getAction().equalsIgnoreCase("Change requested") && workflowTransition.getNextRole().equalsIgnoreCase("Tender Creator")) {
//                 validateUserRole(transitionActionReqDto.getActionBy(), currentTransition.getCurrentRoleId());
//             } else {
//                 validateUserRole(transitionActionReqDto.getActionBy(), currentTransition.getNextRoleId());
//             }
//         }
//         /* if (AppConstant.COMPLETED_TYPE.equalsIgnoreCase(workflowTransition.getStatus())) {
//             throw new BusinessException(new ErrorDetails(AppConstant.INVALID_ACTION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Workflow already completed."));
//         }*/
//         if (AppConstant.COMPLETED_TYPE.equalsIgnoreCase(workflowTransition.getStatus())
//                 && !AppConstant.REJECT_TYPE.equalsIgnoreCase(transitionActionReqDto.getAction())) {
//             throw new BusinessException(new ErrorDetails(AppConstant.INVALID_ACTION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Workflow already completed."));
//         }

//         if (AppConstant.APPROVE_TYPE.equalsIgnoreCase(transitionActionReqDto.getAction())) {
//             WorkflowTransitionDto wt = approveTransition(workflowTransition, currentTransition, transitionActionReqDto);
//             if (wt != null) {
//                 try {
//                     emailService.sendWorkflowEmail(wt); // @Async method
//                     /*  if ("Tender Approver".equals(wt.getCurrentRole())) {
//                         tenderEmailService.handleTenderApproverEmail(wt);
//                     }*/
//                     if (wt.getWorkflowName().equalsIgnoreCase("PO Workflow") && (wt.getStatus().equalsIgnoreCase("In-progress") || wt.getStatus().equalsIgnoreCase("Completed"))) {
//                         PoFormateDto poData = purchaseOrderService.getPoFormatDetails(wt.getRequestId());
//                         // String purchaseDeptMail ="kudaykiran.9949@gmail.com";  //change. get the purchase dept mail from db
//                         //updated by abhinav
//                         String purchaseDeptMail = userMasterRepository
//                                 .findEmailByRoleName("Purchase Dept");
//                         tenderEmailService.handlePoApproverEmail(poData, purchaseDeptMail);
//                     }
//                     if ("Tender Approver".equals(wt.getCurrentRole())) {
//                         TenderWithIndentResponseDTO tenderData = TRService.getTenderRequestById(wt.getRequestId());

//                         Set<String> vendorIds = new HashSet<>();
//                         for (IndentCreationResponseDTO indent : tenderData.getIndentResponseDTO()) {
//                             for (MaterialDetailsResponseDTO material : indent.getMaterialDetails()) {
//                                 if (material.getVendorNames() != null) {
//                                     vendorIds.addAll(material.getVendorNames());
//                                 }
//                             }
//                         }
//                         /*
//                         Map<String, String> vendorEmailMap = new HashMap<>();
//                         for (String vendorId : vendorIds) {
//                             vendorMasterRepository.findById(vendorId).ifPresent(vendor -> {
//                                 vendorEmailMap.put(vendorId, vendor.getEmailAddress());
//                             });
//                         }

//                         tenderEmailService.handleTenderApproverEmail(wt.getRequestId(), tenderData, vendorEmailMap);*/
//                         Map<String, VendorDto> vendorMap = new HashMap<>();
//                         for (String vendorId : vendorIds) {
//                             vendorMasterRepository.findById(vendorId).ifPresent(vendor -> {
//                                 VendorDto dto = new VendorDto();
//                                 dto.setVendorId(vendor.getVendorId());
//                                 dto.setVendorName(vendor.getVendorName());
//                                 dto.setEmailAddress(vendor.getEmailAddress());
//                                 dto.setAddress(vendor.getAddress());
//                                 vendorMap.put(vendorId, dto);
//                             });
//                         }
//                         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//                         try {
//                             LocalDate opening = LocalDate.parse(tenderData.getOpeningDate(), formatter);
//                             LocalDate closing = LocalDate.parse(tenderData.getClosingDate(), formatter);
//                             long days = Duration.between(opening.atStartOfDay(), closing.atStartOfDay()).toDays();
//                             tenderData.setValidityPeriod(days + " Days");
//                         } catch (Exception e) {
//                             tenderData.setValidityPeriod("____ Days");
//                         }

//                         tenderEmailService.handleTenderApproverEmail(wt.getRequestId(), tenderData, vendorMap);

//                     }

//                 } catch (Exception e) {
//                     // log.error("Failed to send transition email", e);
//                 }
//             }
//         } else if (AppConstant.REJECT_TYPE.equalsIgnoreCase(transitionActionReqDto.getAction())) {
//             WorkflowTransitionDto wt = rejectTransition(workflowTransition, currentTransition, transitionActionReqDto);
//             // List<WorkflowTransition> transitions = workflowTransitionRepository.findByRequestId(transitionActionReqDto.getRequestId());
//             List<WorkflowTransition> transitions = workflowTransitionRepository.findByRequestId(transitionActionReqDto.getRequestId());

//             if (wt != null) {
//                 try {
//                     //  emailService.sendWorkflowEmail(wt); // @Async method
//                     sendRejectionTransitionEmails(transitions, wt);
//                 } catch (Exception e) {
//                     // log.error("Failed to send transition email", e);
//                 }
//             }
//         } else if (AppConstant.CHANGE_REQUEST_TYPE.equalsIgnoreCase(transitionActionReqDto.getAction())) {
//             WorkflowTransitionDto wt = requestChangeTransition(workflowTransition, currentTransition, transitionActionReqDto);
//             if (wt != null) {
//                 try {
//                     emailService.sendWorkflowEmail(wt); // @Async method
//                 } catch (Exception e) {
//                     // log.error("Failed to send transition email", e);
//                 }
//             }
//         } else {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.INVALID_TRANSITION_ACTION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Invalid transition action."));
//         }

//         return null;
//     }

//     private void sendRejectionTransitionEmails(List<WorkflowTransition> transitions, WorkflowTransitionDto wt) {
//         if (transitions == null || transitions.isEmpty() || wt == null) {
//             return;
//         }

//         // Collect all distinct createdBy and modifiedBy from all records
//         Set<Integer> userIds = transitions.stream()
//                 .flatMap(t -> Stream.of(t.getCreatedBy(), t.getModifiedBy()))
//                 .filter(Objects::nonNull)
//                 .collect(Collectors.toSet());

//         if (userIds.isEmpty()) {
//             return; // No users to send email
//         }

//         // Fetch email addresses from UserMaster
//         List<String> emails = userMasterRepository.findByUserIdIn(userIds).stream()
//                 .map(UserMaster::getEmail)
//                 .filter(Objects::nonNull)
//                 .distinct()
//                 .toList();

//         if (!emails.isEmpty()) {
//             try {
//                 emailService.sendRejectionWorkflowEmail(emails, wt); // Pass list of emails and transition data
//             } catch (Exception e) {
//                 // log.error("Failed to send workflow email", e);
//             }
//         }
//     }

//     @Override
//     @Transactional
//     public WorkflowTransitionDto submitWorkflow(Integer workflowTransitionId, Integer actionBy, String remarks) {
//         userService.validateUser(actionBy);
//         WorkflowTransition currentWorkflowTransition = workflowTransitionRepository.findById(workflowTransitionId).orElse(null);
//         if (Objects.isNull(currentWorkflowTransition)) {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.INVALID_WORKFLOW_TRANSITION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Workflow transition not found.With given workflow transition id and request id."));
//         }

//         TransitionDto nextTransition = nextTransition(currentWorkflowTransition.getWorkflowId(), currentWorkflowTransition.getWorkflowName(), roleNameByUserId(actionBy), currentWorkflowTransition.getRequestId());
//         if (Objects.isNull(nextTransition)) {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.NEXT_TRANSITION_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Error occurred at approval. No next transition found."));
//         }

//         currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
//         workflowTransitionRepository.save(currentWorkflowTransition);

//         WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
//         nextWorkflowTransition.setWorkflowId(nextTransition.getWorkflowId());
//         nextWorkflowTransition.setTransitionId(nextTransition.getTransitionId());
//         nextWorkflowTransition.setTransitionOrder(nextTransition.getTransitionOrder());
//         nextWorkflowTransition.setTransitionSubOrder(nextTransition.getTransitionSubOrder());
//         nextWorkflowTransition.setWorkflowName(nextTransition.getWorkflowName());
//         nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
//         nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
//         nextWorkflowTransition.setAction(AppConstant.APPROVE_TYPE);
//         nextWorkflowTransition.setRemarks(remarks);
//         nextWorkflowTransition.setModifiedBy(actionBy);
//         nextWorkflowTransition.setModificationDate(new Date());
//         nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
//         nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
//         nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
//         nextWorkflowTransition.setCurrentRole(nextTransition.getCurrentRoleName());
//         nextWorkflowTransition.setNextRole(nextTransition.getNextRoleName());
//         nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

//         workflowTransitionRepository.save(nextWorkflowTransition);

//         return null;
//     }

//     @Override
//     public List<WorkflowTransitionDto> approvedWorkflowTransition(Integer modifiedBy) {
//         List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();
//         List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByModifiedBy(modifiedBy);
//         if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
//             workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowSequence).reversed()).map(e -> {
//                 return mapWorkflowTransitionDto(e);
//             }).collect(Collectors.toList());
//         }
//         return workflowTransitionDtoList;
//     }

//     @Override
//     public List<SubWorkflowTransitionDto> getSubWorkflowTransition(Integer modifiedBy) {
//         List<SubWorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();

//         List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByActionOn(modifiedBy);
//         if (Objects.nonNull(subWorkflowTransitionList) && !subWorkflowTransitionList.isEmpty()) {
//             workflowTransitionDtoList = subWorkflowTransitionList.stream().map(e -> {
//                 SubWorkflowTransitionDto subWorkflowTransitionDto = new SubWorkflowTransitionDto();
//                 subWorkflowTransitionDto.setSubWorkflowTransitionId(e.getSubWorkflowTransitionId());
//                 subWorkflowTransitionDto.setWorkflowId(e.getWorkflowId());
//                 subWorkflowTransitionDto.setWorkflowName(e.getWorkflowName());
//                 subWorkflowTransitionDto.setModifiedBy(e.getModifiedBy());
//                 subWorkflowTransitionDto.setWorkflowSequence(e.getWorkflowSequence());
//                 subWorkflowTransitionDto.setStatus(e.getStatus());
//                 subWorkflowTransitionDto.setRemarks(e.getRemarks());
//                 subWorkflowTransitionDto.setAction(e.getAction());
//                 subWorkflowTransitionDto.setActionOn(e.getActionOn());
//                 subWorkflowTransitionDto.setRequestId(e.getRequestId());
//                 subWorkflowTransitionDto.setCreatedBy(e.getCreatedBy());
//                 subWorkflowTransitionDto.setCreatedDate(e.getCreatedDate());
//                 subWorkflowTransitionDto.setModificationDate(e.getModificationDate());

//                 return subWorkflowTransitionDto;
//             }).collect(Collectors.toList());
//         }
//         return workflowTransitionDtoList;
//     }

//     @Override
//     @Transactional
//     public void approveSubWorkflow(Integer subWorkflowTransitionId) {
//         SubWorkflowTransitionDto subDto = new SubWorkflowTransitionDto();
//         if (Objects.nonNull(subWorkflowTransitionId)) {
//             Optional<SubWorkflowTransition> subWorkflowTransitionOptional = subWorkflowTransitionRepository.findById(subWorkflowTransitionId);
//             if (subWorkflowTransitionOptional.isPresent()) {
//                 SubWorkflowTransition subWorkflowTransition = subWorkflowTransitionOptional.get();
//                 subWorkflowTransition.setStatus(AppConstant.APPROVE_TYPE);
//                 subWorkflowTransition.setAction(AppConstant.COMPLETED_TYPE);
//                 subWorkflowTransition.setModificationDate(new Date());

//                 subWorkflowTransitionRepository.save(subWorkflowTransition);
//                 WorkflowTransitionDto wt = validateSubWorkflow(subWorkflowTransition);
//                 if (wt != null) {
//                     try {
//                         emailService.sendWorkflowEmail(wt); // @Async method
//                     } catch (Exception e) {
//                         // log.error("Failed to send transition email", e);
//                         //  e.printStackTrace();
//                     }
//                 }

//             } else {
//                 throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                         AppConstant.ERROR_TYPE_VALIDATION, "Invalid sub workflow transition id."));
//             }
//         } else {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Invalid sub workflow transition id."));
//         }

//     }

//     private WorkflowTransitionDto validateSubWorkflow(SubWorkflowTransition subWorkflowTransition) {
//         String requestId = subWorkflowTransition.getRequestId();
//         Integer workflowTransitionId = subWorkflowTransition.getWorkflowTransitionId();
//         WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
//         List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByWorkflowTransitionIdAndRequestIdAndTransitionTypeAndTransitionName(workflowTransitionId, requestId, "Double", "Phase_1");
//         if (Objects.nonNull(subWorkflowTransitionList) && !subWorkflowTransitionList.isEmpty()) {
//             List<SubWorkflowTransition> subWorkflowTransitionFilteredList = subWorkflowTransitionList.stream().filter(e -> !e.getStatus().equalsIgnoreCase(AppConstant.APPROVE_TYPE)).collect(Collectors.toList());
//             if (subWorkflowTransitionFilteredList.isEmpty()) {
//                 WorkflowTransition currentWorkflowTransition = workflowTransitionRepository.findByWorkflowTransitionIdAndRequestId(workflowTransitionId, requestId);
//                 currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
//                 workflowTransitionRepository.save(currentWorkflowTransition);

//                 nextWorkflowTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
//                 nextWorkflowTransition.setTransitionId(currentWorkflowTransition.getTransitionId());
//                 nextWorkflowTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder());
//                 nextWorkflowTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
//                 nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
//                 nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
//                 nextWorkflowTransition.setTransitionSubOrder(currentWorkflowTransition.getTransitionSubOrder());
//                 nextWorkflowTransition.setModifiedBy(currentWorkflowTransition.getModifiedBy());
//                 nextWorkflowTransition.setModificationDate(new Date());
//                 nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
//                 nextWorkflowTransition.setAction(AppConstant.APPROVE_TYPE);
//                 nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
//                 nextWorkflowTransition.setRemarks(null);
//                 nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getCurrentRole());
//                 nextWorkflowTransition.setNextRole(currentWorkflowTransition.getCurrentRole());
//                 nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);
//                 nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());

//                 workflowTransitionRepository.save(nextWorkflowTransition);

//             }
//         }
//         return mapToWorkflowTransitionDto(nextWorkflowTransition);
//     }

//     private WorkflowTransitionDto requestChangeTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {
//         if (Objects.nonNull(transitionActionReqDto.getAssignmentRole())) {
//             validateAssignmentRole(transitionActionReqDto.getAssignmentRole(), currentWorkflowTransition);

//             currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
//             workflowTransitionRepository.save(currentWorkflowTransition);

//             WorkflowTransition latestWorkflowTransition = getLatestWorkflowTransiton(currentWorkflowTransition, transitionActionReqDto);

//             WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
//             nextWorkflowTransition.setWorkflowId(latestWorkflowTransition.getWorkflowId());
//             nextWorkflowTransition.setTransitionId(latestWorkflowTransition.getTransitionId());
//             nextWorkflowTransition.setTransitionOrder(latestWorkflowTransition.getTransitionOrder());
//             nextWorkflowTransition.setTransitionSubOrder(latestWorkflowTransition.getTransitionSubOrder());
//             nextWorkflowTransition.setWorkflowName(latestWorkflowTransition.getWorkflowName());
//             nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
//             nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
//             nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
//             nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
//             nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
//             nextWorkflowTransition.setModificationDate(new Date());
//             nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
//             nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
//             nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
//             nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getNextRole());
//             // if (transitionActionReqDto.getAssignmentRole().equalsIgnoreCase("Request Creator")) {
//             /* if(CREATOR_ROLES.contains(transitionActionReqDto.getAssignmentRole())){
//                 nextWorkflowTransition.setNextRole(latestWorkflowTransition.getCurrentRole());
//             } else {
//                 nextWorkflowTransition.setNextRole(latestWorkflowTransition.getNextRole());
//             }*/

//             // nextWorkflowTransition.setNextRole(transitionActionReqDto.getAssignmentRole());
//             // updated by abhinav
//             String assignmentRole = transitionActionReqDto.getAssignmentRole();

//             if ("Request Creator".equalsIgnoreCase(assignmentRole)
//                     && currentWorkflowTransition.getWorkflowName().toUpperCase().contains("PO")) {

//                 nextWorkflowTransition.setNextRole("PO Creator");

//             } else {
//                 nextWorkflowTransition.setNextRole(assignmentRole);
//             }
//             // updated by abhinav ends
//             nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

//             workflowTransitionRepository.save(nextWorkflowTransition);

//             // Bug Fix 1: Make indent editable when sent back to Indent Creator
//             if ("Indent Workflow".equalsIgnoreCase(currentWorkflowTransition.getWorkflowName())
//                     && "Indent Creator".equalsIgnoreCase(transitionActionReqDto.getAssignmentRole())) {
//                 String requestId = currentWorkflowTransition.getRequestId();
//                 if (requestId != null && requestId.startsWith("IND")) {
//                     indentCreationRepository.findById(requestId).ifPresent(indent -> {
//                         indent.setIsEditable(true);
//                         indent.setCurrentStatus("CHANGE_REQUESTED");
//                         indent.setCurrentStage("INDENT_REVISION");
//                         indentCreationRepository.save(indent);
//                     });
//                 }
//             }

//             return mapToWorkflowTransitionDto(nextWorkflowTransition);
//         } else {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Invalid assignment role."));
//         }

//     }

//     private static final Set<String> CREATOR_ROLES = Set.of(
//             "Request Creator",
//             "Indent Creator",
//             "Tender Creator",
//             "PO Creator",
//             "SO Creator",
//             "CP Creator",
//             "Purchase Personnel"
//     );

//     private WorkflowTransition getLatestWorkflowTransiton(WorkflowTransition currentWorkflowTransition, TransitionActionReqDto transitionActionReqDto) {
//         WorkflowTransition workflowTransition = null;
//         List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByWorkflowIdAndRequestIdAndNextRole(currentWorkflowTransition.getWorkflowId(), currentWorkflowTransition.getRequestId(), transitionActionReqDto.getAssignmentRole());
//         if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
//             workflowTransition = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowTransitionId).reversed()).limit(1).collect(Collectors.toList()).get(0);
//         } else if (CREATOR_ROLES.contains(transitionActionReqDto.getAssignmentRole())) {//else if (transitionActionReqDto.getAssignmentRole().equalsIgnoreCase("Request Creator")) {
//             workflowTransitionList = workflowTransitionRepository.findByWorkflowIdAndRequestIdAndCurrentRole(currentWorkflowTransition.getWorkflowId(), currentWorkflowTransition.getRequestId(), transitionActionReqDto.getAssignmentRole());
//             if (Objects.nonNull(workflowTransitionList)) {
//                 workflowTransition = workflowTransitionList.get(0);
//             }
//         }
//         return workflowTransition;
//     }

//     private void validateAssignmentRole(String assignmentRole, WorkflowTransition workflowTransition) {
//         List<String> allPreviousRole = allPreviousRoleWorkflowTransition(workflowTransition.getWorkflowId(), workflowTransition.getRequestId());
//         if (!allPreviousRole.isEmpty() && allPreviousRole.contains(assignmentRole)) {
//         } else {
//             throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION, "Invalid previous assignment role."));
//         }
//     }

//     private WorkflowTransitionDto rejectTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {

//         String requestId = transitionActionReqDto.getRequestId();

//         // Check if requestId starts with IND
//         if (requestId != null && requestId.startsWith("IND")) {

//             // Fetch the IndentId entity
//             Optional<IndentId> optionalIndent = indentIdtenderIdsRepository.findByIndentId(requestId);
//             if (optionalIndent.isEmpty()) {
//                 throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                         AppConstant.ERROR_TYPE_VALIDATION, "Invalid indent ids."));
//             }

//             IndentId indent = optionalIndent.get();
//             String tenderId = indent.getTenderRequest().getTenderId();

//             if (tenderId != null) {
//                 // Fetch latest WorkflowTransition for this tender
//                 Optional<WorkflowTransition> latestTransitionOpt
//                         = workflowTransitionRepository.findTopByRequestIdOrderByWorkflowTransitionIdDesc(tenderId);

//                 if (latestTransitionOpt.isEmpty()) {
//                     throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                             AppConstant.ERROR_TYPE_VALIDATION, "no workflow transition fount."));
//                 }

//                 WorkflowTransition latestTransition = latestTransitionOpt.get();

//                 // If tender is not canceled, return message without canceling indent
//                 if (!AppConstant.CANCELED_TYPE.equalsIgnoreCase(latestTransition.getStatus())) {
//                     // Could return null or custom DTO with message
//                     throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                             AppConstant.ERROR_TYPE_VALIDATION, "Cannot cancel Indent " + requestId + ". Tender " + tenderId + " is not canceled yet.")
//                     );

//                 }
//             }
//         }

//         //update currentWorkflowTransition and save
//         currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
//         workflowTransitionRepository.save(currentWorkflowTransition);

//         WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
//         nextWorkflowTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
//         nextWorkflowTransition.setTransitionId(currentWorkflowTransition.getTransitionId());
//         nextWorkflowTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder());
//         nextWorkflowTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
//         nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
//         nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
//         nextWorkflowTransition.setTransitionSubOrder(currentWorkflowTransition.getTransitionSubOrder());
//         nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
//         nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
//         nextWorkflowTransition.setModificationDate(new Date());
//         nextWorkflowTransition.setStatus(AppConstant.CANCELED_TYPE);
//         nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
//         nextWorkflowTransition.setNextAction(null);
//         nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
//         nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getNextRole());
//         nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

//         workflowTransitionRepository.save(nextWorkflowTransition);

//         return mapToWorkflowTransitionDto(nextWorkflowTransition);

//     }

//     private WorkflowTransition getPrevWorkflowTransition(WorkflowTransition workflowTransition) {
//         List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByRequestId(workflowTransition.getRequestId());
//         if (workflowTransitionList.size() == 1) {
//             return workflowTransitionList.get(0);
//         } else {
//             return workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowTransitionId).reversed()).skip(1).findFirst().get();
//         }
//     }

//     // private void approveTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {
//     private WorkflowTransitionDto approveTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {
//         WorkflowTransition nextWorkflowTransition = null;

//         // ===== BRANCH-BASED APPROVAL ROUTING =====
//         if (currentWorkflowTransition.getBranchId() != null) {
//             // This is a branch-based workflow
//             nextWorkflowTransition = approveBranchBasedTransition(currentWorkflowTransition, transitionActionReqDto);
//         } else {
//             // Legacy TransitionMaster-based workflow
//             nextWorkflowTransition = approveLegacyTransition(currentWorkflowTransition, currentTransition, transitionActionReqDto);
//         }

//         return mapToWorkflowTransitionDto(nextWorkflowTransition);
//     }

//     /**
//      * BRANCH-BASED APPROVAL TRANSITION Routes to next approver in the
//      * sequential approval chain with limit checks and escalation
//      */
//     private WorkflowTransition approveBranchBasedTransition(WorkflowTransition currentWorkflowTransition, TransitionActionReqDto transitionActionReqDto) {
//         // Mark current transition as completed
//         currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
//         workflowTransitionRepository.save(currentWorkflowTransition);

//         System.out.println("🔍 Finding next approver for branch " + currentWorkflowTransition.getBranchId()
//                 + " after Level: " + currentWorkflowTransition.getApprovalLevel()
//                 + ", Seq: " + currentWorkflowTransition.getApprovalSequence());

//         // Get indent details for limit check
//         // added by abhinav start
//         String requestId = currentWorkflowTransition.getRequestId();
//         com.astro.entity.ProcurementModule.IndentCreation indent = null;
//         BigDecimal indentValue = BigDecimal.ZERO;
//         String departmentName = null;
//         String category = null;
//         String location = null;

//         // if (requestId != null && requestId.startsWith("IND")) {
//         //     indent = indentCreationRepository.findById(requestId).orElse(null);
//         //     if (indent != null) {
//         //         indentValue = indent.getTotalIntentValue() != null ? indent.getTotalIntentValue() : BigDecimal.ZERO;
//         //         departmentName = indent.getIndentorDepartment();
//         //         category = indent.getMaterialCategoryType();
//         //         location = indent.getConsignesLocation();
//         //     }
//         // }
//         // updated indent and tender workflow
//         String workflowName = currentWorkflowTransition.getWorkflowName();

//         if (workflowName.toUpperCase().contains("INDENT")) {

//             indent = indentCreationRepository.findById(requestId).orElse(null);

//             if (indent != null) {
//                 indentValue = indent.getTotalIntentValue();
//                 departmentName = indent.getIndentorDepartment();
//                 category = indent.getMaterialCategoryType();
//                 location = indent.getConsignesLocation();
//             }

//         } // else if (workflowName.toUpperCase().contains("TENDER")) {
//         //     TenderRequest tender = tenderRequestRepository.findById(requestId).orElse(null);
//         //     if (tender != null) {
//         //         indentValue = tender.getTotalTenderValue();
//         //         location = tender.getConsignes();
//         //         category = tender.getModeOfProcurement();   // optional
//         //         departmentName = tender.getProjectName();  // if branch uses project as department
//         //     }
//         // } 
//         else if (workflowName.toUpperCase().contains("TENDER")) {

//             TenderRequest tender = tenderRequestRepository.findById(requestId).orElse(null);

//             if (tender != null) {
//                 indentValue = tender.getTotalTenderValue();
//                 location = tender.getConsignes();
//                 category = tender.getModeOfProcurement();

//                 // FIX: GET DEPARTMENT FROM LINKED INDENT
//                 List<String> indentIds = indentIdRepository.findTenderWithIndent(requestId);

//                 if (indentIds != null && !indentIds.isEmpty()) {
//                     indent = indentCreationRepository.findByIndentId(indentIds.get(0));
//                     if (indent != null) {
//                         departmentName = indent.getIndentorDepartment();
//                     }
//                 }
//             }
//         } // else if (workflowName.toUpperCase().contains("PO")) {
//         //     PurchaseOrder po = purchaseOrderRepository.findById(requestId).orElse(null);
//         //     if (po != null) {
//         //         indentValue = po.getTotalValueOfPo();
//         //         departmentName = po.getProjectName();
//         //         location = po.getConsignesAddress();
//         //         // category = po.getMaterialCategoryType();
//         //     }
//         // }
//         else if (workflowName.toUpperCase().contains("PO")) {

//             PurchaseOrder po = purchaseOrderRepository.findById(requestId).orElse(null);

//             if (po != null) {
//                 indentValue = po.getTotalValueOfPo();
//                 location = po.getConsignesAddress();

//                 // FIX: GET DEPARTMENT FROM INDENT
//                 if (po.getIndentId() != null) {
//                     indent = indentCreationRepository.findByIndentId(po.getIndentId());
//                     if (indent != null) {
//                         departmentName = indent.getIndentorDepartment();
//                     }
//                 }
//             }
//         }
//         // debugger
//         System.out.println("==== APPROVAL DEBUG ====");
//         System.out.println("Workflow: " + currentWorkflowTransition.getWorkflowName());
//         System.out.println("Amount: " + indentValue);
//         System.out.println("Department: " + departmentName);
//         System.out.println("Location: " + location);
//         System.out.println("Category: " + category);
//         System.out.println("========================");
//         // Get next approver with limit check (skips approvers if amount is within limit)
//         com.astro.entity.AdminPanel.ApproverMaster nextApprover = branchWorkflowService.getNextApproverWithLimitCheck(
//                 currentWorkflowTransition.getBranchId(),
//                 currentWorkflowTransition.getApprovalLevel(),
//                 currentWorkflowTransition.getApprovalSequence(),
//                 indentValue,
//                 departmentName,
//                 category,
//                 location
//         );
//         if (nextApprover == null) {
//             System.out.println("NO NEXT APPROVER FOUND → WORKFLOW WILL COMPLETE HERE");
//         } else {
//             System.out.println("Next Approver: " + nextApprover.getRoleName());
//         }

//         // Check if escalation is required based on current approver's limits
//         if (nextApprover != null) {
//             com.astro.dto.workflow.EscalationCheckResultDTO escalationResult
//                     = branchWorkflowService.checkEscalationRequired(
//                             nextApprover.getRoleName(),
//                             indentValue,
//                             category,
//                             departmentName,
//                             location
//                     );

//             if (escalationResult != null && escalationResult.isEscalationRequired()) {
//                 // Escalate to Director
//                 System.out.println("ESCALATION REQUIRED: " + escalationResult.getReason());
//                 System.out.println("   Escalating to: " + escalationResult.getEscalateTo());

//                 // Update indent with escalation info
//                 if (indent != null) {
//                     indent.setEscalatedToDirector(true);
//                     indent.setEscalationReason(escalationResult.getReason());
//                     indentCreationRepository.save(indent);
//                 }

//                 // Create escalation transition
//                 return createEscalationTransition(currentWorkflowTransition, transitionActionReqDto,
//                         escalationResult.getEscalateTo(), escalationResult.getReason());
//             }
//         }

//         // Create new workflow transition
//         WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
//         nextWorkflowTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
//         nextWorkflowTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
//         nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
//         nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
//         nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
//         nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
//         nextWorkflowTransition.setModificationDate(new Date());
//         nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
//         nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
//         nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getNextRole());
//         nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);
//         nextWorkflowTransition.setBranchId(currentWorkflowTransition.getBranchId());
//         nextWorkflowTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder() + 1);
//         nextWorkflowTransition.setTransitionSubOrder(1);

//         if (nextApprover != null) {
//             // More approvers in the chain
//             nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
//             nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
//             nextWorkflowTransition.setNextRole(nextApprover.getRoleName());
//             nextWorkflowTransition.setApproverId(nextApprover.getApproverId());
//             nextWorkflowTransition.setApprovalLevel(nextApprover.getApprovalLevel());
//             nextWorkflowTransition.setApprovalSequence(nextApprover.getApprovalSequence());

//             // Reporting Officer: resolve and assign to specific user
//             if (isReportingOfficerRole(nextApprover.getRoleName())) {
//                 try {
//                     Map<String, Object> roInfo = resolveReportingOfficer(currentWorkflowTransition.getCreatedBy());
//                     nextWorkflowTransition.setAssignedToUserId((Integer) roInfo.get("userId"));
//                     nextWorkflowTransition.setAssignedToEmployeeId((String) roInfo.get("employeeId"));
//                     System.out.println("Reporting Officer assigned: " + roInfo.get("employeeName")
//                             + " (userId=" + roInfo.get("userId") + ")");
//                 } catch (BusinessException e) {
//                     System.err.println("Could not resolve reporting officer: " + e.getMessage());
//                     throw e;
//                 }
//             }

//             // Project Head: resolve and assign to specific project head user
//             if (isProjectHeadRole(nextApprover.getRoleName())) {
//                 try {
//                     Map<String, Object> phInfo = resolveProjectHead(currentWorkflowTransition.getRequestId());
//                     nextWorkflowTransition.setAssignedToUserId((Integer) phInfo.get("userId"));
//                     nextWorkflowTransition.setAssignedToEmployeeId((String) phInfo.get("employeeId"));
//                     System.out.println("Project Head assigned: " + phInfo.get("employeeName")
//                             + " (userId=" + phInfo.get("userId") + ")");
//                 } catch (BusinessException e) {
//                     System.err.println("Could not resolve project head: " + e.getMessage());
//                     throw e;
//                 }
//             }

//             // Location-based roles (Professor In Charge / Engineer In Charge)
//             if (isLocationBasedRole(nextApprover.getRoleName())) {
//                 try {
//                     Map<String, Object> locInfo = resolveLocationBasedApprover(
//                             currentWorkflowTransition.getRequestId(), nextApprover.getRoleName());
//                     nextWorkflowTransition.setAssignedToUserId((Integer) locInfo.get("userId"));
//                     nextWorkflowTransition.setAssignedToEmployeeId((String) locInfo.get("employeeId"));
//                     System.out.println(nextApprover.getRoleName() + " assigned: " + locInfo.get("employeeName")
//                             + " (userId=" + locInfo.get("userId") + ")");
//                 } catch (BusinessException e) {
//                     System.err.println("Could not resolve " + nextApprover.getRoleName() + ": " + e.getMessage());
//                     throw e;
//                 }
//             }

//             System.out.println("Approved by " + currentWorkflowTransition.getNextRole()
//                     + " → Routing to " + nextApprover.getRoleName()
//                     + " (Branch: " + currentWorkflowTransition.getBranchId()
//                     + ", Level: " + nextApprover.getApprovalLevel()
//                     + ", Seq: " + nextApprover.getApprovalSequence() + ")");
//         } else {
//             // No more approvers - workflow complete
//             nextWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
//             nextWorkflowTransition.setNextAction(null);
//             nextWorkflowTransition.setNextRole(null);

//             System.out.println("Final approval by " + currentWorkflowTransition.getNextRole()
//                     + " (Branch: " + currentWorkflowTransition.getBranchId()
//                     + ") - Workflow COMPLETED");
//         }

//         workflowTransitionRepository.save(nextWorkflowTransition);
//         return nextWorkflowTransition;
//     }

//     /**
//      * Create an escalation transition to Director
//      */
//     private WorkflowTransition createEscalationTransition(
//             WorkflowTransition currentWorkflowTransition,
//             TransitionActionReqDto transitionActionReqDto,
//             String escalateTo,
//             String escalationReason
//     ) {
//         WorkflowTransition escalationTransition = new WorkflowTransition();
//         escalationTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
//         escalationTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
//         escalationTransition.setRequestId(currentWorkflowTransition.getRequestId());
//         escalationTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
//         escalationTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
//         escalationTransition.setModifiedBy(transitionActionReqDto.getActionBy());
//         escalationTransition.setModificationDate(new Date());
//         escalationTransition.setAction("Escalated");
//         escalationTransition.setRemarks("ESCALATION: " + escalationReason);
//         escalationTransition.setCurrentRole(currentWorkflowTransition.getNextRole());
//         escalationTransition.setNextRole(escalateTo);
//         escalationTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);
//         escalationTransition.setBranchId(currentWorkflowTransition.getBranchId());
//         escalationTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder() + 1);
//         escalationTransition.setTransitionSubOrder(1);
//         escalationTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
//         escalationTransition.setNextAction(AppConstant.PENDING_TYPE);

//         System.out.println("⚠️ ESCALATION: " + currentWorkflowTransition.getNextRole()
//                 + " → " + escalateTo
//                 + " | Reason: " + escalationReason);

//         workflowTransitionRepository.save(escalationTransition);
//         return escalationTransition;
//     }

//     @Override
//     @Transactional
//     public WorkflowTransitionDto performAutoApproval(WorkflowTransition pendingTransition, Integer autoApproveHours) {
//         if (!"Pending".equals(pendingTransition.getNextAction())) {
//             return null;
//         }

//         pendingTransition.setNextAction(AppConstant.COMPLETED_TYPE);
//         workflowTransitionRepository.save(pendingTransition);

//         String requestId = pendingTransition.getRequestId();
//         com.astro.entity.ProcurementModule.IndentCreation indent = null;
//         BigDecimal indentValue = BigDecimal.ZERO;
//         String departmentName = null;
//         String category = null;
//         String location = null;

//         if (requestId != null && requestId.startsWith("IND")) {
//             indent = indentCreationRepository.findById(requestId).orElse(null);
//             if (indent != null) {
//                 indentValue = indent.getTotalIntentValue() != null ? indent.getTotalIntentValue() : BigDecimal.ZERO;
//                 departmentName = indent.getIndentorDepartment();
//                 category = indent.getMaterialCategoryType();
//                 location = indent.getConsignesLocation();
//             }
//         }

//         com.astro.entity.AdminPanel.ApproverMaster nextApprover = branchWorkflowService.getNextApproverWithLimitCheck(
//                 pendingTransition.getBranchId(),
//                 pendingTransition.getApprovalLevel(),
//                 pendingTransition.getApprovalSequence(),
//                 indentValue,
//                 departmentName,
//                 category,
//                 location
//         );

//         if (nextApprover != null) {
//             com.astro.dto.workflow.EscalationCheckResultDTO escalationResult
//                     = branchWorkflowService.checkEscalationRequired(
//                             nextApprover.getRoleName(),
//                             indentValue,
//                             category,
//                             departmentName,
//                             location
//                     );

//             if (escalationResult != null && escalationResult.isEscalationRequired()) {
//                 if (indent != null) {
//                     indent.setEscalatedToDirector(true);
//                     indent.setEscalationReason(escalationResult.getReason());
//                     indentCreationRepository.save(indent);
//                 }

//                 TransitionActionReqDto autoActionDto = new TransitionActionReqDto();
//                 autoActionDto.setAction("Auto-Approved");
//                 autoActionDto.setRemarks("AUTO-APPROVED: " + pendingTransition.getNextRole() + " did not act within " + autoApproveHours + " hours");
//                 autoActionDto.setActionBy(null);

//                 WorkflowTransition escalated = createEscalationTransition(pendingTransition, autoActionDto,
//                         escalationResult.getEscalateTo(), escalationResult.getReason());
//                 return mapToWorkflowTransitionDto(escalated);
//             }
//         }

//         WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
//         nextWorkflowTransition.setWorkflowId(pendingTransition.getWorkflowId());
//         nextWorkflowTransition.setWorkflowName(pendingTransition.getWorkflowName());
//         nextWorkflowTransition.setRequestId(pendingTransition.getRequestId());
//         nextWorkflowTransition.setCreatedDate(pendingTransition.getCreatedDate());
//         nextWorkflowTransition.setCreatedBy(pendingTransition.getCreatedBy());
//         nextWorkflowTransition.setModifiedBy(null);
//         nextWorkflowTransition.setModificationDate(new Date());
//         nextWorkflowTransition.setAction("Auto-Approved");
//         nextWorkflowTransition.setRemarks("AUTO-APPROVED: " + pendingTransition.getNextRole() + " did not act within " + autoApproveHours + " hours");
//         nextWorkflowTransition.setCurrentRole(pendingTransition.getNextRole());
//         nextWorkflowTransition.setWorkflowSequence(pendingTransition.getWorkflowSequence() + 1);
//         nextWorkflowTransition.setBranchId(pendingTransition.getBranchId());
//         nextWorkflowTransition.setTransitionOrder(
//                 (pendingTransition.getTransitionOrder() != null ? pendingTransition.getTransitionOrder() : 0) + 1
//         );
//         nextWorkflowTransition.setTransitionSubOrder(1);

//         if (nextApprover != null) {
//             nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
//             nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
//             nextWorkflowTransition.setNextRole(nextApprover.getRoleName());
//             nextWorkflowTransition.setApproverId(nextApprover.getApproverId());
//             nextWorkflowTransition.setApprovalLevel(nextApprover.getApprovalLevel());
//             nextWorkflowTransition.setApprovalSequence(nextApprover.getApprovalSequence());

//             // Reporting Officer: resolve and assign to specific user (auto-approval path)
//             if (isReportingOfficerRole(nextApprover.getRoleName())) {
//                 try {
//                     Map<String, Object> roInfo = resolveReportingOfficer(pendingTransition.getCreatedBy());
//                     nextWorkflowTransition.setAssignedToUserId((Integer) roInfo.get("userId"));
//                     nextWorkflowTransition.setAssignedToEmployeeId((String) roInfo.get("employeeId"));
//                 } catch (BusinessException e) {
//                     System.err.println("Auto-approval: Could not resolve reporting officer: " + e.getMessage());
//                 }
//             }

//             // Project Head: resolve and assign to specific project head user (auto-approval path)
//             if (isProjectHeadRole(nextApprover.getRoleName())) {
//                 try {
//                     Map<String, Object> phInfo = resolveProjectHead(pendingTransition.getRequestId());
//                     nextWorkflowTransition.setAssignedToUserId((Integer) phInfo.get("userId"));
//                     nextWorkflowTransition.setAssignedToEmployeeId((String) phInfo.get("employeeId"));
//                 } catch (BusinessException e) {
//                     System.err.println("Auto-approval: Could not resolve project head: " + e.getMessage());
//                 }
//             }

//             // Location-based roles (Professor In Charge / Engineer In Charge) (auto-approval path)
//             if (isLocationBasedRole(nextApprover.getRoleName())) {
//                 try {
//                     Map<String, Object> locInfo = resolveLocationBasedApprover(
//                             pendingTransition.getRequestId(), nextApprover.getRoleName());
//                     nextWorkflowTransition.setAssignedToUserId((Integer) locInfo.get("userId"));
//                     nextWorkflowTransition.setAssignedToEmployeeId((String) locInfo.get("employeeId"));
//                 } catch (BusinessException e) {
//                     System.err.println("Auto-approval: Could not resolve " + nextApprover.getRoleName() + ": " + e.getMessage());
//                 }
//             }
//         } else {
//             nextWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
//             nextWorkflowTransition.setNextAction(null);
//             nextWorkflowTransition.setNextRole(null);

//             if (indent != null) {
//                 indent.setCurrentStatus("APPROVED");
//                 indent.setCurrentStage("INDENT_APPROVED");
//                 indent.setIsEditable(false);
//                 indentCreationRepository.save(indent);
//             }
//         }

//         workflowTransitionRepository.save(nextWorkflowTransition);
//         return mapToWorkflowTransitionDto(nextWorkflowTransition);
//     }

//     /**
//      * LEGACY TRANSITION-MASTER BASED APPROVAL (Fallback)
//      */
//     private WorkflowTransition approveLegacyTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {
//         TransitionDto nextTransition = null;
//         WorkflowTransition nextWorkflowTransition = null;

//         if (Objects.isNull(currentTransition.getNextRoleId())) {
//             currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
//             workflowTransitionRepository.save(currentWorkflowTransition);

//             nextWorkflowTransition = new WorkflowTransition();
//             nextWorkflowTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
//             nextWorkflowTransition.setTransitionId(currentWorkflowTransition.getTransitionId());
//             nextWorkflowTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder());
//             nextWorkflowTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
//             nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
//             nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
//             nextWorkflowTransition.setTransitionSubOrder(currentWorkflowTransition.getTransitionSubOrder());
//             nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
//             nextWorkflowTransition.setModificationDate(new Date());
//             nextWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
//             nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
//             nextWorkflowTransition.setNextAction(null);
//             nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
//             nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getNextRole());
//             nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

//             workflowTransitionRepository.save(nextWorkflowTransition);
//         } else {
//             nextTransition = nextTransition(currentTransition.getWorkflowId(), currentWorkflowTransition.getWorkflowName(), transitionActionReqDto.getRoleName(), currentWorkflowTransition.getRequestId());

//             if (Objects.isNull(nextTransition)) {
//                 throw new InvalidInputException(new ErrorDetails(AppConstant.NEXT_TRANSITION_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                         AppConstant.ERROR_TYPE_VALIDATION, "Error occurred at approval. No next transition found."));
//             }

//             //update currentWorkflowTransition nextSatus and save
//             currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
//             workflowTransitionRepository.save(currentWorkflowTransition);

//             nextWorkflowTransition = new WorkflowTransition();
//             nextWorkflowTransition.setWorkflowId(nextTransition.getWorkflowId());
//             nextWorkflowTransition.setTransitionId(nextTransition.getTransitionId());
//             nextWorkflowTransition.setTransitionOrder(nextTransition.getTransitionOrder());
//             nextWorkflowTransition.setTransitionSubOrder(nextTransition.getTransitionSubOrder());
//             nextWorkflowTransition.setWorkflowName(nextTransition.getWorkflowName());
//             if (Objects.isNull(nextTransition.getNextRoleId())) {
//                 nextWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
//                 nextWorkflowTransition.setNextAction(null);
//             } else {
//                 nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
//                 nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
//             }

//             nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
//             nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
//             nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
//             nextWorkflowTransition.setModificationDate(new Date());
//             nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
//             nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
//             nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
//             nextWorkflowTransition.setCurrentRole(nextTransition.getCurrentRoleName());
//             nextWorkflowTransition.setNextRole(nextTransition.getNextRoleName());
//             nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

//             workflowTransitionRepository.save(nextWorkflowTransition);
//         }

//         //validation for tender workflow
//         if (WorkflowName.TENDER_EVALUATOR.getValue().equalsIgnoreCase(currentWorkflowTransition.getWorkflowName())) {
//             validateTenderWorkFlow(currentWorkflowTransition, nextWorkflowTransition, AppConstant.APPROVE_TYPE);
//         }

//         // Bug Fix 1 & 4: Update indent status and editability when approved
//         if ("Indent Workflow".equalsIgnoreCase(currentWorkflowTransition.getWorkflowName())) {
//             String requestId = currentWorkflowTransition.getRequestId();
//             if (requestId != null && requestId.startsWith("IND")) {
//                 // Make final copy for lambda
//                 final WorkflowTransition finalNextWorkflowTransition = nextWorkflowTransition;

//                 indentCreationRepository.findById(requestId).ifPresent(indent -> {
//                     // Make indent non-editable after submission/approval
//                     indent.setIsEditable(false);

//                     // Update status based on workflow completion
//                     if (AppConstant.COMPLETED_TYPE.equalsIgnoreCase(finalNextWorkflowTransition.getStatus())) {
//                         indent.setCurrentStatus("APPROVED");
//                         indent.setCurrentStage("INDENT_APPROVED");
//                     } else {
//                         indent.setCurrentStatus("IN_APPROVAL");
//                         indent.setCurrentStage("INDENT_APPROVAL_LEVEL_" + finalNextWorkflowTransition.getWorkflowSequence());
//                         indent.setApprovalLevel(finalNextWorkflowTransition.getWorkflowSequence());
//                     }
//                     indentCreationRepository.save(indent);
//                 });
//             }
//         }

//         return nextWorkflowTransition;
//     }

//     private WorkflowTransitionDto mapToWorkflowTransitionDto(WorkflowTransition nextWorkflowTransition) {
//         WorkflowTransitionDto dto = new WorkflowTransitionDto();
//         dto.setWorkflowTransitionId(nextWorkflowTransition.getWorkflowTransitionId());
//         dto.setWorkflowId(nextWorkflowTransition.getWorkflowId());
//         dto.setTransitionId(nextWorkflowTransition.getTransitionId());
//         dto.setTransitionOrder(nextWorkflowTransition.getTransitionOrder());
//         dto.setTransitionSubOrder(nextWorkflowTransition.getTransitionSubOrder());
//         dto.setWorkflowName(nextWorkflowTransition.getWorkflowName());
//         dto.setStatus(nextWorkflowTransition.getStatus());
//         dto.setNextAction(nextWorkflowTransition.getNextAction());
//         dto.setAction(nextWorkflowTransition.getAction());
//         dto.setRemarks(nextWorkflowTransition.getRemarks());
//         dto.setModifiedBy(nextWorkflowTransition.getModifiedBy());
//         dto.setModificationDate(nextWorkflowTransition.getModificationDate());
//         dto.setRequestId(nextWorkflowTransition.getRequestId());
//         dto.setCreatedBy(nextWorkflowTransition.getCreatedBy());
//         dto.setCreatedDate(nextWorkflowTransition.getCreatedDate());
//         dto.setCurrentRole(nextWorkflowTransition.getCurrentRole());
//         dto.setNextRole(nextWorkflowTransition.getNextRole());
//         dto.setWorkflowSequence(nextWorkflowTransition.getWorkflowSequence());

//         // Reporting Officer assignment fields
//         dto.setAssignedToUserId(nextWorkflowTransition.getAssignedToUserId());
//         dto.setAssignedToEmployeeId(nextWorkflowTransition.getAssignedToEmployeeId());
//         if (nextWorkflowTransition.getAssignedToEmployeeId() != null) {
//             employeeDepartmentMasterRepository.findByEmployeeId(nextWorkflowTransition.getAssignedToEmployeeId())
//                     .ifPresent(emp -> dto.setAssignedToEmployeeName(emp.getEmployeeName()));
//         }

//         return dto;
//     }

//     private List<SubWorkflowTransitionDto> validateTenderWorkFlow(WorkflowTransition currentWorkflowTransition, WorkflowTransition nextWorkflowTransition, String actionType) {
//         List<SubWorkflowTransitionDto> subWorkflowDtoList = new ArrayList<>();
//         if ((nextWorkflowTransition.getCurrentRole().equalsIgnoreCase("Purchase Dept") && Objects.isNull(nextWorkflowTransition.getNextRole())) || (nextWorkflowTransition.getCurrentRole().equalsIgnoreCase("Purchase Dept") && Objects.nonNull(nextWorkflowTransition.getNextRole()) && nextWorkflowTransition.getNextRole().equalsIgnoreCase("Purchase Dept"))) {
//             List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByWorkflowTransitionIdAndStatus(currentWorkflowTransition.getWorkflowTransitionId(), AppConstant.PENDING_TYPE);
//             if (Objects.nonNull(subWorkflowTransitionList) && !subWorkflowTransitionList.isEmpty()) {
//                 throw new InvalidInputException(new ErrorDetails(AppConstant.NEXT_TRANSITION_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                         AppConstant.ERROR_TYPE_VALIDATION, "Error occurred at approval. All indentor not performed action for this workflow to approve."));
//             }

//         }
//         if ((nextWorkflowTransition.getCurrentRole().equalsIgnoreCase("Tender Evaluator")) || (nextWorkflowTransition.getCurrentRole().equalsIgnoreCase("Purchase Dept") && Objects.nonNull(nextWorkflowTransition.getNextRole()) && nextWorkflowTransition.getNextRole().equalsIgnoreCase("Purchase Dept"))) {
//             TenderWithIndentResponseDTO tenderWithIndentResponseDTO = tenderRequestService.getTenderRequestById(nextWorkflowTransition.getRequestId());
//             if (Objects.nonNull(tenderWithIndentResponseDTO) && Objects.nonNull(tenderWithIndentResponseDTO.getIndentResponseDTO()) && !tenderWithIndentResponseDTO.getIndentResponseDTO().isEmpty()) {
//                 List<IndentCreationResponseDTO> indentResponseDTO = tenderWithIndentResponseDTO.getIndentResponseDTO();
//                 List<Integer> indenterList = indentResponseDTO.stream().map(e -> e.getCreatedBy()).collect(Collectors.toList());
//                 if (Objects.nonNull(indenterList) && !indenterList.isEmpty()) {
//                     AtomicInteger seq = new AtomicInteger(1);
//                     indenterList.forEach(e -> {
//                         SubWorkflowTransition subWorkflowTransition = new SubWorkflowTransition();
//                         subWorkflowTransition.setWorkflowId(nextWorkflowTransition.getWorkflowId());
//                         subWorkflowTransition.setWorkflowTransitionId(nextWorkflowTransition.getWorkflowTransitionId());
//                         subWorkflowTransition.setAction(AppConstant.PENDING_TYPE);
//                         subWorkflowTransition.setWorkflowName(nextWorkflowTransition.getWorkflowName());
//                         subWorkflowTransition.setActionOn(e);
//                         /* if (tenderWithIndentResponseDTO.getTotalTenderValue().compareTo(BigDecimal.valueOf(1000000)) < 0) {
//                             subWorkflowTransition.setActionOn(e); // Indentor
//                         } else {
//                             subWorkflowTransition.setActionOn(26); // Evaluator
//                         }*/
//                         subWorkflowTransition.setCreatedBy(nextWorkflowTransition.getModifiedBy());
//                         subWorkflowTransition.setWorkflowSequence(seq.get());
//                         subWorkflowTransition.setRequestId(nextWorkflowTransition.getRequestId());
//                         subWorkflowTransition.setStatus(AppConstant.PENDING_TYPE);
//                         subWorkflowTransition.setCreatedDate(new Date());
//                         subWorkflowTransition.setTransitionType(tenderWithIndentResponseDTO.getBidType());
//                         if ("Double".equalsIgnoreCase(tenderWithIndentResponseDTO.getBidType()) && Objects.isNull(actionType)) {
//                             subWorkflowTransition.setTransitionName("Phase_1");
//                         } else if ("Double".equalsIgnoreCase(tenderWithIndentResponseDTO.getBidType()) && AppConstant.APPROVE_TYPE.equalsIgnoreCase(actionType)) {
//                             subWorkflowTransition.setTransitionName("Phase_2");
//                         }
//                         seq.getAndIncrement();

//                         subWorkflowTransitionRepository.save(subWorkflowTransition);
//                         SubWorkflowTransitionDto subDto = new SubWorkflowTransitionDto();
//                         subDto.setSubWorkflowTransitionId(subWorkflowTransition.getSubWorkflowTransitionId());
//                         subDto.setWorkflowId(subWorkflowTransition.getWorkflowId());
//                         subDto.setWorkflowName(subWorkflowTransition.getWorkflowName());
//                         subDto.setRequestId(subWorkflowTransition.getRequestId());
//                         subDto.setCreatedBy(subWorkflowTransition.getCreatedBy());
//                         subDto.setModifiedBy(subWorkflowTransition.getModifiedBy());
//                         subDto.setStatus(subWorkflowTransition.getStatus());
//                         subDto.setAction(subWorkflowTransition.getAction());
//                         subDto.setRemarks(subWorkflowTransition.getRemarks());
//                         subDto.setActionOn(subWorkflowTransition.getActionOn());
//                         subDto.setWorkflowSequence(subWorkflowTransition.getWorkflowSequence());
//                         subDto.setModificationDate(subWorkflowTransition.getModificationDate());
//                         subDto.setCreatedDate(subWorkflowTransition.getCreatedDate());

//                         subWorkflowDtoList.add(subDto);

//                     });
//                 }
//             }
//         }

//         return subWorkflowDtoList;

//     }

//     private void validateUserRole(Integer actionBy, Integer roleId) {
//         if (Objects.nonNull(roleId)) {
//             UserRoleMaster userRoleMaster = userRoleMasterRepository.findByRoleIdAndUserId(roleId, actionBy);
//             if (Objects.isNull(userRoleMaster)) {
//                 throw new InvalidInputException(new ErrorDetails(AppConstant.UNAUTHORIZED_ACTION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                         AppConstant.ERROR_TYPE_VALIDATION, "Unauthorized user."));
//             }
//         }
//     }

//     private TransitionDto nextTransitionDto(List<TransitionDto> nextTransitionDtoList, String workflowName, String requestId) {
//         TransitionDto transitionDto = null;
//         List<Integer> conditionIdList = nextTransitionDtoList.stream().filter(f -> Objects.nonNull(f.getConditionId())).map(e -> e.getConditionId()).collect(Collectors.toList());

//         //for without any condition move and have only one next move
//         if (conditionIdList.isEmpty() && nextTransitionDtoList.size() == 1) {
//             return nextTransitionDtoList.get(0);
//         } else {
//             List<TransitionConditionMaster> transitionConditionMasterList = transitionConditionMasterRepository.findAllById(conditionIdList);

//             switch (workflowName.toUpperCase()) {
//                 case "INDENT WORKFLOW":
//                     //get indent data here
//                     IndentCreationResponseDTO indentCreationResponseDTO = indentCreationService.getIndentById(requestId);
//                     for (TransitionDto dto : nextTransitionDtoList) {
//                         Integer conditionId = dto.getConditionId();
//                         if (Objects.nonNull(conditionId)) {
//                             TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
//                             String conditionKey = transitionConditionMaster.getConditionKey();
//                             String conditionValue = transitionConditionMaster.getConditionValue();
//                             Object dataValue = null;
//                             boolean conditionCheckFlag = Boolean.FALSE;
//                             if (conditionKey.equalsIgnoreCase("ProjectName")) {
//                                 dataValue = indentCreationResponseDTO.getProjectName();
//                                 conditionCheckFlag = Objects.nonNull(dataValue);
//                             } else if (conditionKey.equalsIgnoreCase("MaterialCategory")) {
//                                 dataValue = indentCreationResponseDTO.getMaterialCategory();
//                                 conditionCheckFlag = ((String) dataValue).equalsIgnoreCase(conditionValue);
//                             } else if (conditionKey.equalsIgnoreCase("ConsignesLocation")) {
//                                 dataValue = indentCreationResponseDTO.getConsignesLocation();
//                                 conditionCheckFlag = ((String) dataValue).equalsIgnoreCase(conditionValue);
//                             } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterials")) {
//                                 dataValue = indentCreationResponseDTO.getTotalPriceOfAllMaterials();
//                                 conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
//                             } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterialsAnd")) {
//                                 dataValue = indentCreationResponseDTO.getTotalPriceOfAllMaterials();
//                                 conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() > Double.valueOf(conditionValue);
//                             } else if (conditionKey.equalsIgnoreCase("projectLimit")) {
//                                 dataValue = indentCreationResponseDTO.getTotalPriceOfAllMaterials();
//                                 BigDecimal projectLimit = indentCreationResponseDTO.getProjectLimit();
//                                 conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= ((BigDecimal) projectLimit).doubleValue();
//                             } else if (conditionKey.equalsIgnoreCase("materialCategoryAndconsignesLocation")) {
//                                 String category = indentCreationResponseDTO.getMaterialCategory();
//                                 String location = indentCreationResponseDTO.getConsignesLocation();

//                                 if (category != null && location != null) {
//                                     String combinedValue = category + "+" + location;
//                                     conditionCheckFlag = combinedValue.equalsIgnoreCase(conditionValue);
//                                 }
//                             } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterialsAndDept")) {
//                                 dataValue = indentCreationResponseDTO.getTotalPriceOfAllMaterials();
//                                 String department = indentCreationResponseDTO.getEmployeeDepartment();

//                                 if (conditionValue != null && conditionValue.contains("(") && conditionValue.endsWith(")")) {
//                                     String[] valueParts = conditionValue.replace(")", "").split("\\(");
//                                     if (valueParts.length == 2) {
//                                         double priceLimit = Double.parseDouble(valueParts[0]);
//                                         String requiredDept = valueParts[1];

//                                         if (dataValue != null && department != null && department.equalsIgnoreCase(requiredDept)) {
//                                             double actualValue = ((BigDecimal) dataValue).doubleValue();

//                                             if (requiredDept.equalsIgnoreCase("Engineering")) {
//                                                 /*if (priceLimit == 50000) {
//                                                     conditionCheckFlag = actualValue > 50000 && actualValue <= 100000;
//                                                 } else if (priceLimit == 100000) {
//                                                     conditionCheckFlag = actualValue > 100000;
//                                                 }*/
//                                                 if (priceLimit == 100000) {

//                                                     conditionCheckFlag = actualValue <= 100000 || actualValue >= 100000;
//                                                 }

//                                             } else if (requiredDept.equalsIgnoreCase("OtherDept")) {
//                                                 if (priceLimit == 150000) {
//                                                     conditionCheckFlag = actualValue <= 150000 || actualValue >= 150000;
//                                                 }

//                                             }
//                                         }
//                                     }
//                                 }
//                             }

//                             if (conditionCheckFlag) {
//                                 transitionDto = dto;
//                                 break;
//                             }
//                         }
//                     }
//                     break;
//                 case "CONTINGENCY PURCHASE WORKFLOW":
//                     ContigencyPurchaseResponseDto contigencyPurchaseResponseDto = contigencyPurchaseService.getContigencyPurchaseById(requestId);
//                     for (TransitionDto dto : nextTransitionDtoList) {
//                         Integer conditionId = dto.getConditionId();
//                         if (Objects.nonNull(conditionId)) {
//                             TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
//                             String conditionKey = transitionConditionMaster.getConditionKey();
//                             String conditionValue = transitionConditionMaster.getConditionValue();
//                             Object dataValue = null;
//                             boolean conditionCheckFlag = Boolean.FALSE;
//                             if (conditionKey.equalsIgnoreCase("ProjectName")) {
//                                 dataValue = contigencyPurchaseResponseDto.getProjectName();
//                                 if ("Empty".equalsIgnoreCase(conditionValue)) {
//                                     conditionCheckFlag = Objects.isNull(dataValue);
//                                 } else if ("Not Empty".equalsIgnoreCase(conditionValue)) {
//                                     conditionCheckFlag = Objects.nonNull(dataValue);
//                                 }
//                             }

//                             if (conditionCheckFlag) {
//                                 transitionDto = dto;
//                                 break;
//                             }
//                         }
//                     }
//                     break;
//                 case "TENDER EVALUATOR WORKFLOW":
//                     TenderWithIndentResponseDTO tenderWithIndentResponseDTO = tenderRequestService.getTenderRequestById(requestId);
//                     for (TransitionDto dto : nextTransitionDtoList) {
//                         Integer conditionId = dto.getConditionId();
//                         if (Objects.nonNull(conditionId)) {
//                             TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
//                             String conditionKey = transitionConditionMaster.getConditionKey();
//                             String conditionValue = transitionConditionMaster.getConditionValue();
//                             Object dataValue = null;
//                             boolean conditionCheckFlag = Boolean.FALSE;
//                             if (conditionKey.equalsIgnoreCase("totalTenderValue")) {
//                                 dataValue = tenderWithIndentResponseDTO.getTotalTenderValue();
//                                 conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
//                             } else if (conditionKey.equalsIgnoreCase("bidType")) {
//                                 dataValue = tenderWithIndentResponseDTO.getBidType();
//                                 conditionCheckFlag = ((String) dataValue).equalsIgnoreCase(conditionValue);
//                             }
//                             if (conditionCheckFlag) {
//                                 transitionDto = dto;
//                                 break;
//                             }
//                         }
//                     }
//                     break;
//                 case "SO WORKFLOW":
//                     soWithTenderAndIndentResponseDTO soWithTenderAndIndentResponseDTO = serviceOrderService.getServiceOrderById(requestId);
//                     for (TransitionDto dto : nextTransitionDtoList) {
//                         Integer conditionId = dto.getConditionId();
//                         if (Objects.nonNull(conditionId)) {
//                             TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
//                             String conditionKey = transitionConditionMaster.getConditionKey();
//                             String conditionValue = transitionConditionMaster.getConditionValue();
//                             Object dataValue = null;
//                             boolean conditionCheckFlag = Boolean.FALSE;
//                             if (conditionKey.equalsIgnoreCase("ProjectName")) {
//                                 dataValue = soWithTenderAndIndentResponseDTO.getProjectName();
//                                 conditionCheckFlag = Objects.nonNull(dataValue);
//                             } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterials")) {
//                                 dataValue = soWithTenderAndIndentResponseDTO.getTotalValueOfSo();
//                                 conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
//                             }
//                             if (conditionCheckFlag) {
//                                 transitionDto = dto;
//                                 break;
//                             }
//                         }
//                     }
//                     break;
//                 case "WO WORKFLOW":
//                     woWithTenderAndIndentResponseDTO woWithTenderAndIndentResponseDTO = workOrderService.getWorkOrderById(requestId);
//                     for (TransitionDto dto : nextTransitionDtoList) {
//                         Integer conditionId = dto.getConditionId();
//                         if (Objects.nonNull(conditionId)) {
//                             TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
//                             String conditionKey = transitionConditionMaster.getConditionKey();
//                             String conditionValue = transitionConditionMaster.getConditionValue();
//                             Object dataValue = null;
//                             boolean conditionCheckFlag = Boolean.FALSE;
//                             if (conditionKey.equalsIgnoreCase("ProjectName")) {
//                                 dataValue = woWithTenderAndIndentResponseDTO.getProjectName();
//                                 conditionCheckFlag = Objects.nonNull(dataValue);
//                             } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterials")) {
//                                 dataValue = woWithTenderAndIndentResponseDTO.getTotalValueOfWo();
//                                 conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
//                             }
//                             if (conditionCheckFlag) {
//                                 transitionDto = dto;
//                                 break;
//                             }
//                         }
//                     }
//                     break;
//                 case "PO WORKFLOW":
//                     poWithTenderAndIndentResponseDTO poWithTenderAndIndentResponseDTO = purchaseOrderService.getPurchaseOrderById(requestId);
//                     for (TransitionDto dto : nextTransitionDtoList) {
//                         Integer conditionId = dto.getConditionId();
//                         if (Objects.nonNull(conditionId)) {
//                             TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
//                             String conditionKey = transitionConditionMaster.getConditionKey();
//                             String conditionValue = transitionConditionMaster.getConditionValue();
//                             Object dataValue = null;
//                             boolean conditionCheckFlag = Boolean.FALSE;
//                             if (conditionKey.equalsIgnoreCase("ProjectName")) {
//                                 dataValue = poWithTenderAndIndentResponseDTO.getProjectName();
//                                 conditionCheckFlag = Objects.nonNull(dataValue);
//                             } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterials")) {
//                                 dataValue = poWithTenderAndIndentResponseDTO.getTotalValueOfPo();
//                                 conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
//                             }
//                             if (conditionCheckFlag) {
//                                 transitionDto = dto;
//                                 break;
//                             }
//                         }
//                     }
//                     break;
//                 //add more case here

//             }
//         }
//         return transitionDto;
//     }

//     //updated  by abhinav starts
//     public List<ApprovedIndentsDto> getApprovedIndents(Integer userId) {

//         UserMaster user = userMasterRepository.findByUserId(userId);
//         if (user == null) {
//             return new ArrayList<>();
//         }

//         String employeeId = user.getEmployeeId();

//         List<IndentAssignment> assignments
//                 = indentAssignmentRepository
//                         .findByAssignedToEmployeeIdAndStatus(employeeId, "ACTIVE");

//         if (assignments.isEmpty()) {
//             return new ArrayList<>();
//         }

//         List<String> indentIds = assignments.stream()
//                 .map(IndentAssignment::getIndentId)
//                 .collect(Collectors.toList());

//         // Now calling correct method
//         List<ApprovedIndentsDto> approvedIndents = getAllApprovedIndentsInternal();

//         return approvedIndents.stream()
//                 .filter(dto -> indentIds.contains(dto.getIndentId()))
//                 .collect(Collectors.toList());
//     }

//     private List<ApprovedIndentsDto> getAllApprovedIndentsInternal() {

//         List<String> approvedIndentIds = workflowTransitionRepository.findApprovedIndentRequestIds();

//         List<ApprovedIndentsDto> rawResults
//                 = indentCreationRepository.findApprovedIndents(approvedIndentIds);

//         Map<String, ApprovedIndentsDto> grouped = new LinkedHashMap<>();

//         for (ApprovedIndentsDto dto : rawResults) {
//             grouped.computeIfAbsent(dto.getIndentId(), k -> {
//                 ApprovedIndentsDto newDto = new ApprovedIndentsDto();
//                 newDto.setIndentId(dto.getIndentId());
//                 newDto.setProjectName(dto.getProjectName());
//                 newDto.setIndentorName(dto.getIndentorName());
//                 newDto.setCreatedDate(dto.getCreatedDate());
//                 newDto.setMaterialDes(new ArrayList<>());
//                 return newDto;
//             }).getMaterialDes().addAll(dto.getMaterialDes());
//         }

//         return new ArrayList<>(grouped.values());
//     }
//     // updates by abhinav line ends

//     // Old getApprovedIndents() kept for reference
//     public List<ApprovedIndentsDto> getApprovedIndents() {
//         List<String> approvedIndentIds = workflowTransitionRepository.findApprovedIndentRequestIds();
//         System.out.println(approvedIndentIds);
//         List<ApprovedIndentsDto> rawResults = indentCreationRepository.findApprovedIndents(approvedIndentIds);

//         Map<String, ApprovedIndentsDto> grouped = new LinkedHashMap<>();

//         for (ApprovedIndentsDto dto : rawResults) {
//             grouped.computeIfAbsent(dto.getIndentId(), k -> {
//                 ApprovedIndentsDto newDto = new ApprovedIndentsDto();
//                 newDto.setIndentId(dto.getIndentId());
//                 newDto.setProjectName(dto.getProjectName());
//                 newDto.setIndentorName(dto.getIndentorName());
//                 newDto.setCreatedDate(dto.getCreatedDate());
//                 newDto.setMaterialDes(new ArrayList<>());
//                 return newDto;
//             }).getMaterialDes().addAll(dto.getMaterialDes());
//         }

//         return new ArrayList<>(grouped.values());
//     }


//     /*
//     public List<ApprovedIndentsDto> getApprovedIndents() {
//         // Step 1: Retrieve all the approved indent request IDs
//         List<String> approvedIndentIds = workflowTransitionRepository.findApprovedIndentRequestIds();

//         // Step 2: Fetch the project names based on the indent IDs and project codes
//         List<ApprovedIndentsDto> approvedIndents = new ArrayList<>();

//         for (String indentId : approvedIndentIds) {
//             // Fetch the indent details from the IndentCreation entity
//             IndentCreation indentCreation = indentCreationRepository.findByIndentId(indentId);

//         //  List<MaterialDetails> md =   materialDetailsRepo.findByIndentCreation_IndentId(indentId);
//             if (indentCreation != null) {
//                 // Get the projectCode from IndentCreation
//                 String projectCode = indentCreation.getProjectName();

//                 // Fetch the project name from ProjectMaster using the projectCode
//                 Optional<ProjectMaster> projectMaster = projectMasterRepository.findByProjectCode(projectCode);

//                 if (projectMaster.isPresent()) {
//                     ProjectMaster pm = projectMaster.get();
//                     // Create ApprovedIndentsDto object with the indentId and projectName
//                     ApprovedIndentsDto dto = new ApprovedIndentsDto();
//                     dto.setIndentId(indentCreation.getIndentId());
//                     dto.setProjectName(pm.getProjectNameDescription());

//                     //dto.setIndentorName(indentCreation.getIndentorName());
//                      approvedIndents.add(dto);


//                 } else {
//                     // Handle case where project is not found
//                     ApprovedIndentsDto dto = new ApprovedIndentsDto();
//                     dto.setIndentId(indentCreation.getIndentId());
//                     dto.setProjectName(null);
//                     approvedIndents.add(dto);
//                 }
//             } else {
//                 // Handle case where indent is not found
//                 ApprovedIndentsDto dto = new ApprovedIndentsDto();
//                 dto.setIndentId(indentId);
//                 dto.setProjectName("Indent not found");
//                 approvedIndents.add(dto);
//             }
//         }

//         return approvedIndents;
//     }*//*
// public List<ApprovedIndentsDto> getApprovedIndents() {
//     List<String> approvedIndentIds = workflowTransitionRepository.findApprovedIndentRequestIds();

//     List<IndentCreation> indentCreations = indentCreationRepository.findByIndentIdIn(approvedIndentIds);

//     Map<String, IndentCreation> indentMap = indentCreations.stream()
//             .collect(Collectors.toMap(IndentCreation::getIndentId, ic -> ic));

//     Set<String> projectCodes = indentCreations.stream()
//             .map(IndentCreation::getProjectName)
//             .collect(Collectors.toSet());

//     List<ProjectMaster> projectMasters = projectMasterRepository.findByProjectCodeIn(projectCodes);

//     Map<String, String> projectCodeToNameMap = projectMasters.stream()
//             .collect(Collectors.toMap(ProjectMaster::getProjectCode, ProjectMaster::getProjectNameDescription));

//     List<ApprovedIndentsDto> approvedIndents = new ArrayList<>();

//     for (String indentId : approvedIndentIds) {
//         ApprovedIndentsDto dto = new ApprovedIndentsDto();
//         dto.setIndentId(indentId);

//         IndentCreation ic = indentMap.get(indentId);
//         if (ic != null) {
//             String projectName = projectCodeToNameMap.get(ic.getProjectName());
//             dto.setProjectName(projectName != null ? projectName : "Project not found");
//         } else {
//             dto.setProjectName("Indent not found");
//         }

//         approvedIndents.add(dto);
//     }

//     return approvedIndents;
// }

//      */

//     @Override
//     public List<String> getApprovedTenderIdsForPOAndSO() {
//         return workflowTransitionRepository.findApprovedTenderIdsForPOANDSO();
//     }

//     @Override
//     public List<String> getApprovedPoIds() {
//         return workflowTransitionRepository.findApprovedPoIds();
//     }


//     /*  @Override
//     public List<ApprovedPoIdsDto> getApprovedPoIds() {

//        List<String> poIds= workflowTransitionRepository.findApprovedPoIds();
//        List<ApprovedPoIdsDto> approvedPoIdsDtos = new ArrayList<>();
//        for(String poId : poIds){
//           PurchaseOrder purchaseOrder =purchaseOrderRepository.findByPoId(poId);

//           IndentCreation ind = indentCreationRepository.findByIndentId(purchaseOrder.getIndentId());
//           ApprovedPoIdsDto dto = new ApprovedPoIdsDto(
//                   poId,
//                   (ind != null) ? ind.getIndentorName() : null,
//                   purchaseOrder.getVendorName()
//           );
//           approvedPoIdsDtos.add(dto);

//        }
//        return approvedPoIdsDtos;

//     }

//      */
//     public List<ApprovedTenderDto> getApprovedTender() {

//         // return workflowTransitionRepository.findApprovedTenderRequestIds();
//         List<String> tenderIds = workflowTransitionRepository.findApprovedTenderRequestIds();

//         List<ApprovedTenderDto> approvedTenders = new ArrayList<>();

//         for (String tenderId : tenderIds) {
//             // Fetch bidType and totalValue from TenderRequest table
//             Optional<TenderRequest> optionalTenderRequest = tenderRequestRepository.findByTenderId(tenderId);
//             List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
//             // int indentNumber = indentIds != null ? indentIds.size() : 0;
//             //  System.out.println("indent number"+ indentIds +""+ indentNumber);
//             int indentNumber = 1;
//             if (optionalTenderRequest.isPresent()) {
//                 TenderRequest tenderRequest = optionalTenderRequest.get();
//                 ApprovedTenderDto dto = new ApprovedTenderDto(
//                         tenderId,
//                         tenderRequest.getBidType(),
//                         tenderRequest.getTotalTenderValue(),
//                         indentNumber
//                 );
//                 approvedTenders.add(dto);
//             }
//         }
//         return approvedTenders;

//     }

//     public ApprovedTenderDto getApprovedTenderId(String tenderId) {
//         boolean tender = workflowTransitionRepository.isApprovedTenderAndNotUsed(tenderId);
//         if (!tender) {
//             throw new BusinessException(new ErrorDetails(
//                     AppConstant.ERROR_CODE_RESOURCE,
//                     AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     "Tender is not approved, already used."
//             ));
//         }

//         TenderRequest tenderRequest = tenderRequestRepository.findByTenderId(tenderId)
//                 .orElseThrow(() -> new BusinessException(new ErrorDetails(
//                 AppConstant.ERROR_CODE_RESOURCE,
//                 AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                 AppConstant.ERROR_TYPE_VALIDATION,
//                 "Tender request not found for the provided tender ID: " + tenderId
//         )));
//         //int indentNumber=1;
//         List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
//         int indentNumber = indentIds != null ? indentIds.size() : 0;
//         return new ApprovedTenderDto(
//                 tenderId,
//                 tenderRequest.getBidType(),
//                 tenderRequest.getTotalTenderValue(),
//                 indentNumber
//         );
//     }

//     /*public List<ApprovedTenderDto> getApprovedTender(String roleName) {
//      List<String> tenderIds = workflowTransitionRepository.findApprovedTenderRequestIds();
//      List<ApprovedTenderDto> approvedTenders = new ArrayList<>();

//      for (String tenderId : tenderIds) {
//          Optional<TenderRequest> optionalTenderRequest = tenderRequestRepository.findByTenderId(tenderId);

//          if (optionalTenderRequest.isPresent()) {
//              TenderRequest tenderRequest = optionalTenderRequest.get();
//              BigDecimal totalValue = tenderRequest.getTotalTenderValue();

//              // Role-based filtering
//              if ("Indent Creator".equalsIgnoreCase(roleName) && totalValue.compareTo(BigDecimal.valueOf(10_00_000)) <= 0) {
//                  approvedTenders.add(new ApprovedTenderDto(tenderId, tenderRequest.getBidType(), totalValue));
//              } else if ("Tender Evaluator".equalsIgnoreCase(roleName) && totalValue.compareTo(BigDecimal.valueOf(10_00_000)) > 0) {
//                  approvedTenders.add(new ApprovedTenderDto(tenderId, tenderRequest.getBidType(), totalValue));
//              }
//          }
//      }

//      return approvedTenders;
//  }*/
//     @Override
//     public List<QueueResponse> allPendingWorkflowTransitionINQueue(String roleName) {
//         return allPendingWorkflowTransitionINQueue(roleName, null);
//     }

//     @Override
//     public List<QueueResponse> allPendingWorkflowTransitionINQueue(String roleName, Integer userId) {
//         List<QueueResponse> queueResponseList = new ArrayList<>();

//         // Fetch Workflow Transitions based on role and pending action
//         List<WorkflowTransition> workflowTransitionList;
//         if (userId != null) {
//             workflowTransitionList = workflowTransitionRepository.findPendingByRoleAndOptionalUser(AppConstant.PENDING_TYPE, roleName, userId);
//         } else {
//             workflowTransitionList = workflowTransitionRepository.findByNextActionAndNextRole(AppConstant.PENDING_TYPE, roleName);
//         }

//         // Filter by user's employee location against branch location condition.
//         // Personally-assigned transitions (assignedToUserId != null, e.g. Reporting Officer) are exempt —
//         // the RO is already explicitly assigned and must see the indent regardless of their own location.
//         if (userId != null && workflowTransitionList != null && !workflowTransitionList.isEmpty()) {
//             UserMaster queueUser = userMasterRepository.findByUserId(userId);
//             if (queueUser != null && queueUser.getEmployeeId() != null) {
//                 EmployeeDepartmentMaster queueEmp = employeeDepartmentMasterRepository.findByEmployeeId(queueUser.getEmployeeId()).orElse(null);
//                 if (queueEmp != null && queueEmp.getLocation() != null) {
//                     String userLocation = queueEmp.getLocation().trim().toUpperCase();
//                     // Global approvers (location = "ALL") see items from every branch location — skip filter
//                     if (!"ALL".equals(userLocation)) {
//                         workflowTransitionList = workflowTransitionList.stream()
//                                 .filter(wt -> {
//                                     if (wt.getAssignedToUserId() != null) {
//                                         return true;
//                                     }
//                                     if (wt.getBranchId() == null) {
//                                         return true;
//                                     }
//                                     return workflowBranchMasterRepository.findById(wt.getBranchId())
//                                             .map(branch -> {
//                                                 if (branch.getConditionConfig() == null || branch.getConditionConfig().trim().isEmpty()) {
//                                                     return true;
//                                                 }
//                                                 try {
//                                                     Map<String, Object> config = objectMapper.readValue(branch.getConditionConfig(), new TypeReference<Map<String, Object>>() {
//                                                     });
//                                                     if (!config.containsKey("location")) {
//                                                         return true;
//                                                     }
//                                                     String branchLocation = String.valueOf(config.get("location")).trim().toUpperCase();
//                                                     return branchLocation.equals(userLocation);
//                                                 } catch (Exception e) {
//                                                     return true;
//                                                 }
//                                             })
//                                             .orElse(true);
//                                 })
//                                 .collect(Collectors.toList());
//                     }
//                 }
//             }
//         }

//         if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
//             queueResponseList = workflowTransitionList.stream()
//                     .sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate))
//                     .map(this::mapToQueueResponse)
//                     .collect(Collectors.toList());
//         }
//         System.out.println(roleName);

//         if ("Store Purchase Officer".equalsIgnoreCase(roleName)) {
//             List<VendorRegistrationResponseDTO> awaitingApprovalVendors = vendorMasterUtilService.getAllAwaitingApprovalVendors();

//             List<QueueResponse> vendorQueueResponses = awaitingApprovalVendors.stream()
//                     .map(this::mapVendorToQueueResponse)
//                     .collect(Collectors.toList());
//             System.out.println("Awaiting Vendors: " + awaitingApprovalVendors.size());
//             queueResponseList.addAll(vendorQueueResponses);

//             // Fetch awaiting approval materials
//             List<MaterialMasterUtilResponseDto> awaitingApprovalMaterials
//                     = materialMasterUtilService.getAllAwaitingApprovalMaterials();

//             List<QueueResponse> materialQueueResponses = awaitingApprovalMaterials.stream()
//                     .map(this::mapMaterialToQueueResponse)
//                     .collect(Collectors.toList());

//             System.out.println("Awaiting Materials: " + awaitingApprovalMaterials.size());
//             queueResponseList.addAll(materialQueueResponses);

//             // Fetch awaiting approval jobs
//             List<com.astro.dto.workflow.JobMasterResponseDto> awaitingApprovalJobs
//                     = jobMasterService.getAllAwaitingApprovalJobs();

//             List<QueueResponse> jobQueueResponses = awaitingApprovalJobs.stream()
//                     .map(this::mapJobToQueueResponse)
//                     .collect(Collectors.toList());

//             System.out.println("Awaiting Jobs: " + awaitingApprovalJobs.size());
//             queueResponseList.addAll(jobQueueResponses);
//         }

//         if ("Indent Creator".equalsIgnoreCase(roleName)) {
//             List<MaterialMasterUtilResponseDto> changeRequestMaterials
//                     = materialMasterUtilService.getAllChangeRequestMaterials();

//             List<QueueResponse> changeRequestQueueResponses = changeRequestMaterials.stream()
//                     .map(this::mapMaterialToQueueResponse)
//                     .collect(Collectors.toList());
//             queueResponseList.addAll(changeRequestQueueResponses);

//             // Fetch change request jobs for Indent Creator
//             List<com.astro.dto.workflow.JobMasterResponseDto> changeRequestJobs
//                     = jobMasterService.getAllChangeRequestJobs();

//             List<QueueResponse> changeRequestJobResponses = changeRequestJobs.stream()
//                     .map(this::mapJobToQueueResponse)
//                     .collect(Collectors.toList());
//             queueResponseList.addAll(changeRequestJobResponses);
//         }

//         return queueResponseList;
//     }

//     private QueueResponse mapMaterialToQueueResponse(MaterialMasterUtilResponseDto material) {

//         String materialCode = material.getMaterialCode();

//         Optional<MaterialMasterUtil> Material = materialMasterUtilRepository.findByMaterialCode(materialCode);

//         MaterialMasterUtil ma = Material.get();
//         UserMaster us = userMasterRepository.findByUserId(ma.getCreatedBy());
//         QueueResponse response = new QueueResponse();
//         response.setRequestId(material.getMaterialCode());
//         response.setWorkflowName("Material Workflow");
//         response.setWorkflowId(9);
//         response.setAmount(ma.getUnitPrice());

//         // Null check to prevent NullPointerException
//         if (us != null) {
//             response.setIndentorName(us.getUserName());
//         } else {
//             response.setIndentorName("Unknown User");
//         }

//         response.setStatus(material.getApprovalStatus());
//         return response;
//     }

//     private QueueResponse mapJobToQueueResponse(com.astro.dto.workflow.JobMasterResponseDto job) {
//         QueueResponse response = new QueueResponse();
//         response.setRequestId(job.getJobCode());
//         response.setWorkflowName("Job Workflow");
//         response.setWorkflowId(11);
//         response.setAmount(job.getEstimatedPriceWithCcy());
//         response.setIndentorName(job.getUpdatedBy() != null ? job.getUpdatedBy() : "Unknown User");
//         response.setStatus(job.getApprovalStatus());
//         return response;
//     }

//     private QueueResponse mapVendorToQueueResponse(VendorRegistrationResponseDTO vendor) {
//         QueueResponse response = new QueueResponse();
//         response.setRequestId(vendor.getVendorId());
//         response.setIndentorName(vendor.getVendorName());
//         response.setStatus(vendor.getApprovalStatus());
//         response.setWorkflowName("Vendor Workflow");
//         response.setWorkflowId(8); // ← ADD THIS LINE
//         return response;
//     }

//     // Mapping function to convert WorkflowTransition to QueueResponse
//     private QueueResponse mapToQueueResponse(WorkflowTransition workflowTransition) {
//         QueueResponse queueResponse = new QueueResponse();

//         // Mapping existing attributes
//         queueResponse.setWorkflowTransitionId(workflowTransition.getWorkflowTransitionId());
//         queueResponse.setWorkflowId(workflowTransition.getWorkflowId());
//         queueResponse.setWorkflowName(workflowTransition.getWorkflowName());
//         queueResponse.setTransitionId(workflowTransition.getTransitionId());
//         queueResponse.setRequestId(workflowTransition.getRequestId());
//         queueResponse.setCreatedBy(workflowTransition.getCreatedBy());
//         //  queueResponse.setCreatedRole(workflowTransition.getCreatedRole());
//         queueResponse.setModifiedBy(workflowTransition.getModifiedBy());
//         // queueResponse.setModifiedRole(workflowTransition.getModifiedRole());
//         queueResponse.setStatus(workflowTransition.getStatus());
//         queueResponse.setNextAction(workflowTransition.getNextAction());
//         queueResponse.setAction(workflowTransition.getAction());
//         queueResponse.setRemarks(workflowTransition.getRemarks());
//         //    queueResponse.setNextActionId(workflowTransition.getNextActionId());
//         //   queueResponse.setNextActionRole(workflowTransition.getNextActionRole());
//         queueResponse.setTransitionOrder(workflowTransition.getTransitionOrder());
//         queueResponse.setTransitionSubOrder(workflowTransition.getTransitionSubOrder());
//         queueResponse.setCurrentRole(workflowTransition.getCurrentRole());
//         queueResponse.setNextRole(workflowTransition.getNextRole());
//         queueResponse.setWorkflowSequence(workflowTransition.getWorkflowSequence());
//         queueResponse.setModificationDate(workflowTransition.getModificationDate());
//         queueResponse.setCreatedDate(workflowTransition.getCreatedDate());

//         // Reporting Officer assignment fields
//         queueResponse.setAssignedToUserId(workflowTransition.getAssignedToUserId());
//         if (workflowTransition.getAssignedToEmployeeId() != null) {
//             employeeDepartmentMasterRepository.findByEmployeeId(workflowTransition.getAssignedToEmployeeId())
//                     .ifPresent(emp -> queueResponse.setAssignedToEmployeeName(emp.getEmployeeName()));
//         }

//         String requestId = workflowTransition.getRequestId();

//         if (requestId.startsWith("IND")) {
//             // Fetch data from IndentCreation entity
//             String indentId = requestId;
//             //  IndentCreationResponseDTO indentCreations = indentCreationService.getIndentById(indentId);
//             IndentCreation indentCreation = indentCreationRepository.getByIndentId(indentId);
//             List<MaterialDetails> mdList = materialDetailsRepo.findByIndentCreation_IndentId(indentId);

//             if (indentCreation != null) {
//                 queueResponse.setIndentorName(indentCreation.getIndentorName());
//                 queueResponse.setProjectName(indentCreation.getProjectName());
//                 queueResponse.setAmount(indentCreation.getTotalIntentValue());
//                 //  queueResponse.setBudgetName();
//                 //   queueResponse.setIndentTitle("NUll");
//                 if (!mdList.isEmpty()) {
//                     MaterialDetails m = mdList.get(0);
//                     queueResponse.setModeOfProcurement(m.getModeOfProcurement());
//                     queueResponse.setBudgetName(m.getBudgetCode());
//                 }

//                 queueResponse.setConsignee(indentCreation.getConsignesLocation());
//             }
//         } else if (requestId.startsWith("T")) {
//             String tenderId = requestId;
//             TenderRequest tenderRequest = tenderRequestRepository.findById(tenderId).orElse(null);

//             // Fetch data from TenderRequest entity
//             //  TenderWithIndentResponseDTO tenderRequest = tenderRequestService.getTenderRequestById(tenderId);
//             //  TenderRequest tenderRequest = tenderRequestRepository.getByTenderId(tenderId);
//             if (tenderRequest != null) {
//                 //  TenderRequest tenderRequest = tenderRequestOptional.get();
//                 //  queueResponse.setIndentorName("NUll");
//                 queueResponse.setProjectName(tenderRequest.getProjectName());
//                 queueResponse.setAmount(tenderRequest.getTotalTenderValue());
//                 //   queueResponse.setBudgetName("NUll");
//                 queueResponse.setIndentTitle(tenderRequest.getTitleOfTender());
//                 queueResponse.setModeOfProcurement(tenderRequest.getModeOfProcurement());
//                 queueResponse.setConsignee(tenderRequest.getConsignes());

//                 /*  List<IndentCreationResponseDTO> indentList = tenderRequest.getIndentResponseDTO();
//                 if (indentList != null && !indentList.isEmpty()) {
//                     IndentCreationResponseDTO firstIndent = indentList.get(0); // Assuming first indent is needed
//                     queueResponse.setIndentorName(firstIndent.getIndentorName());
//                     queueResponse.setProjectName(firstIndent.getProjectName());
//                 }

//                  */
//             }
//         } else if (requestId.startsWith("CP")) {
//             // Fetch data from CP table
//             String contigencyId = requestId;
//             //  ContigencyPurchaseResponseDto cpTable = contigencyPurchaseService.getContigencyPurchaseById(contigencyId);
//             ContigencyPurchase cp = contigencyPurchaseRepository.findById(contigencyId).orElse(null);

//             if (cp != null) {
//                 // ContigencyPurchase cp = cpTable.get();
//                 //  queueResponse.setIndentorName("Null");
//                 queueResponse.setProjectName(cp.getProjectName());
//                 queueResponse.setAmount(cp.getTotalCpValue());
//                 //  queueResponse.setAmount(cp.getAmountToBePaid());
//                 //  queueResponse.setBudgetName("Null");
//                 //  queueResponse.setIndentTitle("NUll");
//                 //queueResponse.setModeOfProcurement("NULL");
//                 //  queueResponse.setConsignee("Null");
//             }
//         } else if (requestId.startsWith("PO")) {
//             String poId = requestId;
//             // poWithTenderAndIndentResponseDTO po = purchaseOrderService.getPurchaseOrderById(poId);
//             PurchaseOrder po = purchaseOrderRepository.findById(poId).orElse(null);

//             if (po != null) {
//                 String mode = tenderRequestRepository.findModeOfProcurementByTenderId(po.getTenderId());

//                 //   queueResponse.setIndentorName("Null");
//                 queueResponse.setProjectName(po.getProjectName());
//                 queueResponse.setAmount(po.getTotalValueOfPo());
//                 queueResponse.setModeOfProcurement(mode);
//                 //     queueResponse.setBudgetName();
//                 //      queueResponse.setIndentTitle();
//                 //   queueResponse.setModeOfProcurement();
//                 queueResponse.setConsignee(po.getConsignesAddress());
//                 //  TenderWithIndentResponseDTO tenderDetails= po.getTenderDetails();
//                 //     queueResponse.setIndentTitle(tenderDetails.getTitleOfTender());
//                 //  queueResponse.setModeOfProcurement(tenderDetails.getModeOfProcurement());
//             }
//         } else if (requestId.startsWith("SO")) {
//             String soId = requestId;
//             //  soWithTenderAndIndentResponseDTO so = serviceOrderService.getServiceOrderById(soId);
//             ServiceOrder so = serviceOrderRepository.findById(soId)
//                     .orElse(null);
//             if (so != null) {
//                 //  ServiceOrder so = SO.get();
//                 //   queueResponse.setIndentorName("Null");
//                 queueResponse.setProjectName(so.getProjectName());
//                 queueResponse.setAmount(so.getTotalValueOfSo());
//                 //     queueResponse.setBudgetName();
//                 //      queueResponse.setIndentTitle();
//                 //   queueResponse.setModeOfProcurement();
//                 queueResponse.setConsignee(so.getConsignesAddress());
//                 //  TenderWithIndentResponseDTO tenderDetails= so.getTenderDetails();
//                 //  queueResponse.setIndentTitle(tenderDetails.getTitleOfTender());
//                 //  queueResponse.setModeOfProcurement(tenderDetails.getModeOfProcurement());
//             }

//         } else if (workflowTransition.getWorkflowId() == 10) {
//             String pvRequestId = workflowTransition.getRequestId();  // "INV/1153/156/15"
//             String[] parts = pvRequestId.split("/");

//             Long id = Long.parseLong(parts[parts.length - 1]);

//             Optional<PaymentVoucher> pv = paymentVoucherReposiotry.findById(id);

//             if (pv.isPresent()) {
//                 PaymentVoucher p = pv.get();
//                 queueResponse.setPaymentType(p.getPaymentVoucherType());
//                 if (p.getPaymentVoucherType().equalsIgnoreCase("partial")) {
//                     queueResponse.setAmount(p.getPaymentVoucherNetAmount());
//                 } else if (p.getPaymentVoucherType().equalsIgnoreCase("Full Payment")) {
//                     queueResponse.setAmount(p.getPaymentVoucherNetAmount());
//                 } else {
//                     queueResponse.setAmount(p.getPaymentVoucherNetAmount());
//                 }
//                 queueResponse.setPoNo(p.getPurchaseOrderId());
//                 queueResponse.setVendorName(p.getVendorName());
//             }

//         }
//         return queueResponse;

//     }

//     /*


//     @Override
//     public List<SubWorkflowQueueDto> getSubWorkflowQueue(Integer modifiedBy) {
//         List<SubWorkflowQueueDto> workflowQueueDtoList = new ArrayList<>();
//         Set<String> processedIndentIds = new HashSet<>(); // To track already added indentIds
//         String previousRequestId = null; // Track last requestId
//         List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByActionOn(modifiedBy);

//         if (Objects.nonNull(subWorkflowTransitionList) && !subWorkflowTransitionList.isEmpty()) {
//             for (SubWorkflowTransition transition : subWorkflowTransitionList) {
//                 String tenderId = transition.getRequestId();
//                 // Reset processedIndentIds if requestId has changed
//                 if (!tenderId.equals(previousRequestId)) {
//                     processedIndentIds.clear();
//                     previousRequestId = tenderId; // Update to new requestId
//                 }
//                 // Fetch indentIds for the current tender (requestId)
//                 List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);

//                 // Fetch indent details using the indentIds
//                 List<IndentCreation> indentList = indentCreationRepository.findByIndentIdIn(indentIds);

//                 for (IndentCreation indent : indentList) {
//                     if (processedIndentIds.contains(indent.getIndentId())) {
//                         continue; // Skip if already added
//                     }

//                     SubWorkflowQueueDto subWorkflowQueueDto = new SubWorkflowQueueDto();
//                     subWorkflowQueueDto.setSubWorkflowTransitionId(transition.getSubWorkflowTransitionId());
//                     subWorkflowQueueDto.setWorkflowId(transition.getWorkflowId());
//                     subWorkflowQueueDto.setWorkflowName(transition.getWorkflowName());
//                     subWorkflowQueueDto.setModifiedBy(transition.getModifiedBy());
//                     subWorkflowQueueDto.setWorkflowSequence(transition.getWorkflowSequence());
//                     subWorkflowQueueDto.setStatus(transition.getStatus());
//                     subWorkflowQueueDto.setRemarks(transition.getRemarks());
//                     subWorkflowQueueDto.setAction(transition.getAction());
//                     subWorkflowQueueDto.setActionOn(transition.getActionOn());
//                     subWorkflowQueueDto.setRequestId(transition.getRequestId());
//                     subWorkflowQueueDto.setCreatedBy(transition.getCreatedBy());
//                     subWorkflowQueueDto.setCreatedDate(transition.getCreatedDate());
//                     subWorkflowQueueDto.setModificationDate(transition.getModificationDate());

//                     // Assign corresponding indent to the tender
//                     subWorkflowQueueDto.setIndentId(indent.getIndentId());
//                     subWorkflowQueueDto.setIndentorName(indent.getIndentorName());
//                     subWorkflowQueueDto.setProjectName(indent.getProjectName());
//                     subWorkflowQueueDto.setAmount(indent.getTotalIntentValue());
//                     subWorkflowQueueDto.setConsignee(indent.getConsignesLocation());

//                     workflowQueueDtoList.add(subWorkflowQueueDto);
//                     processedIndentIds.add(indent.getIndentId()); // Mark indentId as processed
//                 }
//             }
//         }
//         return workflowQueueDtoList;
//     }

//      */
//  /*
//     @Override
//     public List<SubWorkflowQueueDto> getSubWorkflowQueue(Integer modifiedBy) {
//         List<SubWorkflowQueueDto> workflowQueueDtoList = new ArrayList<>();


//         List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByActionOn(modifiedBy);

//         if (subWorkflowTransitionList != null && !subWorkflowTransitionList.isEmpty()) {

//             Map<String, List<SubWorkflowTransition>> transitionsByRequestId = subWorkflowTransitionList.stream()
//                     .collect(Collectors.groupingBy(SubWorkflowTransition::getRequestId));

//             for (Map.Entry<String, List<SubWorkflowTransition>> entry : transitionsByRequestId.entrySet()) {
//                 String tenderId = entry.getKey(); // Request ID
//                 List<SubWorkflowTransition> transitions = entry.getValue(); // Transitions for the request ID

//                 List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
//                 if (indentIds.isEmpty()) continue;

//                 // Fetch indent details using the indentIds
//                 List<IndentCreation> indentList = indentCreationRepository.findByIndentIdIn(indentIds);
//                 if (indentList.isEmpty()) continue;

//                 // Distribute indent records across transitions
//                 int transitionIndex = 0;
//                 for (IndentCreation indent : indentList) {
//                     SubWorkflowTransition transition = transitions.get(transitionIndex);

//                     SubWorkflowQueueDto subWorkflowQueueDto = new SubWorkflowQueueDto();
//                     subWorkflowQueueDto.setSubWorkflowTransitionId(transition.getSubWorkflowTransitionId());
//                     subWorkflowQueueDto.setWorkflowId(transition.getWorkflowId());
//                     subWorkflowQueueDto.setWorkflowName(transition.getWorkflowName());
//                     subWorkflowQueueDto.setModifiedBy(transition.getModifiedBy());
//                     subWorkflowQueueDto.setWorkflowSequence(transition.getWorkflowSequence());
//                     subWorkflowQueueDto.setStatus(transition.getStatus());
//                     subWorkflowQueueDto.setRemarks(transition.getRemarks());
//                     subWorkflowQueueDto.setAction(transition.getAction());
//                     subWorkflowQueueDto.setActionOn(transition.getActionOn());
//                     subWorkflowQueueDto.setRequestId(transition.getRequestId());
//                     subWorkflowQueueDto.setCreatedBy(transition.getCreatedBy());
//                     subWorkflowQueueDto.setCreatedDate(transition.getCreatedDate());
//                     subWorkflowQueueDto.setModificationDate(transition.getModificationDate());

//                     // Assign corresponding indent to the transition
//                     subWorkflowQueueDto.setIndentId(indent.getIndentId());
//                     subWorkflowQueueDto.setIndentorName(indent.getIndentorName());
//                     subWorkflowQueueDto.setProjectName(indent.getProjectName());
//                     subWorkflowQueueDto.setAmount(indent.getTotalIntentValue());
//                     subWorkflowQueueDto.setConsignee(indent.getConsignesLocation());

//                     workflowQueueDtoList.add(subWorkflowQueueDto);

//                     transitionIndex = (transitionIndex + 1) % transitions.size();
//                 }
//             }
//         }
//         return workflowQueueDtoList;
//     }

//      */
//     @Override
//     public List<SubWorkflowQueueDto> getSubWorkflowQueue(Integer modifiedBy) {
//         List<SubWorkflowQueueDto> workflowQueueDtoList = new ArrayList<>();

//         List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByActionOn(modifiedBy);

//         if (subWorkflowTransitionList != null && !subWorkflowTransitionList.isEmpty()) {

//             Map<String, List<SubWorkflowTransition>> transitionsByRequestId = subWorkflowTransitionList.stream()
//                     .collect(Collectors.groupingBy(SubWorkflowTransition::getRequestId));

//             for (Map.Entry<String, List<SubWorkflowTransition>> entry : transitionsByRequestId.entrySet()) {
//                 String tenderId = entry.getKey(); // Request ID
//                 List<SubWorkflowTransition> transitions = entry.getValue(); // Transitions for the request ID

//                 List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
//                 if (indentIds.isEmpty()) {
//                     continue;
//                 }

//                 // Fetch indent details using the indentIds
//                 List<IndentCreation> indentList = indentCreationRepository.findByIndentIdIn(indentIds);
//                 if (indentList.isEmpty()) {
//                     continue;
//                 }

// // Double the size of indentList dynamically
//                 List<IndentCreation> extendedIndentList = new ArrayList<>(indentList);
//                 extendedIndentList.addAll(indentList); // Duplicate the list

//                 int transitionIndex = 0;
//                 for (SubWorkflowTransition transition : transitions) {
//                     // Assign indent details (cycling through the extended list)
//                     IndentCreation indent = extendedIndentList.get(transitionIndex % extendedIndentList.size());

//                     SubWorkflowQueueDto subWorkflowQueueDto = new SubWorkflowQueueDto();
//                     subWorkflowQueueDto.setSubWorkflowTransitionId(transition.getSubWorkflowTransitionId());
//                     subWorkflowQueueDto.setWorkflowId(transition.getWorkflowId());
//                     subWorkflowQueueDto.setWorkflowName(transition.getWorkflowName());
//                     subWorkflowQueueDto.setModifiedBy(transition.getModifiedBy());
//                     subWorkflowQueueDto.setWorkflowSequence(transition.getWorkflowSequence());
//                     subWorkflowQueueDto.setStatus(transition.getStatus());
//                     subWorkflowQueueDto.setRemarks(transition.getRemarks());
//                     subWorkflowQueueDto.setAction(transition.getAction());
//                     subWorkflowQueueDto.setActionOn(transition.getActionOn());
//                     subWorkflowQueueDto.setRequestId(transition.getRequestId());
//                     subWorkflowQueueDto.setCreatedBy(transition.getCreatedBy());
//                     subWorkflowQueueDto.setCreatedDate(transition.getCreatedDate());
//                     subWorkflowQueueDto.setModificationDate(transition.getModificationDate());

//                     // Assign indent details
//                     subWorkflowQueueDto.setIndentId(indent.getIndentId());
//                     subWorkflowQueueDto.setIndentorName(indent.getIndentorName());
//                     subWorkflowQueueDto.setProjectName(indent.getProjectName());
//                     subWorkflowQueueDto.setAmount(indent.getTotalIntentValue());
//                     subWorkflowQueueDto.setConsignee(indent.getConsignesLocation());

//                     workflowQueueDtoList.add(subWorkflowQueueDto);

//                     transitionIndex++;
//                 }

//             }
//         }
//         return workflowQueueDtoList;
//     }

//     @Override
//     public List<WorkflowTransitionDto> performAllTransitionAction(List<TransitionActionReqDto> transitionActionReqDto) {

//         List<WorkflowTransitionDto> workflow = new ArrayList<>();

//         for (TransitionActionReqDto action : transitionActionReqDto) {
//             WorkflowTransitionDto dto = performTransitionAction(action);
//             workflow.add(dto);
//         }

//         return workflow;
//     }

//     @Override
//     public List<pendingRecordsDto> getPendingRecordsForRole(String roleName) {
//         return workflowTransitionRepository.findPendingByNextRole(roleName);
//     }

// }



package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.constant.WorkflowName;
import com.astro.dto.workflow.*;
import com.astro.dto.workflow.ProcurementDtos.ContigencyPurchaseResponseDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.MaterialDetailsResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.PoFormateDto;
import com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto.soWithTenderAndIndentResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.WorkOrderDto.woWithTenderAndIndentResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.pendingRecordsDto;
import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.poWithTenderAndIndentResponseDTO;
import com.astro.entity.*;
import com.astro.entity.ProcurementModule.*;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.*;
import com.astro.repository.AdminPanel.WorkflowBranchMasterRepository;
import com.astro.repository.InventoryModule.PaymentVoucherReposiotry;
import com.astro.repository.ProcurementModule.ContigencyPurchaseRepository;
import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
import com.astro.repository.ProcurementModule.IndentCreation.MaterialDetailsRepository;
import com.astro.repository.ProcurementModule.IndentIdRepository;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;
import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.service.*;
import com.astro.util.CommonUtils;
import com.astro.util.EmailService;
import com.astro.util.TenderEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired
    WorkflowTransitionRepository workflowTransitionRepository;

    @Autowired
    WorkflowMasterRepository workflowMasterRepository;

    @Autowired
    UserService userService;

    @Autowired
    TransitionMasterRepository transitionMasterRepository;

    @Autowired
    RoleMasterRepository roleMasterRepository;

    @Autowired
    TransitionConditionMasterRepository transitionConditionMasterRepository;

    @Autowired
    UserRoleMasterRepository userRoleMasterRepository;

    @Autowired
    IndentCreationService indentCreationService;

    @Autowired
    ContigencyPurchaseService contigencyPurchaseService;

    @Autowired
    @Lazy // added by abhinav
    TenderRequestService tenderRequestService;

    @Autowired
    @Lazy // added by abhinav
    ServiceOrderService serviceOrderService;

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    PurchaseOrderService purchaseOrderService;

    @Autowired
    SubWorkflowTransitionRepository subWorkflowTransitionRepository;

    @Autowired
    private IndentCreationRepository indentCreationRepository;
    @Autowired
    private TenderRequestRepository tenderRequestRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private ContigencyPurchaseRepository contigencyPurchaseRepository;

    @Autowired
    private BranchWorkflowService branchWorkflowService;

    @Autowired
    @Lazy
    private com.astro.service.JobMasterService jobMasterService;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
    private IndentIdRepository indentIdRepository;
    @Autowired
    private VendorMasterUtilService vendorMasterUtilService;

    @Autowired
    private MaterialMasterUtilService materialMasterUtilService;
    @Autowired
    private MaterialMasterUtilRepository materialMasterUtilRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;
    @Autowired
    private ProjectMasterRepository projectMasterRepository;
    @Autowired
    private MaterialDetailsRepository materialDetailsRepo;

    @Autowired
    private EmailService emailService;
    @Autowired
    private TenderEmailService tenderEmailService;

    @Autowired
    private TenderRequestService TRService;
    @Autowired
    private VendorMasterRepository vendorMasterRepository;
    @Autowired
    private IndentIdRepository indentIdtenderIdsRepository;
    @Autowired
    private PaymentVoucherReposiotry paymentVoucherReposiotry;
    // added b y abhinav
    @Autowired
    private IndentAssignmentRepository indentAssignmentRepository;

    @Autowired
    private EmployeeDepartmentMasterRepository employeeDepartmentMasterRepository;

    @Autowired
    private WorkflowBranchMasterRepository workflowBranchMasterRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WorkflowDto workflowByWorkflowName(String workflowName) {
        WorkflowDto workflowDto = null;

        if (Objects.nonNull(workflowName)) {
            WorkflowMaster workflowMaster = workflowMasterRepository.findByWorkflowName(workflowName);
            if (Objects.nonNull(workflowMaster)) {
                workflowDto = new WorkflowDto();
                workflowDto.setWorkflowId(workflowMaster.getWorkflowId());
                workflowDto.setWorkflowName(workflowMaster.getWorkflowName());
                workflowDto.setCreatedBy(workflowMaster.getCreatedBy());
                workflowDto.setCreatedDate(workflowMaster.getCreatedDate());
            } else {
                throw new InvalidInputException(new ErrorDetails(AppConstant.WORKFLOW_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION, "Workflow not found."));
            }
        } else {
            throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid input."));
        }

        return workflowDto;
    }

    @Override
    public List<TransitionDto> transitionsByWorkflowId(Integer workflowId) {

        List<TransitionDto> transitionDtoList = new ArrayList<>();
        List<TransitionMaster> transitionMasterList = transitionMasterRepository.findByWorkflowId(workflowId);

        if (Objects.nonNull(transitionMasterList) && !transitionMasterList.isEmpty()) {
            transitionDtoList = transitionMasterList.stream().map(transitionMaster -> {
                TransitionDto transitionDto = new TransitionDto();
                transitionDto.setWorkflowId(transitionMaster.getWorkflowId());
                transitionDto.setTransitionId(transitionMaster.getTransitionId());
                transitionDto.setTransitionSubOrder(transitionMaster.getTransitionSubOrder());
                transitionDto.setCreatedDate(transitionMaster.getCreatedDate());
                transitionDto.setCreatedBy(transitionMaster.getCreatedBy());
                transitionDto.setTransitionOrder(transitionMaster.getTransitionOrder());
                transitionDto.setConditionId(transitionMaster.getConditionId());
                transitionDto.setCurrentRoleId(transitionMaster.getCurrentRoleId());
                transitionDto.setNextRoleId(transitionMaster.getNextRoleId());
                transitionDto.setPreviousRoleId(transitionMaster.getPreviousRoleId());
                transitionDto.setTransitionName(transitionMaster.getTransitionName());
                transitionDto.setWorkflowName(workflowNameById(transitionMaster.getWorkflowId()));
                transitionDto.setCurrentRoleName(roleNameById(transitionMaster.getCurrentRoleId()));
                transitionDto.setNextRoleName(roleNameById(transitionMaster.getNextRoleId()));
                transitionDto.setPreviousRoleName(roleNameById(transitionMaster.getPreviousRoleId()));
                TransitionConditionDto transitionConditionDto = transitionConditionById(transitionMaster.getConditionId());
                transitionDto.setConditionKey(transitionConditionDto.getConditionKey());
                transitionDto.setConditionValue(transitionConditionDto.getConditionValue());

                return transitionDto;
            }).collect(Collectors.toList());
        } else {
            throw new InvalidInputException(new ErrorDetails(AppConstant.WORKFLOW_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Workflow not found."));
        }
        return transitionDtoList;
    }

    private TransitionConditionDto transitionConditionById(Integer conditionId) {
        TransitionConditionDto transitionConditionDto = new TransitionConditionDto();
        if (Objects.nonNull(conditionId)) {
            TransitionConditionMaster transitionConditionMaster = transitionConditionMasterRepository.findById(conditionId).orElse(null);
            if (Objects.nonNull(transitionConditionMaster)) {
                transitionConditionDto.setConditionId(transitionConditionMaster.getConditionId());
                transitionConditionDto.setConditionKey(transitionConditionMaster.getConditionKey());
                transitionConditionDto.setWorkflowId(transitionConditionMaster.getWorkflowId());
                transitionConditionDto.setConditionValue(transitionConditionMaster.getConditionValue());
                transitionConditionDto.setCreatedDate(transitionConditionMaster.getCreatedDate());
                transitionConditionDto.setCreatedBy(transitionConditionMaster.getCreatedBy());
            }
        }

        return transitionConditionDto;
    }

    private String roleNameById(Integer roleId) {
        if (Objects.nonNull(roleId)) {
            return roleMasterRepository.findById(roleId).orElse(new RoleMaster()).getRoleName();
        } else {
            return null;
        }
    }

    private String roleNameByUserId(Integer userId) {
        if (Objects.nonNull(userId)) {
            UserRoleMaster userRoleMaster = userRoleMasterRepository.findByUserId(userId);
            if (Objects.nonNull(userRoleMaster)) {
                return roleMasterRepository.findById(userRoleMaster.getRoleId()).orElse(new RoleMaster()).getRoleName();
            }
        }
        return null;
    }

    private String workflowNameById(Integer workflowId) {
        if (Objects.nonNull(workflowId)) {
            return workflowMasterRepository.findById(workflowId).orElse(new WorkflowMaster()).getWorkflowName();
        } else {
            return null;
        }
    }

    @Override
    public TransitionDto transitionsByWorkflowIdAndOrder(Integer workflowId, Integer order, Integer subOrder) {
        TransitionDto transitionDto = null;
        TransitionMaster transitionMaster = transitionMasterRepository.findByWorkflowIdAndTransitionOrderAndTransitionSubOrder(workflowId, order, subOrder);
        if (Objects.nonNull(transitionMaster)) {
            transitionDto = new TransitionDto();
            transitionDto.setWorkflowId(transitionMaster.getWorkflowId());
            transitionDto.setTransitionId(transitionMaster.getTransitionId());
            transitionDto.setTransitionSubOrder(transitionMaster.getTransitionSubOrder());
            transitionDto.setCreatedDate(transitionMaster.getCreatedDate());
            transitionDto.setCreatedBy(transitionMaster.getCreatedBy());
            transitionDto.setTransitionOrder(transitionMaster.getTransitionOrder());
            transitionDto.setConditionId(transitionMaster.getConditionId());
            transitionDto.setCurrentRoleId(transitionMaster.getCurrentRoleId());
            transitionDto.setNextRoleId(transitionMaster.getNextRoleId());
            transitionDto.setPreviousRoleId(transitionMaster.getPreviousRoleId());
            transitionDto.setTransitionName(transitionMaster.getTransitionName());
            transitionDto.setWorkflowName(workflowNameById(transitionMaster.getWorkflowId()));
            transitionDto.setCurrentRoleName(roleNameById(transitionMaster.getCurrentRoleId()));
            transitionDto.setNextRoleName(roleNameById(transitionMaster.getNextRoleId()));
            transitionDto.setPreviousRoleName(roleNameById(transitionMaster.getPreviousRoleId()));
            TransitionConditionDto transitionConditionDto = transitionConditionById(transitionMaster.getConditionId());
            transitionDto.setConditionKey(transitionConditionDto.getConditionKey());
            transitionDto.setConditionValue(transitionConditionDto.getConditionValue());
        }
        return transitionDto;
    }

    @Override
    @Transactional
    public WorkflowTransitionDto initiateWorkflow(String requestId, String workflowName, Integer createdBy) {
        System.err.println("🚀🚀🚀 WORKFLOW INITIATION - RequestID: " + requestId + ", Workflow: " + workflowName);
        WorkflowTransitionDto workflowTransitionDto = null;
        if (Objects.nonNull(requestId) && Objects.nonNull(workflowName) && Objects.nonNull(createdBy)) {
            userService.validateUser(createdBy);
            WorkflowDto workflowDto = workflowByWorkflowName(workflowName);

            // validateWorkflowTransition(requestId, createdBy, workflowDto.getWorkflowId());
            //updated by abhinav
            List<WorkflowTransition> existingList
                    = workflowTransitionRepository.findByWorkflowIdAndCreatedByAndRequestId(
                            workflowDto.getWorkflowId(), createdBy, requestId);

            WorkflowTransition existing = existingList.isEmpty() ? null : existingList.get(0);

            if (existing != null) {
                // Workflow already initiated, return existing
                return mapToWorkflowTransitionDto(existing);
            }

            // ===== BRANCH-BASED WORKFLOW ROUTING =====
            WorkflowTransition workflowTransition = initiateBranchBasedWorkflow(requestId, workflowDto, createdBy);

            if (workflowTransition == null) {
                // Fallback to old TransitionMaster system if no branch found
                System.err.println("⚠️⚠️⚠️ NO BRANCH MATCHED - Falling back to old TransitionMaster system for " + requestId);
                System.err.println("⚠️ Check: 1) Active branches exist, 2) Conditions match request data, 3) Data types match");
                TransitionDto transitionDto = transitionsByWorkflowIdAndOrder(workflowDto.getWorkflowId(), 1, 1);
                if (Objects.isNull(transitionDto)) {
                    throw new InvalidInputException(new ErrorDetails(AppConstant.TRANSITION_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION, "Transition not found."));
                }
                workflowTransition = createWorkflowTransition(requestId, workflowDto, transitionDto, createdBy);
            } else {
                System.out.println("BRANCH-BASED WORKFLOW - Branch ID: " + workflowTransition.getBranchId()
                        + ", Next: " + workflowTransition.getNextRole());
            }

            workflowTransitionRepository.save(workflowTransition);
            workflowTransitionDto = mapWorkflowTransitionDto(workflowTransition);

            if (WorkflowName.TENDER_EVALUATOR.getValue().equalsIgnoreCase(workflowName)) {
                workflowTransition.setModifiedBy(createdBy);
                List<SubWorkflowTransitionDto> list = validateTenderWorkFlow(null, workflowTransition, null);
                list.forEach(dto -> {
                    if (dto != null) {
                        try {
                            emailService.sendSubWorkflowEmail(dto);
                        } catch (Exception e) {
                            // log error
                        }
                    }
                });
            }

        } else {
            throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid input."));
        }

        if (workflowTransitionDto != null) {
            try {
                emailService.sendWorkflowEmail(workflowTransitionDto);
            } catch (Exception e) {
                // log error
            }
        }
        return workflowTransitionDto;
    }

    /**
     * Checks if the given role name represents a Reporting Officer approver
     * type.
     */
    private boolean isReportingOfficerRole(String roleName) {
        return roleName != null && roleName.trim().equalsIgnoreCase("Reporting Officer");
    }

    /**
     * Resolves the reporting officer's user ID for the given indent creator.
     * Flow: creatorUserId -> UserMaster.employeeId ->
     * EmployeeDepartmentMaster.reportingOfficerId -> UserMaster.userId Returns
     * a map with keys: userId (Integer), employeeId (String), employeeName
     * (String) Throws BusinessException if the reporting officer or their user
     * account cannot be found.
     */
    private Map<String, Object> resolveReportingOfficer(Integer creatorUserId) {
        // Step 1: Find the creator's user record to get their employeeId
        UserMaster creatorUser = userMasterRepository.findByUserId(creatorUserId);
        if (creatorUser == null || creatorUser.getEmployeeId() == null || creatorUser.getEmployeeId().trim().isEmpty()) {
            throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot resolve reporting officer: Indent creator (userId=" + creatorUserId
                    + ") does not have a linked employee record."));
        }

        // Step 2: Find the creator's employee record to get the reporting officer's employeeId
        EmployeeDepartmentMaster creatorEmployee = employeeDepartmentMasterRepository
                .findByEmployeeId(creatorUser.getEmployeeId())
                .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                "Cannot resolve reporting officer: Employee record not found for employeeId="
                + creatorUser.getEmployeeId())));

        String reportingOfficerEmpId = creatorEmployee.getReportingOfficerId();
        if (reportingOfficerEmpId == null || reportingOfficerEmpId.trim().isEmpty()) {
            throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot resolve reporting officer: No reporting officer assigned to employee "
                    + creatorEmployee.getEmployeeName() + " (" + creatorEmployee.getEmployeeId() + ")."));
        }

        // Step 3: Find the reporting officer's employee record (for name)
        EmployeeDepartmentMaster reportingOfficerEmployee = employeeDepartmentMasterRepository
                .findByEmployeeId(reportingOfficerEmpId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                "Cannot resolve reporting officer: Employee record not found for reporting officer employeeId="
                + reportingOfficerEmpId)));

        // Step 4: Find the reporting officer's user account
        UserMaster reportingOfficerUser = userMasterRepository.findByEmployeeId(reportingOfficerEmpId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                "Cannot resolve reporting officer: No user account found for reporting officer "
                + reportingOfficerEmployee.getEmployeeName() + " (" + reportingOfficerEmpId + "). "
                + "Please create a user account for this employee.")));

        Map<String, Object> result = new HashMap<>();
        result.put("userId", reportingOfficerUser.getUserId());
        result.put("employeeId", reportingOfficerEmpId);
        result.put("employeeName", reportingOfficerEmployee.getEmployeeName());

        System.out.println("✅ Resolved reporting officer for creator userId=" + creatorUserId
                + " → " + reportingOfficerEmployee.getEmployeeName() + " (userId=" + reportingOfficerUser.getUserId() + ")");

        return result;
    }

    /**
     * Checks if the given role name represents a Project Head approver type.
     */
    private boolean isProjectHeadRole(String roleName) {
        return roleName != null && roleName.trim().equalsIgnoreCase("Project Head");
    }

    /**
     * Resolves the project head's user ID for the given indent request. Flow:
     * requestId -> IndentCreation.projectCode -> ProjectMaster.projectHead
     * (employeeId) -> UserMaster.userId Returns a map with keys: userId
     * (Integer), employeeId (String), employeeName (String) Throws
     * BusinessException if the project head or their user account cannot be
     * found.
     */
    private Map<String, Object> resolveProjectHead(String requestId) {
        // Step 1: Find the indent to get the project code
        IndentCreation indent = indentCreationRepository.findByIndentId(requestId);
        if (indent == null) {
            throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot resolve project head: Indent not found for requestId=" + requestId));
        }

        if (indent.getProjectCode() == null || indent.getProjectCode().trim().isEmpty()) {
            throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot resolve project head: Indent " + requestId + " is not associated with any project."));
        }

        // Step 2: Find the project master to get the project head's employee ID
        ProjectMaster project = projectMasterRepository.findByProjectCode(indent.getProjectCode())
                .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                "Cannot resolve project head: Project not found for projectCode=" + indent.getProjectCode())));

        String projectHeadEmpId = project.getProjectHead();
        if (projectHeadEmpId == null || projectHeadEmpId.trim().isEmpty()) {
            throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot resolve project head: No project head assigned to project "
                    + project.getProjectNameDescription() + " (" + project.getProjectCode() + ")."));
        }

        // Step 3: Find the project head's employee record (for name)
        String projectHeadName = project.getProjectHeadName();
        if (projectHeadName == null || projectHeadName.trim().isEmpty()) {
            // Fallback: look up from employee table
            EmployeeDepartmentMaster projectHeadEmployee = employeeDepartmentMasterRepository
                    .findByEmployeeId(projectHeadEmpId)
                    .orElse(null);
            if (projectHeadEmployee != null) {
                projectHeadName = projectHeadEmployee.getEmployeeName();
            } else {
                projectHeadName = projectHeadEmpId;
            }
        }

        // Step 4: Find the project head's user account
        UserMaster projectHeadUser = userMasterRepository.findByEmployeeId(projectHeadEmpId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                "Cannot resolve project head: No user account found for project head "
                + project.getProjectHeadName() + " (" + projectHeadEmpId + "). "
                + "Please create a user account for this employee.")));

        Map<String, Object> result = new HashMap<>();
        result.put("userId", projectHeadUser.getUserId());
        result.put("employeeId", projectHeadEmpId);
        result.put("employeeName", projectHeadName);

        System.out.println("Resolved project head for requestId=" + requestId
                + " → " + projectHeadName + " (userId=" + projectHeadUser.getUserId() + ")");

        return result;
    }

    /**
     * Checks if the given role name is a location-based role (Professor In
     * Charge or Engineer In Charge). These roles are resolved based on the
     * indent's consignee location matching the employee's location.
     */
    private boolean isLocationBasedRole(String roleName) {
        if (roleName == null) {
            return false;
        }
        String trimmed = roleName.trim().toLowerCase();
        return trimmed.equals("professor in charge") || trimmed.equals("engineer in charge");
    }

    /**
     * Resolves the location-based approver (Professor In Charge or Engineer In
     * Charge) for the given indent. Flow: requestId ->
     * IndentCreation.consignesLocation -> find User with matching role AND
     * employee location Returns a map with keys: userId (Integer), employeeId
     * (String), employeeName (String) Throws BusinessException if no matching
     * user is found.
     */
    private Map<String, Object> resolveLocationBasedApprover(String requestId, String roleName) {
        // Step 1: Find the indent to get the consignee location
        IndentCreation indent = indentCreationRepository.findByIndentId(requestId);
        if (indent == null) {
            throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot resolve " + roleName + ": Indent not found for requestId=" + requestId));
        }

        String location = indent.getConsignesLocation();
        if (location == null || location.trim().isEmpty()) {
            throw new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot resolve " + roleName + ": Indent " + requestId + " has no consignee location set."));
        }

        // Step 2: Find user with the given role whose employee is at the same location
        UserMaster matchingUser = userMasterRepository.findByRoleNameAndEmployeeLocation(roleName, location)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_VALIDATION,
                "Cannot resolve " + roleName + ": No user with role '" + roleName
                + "' found at location '" + location + "'. "
                + "Please ensure an employee at this location has a user account with the '" + roleName + "' role.")));

        // Step 3: Get the employee name for display
        String employeeName = roleName;
        if (matchingUser.getEmployeeId() != null) {
            EmployeeDepartmentMaster employee = employeeDepartmentMasterRepository
                    .findByEmployeeId(matchingUser.getEmployeeId())
                    .orElse(null);
            if (employee != null) {
                employeeName = employee.getEmployeeName();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", matchingUser.getUserId());
        result.put("employeeId", matchingUser.getEmployeeId());
        result.put("employeeName", employeeName);

        System.out.println("Resolved " + roleName + " for requestId=" + requestId
                + " (location=" + location + ") → " + employeeName + " (userId=" + matchingUser.getUserId() + ")");

        return result;
    }

    /**
     * BRANCH-BASED WORKFLOW INITIATION Matches workflow branch based on
     * conditions and routes to first approver
     */
    private WorkflowTransition initiateBranchBasedWorkflow(String requestId, WorkflowDto workflowDto, Integer createdBy) {
        try {
            System.out.println("Initiating workflow for " + requestId);
            System.out.println(" WORKFLOW NAME RECEIVED: " + workflowDto.getWorkflowName()); // updated by abhinav

            // Build conditions based on workflow type
            Map<String, Object> conditions = buildConditionsForWorkflow(workflowDto.getWorkflowName(), requestId);

            // Resolve indentorDepartment BEFORE branch matching so department-filtered branches work correctly.
            // Without this, branches with "indentorDepartment" conditions (e.g. B46 for Tech dept)
            // would never match because the dept is null at the time findMatchingBranch runs.
            String indentorDepartment = (String) conditions.get("indentorDepartment");
            if ((indentorDepartment == null || indentorDepartment.trim().isEmpty()) && createdBy != null) {
                try {
                    com.astro.entity.UserMaster creatorUser = userMasterRepository.findByUserId(createdBy);
                    if (creatorUser != null && creatorUser.getEmployeeId() != null) {
                        employeeDepartmentMasterRepository.findByEmployeeId(creatorUser.getEmployeeId())
                                .ifPresent(emp -> {
                                    conditions.put("indentorDepartment", emp.getDepartmentName());
                                    System.out.println("✅ Resolved indentorDepartment (pre-branch-match fallback): "
                                            + emp.getDepartmentName());
                                });
                        indentorDepartment = (String) conditions.get("indentorDepartment");
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Could not resolve indentorDepartment fallback: " + e.getMessage());
                }
            }

            System.out.println("📋 Conditions: " + conditions);

            // Find matching branch (now with resolved department info)
            com.astro.entity.AdminPanel.WorkflowBranchMaster matchedBranch
                    = branchWorkflowService.findMatchingBranch(workflowDto.getWorkflowId(), conditions);

            if (matchedBranch == null) {
                System.out.println("⚠️ No matching branch found for " + workflowDto.getWorkflowName());
                return null;
            }

            System.out.println("🔥 MATCHED BRANCH:");
            System.out.println("   BranchId: " + matchedBranch.getBranchId());
            System.out.println("   BranchCode: " + matchedBranch.getBranchCode());

            Object totalAmountObj = conditions.get("totalAmount");
            java.math.BigDecimal totalAmount = totalAmountObj != null
                    ? new java.math.BigDecimal(totalAmountObj.toString())
                    : java.math.BigDecimal.ZERO;

            com.astro.entity.AdminPanel.ApproverMaster firstApprover
                    = (indentorDepartment != null && !indentorDepartment.trim().isEmpty())
                    ? branchWorkflowService.getFirstApproverForDepartment(matchedBranch.getBranchId(), indentorDepartment, totalAmount)
                    : branchWorkflowService.getFirstApprover(matchedBranch.getBranchId());

            if (firstApprover == null) {
                System.out.println("⚠️ No approvers configured for branch " + matchedBranch.getBranchCode());
                return null;
            }

            // Create workflow transition with branch info
            WorkflowTransition transition = new WorkflowTransition();
            transition.setRequestId(requestId);
            transition.setWorkflowId(workflowDto.getWorkflowId());
            transition.setWorkflowName(workflowDto.getWorkflowName());
            transition.setCreatedBy(createdBy);
            transition.setCreatedDate(new Date());
            transition.setStatus(AppConstant.CREATED_TYPE);
            transition.setAction(AppConstant.CREATED_TYPE);
            // transition.setCurrentRole("Indent Creator");
            // updated by abhinav
            if (workflowDto.getWorkflowName().toUpperCase().contains("TENDER")) {
                transition.setCurrentRole("Tender Creator");
            } else if (workflowDto.getWorkflowName().toUpperCase().contains("INDENT")) {
                transition.setCurrentRole("Indent Creator");
            } else {
                transition.setCurrentRole("Request Creator");
            }
            // updated by abhinav end here
            System.out.println("🔥 FIRST APPROVER DEBUG:");
            System.out.println("   Role: " + firstApprover.getRoleName());
            System.out.println("   Level: " + firstApprover.getApprovalLevel());
            System.out.println("   Sequence: " + firstApprover.getApprovalSequence());
            System.out.println("   BranchId: " + matchedBranch.getBranchId());

            transition.setNextRole(firstApprover.getRoleName());
            transition.setNextAction(AppConstant.PENDING_TYPE);
            transition.setTransitionOrder(1);
            transition.setTransitionSubOrder(1);
            transition.setWorkflowSequence(1);

            // Set branch-based fields
            transition.setBranchId(matchedBranch.getBranchId());
            transition.setApproverId(firstApprover.getApproverId());
            transition.setApprovalLevel(firstApprover.getApprovalLevel());
            transition.setApprovalSequence(firstApprover.getApprovalSequence());

            // Reporting Officer: resolve and assign to specific user
            if (isReportingOfficerRole(firstApprover.getRoleName())) {
                Map<String, Object> roInfo = resolveReportingOfficer(createdBy);
                transition.setAssignedToUserId((Integer) roInfo.get("userId"));
                transition.setAssignedToEmployeeId((String) roInfo.get("employeeId"));
                System.out.println("Reporting Officer assigned: " + roInfo.get("employeeName")
                        + " (userId=" + roInfo.get("userId") + ")");
            }

            // Project Head: resolve and assign to specific project head user
            if (isProjectHeadRole(firstApprover.getRoleName())) {
                Map<String, Object> phInfo = resolveProjectHead(requestId);
                transition.setAssignedToUserId((Integer) phInfo.get("userId"));
                transition.setAssignedToEmployeeId((String) phInfo.get("employeeId"));
                System.out.println("Project Head assigned: " + phInfo.get("employeeName")
                        + " (userId=" + phInfo.get("userId") + ")");
            }

            // Location-based roles (Professor In Charge / Engineer In Charge):
            // resolve by matching consignee location with employee location
            if (isLocationBasedRole(firstApprover.getRoleName())) {
                Map<String, Object> locInfo = resolveLocationBasedApprover(requestId, firstApprover.getRoleName());
                transition.setAssignedToUserId((Integer) locInfo.get("userId"));
                transition.setAssignedToEmployeeId((String) locInfo.get("employeeId"));
                System.out.println(firstApprover.getRoleName() + " assigned: " + locInfo.get("employeeName")
                        + " (userId=" + locInfo.get("userId") + ")");
            }

            System.out.println("✅ Workflow initiated with branch: " + matchedBranch.getBranchCode()
                    + " (ID: " + matchedBranch.getBranchId() + ")"
                    + ", First approver: " + firstApprover.getRoleName()
                    + " (Level: " + firstApprover.getApprovalLevel()
                    + ", Seq: " + firstApprover.getApprovalSequence() + ")");

            return transition;

        } catch (BusinessException e) {
            // Re-throw business exceptions (e.g., reporting officer not found)
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error in branch-based workflow initiation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> buildConditionsForWorkflow(String workflowName, String requestId) {
        String workflowNameUpper = workflowName.toUpperCase();
        if (workflowNameUpper.contains("INDENT")) {
            return branchWorkflowService.buildIndentConditions(requestId);
            // added by abhinav new from here
        } //  else if (workflowNameUpper.contains("TENDER")) {
        //     return branchWorkflowService.buildTenderConditions(requestId);
        // } else if (workflowNameUpper.contains("PURCHASE") || workflowNameUpper.contains("PO")) {
        //     return branchWorkflowService.buildPOConditions(requestId);
        // } else if (workflowNameUpper.contains("CONTINGENCY")) {
        //     return branchWorkflowService.buildPOConditions(requestId);
        // }
        // if ("INDENT APPROVAL WORKFLOW".equalsIgnoreCase(workflowName)) {
        //     return branchWorkflowService.buildIndentConditions(requestId);
        // } 
        else if (workflowNameUpper.contains("TENDER")
                && workflowNameUpper.contains("APPROVER")) {

            return branchWorkflowService.buildTenderConditions(requestId);
            // Modified by Aman changed Purchase to PO 
        } else if (workflowNameUpper.contains("PO")) {
 
 return branchWorkflowService.buildPOConditions(requestId);
        } else if (workflowNameUpper.contains("SO")){
            return branchWorkflowService.buildSOConditions(requestId);
        } else if (workflowNameUpper.contains("Payment")){
            return branchWorkflowService.buildPaymentConditions(requestId);
        }

        System.out.println("❌ No matching workflow found for: " + workflowName);
        return new HashMap<>();
    }

    // private void validateWorkflowTransition(String requestId, Integer createdBy, Integer workflowId) {
    //     WorkflowTransition workflowTransition = workflowTransitionRepository.findByWorkflowIdAndCreatedByAndRequestId(workflowId, createdBy, requestId);
    //     if (Objects.nonNull(workflowTransition)) {
    //         throw new InvalidInputException(new ErrorDetails(AppConstant.WORKFLOW_ALREADY_EXISTS, AppConstant.ERROR_TYPE_CODE_VALIDATION,
    //                 AppConstant.ERROR_TYPE_VALIDATION, "Workflow with same request id and created by already exists."));
    //     }
    // }
    @Override
    public List<WorkflowTransitionDto> workflowTransitionHistory(String requestId) {

        List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();
        List<WorkflowTransition> workflowTransitionList = null;
        workflowTransitionList = workflowTransitionRepository.findByRequestId(requestId);
        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
            workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowSequence).reversed()).map(e -> {
                return mapWorkflowTransitionDto(e);
            }).collect(Collectors.toList());
        }

        return workflowTransitionDtoList;
    }

    @Override
    public List<WorkflowTransitionDto> allWorkflowTransition(String roleName) {
        List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();

        List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByNextRole(roleName);
        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
            workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate)).map(e -> {
                return mapWorkflowTransitionDto(e);
            }).collect(Collectors.toList());
        }
        return workflowTransitionDtoList;
    }

    @Override
    public List<WorkflowTransitionDto> allPendingWorkflowTransition(String roleName) {
        return allPendingWorkflowTransition(roleName, null);
    }

    @Override
    public List<WorkflowTransitionDto> allPendingWorkflowTransition(String roleName, Integer userId) {
        List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();

        List<WorkflowTransition> workflowTransitionList;
        if (userId != null) {
            workflowTransitionList = workflowTransitionRepository.findPendingByRoleAndOptionalUser(AppConstant.PENDING_TYPE, roleName, userId);
        } else {
            workflowTransitionList = workflowTransitionRepository.findByNextActionAndNextRole(AppConstant.PENDING_TYPE, roleName);
        }

        // Filter by user's employee location against branch location condition
        if (userId != null && workflowTransitionList != null && !workflowTransitionList.isEmpty()) {
            UserMaster transUser = userMasterRepository.findByUserId(userId);
            if (transUser != null && transUser.getEmployeeId() != null) {
                EmployeeDepartmentMaster transEmp = employeeDepartmentMasterRepository.findByEmployeeId(transUser.getEmployeeId()).orElse(null);
                if (transEmp != null && transEmp.getLocation() != null) {
                    String userLocation = transEmp.getLocation().trim().toUpperCase();
                    workflowTransitionList = workflowTransitionList.stream()
                            .filter(wt -> {
                                if (wt.getBranchId() == null) {
                                    return true;
                                }
                                return workflowBranchMasterRepository.findById(wt.getBranchId())
                                        .map(branch -> {
                                            if (branch.getConditionConfig() == null || branch.getConditionConfig().trim().isEmpty()) {
                                                return true;
                                            }
                                            try {
                                                Map<String, Object> config = objectMapper.readValue(branch.getConditionConfig(), new TypeReference<Map<String, Object>>() {
                                                });
                                                if (!config.containsKey("location")) {
                                                    return true;
                                                }
                                                String branchLocation = String.valueOf(config.get("location")).trim().toUpperCase();
                                                return branchLocation.equals(userLocation);
                                            } catch (Exception e) {
                                                return true;
                                            }
                                        })
                                        .orElse(true);
                            })
                            .collect(Collectors.toList());
                }
            }
        }

        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
            workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate)).map(e -> {
                return mapWorkflowTransitionDto(e);
            }).collect(Collectors.toList());
        }

        return workflowTransitionDtoList;
    }

    /*   @Override
       public List<QueueResponse> allCompletedWorkflowTransition(String roleName) {
           List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();
           List<QueueResponse> queueResponseList = new ArrayList<>();

           int workflowId =1;
        //   List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByStatusAndWorkflowId(AppConstant.COMPLETED_TYPE, workflowId);
        /*   if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
               workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate)).map(e -> {
                   return mapWorkflowTransitionDto(e);
               }).collect(Collectors.toList());
           }*

     */ /* List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findValidTransitions(AppConstant.COMPLETED_TYPE, workflowId);

        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
            queueResponseList = workflowTransitionList.stream()
                    .sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate))
                    .map(this::mapToQueueResponse)
                    .collect(Collectors.toList());
        }

        return queueResponseList;
    }*/
 /*public List<QueueResponse> allCompletedWorkflowTransition(String roleName) {
     int workflowId = 1;

     return workflowTransitionRepository.findValidTransitions(AppConstant.COMPLETED_TYPE, workflowId)
             .stream()
             .filter(transition -> !indentCreationRepository.isAssigned(transition.getRequestId())) // only not assigned
             .sorted(Comparator.comparing(WorkflowTransition::getRequestId)
                     .thenComparing(WorkflowTransition::getCreatedDate))
             .map(this::mapToQueueResponse)
             .collect(Collectors.toList());
 }*/
 /*
 public List<QueueResponse> allCompletedWorkflowTransition(String roleName) {
     int workflowId = 1; // fetch dynamically if needed
     return workflowTransitionRepository.findValidTransitions(AppConstant.COMPLETED_TYPE, workflowId)
             .stream()
             .sorted(Comparator.comparing(WorkflowTransition::getRequestId)
                     .thenComparing(WorkflowTransition::getCreatedDate))
             .map(this::mapToQueueResponse)
             .collect(Collectors.toList());

 }*/
    public List<CompletedIndentsQueueResponse> allCompletedWorkflowTransition(String roleName) {
        int workflowId = 1; // fetch dynamically if needed
        return workflowTransitionRepository.findCompletedIndents(AppConstant.COMPLETED_TYPE, workflowId);
    }

    public List<QueueResponse> allCancelledIndents() {
        List<IndentCreation> cancelledIndents = indentCreationRepository.findAllByCancelStatusTrue();

        List<QueueResponse> responses = cancelledIndents.stream()
                .filter(indent -> {
                    // Fetch the latest workflow transition by workflowTransitionId
                    Optional<WorkflowTransition> lastTransitionOpt
                            = workflowTransitionRepository.findFirstByRequestIdOrderByWorkflowTransitionIdDesc(indent.getIndentId());

                    // Include only if last transition is NOT "Canceled"
                    return lastTransitionOpt.map(t -> !"Canceled".equalsIgnoreCase(t.getStatus()))
                            .orElse(true); // If no transition exists, include it
                })
                .map(indent -> {
                    QueueResponse response = new QueueResponse();
                    response.setRequestId(indent.getIndentId());
                    response.setIndentorName(indent.getIndentorName());
                    response.setAction("Indentor Cancelled");
                    response.setStatus("Indentor Cancelled");
                    response.setWorkflowName("Indent Workflow");
                    response.setAmount(indent.getTotalIntentValue());
                    response.setProjectName(indent.getProjectName());
                    response.setModeOfProcurement("");
                    response.setConsignee(indent.getConsignesLocation());
                    response.setCreatedDate(new Date());
                    response.setWorkflowId(1);
                    return response;
                })
                .collect(Collectors.toList());

        return responses;
    }

    /* @Override
    public List<String> allPreviousRoleWorkflowTransition(Integer workflowId, String requestId) {
        List<String> allPreviousRole = new ArrayList<>();

        List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByWorkflowIdAndRequestId(workflowId, requestId);
        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty() && workflowTransitionList.size() > 1) {
            allPreviousRole = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowTransitionId)).limit(workflowTransitionList.size() - 1).map(e -> e.getCurrentRole()).distinct().collect(Collectors.toList());
        }
        return allPreviousRole;
    }
     */
    public List<String> allPreviousRoleWorkflowTransition(Integer workflowId, String requestId) {
        List<String> allPreviousRole = new ArrayList<>();

        List<WorkflowTransition> workflowTransitionList
                = workflowTransitionRepository.findByWorkflowIdAndRequestId(workflowId, requestId);

        if (workflowTransitionList != null && !workflowTransitionList.isEmpty()) {
            allPreviousRole = workflowTransitionList.stream()
                    .sorted(Comparator.comparing(WorkflowTransition::getWorkflowTransitionId))
                    .map(WorkflowTransition::getCurrentRole)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
        }

        return allPreviousRole;
    }

    private WorkflowTransitionDto mapWorkflowTransitionDto(WorkflowTransition workflowTransition) {
        WorkflowTransitionDto workflowTransitionDto = new WorkflowTransitionDto();
        workflowTransitionDto.setWorkflowTransitionId(workflowTransition.getWorkflowTransitionId());
        workflowTransitionDto.setTransitionId(workflowTransition.getTransitionId());
        workflowTransitionDto.setWorkflowId(workflowTransition.getWorkflowId());
        workflowTransitionDto.setWorkflowName(workflowTransition.getWorkflowName());
        workflowTransitionDto.setModificationDate(workflowTransition.getModificationDate());
        workflowTransitionDto.setCreatedBy(workflowTransition.getCreatedBy());
        workflowTransitionDto.setTransitionOrder(workflowTransition.getTransitionOrder());
        workflowTransitionDto.setRequestId(workflowTransition.getRequestId());
        workflowTransitionDto.setStatus(workflowTransition.getStatus());
        workflowTransitionDto.setTransitionSubOrder(workflowTransition.getTransitionSubOrder());
        workflowTransitionDto.setCreatedDate(workflowTransition.getCreatedDate());
        workflowTransitionDto.setModifiedBy(workflowTransition.getModifiedBy());
        workflowTransitionDto.setNextAction(workflowTransition.getNextAction());
        workflowTransitionDto.setCreatedRole(roleNameById(workflowTransition.getCreatedBy()));
        workflowTransitionDto.setModifiedRole(roleNameById(workflowTransition.getModifiedBy()));
        workflowTransitionDto.setCurrentRole(workflowTransition.getCurrentRole());
        workflowTransitionDto.setNextRole(workflowTransition.getNextRole());
        workflowTransitionDto.setWorkflowSequence(workflowTransition.getWorkflowSequence());
        workflowTransitionDto.setAction(workflowTransition.getAction());
        workflowTransitionDto.setRemarks(workflowTransition.getRemarks());

        // For branch-based workflows, transitionId is null
        if (workflowTransition.getTransitionId() != null) {
            TransitionMaster transitionMaster = transitionById(workflowTransition.getTransitionId());
            if (Objects.nonNull(transitionMaster)) {
                workflowTransitionDto.setNextActionId(transitionMaster.getNextRoleId());
                workflowTransitionDto.setNextActionRole(roleNameById(transitionMaster.getNextRoleId()));
            }
        }

        // Reporting Officer assignment fields
        workflowTransitionDto.setAssignedToUserId(workflowTransition.getAssignedToUserId());
        workflowTransitionDto.setAssignedToEmployeeId(workflowTransition.getAssignedToEmployeeId());
        if (workflowTransition.getAssignedToEmployeeId() != null) {
            employeeDepartmentMasterRepository.findByEmployeeId(workflowTransition.getAssignedToEmployeeId())
                    .ifPresent(emp -> workflowTransitionDto.setAssignedToEmployeeName(emp.getEmployeeName()));
        }

        return workflowTransitionDto;
    }

    private TransitionMaster transitionById(Integer transitionId) {
        return transitionMasterRepository.findById(transitionId).orElse(null);
    }

    private WorkflowTransition createWorkflowTransition(String requestId, WorkflowDto workflowDto, TransitionDto transitionDto, Integer createdBy) {
        WorkflowTransition workflowTransition = new WorkflowTransition();
        workflowTransition.setTransitionId(transitionDto.getTransitionId());
        workflowTransition.setWorkflowId(workflowDto.getWorkflowId());
        workflowTransition.setTransitionOrder(transitionDto.getTransitionOrder());
        workflowTransition.setTransitionSubOrder(transitionDto.getTransitionSubOrder());
        workflowTransition.setStatus(AppConstant.CREATED_TYPE);
        workflowTransition.setAction(AppConstant.CREATED_TYPE);
        workflowTransition.setNextAction(AppConstant.PENDING_TYPE);
        workflowTransition.setCreatedDate(new Date());
        workflowTransition.setCreatedBy(createdBy);
        workflowTransition.setModifiedBy(null);
        workflowTransition.setModificationDate(null);
        workflowTransition.setRequestId(requestId);
        workflowTransition.setWorkflowName(workflowDto.getWorkflowName());
        workflowTransition.setCurrentRole(transitionDto.getCurrentRoleName());
        workflowTransition.setNextRole(transitionDto.getNextRoleName());
        workflowTransition.setWorkflowSequence(1);

        return workflowTransition;
    }

    @Override
    public TransitionDto nextTransition(Integer workflowId, String workflowName, String currentRole, String requestId) {
        TransitionDto transitionDto = null;

        if (Objects.nonNull(workflowId) && Objects.nonNull(currentRole)) {
            List<TransitionDto> nextTransitionDtoList = transitionsByWorkflowId(workflowId).stream().filter(e -> currentRole.equalsIgnoreCase(e.getCurrentRoleName())).sorted(Comparator.comparing(s -> s.getTransitionSubOrder())).collect(Collectors.toList());
            transitionDto = nextTransitionDto(nextTransitionDtoList, workflowName, requestId);
        } else {
            throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid input."));
        }
        return transitionDto;
    }

    @Override
    @Transactional
    public WorkflowTransitionDto performTransitionAction(TransitionActionReqDto transitionActionReqDto) {
        userService.validateUser(transitionActionReqDto.getActionBy());
        WorkflowTransition workflowTransition = workflowTransitionRepository.findByWorkflowTransitionIdAndRequestId(transitionActionReqDto.getWorkflowTransitionId(), transitionActionReqDto.getRequestId());
        if (Objects.isNull(workflowTransition)) {
            throw new InvalidInputException(new ErrorDetails(AppConstant.INVALID_WORKFLOW_TRANSITION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Workflow transition not found.With given workflow transition id and request id."));
        }

        // Recovery: if a "Change requested" return transition was saved without branchId
        // (created before the fix), recover branchId from any sibling transition that has one.
        // Level/seq are set to 0 so getNextApprover restarts from the first approver (RO),
        // not from wherever the approver who sent the change request was in the chain.
        if (workflowTransition.getBranchId() == null && workflowTransition.getTransitionId() == null) {
            workflowTransitionRepository.findByRequestId(workflowTransition.getRequestId())
                .stream()
                .filter(wt -> wt.getBranchId() != null
                        && !wt.getWorkflowTransitionId().equals(workflowTransition.getWorkflowTransitionId()))
                .findFirst()
                .ifPresent(sibling -> {
                    workflowTransition.setBranchId(sibling.getBranchId());
                    workflowTransition.setApprovalLevel(0);
                    workflowTransition.setApprovalSequence(0);
                    workflowTransitionRepository.save(workflowTransition);
                });
        }

        // For branch-based workflows, transitionId is null
        TransitionMaster currentTransition = null;
        if (workflowTransition.getTransitionId() != null) {
            currentTransition = transitionMasterRepository.findById(workflowTransition.getTransitionId()).orElse(null);
        }

        // Only validate user role for legacy (non-branch) workflows
        if (currentTransition != null) {
            if (workflowTransition.getWorkflowId() == 7 && workflowTransition.getCurrentRole().equalsIgnoreCase("Tender Evaluator") && workflowTransition.getNextRole().equalsIgnoreCase("Tender Evaluator")
                    || workflowTransition.getWorkflowId() == 1 && workflowTransition.getAction().equalsIgnoreCase("Change requested") && workflowTransition.getNextRole().equalsIgnoreCase("Indent Creator")
                    || workflowTransition.getWorkflowId() == 3 && workflowTransition.getAction().equalsIgnoreCase("Change requested") && workflowTransition.getNextRole().equalsIgnoreCase("PO Creator")
                    || workflowTransition.getWorkflowId() == 4 && workflowTransition.getAction().equalsIgnoreCase("Change requested") && workflowTransition.getNextRole().equalsIgnoreCase("Tender Creator")) {
                validateUserRole(transitionActionReqDto.getActionBy(), currentTransition.getCurrentRoleId());
            } else {
                validateUserRole(transitionActionReqDto.getActionBy(), currentTransition.getNextRoleId());
            }
        }

        // Block any action on a transition that is already fully completed.
        // NOTE: we do NOT make a special exception for REJECT here — once a workflow is
        // COMPLETED (i.e. the indent was approved by the full hierarchy), it cannot be
        // rejected. An approved indent is immutable.
        if (AppConstant.COMPLETED_TYPE.equalsIgnoreCase(workflowTransition.getStatus())) {
            throw new BusinessException(new ErrorDetails(AppConstant.INVALID_ACTION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Workflow already completed. An approved request cannot be rejected."));
        }

        if (AppConstant.APPROVE_TYPE.equalsIgnoreCase(transitionActionReqDto.getAction())) {
            WorkflowTransitionDto wt = approveTransition(workflowTransition, currentTransition, transitionActionReqDto);
            if (wt != null) {
                try {
                    emailService.sendWorkflowEmail(wt); // @Async method
                    /*  if ("Tender Approver".equals(wt.getCurrentRole())) {
                        tenderEmailService.handleTenderApproverEmail(wt);
                    }*/
                    if (wt.getWorkflowName().equalsIgnoreCase("PO Workflow") && (wt.getStatus().equalsIgnoreCase("In-progress") || wt.getStatus().equalsIgnoreCase("Completed"))) {
                        PoFormateDto poData = purchaseOrderService.getPoFormatDetails(wt.getRequestId());
                        // String purchaseDeptMail ="kudaykiran.9949@gmail.com";  //change. get the purchase dept mail from db
                        //updated by abhinav
                        String purchaseDeptMail = userMasterRepository
                                .findEmailByRoleName("Purchase Dept");
                        tenderEmailService.handlePoApproverEmail(poData, purchaseDeptMail);
                    }
                    if ("Tender Approver".equals(wt.getCurrentRole())) {
                        TenderWithIndentResponseDTO tenderData = TRService.getTenderRequestById(wt.getRequestId());

                        Set<String> vendorIds = new HashSet<>();
                        for (IndentCreationResponseDTO indent : tenderData.getIndentResponseDTO()) {
                            for (MaterialDetailsResponseDTO material : indent.getMaterialDetails()) {
                                if (material.getVendorNames() != null) {
                                    vendorIds.addAll(material.getVendorNames());
                                }
                            }
                        }
                        /*
                        Map<String, String> vendorEmailMap = new HashMap<>();
                        for (String vendorId : vendorIds) {
                            vendorMasterRepository.findById(vendorId).ifPresent(vendor -> {
                                vendorEmailMap.put(vendorId, vendor.getEmailAddress());
                            });
                        }

                        tenderEmailService.handleTenderApproverEmail(wt.getRequestId(), tenderData, vendorEmailMap);*/
                        Map<String, VendorDto> vendorMap = new HashMap<>();
                        for (String vendorId : vendorIds) {
                            vendorMasterRepository.findById(vendorId).ifPresent(vendor -> {
                                VendorDto dto = new VendorDto();
                                dto.setVendorId(vendor.getVendorId());
                                dto.setVendorName(vendor.getVendorName());
                                dto.setEmailAddress(vendor.getEmailAddress());
                                dto.setAddress(vendor.getAddress());
                                vendorMap.put(vendorId, dto);
                            });
                        }
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        try {
                            LocalDate opening = LocalDate.parse(tenderData.getOpeningDate(), formatter);
                            LocalDate closing = LocalDate.parse(tenderData.getClosingDate(), formatter);
                            long days = Duration.between(opening.atStartOfDay(), closing.atStartOfDay()).toDays();
                            tenderData.setValidityPeriod(days + " Days");
                        } catch (Exception e) {
                            tenderData.setValidityPeriod("____ Days");
                        }

                        tenderEmailService.handleTenderApproverEmail(wt.getRequestId(), tenderData, vendorMap);

                    }

                } catch (Exception e) {
                    // log.error("Failed to send transition email", e);
                }
            }
        } else if (AppConstant.REJECT_TYPE.equalsIgnoreCase(transitionActionReqDto.getAction())) {
            // Collect all transitions BEFORE termination so we have the full recipient list for email
            List<WorkflowTransition> allTransitions =
                    workflowTransitionRepository.findByRequestId(transitionActionReqDto.getRequestId());

            // Terminal rejection — marks current transition COMPLETED/REJECTED,
            // cancels every other PENDING/IN-PROGRESS transition, updates the request entity status.
            // No backward traversal. No reassignment. Rejection is a dead end.
            WorkflowTransitionDto wt = terminateWorkflowOnRejection(workflowTransition, transitionActionReqDto);

            if (wt != null) {
                try {
                    sendRejectionTransitionEmails(allTransitions, wt);
                } catch (Exception e) {
                    // email failure must never roll back the rejection transaction
                }
            }
        } else if (AppConstant.CHANGE_REQUEST_TYPE.equalsIgnoreCase(transitionActionReqDto.getAction())) {
            WorkflowTransitionDto wt = requestChangeTransition(workflowTransition, currentTransition, transitionActionReqDto);
            if (wt != null) {
                try {
                    emailService.sendWorkflowEmail(wt); // @Async method
                } catch (Exception e) {
                    // log.error("Failed to send transition email", e);
                }
            }
        } else {
            throw new InvalidInputException(new ErrorDetails(AppConstant.INVALID_TRANSITION_ACTION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid transition action."));
        }

        return null;
    }

    private void sendRejectionTransitionEmails(List<WorkflowTransition> transitions, WorkflowTransitionDto wt) {
        if (transitions == null || transitions.isEmpty() || wt == null) {
            return;
        }

        // Collect all distinct createdBy and modifiedBy from all records
        Set<Integer> userIds = transitions.stream()
                .flatMap(t -> Stream.of(t.getCreatedBy(), t.getModifiedBy()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (userIds.isEmpty()) {
            return; // No users to send email
        }

        // Fetch email addresses from UserMaster
        List<String> emails = userMasterRepository.findByUserIdIn(userIds).stream()
                .map(UserMaster::getEmail)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!emails.isEmpty()) {
            try {
                emailService.sendRejectionWorkflowEmail(emails, wt); // Pass list of emails and transition data
            } catch (Exception e) {
                // log.error("Failed to send workflow email", e);
            }
        }
    }

    @Override
    @Transactional
    public WorkflowTransitionDto submitWorkflow(Integer workflowTransitionId, Integer actionBy, String remarks) {
        userService.validateUser(actionBy);
        WorkflowTransition currentWorkflowTransition = workflowTransitionRepository.findById(workflowTransitionId).orElse(null);
        if (Objects.isNull(currentWorkflowTransition)) {
            throw new InvalidInputException(new ErrorDetails(AppConstant.INVALID_WORKFLOW_TRANSITION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Workflow transition not found.With given workflow transition id and request id."));
        }

        TransitionDto nextTransition = nextTransition(currentWorkflowTransition.getWorkflowId(), currentWorkflowTransition.getWorkflowName(), roleNameByUserId(actionBy), currentWorkflowTransition.getRequestId());
        if (Objects.isNull(nextTransition)) {
            throw new InvalidInputException(new ErrorDetails(AppConstant.NEXT_TRANSITION_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Error occurred at approval. No next transition found."));
        }

        currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
        workflowTransitionRepository.save(currentWorkflowTransition);

        WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
        nextWorkflowTransition.setWorkflowId(nextTransition.getWorkflowId());
        nextWorkflowTransition.setTransitionId(nextTransition.getTransitionId());
        nextWorkflowTransition.setTransitionOrder(nextTransition.getTransitionOrder());
        nextWorkflowTransition.setTransitionSubOrder(nextTransition.getTransitionSubOrder());
        nextWorkflowTransition.setWorkflowName(nextTransition.getWorkflowName());
        nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
        nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
        nextWorkflowTransition.setAction(AppConstant.APPROVE_TYPE);
        nextWorkflowTransition.setRemarks(remarks);
        nextWorkflowTransition.setModifiedBy(actionBy);
        nextWorkflowTransition.setModificationDate(new Date());
        nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
        nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
        nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
        nextWorkflowTransition.setCurrentRole(nextTransition.getCurrentRoleName());
        nextWorkflowTransition.setNextRole(nextTransition.getNextRoleName());
        nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

        workflowTransitionRepository.save(nextWorkflowTransition);

        return null;
    }

    @Override
    public List<WorkflowTransitionDto> approvedWorkflowTransition(Integer modifiedBy) {
        List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();
        List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByModifiedBy(modifiedBy);
        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
            workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowSequence).reversed()).map(e -> {
                return mapWorkflowTransitionDto(e);
            }).collect(Collectors.toList());
        }
        return workflowTransitionDtoList;
    }

    @Override
    public List<SubWorkflowTransitionDto> getSubWorkflowTransition(Integer modifiedBy) {
        List<SubWorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();

        List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByActionOn(modifiedBy);
        if (Objects.nonNull(subWorkflowTransitionList) && !subWorkflowTransitionList.isEmpty()) {
            workflowTransitionDtoList = subWorkflowTransitionList.stream().map(e -> {
                SubWorkflowTransitionDto subWorkflowTransitionDto = new SubWorkflowTransitionDto();
                subWorkflowTransitionDto.setSubWorkflowTransitionId(e.getSubWorkflowTransitionId());
                subWorkflowTransitionDto.setWorkflowId(e.getWorkflowId());
                subWorkflowTransitionDto.setWorkflowName(e.getWorkflowName());
                subWorkflowTransitionDto.setModifiedBy(e.getModifiedBy());
                subWorkflowTransitionDto.setWorkflowSequence(e.getWorkflowSequence());
                subWorkflowTransitionDto.setStatus(e.getStatus());
                subWorkflowTransitionDto.setRemarks(e.getRemarks());
                subWorkflowTransitionDto.setAction(e.getAction());
                subWorkflowTransitionDto.setActionOn(e.getActionOn());
                subWorkflowTransitionDto.setRequestId(e.getRequestId());
                subWorkflowTransitionDto.setCreatedBy(e.getCreatedBy());
                subWorkflowTransitionDto.setCreatedDate(e.getCreatedDate());
                subWorkflowTransitionDto.setModificationDate(e.getModificationDate());

                return subWorkflowTransitionDto;
            }).collect(Collectors.toList());
        }
        return workflowTransitionDtoList;
    }

    @Override
    @Transactional
    public void approveSubWorkflow(Integer subWorkflowTransitionId) {
        SubWorkflowTransitionDto subDto = new SubWorkflowTransitionDto();
        if (Objects.nonNull(subWorkflowTransitionId)) {
            Optional<SubWorkflowTransition> subWorkflowTransitionOptional = subWorkflowTransitionRepository.findById(subWorkflowTransitionId);
            if (subWorkflowTransitionOptional.isPresent()) {
                SubWorkflowTransition subWorkflowTransition = subWorkflowTransitionOptional.get();
                subWorkflowTransition.setStatus(AppConstant.APPROVE_TYPE);
                subWorkflowTransition.setAction(AppConstant.COMPLETED_TYPE);
                subWorkflowTransition.setModificationDate(new Date());

                subWorkflowTransitionRepository.save(subWorkflowTransition);
                WorkflowTransitionDto wt = validateSubWorkflow(subWorkflowTransition);
                if (wt != null) {
                    try {
                        emailService.sendWorkflowEmail(wt); // @Async method
                    } catch (Exception e) {
                        // log.error("Failed to send transition email", e);
                        //  e.printStackTrace();
                    }
                }

            } else {
                throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION, "Invalid sub workflow transition id."));
            }
        } else {
            throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid sub workflow transition id."));
        }

    }

    private WorkflowTransitionDto validateSubWorkflow(SubWorkflowTransition subWorkflowTransition) {
        String requestId = subWorkflowTransition.getRequestId();
        Integer workflowTransitionId = subWorkflowTransition.getWorkflowTransitionId();
        WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
        List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByWorkflowTransitionIdAndRequestIdAndTransitionTypeAndTransitionName(workflowTransitionId, requestId, "Double", "Phase_1");
        if (Objects.nonNull(subWorkflowTransitionList) && !subWorkflowTransitionList.isEmpty()) {
            List<SubWorkflowTransition> subWorkflowTransitionFilteredList = subWorkflowTransitionList.stream().filter(e -> !e.getStatus().equalsIgnoreCase(AppConstant.APPROVE_TYPE)).collect(Collectors.toList());
            if (subWorkflowTransitionFilteredList.isEmpty()) {
                WorkflowTransition currentWorkflowTransition = workflowTransitionRepository.findByWorkflowTransitionIdAndRequestId(workflowTransitionId, requestId);
                currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
                workflowTransitionRepository.save(currentWorkflowTransition);

                nextWorkflowTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
                nextWorkflowTransition.setTransitionId(currentWorkflowTransition.getTransitionId());
                nextWorkflowTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder());
                nextWorkflowTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
                nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
                nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
                nextWorkflowTransition.setTransitionSubOrder(currentWorkflowTransition.getTransitionSubOrder());
                nextWorkflowTransition.setModifiedBy(currentWorkflowTransition.getModifiedBy());
                nextWorkflowTransition.setModificationDate(new Date());
                nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
                nextWorkflowTransition.setAction(AppConstant.APPROVE_TYPE);
                nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
                nextWorkflowTransition.setRemarks(null);
                nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getCurrentRole());
                nextWorkflowTransition.setNextRole(currentWorkflowTransition.getCurrentRole());
                nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);
                nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());

                workflowTransitionRepository.save(nextWorkflowTransition);

            }
        }
        return mapToWorkflowTransitionDto(nextWorkflowTransition);
    }

    private WorkflowTransitionDto requestChangeTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {
        if (Objects.nonNull(transitionActionReqDto.getAssignmentRole())) {
            validateAssignmentRole(transitionActionReqDto.getAssignmentRole(), currentWorkflowTransition);

            currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
            workflowTransitionRepository.save(currentWorkflowTransition);

            WorkflowTransition latestWorkflowTransition = getLatestWorkflowTransiton(currentWorkflowTransition, transitionActionReqDto);

            WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
            nextWorkflowTransition.setWorkflowId(latestWorkflowTransition.getWorkflowId());
            nextWorkflowTransition.setTransitionId(latestWorkflowTransition.getTransitionId());
            nextWorkflowTransition.setTransitionOrder(latestWorkflowTransition.getTransitionOrder());
            nextWorkflowTransition.setTransitionSubOrder(latestWorkflowTransition.getTransitionSubOrder());
            nextWorkflowTransition.setWorkflowName(latestWorkflowTransition.getWorkflowName());
            // Preserve branch-based workflow fields so approveTransition can route correctly on resubmit.
            // Use currentWorkflowTransition (the approver who sent the change request) for branchId —
            // it always has branchId set. Set level/seq to 0 so getNextApprover restarts from the
            // first approver in the chain (Reporting Officer) rather than skipping past them.
            nextWorkflowTransition.setBranchId(currentWorkflowTransition.getBranchId());
            nextWorkflowTransition.setApprovalLevel(0);
            nextWorkflowTransition.setApprovalSequence(0);
            nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
            nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
            nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
            nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
            nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
            nextWorkflowTransition.setModificationDate(new Date());
            nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
            nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
            nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
            nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getNextRole());

            // nextWorkflowTransition.setNextRole(transitionActionReqDto.getAssignmentRole());
            // updated by abhinav
            String assignmentRole = transitionActionReqDto.getAssignmentRole();

            if ("Request Creator".equalsIgnoreCase(assignmentRole)
                    && currentWorkflowTransition.getWorkflowName().toUpperCase().contains("PO")) {

                nextWorkflowTransition.setNextRole("PO Creator");

            } else {
                nextWorkflowTransition.setNextRole(assignmentRole);
            }
            // updated by abhinav ends
            nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

            workflowTransitionRepository.save(nextWorkflowTransition);

            // Bug Fix 1: Make indent editable when sent back to Indent Creator
            if ("Indent Workflow".equalsIgnoreCase(currentWorkflowTransition.getWorkflowName())
                    && "Indent Creator".equalsIgnoreCase(transitionActionReqDto.getAssignmentRole())) {
                String requestId = currentWorkflowTransition.getRequestId();
                if (requestId != null && requestId.startsWith("IND")) {
                    indentCreationRepository.findById(requestId).ifPresent(indent -> {
                        indent.setIsEditable(true);
                        indent.setCurrentStatus("CHANGE_REQUESTED");
                        indent.setCurrentStage("INDENT_REVISION");
                        indentCreationRepository.save(indent);
                    });
                }
            }

            return mapToWorkflowTransitionDto(nextWorkflowTransition);
        } else {
            throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid assignment role."));
        }

    }

    private static final Set<String> CREATOR_ROLES = Set.of(
            "Request Creator",
            "Indent Creator",
            "Tender Creator",
            "PO Creator",
            "SO Creator",
            "CP Creator",
            "Purchase Personnel"
    );

    private WorkflowTransition getLatestWorkflowTransiton(WorkflowTransition currentWorkflowTransition, TransitionActionReqDto transitionActionReqDto) {
        WorkflowTransition workflowTransition = null;
        List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByWorkflowIdAndRequestIdAndNextRole(currentWorkflowTransition.getWorkflowId(), currentWorkflowTransition.getRequestId(), transitionActionReqDto.getAssignmentRole());
        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
            workflowTransition = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowTransitionId).reversed()).limit(1).collect(Collectors.toList()).get(0);
        } else if (CREATOR_ROLES.contains(transitionActionReqDto.getAssignmentRole())) {//else if (transitionActionReqDto.getAssignmentRole().equalsIgnoreCase("Request Creator")) {
            workflowTransitionList = workflowTransitionRepository.findByWorkflowIdAndRequestIdAndCurrentRole(currentWorkflowTransition.getWorkflowId(), currentWorkflowTransition.getRequestId(), transitionActionReqDto.getAssignmentRole());
            if (Objects.nonNull(workflowTransitionList)) {
                workflowTransition = workflowTransitionList.get(0);
            }
        }
        return workflowTransition;
    }

    private void validateAssignmentRole(String assignmentRole, WorkflowTransition workflowTransition) {
        List<String> allPreviousRole = allPreviousRoleWorkflowTransition(workflowTransition.getWorkflowId(), workflowTransition.getRequestId());
        if (!allPreviousRole.isEmpty() && allPreviousRole.contains(assignmentRole)) {
        } else {
            throw new InvalidInputException(new ErrorDetails(AppConstant.USER_INVALID_INPUT, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid previous assignment role."));
        }
    }

    /**
     * Terminates the entire workflow when a user performs REJECTED.
     *
     * <p>Key business rules enforced here:
     * <ol>
     *   <li><strong>Approved indent cannot be rejected.</strong> Once an indent has been
     *       fully approved by the hierarchy (currentStatus = "APPROVED"), it is immutable.
     *       Attempting to reject it throws a BusinessException.</li>
     *   <li><strong>Indent linked to an active Tender cannot be rejected.</strong> If the
     *       indent already has a tender raised against it, the tender must be cancelled first.
     *       If the indent has no tender yet, it can be rejected freely.</li>
     *   <li><strong>Terminal rejection.</strong> The current transition is closed in-place
     *       (status=COMPLETED, action=REJECTED). All other PENDING/IN-PROGRESS sibling
     *       transitions are bulk-cancelled. The underlying request entity is updated to REJECTED.
     *       No new transition row is created. No backward traversal. No role re-assignment.</li>
     * </ol>
     *
     * @param currentWorkflowTransition The transition the approver is acting on.
     * @param transitionActionReqDto    The request payload: action=REJECTED, remarks, actionBy, requestId.
     * @return DTO of the closed (rejected) transition for email dispatch upstream.
     */
    @Transactional
    private WorkflowTransitionDto terminateWorkflowOnRejection(
            WorkflowTransition currentWorkflowTransition,
            TransitionActionReqDto transitionActionReqDto) {

        String requestId = transitionActionReqDto.getRequestId();

        // ── Guard: Approved indent cannot be rejected ──
        // Flow: Indent approved → Tender raised → IndentId table entry created.
        // Therefore if an indent is in the IndentId table it is already approved,
        // and Guard 1a below would have caught it first. Guard 1b (tender-cancellation
        // check) is intentionally omitted — it is unreachable given this flow.
        if (requestId != null && requestId.startsWith("IND")) {
            indentCreationRepository.findById(requestId).ifPresent(indent -> {
                if ("APPROVED".equalsIgnoreCase(indent.getCurrentStatus())) {
                    throw new BusinessException(new ErrorDetails(
                            AppConstant.USER_INVALID_INPUT,
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Cannot reject Indent " + requestId
                                    + ". It has already been approved and cannot be reversed."));
                }
            });
        }
        // ── End of Indent guard ──

        Date now = new Date();

        // ── Step 1: Close the CURRENT transition as REJECTED (in-place update) ──
        // Stamping in-place preserves who rejected and when on the exact transition row
        // without creating a dangling new row that could re-enter any queue.
        currentWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
        currentWorkflowTransition.setAction(AppConstant.REJECT_TYPE);
        currentWorkflowTransition.setNextAction(null);   // nothing pending after rejection
        currentWorkflowTransition.setNextRole(null);     // clear queue assignment
        currentWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
        currentWorkflowTransition.setModificationDate(now);
        currentWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
        workflowTransitionRepository.save(currentWorkflowTransition);

        System.out.println("✅ [REJECTION] Closed transition ID="
                + currentWorkflowTransition.getWorkflowTransitionId()
                + " for requestId=" + requestId
                + " by userId=" + transitionActionReqDto.getActionBy());

        // ── Step 2: Cancel ALL other PENDING / IN-PROGRESS sibling transitions ──
        // This handles parallel-approver scenarios and ensures nothing lingers in any queue.
        List<WorkflowTransition> siblingTransitions =
                workflowTransitionRepository.findByRequestId(requestId);

        List<WorkflowTransition> toCancel = siblingTransitions.stream()
                .filter(t -> !t.getWorkflowTransitionId()
                        .equals(currentWorkflowTransition.getWorkflowTransitionId()))
                .filter(t -> AppConstant.PENDING_TYPE.equalsIgnoreCase(t.getNextAction())
                        || AppConstant.IN_PROGRESS_TYPE.equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (!toCancel.isEmpty()) {
            toCancel.forEach(t -> {
                t.setStatus(AppConstant.CANCELED_TYPE);
                t.setNextAction(null);
                t.setNextRole(null);
                t.setModifiedBy(transitionActionReqDto.getActionBy());
                t.setModificationDate(now);
                t.setRemarks("Auto-cancelled: workflow terminated by rejection on transition ID="
                        + currentWorkflowTransition.getWorkflowTransitionId());
            });
            workflowTransitionRepository.saveAll(toCancel);
            System.out.println("✅ [REJECTION] Cancelled " + toCancel.size()
                    + " sibling transition(s) for requestId=" + requestId);
        }

        // ── Step 3: Update the underlying request entity status to REJECTED ──
        // This makes the request disappear from application-level lists that filter by status.
        updateRequestEntityOnRejection(
                requestId,
                currentWorkflowTransition.getWorkflowName(),
                transitionActionReqDto.getActionBy(),
                transitionActionReqDto.getRemarks(),
                now);

        // ── Step 4: Return DTO of the closed transition for email dispatch ──
        return mapToWorkflowTransitionDto(currentWorkflowTransition);
    }

    /**
     * Updates the underlying request entity (Indent, Tender, PO, SO, CP) to REJECTED status.
     *
     * <p>Each domain table has its own status field. We dispatch to the right repository
     * based on the requestId prefix or workflow name. If an entity update fails, we log
     * and swallow the exception — the workflow state is already committed and must not be
     * rolled back due to a secondary status update failure.
     *
     * <p>DB field assumptions:
     * <ul>
     *   <li>indent_creation: current_status, current_stage, is_editable</li>
     *   <li>tender_request:  status</li>
     *   <li>purchase_order:  status</li>
     *   <li>service_order:   status</li>
     *   <li>contingency_purchase: status</li>
     * </ul>
     */
    private void updateRequestEntityOnRejection(
            String requestId, String workflowName,
            Integer actionBy, String remarks, Date now) {

        if (requestId == null || workflowName == null) {
            System.err.println("⚠️ [REJECTION] Cannot update entity — requestId or workflowName is null.");
            return;
        }

        String workflowNameUpper = workflowName.toUpperCase();

        try {
            if (requestId.startsWith("IND") || workflowNameUpper.contains("INDENT")) {
                indentCreationRepository.findById(requestId).ifPresent(indent -> {
                    indent.setCurrentStatus("REJECTED");
                    indent.setCurrentStage("INDENT_REJECTED");
                    indent.setIsEditable(false);
                    indentCreationRepository.save(indent);
                    System.out.println("✅ [REJECTION] IndentCreation " + requestId + " → REJECTED");
                });

            } else if (requestId.startsWith("T") || workflowNameUpper.contains("TENDER")) {
                tenderRequestRepository.findById(requestId).ifPresent(tender -> {
                    tender.setCurrentStatus("REJECTED");
                    tenderRequestRepository.save(tender);
                    System.out.println("✅ [REJECTION] TenderRequest " + requestId + " → REJECTED");
                });

            } else if (requestId.startsWith("PO") || workflowNameUpper.contains("PO")) {
                purchaseOrderRepository.findById(requestId).ifPresent(po -> {
                    po.setCurrentStatus("REJECTED");
                    po.setIsActive(false);
                    purchaseOrderRepository.save(po);
                    System.out.println("✅ [REJECTION] PurchaseOrder " + requestId + " → REJECTED");
                });

            } else if (requestId.startsWith("SO") || workflowNameUpper.contains("SO")) {
                serviceOrderRepository.findById(requestId).ifPresent(so -> {
                    so.setCurrentStatus("REJECTED");
                    serviceOrderRepository.save(so);
                    System.out.println("✅ [REJECTION] ServiceOrder " + requestId + " → REJECTED");
                });

            } else if (requestId.startsWith("CP") || workflowNameUpper.contains("CONTINGENCY")) {
                contigencyPurchaseRepository.findById(requestId).ifPresent(cp -> {
                    cp.setCurrentStatus("REJECTED");
                    contigencyPurchaseRepository.save(cp);
                    System.out.println("✅ [REJECTION] ContingencyPurchase " + requestId + " → REJECTED");
                });

            } else {
                System.err.println("⚠️ [REJECTION] No entity update rule for requestId="
                        + requestId + ", workflowName=" + workflowName
                        + ". Add a branch in updateRequestEntityOnRejection() if needed.");
            }

        } catch (Exception e) {
            // Log but do NOT rethrow — the workflow transition state is already persisted.
            // A failed entity status update must not roll back the rejection itself.
            System.err.println("❌ [REJECTION] Failed to update request entity for "
                    + requestId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WorkflowTransition getPrevWorkflowTransition(WorkflowTransition workflowTransition) {
        List<WorkflowTransition> workflowTransitionList = workflowTransitionRepository.findByRequestId(workflowTransition.getRequestId());
        if (workflowTransitionList.size() == 1) {
            return workflowTransitionList.get(0);
        } else {
            return workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowTransitionId).reversed()).skip(1).findFirst().get();
        }
    }

    // private void approveTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {
    private WorkflowTransitionDto approveTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {
        WorkflowTransition nextWorkflowTransition = null;

        // ===== BRANCH-BASED APPROVAL ROUTING =====
        if (currentWorkflowTransition.getBranchId() != null) {
            // This is a branch-based workflow
            nextWorkflowTransition = approveBranchBasedTransition(currentWorkflowTransition, transitionActionReqDto);
        } else {
            // Legacy TransitionMaster-based workflow
            nextWorkflowTransition = approveLegacyTransition(currentWorkflowTransition, currentTransition, transitionActionReqDto);
        }

        return mapToWorkflowTransitionDto(nextWorkflowTransition);
    }

    /**
     * BRANCH-BASED APPROVAL TRANSITION Routes to next approver in the
     * sequential approval chain with limit checks and escalation
     */
    private WorkflowTransition approveBranchBasedTransition(WorkflowTransition currentWorkflowTransition, TransitionActionReqDto transitionActionReqDto) {
        // Mark current transition as completed
        currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
        workflowTransitionRepository.save(currentWorkflowTransition);

        System.out.println("🔍 Finding next approver for branch " + currentWorkflowTransition.getBranchId()
                + " after Level: " + currentWorkflowTransition.getApprovalLevel()
                + ", Seq: " + currentWorkflowTransition.getApprovalSequence());

        // Get indent details for limit check
        // added by abhinav start
        String requestId = currentWorkflowTransition.getRequestId();
        com.astro.entity.ProcurementModule.IndentCreation indent = null;
        BigDecimal indentValue = BigDecimal.ZERO;
        String departmentName = null;
        String category = null;
        String location = null;

        // if (requestId != null && requestId.startsWith("IND")) {
        //     indent = indentCreationRepository.findById(requestId).orElse(null);
        //     if (indent != null) {
        //         indentValue = indent.getTotalIntentValue() != null ? indent.getTotalIntentValue() : BigDecimal.ZERO;
        //         departmentName = indent.getIndentorDepartment();
        //         category = indent.getMaterialCategoryType();
        //         location = indent.getConsignesLocation();
        //     }
        // }
        // updated indent and tender workflow
        String workflowName = currentWorkflowTransition.getWorkflowName();

        if (workflowName.toUpperCase().contains("INDENT")) {

            indent = indentCreationRepository.findById(requestId).orElse(null);

            if (indent != null) {
                indentValue = indent.getTotalIntentValue();
                departmentName = indent.getIndentorDepartment();
                category = indent.getMaterialCategoryType();
                location = indent.getConsignesLocation();
            }

        } // else if (workflowName.toUpperCase().contains("TENDER")) {
        //     TenderRequest tender = tenderRequestRepository.findById(requestId).orElse(null);
        //     if (tender != null) {
        //         indentValue = tender.getTotalTenderValue();
        //         location = tender.getConsignes();
        //         category = tender.getModeOfProcurement();   // optional
        //         departmentName = tender.getProjectName();  // if branch uses project as department
        //     }
        // } 
        else if (workflowName.toUpperCase().contains("TENDER")) {

            TenderRequest tender = tenderRequestRepository.findById(requestId).orElse(null);

            if (tender != null) {
                indentValue = tender.getTotalTenderValue();
                location = tender.getConsignes();
                category = tender.getModeOfProcurement();

                // FIX: GET DEPARTMENT FROM LINKED INDENT
                List<String> indentIds = indentIdRepository.findTenderWithIndent(requestId);

                if (indentIds != null && !indentIds.isEmpty()) {
                    indent = indentCreationRepository.findByIndentId(indentIds.get(0));
                    if (indent != null) {
                        departmentName = indent.getIndentorDepartment();
                    }
                }
            }
        } // else if (workflowName.toUpperCase().contains("PO")) {
        //     PurchaseOrder po = purchaseOrderRepository.findById(requestId).orElse(null);
        //     if (po != null) {
        //         indentValue = po.getTotalValueOfPo();
        //         departmentName = po.getProjectName();
        //         location = po.getConsignesAddress();
        //         // category = po.getMaterialCategoryType();
        //     }
        // }
        else if (workflowName.toUpperCase().contains("PO")) {

            PurchaseOrder po = purchaseOrderRepository.findById(requestId).orElse(null);

            if (po != null) {
                indentValue = po.getTotalValueOfPo();
                location = po.getConsignesAddress();

                // FIX: GET DEPARTMENT FROM INDENT
                if (po.getIndentId() != null) {
                    indent = indentCreationRepository.findByIndentId(po.getIndentId());
                    if (indent != null) {
                        departmentName = indent.getIndentorDepartment();
                    }
                }
            }
        }
        // debugger
        System.out.println("==== APPROVAL DEBUG ====");
        System.out.println("Workflow: " + currentWorkflowTransition.getWorkflowName());
        System.out.println("Amount: " + indentValue);
        System.out.println("Department: " + departmentName);
        System.out.println("Location: " + location);
        System.out.println("Category: " + category);
        System.out.println("========================");
        // Get next approver with limit check (skips approvers if amount is within limit)
        com.astro.entity.AdminPanel.ApproverMaster nextApprover = branchWorkflowService.getNextApproverWithLimitCheck(
                currentWorkflowTransition.getBranchId(),
                currentWorkflowTransition.getApprovalLevel(),
                currentWorkflowTransition.getApprovalSequence(),
                indentValue,
                departmentName,
                category,
                location
        );
        if (nextApprover == null) {
            System.out.println("NO NEXT APPROVER FOUND → WORKFLOW WILL COMPLETE HERE");
        } else {
            System.out.println("Next Approver: " + nextApprover.getRoleName());
        }

        // Check if escalation is required based on current approver's limits
        if (nextApprover != null) {
            com.astro.dto.workflow.EscalationCheckResultDTO escalationResult
                    = branchWorkflowService.checkEscalationRequired(
                            nextApprover.getRoleName(),
                            indentValue,
                            category,
                            departmentName,
                            location
                    );

            if (escalationResult != null && escalationResult.isEscalationRequired()) {
                // Escalate to Director
                System.out.println("ESCALATION REQUIRED: " + escalationResult.getReason());
                System.out.println("   Escalating to: " + escalationResult.getEscalateTo());

                // Update indent with escalation info
                if (indent != null) {
                    indent.setEscalatedToDirector(true);
                    indent.setEscalationReason(escalationResult.getReason());
                    indentCreationRepository.save(indent);
                }

                // Create escalation transition
                return createEscalationTransition(currentWorkflowTransition, transitionActionReqDto,
                        escalationResult.getEscalateTo(), escalationResult.getReason());
            }
        }

        // Create new workflow transition
        WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
        nextWorkflowTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
        nextWorkflowTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
        nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
        nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
        nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
        nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
        nextWorkflowTransition.setModificationDate(new Date());
        nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
        nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
        nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getNextRole());
        nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);
        nextWorkflowTransition.setBranchId(currentWorkflowTransition.getBranchId());
        nextWorkflowTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder() + 1);
        nextWorkflowTransition.setTransitionSubOrder(1);

        if (nextApprover != null) {
            // More approvers in the chain
            nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
            nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
            nextWorkflowTransition.setNextRole(nextApprover.getRoleName());
            nextWorkflowTransition.setApproverId(nextApprover.getApproverId());
            nextWorkflowTransition.setApprovalLevel(nextApprover.getApprovalLevel());
            nextWorkflowTransition.setApprovalSequence(nextApprover.getApprovalSequence());

            // Reporting Officer: resolve and assign to specific user
            if (isReportingOfficerRole(nextApprover.getRoleName())) {
                try {
                    Map<String, Object> roInfo = resolveReportingOfficer(currentWorkflowTransition.getCreatedBy());
                    nextWorkflowTransition.setAssignedToUserId((Integer) roInfo.get("userId"));
                    nextWorkflowTransition.setAssignedToEmployeeId((String) roInfo.get("employeeId"));
                    System.out.println("Reporting Officer assigned: " + roInfo.get("employeeName")
                            + " (userId=" + roInfo.get("userId") + ")");
                } catch (BusinessException e) {
                    System.err.println("Could not resolve reporting officer: " + e.getMessage());
                    throw e;
                }
            }

            // Project Head: resolve and assign to specific project head user
            if (isProjectHeadRole(nextApprover.getRoleName())) {
                try {
                    Map<String, Object> phInfo = resolveProjectHead(currentWorkflowTransition.getRequestId());
                    nextWorkflowTransition.setAssignedToUserId((Integer) phInfo.get("userId"));
                    nextWorkflowTransition.setAssignedToEmployeeId((String) phInfo.get("employeeId"));
                    System.out.println("Project Head assigned: " + phInfo.get("employeeName")
                            + " (userId=" + phInfo.get("userId") + ")");
                } catch (BusinessException e) {
                    System.err.println("Could not resolve project head: " + e.getMessage());
                    throw e;
                }
            }

            // Location-based roles (Professor In Charge / Engineer In Charge)
            if (isLocationBasedRole(nextApprover.getRoleName())) {
                try {
                    Map<String, Object> locInfo = resolveLocationBasedApprover(
                            currentWorkflowTransition.getRequestId(), nextApprover.getRoleName());
                    nextWorkflowTransition.setAssignedToUserId((Integer) locInfo.get("userId"));
                    nextWorkflowTransition.setAssignedToEmployeeId((String) locInfo.get("employeeId"));
                    System.out.println(nextApprover.getRoleName() + " assigned: " + locInfo.get("employeeName")
                            + " (userId=" + locInfo.get("userId") + ")");
                } catch (BusinessException e) {
                    System.err.println("Could not resolve " + nextApprover.getRoleName() + ": " + e.getMessage());
                    throw e;
                }
            }

            System.out.println("Approved by " + currentWorkflowTransition.getNextRole()
                    + " → Routing to " + nextApprover.getRoleName()
                    + " (Branch: " + currentWorkflowTransition.getBranchId()
                    + ", Level: " + nextApprover.getApprovalLevel()
                    + ", Seq: " + nextApprover.getApprovalSequence() + ")");
        } else {
            // No more approvers - workflow complete
            nextWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
            nextWorkflowTransition.setNextAction(null);
            nextWorkflowTransition.setNextRole(null);

            System.out.println("Final approval by " + currentWorkflowTransition.getNextRole()
                    + " (Branch: " + currentWorkflowTransition.getBranchId()
                    + ") - Workflow COMPLETED");
        }

        workflowTransitionRepository.save(nextWorkflowTransition);
        return nextWorkflowTransition;
    }

    /**
     * Create an escalation transition to Director
     */
    private WorkflowTransition createEscalationTransition(
            WorkflowTransition currentWorkflowTransition,
            TransitionActionReqDto transitionActionReqDto,
            String escalateTo,
            String escalationReason
    ) {
        WorkflowTransition escalationTransition = new WorkflowTransition();
        escalationTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
        escalationTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
        escalationTransition.setRequestId(currentWorkflowTransition.getRequestId());
        escalationTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
        escalationTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
        escalationTransition.setModifiedBy(transitionActionReqDto.getActionBy());
        escalationTransition.setModificationDate(new Date());
        escalationTransition.setAction("Escalated");
        escalationTransition.setRemarks("ESCALATION: " + escalationReason);
        escalationTransition.setCurrentRole(currentWorkflowTransition.getNextRole());
        escalationTransition.setNextRole(escalateTo);
        escalationTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);
        escalationTransition.setBranchId(currentWorkflowTransition.getBranchId());
        escalationTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder() + 1);
        escalationTransition.setTransitionSubOrder(1);
        escalationTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
        escalationTransition.setNextAction(AppConstant.PENDING_TYPE);

        System.out.println("⚠️ ESCALATION: " + currentWorkflowTransition.getNextRole()
                + " → " + escalateTo
                + " | Reason: " + escalationReason);

        workflowTransitionRepository.save(escalationTransition);
        return escalationTransition;
    }

    @Override
    @Transactional
    public WorkflowTransitionDto performAutoApproval(WorkflowTransition pendingTransition, Integer autoApproveHours) {
        if (!"Pending".equals(pendingTransition.getNextAction())) {
            return null;
        }

        pendingTransition.setNextAction(AppConstant.COMPLETED_TYPE);
        workflowTransitionRepository.save(pendingTransition);

        String requestId = pendingTransition.getRequestId();
        com.astro.entity.ProcurementModule.IndentCreation indent = null;
        BigDecimal indentValue = BigDecimal.ZERO;
        String departmentName = null;
        String category = null;
        String location = null;

        if (requestId != null && requestId.startsWith("IND")) {
            indent = indentCreationRepository.findById(requestId).orElse(null);
            if (indent != null) {
                indentValue = indent.getTotalIntentValue() != null ? indent.getTotalIntentValue() : BigDecimal.ZERO;
                departmentName = indent.getIndentorDepartment();
                category = indent.getMaterialCategoryType();
                location = indent.getConsignesLocation();
            }
        }

        com.astro.entity.AdminPanel.ApproverMaster nextApprover = branchWorkflowService.getNextApproverWithLimitCheck(
                pendingTransition.getBranchId(),
                pendingTransition.getApprovalLevel(),
                pendingTransition.getApprovalSequence(),
                indentValue,
                departmentName,
                category,
                location
        );

        if (nextApprover != null) {
            com.astro.dto.workflow.EscalationCheckResultDTO escalationResult
                    = branchWorkflowService.checkEscalationRequired(
                            nextApprover.getRoleName(),
                            indentValue,
                            category,
                            departmentName,
                            location
                    );

            if (escalationResult != null && escalationResult.isEscalationRequired()) {
                if (indent != null) {
                    indent.setEscalatedToDirector(true);
                    indent.setEscalationReason(escalationResult.getReason());
                    indentCreationRepository.save(indent);
                }

                TransitionActionReqDto autoActionDto = new TransitionActionReqDto();
                autoActionDto.setAction("Auto-Approved");
                autoActionDto.setRemarks("AUTO-APPROVED: " + pendingTransition.getNextRole() + " did not act within " + autoApproveHours + " hours");
                autoActionDto.setActionBy(null);

                WorkflowTransition escalated = createEscalationTransition(pendingTransition, autoActionDto,
                        escalationResult.getEscalateTo(), escalationResult.getReason());
                return mapToWorkflowTransitionDto(escalated);
            }
        }

        WorkflowTransition nextWorkflowTransition = new WorkflowTransition();
        nextWorkflowTransition.setWorkflowId(pendingTransition.getWorkflowId());
        nextWorkflowTransition.setWorkflowName(pendingTransition.getWorkflowName());
        nextWorkflowTransition.setRequestId(pendingTransition.getRequestId());
        nextWorkflowTransition.setCreatedDate(pendingTransition.getCreatedDate());
        nextWorkflowTransition.setCreatedBy(pendingTransition.getCreatedBy());
        nextWorkflowTransition.setModifiedBy(null);
        nextWorkflowTransition.setModificationDate(new Date());
        nextWorkflowTransition.setAction("Auto-Approved");
        nextWorkflowTransition.setRemarks("AUTO-APPROVED: " + pendingTransition.getNextRole() + " did not act within " + autoApproveHours + " hours");
        nextWorkflowTransition.setCurrentRole(pendingTransition.getNextRole());
        nextWorkflowTransition.setWorkflowSequence(pendingTransition.getWorkflowSequence() + 1);
        nextWorkflowTransition.setBranchId(pendingTransition.getBranchId());
        nextWorkflowTransition.setTransitionOrder(
                (pendingTransition.getTransitionOrder() != null ? pendingTransition.getTransitionOrder() : 0) + 1
        );
        nextWorkflowTransition.setTransitionSubOrder(1);

        if (nextApprover != null) {
            nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
            nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
            nextWorkflowTransition.setNextRole(nextApprover.getRoleName());
            nextWorkflowTransition.setApproverId(nextApprover.getApproverId());
            nextWorkflowTransition.setApprovalLevel(nextApprover.getApprovalLevel());
            nextWorkflowTransition.setApprovalSequence(nextApprover.getApprovalSequence());

            // Reporting Officer: resolve and assign to specific user (auto-approval path)
            if (isReportingOfficerRole(nextApprover.getRoleName())) {
                try {
                    Map<String, Object> roInfo = resolveReportingOfficer(pendingTransition.getCreatedBy());
                    nextWorkflowTransition.setAssignedToUserId((Integer) roInfo.get("userId"));
                    nextWorkflowTransition.setAssignedToEmployeeId((String) roInfo.get("employeeId"));
                } catch (BusinessException e) {
                    System.err.println("Auto-approval: Could not resolve reporting officer: " + e.getMessage());
                }
            }

            // Project Head: resolve and assign to specific project head user (auto-approval path)
            if (isProjectHeadRole(nextApprover.getRoleName())) {
                try {
                    Map<String, Object> phInfo = resolveProjectHead(pendingTransition.getRequestId());
                    nextWorkflowTransition.setAssignedToUserId((Integer) phInfo.get("userId"));
                    nextWorkflowTransition.setAssignedToEmployeeId((String) phInfo.get("employeeId"));
                } catch (BusinessException e) {
                    System.err.println("Auto-approval: Could not resolve project head: " + e.getMessage());
                }
            }

            // Location-based roles (Professor In Charge / Engineer In Charge) (auto-approval path)
            if (isLocationBasedRole(nextApprover.getRoleName())) {
                try {
                    Map<String, Object> locInfo = resolveLocationBasedApprover(
                            pendingTransition.getRequestId(), nextApprover.getRoleName());
                    nextWorkflowTransition.setAssignedToUserId((Integer) locInfo.get("userId"));
                    nextWorkflowTransition.setAssignedToEmployeeId((String) locInfo.get("employeeId"));
                } catch (BusinessException e) {
                    System.err.println("Auto-approval: Could not resolve " + nextApprover.getRoleName() + ": " + e.getMessage());
                }
            }
        } else {
            nextWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
            nextWorkflowTransition.setNextAction(null);
            nextWorkflowTransition.setNextRole(null);

            if (indent != null) {
                indent.setCurrentStatus("APPROVED");
                indent.setCurrentStage("INDENT_APPROVED");
                indent.setIsEditable(false);
                indentCreationRepository.save(indent);
            }
        }

        workflowTransitionRepository.save(nextWorkflowTransition);
        return mapToWorkflowTransitionDto(nextWorkflowTransition);
    }

    /**
     * LEGACY TRANSITION-MASTER BASED APPROVAL (Fallback)
     */
    private WorkflowTransition approveLegacyTransition(WorkflowTransition currentWorkflowTransition, TransitionMaster currentTransition, TransitionActionReqDto transitionActionReqDto) {
        TransitionDto nextTransition = null;
        WorkflowTransition nextWorkflowTransition = null;

        if (Objects.isNull(currentTransition.getNextRoleId())) {
            currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
            workflowTransitionRepository.save(currentWorkflowTransition);

            nextWorkflowTransition = new WorkflowTransition();
            nextWorkflowTransition.setWorkflowId(currentWorkflowTransition.getWorkflowId());
            nextWorkflowTransition.setTransitionId(currentWorkflowTransition.getTransitionId());
            nextWorkflowTransition.setTransitionOrder(currentWorkflowTransition.getTransitionOrder());
            nextWorkflowTransition.setWorkflowName(currentWorkflowTransition.getWorkflowName());
            nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
            nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
            nextWorkflowTransition.setTransitionSubOrder(currentWorkflowTransition.getTransitionSubOrder());
            nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
            nextWorkflowTransition.setModificationDate(new Date());
            nextWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
            nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
            nextWorkflowTransition.setNextAction(null);
            nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
            nextWorkflowTransition.setCurrentRole(currentWorkflowTransition.getNextRole());
            nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

            workflowTransitionRepository.save(nextWorkflowTransition);
        } else {
            nextTransition = nextTransition(currentTransition.getWorkflowId(), currentWorkflowTransition.getWorkflowName(), transitionActionReqDto.getRoleName(), currentWorkflowTransition.getRequestId());

            if (Objects.isNull(nextTransition)) {
                throw new InvalidInputException(new ErrorDetails(AppConstant.NEXT_TRANSITION_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION, "Error occurred at approval. No next transition found."));
            }

            //update currentWorkflowTransition nextSatus and save
            currentWorkflowTransition.setNextAction(AppConstant.COMPLETED_TYPE);
            workflowTransitionRepository.save(currentWorkflowTransition);

            nextWorkflowTransition = new WorkflowTransition();
            nextWorkflowTransition.setWorkflowId(nextTransition.getWorkflowId());
            nextWorkflowTransition.setTransitionId(nextTransition.getTransitionId());
            nextWorkflowTransition.setTransitionOrder(nextTransition.getTransitionOrder());
            nextWorkflowTransition.setTransitionSubOrder(nextTransition.getTransitionSubOrder());
            nextWorkflowTransition.setWorkflowName(nextTransition.getWorkflowName());
            if (Objects.isNull(nextTransition.getNextRoleId())) {
                nextWorkflowTransition.setStatus(AppConstant.COMPLETED_TYPE);
                nextWorkflowTransition.setNextAction(null);
            } else {
                nextWorkflowTransition.setStatus(AppConstant.IN_PROGRESS_TYPE);
                nextWorkflowTransition.setNextAction(AppConstant.PENDING_TYPE);
            }

            nextWorkflowTransition.setAction(transitionActionReqDto.getAction());
            nextWorkflowTransition.setRemarks(transitionActionReqDto.getRemarks());
            nextWorkflowTransition.setModifiedBy(transitionActionReqDto.getActionBy());
            nextWorkflowTransition.setModificationDate(new Date());
            nextWorkflowTransition.setRequestId(currentWorkflowTransition.getRequestId());
            nextWorkflowTransition.setCreatedBy(currentWorkflowTransition.getCreatedBy());
            nextWorkflowTransition.setCreatedDate(currentWorkflowTransition.getCreatedDate());
            nextWorkflowTransition.setCurrentRole(nextTransition.getCurrentRoleName());
            nextWorkflowTransition.setNextRole(nextTransition.getNextRoleName());
            nextWorkflowTransition.setWorkflowSequence(currentWorkflowTransition.getWorkflowSequence() + 1);

            workflowTransitionRepository.save(nextWorkflowTransition);
        }

        //validation for tender workflow
        if (WorkflowName.TENDER_EVALUATOR.getValue().equalsIgnoreCase(currentWorkflowTransition.getWorkflowName())) {
            validateTenderWorkFlow(currentWorkflowTransition, nextWorkflowTransition, AppConstant.APPROVE_TYPE);
        }

        // Bug Fix 1 & 4: Update indent status and editability when approved
        if ("Indent Workflow".equalsIgnoreCase(currentWorkflowTransition.getWorkflowName())) {
            String requestId = currentWorkflowTransition.getRequestId();
            if (requestId != null && requestId.startsWith("IND")) {
                // Make final copy for lambda
                final WorkflowTransition finalNextWorkflowTransition = nextWorkflowTransition;

                indentCreationRepository.findById(requestId).ifPresent(indent -> {
                    // Make indent non-editable after submission/approval
                    indent.setIsEditable(false);

                    // Update status based on workflow completion
                    if (AppConstant.COMPLETED_TYPE.equalsIgnoreCase(finalNextWorkflowTransition.getStatus())) {
                        indent.setCurrentStatus("APPROVED");
                        indent.setCurrentStage("INDENT_APPROVED");
                    } else {
                        indent.setCurrentStatus("IN_APPROVAL");
                        indent.setCurrentStage("INDENT_APPROVAL_LEVEL_" + finalNextWorkflowTransition.getWorkflowSequence());
                        indent.setApprovalLevel(finalNextWorkflowTransition.getWorkflowSequence());
                    }
                    indentCreationRepository.save(indent);
                });
            }
        }

        return nextWorkflowTransition;
    }

    private WorkflowTransitionDto mapToWorkflowTransitionDto(WorkflowTransition nextWorkflowTransition) {
        WorkflowTransitionDto dto = new WorkflowTransitionDto();
        dto.setWorkflowTransitionId(nextWorkflowTransition.getWorkflowTransitionId());
        dto.setWorkflowId(nextWorkflowTransition.getWorkflowId());
        dto.setTransitionId(nextWorkflowTransition.getTransitionId());
        dto.setTransitionOrder(nextWorkflowTransition.getTransitionOrder());
        dto.setTransitionSubOrder(nextWorkflowTransition.getTransitionSubOrder());
        dto.setWorkflowName(nextWorkflowTransition.getWorkflowName());
        dto.setStatus(nextWorkflowTransition.getStatus());
        dto.setNextAction(nextWorkflowTransition.getNextAction());
        dto.setAction(nextWorkflowTransition.getAction());
        dto.setRemarks(nextWorkflowTransition.getRemarks());
        dto.setModifiedBy(nextWorkflowTransition.getModifiedBy());
        dto.setModificationDate(nextWorkflowTransition.getModificationDate());
        dto.setRequestId(nextWorkflowTransition.getRequestId());
        dto.setCreatedBy(nextWorkflowTransition.getCreatedBy());
        dto.setCreatedDate(nextWorkflowTransition.getCreatedDate());
        dto.setCurrentRole(nextWorkflowTransition.getCurrentRole());
        dto.setNextRole(nextWorkflowTransition.getNextRole());
        dto.setWorkflowSequence(nextWorkflowTransition.getWorkflowSequence());

        // Reporting Officer assignment fields
        dto.setAssignedToUserId(nextWorkflowTransition.getAssignedToUserId());
        dto.setAssignedToEmployeeId(nextWorkflowTransition.getAssignedToEmployeeId());
        if (nextWorkflowTransition.getAssignedToEmployeeId() != null) {
            employeeDepartmentMasterRepository.findByEmployeeId(nextWorkflowTransition.getAssignedToEmployeeId())
                    .ifPresent(emp -> dto.setAssignedToEmployeeName(emp.getEmployeeName()));
        }

        return dto;
    }

    private List<SubWorkflowTransitionDto> validateTenderWorkFlow(WorkflowTransition currentWorkflowTransition, WorkflowTransition nextWorkflowTransition, String actionType) {
        List<SubWorkflowTransitionDto> subWorkflowDtoList = new ArrayList<>();
        if ((nextWorkflowTransition.getCurrentRole().equalsIgnoreCase("Purchase Dept") && Objects.isNull(nextWorkflowTransition.getNextRole())) || (nextWorkflowTransition.getCurrentRole().equalsIgnoreCase("Purchase Dept") && Objects.nonNull(nextWorkflowTransition.getNextRole()) && nextWorkflowTransition.getNextRole().equalsIgnoreCase("Purchase Dept"))) {
            List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByWorkflowTransitionIdAndStatus(currentWorkflowTransition.getWorkflowTransitionId(), AppConstant.PENDING_TYPE);
            if (Objects.nonNull(subWorkflowTransitionList) && !subWorkflowTransitionList.isEmpty()) {
                throw new InvalidInputException(new ErrorDetails(AppConstant.NEXT_TRANSITION_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION, "Error occurred at approval. All indentor not performed action for this workflow to approve."));
            }

        }
        if ((nextWorkflowTransition.getCurrentRole().equalsIgnoreCase("Tender Evaluator")) || (nextWorkflowTransition.getCurrentRole().equalsIgnoreCase("Purchase Dept") && Objects.nonNull(nextWorkflowTransition.getNextRole()) && nextWorkflowTransition.getNextRole().equalsIgnoreCase("Purchase Dept"))) {
            TenderWithIndentResponseDTO tenderWithIndentResponseDTO = tenderRequestService.getTenderRequestById(nextWorkflowTransition.getRequestId());
            if (Objects.nonNull(tenderWithIndentResponseDTO) && Objects.nonNull(tenderWithIndentResponseDTO.getIndentResponseDTO()) && !tenderWithIndentResponseDTO.getIndentResponseDTO().isEmpty()) {
                List<IndentCreationResponseDTO> indentResponseDTO = tenderWithIndentResponseDTO.getIndentResponseDTO();
                List<Integer> indenterList = indentResponseDTO.stream().map(e -> e.getCreatedBy()).collect(Collectors.toList());
                if (Objects.nonNull(indenterList) && !indenterList.isEmpty()) {
                    AtomicInteger seq = new AtomicInteger(1);
                    indenterList.forEach(e -> {
                        SubWorkflowTransition subWorkflowTransition = new SubWorkflowTransition();
                        subWorkflowTransition.setWorkflowId(nextWorkflowTransition.getWorkflowId());
                        subWorkflowTransition.setWorkflowTransitionId(nextWorkflowTransition.getWorkflowTransitionId());
                        subWorkflowTransition.setAction(AppConstant.PENDING_TYPE);
                        subWorkflowTransition.setWorkflowName(nextWorkflowTransition.getWorkflowName());
                        subWorkflowTransition.setActionOn(e);
                        /* if (tenderWithIndentResponseDTO.getTotalTenderValue().compareTo(BigDecimal.valueOf(1000000)) < 0) {
                            subWorkflowTransition.setActionOn(e); // Indentor
                        } else {
                            subWorkflowTransition.setActionOn(26); // Evaluator
                        }*/
                        subWorkflowTransition.setCreatedBy(nextWorkflowTransition.getModifiedBy());
                        subWorkflowTransition.setWorkflowSequence(seq.get());
                        subWorkflowTransition.setRequestId(nextWorkflowTransition.getRequestId());
                        subWorkflowTransition.setStatus(AppConstant.PENDING_TYPE);
                        subWorkflowTransition.setCreatedDate(new Date());
                        subWorkflowTransition.setTransitionType(tenderWithIndentResponseDTO.getBidType());
                        if ("Double".equalsIgnoreCase(tenderWithIndentResponseDTO.getBidType()) && Objects.isNull(actionType)) {
                            subWorkflowTransition.setTransitionName("Phase_1");
                        } else if ("Double".equalsIgnoreCase(tenderWithIndentResponseDTO.getBidType()) && AppConstant.APPROVE_TYPE.equalsIgnoreCase(actionType)) {
                            subWorkflowTransition.setTransitionName("Phase_2");
                        }
                        seq.getAndIncrement();

                        subWorkflowTransitionRepository.save(subWorkflowTransition);
                        SubWorkflowTransitionDto subDto = new SubWorkflowTransitionDto();
                        subDto.setSubWorkflowTransitionId(subWorkflowTransition.getSubWorkflowTransitionId());
                        subDto.setWorkflowId(subWorkflowTransition.getWorkflowId());
                        subDto.setWorkflowName(subWorkflowTransition.getWorkflowName());
                        subDto.setRequestId(subWorkflowTransition.getRequestId());
                        subDto.setCreatedBy(subWorkflowTransition.getCreatedBy());
                        subDto.setModifiedBy(subWorkflowTransition.getModifiedBy());
                        subDto.setStatus(subWorkflowTransition.getStatus());
                        subDto.setAction(subWorkflowTransition.getAction());
                        subDto.setRemarks(subWorkflowTransition.getRemarks());
                        subDto.setActionOn(subWorkflowTransition.getActionOn());
                        subDto.setWorkflowSequence(subWorkflowTransition.getWorkflowSequence());
                        subDto.setModificationDate(subWorkflowTransition.getModificationDate());
                        subDto.setCreatedDate(subWorkflowTransition.getCreatedDate());

                        subWorkflowDtoList.add(subDto);

                    });
                }
            }
        }

        return subWorkflowDtoList;

    }

    private void validateUserRole(Integer actionBy, Integer roleId) {
        if (Objects.nonNull(roleId)) {
            UserRoleMaster userRoleMaster = userRoleMasterRepository.findByRoleIdAndUserId(roleId, actionBy);
            if (Objects.isNull(userRoleMaster)) {
                throw new InvalidInputException(new ErrorDetails(AppConstant.UNAUTHORIZED_ACTION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION, "Unauthorized user."));
            }
        }
    }

    private TransitionDto nextTransitionDto(List<TransitionDto> nextTransitionDtoList, String workflowName, String requestId) {
        TransitionDto transitionDto = null;
        List<Integer> conditionIdList = nextTransitionDtoList.stream().filter(f -> Objects.nonNull(f.getConditionId())).map(e -> e.getConditionId()).collect(Collectors.toList());

        //for without any condition move and have only one next move
        if (conditionIdList.isEmpty() && nextTransitionDtoList.size() == 1) {
            return nextTransitionDtoList.get(0);
        } else {
            List<TransitionConditionMaster> transitionConditionMasterList = transitionConditionMasterRepository.findAllById(conditionIdList);

            switch (workflowName.toUpperCase()) {
                case "INDENT WORKFLOW":
                    //get indent data here
                    IndentCreationResponseDTO indentCreationResponseDTO = indentCreationService.getIndentById(requestId);
                    for (TransitionDto dto : nextTransitionDtoList) {
                        Integer conditionId = dto.getConditionId();
                        if (Objects.nonNull(conditionId)) {
                            TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
                            String conditionKey = transitionConditionMaster.getConditionKey();
                            String conditionValue = transitionConditionMaster.getConditionValue();
                            Object dataValue = null;
                            boolean conditionCheckFlag = Boolean.FALSE;
                            if (conditionKey.equalsIgnoreCase("ProjectName")) {
                                dataValue = indentCreationResponseDTO.getProjectName();
                                conditionCheckFlag = Objects.nonNull(dataValue);
                            } else if (conditionKey.equalsIgnoreCase("MaterialCategory")) {
                                dataValue = indentCreationResponseDTO.getMaterialCategory();
                                conditionCheckFlag = ((String) dataValue).equalsIgnoreCase(conditionValue);
                            } else if (conditionKey.equalsIgnoreCase("ConsignesLocation")) {
                                dataValue = indentCreationResponseDTO.getConsignesLocation();
                                conditionCheckFlag = ((String) dataValue).equalsIgnoreCase(conditionValue);
                            } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterials")) {
                                dataValue = indentCreationResponseDTO.getTotalPriceOfAllMaterials();
                                conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
                            } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterialsAnd")) {
                                dataValue = indentCreationResponseDTO.getTotalPriceOfAllMaterials();
                                conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() > Double.valueOf(conditionValue);
                            } else if (conditionKey.equalsIgnoreCase("projectLimit")) {
                                dataValue = indentCreationResponseDTO.getTotalPriceOfAllMaterials();
                                BigDecimal projectLimit = indentCreationResponseDTO.getProjectLimit();
                                conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= ((BigDecimal) projectLimit).doubleValue();
                            } else if (conditionKey.equalsIgnoreCase("materialCategoryAndconsignesLocation")) {
                                String category = indentCreationResponseDTO.getMaterialCategory();
                                String location = indentCreationResponseDTO.getConsignesLocation();

                                if (category != null && location != null) {
                                    String combinedValue = category + "+" + location;
                                    conditionCheckFlag = combinedValue.equalsIgnoreCase(conditionValue);
                                }
                            } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterialsAndDept")) {
                                dataValue = indentCreationResponseDTO.getTotalPriceOfAllMaterials();
                                String department = indentCreationResponseDTO.getEmployeeDepartment();

                                if (conditionValue != null && conditionValue.contains("(") && conditionValue.endsWith(")")) {
                                    String[] valueParts = conditionValue.replace(")", "").split("\\(");
                                    if (valueParts.length == 2) {
                                        double priceLimit = Double.parseDouble(valueParts[0]);
                                        String requiredDept = valueParts[1];

                                        if (dataValue != null && department != null && department.equalsIgnoreCase(requiredDept)) {
                                            double actualValue = ((BigDecimal) dataValue).doubleValue();

                                            if (requiredDept.equalsIgnoreCase("Engineering")) {
                                                /*if (priceLimit == 50000) {
                                                    conditionCheckFlag = actualValue > 50000 && actualValue <= 100000;
                                                } else if (priceLimit == 100000) {
                                                    conditionCheckFlag = actualValue > 100000;
                                                }*/
                                                if (priceLimit == 100000) {

                                                    conditionCheckFlag = actualValue <= 100000 || actualValue >= 100000;
                                                }

                                            } else if (requiredDept.equalsIgnoreCase("OtherDept")) {
                                                if (priceLimit == 150000) {
                                                    conditionCheckFlag = actualValue <= 150000 || actualValue >= 150000;
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                            if (conditionCheckFlag) {
                                transitionDto = dto;
                                break;
                            }
                        }
                    }
                    break;
                case "CONTINGENCY PURCHASE WORKFLOW":
                    ContigencyPurchaseResponseDto contigencyPurchaseResponseDto = contigencyPurchaseService.getContigencyPurchaseById(requestId);
                    for (TransitionDto dto : nextTransitionDtoList) {
                        Integer conditionId = dto.getConditionId();
                        if (Objects.nonNull(conditionId)) {
                            TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
                            String conditionKey = transitionConditionMaster.getConditionKey();
                            String conditionValue = transitionConditionMaster.getConditionValue();
                            Object dataValue = null;
                            boolean conditionCheckFlag = Boolean.FALSE;
                            if (conditionKey.equalsIgnoreCase("ProjectName")) {
                                dataValue = contigencyPurchaseResponseDto.getProjectName();
                                if ("Empty".equalsIgnoreCase(conditionValue)) {
                                    conditionCheckFlag = Objects.isNull(dataValue);
                                } else if ("Not Empty".equalsIgnoreCase(conditionValue)) {
                                    conditionCheckFlag = Objects.nonNull(dataValue);
                                }
                            }

                            if (conditionCheckFlag) {
                                transitionDto = dto;
                                break;
                            }
                        }
                    }
                    break;
                case "TENDER EVALUATOR WORKFLOW":
                    TenderWithIndentResponseDTO tenderWithIndentResponseDTO = tenderRequestService.getTenderRequestById(requestId);
                    for (TransitionDto dto : nextTransitionDtoList) {
                        Integer conditionId = dto.getConditionId();
                        if (Objects.nonNull(conditionId)) {
                            TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
                            String conditionKey = transitionConditionMaster.getConditionKey();
                            String conditionValue = transitionConditionMaster.getConditionValue();
                            Object dataValue = null;
                            boolean conditionCheckFlag = Boolean.FALSE;
                            if (conditionKey.equalsIgnoreCase("totalTenderValue")) {
                                dataValue = tenderWithIndentResponseDTO.getTotalTenderValue();
                                conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
                            } else if (conditionKey.equalsIgnoreCase("bidType")) {
                                dataValue = tenderWithIndentResponseDTO.getBidType();
                                conditionCheckFlag = ((String) dataValue).equalsIgnoreCase(conditionValue);
                            }
                            if (conditionCheckFlag) {
                                transitionDto = dto;
                                break;
                            }
                        }
                    }
                    break;
                case "SO WORKFLOW":
                    soWithTenderAndIndentResponseDTO soWithTenderAndIndentResponseDTO = serviceOrderService.getServiceOrderById(requestId);
                    for (TransitionDto dto : nextTransitionDtoList) {
                        Integer conditionId = dto.getConditionId();
                        if (Objects.nonNull(conditionId)) {
                            TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
                            String conditionKey = transitionConditionMaster.getConditionKey();
                            String conditionValue = transitionConditionMaster.getConditionValue();
                            Object dataValue = null;
                            boolean conditionCheckFlag = Boolean.FALSE;
                            if (conditionKey.equalsIgnoreCase("ProjectName")) {
                                dataValue = soWithTenderAndIndentResponseDTO.getProjectName();
                                conditionCheckFlag = Objects.nonNull(dataValue);
                            } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterials")) {
                                dataValue = soWithTenderAndIndentResponseDTO.getTotalValueOfSo();
                                conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
                            }
                            if (conditionCheckFlag) {
                                transitionDto = dto;
                                break;
                            }
                        }
                    }
                    break;
                case "WO WORKFLOW":
                    woWithTenderAndIndentResponseDTO woWithTenderAndIndentResponseDTO = workOrderService.getWorkOrderById(requestId);
                    for (TransitionDto dto : nextTransitionDtoList) {
                        Integer conditionId = dto.getConditionId();
                        if (Objects.nonNull(conditionId)) {
                            TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
                            String conditionKey = transitionConditionMaster.getConditionKey();
                            String conditionValue = transitionConditionMaster.getConditionValue();
                            Object dataValue = null;
                            boolean conditionCheckFlag = Boolean.FALSE;
                            if (conditionKey.equalsIgnoreCase("ProjectName")) {
                                dataValue = woWithTenderAndIndentResponseDTO.getProjectName();
                                conditionCheckFlag = Objects.nonNull(dataValue);
                            } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterials")) {
                                dataValue = woWithTenderAndIndentResponseDTO.getTotalValueOfWo();
                                conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
                            }
                            if (conditionCheckFlag) {
                                transitionDto = dto;
                                break;
                            }
                        }
                    }
                    break;
                case "PO WORKFLOW":
                    poWithTenderAndIndentResponseDTO poWithTenderAndIndentResponseDTO = purchaseOrderService.getPurchaseOrderById(requestId);
                    for (TransitionDto dto : nextTransitionDtoList) {
                        Integer conditionId = dto.getConditionId();
                        if (Objects.nonNull(conditionId)) {
                            TransitionConditionMaster transitionConditionMaster = transitionConditionMasterList.stream().filter(f -> f.getConditionId().equals(dto.getConditionId())).findFirst().get();
                            String conditionKey = transitionConditionMaster.getConditionKey();
                            String conditionValue = transitionConditionMaster.getConditionValue();
                            Object dataValue = null;
                            boolean conditionCheckFlag = Boolean.FALSE;
                            if (conditionKey.equalsIgnoreCase("ProjectName")) {
                                dataValue = poWithTenderAndIndentResponseDTO.getProjectName();
                                conditionCheckFlag = Objects.nonNull(dataValue);
                            } else if (conditionKey.equalsIgnoreCase("TotalPriceOfAllMaterials")) {
                                dataValue = poWithTenderAndIndentResponseDTO.getTotalValueOfPo();
                                conditionCheckFlag = ((BigDecimal) dataValue).doubleValue() <= Double.valueOf(conditionValue);
                            }
                            if (conditionCheckFlag) {
                                transitionDto = dto;
                                break;
                            }
                        }
                    }
                    break;
                //add more case here

            }
        }
        return transitionDto;
    }

    //updated  by abhinav starts
    public List<ApprovedIndentsDto> getApprovedIndents(Integer userId) {

        UserMaster user = userMasterRepository.findByUserId(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        String employeeId = user.getEmployeeId();

        List<IndentAssignment> assignments
                = indentAssignmentRepository
                        .findByAssignedToEmployeeIdAndStatus(employeeId, "ACTIVE");

        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> indentIds = assignments.stream()
                .map(IndentAssignment::getIndentId)
                .collect(Collectors.toList());

        // Now calling correct method
        List<ApprovedIndentsDto> approvedIndents = getAllApprovedIndentsInternal();

        return approvedIndents.stream()
                .filter(dto -> indentIds.contains(dto.getIndentId()))
                .collect(Collectors.toList());
    }

    private List<ApprovedIndentsDto> getAllApprovedIndentsInternal() {

        List<String> approvedIndentIds = workflowTransitionRepository.findApprovedIndentRequestIds();

        List<ApprovedIndentsDto> rawResults
                = indentCreationRepository.findApprovedIndents(approvedIndentIds);

        Map<String, ApprovedIndentsDto> grouped = new LinkedHashMap<>();

        for (ApprovedIndentsDto dto : rawResults) {
            grouped.computeIfAbsent(dto.getIndentId(), k -> {
                ApprovedIndentsDto newDto = new ApprovedIndentsDto();
                newDto.setIndentId(dto.getIndentId());
                newDto.setProjectName(dto.getProjectName());
                newDto.setIndentorName(dto.getIndentorName());
                newDto.setCreatedDate(dto.getCreatedDate());
                newDto.setMaterialDes(new ArrayList<>());
                return newDto;
            }).getMaterialDes().addAll(dto.getMaterialDes());
        }

        return new ArrayList<>(grouped.values());
    }
    // updates by abhinav line ends

    // Old getApprovedIndents() kept for reference
    public List<ApprovedIndentsDto> getApprovedIndents() {
        List<String> approvedIndentIds = workflowTransitionRepository.findApprovedIndentRequestIds();
        System.out.println(approvedIndentIds);
        List<ApprovedIndentsDto> rawResults = indentCreationRepository.findApprovedIndents(approvedIndentIds);

        Map<String, ApprovedIndentsDto> grouped = new LinkedHashMap<>();

        for (ApprovedIndentsDto dto : rawResults) {
            grouped.computeIfAbsent(dto.getIndentId(), k -> {
                ApprovedIndentsDto newDto = new ApprovedIndentsDto();
                newDto.setIndentId(dto.getIndentId());
                newDto.setProjectName(dto.getProjectName());
                newDto.setIndentorName(dto.getIndentorName());
                newDto.setCreatedDate(dto.getCreatedDate());
                newDto.setMaterialDes(new ArrayList<>());
                return newDto;
            }).getMaterialDes().addAll(dto.getMaterialDes());
        }

        return new ArrayList<>(grouped.values());
    }


    /*
    public List<ApprovedIndentsDto> getApprovedIndents() {
        // Step 1: Retrieve all the approved indent request IDs
        List<String> approvedIndentIds = workflowTransitionRepository.findApprovedIndentRequestIds();

        // Step 2: Fetch the project names based on the indent IDs and project codes
        List<ApprovedIndentsDto> approvedIndents = new ArrayList<>();

        for (String indentId : approvedIndentIds) {
            // Fetch the indent details from the IndentCreation entity
            IndentCreation indentCreation = indentCreationRepository.findByIndentId(indentId);

        //  List<MaterialDetails> md =   materialDetailsRepo.findByIndentCreation_IndentId(indentId);
            if (indentCreation != null) {
                // Get the projectCode from IndentCreation
                String projectCode = indentCreation.getProjectName();

                // Fetch the project name from ProjectMaster using the projectCode
                Optional<ProjectMaster> projectMaster = projectMasterRepository.findByProjectCode(projectCode);

                if (projectMaster.isPresent()) {
                    ProjectMaster pm = projectMaster.get();
                    // Create ApprovedIndentsDto object with the indentId and projectName
                    ApprovedIndentsDto dto = new ApprovedIndentsDto();
                    dto.setIndentId(indentCreation.getIndentId());
                    dto.setProjectName(pm.getProjectNameDescription());

                    //dto.setIndentorName(indentCreation.getIndentorName());
                     approvedIndents.add(dto);


                } else {
                    // Handle case where project is not found
                    ApprovedIndentsDto dto = new ApprovedIndentsDto();
                    dto.setIndentId(indentCreation.getIndentId());
                    dto.setProjectName(null);
                    approvedIndents.add(dto);
                }
            } else {
                // Handle case where indent is not found
                ApprovedIndentsDto dto = new ApprovedIndentsDto();
                dto.setIndentId(indentId);
                dto.setProjectName("Indent not found");
                approvedIndents.add(dto);
            }
        }

        return approvedIndents;
    }*//*
public List<ApprovedIndentsDto> getApprovedIndents() {
    List<String> approvedIndentIds = workflowTransitionRepository.findApprovedIndentRequestIds();

    List<IndentCreation> indentCreations = indentCreationRepository.findByIndentIdIn(approvedIndentIds);

    Map<String, IndentCreation> indentMap = indentCreations.stream()
            .collect(Collectors.toMap(IndentCreation::getIndentId, ic -> ic));

    Set<String> projectCodes = indentCreations.stream()
            .map(IndentCreation::getProjectName)
            .collect(Collectors.toSet());

    List<ProjectMaster> projectMasters = projectMasterRepository.findByProjectCodeIn(projectCodes);

    Map<String, String> projectCodeToNameMap = projectMasters.stream()
            .collect(Collectors.toMap(ProjectMaster::getProjectCode, ProjectMaster::getProjectNameDescription));

    List<ApprovedIndentsDto> approvedIndents = new ArrayList<>();

    for (String indentId : approvedIndentIds) {
        ApprovedIndentsDto dto = new ApprovedIndentsDto();
        dto.setIndentId(indentId);

        IndentCreation ic = indentMap.get(indentId);
        if (ic != null) {
            String projectName = projectCodeToNameMap.get(ic.getProjectName());
            dto.setProjectName(projectName != null ? projectName : "Project not found");
        } else {
            dto.setProjectName("Indent not found");
        }

        approvedIndents.add(dto);
    }

    return approvedIndents;
}

     */

    @Override
    public List<String> getApprovedTenderIdsForPOAndSO() {
        return workflowTransitionRepository.findApprovedTenderIdsForPOANDSO();
    }

    @Override
    public List<String> getApprovedPoIds() {
        return workflowTransitionRepository.findApprovedPoIds();
    }


    /*  @Override
    public List<ApprovedPoIdsDto> getApprovedPoIds() {

       List<String> poIds= workflowTransitionRepository.findApprovedPoIds();
       List<ApprovedPoIdsDto> approvedPoIdsDtos = new ArrayList<>();
       for(String poId : poIds){
          PurchaseOrder purchaseOrder =purchaseOrderRepository.findByPoId(poId);

          IndentCreation ind = indentCreationRepository.findByIndentId(purchaseOrder.getIndentId());
          ApprovedPoIdsDto dto = new ApprovedPoIdsDto(
                  poId,
                  (ind != null) ? ind.getIndentorName() : null,
                  purchaseOrder.getVendorName()
          );
          approvedPoIdsDtos.add(dto);

       }
       return approvedPoIdsDtos;

    }

     */
    public List<ApprovedTenderDto> getApprovedTender() {

        // return workflowTransitionRepository.findApprovedTenderRequestIds();
        List<String> tenderIds = workflowTransitionRepository.findApprovedTenderRequestIds();

        List<ApprovedTenderDto> approvedTenders = new ArrayList<>();

        for (String tenderId : tenderIds) {
            // Fetch bidType and totalValue from TenderRequest table
            Optional<TenderRequest> optionalTenderRequest = tenderRequestRepository.findByTenderId(tenderId);
            List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
            // int indentNumber = indentIds != null ? indentIds.size() : 0;
            //  System.out.println("indent number"+ indentIds +""+ indentNumber);
            int indentNumber = 1;
            if (optionalTenderRequest.isPresent()) {
                TenderRequest tenderRequest = optionalTenderRequest.get();
                ApprovedTenderDto dto = new ApprovedTenderDto(
                        tenderId,
                        tenderRequest.getBidType(),
                        tenderRequest.getTotalTenderValue(),
                        indentNumber
                );
                approvedTenders.add(dto);
            }
        }
        return approvedTenders;

    }

    public ApprovedTenderDto getApprovedTenderId(String tenderId) {
        boolean tender = workflowTransitionRepository.isApprovedTenderAndNotUsed(tenderId);
        if (!tender) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Tender is not approved, already used."
            ));
        }

        TenderRequest tenderRequest = tenderRequestRepository.findByTenderId(tenderId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Tender request not found for the provided tender ID: " + tenderId
        )));
        //int indentNumber=1;
        List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
        int indentNumber = indentIds != null ? indentIds.size() : 0;
        return new ApprovedTenderDto(
                tenderId,
                tenderRequest.getBidType(),
                tenderRequest.getTotalTenderValue(),
                indentNumber
        );
    }

    /*public List<ApprovedTenderDto> getApprovedTender(String roleName) {
     List<String> tenderIds = workflowTransitionRepository.findApprovedTenderRequestIds();
     List<ApprovedTenderDto> approvedTenders = new ArrayList<>();

     for (String tenderId : tenderIds) {
         Optional<TenderRequest> optionalTenderRequest = tenderRequestRepository.findByTenderId(tenderId);

         if (optionalTenderRequest.isPresent()) {
             TenderRequest tenderRequest = optionalTenderRequest.get();
             BigDecimal totalValue = tenderRequest.getTotalTenderValue();

             // Role-based filtering
             if ("Indent Creator".equalsIgnoreCase(roleName) && totalValue.compareTo(BigDecimal.valueOf(10_00_000)) <= 0) {
                 approvedTenders.add(new ApprovedTenderDto(tenderId, tenderRequest.getBidType(), totalValue));
             } else if ("Tender Evaluator".equalsIgnoreCase(roleName) && totalValue.compareTo(BigDecimal.valueOf(10_00_000)) > 0) {
                 approvedTenders.add(new ApprovedTenderDto(tenderId, tenderRequest.getBidType(), totalValue));
             }
         }
     }

     return approvedTenders;
 }*/
    @Override
    public List<QueueResponse> allPendingWorkflowTransitionINQueue(String roleName) {
        return allPendingWorkflowTransitionINQueue(roleName, null);
    }

    @Override
    public List<QueueResponse> allPendingWorkflowTransitionINQueue(String roleName, Integer userId) {
        List<QueueResponse> queueResponseList = new ArrayList<>();

        // Fetch Workflow Transitions based on role and pending action
        List<WorkflowTransition> workflowTransitionList;
        if (userId != null) {
            workflowTransitionList = workflowTransitionRepository.findPendingByRoleAndOptionalUser(AppConstant.PENDING_TYPE, roleName, userId);
        } else {
            workflowTransitionList = workflowTransitionRepository.findByNextActionAndNextRole(AppConstant.PENDING_TYPE, roleName);
        }

        // Filter by user's employee location against branch location condition.
        // Personally-assigned transitions (assignedToUserId != null, e.g. Reporting Officer) are exempt —
        // the RO is already explicitly assigned and must see the indent regardless of their own location.
        if (userId != null && workflowTransitionList != null && !workflowTransitionList.isEmpty()) {
            UserMaster queueUser = userMasterRepository.findByUserId(userId);
            if (queueUser != null && queueUser.getEmployeeId() != null) {
                EmployeeDepartmentMaster queueEmp = employeeDepartmentMasterRepository.findByEmployeeId(queueUser.getEmployeeId()).orElse(null);
                if (queueEmp != null && queueEmp.getLocation() != null) {
                    String userLocation = queueEmp.getLocation().trim().toUpperCase();
                    // Global approvers (location = "ALL") see items from every branch location — skip filter
                    if (!"ALL".equals(userLocation)) {
                        workflowTransitionList = workflowTransitionList.stream()
                                .filter(wt -> {
                                    if (wt.getAssignedToUserId() != null) {
                                        return true;
                                    }
                                    if (wt.getBranchId() == null) {
                                        return true;
                                    }
                                    return workflowBranchMasterRepository.findById(wt.getBranchId())
                                            .map(branch -> {
                                                if (branch.getConditionConfig() == null || branch.getConditionConfig().trim().isEmpty()) {
                                                    return true;
                                                }
                                                try {
                                                    Map<String, Object> config = objectMapper.readValue(branch.getConditionConfig(), new TypeReference<Map<String, Object>>() {
                                                    });
                                                    if (!config.containsKey("location")) {
                                                        return true;
                                                    }
                                                    String branchLocation = String.valueOf(config.get("location")).trim().toUpperCase();
                                                    return branchLocation.equals(userLocation);
                                                } catch (Exception e) {
                                                    return true;
                                                }
                                            })
                                            .orElse(true);
                                })
                                .collect(Collectors.toList());
                    }
                }
            }
        }

        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
            queueResponseList = workflowTransitionList.stream()
                    .sorted(Comparator.comparing(WorkflowTransition::getRequestId).thenComparing(WorkflowTransition::getCreatedDate))
                    .map(this::mapToQueueResponse)
                    .collect(Collectors.toList());
        }
        System.out.println(roleName);

        if ("Store Purchase Officer".equalsIgnoreCase(roleName)) {
            List<VendorRegistrationResponseDTO> awaitingApprovalVendors = vendorMasterUtilService.getAllAwaitingApprovalVendors();

            List<QueueResponse> vendorQueueResponses = awaitingApprovalVendors.stream()
                    .map(this::mapVendorToQueueResponse)
                    .collect(Collectors.toList());
            System.out.println("Awaiting Vendors: " + awaitingApprovalVendors.size());
            queueResponseList.addAll(vendorQueueResponses);

            // Fetch awaiting approval materials
            List<MaterialMasterUtilResponseDto> awaitingApprovalMaterials
                    = materialMasterUtilService.getAllAwaitingApprovalMaterials();

            List<QueueResponse> materialQueueResponses = awaitingApprovalMaterials.stream()
                    .map(this::mapMaterialToQueueResponse)
                    .collect(Collectors.toList());

            System.out.println("Awaiting Materials: " + awaitingApprovalMaterials.size());
            queueResponseList.addAll(materialQueueResponses);

            // Fetch awaiting approval jobs
            List<com.astro.dto.workflow.JobMasterResponseDto> awaitingApprovalJobs
                    = jobMasterService.getAllAwaitingApprovalJobs();

            List<QueueResponse> jobQueueResponses = awaitingApprovalJobs.stream()
                    .map(this::mapJobToQueueResponse)
                    .collect(Collectors.toList());

            System.out.println("Awaiting Jobs: " + awaitingApprovalJobs.size());
            queueResponseList.addAll(jobQueueResponses);
        }

        if ("Indent Creator".equalsIgnoreCase(roleName)) {
            List<MaterialMasterUtilResponseDto> changeRequestMaterials
                    = materialMasterUtilService.getAllChangeRequestMaterials();

            List<QueueResponse> changeRequestQueueResponses = changeRequestMaterials.stream()
                    .map(this::mapMaterialToQueueResponse)
                    .collect(Collectors.toList());
            queueResponseList.addAll(changeRequestQueueResponses);

            // Fetch change request jobs for Indent Creator
            List<com.astro.dto.workflow.JobMasterResponseDto> changeRequestJobs
                    = jobMasterService.getAllChangeRequestJobs();

            List<QueueResponse> changeRequestJobResponses = changeRequestJobs.stream()
                    .map(this::mapJobToQueueResponse)
                    .collect(Collectors.toList());
            queueResponseList.addAll(changeRequestJobResponses);
        }

        return queueResponseList;
    }

    private QueueResponse mapMaterialToQueueResponse(MaterialMasterUtilResponseDto material) {

        String materialCode = material.getMaterialCode();

        Optional<MaterialMasterUtil> Material = materialMasterUtilRepository.findByMaterialCode(materialCode);

        MaterialMasterUtil ma = Material.get();
        UserMaster us = userMasterRepository.findByUserId(ma.getCreatedBy());
        QueueResponse response = new QueueResponse();
        response.setRequestId(material.getMaterialCode());
        response.setWorkflowName("Material Workflow");
        response.setWorkflowId(9);
        response.setAmount(ma.getUnitPrice());

        // Null check to prevent NullPointerException
        if (us != null) {
            response.setIndentorName(us.getUserName());
        } else {
            response.setIndentorName("Unknown User");
        }

        response.setStatus(material.getApprovalStatus());
        return response;
    }

    private QueueResponse mapJobToQueueResponse(com.astro.dto.workflow.JobMasterResponseDto job) {
        QueueResponse response = new QueueResponse();
        response.setRequestId(job.getJobCode());
        response.setWorkflowName("Job Workflow");
        response.setWorkflowId(11);
        response.setAmount(job.getEstimatedPriceWithCcy());
        response.setIndentorName(job.getUpdatedBy() != null ? job.getUpdatedBy() : "Unknown User");
        response.setStatus(job.getApprovalStatus());
        return response;
    }

    private QueueResponse mapVendorToQueueResponse(VendorRegistrationResponseDTO vendor) {
        QueueResponse response = new QueueResponse();
        response.setRequestId(vendor.getVendorId());
        response.setIndentorName(vendor.getVendorName());
        response.setStatus(vendor.getApprovalStatus());
        response.setWorkflowName("Vendor Workflow");
        response.setWorkflowId(8); // ← ADD THIS LINE
        return response;
    }

    // Mapping function to convert WorkflowTransition to QueueResponse
    private QueueResponse mapToQueueResponse(WorkflowTransition workflowTransition) {
        QueueResponse queueResponse = new QueueResponse();

        // Mapping existing attributes
        queueResponse.setWorkflowTransitionId(workflowTransition.getWorkflowTransitionId());
        queueResponse.setWorkflowId(workflowTransition.getWorkflowId());
        queueResponse.setWorkflowName(workflowTransition.getWorkflowName());
        queueResponse.setTransitionId(workflowTransition.getTransitionId());
        queueResponse.setRequestId(workflowTransition.getRequestId());
        queueResponse.setCreatedBy(workflowTransition.getCreatedBy());
        //  queueResponse.setCreatedRole(workflowTransition.getCreatedRole());
        queueResponse.setModifiedBy(workflowTransition.getModifiedBy());
        // queueResponse.setModifiedRole(workflowTransition.getModifiedRole());
        queueResponse.setStatus(workflowTransition.getStatus());
        queueResponse.setNextAction(workflowTransition.getNextAction());
        queueResponse.setAction(workflowTransition.getAction());
        queueResponse.setRemarks(workflowTransition.getRemarks());
        //    queueResponse.setNextActionId(workflowTransition.getNextActionId());
        //   queueResponse.setNextActionRole(workflowTransition.getNextActionRole());
        queueResponse.setTransitionOrder(workflowTransition.getTransitionOrder());
        queueResponse.setTransitionSubOrder(workflowTransition.getTransitionSubOrder());
        queueResponse.setCurrentRole(workflowTransition.getCurrentRole());
        queueResponse.setNextRole(workflowTransition.getNextRole());
        queueResponse.setWorkflowSequence(workflowTransition.getWorkflowSequence());
        queueResponse.setModificationDate(workflowTransition.getModificationDate());
        queueResponse.setCreatedDate(workflowTransition.getCreatedDate());

        // Reporting Officer assignment fields
        queueResponse.setAssignedToUserId(workflowTransition.getAssignedToUserId());
        if (workflowTransition.getAssignedToEmployeeId() != null) {
            employeeDepartmentMasterRepository.findByEmployeeId(workflowTransition.getAssignedToEmployeeId())
                    .ifPresent(emp -> queueResponse.setAssignedToEmployeeName(emp.getEmployeeName()));
        }

        String requestId = workflowTransition.getRequestId();

        if (requestId.startsWith("IND")) {
            // Fetch data from IndentCreation entity
            String indentId = requestId;
            //  IndentCreationResponseDTO indentCreations = indentCreationService.getIndentById(indentId);
            IndentCreation indentCreation = indentCreationRepository.getByIndentId(indentId);
            List<MaterialDetails> mdList = materialDetailsRepo.findByIndentCreation_IndentId(indentId);

            if (indentCreation != null) {
                queueResponse.setIndentorName(indentCreation.getIndentorName());
                queueResponse.setProjectName(indentCreation.getProjectName());
                queueResponse.setAmount(indentCreation.getTotalIntentValue());
                //  queueResponse.setBudgetName();
                //   queueResponse.setIndentTitle("NUll");
                if (!mdList.isEmpty()) {
                    MaterialDetails m = mdList.get(0);
                    queueResponse.setModeOfProcurement(m.getModeOfProcurement());
                    queueResponse.setBudgetName(m.getBudgetCode());
                }

                queueResponse.setConsignee(indentCreation.getConsignesLocation());
            }
        } else if (requestId.startsWith("T")) {
            String tenderId = requestId;
            TenderRequest tenderRequest = tenderRequestRepository.findById(tenderId).orElse(null);

            // Fetch data from TenderRequest entity
            //  TenderWithIndentResponseDTO tenderRequest = tenderRequestService.getTenderRequestById(tenderId);
            //  TenderRequest tenderRequest = tenderRequestRepository.getByTenderId(tenderId);
            if (tenderRequest != null) {
                //  TenderRequest tenderRequest = tenderRequestOptional.get();
                //  queueResponse.setIndentorName("NUll");
                queueResponse.setProjectName(tenderRequest.getProjectName());
                queueResponse.setAmount(tenderRequest.getTotalTenderValue());
                //   queueResponse.setBudgetName("NUll");
                queueResponse.setIndentTitle(tenderRequest.getTitleOfTender());
                queueResponse.setModeOfProcurement(tenderRequest.getModeOfProcurement());
                queueResponse.setConsignee(tenderRequest.getConsignes());

                /*  List<IndentCreationResponseDTO> indentList = tenderRequest.getIndentResponseDTO();
                if (indentList != null && !indentList.isEmpty()) {
                    IndentCreationResponseDTO firstIndent = indentList.get(0); // Assuming first indent is needed
                    queueResponse.setIndentorName(firstIndent.getIndentorName());
                    queueResponse.setProjectName(firstIndent.getProjectName());
                }

                 */
            }
        } else if (requestId.startsWith("CP")) {
            // Fetch data from CP table
            String contigencyId = requestId;
            //  ContigencyPurchaseResponseDto cpTable = contigencyPurchaseService.getContigencyPurchaseById(contigencyId);
            ContigencyPurchase cp = contigencyPurchaseRepository.findById(contigencyId).orElse(null);

            if (cp != null) {
                // ContigencyPurchase cp = cpTable.get();
                //  queueResponse.setIndentorName("Null");
                queueResponse.setProjectName(cp.getProjectName());
                queueResponse.setAmount(cp.getTotalCpValue());
                //  queueResponse.setAmount(cp.getAmountToBePaid());
                //  queueResponse.setBudgetName("Null");
                //  queueResponse.setIndentTitle("NUll");
                //queueResponse.setModeOfProcurement("NULL");
                //  queueResponse.setConsignee("Null");
            }
        } else if (requestId.startsWith("PO")) {
            String poId = requestId;
            // poWithTenderAndIndentResponseDTO po = purchaseOrderService.getPurchaseOrderById(poId);
            PurchaseOrder po = purchaseOrderRepository.findById(poId).orElse(null);

            if (po != null) {
                String mode = tenderRequestRepository.findModeOfProcurementByTenderId(po.getTenderId());

                //   queueResponse.setIndentorName("Null");
                queueResponse.setProjectName(po.getProjectName());
                queueResponse.setAmount(po.getTotalValueOfPo());
                queueResponse.setModeOfProcurement(mode);
                //     queueResponse.setBudgetName();
                //      queueResponse.setIndentTitle();
                //   queueResponse.setModeOfProcurement();
                queueResponse.setConsignee(po.getConsignesAddress());
                //  TenderWithIndentResponseDTO tenderDetails= po.getTenderDetails();
                //     queueResponse.setIndentTitle(tenderDetails.getTitleOfTender());
                //  queueResponse.setModeOfProcurement(tenderDetails.getModeOfProcurement());
            }
        } else if (requestId.startsWith("SO")) {
            String soId = requestId;
            //  soWithTenderAndIndentResponseDTO so = serviceOrderService.getServiceOrderById(soId);
            ServiceOrder so = serviceOrderRepository.findById(soId)
                    .orElse(null);
            if (so != null) {
                //  ServiceOrder so = SO.get();
                //   queueResponse.setIndentorName("Null");
                queueResponse.setProjectName(so.getProjectName());
                queueResponse.setAmount(so.getTotalValueOfSo());
                //     queueResponse.setBudgetName();
                //      queueResponse.setIndentTitle();
                //   queueResponse.setModeOfProcurement();
                queueResponse.setConsignee(so.getConsignesAddress());
                //  TenderWithIndentResponseDTO tenderDetails= so.getTenderDetails();
                //  queueResponse.setIndentTitle(tenderDetails.getTitleOfTender());
                //  queueResponse.setModeOfProcurement(tenderDetails.getModeOfProcurement());
            }

        } else if (workflowTransition.getWorkflowId() == 10) {
            String pvRequestId = workflowTransition.getRequestId();  // "INV/1153/156/15"
            String[] parts = pvRequestId.split("/");

            Long id = Long.parseLong(parts[parts.length - 1]);

            Optional<PaymentVoucher> pv = paymentVoucherReposiotry.findById(id);

            if (pv.isPresent()) {
                PaymentVoucher p = pv.get();
                queueResponse.setPaymentType(p.getPaymentVoucherType());
                if (p.getPaymentVoucherType().equalsIgnoreCase("partial")) {
                    queueResponse.setAmount(p.getPaymentVoucherNetAmount());
                } else if (p.getPaymentVoucherType().equalsIgnoreCase("Full Payment")) {
                    queueResponse.setAmount(p.getPaymentVoucherNetAmount());
                } else {
                    queueResponse.setAmount(p.getPaymentVoucherNetAmount());
                }
                queueResponse.setPoNo(p.getPurchaseOrderId());
                queueResponse.setVendorName(p.getVendorName());
            }

        }
        return queueResponse;

    }

    /*


    @Override
    public List<SubWorkflowQueueDto> getSubWorkflowQueue(Integer modifiedBy) {
        List<SubWorkflowQueueDto> workflowQueueDtoList = new ArrayList<>();
        Set<String> processedIndentIds = new HashSet<>(); // To track already added indentIds
        String previousRequestId = null; // Track last requestId
        List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByActionOn(modifiedBy);

        if (Objects.nonNull(subWorkflowTransitionList) && !subWorkflowTransitionList.isEmpty()) {
            for (SubWorkflowTransition transition : subWorkflowTransitionList) {
                String tenderId = transition.getRequestId();
                // Reset processedIndentIds if requestId has changed
                if (!tenderId.equals(previousRequestId)) {
                    processedIndentIds.clear();
                    previousRequestId = tenderId; // Update to new requestId
                }
                // Fetch indentIds for the current tender (requestId)
                List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);

                // Fetch indent details using the indentIds
                List<IndentCreation> indentList = indentCreationRepository.findByIndentIdIn(indentIds);

                for (IndentCreation indent : indentList) {
                    if (processedIndentIds.contains(indent.getIndentId())) {
                        continue; // Skip if already added
                    }

                    SubWorkflowQueueDto subWorkflowQueueDto = new SubWorkflowQueueDto();
                    subWorkflowQueueDto.setSubWorkflowTransitionId(transition.getSubWorkflowTransitionId());
                    subWorkflowQueueDto.setWorkflowId(transition.getWorkflowId());
                    subWorkflowQueueDto.setWorkflowName(transition.getWorkflowName());
                    subWorkflowQueueDto.setModifiedBy(transition.getModifiedBy());
                    subWorkflowQueueDto.setWorkflowSequence(transition.getWorkflowSequence());
                    subWorkflowQueueDto.setStatus(transition.getStatus());
                    subWorkflowQueueDto.setRemarks(transition.getRemarks());
                    subWorkflowQueueDto.setAction(transition.getAction());
                    subWorkflowQueueDto.setActionOn(transition.getActionOn());
                    subWorkflowQueueDto.setRequestId(transition.getRequestId());
                    subWorkflowQueueDto.setCreatedBy(transition.getCreatedBy());
                    subWorkflowQueueDto.setCreatedDate(transition.getCreatedDate());
                    subWorkflowQueueDto.setModificationDate(transition.getModificationDate());

                    // Assign corresponding indent to the tender
                    subWorkflowQueueDto.setIndentId(indent.getIndentId());
                    subWorkflowQueueDto.setIndentorName(indent.getIndentorName());
                    subWorkflowQueueDto.setProjectName(indent.getProjectName());
                    subWorkflowQueueDto.setAmount(indent.getTotalIntentValue());
                    subWorkflowQueueDto.setConsignee(indent.getConsignesLocation());

                    workflowQueueDtoList.add(subWorkflowQueueDto);
                    processedIndentIds.add(indent.getIndentId()); // Mark indentId as processed
                }
            }
        }
        return workflowQueueDtoList;
    }

     */
 /*
    @Override
    public List<SubWorkflowQueueDto> getSubWorkflowQueue(Integer modifiedBy) {
        List<SubWorkflowQueueDto> workflowQueueDtoList = new ArrayList<>();


        List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByActionOn(modifiedBy);

        if (subWorkflowTransitionList != null && !subWorkflowTransitionList.isEmpty()) {

            Map<String, List<SubWorkflowTransition>> transitionsByRequestId = subWorkflowTransitionList.stream()
                    .collect(Collectors.groupingBy(SubWorkflowTransition::getRequestId));

            for (Map.Entry<String, List<SubWorkflowTransition>> entry : transitionsByRequestId.entrySet()) {
                String tenderId = entry.getKey(); // Request ID
                List<SubWorkflowTransition> transitions = entry.getValue(); // Transitions for the request ID

                List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
                if (indentIds.isEmpty()) continue;

                // Fetch indent details using the indentIds
                List<IndentCreation> indentList = indentCreationRepository.findByIndentIdIn(indentIds);
                if (indentList.isEmpty()) continue;

                // Distribute indent records across transitions
                int transitionIndex = 0;
                for (IndentCreation indent : indentList) {
                    SubWorkflowTransition transition = transitions.get(transitionIndex);

                    SubWorkflowQueueDto subWorkflowQueueDto = new SubWorkflowQueueDto();
                    subWorkflowQueueDto.setSubWorkflowTransitionId(transition.getSubWorkflowTransitionId());
                    subWorkflowQueueDto.setWorkflowId(transition.getWorkflowId());
                    subWorkflowQueueDto.setWorkflowName(transition.getWorkflowName());
                    subWorkflowQueueDto.setModifiedBy(transition.getModifiedBy());
                    subWorkflowQueueDto.setWorkflowSequence(transition.getWorkflowSequence());
                    subWorkflowQueueDto.setStatus(transition.getStatus());
                    subWorkflowQueueDto.setRemarks(transition.getRemarks());
                    subWorkflowQueueDto.setAction(transition.getAction());
                    subWorkflowQueueDto.setActionOn(transition.getActionOn());
                    subWorkflowQueueDto.setRequestId(transition.getRequestId());
                    subWorkflowQueueDto.setCreatedBy(transition.getCreatedBy());
                    subWorkflowQueueDto.setCreatedDate(transition.getCreatedDate());
                    subWorkflowQueueDto.setModificationDate(transition.getModificationDate());

                    // Assign corresponding indent to the transition
                    subWorkflowQueueDto.setIndentId(indent.getIndentId());
                    subWorkflowQueueDto.setIndentorName(indent.getIndentorName());
                    subWorkflowQueueDto.setProjectName(indent.getProjectName());
                    subWorkflowQueueDto.setAmount(indent.getTotalIntentValue());
                    subWorkflowQueueDto.setConsignee(indent.getConsignesLocation());

                    workflowQueueDtoList.add(subWorkflowQueueDto);

                    transitionIndex = (transitionIndex + 1) % transitions.size();
                }
            }
        }
        return workflowQueueDtoList;
    }

     */
    @Override
    public List<SubWorkflowQueueDto> getSubWorkflowQueue(Integer modifiedBy) {
        List<SubWorkflowQueueDto> workflowQueueDtoList = new ArrayList<>();

        List<SubWorkflowTransition> subWorkflowTransitionList = subWorkflowTransitionRepository.findByActionOn(modifiedBy);

        if (subWorkflowTransitionList != null && !subWorkflowTransitionList.isEmpty()) {

            Map<String, List<SubWorkflowTransition>> transitionsByRequestId = subWorkflowTransitionList.stream()
                    .collect(Collectors.groupingBy(SubWorkflowTransition::getRequestId));

            for (Map.Entry<String, List<SubWorkflowTransition>> entry : transitionsByRequestId.entrySet()) {
                String tenderId = entry.getKey(); // Request ID
                List<SubWorkflowTransition> transitions = entry.getValue(); // Transitions for the request ID

                List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
                if (indentIds.isEmpty()) {
                    continue;
                }

                // Fetch indent details using the indentIds
                List<IndentCreation> indentList = indentCreationRepository.findByIndentIdIn(indentIds);
                if (indentList.isEmpty()) {
                    continue;
                }

// Double the size of indentList dynamically
                List<IndentCreation> extendedIndentList = new ArrayList<>(indentList);
                extendedIndentList.addAll(indentList); // Duplicate the list

                int transitionIndex = 0;
                for (SubWorkflowTransition transition : transitions) {
                    // Assign indent details (cycling through the extended list)
                    IndentCreation indent = extendedIndentList.get(transitionIndex % extendedIndentList.size());

                    SubWorkflowQueueDto subWorkflowQueueDto = new SubWorkflowQueueDto();
                    subWorkflowQueueDto.setSubWorkflowTransitionId(transition.getSubWorkflowTransitionId());
                    subWorkflowQueueDto.setWorkflowId(transition.getWorkflowId());
                    subWorkflowQueueDto.setWorkflowName(transition.getWorkflowName());
                    subWorkflowQueueDto.setModifiedBy(transition.getModifiedBy());
                    subWorkflowQueueDto.setWorkflowSequence(transition.getWorkflowSequence());
                    subWorkflowQueueDto.setStatus(transition.getStatus());
                    subWorkflowQueueDto.setRemarks(transition.getRemarks());
                    subWorkflowQueueDto.setAction(transition.getAction());
                    subWorkflowQueueDto.setActionOn(transition.getActionOn());
                    subWorkflowQueueDto.setRequestId(transition.getRequestId());
                    subWorkflowQueueDto.setCreatedBy(transition.getCreatedBy());
                    subWorkflowQueueDto.setCreatedDate(transition.getCreatedDate());
                    subWorkflowQueueDto.setModificationDate(transition.getModificationDate());

                    // Assign indent details
                    subWorkflowQueueDto.setIndentId(indent.getIndentId());
                    subWorkflowQueueDto.setIndentorName(indent.getIndentorName());
                    subWorkflowQueueDto.setProjectName(indent.getProjectName());
                    subWorkflowQueueDto.setAmount(indent.getTotalIntentValue());
                    subWorkflowQueueDto.setConsignee(indent.getConsignesLocation());

                    workflowQueueDtoList.add(subWorkflowQueueDto);

                    transitionIndex++;
                }

            }
        }
        return workflowQueueDtoList;
    }

    @Override
    public List<WorkflowTransitionDto> performAllTransitionAction(List<TransitionActionReqDto> transitionActionReqDto) {

        List<WorkflowTransitionDto> workflow = new ArrayList<>();

        for (TransitionActionReqDto action : transitionActionReqDto) {
            WorkflowTransitionDto dto = performTransitionAction(action);
            workflow.add(dto);
        }

        return workflow;
    }

    @Override
    public List<pendingRecordsDto> getPendingRecordsForRole(String roleName) {
        return workflowTransitionRepository.findPendingByNextRole(roleName);
    }

}