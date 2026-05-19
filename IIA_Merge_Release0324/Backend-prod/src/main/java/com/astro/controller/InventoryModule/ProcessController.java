package com.astro.controller.InventoryModule;

import com.astro.dto.workflow.InventoryModule.*;
import com.astro.dto.workflow.InventoryModule.GiDto.GiApprovalDto;
import com.astro.dto.workflow.InventoryModule.GiDto.GiWorkflowStatusDto;
import com.astro.dto.workflow.InventoryModule.GiDto.SaveGiDto;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtIdDto;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtMasterDto;
import com.astro.dto.workflow.InventoryModule.GprnDto.SaveGprnDto;
import com.astro.dto.workflow.InventoryModule.asset.AssetOhqDisposalDto;
import com.astro.dto.workflow.InventoryModule.gprn.GprnPendingInspectionDto;
import com.astro.dto.workflow.InventoryModule.grn.GrnDto;
import com.astro.dto.workflow.InventoryModule.grn.GrnMaterialMasterDto;
import com.astro.dto.workflow.InventoryModule.grn.UpdateGrnDto;
import com.astro.dto.workflow.InventoryModule.grv.GrvDto;
import com.astro.dto.workflow.InventoryModule.igp.IgpCombinedDetailDto;
import com.astro.dto.workflow.InventoryModule.igp.IgpDetailReportDto;
import com.astro.dto.workflow.InventoryModule.igp.IgpDto;
import com.astro.dto.workflow.InventoryModule.igp.IgpIdDto;
import com.astro.dto.workflow.InventoryModule.igp.IgpReportDto;
import com.astro.dto.workflow.InventoryModule.isn.IsnDto;
import com.astro.dto.workflow.InventoryModule.ogp.GprApprovalDto;
import com.astro.dto.workflow.InventoryModule.ogp.MaterialIgpDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpIdDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpMasterRejectedGiDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoResponseDto;
import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.dto.workflow.paymentVoucherRequestDto;
import com.astro.entity.InventoryModule.*;
import com.astro.entity.PaymentVoucher;
import com.astro.service.InventoryModule.*;
import com.astro.service.PaymentVoucherService;
import com.astro.service.ProcessService;
import com.astro.service.WorkflowService;
import com.astro.service.impl.InventoryModule.GiServiceImpl;
import com.astro.util.ResponseBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/process-controller")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @Autowired
    private GiService gis;

    @Autowired
    private IgpService igpService;
    @Autowired
    private GrnService grns;

    @Autowired
    private AssetMasterService assetMasterService;

    @Autowired
    private GtService gtService;

    @Autowired
    private OgpService ogpService;
    @Autowired
    private DiService diService;
    @Autowired
    private ogpAssetService ogpAssetDisposalService;
    @Autowired
    private PaymentVoucherService paymentVoucherService;
    @Autowired
    private WorkflowService workflowService;

    @PostMapping("/saveGprn")
    public ResponseEntity<Object> saveGprn(@RequestBody SaveGprnDto req) {
        String processNo = processService.saveGprn(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/saveGi")
    public ResponseEntity<Object> saveGi(@RequestBody SaveGiDto req) {
        String processNo = processService.saveGi(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getSubProcessDtls")
    public ResponseEntity<Object> getSubProcessDtls(@RequestParam String processStage, @RequestParam String processNo ) {
       Object created = processService.getSubProcessDtls(processStage, processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(created), HttpStatus.OK);
    }

    @PostMapping("/saveGrv")
    public ResponseEntity<Object> saveGrv(@RequestBody GrvDto req) {
        String processNo = processService.saveGrv(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/saveIgp")
    public ResponseEntity<Object> saveGrn(@RequestBody IgpDto req) {
        String processNo = processService.saveIgp(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @PostMapping("/saveOgp")
    public ResponseEntity<Object> saveGrn(@RequestBody OgpDto req) {
        String processNo = processService.saveOgp(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @PostMapping("/saveIsn")
    public ResponseEntity<Object> saveIsn(@RequestBody IsnDto req) {
        String processNo = processService.saveIsn(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/saveGrn")
    public ResponseEntity<Object> saveGrn(@RequestBody GrnDto req) {
        String processNo = processService.saveGrn(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/savePoOgp")
    public ResponseEntity<Object> savePoOgp(@RequestBody OgpPoDto req) {
        String processNo = processService.savePoOgp(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getIsnAssetOhqDtls")
    public ResponseEntity<Object> getIsnAssetOhqDtls() {
        List<IsnAssetOhqDtlsDto> res = processService.getIsnAssetOhqDtls();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    
    @GetMapping("/getGiStatusWise")
    public ResponseEntity<Object> getGiStatusWise(@RequestParam String status, @RequestParam Optional<String> createdBy) {
        List<GprnPendingInspectionDto> res = gis.getGiStatusWise(status, createdBy);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }


    @GetMapping("/getPendingGprn")
    public ResponseEntity<Object> getPendingGprn() {
        List<String> pendingGprnList = processService.getPendingGprn();
        Map<String, List<String>> res = new HashMap<>();
        res.put("pendingGprnList", pendingGprnList);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getPendingAllPoDataForGprn")
    public ResponseEntity<Object> getPendingPoIdsGprn() {
        List<PendingGprnPoDto> pendingGprnList = processService.getPendingGprnDetails();
        Map<String,  List<PendingGprnPoDto>> res = new HashMap<>();
        res.put("pendingGprnList", pendingGprnList);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getPoOgp")
    public ResponseEntity<Object> getPoOgp(@RequestParam String processNo) {
        OgpPoResponseDto res = processService.getPoOgp(processNo);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getGatePassReport")
    public ResponseEntity<Object> getGatePassReport() {
        List<IgpCombinedDetailDto> res = igpService.getIgpDetails();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    
    @PostMapping("/approveOgp")
    public ResponseEntity<Object> approveOgp(@RequestBody GprApprovalDto req) {
        processService.approveOgp(req);
        Map<String, String> res = new HashMap<>();
        res.put("message", "OGP approved successfully");
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/rejectOgp")
    public ResponseEntity<Object> rejectOgp(@RequestBody GprApprovalDto req) {
        processService.rejectOgp(req);
        Map<String, String> res = new HashMap<>();
        res.put("message", "OGP rejected successfully");
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/approveGprn")
    public ResponseEntity<Object> approveGi(@RequestBody GprApprovalDto req) {
        processService.approveGprn(req.getProcessNo());
        Map<String, String> res = new HashMap<>();
        res.put("message", "GPRN approved successfully");
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @PostMapping("/changeReqGprn")
    public ResponseEntity<Object> changeReqGprn(@RequestBody GprApprovalDto req) {
        processService.changeReqGprn(req.getProcessNo());
        Map<String, String> res = new HashMap<>();
        res.put("message", "GPRN change request successful.");
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/rejectGprn")
    public ResponseEntity<Object> rejectGi(@RequestBody GprApprovalDto req) {
        processService.rejectGprn(req.getProcessNo());
        Map<String, String> res = new HashMap<>();
        res.put("message", "GPRN rejected successfully");
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/updateGprn")
    public ResponseEntity<Object> updateGprn(@RequestBody SaveGprnDto updateRequest) {
        processService.updateGprn(updateRequest);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("GPRN updated successfully"), HttpStatus.OK);
    }

/*
    @PostMapping("/approveGi")
    public ResponseEntity<Object> approveGiId(@RequestBody GprApprovalDto req) {
        gis.approveGi(req.getProcessNo());
        Map<String, String> res = new HashMap<>();
        res.put("message", "GI approved successfully");
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/changeReqGi")
    public ResponseEntity<Object> changeReqGi(@RequestBody GprApprovalDto req) {
        gis.changeReqGi(req.getProcessNo());
        Map<String, String> res = new HashMap<>();
        res.put("message", "GI change request successful.");
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/rejectGi")
    public ResponseEntity<Object> rejectGiProcessId(@RequestBody GprApprovalDto req) {
        gis.rejectGi(req.getProcessNo());
        Map<String, String> res = new HashMap<>();
        res.put("message", "GI rejected successfully");
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }*/
    @PostMapping("/approveGi")
    public ResponseEntity<Object> approveGiId(@RequestBody GiApprovalDto req) {
        gis.approveGi(req);
        Map<String, String> res = new HashMap<>();
        res.put("message", "GI approved successfully");
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(res));
    }

    @PostMapping("/changeReqGi")
    public ResponseEntity<Object> changeReqGi(@RequestBody GiApprovalDto req) {
        gis.changeReqGi(req);
        Map<String, String> res = new HashMap<>();
        res.put("message", "GI change request successful.");
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(res));
    }

    @PostMapping("/rejectGi")
    public ResponseEntity<Object> rejectGiProcessId(@RequestBody GiApprovalDto req) {
        gis.rejectGi(req);
        Map<String, String> res = new HashMap<>();
        res.put("message", "GI rejected successfully");
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(res));
    }

    @GetMapping("/getGiByStatuses")
    public ResponseEntity<Object> getGiByStatuses() {
        List<GiMasterEntity> res = gis.getGiByStatuses();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getGiByIndentorStatuses")
    public ResponseEntity<Object> getGiByIndentorStatuses() {
        List<GiMasterEntity> res = gis.getGiByIndentorStatuses();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/updateGi")
    public ResponseEntity<Object> updateGi(@RequestBody SaveGiDto updateRequest) {
        gis.updateGi(updateRequest);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("GI updated successfully"), HttpStatus.OK);
    }

    @GetMapping("/giHistory")
    public ResponseEntity<Object> getGiHistory(
            @RequestParam String processId,
            @RequestParam Integer subProcessId
    ) {
      List<GiWorkflowStatusDto> gi = gis.getGiHistoryByProcessId(processId, subProcessId);
      return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(gi), HttpStatus.OK);
    }


    @PostMapping("/approveGrn")
    public ResponseEntity<Object> approveGrnId(@RequestBody GiApprovalDto req) {
        grns.approveGrn(req);
        Map<String, String> res = new HashMap<>();
        res.put("message", "GRN approved successfully");
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(res));
    }

    @PostMapping("/changeReqGrn")
    public ResponseEntity<Object> changeReqGrn(@RequestBody GiApprovalDto req) {
        grns.changeReqGrn(req);
        Map<String, String> res = new HashMap<>();
        res.put("message", "GRN change request successful.");
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(res));
    }

    @PostMapping("/rejectGrn")
    public ResponseEntity<Object> rejectGrnProcessId(@RequestBody GiApprovalDto req) {
        grns.rejectGrn(req);
        Map<String, String> res = new HashMap<>();
        res.put("message", "GRN rejected successfully");
        return ResponseEntity.ok(ResponseBuilder.getSuccessResponse(res));
    }

    @GetMapping("/getGrnByStatuses")
    public ResponseEntity<Object> getGrnByStatuses() {
        List<GrnMasterEntity> res = grns.getGrnByStatuses();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getGrnByStorePersonStatuses")
    public ResponseEntity<Object> getGrnByStorepersonStatuses() {
        List<GrnMasterEntity> res = grns.getGrnByStorePresonStatuses();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/updateGrn")
    public ResponseEntity<Object> updateGrn(@RequestBody GrnDto updateRequest) {
        grns.updateGrn(updateRequest);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Grn updated successfully"), HttpStatus.OK);
    }

    @GetMapping("/grnHistory")
    public ResponseEntity<Object> getGrnHistory(
            @RequestParam String processId,
            @RequestParam Integer subProcessId
    ) {
        List<GiWorkflowStatusDto> gi = grns.getGrnHistoryByProcessId(processId, subProcessId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(gi), HttpStatus.OK);
    }

    @PostMapping("/saveOgpRejectedGi")
    public ResponseEntity<Object> saveOgpRejectedGi(@RequestBody OgpMasterRejectedGiDto req) {
        
        String id = processService.saveOgpRejectedGi(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", id);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getAwaitingRejectedGi")
    public ResponseEntity<Object> getAwaitingRejectedGi() {
        List<OgpMasterRejectedGiDto> res = processService.getAwaitingRejectedGi();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/approveGiOgp")
    public ResponseEntity<Object> approveGiOgp(@RequestBody OgpIdDto req) {
        processService.approveGiOgp(req.getOgpId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    
    @PostMapping("/rejectGiOgp")
    public ResponseEntity<Object> rejectGiOgp(@RequestBody OgpIdDto req) {
        processService.rejectGiOgp(req.getOgpId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    

    @PostMapping("/saveMaterialIgp")
    public ResponseEntity<Object> saveMaterialIgp(@RequestBody MaterialIgpDto req) {
        //TODO: process POST request
        String id = igpService.saveMaterialIgp(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", id);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/approveMaterialIgp")
    public ResponseEntity<Object> approveMaterialIgp(@RequestBody IgpIdDto req) {
        igpService.approveMaterialIgp(req.getIgpId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    

    @PostMapping("/rejectMaterialIgp")
    public ResponseEntity<Object> rejectMaterialIgp(@RequestBody IgpIdDto req) {
        igpService.rejectMaterialIgp(req.getIgpId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }

    @GetMapping("/getIgpMaterialDtls")
    public ResponseEntity<Object> getIgpMaterialDtls(@RequestParam String igpId) {
        MaterialIgpDto res = igpService.getIgpMaterialDtls(igpId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/saveMaterialGrn")
    public ResponseEntity<Object> saveMaterialGrn(@RequestBody GrnMaterialMasterDto req) {
        String processNo = grns.saveMaterialGrn(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", processNo);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getPendingIgp")
    public ResponseEntity<Object> getAwaitingApprovalIgp() {
        List<MaterialIgpDto> res = igpService.getAwaitingApprovalIgp();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    // @GetMapping("/getMaterialOhq")
    // public ResponseEntity<Object> getAwaitingApprovalGrn() {
    //     List<GrnMaterialMasterDto> res = .getMaterialOhq();
    //     return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    // }

    @GetMapping("/getAssetOhq")
    public ResponseEntity<Object> getAssetOhq() {
        List<OhqMasterEntity> res = assetMasterService.getAssetOhqList();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getAssetOhqDetails")
    public ResponseEntity<Object> getAssetOhqDetails() {
        List<AssetOhqDetailsDto> res = assetMasterService.getAssetOhqDetails();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getAssetOhqForDisposal")
    public ResponseEntity<Object> getAssetOhqForDisposal() {
        List<AssetOhqDisposalDto> res = assetMasterService.getAllAssetsForDisposal();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getAssetOhqConsumable")
    public ResponseEntity<Object> getAssetOhqConsumable() {
        List<OhqMasterConsumableEntity> res = assetMasterService.getAssetOhqConsumableList();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/createGt")
    public ResponseEntity<Object> createGt(@RequestBody GtMasterDto req) {
        //TODO: process POST request
        String id = gtService.createGt(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", id);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getPendingGt")
    public ResponseEntity<Object> getPendingGt() {
        List<GtMasterDto> res = gtService.getPendingGt();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getRecevierPendingGt")
    public ResponseEntity<Object> getRecevierPendingGt() {
        List<GtMasterDto> res = gtService.getRecevierPendingGt();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/SearchById")
    public ResponseEntity<Object> getGtById(@RequestParam String gtId) {
        GtMasterResponseDto res = gtService.getGtById(gtId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/approveGt")
    public ResponseEntity<Object> approveGt(@RequestBody GtIdDto req) {
        gtService.approveGt(req.getGtId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    @PostMapping("/receiverApproveGt")
    public ResponseEntity<Object> receiverApproveGt(@RequestBody GtIdDto req) {
        gtService.receiverApproveGt(req.getGtId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    @PostMapping("/rejectGt")
    public ResponseEntity<Object> rejectGt(@RequestBody GtIdDto req) {
        gtService.rejectGt(req.getGtId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }

    @PostMapping("/saveGtOgp")
    public ResponseEntity<Object> saveGtOgp(@RequestBody GtMasterDto req) {
        String id = ogpService.saveGtOgp(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", id);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    
    @GetMapping("/getPendingGtOgp")
    public ResponseEntity<Object> getPendingGtOgp() {
        List<GtMasterDto> res = ogpService.getPendingGtOgp();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getRecevierPendingGtOgp")
    public ResponseEntity<Object> getRecevierPendingGtOgp(@RequestParam("userId") Integer userId) {
        List<GtMasterDto> res = ogpService.getReciverPendingGtOgp(userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }


    @PostMapping("/approveGtOgp")
    public ResponseEntity<Object> approveGtOgp(@RequestBody OgpIdDto req) {
        ogpService.approveGtOgp(req.getOgpId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    @PostMapping("/approveReciverGtOgp")
    public ResponseEntity<Object> approveReciverGtOgp(@RequestBody OgpIdDto req) {
        ogpService.approveReceiverGtOgp(req.getOgpId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }

    @PostMapping("/rejectGtOgp")
    public ResponseEntity<Object> rejectGtOgp(@RequestBody OgpIdDto req) {
        ogpService.rejectGtOgp(req.getOgpId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }

    @PostMapping("/createDi")
    public ResponseEntity<Object> createDi(@RequestBody DiMasterDto req) {
        //TODO: process POST request
        String id = diService.createDi(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", id);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getPendingDi")
    public ResponseEntity<Object> getPendingDi() {
        List<DiMasterDto> res = diService.getPendingDi();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getPendingIssueNote")
    public ResponseEntity<Object> getPendingIssueNote() {
        List<DiMasterDto> res = diService.getPendingIssueNote();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @PostMapping("/approveDi")
    public ResponseEntity<Object> approveDi(@RequestParam String diId) {
        diService.approveDi(diId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    @PostMapping("/rejectDi")
    public ResponseEntity<Object> rejectDi(@RequestParam String diId) {
        diService.rejectDi(diId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    @GetMapping("/getStoreStockOhqConsumable")
    public ResponseEntity<Object> getStoreStockOhqConsumable() {
        List<OhqConsumableStoreStockEntity> res = assetMasterService.getStoreStockOhqConsumableList();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/SearchByDiId")
    public ResponseEntity<Object> getDiById(@RequestParam String diId) {
        DiMasterDto res = diService.getDiById(diId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @PutMapping("/issueNote")
    public ResponseEntity<Object> updateDiIssueNote(
            @RequestBody DiMasterDto diMasterDto) {

        String res = diService.updateDi(diMasterDto.getDiId(), diMasterDto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/AssetDisposalApproval")
    public ResponseEntity<Object> getAllPendingAssetDisposal() {
        List<AssetDisposalDto> res = assetMasterService.getAllAssetDisposalAwaitingForApproval();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @PostMapping("/approveAssetDisposal")
    public ResponseEntity<Object> getAssetDisposalApproval(@RequestParam String disposalId) {
        assetMasterService.approveDisposal(disposalId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }
    @PostMapping("/rejectAssetDisposal")
    public ResponseEntity<Object> getAssetDisposalReject(@RequestParam String disposalId) {
        assetMasterService.rejectDisposal(disposalId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
    }

    @GetMapping("/SearchByDisposalId")
    public ResponseEntity<Object> getDisposalById(@RequestParam String disposalId) {
        AssetDisposalDto res = assetMasterService.getAssetDisposalById(disposalId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PutMapping("/updateAssetDisposal")
    public ResponseEntity<Object> updateAssetDisposal(
            @RequestBody AssetDisposalDto assetDisposalDtoMasterDto) {

        String res = assetMasterService.updateAssetDisposal(assetDisposalDtoMasterDto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/saveAssetDisposal")
    public ResponseEntity<Object> saveAssetDisposalOgp(@RequestBody AssetsAuctionDto req) {
        String id = ogpAssetDisposalService.saveAssetDisposalOgp(req);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", id);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/pendingOgpAssetDisposal")
    public ResponseEntity<Object> getAllPendingassetDisposal() {
        List<AssetsAuctionDto> res = ogpAssetDisposalService.getPendingApprovals();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @PostMapping("/approveOgpAssetDisposal")
    public ResponseEntity<Object> getOgpAssetDisposalApproval(@RequestParam Integer disposalOgpId) {
      String res=  ogpAssetDisposalService.approveOgpAssetDisposal(disposalOgpId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @PostMapping("/rejectOgpAssetDisposal")
    public ResponseEntity<Object> getOgpAssetDisposalReject(@RequestParam Integer disposalOgpId) {
        String res=  ogpAssetDisposalService.rejectOgpAssetDisposal(disposalOgpId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PutMapping("/MultipleAssetsDisposal")
    public ResponseEntity<Object> disposeMultipleAssets(
            @RequestBody DisposeAssetRequest request) {

      String id =  assetMasterService.disposeMultipleAssets(request);
        Map<String, String> res = new HashMap<>();
        res.put("processNo", id);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/SearchByAuctionId")
    public ResponseEntity<Object> getAuctionById(@RequestParam String auctionId) {
        AssetsAuctionDto res = assetMasterService.searchByAuctionId(auctionId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/approvedGrnPoIds")
    public ResponseEntity<Object> getApprovedGrnPoIds() {
        List<PoGrnInfoDto> res=   grns.getDistinctGrnProcessIdsForGIAndApproved();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/approvedSoIds")
    public ResponseEntity<Object> getApprovedSoIds() {
        List<String> res=   grns.getApprovedSoIds();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/paymentVoucherGrnId")
    public ResponseEntity<Object> getPaymentVoucherPaymentGrnIds( @RequestParam("grnProcessId") String grnProcessId) {
        List<String> res=   grns.getGrnDetailsByProcessId(grnProcessId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/paymen")
    public ResponseEntity<Object> getPaymen( @RequestParam("processNo") String processNo) {
        Map<String, Object> res=   grns.getGrnDtls(processNo);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/paymentVoucherData")
    public ResponseEntity<Object> getPaymentVoucherDetails( @RequestParam("processNo") String processNo) {
       paymentVoucherDto res=   grns.getPaymentVoucherData(processNo);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/paymentVoucherSOData")
    public ResponseEntity<Object> getPaymentVoucherSoDetails( @RequestParam("processNo") String processNo) {
        paymentVoucherDto res=   grns.getPaymentVoucherDtoBySoId(processNo);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @PostMapping("/savePaymentVoucher")
    public ResponseEntity<Object> createPaymentVoucher(@RequestBody paymentVoucherRequestDto dto) {
      String res = paymentVoucherService.createPaymentVoucher(dto);

        String requestId = res;
        String workflowName = "Payment Voucher Workflow";
        Integer userId = dto.getCreatedBy();

        //initiateing Workflow API
        WorkflowTransitionDto workflowTransitionDto = workflowService.initiateWorkflow(requestId, workflowName, userId);
        Map<String, String> ress = new HashMap<>();
        ress.put("processNo", res);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(ress), HttpStatus.OK);
    }

    @GetMapping("/VoucherData")
    public ResponseEntity<Object> getPaymentVoucherData(@RequestParam String processNo) {
        paymentVoucherRequestDto res = paymentVoucherService.getVoucherByProcessNo(processNo);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    // @GetMapping("/pendingGprnsForGi")
    // public ResponseEntity<Object> getPendingGprnsForGI() {
    //    List<GprnDropdownDto> res = gis.getPendingGprnsForGI();
    //     return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    // }

    @GetMapping("/pendingGprnsForGi")
public ResponseEntity<Object> getPendingGprnsForGI(
        @RequestParam Integer userId,
        @RequestParam String role) {
    List<GprnDropdownDto> res = gis.getPendingGprnsForGI(userId, role);
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
}

    @GetMapping("/getPendingGrns")
    public ResponseEntity<Object> getgetPendingGrns() {
        List<GrnDropdownDto> res = grns.getPendingGrns();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }
    @GetMapping("/getPendingRejectedGis")
    public ResponseEntity<Object> getPendingRejectedGis() {
        List<GprnDropdownDto> res = gis.getPendingRejectedGis();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/pendingAuctionIds")
    public ResponseEntity<Object> getPendingAuctionIds() {
        List<Integer> res = assetMasterService.getPendingAuctionIds();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(res), HttpStatus.OK);
    }

    @GetMapping("/getPendingInterFieldGtIds")
    public ResponseEntity<Object> getPendingInterFieldGtIds() {
        List<Long> id = gtService.getPendingInterFiledGtIdsOgp();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(id), HttpStatus.OK);
    }

}
