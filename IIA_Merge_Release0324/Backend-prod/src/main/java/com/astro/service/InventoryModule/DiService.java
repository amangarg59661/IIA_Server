package com.astro.service.InventoryModule;

import com.astro.dto.workflow.InventoryModule.DemandAndIssueReportDto;
import com.astro.dto.workflow.InventoryModule.DiMasterDto;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtMasterDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DiService {
    public String createDi(DiMasterDto diMasterDto);
    public List<DiMasterDto> getPendingDi();
    public List<DiMasterDto> getPendingIssueNote();
    public void approveDi(String diId, Integer actionBy);
    public void rejectDi(String diId, Integer actionBy);
    public DiMasterDto getDiById(String diId);
    public String updateDi(String diId, DiMasterDto diMasterDto);
    public List<DemandAndIssueReportDto> getDemandAndIssueReport(String startDate, String endDate);
    }
