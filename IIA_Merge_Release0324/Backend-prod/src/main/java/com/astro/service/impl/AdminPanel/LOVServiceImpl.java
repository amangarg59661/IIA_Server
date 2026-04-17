package com.astro.service.impl.AdminPanel;

import com.astro.entity.AdminPanel.DesignatorMaster;
import com.astro.entity.AdminPanel.FormMaster;
import com.astro.entity.AdminPanel.LOVMaster;
import com.astro.repository.AdminPanel.DesignatorMasterRepository;
import com.astro.repository.AdminPanel.FormMasterRepository;
import com.astro.repository.AdminPanel.LOVMasterRepository;
import com.astro.service.AdminPanel.LOVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Modified by aman on 23 Feb 2026
import com.astro.service.LocationMasterService;
import com.astro.service.DepartmentComputerPriceLimitService;
import com.astro.dto.AdminPanel.LOVRequestDto;
import com.astro.service.UomMasterService;
import com.astro.service.LocatorService;
// END

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of LOV Service for managing dropdown values across all forms.
 * Includes caching for performance optimization.
 */
@Service
@Transactional
public class LOVServiceImpl implements LOVService {

     // Added by Aman
   // ========== DESIGNATOR ID CONSTANTS ==========

    private static final Long DESIGNATOR_LOCATION     = 1L;
    private static final Long DESIGNATOR_LOCATOR = 10L;
    private static final Long DESIGNATOR_DEPARTMENT   = 13L;
    private static final Long DESIGNATOR_UOM = 22L;
    
    @Autowired
    private LocationMasterService locationMasterService;

    @Autowired
private UomMasterService uomMasterService;

    @Autowired 
    private DepartmentComputerPriceLimitService departmentComputerPriceLimitService;

    @Autowired
    private LocatorService locatorService;

    // End

    @Autowired
    private FormMasterRepository formMasterRepository;

    @Autowired
    private DesignatorMasterRepository designatorMasterRepository;

    @Autowired
    private LOVMasterRepository lovMasterRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ========== FORM MASTER OPERATIONS ==========

