package com.astro.repository.InventoryModule.GiRepository;

import com.astro.entity.InventoryModule.GiMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiMasterRepository extends JpaRepository<GiMasterEntity, Integer> {

    List<GiMasterEntity> findByStatus(String status);
    
    List<GiMasterEntity> findByGprnProcessId(String gprnProcessId);
    
    Optional<GiMasterEntity> findByGprnProcessIdAndGprnSubProcessId(String gprnProcessId, Integer gprnSubProcessId);

    @Query(value = """
        SELECT 
            gm.process_id,
            gm.sub_process_id,
            gm.po_id,
            gm.location_id,
            gm.date,
            gm.challan_no,
            gm.delivery_date,
            gm.vendor_id,
            gm.field_station,
            gm.indentor_name,
            gm.supply_expected_date,
            gm.consignee_detail,
            gm.warranty_years,
            gm.project,
            gm.received_by,
            JSON_ARRAYAGG(
                JSON_OBJECT(
                    'detailId', gmd.detail_id,
                    'materialCode', gmd.material_code,
                    'materialDesc', gmd.material_desc,
                    'uomId', gmd.uom_id,
                    'receivedQuantity', gmd.received_quantity,
                    'unitPrice', gmd.unit_price,
                    'makeNo', gmd.make_no,
                    'serialNo', gmd.serial_no,
                    'modelNo', gmd.model_no,
                    'warrantyTerms', gmd.warranty_terms,
                    'note', gmd.note,
                    'photoPath', gmd.photo_path
                )
            ) as material_details,
        gm.status
        FROM 
            gprn_master gm
        LEFT JOIN 
            goods_inspection_master gim ON gm.sub_process_id = gim.gprn_sub_process_id
        JOIN 
            gprn_material_detail gmd ON gm.sub_process_id = gmd.sub_process_id
        WHERE 
            gim.inspection_sub_process_id IS NULL
        AND 
            gm.status = :status
        GROUP BY 
            gm.sub_process_id
        ORDER BY 
            gm.date DESC
        """, nativeQuery = true)
    List<Object[]> getGiStatusWise(@Param("status") String status);
    @Query(value = """
        SELECT 
            gm.process_id,
            gm.sub_process_id,
            gm.po_id,
            gm.location_id,
            gm.date,
            gm.challan_no,
            gm.delivery_date,
            gm.vendor_id,
            gm.field_station,
            gm.indentor_name,
            gm.supply_expected_date,
            gm.consignee_detail,
            gm.warranty_years,
            gm.project,
            gm.received_by,
            JSON_ARRAYAGG(
                JSON_OBJECT(
                    'detailId', gmd.detail_id,
                    'materialCode', gmd.material_code,
                    'materialDesc', gmd.material_desc,
                    'uomId', gmd.uom_id,
                    'receivedQuantity', gmd.received_quantity,
                    'unitPrice', gmd.unit_price,
                    'makeNo', gmd.make_no,
                    'serialNo', gmd.serial_no,
                    'modelNo', gmd.model_no,
                    'warrantyTerms', gmd.warranty_terms,
                    'note', gmd.note,
                    'photoPath', gmd.photo_path
                )
            ) as material_details,
        gm.status
        FROM 
            gprn_master gm
        LEFT JOIN 
            goods_inspection_master gim ON gm.sub_process_id = gim.gprn_sub_process_id
        JOIN 
            gprn_material_detail gmd ON gm.sub_process_id = gmd.sub_process_id
        WHERE 
            gim.inspection_sub_process_id IS NULL
        AND 
            gm.status = :status
        AND
            gm.created_by = :createdBy
        GROUP BY 
            gm.sub_process_id
        ORDER BY 
            gm.date DESC
        """, nativeQuery = true)
    List<Object[]> getGiStatusWiseAndCreatedBy(@Param("status") String status, @Param("createdBy") String createdBy);

    List<GiMasterEntity> findByStatusIn(List<String> statuses);

    Optional<GiMasterEntity> findByGprnSubProcessId(Integer subProcessId);

    Optional<GiMasterEntity> findByGprnProcessIdAndInspectionSubProcessId(String processId, Integer subProcessId);

    Optional<GiMasterEntity> findByInspectionSubProcessId(Integer giSubProcessId);
}