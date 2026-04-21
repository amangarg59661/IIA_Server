package com.astro.service.impl.InventoryModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


import javax.transaction.Transactional;

import com.astro.dto.workflow.InventoryModule.GtDtlDto;
import com.astro.dto.workflow.InventoryModule.GtMasterResponseDto;
import com.astro.dto.workflow.InventoryModule.GtReportDtlDto;
import com.astro.dto.workflow.InventoryModule.withinFieldStationGtDto;
import com.astro.entity.InventoryModule.*;
import com.astro.entity.UserMaster;
import com.astro.repository.InventoryModule.AssetSerialEntityRepository;
import com.astro.repository.InventoryModule.ogp.OgpGtDtlRepository;
import com.astro.repository.InventoryModule.ogp.OgpGtMasterRepository;
import com.astro.repository.UserMasterRepository;
import com.astro.util.EmailService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtDtl;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtMasterDto;
import com.astro.entity.MaterialMaster;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.MaterialMasterRepository;
import com.astro.repository.InventoryModule.OhqMasterConsumableRepository;
import com.astro.repository.InventoryModule.GoodsTransfer.GtDtlRepository;
import com.astro.repository.InventoryModule.GoodsTransfer.GtMasterRepository;
import com.astro.repository.ohq.OhqMasterRepository;
import com.astro.service.InventoryModule.GtService;
import com.astro.util.CommonUtils;
import org.thymeleaf.context.Context;

@Service
public class GtServiceImpl implements GtService {
    @Autowired
    private GtMasterRepository gtmr;

    @Autowired
    private GtDtlRepository gtdr;

    @Autowired
    private OhqMasterConsumableRepository omcr;

    @Autowired
    private OhqMasterRepository ohqmr;
    @Autowired
    private UserMasterRepository userMasterRepository;
    @Autowired
    private OgpGtMasterRepository ogmr;
    @Autowired
    private AssetSerialEntityRepository assetSerialEntityRepository;

    @Autowired
    private OgpGtDtlRepository ogdr;
    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public String createGt(GtMasterDto gtMasterDto) {
        GtMasterEntity gtMasterEntity = new GtMasterEntity();
        gtMasterEntity.setSenderLocationId(gtMasterDto.getSenderLocationId());
        gtMasterEntity.setReceiverLocationId(gtMasterDto.getReceiverLocationId());
        gtMasterEntity.setReceiverCustodianId(gtMasterDto.getReceiverCustodianId());
        gtMasterEntity.setSenderCustodianId(gtMasterDto.getSenderCustodianId());
        gtMasterEntity.setGtDate(CommonUtils.convertStringToDateObject(gtMasterDto.getGtDate()));
        gtMasterEntity.setCreatedBy(gtMasterDto.getCreatedBy());
        gtMasterEntity.setCreateDate(LocalDateTime.now());
      //  gtMasterEntity.setStatus("AWAITING APPROVAL");
        gtMasterEntity.setStatus("PENDING RECEIVER APPROVAL");
        gtMasterEntity = gtmr.save(gtMasterEntity);

        for (GtDtl gtDtl : gtMasterDto.getMaterialDtlList()) {
            GtDtlEntity gtDtlEntity = new GtDtlEntity();
            gtDtlEntity.setGtId(gtMasterEntity.getId());
            gtDtlEntity.setAssetId(gtDtl.getAssetId());
            gtDtlEntity.setAssetCode(gtDtl.getAssetCode());
            gtDtlEntity.setAssetDesc(gtDtl.getAssetDesc());
            gtDtlEntity.setMaterialCode(gtDtl.getMaterialCode());
            gtDtlEntity.setUnitPrice(gtDtl.getUnitPrice());
            gtDtlEntity.setDepriciationRate(gtDtl.getDepriciationRate());
            gtDtlEntity.setBookValue(gtDtl.getBookValue());
            gtDtlEntity.setMaterialDesc(gtDtl.getMaterialDesc());
            gtDtlEntity.setQuantity(gtDtl.getQuantity());
            gtDtlEntity.setReceiverLocatorId(gtDtl.getReceiverLocatorId());
            gtDtlEntity.setSenderLocatorId(gtDtl.getSenderLocatorId());
            gtDtlEntity.setPoId(gtDtl.getPoId());
            gtDtlEntity.setModelNo(gtDtl.getModelNo());
            gtDtlEntity.setSerialNo(gtDtl.getSerialNo());
            gtDtlEntity.setReasonForTransfer(gtDtl.getReasonForTransfer());
            gtdr.save(gtDtlEntity);
        }

        return "INV/" + gtMasterEntity.getId();

    }

