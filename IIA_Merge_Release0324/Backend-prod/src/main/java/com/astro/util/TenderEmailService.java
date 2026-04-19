package com.astro.util;

import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.MaterialDetailsResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.PoFormateDto;
import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
import com.astro.dto.workflow.VendorDto;
import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.entity.VendorMaster;
import com.astro.repository.VendorMasterRepository;
import com.astro.service.TenderRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.thymeleaf.context.Context;

@Service
public class TenderEmailService {

        @Autowired
        private VendorMasterRepository vendorRepo;

        @Autowired
        private PdfGeneratorService pdfGeneratorService;

        @Autowired
        private TemplateEngine templateEngine;

        @Autowired
        private TenderRequestService TRService;
        @Autowired
        private VendorMasterRepository vendorMasterRepository;
        @Autowired
        private EmailService emailService; // For sendMailWithAttachments()
        @Autowired
        private JavaMailSender mailSender;



    @Async
   public void handleTenderApproverEmail(String tenderId,
                                         TenderWithIndentResponseDTO tenderData,
                                         Map<String, VendorDto> vendorMap) throws IOException {
    List<MaterialDetailsResponseDTO> allMaterials = new ArrayList<>();
        for (IndentCreationResponseDTO indent : tenderData.getIndentResponseDTO()) {
            if (indent.getMaterialDetails() != null) {
                allMaterials.addAll(indent.getMaterialDetails());
            }
        }

        for (Map.Entry<String, VendorDto> entry : vendorMap.entrySet()) {
            VendorDto vendor = entry.getValue();
            if (vendor.getEmailAddress() == null || vendor.getEmailAddress().isEmpty()) continue;


            // Create context with DTOs only
            Context con = new Context();
           /* VendorMaster vendor = new VendorMaster();
            vendor.setVendorId(vendorId);
            vendor.setEmailAddress(email); // if needed in template*/
            con.setVariable("tender", tenderData);
            con.setVariable("vendor", vendor); // vendorDTO now
            con.setVariable("materials", allMaterials);

            String html = templateEngine.process("Tender-Format", con);
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml(html);

            File pdfFile = File.createTempFile("tender-format-" + vendor.getVendorId(), ".pdf");
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(pdfBytes);
            }

            List<File> attachments = new ArrayList<>();
            attachments.add(pdfFile);
            attachments.addAll(getTenderFilesFromDTO(tenderData));
            Context vendorContext = new Context();
            vendorContext.setVariable("vendor", vendor);
            vendorContext.setVariable("tender", tenderData);
            String emailHtmlBody = templateEngine.process("vendor-tender-email-template", vendorContext);

            sendMailWithAttachments(
                    vendor.getEmailAddress(),
                    "Tender Invitation - " + tenderId,
                    emailHtmlBody,
                    attachments
            );



        }
    }
    public void sendMailWithAttachments(String toEmail, String subject, String htmlBody, List<File> attachments) throws IOException {
       try {
           MimeMessage message = mailSender.createMimeMessage();
           MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

           helper.setTo(toEmail);
           helper.setSubject(subject);
          // helper.setText(bodyText, true);
           helper.setText(htmlBody, true);
           helper.setFrom("sipm");

           for (File file : attachments) {
               helper.addAttachment(file.getName(), file);
           }

           mailSender.send(message);
           System.out.println("Vendor Email Sent to " + toEmail);
       } catch (Exception e) {
           throw new IOException("Failed to send email to " + toEmail, e);
       }
   }




    public List<File> getTenderFilesFromDTO(TenderWithIndentResponseDTO tenderDto) {
        List<File> files = new ArrayList<>();
        String basePath = "C://astro//document//Tender//";

        if (tenderDto.getUploadGeneralTermsAndConditions() != null) {
            files.addAll(resolveFilesFromField(tenderDto.getUploadGeneralTermsAndConditions(), basePath));
        }

        if (tenderDto.getUploadSpecificTermsAndConditions() != null) {
            files.addAll(resolveFilesFromField(tenderDto.getUploadSpecificTermsAndConditions(), basePath));
        }
        if (tenderDto.getUploadTenderDocuments() != null) {
            files.addAll(resolveFilesFromField(tenderDto.getUploadTenderDocuments(), basePath));
        }
        if (tenderDto.getBidSecurityDeclarationFileName() != null) {
            files.addAll(resolveFilesFromField(tenderDto.getBidSecurityDeclarationFileName(), basePath));
        }
        if (tenderDto.getMllStatusDeclarationFileName() != null) {
            files.addAll(resolveFilesFromField(tenderDto.getMllStatusDeclarationFileName(), basePath));
        }

        // Similarly handle other file also to send as attachment

        return files;
    }

    private List<File> resolveFilesFromField(String fileNamesCsv, String basePath) {
        List<File> files = new ArrayList<>();
        String[] fileNames = fileNamesCsv.split(",");

        for (String fileName : fileNames) {
            File file = new File(basePath + fileName.trim());
            if (file.exists()) {
                files.add(file);
            } else {
                System.err.println("File not found: " + file.getAbsolutePath());
            }
        }

        return files;
    }