    @Override
    @Cacheable(value = "allForms")
    public List<FormMaster> getAllForms() {
        return formMasterRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    @Cacheable(value = "activeForms")
    public List<FormMaster> getActiveForms() {
        return formMasterRepository.findByIsActiveTrue();
    }

    @Override
    @Cacheable(value = "formById", key = "#formId")
    public Optional<FormMaster> getFormById(Long formId) {
        return formMasterRepository.findById(formId);
    }

    @Override
    @Cacheable(value = "formByName", key = "#formName")
    public Optional<FormMaster> getFormByName(String formName) {
        return formMasterRepository.findByFormName(formName);
    }

    @Override
    @CacheEvict(value = {"allForms", "activeForms"}, allEntries = true)
    public FormMaster createForm(FormMaster formMaster) {
        FormMaster saved = formMasterRepository.save(formMaster);
        entityManager.flush();
        entityManager.clear();
        return saved;
    }

    @Override
    @CacheEvict(value = {"allForms", "activeForms", "formById", "formByName"}, allEntries = true)
    public FormMaster updateForm(Long formId, FormMaster formMaster) {
        Optional<FormMaster> existingForm = formMasterRepository.findById(formId);
        if (existingForm.isPresent()) {
            FormMaster form = existingForm.get();
            form.setFormName(formMaster.getFormName());
            form.setFormDisplayName(formMaster.getFormDisplayName());
            form.setFormDescription(formMaster.getFormDescription());
            form.setModuleName(formMaster.getModuleName());
            form.setIsActive(formMaster.getIsActive());
            form.setDisplayOrder(formMaster.getDisplayOrder());
            FormMaster updated = formMasterRepository.save(form);
            entityManager.flush();
            entityManager.clear();
            return updated;
        }
        throw new RuntimeException("Form not found with ID: " + formId);
    }

    // ========== DESIGNATOR MASTER OPERATIONS ==========

    @Override
    @Cacheable(value = "designatorsByFormId", key = "#formId")
    public List<DesignatorMaster> getDesignatorsByFormId(Long formId) {
        return designatorMasterRepository.findByFormIdOrderByDisplayOrderAsc(formId);
    }

    @Override
    @Cacheable(value = "activeDesignatorsByFormId", key = "#formId")
    public List<DesignatorMaster> getActiveDesignatorsByFormId(Long formId) {
        return designatorMasterRepository.findByFormIdAndIsActiveTrue(formId);
    }

    @Override
    @Cacheable(value = "designatorByFormAndName", key = "#formName + '_' + #designatorName")
    public Optional<DesignatorMaster> getDesignatorByFormAndName(String formName, String designatorName) {
        Optional<FormMaster> form = formMasterRepository.findByFormName(formName);
        if (form.isPresent()) {
            return designatorMasterRepository.findByFormIdAndDesignatorName(form.get().getFormId(), designatorName);
        }
        return Optional.empty();
    }

    @Override
    @Cacheable(value = "designatorById", key = "#designatorId")
    public Optional<DesignatorMaster> getDesignatorById(Long designatorId) {
        return designatorMasterRepository.findById(designatorId);
    }

    @Override
    @CacheEvict(value = {"designatorsByFormId", "activeDesignatorsByFormId"}, allEntries = true)
    public DesignatorMaster createDesignator(DesignatorMaster designatorMaster) {
        DesignatorMaster saved = designatorMasterRepository.save(designatorMaster);
        entityManager.flush();
        entityManager.clear();
        return saved;
    }

    @Override
    @CacheEvict(value = {"designatorsByFormId", "activeDesignatorsByFormId", "designatorById", "designatorByFormAndName"}, allEntries = true)
    public DesignatorMaster updateDesignator(Long designatorId, DesignatorMaster designatorMaster) {
        Optional<DesignatorMaster> existingDesignator = designatorMasterRepository.findById(designatorId);
        if (existingDesignator.isPresent()) {
            DesignatorMaster designator = existingDesignator.get();
            designator.setDesignatorName(designatorMaster.getDesignatorName());
            designator.setDesignatorDisplayName(designatorMaster.getDesignatorDisplayName());
            designator.setDataType(designatorMaster.getDataType());
            designator.setIsActive(designatorMaster.getIsActive());
            designator.setDisplayOrder(designatorMaster.getDisplayOrder());
            DesignatorMaster updated = designatorMasterRepository.save(designator);
            entityManager.flush();
            entityManager.clear();
            return updated;
        }
        throw new RuntimeException("Designator not found with ID: " + designatorId);
    }

    // ========== LOV MASTER OPERATIONS ==========

    @Override
    @Cacheable(value = "lovsByDesignatorId", key = "#designatorId")
    public List<LOVMaster> getLOVsByDesignatorId(Long designatorId) {
        return lovMasterRepository.findByDesignatorIdOrderByDisplayOrderAsc(designatorId);
    }

    @Override
    @Cacheable(value = "activeLovsByDesignatorId", key = "#designatorId")
    public List<LOVMaster> getActiveLOVsByDesignatorId(Long designatorId) {
        return lovMasterRepository.findByDesignatorIdAndIsActiveTrueOrderByDisplayOrderAsc(designatorId);
    }

    @Override
    @Cacheable(value = "lovsByFormAndField", key = "#formName + '_' + #fieldName")
    public List<LOVMaster> getLOVsByFormAndField(String formName, String fieldName) {
        Optional<DesignatorMaster> designator = getDesignatorByFormAndName(formName, fieldName);
        if (designator.isPresent()) {
            // TC_13 FIX: Return ALL LOVs (including inactive) so admin panel can show/manage them
            // Frontend should handle filtering/display of inactive items with visual indicators
            return lovMasterRepository.findByDesignatorIdOrderByDisplayOrderAsc(designator.get().getDesignatorId());
        }
        return new ArrayList<>();
    }

    @Override
    @Cacheable(value = "activeLovsByFormAndField", key = "#formName + '_' + #fieldName")
    public List<LOVMaster> getActiveLOVsByFormAndField(String formName, String fieldName) {
        Optional<DesignatorMaster> designator = getDesignatorByFormAndName(formName, fieldName);
        if (designator.isPresent()) {
            // Return ONLY active LOVs for frontend dropdowns
            return lovMasterRepository.findByDesignatorIdAndIsActiveTrueOrderByDisplayOrderAsc(designator.get().getDesignatorId());
        }
        return new ArrayList<>();
    }

    @Override
    @Cacheable(value = "lovById", key = "#lovId")
    public Optional<LOVMaster> getLOVById(Long lovId) {
        return lovMasterRepository.findById(lovId);
    }

    @Override
    @Cacheable(value = "dependentLovs", key = "#parentLovId")
    public List<LOVMaster> getDependentLOVs(Long parentLovId) {
        return lovMasterRepository.findByParentLovId(parentLovId);
    }

    @Override
    @Cacheable(value = "activeDependentLovs", key = "#parentLovId")
    public List<LOVMaster> getActiveDependentLOVs(Long parentLovId) {
        // Return ONLY active dependent LOVs for frontend
        return lovMasterRepository.findByParentLovId(parentLovId).stream()
                .filter(lov -> Boolean.TRUE.equals(lov.getIsActive()))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"lovsByDesignatorId", "activeLovsByDesignatorId", "lovsByFormAndField", "activeLovsByFormAndField", "allDropdownsForForm", "activeDropdownsForForm", "totalActiveLOVCount", "activeDependentLovs"}, allEntries = true)
    // modified by Aman
    public LOVMaster createLOV(LOVMaster lovMaster, LOVRequestDto request) {
    // public LOVMaster createLOV(LOVMaster lovMaster) {
    // End
        // Check for duplicate
        Optional<LOVMaster> existing = lovMasterRepository.findByDesignatorIdAndLovValue(
            lovMaster.getDesignatorId(), lovMaster.getLovValue()
        );
        if (existing.isPresent()) {
            throw new RuntimeException("LOV value already exists for this designator: " + lovMaster.getLovValue());
        }
        LOVMaster saved = lovMasterRepository.save(lovMaster);
        // Added by Aman 
        dispatchCreateHandler(saved, request);

        //  End
        entityManager.flush(); // Force immediate database write
        entityManager.clear(); // Clear persistence context to ensure fresh reads
        return saved;
    }