    @Override
    @Transactional
    public void rejectGt(String gtId){
        Long id = Long.valueOf(gtId.split("/")[1]);
        GtMasterEntity gtMasterEntity = gtmr.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Goods Transfer not found for the provided process number.")));
        gtMasterEntity.setStatus("REJECTED");
        gtmr.save(gtMasterEntity);

        UserMaster um = userMasterRepository.findByUserId(gtMasterEntity.getSenderCustodianId());
        UserMaster umR = userMasterRepository.findByUserId(gtMasterEntity.getReceiverCustodianId());
        List<String> recipients = List.of(
                um.getEmail(),
                "udaychowdhary743@gmail.com",   //replace mail store preson and store purchase officer
                "kudaykiran.9949@gmail.com"
        );
        Context context = new Context();
        context.setVariable("gtId", gtId);
        context.setVariable("status", gtMasterEntity.getStatus());
        context.setVariable("senderName", um.getUserName());
        context.setVariable("receiverName", umR.getUserName());
        context.setVariable("remarks", "Goods Transfer has been rejected by receiver.");



        String subject = "Goods Transfer Rejected - GT No: " + gtId;
        emailService.sendGtReciverRejectedEmail(recipients, subject, "gt-receiver-rejection-template", context);
    }
    @Override
    @Transactional
    public void receiverApproveGt(String gtId) {
        Long id = Long.valueOf(gtId.split("/")[1]);
        GtMasterEntity gtMasterEntity = gtmr.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Goods Transfer not found for the provided process number.")));


        // After receiver accepts, move to awaiting store purchase officer approval
        gtMasterEntity.setStatus("AWAITING APPROVAL");
        gtmr.save(gtMasterEntity);

        UserMaster um = userMasterRepository.findByUserId(gtMasterEntity.getSenderCustodianId());
        UserMaster umR = userMasterRepository.findByUserId(gtMasterEntity.getReceiverCustodianId());
//        List<String> recipients = List.of(
//               um.getEmail(),
//                "udaychowdhary743@gmail.com",   //replace mail store preson and store purchase officer
//                "kudaykiran.9949@gmail.com"
//        );
        List<String> recipients = new ArrayList<>();

        if (um != null && um.getEmail() != null) {
            recipients.add(um.getEmail());
        }
        // recipients.add("udaychowdhary743@gmail.com");
        // recipients.add("kudaykiran.9949@gmail.com");



        Context context = new Context();
        context.setVariable("gtId", gtId);
        context.setVariable("status", gtMasterEntity.getStatus());
        context.setVariable("senderName", um.getUserName());
        context.setVariable("receiverName", umR.getUserName());
        context.setVariable("remarks", "Goods Transfer has been approved by receiver.");

        String subject = "Goods Transfer Approved - GT No: " + gtId;

        // Send asynchronously
        emailService.sendGtReciverEmail(recipients, subject, "gt-receiver-approval-template", context);

    }


