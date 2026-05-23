package com.astro.service.impl.InventoryModule;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.DemandAndIssueReportDto;
import com.astro.dto.workflow.InventoryModule.DemandMaterialsDto;
import com.astro.dto.workflow.InventoryModule.DiMasterDto;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtDtl;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtMasterDto;
import com.astro.entity.InventoryModule.*;
import com.astro.entity.UserMaster;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.DemandAndIssueDtlEntityRepository;
import com.astro.repository.InventoryModule.DemandAndIssueMasterEntityRepository;
import com.astro.repository.InventoryModule.OhqConsumableStoreStockRepository;
import com.astro.repository.InventoryModule.OhqMasterConsumableRepository;
import com.astro.repository.UserMasterRepository;
import com.astro.service.InventoryModule.DiService;
import com.astro.service.InventoryModule.GtService;
import com.astro.util.CommonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiServiceImpl implements DiService {
    @Autowired
    private DemandAndIssueMasterEntityRepository demandAndIssueMasterEntityRepository;
    @Autowired
    private DemandAndIssueDtlEntityRepository demandAndIssueDtlEntityRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;
    @Autowired
    private OhqMasterConsumableRepository omcr;
    @Autowired
    private OhqConsumableStoreStockRepository ohqStoreStockRepo;
    @Autowired
    private OhqMasterConsumableRepository ohqMasterConsumableRepository;
    @Override
    @Transactional
    public String createDi(DiMasterDto diMasterDto) {
        for (GtDtl gtDtl : diMasterDto.getMaterialDtlList()) {
            // Get current stock
            OhqConsumableStoreStockEntity stock = ohqStoreStockRepo
                    .findByMaterialCode(gtDtl.getMaterialCode())
                    .orElse(null);

            BigDecimal requestedQty = gtDtl.getQuantity();
            BigDecimal availableQty = stock != null ? stock.getQuantity() : BigDecimal.ZERO;

            // Get in-progress quantity for this material (AWAITING APPROVAL & DEMAND)
            BigDecimal inProgressQty = demandAndIssueDtlEntityRepository.getInProgressQty(
                    gtDtl.getMaterialCode(),
                    Arrays.asList("AWAITING APPROVAL", "DEMAND")
            );

            // Total pending qty = in-progress + requested
            BigDecimal totalPendingQty = inProgressQty.add(requestedQty);

            // Validate against available stock
            if (totalPendingQty.compareTo(availableQty) > 0) {
                throw new RuntimeException("Insufficient stock for material: " + gtDtl.getMaterialCode()
                        + ". Available: " + availableQty + ", In Progress: " + inProgressQty
                        + ", Requested: " + requestedQty);
            }
        }
        DemandAndIssueMasterEntity di = new DemandAndIssueMasterEntity();
        di.setSenderLocationId(diMasterDto.getSenderLocationId());
        di.setSenderCustodianId(diMasterDto.getSenderCustodianId());
        di.setDemandIssueDate(CommonUtils.convertStringToDateObject(diMasterDto.getDiDate()));
        di.setCreatedBy(diMasterDto.getCreatedBy());
        di.setCreateDate(LocalDateTime.now());
        di.setStatus("AWAITING APPROVAL");
        di = demandAndIssueMasterEntityRepository.save(di);

        for (GtDtl gtDtl : diMasterDto.getMaterialDtlList()) {
            DemandAndIssueDtlEntity demandAndIssueDtl = new DemandAndIssueDtlEntity();
            demandAndIssueDtl.setDiId(di.getId());
            demandAndIssueDtl.setAssetId(gtDtl.getAssetId());
            demandAndIssueDtl.setAssetDesc(gtDtl.getAssetDesc());
            demandAndIssueDtl.setMaterialCode(gtDtl.getMaterialCode());
            demandAndIssueDtl.setUnitPrice(gtDtl.getUnitPrice());
            demandAndIssueDtl.setDepriciationRate(gtDtl.getDepriciationRate());
            demandAndIssueDtl.setBookValue(gtDtl.getBookValue());
            demandAndIssueDtl.setMaterialDesc(gtDtl.getMaterialDesc());
            demandAndIssueDtl.setQuantity(gtDtl.getQuantity());
            demandAndIssueDtl.setReceiverLocatorId(gtDtl.getReceiverLocatorId());
            demandAndIssueDtl.setSenderLocatorId(gtDtl.getSenderLocatorId());
            demandAndIssueDtl.setUom(gtDtl.getUom());
            demandAndIssueDtlEntityRepository.save(demandAndIssueDtl);
        }

        return "INV/" + di.getId();
    }

    @Override
    public List<DiMasterDto> getPendingDi() {

        List<DemandAndIssueMasterEntity> diMasterEntityList = demandAndIssueMasterEntityRepository.findByStatus("AWAITING APPROVAL");

        List<DiMasterDto> diMasterDtoList = new ArrayList<>();
        Set<Integer> userIds = new HashSet<>();


        for (DemandAndIssueMasterEntity entity : diMasterEntityList) {
            userIds.add(entity.getSenderCustodianId());
          //  userIds.add(entity.getReceiverCustodianId());
        }


        List<UserMaster> users = userMasterRepository.findByUserIdIn(userIds);
        Map<Integer, String> userMap = users.stream()
                .filter(u -> u.getUserId() != null)
                .collect(Collectors.toMap(
                        UserMaster::getUserId,
                        u -> u.getUserName() != null ? u.getUserName() : "Unknown",
                        (v1, v2) -> v1
                ));

        for (DemandAndIssueMasterEntity diMasterEntity : diMasterEntityList) {
            DiMasterDto diMasterDto = new DiMasterDto();
            diMasterDto.setId("INV/" + diMasterEntity.getId());
            diMasterDto.setDiDate(CommonUtils.convertDateToString(diMasterEntity.getDemandIssueDate()));
            diMasterDto.setSenderLocationId(diMasterEntity.getSenderLocationId());
         //   diMasterDto.setReceiverLocationId(diMasterEntity.getReceiverLocationId());
            diMasterDto.setSenderCustodianId(diMasterEntity.getSenderCustodianId());
          //  diMasterDto.setReceiverCustodianId(diMasterEntity.getReceiverCustodianId());
            diMasterDto.setSenderCustodianName(
                    userMap.getOrDefault(diMasterEntity.getSenderCustodianId(), "Unknown"));
           // diMasterDto.setReceiverCustodianName(
               //     userMap.getOrDefault(diMasterEntity.getReceiverCustodianId(), "Unknown"));

            List<DemandAndIssueDtlEntity> diDtlEntityList = demandAndIssueDtlEntityRepository.findByDiId(diMasterEntity.getId());
            List<GtDtl> diDtlList = new ArrayList<>();
            for (DemandAndIssueDtlEntity diDtlEntity : diDtlEntityList) {
                GtDtl diDtl = new GtDtl();
                diDtl.setAssetId(diDtlEntity.getAssetId());
                diDtl.setAssetDesc(diDtlEntity.getAssetDesc());
                diDtl.setMaterialCode(diDtlEntity.getMaterialCode());
                diDtl.setMaterialDesc(diDtlEntity.getMaterialDesc());
                diDtl.setQuantity(diDtlEntity.getQuantity());
                diDtl.setReceiverLocatorId(diDtlEntity.getReceiverLocatorId());
                diDtl.setSenderLocatorId(diDtlEntity.getSenderLocatorId());
                diDtl.setUnitPrice(diDtlEntity.getUnitPrice());
                diDtl.setDepriciationRate(diDtlEntity.getDepriciationRate());
                diDtl.setBookValue(diDtlEntity.getBookValue());
                diDtlList.add(diDtl);
            }

            diMasterDto.setMaterialDtlList(diDtlList);
            diMasterDtoList.add(diMasterDto);
        }

        return diMasterDtoList;
    }
    @Override
    public List<DiMasterDto> getPendingIssueNote() {

        List<DemandAndIssueMasterEntity> diMasterEntityList = demandAndIssueMasterEntityRepository.findByStatus("DEMAND");

        List<DiMasterDto> diMasterDtoList = new ArrayList<>();
        Set<Integer> userIds = new HashSet<>();


        for (DemandAndIssueMasterEntity entity : diMasterEntityList) {
            userIds.add(entity.getSenderCustodianId());
            //  userIds.add(entity.getReceiverCustodianId());
        }


        List<UserMaster> users = userMasterRepository.findByUserIdIn(userIds);
        Map<Integer, String> userMap = users.stream()
                .filter(u -> u.getUserId() != null)
                .collect(Collectors.toMap(
                        UserMaster::getUserId,
                        u -> u.getUserName() != null ? u.getUserName() : "Unknown",
                        (v1, v2) -> v1
                ));

        for (DemandAndIssueMasterEntity diMasterEntity : diMasterEntityList) {
            DiMasterDto diMasterDto = new DiMasterDto();
            diMasterDto.setId("INV/" + diMasterEntity.getId());
            diMasterDto.setDiDate(CommonUtils.convertDateToString(diMasterEntity.getDemandIssueDate()));
            diMasterDto.setSenderLocationId(diMasterEntity.getSenderLocationId());
            //   diMasterDto.setReceiverLocationId(diMasterEntity.getReceiverLocationId());
            diMasterDto.setSenderCustodianId(diMasterEntity.getSenderCustodianId());
            diMasterDto.setStatus(diMasterEntity.getStatus());
            //  diMasterDto.setReceiverCustodianId(diMasterEntity.getReceiverCustodianId());
            diMasterDto.setSenderCustodianName(
                    userMap.getOrDefault(diMasterEntity.getSenderCustodianId(), "Unknown"));
            // diMasterDto.setReceiverCustodianName(
            //     userMap.getOrDefault(diMasterEntity.getReceiverCustodianId(), "Unknown"));

            List<DemandAndIssueDtlEntity> diDtlEntityList = demandAndIssueDtlEntityRepository.findByDiId(diMasterEntity.getId());
            List<GtDtl> diDtlList = new ArrayList<>();
            for (DemandAndIssueDtlEntity diDtlEntity : diDtlEntityList) {
                GtDtl diDtl = new GtDtl();
                diDtl.setAssetId(diDtlEntity.getAssetId());
                diDtl.setAssetDesc(diDtlEntity.getAssetDesc());
                diDtl.setMaterialCode(diDtlEntity.getMaterialCode());
                diDtl.setMaterialDesc(diDtlEntity.getMaterialDesc());
                diDtl.setQuantity(diDtlEntity.getQuantity());
                diDtl.setReceiverLocatorId(diDtlEntity.getReceiverLocatorId());
                diDtl.setSenderLocatorId(diDtlEntity.getSenderLocatorId());
                diDtl.setUnitPrice(diDtlEntity.getUnitPrice());
                diDtl.setDepriciationRate(diDtlEntity.getDepriciationRate());
                diDtl.setBookValue(diDtlEntity.getBookValue());
                diDtlList.add(diDtl);
            }

            diMasterDto.setMaterialDtlList(diDtlList);
            diMasterDtoList.add(diMasterDto);
        }

        return diMasterDtoList;
    }

    @Override
    @Transactional
    public void approveDi(String diId) {
        Long id = Long.valueOf(diId.split("/")[1]);
        DemandAndIssueMasterEntity demandAndIssueMaster = demandAndIssueMasterEntityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Demand Issue not found for the provided process number.")));
        demandAndIssueMaster.setStatus("DEMAND");
        demandAndIssueMasterEntityRepository.save(demandAndIssueMaster);

    }
    @Override
    @Transactional
    public void rejectDi(String diId) {
        Long id = Long.valueOf(diId.split("/")[1]);
        DemandAndIssueMasterEntity demandAndIssueMaster = demandAndIssueMasterEntityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Demand Issue not found for the provided process number.")));
        demandAndIssueMaster.setStatus("Rejected");
        demandAndIssueMasterEntityRepository.save(demandAndIssueMaster);

    }

    /*@Override
    @Transactional
    public void approveDi(String diId) {
        Long id = Long.valueOf(diId.split("/")[1]);
        DemandAndIssueMasterEntity demandAndIssueMaster = demandAndIssueMasterEntityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Delivery Issue not found for the provided process number.")));


    }
    private void addToConsumableOhqDi(DemandAndIssueDtlEntity diDtlEntity, Integer custodianId) {
        Optional<OhqMasterConsumableEntity> existingOhq = omcr.findByMaterialCodeAndLocatorIdAndCustodianId(
                diDtlEntity.getMaterialCode(), diDtlEntity.getReceiverLocatorId(), custodianId.toString());
        OhqMasterConsumableEntity ohq;
        if (existingOhq.isPresent()) {
            ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.add(diDtlEntity.getQuantity()));
        } else {
            ohq = new OhqMasterConsumableEntity();
            ohq.setCustodianId(custodianId.toString());
            ohq.setMaterialCode(diDtlEntity.getMaterialCode());
            ohq.setLocatorId(diDtlEntity.getReceiverLocatorId());
            ohq.setQuantity(diDtlEntity.getQuantity());
            ohq.setBookValue(diDtlEntity.getBookValue());
            ohq.setDepriciationRate(diDtlEntity.getDepriciationRate());
            ohq.setUnitPrice(diDtlEntity.getUnitPrice());
        }
        omcr.save(ohq);
    }

    private void reduceFromConsumableDi(DemandAndIssueDtlEntity diDtlEntity, DemandAndIssueMasterEntity diMasterEntity) {
        Optional<OhqMasterConsumableEntity> existingOhq = omcr.findByMaterialCodeAndLocatorIdAndCustodianId(
                diDtlEntity.getMaterialCode(), diDtlEntity.getSenderLocatorId(), diMasterEntity.getSenderCustodianId().toString());
        if (existingOhq.isPresent()) {
            OhqMasterConsumableEntity ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.subtract(diDtlEntity.getQuantity()));
            omcr.save(ohq);
        }
    }
*/
    public DiMasterDto getDiById(String diId) {
        Long id = Long.valueOf(diId.split("/")[1]);
        DemandAndIssueMasterEntity masterEntity = demandAndIssueMasterEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demand & Issue ID not found: " + diId));


        List<DemandAndIssueDtlEntity> detailEntities = demandAndIssueDtlEntityRepository.findByDiId(id);

        // Convert to DTO
        DiMasterDto dto = new DiMasterDto();
        dto.setDiId(diId);
        dto.setSenderLocationId(masterEntity.getSenderLocationId());
        dto.setSenderCustodianId(masterEntity.getSenderCustodianId());
        LocalDate diDate = masterEntity.getDemandIssueDate();
        dto.setDiDate(CommonUtils.convertDateToString(diDate));
        dto.setStatus(masterEntity.getStatus());
        dto.setMaterialDtlList(detailEntities.stream().map(this::mapToDtlDto).collect(Collectors.toList()));

        return dto;
    }

    private GtDtl mapToDtlDto(DemandAndIssueDtlEntity entity) {
        GtDtl dto = new GtDtl();
        dto.setAssetId(entity.getAssetId());
        dto.setAssetDesc(entity.getAssetDesc());
        dto.setMaterialCode(entity.getMaterialCode());
        dto.setMaterialDesc(entity.getMaterialDesc());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setDepriciationRate(entity.getDepriciationRate());
        dto.setBookValue(entity.getBookValue());
        dto.setQuantity(entity.getQuantity());
        dto.setSenderLocatorId(entity.getSenderLocatorId());
        dto.setReceiverLocatorId(entity.getReceiverLocatorId());
        dto.setUom(entity.getUom());
        return dto;
    }

    @Transactional
    public String updateDi(String diId, DiMasterDto diMasterDto) {
        Long id = Long.valueOf(diId.split("/")[1]);

        DemandAndIssueMasterEntity di = demandAndIssueMasterEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DI not found with ID: " + diId));

        // Update DI Master details if needed
        di.setDemandIssueDate(CommonUtils.convertStringToDateObject(diMasterDto.getDiDate()));
        di.setSenderLocationId(diMasterDto.getSenderLocationId());
        di.setSenderCustodianId(diMasterDto.getSenderCustodianId());
        di.setIssueDate(LocalDate.now());
        di.setIssuedBy(diMasterDto.getCreatedBy() != null ? Integer.valueOf(diMasterDto.getCreatedBy()) : null);
        di.setStatus("Approved");
        demandAndIssueMasterEntityRepository.save(di);

        // Fetch existing materials for this DI
        List<DemandAndIssueDtlEntity> existingMaterials = demandAndIssueDtlEntityRepository.findByDiId(di.getId());

        // Map for quick lookup
        Map<String, DemandAndIssueDtlEntity> existingMap = existingMaterials.stream()
                .collect(Collectors.toMap(DemandAndIssueDtlEntity::getMaterialCode, m -> m));

        // Handle Add / Update
        for (GtDtl gtDtl : diMasterDto.getMaterialDtlList()) {
            DemandAndIssueDtlEntity existing = existingMap.get(gtDtl.getMaterialCode());

            if (existing != null) {
                // Update existing material
                existing.setQuantity(gtDtl.getQuantity());
                existing.setReceiverLocatorId(gtDtl.getReceiverLocatorId());
                existing.setSenderLocatorId(gtDtl.getSenderLocatorId());
                existing.setUnitPrice(gtDtl.getUnitPrice());
                existing.setBookValue(gtDtl.getBookValue());
                existing.setDepriciationRate(gtDtl.getDepriciationRate());
                existing.setMaterialDesc(gtDtl.getMaterialDesc());
                demandAndIssueDtlEntityRepository.save(existing);
                existingMap.remove(gtDtl.getMaterialCode());

                // Reduce stock quantity for updated material
                reduceStock(gtDtl.getMaterialCode(), gtDtl.getQuantity());

                addToCustodianStock(gtDtl.getMaterialCode(), gtDtl.getQuantity(), di.getSenderCustodianId(),gtDtl.getSenderLocatorId());

            } else {
                // Add new material
                DemandAndIssueDtlEntity newMaterial = new DemandAndIssueDtlEntity();
                newMaterial.setDiId(di.getId());
                newMaterial.setAssetId(gtDtl.getAssetId());
                newMaterial.setAssetDesc(gtDtl.getAssetDesc());
                newMaterial.setMaterialCode(gtDtl.getMaterialCode());
                newMaterial.setUnitPrice(gtDtl.getUnitPrice());
                newMaterial.setDepriciationRate(gtDtl.getDepriciationRate());
                newMaterial.setBookValue(gtDtl.getBookValue());
                newMaterial.setMaterialDesc(gtDtl.getMaterialDesc());
                newMaterial.setQuantity(gtDtl.getQuantity());
                newMaterial.setReceiverLocatorId(gtDtl.getReceiverLocatorId());
                newMaterial.setSenderLocatorId(gtDtl.getSenderLocatorId());
                demandAndIssueDtlEntityRepository.save(newMaterial);

                // Reduce stock for newly added material
                reduceStock(gtDtl.getMaterialCode(), gtDtl.getQuantity());

                addToCustodianStock(gtDtl.getMaterialCode(), gtDtl.getQuantity(), di.getSenderCustodianId(),gtDtl.getSenderLocatorId());


            }
        }

        // 6. Remove materials not present in the updated list
        if (!existingMap.isEmpty()) {
            for (DemandAndIssueDtlEntity toDelete : existingMap.values()) {
                demandAndIssueDtlEntityRepository.delete(toDelete);
            }
        }

        return "DI " + diId + " updated successfully and stock adjusted!";
    }

    private void reduceStock(String materialCode, BigDecimal issuedQty) {
        OhqConsumableStoreStockEntity stock = ohqStoreStockRepo
                .findByMaterialCode(materialCode)
                .orElseThrow(() -> new RuntimeException("Stock not found for material: " + materialCode));

        BigDecimal newQty = stock.getQuantity().subtract(issuedQty);
        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient stock for material: " + materialCode);
        }

        stock.setQuantity(newQty);
        ohqStoreStockRepo.save(stock);
    }

    private void addToCustodianStock(String materialCode, BigDecimal qtyToAdd, Integer custodianId,Integer senderLocatorId) {
        OhqMasterConsumableEntity stock = ohqMasterConsumableRepository
                .findByMaterialCodeAndCustodianId(materialCode, String.valueOf(custodianId))
                .orElse(null);

        if (stock != null) {
            // Material exists for custodian -> increment quantity
            stock.setQuantity(stock.getQuantity().add(qtyToAdd));
        } else {
            // Material does not exist for custodian -> create new record
            stock = new OhqMasterConsumableEntity();
            stock.setMaterialCode(materialCode);
            stock.setCustodianId(String.valueOf(custodianId));
            stock.setQuantity(qtyToAdd);
            stock.setBookValue(BigDecimal.ZERO);
            stock.setUnitPrice(BigDecimal.ZERO);
            stock.setDepriciationRate(BigDecimal.ZERO);
            stock.setLocatorId(senderLocatorId);
        }
       ohqMasterConsumableRepository.save(stock);
    }


    @Override
    public List<DemandAndIssueReportDto> getDemandAndIssueReport(String startDate, String endDate) {
        List<LocalDateTime> range = CommonUtils.getDateRenge(startDate, endDate);
        LocalDateTime start = range.get(0);
        LocalDateTime end = range.get(1);
        List<Object[]> rows = demandAndIssueMasterEntityRepository.getApprovedDemandAndIssueReport(start, end);

        ObjectMapper mapper = new ObjectMapper();
        List<DemandAndIssueReportDto> reports = new ArrayList<>();

        for (Object[] row : rows) {
            DemandAndIssueReportDto dto = new DemandAndIssueReportDto();
            dto.setId(((Number) row[0]).longValue());
            dto.setSenderLocationId((String) row[1]);
            dto.setStatus((String) row[2]);
            dto.setSenderCustodianId((Integer) row[3]);
            dto.setCreateDate(((Timestamp) row[4]).toLocalDateTime());
            dto.setDemandIssueDate(row[5] != null ? ((Date) row[5]) : null);
            dto.setCreatedBy(row[6] != null ? String.valueOf(row[6]) : null);
            dto.setIssueDate(row[7] != null ? ((Date) row[7]) : null);
            dto.setIssuedBy(row[8] != null ? ((Number) row[8]).intValue() : null);

            // Parse Materials JSON Safely
            try {
                String materialsJson = (String) row[9];
                if (materialsJson != null && !materialsJson.isEmpty()) {
                    List<DemandMaterialsDto> materials = mapper.readValue(
                            materialsJson,
                            new TypeReference<List<DemandMaterialsDto>>() {}
                    );
                    dto.setMaterialDtos(materials);
                } else {
                    dto.setMaterialDtos(new ArrayList<>());
                }
            } catch (Exception e) {
                dto.setMaterialDtos(new ArrayList<>());
            }

            reports.add(dto);
        }

        return reports;
    }





}
