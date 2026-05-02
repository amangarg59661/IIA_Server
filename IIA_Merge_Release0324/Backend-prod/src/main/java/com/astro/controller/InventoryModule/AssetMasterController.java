package com.astro.controller.InventoryModule;

import java.util.HashMap;
// import java.util.List;
import java.util.Map;
// import com.astro.service.InventoryModule.AssetMasterService;
import com.astro.dto.workflow.AssetDataForGtDto;
import java.util.List;
import com.astro.dto.workflow.AssetDataForGtDto;
import com.astro.dto.workflow.AssetSearchResponseDto;
import com.astro.dto.workflow.InventoryModule.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.astro.service.InventoryModule.AssetMasterService;
import com.astro.util.ResponseBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/asset")
public class AssetMasterController {

    @Autowired
    private AssetMasterService assetMasterService;

    @PostMapping("/save")
    public ResponseEntity<Object> saveAssetMaster(@RequestBody AssetMasterDto request) {
        String res = assetMasterService.saveAssetMaster(request);
        Map<String, Integer> response = new HashMap<>();
        response.put("processNo", Integer.parseInt(res));
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }
    @PostMapping("/update")
    public ResponseEntity<Object> updateAssetMaster(@RequestBody AssetMasterDto request) {
        String res = assetMasterService.updateAssetMaster(request);
        Map<String, Integer> response = new HashMap<>();
        response.put("processNo", Integer.parseInt(res));
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }
    @GetMapping("/allAssetDataForStorePerson")
public ResponseEntity<Object> getAllAssetDataForStorePerson() {
    List<AssetDataForGtDto> data = assetMasterService.getAllFullAssetsForStorePerson();
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
}

    // @PutMapping("/update")
    // public String updateAssetMaster(@RequestBody AssetMasterDto request) {
    //     return assetMasterService.updateAssetMaster(request);
    // }

    @PostMapping("/dispose")
    public ResponseEntity<Object> dispose(@RequestBody AssetDisposalDto req) {
        String res = assetMasterService.saveAssetDisposal(req);
        Map<String, String> response = new HashMap<>();
        response.put("processNo", res);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
        
        
    }
    
    @GetMapping("/getAssetDtl")
    public ResponseEntity<Object> getAssetDetails(@RequestParam("assetId") Integer assetId) {
        AssetMasterDto response = assetMasterService.getAssetDetails(assetId);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @GetMapping("/assetIds")
    public ResponseEntity<Object> getAllAssetIds() {
        List<Integer> assetIds = assetMasterService.getAllAssetIds();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(assetIds), HttpStatus.OK);
}

    @GetMapping("/search")
    public ResponseEntity<Object> searchAssets(@RequestParam String keyword) {
        List<AssetSearchResponseDto> results = assetMasterService.searchAssetsByKeyword(keyword);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(results), HttpStatus.OK);
    }

    @GetMapping("/asset-full-details")
    public ResponseEntity<Object> getFullAssetDetails(
            @RequestParam(required = false) Integer assetId,
            @RequestParam(required = false) String assetCode,
            @RequestParam(required = false) String custodianId,
            @RequestParam(required = false) Integer locatorId) {

        List<AssetFullResponseDto> results = assetMasterService.getFullAssetDetails(
                assetId, assetCode, custodianId, locatorId);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(results), HttpStatus.OK);
    }
    @PostMapping("/update-serials")
    public ResponseEntity<Object> updateAssetSerials(@RequestBody AssetSerialUpdateRequestDto request) {

      String res =      assetMasterService.updateAssetSerials(request);
      Map<String, String> response = new HashMap<>();
      response.put("assetCode", res);
      return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }
    @PostMapping("/updateRemaining-serials")
    public ResponseEntity<Object> updateRemainingAssetSerials(@RequestBody AssetSerialUpdateRequestDto request) {

        String res =      assetMasterService.addRemainingSerials(request);
        Map<String, String> response = new HashMap<>();
        response.put("assetCode", res);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @GetMapping("/assetDataForGt")
    public ResponseEntity<Object> assetDataForGt() {
        List<AssetDataForGtDto> results = assetMasterService.getAllFullAssets();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(results), HttpStatus.OK);
    }
    @GetMapping("/exstingSerialNoOnCustodainIdofAsset")
    public ResponseEntity<Object> exstingSerialNoOnCustodainId(  @RequestParam String assetCode,
     @RequestParam Integer assetId, @RequestParam String custodianId, @RequestParam Integer locatorId,@RequestParam Integer quantity) {
       SerialCheckResponseDto results = assetMasterService.checkSerials(assetCode, assetId,  custodianId,  locatorId, quantity);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(results), HttpStatus.OK);
    }

}