    @Override
    @Transactional
    public void approveGt(String gtId) {
        Long id = Long.valueOf(gtId.split("/")[1]);
        GtMasterEntity gtMasterEntity = gtmr.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Goods Transfer not found for the provided process number.")));
        gtMasterEntity.setStatus("APPROVED");

        List<GtDtlEntity> gtDtlEntityList = gtdr.findByGtId(id);

        if(gtMasterEntity.getSenderLocationId().equalsIgnoreCase(gtMasterEntity.getReceiverLocationId())){
            System.out.println("CAME INNNN");
            for (GtDtlEntity gtDtlEntity : gtDtlEntityList) {
                if (Objects.isNull(gtDtlEntity.getAssetId())) {
                    addToConsumableOhq(gtDtlEntity, gtMasterEntity.getReceiverCustodianId());
                    reduceFromConsumable(gtDtlEntity, gtMasterEntity);
                } else {
                    addToCapitalOhq(gtDtlEntity, gtMasterEntity.getReceiverCustodianId());
                    reduceFromCapital(gtDtlEntity, gtMasterEntity);
                }
                
            }
            
        }
        gtmr.save(gtMasterEntity);
    }
    @Override
    @Transactional
    public void approveGtFromOgp(String gtId) {
        System.out.println("CALLED GT FROM OGP");
        Long id = Long.valueOf(gtId.split("/")[1]);
        GtMasterEntity gtMasterEntity = gtmr.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Goods Transfer not found for the provided process number.")));
        gtMasterEntity.setStatus("APPROVED");

        List<GtDtlEntity> gtDtlEntityList = gtdr.findByGtId(id);

            for (GtDtlEntity gtDtlEntity : gtDtlEntityList) {
                if (Objects.isNull(gtDtlEntity.getAssetId())) {
                    addToConsumableOhq(gtDtlEntity, gtMasterEntity.getReceiverCustodianId());
                    reduceFromConsumable(gtDtlEntity, gtMasterEntity);
                } else {
                    addToCapitalOhq(gtDtlEntity, gtMasterEntity.getReceiverCustodianId());
                    reduceFromCapital(gtDtlEntity, gtMasterEntity);
                }
                
            }
        gtmr.save(gtMasterEntity);
    }

