package com.astro.controller;

import com.astro.dto.workflow.ApprovedIndentsDto;
import com.astro.dto.workflow.ApprovedPoIdsDto;
import com.astro.dto.workflow.TransitionActionReqDto;
import com.astro.service.WorkflowService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WorkflowController {

    @Autowired
    WorkflowService workflowService;

    @GetMapping("/getWorkflowByName")
    public ResponseEntity<Object> getWorkflowByName(@RequestParam String workflowName) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.workflowByWorkflowName(workflowName)), HttpStatus.OK);

    }

    @GetMapping("/getTransitionsByWorkflowId")
    public ResponseEntity<Object> getTransitionsByWorkflowId(@RequestParam Integer workflowId) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.transitionsByWorkflowId(workflowId)), HttpStatus.OK);
    }

    @GetMapping("/getTransitionByOrder")
    public ResponseEntity<Object> getTransitionByOrder(@RequestParam Integer workflowId, @RequestParam Integer order, @RequestParam Integer subOrder) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.transitionsByWorkflowIdAndOrder(workflowId, order, subOrder)), HttpStatus.OK);
    }

    @PostMapping("/initiateWorkflow")
    public ResponseEntity<Object> initiateWorkflow(@RequestParam String requestId, @RequestParam String workflowName, @RequestParam String createdBy) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.initiateWorkflow(requestId, workflowName, createdBy)), HttpStatus.OK);
    }

    @GetMapping("/workflowTransitionHistory")
    public ResponseEntity<Object> workflowTransitionHistory(@RequestParam String requestId) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.workflowTransitionHistory(requestId)), HttpStatus.OK);
    }

    @GetMapping("/pendingWorkflowTransition")
    public ResponseEntity<Object> pendingWorkflowTransition(@RequestParam String roleName, @RequestParam(required = false) Integer userId) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.allPendingWorkflowTransition(roleName, userId)), HttpStatus.OK);
    }

    @GetMapping("/completedIndentWorkflowTransition")
    public ResponseEntity<Object> completedIndentWorkflowTransition(@RequestParam String roleName) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.allCompletedWorkflowTransition(roleName)), HttpStatus.OK);
    }

    @GetMapping("/pendingWorkflowTransitionQueue")
    public ResponseEntity<Object> pendingWorkflowTransitionQueue(@RequestParam String roleName, @RequestParam(required = false) Integer userId) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.allPendingWorkflowTransitionINQueue(roleName, userId)), HttpStatus.OK);
    }

    @GetMapping("/allPendingWorkflowTransition")
    public ResponseEntity<Object> allPendingWorkflowTransition(@RequestParam String roleName, @RequestParam(required = false) Integer userId) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.allPendingWorkflowTransition(roleName, userId)), HttpStatus.OK);
    }

    @GetMapping("/allCancledIndents")
    public ResponseEntity<Object> allCancledIndents() {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.allCancelledIndents()), HttpStatus.OK);
    }

    @GetMapping("/allPendingRecords")
    public ResponseEntity<Object> allPendingRecord(@RequestParam String roleName) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.getPendingRecordsForRole(roleName)), HttpStatus.OK);
    }

    @GetMapping("/allWorkflowTransition")
    public ResponseEntity<Object> allWorkflowTransition(@RequestParam String roleName) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.allWorkflowTransition(roleName)), HttpStatus.OK);
    }

    @GetMapping("/nextTransition")
    public ResponseEntity<Object> nextTransition(@RequestParam Integer workflowId, @RequestParam String workflowName, @RequestParam String currentRole, @RequestParam String requestId) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.nextTransition(workflowId, workflowName, currentRole, requestId)), HttpStatus.OK);
    }

    @PostMapping("/performTransitionAction")
    public ResponseEntity<Object> performTransitionAction(@RequestBody TransitionActionReqDto transitionActionReqDto) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.performTransitionAction(transitionActionReqDto)), HttpStatus.OK);
    }

    @GetMapping("/allPreviousWorkflowRole")
    public ResponseEntity<Object> allPreviousRoleWorkflowTransition(@RequestParam Integer workflowId, @RequestParam String requestId) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.allPreviousRoleWorkflowTransition(workflowId, requestId)), HttpStatus.OK);
    }

    @PostMapping("/submitWorkflow")
    public ResponseEntity<Object> submitWorkflow(@RequestParam Integer workflowTransitionId, @RequestParam Integer actionBy, @RequestParam String remarks) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.submitWorkflow(workflowTransitionId, actionBy, remarks)), HttpStatus.OK);
    }

    @GetMapping("/approvedWorkflowTransition")
    public ResponseEntity<Object> approvedWorkflowTransition(@RequestParam String updatedBy) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.approvedWorkflowTransition(updatedBy)), HttpStatus.OK);
    }

    @GetMapping("/getSubWorkflowTransition")
    public ResponseEntity<Object> getSubWorkflowTransition(@RequestParam String updatedBy) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.getSubWorkflowTransition(updatedBy)), HttpStatus.OK);
    }

    @GetMapping("/getSubWorkflowTransitionQueue")
    public ResponseEntity<Object> getSubWorkflowTransitionQueue(@RequestParam String updatedBy) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.getSubWorkflowQueue(updatedBy)), HttpStatus.OK);
    }

    @PostMapping("/approveSubWorkflow")
    public ResponseEntity<Object> approveSubWorkflow(@RequestParam Integer subWorkflowTransitionId) {
        workflowService.approveSubWorkflow(subWorkflowTransitionId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }

    // @GetMapping("/approved-indents")
    // public ResponseEntity<Object>  getApprovedIndents() {
    //     List<ApprovedIndentsDto> response = workflowService.getApprovedIndents();
    //     return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    // }
    // update by abhinav
    @GetMapping("/approved-indents")
    public ResponseEntity<Object> getApprovedIndents(@RequestParam Integer userId) {// added by abhinav
        List<ApprovedIndentsDto> response = workflowService.getApprovedIndents(userId); // added by abhinav
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    /*  @GetMapping("/approved-PoIds")
    public ResponseEntity<Object>  getApprovedPoIds() {
        List<ApprovedPoIdsDto> response= workflowService.getApprovedPoIds();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

     */
    @GetMapping("/approved-PoIds")
    public ResponseEntity<Object> getApprovedPoIds() {
        List<String> response = workflowService.getApprovedPoIds();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @GetMapping("/getApprovedTender")
    public ResponseEntity<Object> getApprovedTender() {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.getApprovedTender()), HttpStatus.OK);
    }

    @GetMapping("/getApprovedTenderId")
    public ResponseEntity<Object> getApprovedTenderId(@RequestParam String tenderId) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.getApprovedTenderId(tenderId)), HttpStatus.OK);
    }

    /* @GetMapping("/getApprovedTender")
  public ResponseEntity<Object> getApprovedTender(@RequestParam String roleName)  {
      return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.getApprovedTender(roleName)), HttpStatus.OK);
  }*/
    @GetMapping("/getApprovedTenderIdForPOAndSO")
    public ResponseEntity<Object> getApprovedTenderIDForPOAndSO() {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.getApprovedTenderIdsForPOAndSO()), HttpStatus.OK);
    }

    @PostMapping("/performAllTransitionAction")
    public ResponseEntity<Object> performAllTransitionAction(@RequestBody List<TransitionActionReqDto> transitionActionReqDto) {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(workflowService.performAllTransitionAction(transitionActionReqDto)), HttpStatus.OK);
    }

    /* @Autowired
    private UserService userService;

    @PostMapping("/userDetails")
    public ResponseEntity<Object> getUserDetails(@RequestBody LoginDto loginDto) {

            UserDetailDto userDetailDto = userService.getUserDetails(loginDto);
            return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(userDetailDto), HttpStatus.OK);

    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegistrationDto registrationDto){
        UserDetailDto userDetailDto = userService.registerUser(registrationDto);
            return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(userDetailDto), HttpStatus.OK);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<Object> updateUserDetail(@RequestBody RegistrationDto registrationDto){
        UserDetailDto userDetailDto = userService.updateUserDetails(registrationDto);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(userDetailDto), HttpStatus.OK);
    }

    @PostMapping("/deleteUser")
    public ResponseEntity<Object> deleteUser(@RequestBody UserIdDto userIdDto){
        userService.deleteUser(userIdDto);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }

    @PostMapping("/activateUser")
    public ResponseEntity<Object> activateUser(@RequestBody UserIdDto userIdDto){
        userService.activateUser(userIdDto);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }

    @PostMapping("/deactivateUser")
    public ResponseEntity<Object> deactivateUser(@RequestBody UserIdDto userIdDto){
        userService.deactivateUser(userIdDto);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }



    @PostMapping("/addContactUs")
    public ResponseEntity<Object> addContactUs(@RequestBody ContactUsDto contactUsDto){
        userService.addContactUs(contactUsDto);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse("ContactUs saved."), HttpStatus.OK);
    }

    @PostMapping("/uploadProfileImage")
    public ResponseEntity<Object> uploadProfileImage(@RequestParam MultipartFile file, @RequestParam String userId){
        userService.uploadProfileImage(file, userId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse("Profile Image saved."), HttpStatus.OK);
    }

    @GetMapping("/downloadProfileImage")
    public ResponseEntity<Resource> downloadProfileImage(@RequestParam String userId){
        Map<String, Object> res = userService.downloadProfileImage(userId);
        ByteArrayResource resource = new ByteArrayResource((byte[]) res.get("content"));

        System.out.println(res.get("fileName"));


        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + res.get("fileName"));

        return ResponseEntity.ok().contentType(MediaType
                .APPLICATION_OCTET_STREAM)
                .headers(headers).body(resource);
    }

    @PostMapping("/removeProfileImage")
    public ResponseEntity<Object> removeProfileImage(@RequestParam String userId){
        boolean res = userService.removeProfileImage(userId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<Object> updatePassword(@RequestBody LoginDto loginDto) {

        UserDetailDto userDetailDto = userService.updatePassword(loginDto);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(userDetailDto), HttpStatus.OK);

    }

    @PostMapping("/getAllUserDetails")
    public ResponseEntity<Object> getAllUserDetails() {
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(userService.getAllUserDetails()), HttpStatus.OK);

    }
     */
}