    @Override
    @CacheEvict(value = {"lovsByDesignatorId", "activeLovsByDesignatorId", "lovsByFormAndField", "activeLovsByFormAndField", "lovById", "allDropdownsForForm", "activeDropdownsForForm", "totalActiveLOVCount", "activeDependentLovs", "dependentLovs"}, allEntries = true)
    // Modified by Aman
    // public LOVMaster updateLOV(Long lovId, LOVMaster lovMaster) {
    public LOVMaster updateLOV(Long lovId, LOVMaster lovMaster, LOVRequestDto request){
    // End
        Optional<LOVMaster> existingLOV = lovMasterRepository.findById(lovId);
        if (existingLOV.isPresent()) {
            LOVMaster lov = existingLOV.get();
            lov.setLovValue(lovMaster.getLovValue());
            lov.setLovDisplayValue(lovMaster.getLovDisplayValue());
            lov.setLovDescription(lovMaster.getLovDescription());
            lov.setIsActive(lovMaster.getIsActive());
            lov.setIsDefault(lovMaster.getIsDefault());
            lov.setDisplayOrder(lovMaster.getDisplayOrder());
            lov.setColorCode(lovMaster.getColorCode());
            lov.setIconName(lovMaster.getIconName());
            lov.setParentLovId(lovMaster.getParentLovId());
            lov.setLocationCode(lovMaster.getLocationCode());
            LOVMaster updated = lovMasterRepository.save(lov);
            // Added by Aman
             dispatchUpdateHandler(updated, request);
            //  End
            entityManager.flush(); // Force immediate database write
            entityManager.clear(); // Clear persistence context to ensure fresh reads
            return updated;
        }
        throw new RuntimeException("LOV not found with ID: " + lovId);
    }