    private void reduceFromConsumable(GtDtlEntity gtDtlEntity, GtMasterEntity gtMasterEntity){
        Optional<OhqMasterConsumableEntity> existingOhq = omcr.findByMaterialCodeAndLocatorIdAndCustodianId(
                gtDtlEntity.getMaterialCode(), gtDtlEntity.getSenderLocatorId(), gtMasterEntity.getSenderCustodianId().toString());
        if(existingOhq.isPresent()){
            OhqMasterConsumableEntity ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.subtract(gtDtlEntity.getQuantity()));
            omcr.save(ohq);
        }
    }

  /*  private void reduceFromCapital(GtDtlEntity gtDtlEntity, GtMasterEntity gtMasterEntity){
        Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
                gtDtlEntity.getAssetId(),
                gtDtlEntity.getSenderLocatorId(),
                gtMasterEntity.getSenderCustodianId().toString());
        if(existingOhq.isPresent()){
            OhqMasterEntity ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.subtract(gtDtlEntity.getQuantity()));
            ohqmr.save(ohq);

            List<AssetSerialEntity> senderSerials = assetSerialEntityRepository.findByAssetIdAndCustodianAndLocator(
                    gtDtlEntity.getAssetId(),
                    gtMasterEntity.getSenderCustodianId().toString(),
                    gtDtlEntity.getSenderLocatorId());

            int transferQty = gtDtlEntity.getQuantity().intValue();
            List<AssetSerialEntity> toTransfer = senderSerials.stream().limit(transferQty).toList();

            for (AssetSerialEntity serial : toTransfer) {
                serial.setCustodianId(gtMasterEntity.getReceiverCustodianId().toString());
                serial.setLocatorId(gtDtlEntity.getReceiverLocatorId());
                assetSerialEntityRepository.save(serial);
            }
        }

    }*/
 /* private void reduceFromCapital(GtDtlEntity gtDtlEntity, GtMasterEntity gtMasterEntity) {
      Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
              gtDtlEntity.getAssetId(),
              gtDtlEntity.getSenderLocatorId(),
              gtMasterEntity.getSenderCustodianId().toString());

      if (existingOhq.isPresent()) {
          OhqMasterEntity ohq = existingOhq.get();
          BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
          BigDecimal transferQty = gtDtlEntity.getQuantity();
          BigDecimal newQty = currentQty.subtract(transferQty);

          // Update quantity for sender
          ohq.setQuantity(newQty);
          ohqmr.save(ohq);

          //  Transfer required serial numbers
          List<AssetSerialEntity> senderSerials = assetSerialEntityRepository.findByAssetIdAndCustodianAndLocator(
                  gtDtlEntity.getAssetId(),
                  gtMasterEntity.getSenderCustodianId().toString(),
                  gtDtlEntity.getSenderLocatorId());

          int transferCount = transferQty.intValue();

          if (senderSerials.size() < transferCount) {
              throw new RuntimeException("Not enough serial numbers available to transfer for asset ID " + gtDtlEntity.getAssetId());
          }

          //  Move only limited number of serials
          List<AssetSerialEntity> toTransfer = senderSerials.stream().limit(transferCount).toList();

          for (AssetSerialEntity serial : toTransfer) {
              serial.setCustodianId(gtMasterEntity.getReceiverCustodianId().toString());
              serial.setLocatorId(gtDtlEntity.getReceiverLocatorId());
              assetSerialEntityRepository.save(serial);
          }
      }
  }
  */

    private void reduceFromCapital(GtDtlEntity gtDtlEntity, GtMasterEntity gtMasterEntity) {
        Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
                gtDtlEntity.getAssetId(),
                gtDtlEntity.getSenderLocatorId(),
                gtMasterEntity.getSenderCustodianId().toString());

        if (existingOhq.isPresent()) {
            OhqMasterEntity ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.subtract(gtDtlEntity.getQuantity()));
            ohqmr.save(ohq);

            // Transfer only the single serial number in this line item
            String serialNumber = gtDtlEntity.getSerialNo();
            if (serialNumber != null && !serialNumber.isEmpty()) {
                AssetSerialEntity serial = assetSerialEntityRepository
                        .findByAssetIdAndSerialNoAndCustodianIdAndLocatorId(
                                gtDtlEntity.getAssetId(),
                                serialNumber,
                                gtMasterEntity.getSenderCustodianId().toString(),
                                gtDtlEntity.getSenderLocatorId());

                if (serial != null) {
                    serial.setCustodianId(gtMasterEntity.getReceiverCustodianId().toString());
                    serial.setLocatorId(gtDtlEntity.getReceiverLocatorId());
                    assetSerialEntityRepository.save(serial);
                }
            }
        }
    }


    private void addToCapitalOhq(GtDtlEntity gtDtlEntity, Integer custodianId) {
        Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
                gtDtlEntity.getAssetId(),
                gtDtlEntity.getReceiverLocatorId(),
                custodianId.toString());

        OhqMasterEntity ohq;
        if (existingOhq.isPresent()) {
            System.out.println("EXISTING OHQ PRESENT");
            ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.add(gtDtlEntity.getQuantity()));
        } else {
            ohq = new OhqMasterEntity();
            ohq.setCustodianId(custodianId.toString());
            ohq.setAssetId(gtDtlEntity.getAssetId());
            ohq.setAssetCode(gtDtlEntity.getAssetCode());
            ohq.setLocatorId(gtDtlEntity.getReceiverLocatorId());
            ohq.setQuantity(gtDtlEntity.getQuantity());
            ohq.setBookValue(gtDtlEntity.getBookValue());
            ohq.setDepriciationRate(gtDtlEntity.getDepriciationRate());
            ohq.setUnitPrice(gtDtlEntity.getUnitPrice());
        }
        ohqmr.save(ohq);
    }


    private void addToConsumableOhq(GtDtlEntity gtDtlEntity, Integer custodianId) {
        Optional<OhqMasterConsumableEntity> existingOhq = omcr.findByMaterialCodeAndLocatorIdAndCustodianId(
                gtDtlEntity.getMaterialCode(), gtDtlEntity.getReceiverLocatorId(), custodianId.toString());
        OhqMasterConsumableEntity ohq;
        if (existingOhq.isPresent()) {
            ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.add(gtDtlEntity.getQuantity()));
        }
        else {  
                    ohq = new OhqMasterConsumableEntity();
                    ohq.setCustodianId(custodianId.toString());
                    ohq.setMaterialCode(gtDtlEntity.getMaterialCode());
                    ohq.setLocatorId(gtDtlEntity.getReceiverLocatorId());
                    ohq.setQuantity(gtDtlEntity.getQuantity());
                    ohq.setBookValue(gtDtlEntity.getBookValue());
                    ohq.setDepriciationRate(gtDtlEntity.getDepriciationRate());
                    ohq.setUnitPrice(gtDtlEntity.getUnitPrice());
                }
        omcr.save(ohq);
    }

   /* private void addToCapitalOhq(GtDtlEntity gtDtlEntity, Integer custodianId) {
         Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
                gtDtlEntity.getAssetId(),
                gtDtlEntity.getReceiverLocatorId(),
                custodianId.toString());


         OhqMasterEntity ohq;
        if (existingOhq.isPresent()) {
            System.out.println("EXISTING OHQ PRESENT");
            ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.add(gtDtlEntity.getQuantity()));
        } else {
            ohq = new OhqMasterEntity();
            ohq.setCustodianId(custodianId.toString());
            ohq.setAssetId(gtDtlEntity.getAssetId());
            ohq.setLocatorId(gtDtlEntity.getReceiverLocatorId());
            ohq.setQuantity(gtDtlEntity.getQuantity());
            ohq.setBookValue(gtDtlEntity.getBookValue());
            ohq.setDepriciationRate(gtDtlEntity.getDepriciationRate());
            ohq.setUnitPrice(gtDtlEntity.getUnitPrice());
        }
        ohqmr.save(ohq);
    }*/

    @Override
    public List<GtMasterDto> getPendingGt(){
        List<GtMasterEntity> gtMasterEntityList = gtmr.findByStatus("AWAITING APPROVAL");
        List<GtMasterDto> gtMasterDtoList = new ArrayList<>();
        Set<Integer> userIds = new HashSet<>();
        for (GtMasterEntity entity : gtMasterEntityList) {
            userIds.add(entity.getSenderCustodianId());
            userIds.add(entity.getReceiverCustodianId());
        }

        List<UserMaster> users = userMasterRepository.findByUserIdIn(userIds);
        Map<Integer, String> userMap = users.stream()
                .filter(u -> u.getUserId() != null)
                .collect(Collectors.toMap(
                        UserMaster::getUserId,
                        u -> u.getUserName() != null ? u.getUserName() : "Unknown",
                        (v1, v2) -> v1
                ));


        for (GtMasterEntity gtMasterEntity : gtMasterEntityList) {
            GtMasterDto gtMasterDto = new GtMasterDto();
            gtMasterDto.setId("INV/" + gtMasterEntity.getId());
            gtMasterDto.setGtDate(CommonUtils.convertDateToString(gtMasterEntity.getGtDate()));
            gtMasterDto.setSenderLocationId(gtMasterEntity.getSenderLocationId());
            gtMasterDto.setReceiverLocationId(gtMasterEntity.getReceiverLocationId());
            gtMasterDto.setSenderCustodianId(gtMasterEntity.getSenderCustodianId());
            gtMasterDto.setReceiverCustodianId(gtMasterEntity.getReceiverCustodianId());
            gtMasterDto.setSenderCustodianName(
                    userMap.getOrDefault(gtMasterEntity.getSenderCustodianId(), "Unknown"));
            gtMasterDto.setReceiverCustodianName(
                    userMap.getOrDefault(gtMasterEntity.getReceiverCustodianId(), "Unknown"));

            List<GtDtlEntity> gtDtlEntityList = gtdr.findByGtId(gtMasterEntity.getId());
            List<GtDtl> gtDtlList = new ArrayList<>();
            for (GtDtlEntity gtDtlEntity : gtDtlEntityList) {
                GtDtl gtDtl = new GtDtl();
                gtDtl.setAssetId(gtDtlEntity.getAssetId());
                gtDtl.setAssetCode(gtDtlEntity.getAssetCode());
                gtDtl.setAssetDesc(gtDtlEntity.getAssetDesc());
                gtDtl.setMaterialCode(gtDtlEntity.getMaterialCode());
                gtDtl.setMaterialDesc(gtDtlEntity.getMaterialDesc());
                gtDtl.setQuantity(gtDtlEntity.getQuantity());
                gtDtl.setReceiverLocatorId(gtDtlEntity.getReceiverLocatorId());
                gtDtl.setSenderLocatorId(gtDtlEntity.getSenderLocatorId());
                gtDtl.setUnitPrice(gtDtlEntity.getUnitPrice());
                gtDtl.setDepriciationRate(gtDtlEntity.getDepriciationRate());
                gtDtl.setBookValue(gtDtlEntity.getBookValue());
                gtDtl.setPoId(gtDtlEntity.getPoId());
                gtDtl.setSerialNo(gtDtlEntity.getSerialNo());
                gtDtl.setReasonForTransfer(gtDtlEntity.getReasonForTransfer());
                gtDtl.setModelNo(gtDtlEntity.getModelNo());
                gtDtlList.add(gtDtl);
            }
            gtMasterDto.setMaterialDtlList(gtDtlList);
            gtMasterDtoList.add(gtMasterDto);

        }
        return gtMasterDtoList;
    }
    @Override
    public List<GtMasterDto> getRecevierPendingGt(){
        List<GtMasterEntity> gtMasterEntityList = gtmr.findByStatus("PENDING RECEIVER APPROVAL");
        List<GtMasterDto> gtMasterDtoList = new ArrayList<>();
        Set<Integer> userIds = new HashSet<>();
        for (GtMasterEntity entity : gtMasterEntityList) {
            userIds.add(entity.getSenderCustodianId());
            userIds.add(entity.getReceiverCustodianId());
        }

        List<UserMaster> users = userMasterRepository.findByUserIdIn(userIds);
        Map<Integer, String> userMap = users.stream()
                .filter(u -> u.getUserId() != null)
                .collect(Collectors.toMap(
                        UserMaster::getUserId,
                        u -> u.getUserName() != null ? u.getUserName() : "Unknown",
                        (v1, v2) -> v1
                ));

        for (GtMasterEntity gtMasterEntity : gtMasterEntityList) {
            GtMasterDto gtMasterDto = new GtMasterDto();
            gtMasterDto.setId("INV/" + gtMasterEntity.getId());
            gtMasterDto.setGtDate(CommonUtils.convertDateToString(gtMasterEntity.getGtDate()));
            gtMasterDto.setSenderLocationId(gtMasterEntity.getSenderLocationId());
            gtMasterDto.setReceiverLocationId(gtMasterEntity.getReceiverLocationId());
            gtMasterDto.setSenderCustodianId(gtMasterEntity.getSenderCustodianId());
            gtMasterDto.setReceiverCustodianId(gtMasterEntity.getReceiverCustodianId());
            gtMasterDto.setSenderCustodianName(
                    userMap.getOrDefault(gtMasterEntity.getSenderCustodianId(), "Unknown"));
            gtMasterDto.setReceiverCustodianName(
                    userMap.getOrDefault(gtMasterEntity.getReceiverCustodianId(), "Unknown"));

            List<GtDtlEntity> gtDtlEntityList = gtdr.findByGtId(gtMasterEntity.getId());
            List<GtDtl> gtDtlList = new ArrayList<>();
            for (GtDtlEntity gtDtlEntity : gtDtlEntityList) {
                GtDtl gtDtl = new GtDtl();
                gtDtl.setAssetId(gtDtlEntity.getAssetId());
                gtDtl.setAssetCode(gtDtlEntity.getAssetCode());
                gtDtl.setAssetDesc(gtDtlEntity.getAssetDesc());
                gtDtl.setMaterialCode(gtDtlEntity.getMaterialCode());
                gtDtl.setMaterialDesc(gtDtlEntity.getMaterialDesc());
                gtDtl.setQuantity(gtDtlEntity.getQuantity());
                gtDtl.setReceiverLocatorId(gtDtlEntity.getReceiverLocatorId());
                gtDtl.setSenderLocatorId(gtDtlEntity.getSenderLocatorId());
                gtDtl.setUnitPrice(gtDtlEntity.getUnitPrice());
                gtDtl.setDepriciationRate(gtDtlEntity.getDepriciationRate());
                gtDtl.setBookValue(gtDtlEntity.getBookValue());
                gtDtl.setPoId(gtDtlEntity.getPoId());
                gtDtl.setModelNo(gtDtlEntity.getModelNo());
                gtDtl.setSerialNo(gtDtlEntity.getSerialNo());
                gtDtl.setReasonForTransfer(gtDtlEntity.getReasonForTransfer());

                gtDtlList.add(gtDtl);
            }
            gtMasterDto.setMaterialDtlList(gtDtlList);
            gtMasterDtoList.add(gtMasterDto);

        }
        return gtMasterDtoList;
    }
    @Override
    public GtMasterResponseDto getGtById(String gtId) {
        Long id = Long.valueOf(gtId.split("/")[1]);
        GtMasterEntity master= gtmr.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Goods Transfer not found for the provided process number.")));

        List<GtDtlEntity> dtls = gtdr.findByGtId(id);

        GtMasterResponseDto dto = new GtMasterResponseDto();
        dto.setGtId("INV/" + master.getId());
        dto.setSenderLocationId(master.getSenderLocationId());
        dto.setReceiverLocationId(master.getReceiverLocationId());
        dto.setSenderCustodianId(master.getSenderCustodianId());
        dto.setReceiverCustodianId(master.getReceiverCustodianId());
        LocalDate da = master.getGtDate();
        if(da !=null){
            dto.setGtDate(CommonUtils.convertDateToString(da));
        }else{
            dto.setGtDate(null);
        }
        dto.setStatus(master.getStatus());
       // String d = CommonUtils.convertDateToString(master.getCreatedBy());
        dto.setCreatedBy(master.getCreatedBy());
        LocalDateTime date = master.getCreateDate();
        if (date != null) {
            dto.setCreateDate(CommonUtils.convertDateToString(date.toLocalDate()));
        }else{
            dto.setCreateDate(null);
        }


        List<GtDtlDto> dtlDtos = dtls.stream().map(d -> {
            GtDtlDto dd = new GtDtlDto();
            dd.setId(d.getId());
            dd.setAssetId(d.getAssetId());
            dd.setAssetDesc(d.getAssetDesc());
            dd.setMaterialCode(d.getMaterialCode());
            dd.setMaterialDesc(d.getMaterialDesc());
            dd.setUnitPrice(d.getUnitPrice());
            dd.setDepriciationRate(d.getDepriciationRate());
            dd.setBookValue(d.getBookValue());
            dd.setQuantity(d.getQuantity());
            dd.setReceiverLocatorId(String.valueOf(d.getReceiverLocatorId()));
            dd.setSenderLocatorId(String.valueOf(d.getSenderLocatorId()));
            return dd;
        }).collect(Collectors.toList());

        dto.setMaterialDtlList(dtlDtos);

        return dto;
    }
    @Override
    public List<Long> getPendingInterFiledGtIdsOgp() {
        return gtmr.findApprovedIdsWithoutOgp();
    }


    public GtMasterDto getGtDtls(String processNo){
        Long id = Long.parseLong(processNo.split("/")[1]);
        GtMasterEntity gtme = gtmr.findById(id)
                        .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Goods Transfer not found for the provided process number.")
                ));
        
        List<GtDtl> gtDtlList = new ArrayList<>();

        List<GtDtlEntity> gtdel = gtdr.findByGtId(gtme.getId());
        for (GtDtlEntity gtde : gtdel) {
            GtDtl gtDtl = new GtDtl();
            gtDtl.setAssetId(gtde.getAssetId());
            gtDtl.setAssetDesc(gtde.getAssetDesc());
            gtDtl.setAssetCode(gtde.getAssetCode());
            gtDtl.setMaterialCode(gtde.getMaterialCode());
            gtDtl.setMaterialDesc(gtde.getMaterialDesc());
            gtDtl.setQuantity(gtde.getQuantity());
            gtDtl.setReceiverLocatorId(gtde.getReceiverLocatorId());
            gtDtl.setSenderLocatorId(gtde.getSenderLocatorId());
            gtDtl.setUnitPrice(gtde.getUnitPrice());
            gtDtl.setDepriciationRate(gtde.getDepriciationRate());
            gtDtl.setBookValue(gtde.getBookValue());
            gtDtl.setSerialNo(gtde.getSerialNo());
            gtDtlList.add(gtDtl);
        }
        GtMasterDto gtMasterDto = new GtMasterDto();
        gtMasterDto.setId("INV/" + gtme.getId());
        gtMasterDto.setGtDate(CommonUtils.convertDateToString(gtme.getGtDate()));
        gtMasterDto.setSenderLocationId(gtme.getSenderLocationId());
        gtMasterDto.setReceiverLocationId(gtme.getReceiverLocationId());
        gtMasterDto.setSenderCustodianId(gtme.getSenderCustodianId());
        gtMasterDto.setReceiverCustodianId(gtme.getReceiverCustodianId());
        gtMasterDto.setMaterialDtlList(gtDtlList);
        gtMasterDto.setStatus(gtme.getStatus());
        return gtMasterDto;
    }
  /*  @Override
    public List<withinFieldStationGtDto> getGtReport(String startDate, String endDate) {
        List<LocalDate> dateRange = CommonUtils.getDateRengeAsLocalDate(startDate, endDate);
        List<Object[]> results = gtmr.getGtReport(dateRange.get(0), dateRange.get(1));
        ObjectMapper mapper = new ObjectMapper();
        List<withinFieldStationGtDto> reportList = new ArrayList<>();

        for (Object[] row : results) {
            withinFieldStationGtDto dto = new withinFieldStationGtDto();
            dto.setGtId(((Number) row[0]).longValue());
            dto.setSenderLocationId((String) row[1]);
            dto.setReceiverLocationId((String) row[2]);
            dto.setSenderCustodianId((Integer) row[3]);
            dto.setReceiverCustodianId((Integer) row[4]);
            dto.setStatus((String) row[5]);
            dto.setGtDate(((java.sql.Date) row[6]).toLocalDate());
            dto.setCreateDate(((java.sql.Timestamp) row[7]).toLocalDateTime());
            dto.setCreatedBy((Integer) row[8]);

            try {
                String materialDetailsJson = (String) row[9];
                List<GtReportDtlDto> materialDetails = mapper.readValue(
                        materialDetailsJson, new TypeReference<List<GtReportDtlDto>>() {}
                );
                dto.setMaterialDetails(materialDetails);
            } catch (Exception e) {
                dto.setMaterialDetails(new ArrayList<>());
            }

            reportList.add(dto);
        }

        return reportList;
    }*/
  @Override
  public List<withinFieldStationGtDto> getGtReport(String startDate, String endDate) {
      List<LocalDate> dateRange = CommonUtils.getDateRengeAsLocalDate(startDate, endDate);
      ObjectMapper mapper = new ObjectMapper();
      List<withinFieldStationGtDto> reportList = new ArrayList<>();


      List<Object[]> gtResults = gtmr.getGtReport(dateRange.get(0), dateRange.get(1));
      for (Object[] row : gtResults) {
          withinFieldStationGtDto dto = mapRowToDto(row, mapper);
          dto.setType("WITHIN FIELD STATION");
          reportList.add(dto);
      }

      List<Object[]> ogpResults = ogmr.getOgpGtReport(dateRange.get(0), dateRange.get(1));
      for (Object[] row : ogpResults) {
          withinFieldStationGtDto dto = mapRowToDto(row, mapper);
          dto.setType("INTER FIELD STATION");
          reportList.add(dto);
      }

      return reportList;
  }
    private withinFieldStationGtDto mapRowToDto(Object[] row, ObjectMapper mapper) {
        withinFieldStationGtDto dto = new withinFieldStationGtDto();
        dto.setGtId(((Number) row[0]).longValue());
        dto.setSenderLocationId((String) row[1]);
        dto.setReceiverLocationId((String) row[2]);
        dto.setSenderCustodianId((Integer) row[3]);
        dto.setReceiverCustodianId((Integer) row[4]);
        dto.setStatus((String) row[5]);
        dto.setGtDate(((java.sql.Date) row[6]).toLocalDate());
        dto.setCreateDate(((java.sql.Timestamp) row[7]).toLocalDateTime());
        dto.setCreatedBy((Integer) row[8]);

        try {
            String materialDetailsJson = (String) row[9];
            List<GtReportDtlDto> materialDetails = mapper.readValue(
                    materialDetailsJson, new TypeReference<List<GtReportDtlDto>>() {}
            );
            dto.setMaterialDetails(materialDetails);
        } catch (Exception e) {
            dto.setMaterialDetails(new ArrayList<>());
        }

        return dto;
    }


}
