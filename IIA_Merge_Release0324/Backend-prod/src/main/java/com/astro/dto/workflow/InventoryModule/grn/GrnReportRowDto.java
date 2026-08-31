package com.astro.dto.workflow.InventoryModule.grn;

import java.math.BigDecimal;

/**
 * One row of the GRN receiving report.
 * One row = one material/consumable line on a GRN (a GRN with 3 line items → 3 rows here).
 */
public class GrnReportRowDto {

    private Integer grinId;          // GrnMasterEntity PK (grnSubProcessId)
    private String grinNo;           // formatted "INV{processId}/{subProcessId}"
    private String date;             // grn.getGrnDate(), formatted via CommonUtils
    private String itemDescription;
    private String category;         // Capital / Furniture / IT Assets / Consumables
    private String subCategory;
    private BigDecimal quantityReceived;
    private String uom;
    private String vendorName;
    private String invoiceNoAndDate;
    private String receivedBy;
    private String location;
    private String indentor;
    private String poNumber;

    public Integer getGrinId() { return grinId; }
    public void setGrinId(Integer grinId) { this.grinId = grinId; }

    public String getGrinNo() { return grinNo; }
    public void setGrinNo(String grinNo) { this.grinNo = grinNo; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public BigDecimal getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(BigDecimal quantityReceived) { this.quantityReceived = quantityReceived; }

    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getInvoiceNoAndDate() { return invoiceNoAndDate; }
    public void setInvoiceNoAndDate(String invoiceNoAndDate) { this.invoiceNoAndDate = invoiceNoAndDate; }

    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getIndentor() { return indentor; }
    public void setIndentor(String indentor) { this.indentor = indentor; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
}