    @Override
    @CacheEvict(value = {"lovsByDesignatorId", "activeLovsByDesignatorId", "lovsByFormAndField", "activeLovsByFormAndField", "lovById", "allDropdownsForForm", "activeDropdownsForForm", "totalActiveLOVCount", "activeDependentLovs", "dependentLovs"}, allEntries = true)
    public void deleteLOV(Long lovId) {
        if (!lovMasterRepository.existsById(lovId)) {
            throw new RuntimeException("LOV not found with ID: " + lovId);
        }
        lovMasterRepository.deleteById(lovId);
        entityManager.flush();
        entityManager.clear();
    }

    // Added by Aman
     // ========== DESIGNATOR HANDLER DISPATCHERS ==========

    private void dispatchCreateHandler(LOVMaster saved, LOVRequestDto request) {
        if (saved.getDesignatorId() == null) return;

        if (saved.getDesignatorId().equals(DESIGNATOR_LOCATION)) {
            locationMasterService.createFromLOV(saved);
        } else if (saved.getDesignatorId().equals(DESIGNATOR_DEPARTMENT)) {
            departmentComputerPriceLimitService.createFromLOV(saved, request);
        } else if (saved.getDesignatorId().equals(DESIGNATOR_UOM)){
            uomMasterService.createFromLOV(saved);
        }
        else if (saved.getDesignatorId().equals(DESIGNATOR_LOCATOR)){
            locatorService.createFromLOV(saved, request);
        }
    }

    private void dispatchUpdateHandler(LOVMaster updated, LOVRequestDto request) {
        if (updated.getDesignatorId() == null) return;

        if (updated.getDesignatorId().equals(DESIGNATOR_LOCATION)) {
            locationMasterService.updateFromLOV(updated);
        } else if (updated.getDesignatorId().equals(DESIGNATOR_DEPARTMENT)) {
            departmentComputerPriceLimitService.updateFromLOV(updated, request);
        } else if (updated.getDesignatorId().equals(DESIGNATOR_UOM)){
            uomMasterService.updateFromLOV(updated);
        }
        else if (updated.getDesignatorId().equals(DESIGNATOR_LOCATOR)){
            locatorService.updateFromLOV(updated, request);
        }
    }
    // End

    // ========== BULK OPERATIONS ==========

    @Override
    @Cacheable(value = "allDropdownsForForm", key = "#formName")
    public Map<String, List<LOVMaster>> getAllDropdownsForForm(String formName) {
        Map<String, List<LOVMaster>> result = new HashMap<>();

        Optional<FormMaster> form = formMasterRepository.findByFormName(formName);
        if (form.isPresent()) {
            List<DesignatorMaster> designators = designatorMasterRepository.findByFormIdAndIsActiveTrue(form.get().getFormId());

            for (DesignatorMaster designator : designators) {
                // TC_13 FIX: Return all LOVs (including inactive) for admin panel
                List<LOVMaster> lovs = lovMasterRepository.findByDesignatorIdOrderByDisplayOrderAsc(designator.getDesignatorId());
                result.put(designator.getDesignatorName(), lovs);
            }
        }

        return result;
    }

    @Override
    @Cacheable(value = "activeDropdownsForForm", key = "#formName")
    public Map<String, List<LOVMaster>> getActiveDropdownsForForm(String formName) {
        Map<String, List<LOVMaster>> result = new HashMap<>();

        Optional<FormMaster> form = formMasterRepository.findByFormName(formName);
        if (form.isPresent()) {
            List<DesignatorMaster> designators = designatorMasterRepository.findByFormIdAndIsActiveTrue(form.get().getFormId());

            for (DesignatorMaster designator : designators) {
                // Return ONLY active LOVs for frontend dropdowns
                List<LOVMaster> lovs = lovMasterRepository.findByDesignatorIdAndIsActiveTrueOrderByDisplayOrderAsc(designator.getDesignatorId());
                result.put(designator.getDesignatorName(), lovs);
            }
        }

        return result;
    }

