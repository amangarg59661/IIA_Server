package com.astro.repository;

import com.astro.entity.MaterialMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialMasterRepository extends JpaRepository<MaterialMaster, String> {


    @Query("SELECT m.uom FROM MaterialMaster m WHERE m.materialCode = :materialCode")
    String findUomByMaterialCode(@Param("materialCode") String materialCode);

   /* @Query(value = "SELECT m.material_code, m.description, m.category " +
            "FROM material_master m " +
            "WHERE LOWER(m.material_code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> searchMaterialsForDropdown(@Param("keyword") String keyword);*/
   @Query(value = "SELECT m.material_code, m.description, m.category, m.sub_category, m.uom, m.unit_price, m.currency " +
           "FROM material_master m " +
           "WHERE (LOWER(m.material_code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(m.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (m.status_of_material_active_or_deactive IS NULL " +
           "   OR LOWER(TRIM(m.status_of_material_active_or_deactive)) <> 'deactive') " +
           "ORDER BY m.created_date DESC " +
           "LIMIT 20",
           nativeQuery = true)
   List<Object[]> searchMaterialsForDropdown(@Param("keyword") String keyword);

   @Query(value = "SELECT m.material_code, m.description, m.category, m.sub_category, m.uom, m.unit_price, m.currency , m.indigenous_or_imported " +
           "FROM material_master m " +
           "WHERE (LOWER(m.material_code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(m.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (m.status_of_material_active_or_deactive IS NULL " +
           "   OR LOWER(TRIM(m.status_of_material_active_or_deactive)) <> 'deactive') " +
           "AND LOWER(m.sub_category) LIKE '%computer%' " +
           "ORDER BY m.created_date DESC " +
           "LIMIT 20",
           nativeQuery = true)
   List<Object[]> searchApprovedMaterialsComputer(@Param("keyword") String keyword);

   @Query(value = "SELECT m.material_code, m.description, m.category, m.sub_category, m.uom, m.unit_price, m.currency , m.indigenous_or_imported " +
           "FROM material_master m " +
           "WHERE (LOWER(m.material_code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(m.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (m.status_of_material_active_or_deactive IS NULL " +
           "   OR LOWER(TRIM(m.status_of_material_active_or_deactive)) <> 'deactive') " +
           "AND LOWER(m.sub_category) NOT LIKE '%computer%' " +
           "ORDER BY m.created_date DESC " +
           "LIMIT 20",
           nativeQuery = true)
   List<Object[]> searchApprovedMaterialsNonComputer(@Param("keyword") String keyword);

    Optional<MaterialMaster> findById(String materialCode);

    boolean existsByCategoryAndSubCategoryAndDescriptionIgnoreCase(String category, String subCategory, String description);

    @Query(
            value = "SELECT * FROM material_master m " +
                    "WHERE m.status_of_material_active_or_deactive IS NULL " +
                    "   OR LOWER(TRIM(m.status_of_material_active_or_deactive)) <> 'deactive'",
            nativeQuery = true
    )
    List<MaterialMaster> findActiveMaterials();


}