//after po approval mail go to vendro and purchase dept
@Async
public void handlePoApproverEmail(PoFormateDto poData, String purchaseDeptEmail) throws IOException {

    // Generate HTML from PO template and convert to PDF
    Context context = new Context();
    context.setVariable("po", poData);
    byte[] logoBytes = Files.readAllBytes(Paths.get("src/main/resources/static/images/iia-logo.png"));
    String base64Logo = Base64.getEncoder().encodeToString(logoBytes);

    context.setVariable("base64Logo", base64Logo);
    String html = templateEngine.process("po-format", context);
    byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml(html);

    File pdfFile = File.createTempFile("po-format-" + poData.getPoNumber(), ".pdf");
    try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
        fos.write(pdfBytes);
    }

    List<File> attachments = new ArrayList<>();
    attachments.add(pdfFile);

    //  Send to Vendor
    if (poData.getEmail() != null && !poData.getEmail().isEmpty()) {
        Context vendorContext = new Context();
        vendorContext.setVariable("po", poData);
        String vendorEmailBody = templateEngine.process("vendor-po-email-template", vendorContext);

        sendMailWithAttachments(
                poData.getEmail(),
                "Purchase Order - " + poData.getPoNumber(),
                vendorEmailBody,
                attachments
        );
        System.out.println("PO Email Sent to Vendor: " + poData.getEmail());
    } else {
        System.err.println("Vendor email not available for PO: " + poData.getPoNumber());
    }

    // Send to Purchase Department
    if (purchaseDeptEmail != null && !purchaseDeptEmail.isEmpty()) {
        Context purchaseContext = new Context();
        purchaseContext.setVariable("po", poData);
      //  String purchaseEmailBody = templateEngine.process("purchase-dept-po-email-template", purchaseContext);
        String purchaseEmailBody = templateEngine.process("vendor-po-email-template", purchaseContext);
        //create new template tp purchase dept also

        sendMailWithAttachments(
                purchaseDeptEmail,
                "Purchase Order - " + poData.getPoNumber(),
                purchaseEmailBody,
                attachments
        );
        System.out.println("PO Email Sent to Purchase Dept: " + purchaseDeptEmail);
    } else {
        System.err.println("Purchase department email not provided for PO: " + poData.getPoNumber());
    }
}

// TC_45: Email notification to vendors when tender is amended
@Async
public void handleTenderAmendmentEmail(String tenderId,
                                       TenderWithIndentResponseDTO tenderData,
                                       String amendmentReason) throws IOException {
    // Fetch all vendors who have submitted quotations for this tender
    List<VendorMaster> vendors = vendorRepo.findVendorsByTenderId(tenderId);

    for (VendorMaster vendor : vendors) {
        if (vendor.getEmailAddress() == null || vendor.getEmailAddress().isEmpty()) continue;

        // Create email context
        Context vendorContext = new Context();
        vendorContext.setVariable("vendor", vendor);
        vendorContext.setVariable("tender", tenderData);
        vendorContext.setVariable("amendmentReason", amendmentReason);
        vendorContext.setVariable("version", tenderData.getTenderVersion());

        String emailHtmlBody = templateEngine.process("vendor-tender-amendment-email-template", vendorContext);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(vendor.getEmailAddress());
            helper.setSubject("Tender Amendment Notification - " + tenderId + " (Version " + tenderData.getTenderVersion() + ")");
            helper.setText(emailHtmlBody, true);
            helper.setFrom("sipm");

            mailSender.send(message);
            System.out.println("Tender Amendment Email Sent to Vendor: " + vendor.getEmailAddress());
        } catch (Exception e) {
            System.err.println("Failed to send amendment email to " + vendor.getEmailAddress() + ": " + e.getMessage());
        }
    }
}

// TC_51: Email notification to vendors when tender is cancelled
@Async
public void handleTenderCancellationEmail(String tenderId,
                                         TenderWithIndentResponseDTO tenderData,
                                         String cancellationReason) throws IOException {
    // Fetch all vendors who have submitted quotations for this tender
    List<VendorMaster> vendors = vendorRepo.findVendorsByTenderId(tenderId);

    for (VendorMaster vendor : vendors) {
        if (vendor.getEmailAddress() == null || vendor.getEmailAddress().isEmpty()) continue;

        // Create email context
        Context vendorContext = new Context();
        vendorContext.setVariable("vendor", vendor);
        vendorContext.setVariable("tender", tenderData);
        vendorContext.setVariable("cancellationReason", cancellationReason);

        String emailHtmlBody = templateEngine.process("vendor-tender-cancellation-email-template", vendorContext);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(vendor.getEmailAddress());
            helper.setSubject("Tender Cancellation Notification - " + tenderId);
            helper.setText(emailHtmlBody, true);
            helper.setFrom("sipm");

            mailSender.send(message);
            System.out.println("Tender Cancellation Email Sent to Vendor: " + vendor.getEmailAddress());
        } catch (Exception e) {
            System.err.println("Failed to send cancellation email to " + vendor.getEmailAddress() + ": " + e.getMessage());
        }
    }
}


}