    @Override
    public Map<String, List<LOVMaster>> getBulkLOVs(List<String> formFieldPairs) {
        Map<String, List<LOVMaster>> result = new HashMap<>();

        for (String pair : formFieldPairs) {
            String[] parts = pair.split("\\.");
            if (parts.length == 2) {
                String formName = parts[0];
                String fieldName = parts[1];
                List<LOVMaster> lovs = getLOVsByFormAndField(formName, fieldName);
                result.put(pair, lovs);
            }
        }

        return result;
    }

    @Override
    public Map<String, List<LOVMaster>> getActiveBulkLOVs(List<String> formFieldPairs) {
        Map<String, List<LOVMaster>> result = new HashMap<>();

        for (String pair : formFieldPairs) {
            String[] parts = pair.split("\\.");
            if (parts.length == 2) {
                String formName = parts[0];
                String fieldName = parts[1];
                // Use active-only method for frontend
                List<LOVMaster> lovs = getActiveLOVsByFormAndField(formName, fieldName);
                result.put(pair, lovs);
            }
        }

        return result;
    }

    @Override
    @CacheEvict(value = {"lovsByDesignatorId", "activeLovsByDesignatorId", "lovsByFormAndField", "allDropdownsForForm"}, allEntries = true)
    public List<LOVMaster> bulkImportLOVs(Long designatorId, List<LOVMaster> lovValues) {
        List<LOVMaster> createdLOVs = new ArrayList<>();

        for (LOVMaster lovMaster : lovValues) {
            lovMaster.setDesignatorId(designatorId);
            // Check for duplicates
            Optional<LOVMaster> existing = lovMasterRepository.findByDesignatorIdAndLovValue(
                designatorId, lovMaster.getLovValue()
            );
            if (!existing.isPresent()) {
                createdLOVs.add(lovMasterRepository.save(lovMaster));
            }
        }

        entityManager.flush(); // Force immediate database write
        entityManager.clear(); // Clear persistence context to ensure fresh reads
        return createdLOVs;
    }

    // ========== UTILITY OPERATIONS ==========

    @Override
    public boolean lovExists(Long designatorId, String lovValue) {
        Optional<LOVMaster> existing = lovMasterRepository.findByDesignatorIdAndLovValue(designatorId, lovValue);
        return existing.isPresent();
    }

    @Override
    public Optional<LOVMaster> getDefaultLOV(Long designatorId) {
        List<LOVMaster> lovs = lovMasterRepository.findByDesignatorIdAndIsActiveTrueOrderByDisplayOrderAsc(designatorId);
        return lovs.stream()
                .filter(LOVMaster::getIsDefault)
                .findFirst();
    }

    @Override
    @CacheEvict(value = {"lovsByDesignatorId", "activeLovsByDesignatorId", "lovsByFormAndField", "allDropdownsForForm"}, allEntries = true)
    public void reorderLOVs(Long designatorId, List<Long> lovIdsInOrder) {
        for (int i = 0; i < lovIdsInOrder.size(); i++) {
            Long lovId = lovIdsInOrder.get(i);
            Optional<LOVMaster> lov = lovMasterRepository.findById(lovId);
            if (lov.isPresent()) {
                LOVMaster lovMaster = lov.get();
                lovMaster.setDisplayOrder(i + 1);
                lovMasterRepository.save(lovMaster);
            }
        }
        entityManager.flush(); // Force immediate database write
        entityManager.clear(); // Clear persistence context to ensure fresh reads
    }

    @Override
    @Cacheable(value = "totalActiveLOVCount")
    public long getTotalActiveLOVCount() {
        return lovMasterRepository.countByIsActiveTrue();
    }
}
