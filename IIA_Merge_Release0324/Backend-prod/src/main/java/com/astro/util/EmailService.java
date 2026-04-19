package com.astro.util;
import com.astro.dto.workflow.SubWorkflowTransitionDto;
import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.entity.ProcurementModule.IndentCreation;
import com.astro.entity.ProcurementModule.PurchaseOrder;
import com.astro.entity.ProcurementModule.ServiceOrder;
import com.astro.entity.UserMaster;
import com.astro.entity.VendorMaster;
import com.astro.entity.VendorMasterUtil;
import com.astro.entity.WorkflowTransition;
import com.astro.repository.UserMasterRepository;
import com.astro.repository.VendorMasterRepository;
import com.astro.service.TenderRequestService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
public class EmailService {
    @Autowired
    private UserMasterRepository userMasterRepository;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private VendorMasterRepository vendorMasterRepository;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    @Autowired
    private JavaMailSender mailSender;


        private static final String SENDGRID_API_KEY = ""; // API key(we can change based on email)

       /* public void sendEmail(String toEmail, String username, String password) throws IOException {
            //  Email from = new Email("udaychowdhary743@gmail.com"); // must be verified in SendGrid
            Email from = new Email("udaykirandkg@gmail.com");
            String subject = "Welcome to Our IIA";
            Email to = new Email(toEmail);

            String contentText = "Hello,\n\n" +
                    "Here are your vendor login details:\n" +
                    "Vendor Id: " + username + "\n" +
                    "Password: " + password + "\n\n" +
                    "Thanks!" +
                    "IIA Group";
            Content content = new Content("text/plain", contentText);

            Mail mail = new Mail(from, subject, to, content);
            SendGrid sg = new SendGrid(SENDGRID_API_KEY);
            Request request = new Request();

            try {
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());
                Response response = sg.api(request);
                System.out.println("Status Code: " + response.getStatusCode());
                System.out.println("Response Body: " + response.getBody());
            } catch (IOException ex) {
                throw ex;
            }

        } */
       @Async
       public void sendEmail(String toEmail, String username, String password, VendorMasterUtil vm)
               throws MessagingException {

           // Prepare Thymeleaf context
           Context ctx = new Context();
           ctx.setVariable("vendorName", vm.getVendorName());
           ctx.setVariable("vendorId", username);
           ctx.setVariable("password", password);

           // Render HTML using template
           String htmlContent = templateEngine.process("vendor-email-template", ctx);

           // Build and send email
           MimeMessage message = mailSender.createMimeMessage();
           MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

           helper.setTo(toEmail);
           helper.setSubject("Welcome to IIA – Vendor Login Details");
           helper.setText(htmlContent, true);
           helper.setFrom("sipm");

           mailSender.send(message);
       }




    @Async
    public void sendWorkflowEmail(WorkflowTransitionDto wt) throws IOException, MessagingException {
        Email from = new Email("sipm");
        String subject = "Workflow Update - Request ID: " + wt.getRequestId();
        String workFlowName = null;
        if (wt.getWorkflowId() == 1) {
            workFlowName = "Indent Workflow";
        } else if (wt.getWorkflowId() == 4) {
            workFlowName = "Tender Approver Workflow";
        } else if (wt.getWorkflowId() == 7) {
            workFlowName = "Tender Evaluator Workflow";
        } else if (wt.getWorkflowId() == 3) {
            workFlowName = "Purchase Order Workflow";
        } else if (wt.getWorkflowId() == 5) {
            workFlowName = "Service Order Workflow";
        } else if (wt.getWorkflowId() == 2) {
            workFlowName = "Contingency Purchase Workflow";
        }

        // Prepare data for template
        Context context = new Context();
        context.setVariable("requestId", wt.getRequestId());
        context.setVariable("action", wt.getAction());
        context.setVariable("status", wt.getStatus());
        context.setVariable("currentRole", wt.getCurrentRole());
        context.setVariable("nextRole", wt.getNextRole());
        context.setVariable("remarks", wt.getRemarks());
        context.setVariable("createdBy", wt.getCreatedBy());
        context.setVariable("workflowName", workFlowName);


        // If status is COMPLETED, send to creator
        String nextRole = wt.getNextRole();
        if (nextRole == null || nextRole.equalsIgnoreCase("NULL") || nextRole.isEmpty()) {
            UserMaster user = userMasterRepository.findByUserId(wt.getCreatedBy());
            context.setVariable("userName", user.getUserName());
            String userEmail = user.getEmail();
            String userBody = templateEngine.process("user-email-template", context);
         //   sendMail(from, new Email(userEmail), subject, new Content("text/html", userBody));
            String body = templateEngine.process("user-email-template", context);
            sendMailToUser(userEmail, subject, body);


        }

        String body = templateEngine.process("role-email-template", context);

        // If assigned to a specific user (e.g., Reporting Officer), send directly to them
        if (wt.getAssignedToUserId() != null) {
            UserMaster assignedUser = userMasterRepository.findByUserId(wt.getAssignedToUserId());
            if (assignedUser != null && assignedUser.getEmail() != null && !assignedUser.getEmail().isEmpty()) {
                context.setVariable("userName", assignedUser.getUserName());
                String assignedBody = templateEngine.process("role-email-template", context);
                sendMailToUser(assignedUser.getEmail(), subject, assignedBody);
                System.out.println("📧 Email sent to assigned Reporting Officer: " + assignedUser.getEmail());
            }
        }

        List<String> recipients = Arrays.asList(
                // "udaychowdhary743@gmail.com"
              //  "satish.k@iiap.res.in",
              //  "neeraj.jha@iiap.res.in",
             //   "sayee.kishan@iiap.res.in",
              //  "shruthi.mathew@iiap.res.in",
              //  "vishnu.vardhan@iiap.res.in"
        );

        sendMail(recipients, subject, body);
        //  sendMail("kudaykiran.9949@gmail.com", subject, body);


        if ("Tender Approver".equals(wt.getCurrentRole())){


        }
    }


 private void sendMailToUser(String toEmail, String subject, String htmlContent) throws MessagingException {
     MimeMessage message = mailSender.createMimeMessage();
     MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

     helper.setTo(toEmail);
     helper.setSubject(subject);
     helper.setText(htmlContent, true); // true for HTML content
     helper.setFrom("sipm"); // replace with your Gmail

     mailSender.send(message);
 }


private void sendMail(List<String> toEmails, String subject, String htmlContent) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");


    helper.setTo(toEmails.toArray(new String[0]));
    helper.setSubject(subject);
    helper.setText(htmlContent, true);
    helper.setFrom("sipm");

    mailSender.send(message);
}


    @Async
    public void sendSubWorkflowEmail(SubWorkflowTransitionDto wt) throws IOException, MessagingException {
        Email from = new Email("sipm");
        String subject = "Workflow Update - Request ID: " + wt.getRequestId();

        String templateName = "";
        if (wt.getWorkflowId() == 7) {
            templateName = "tender-evaluator-template";
        }

        // Prepare data for template
        Context context = new Context();
        context.setVariable("workflowName", "Tender Evaluator Workflow");
        context.setVariable("requestId", wt.getRequestId());
        context.setVariable("createdBy", wt.getCreatedBy());
        context.setVariable("status", wt.getStatus());
       // context.setVariable("transitionType", wt.getT);
        context.setVariable("actionOn", wt.getActionOn()); // Role performing next action

        // Render email body using Thymeleaf
        String body = templateEngine.process("tender-evaluator-email-template", context);
        Content content = new Content("text/html", body);

        // we have to toEmail base on the clent employee
      //  String toEmail = "kudaykiran.9949@gmail.com";
       // sendMail(from, new Email(toEmail), subject, content);
     //   sendMail("kudaykiran.9949@gmail.com", subject, body);
        List<String> recipients = Arrays.asList(
                // "udaychowdhary743@gmail.com"
               // "satish.k@iiap.res.in",
              //  "neeraj.jha@iiap.res.in",
              //  "sayee.kishan@iiap.res.in",
             //   "shruthi.mathew@iiap.res.in",
             //   "vishnu.vardhan@iiap.res.in"
        );

        sendMail(recipients, subject, body);


    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML
        helper.setFrom("sipm");

        mailSender.send(message);
    }

    public void sendEmailWithAttachments(String to, String subject, String htmlContent, List<File> attachments) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("sipm");

        for (File file : attachments) {
            helper.addAttachment(file.getName(), file);
        }

        mailSender.send(message);
    }

        @Async
        public void sendReminder(String approverRole, List<WorkflowTransition> transitions) {
            for (WorkflowTransition wt : transitions) {
                try {
                    sendWorkflowReminderEmail(wt);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Optionally log the failed email
                }
            }
        }

        private void sendWorkflowReminderEmail(WorkflowTransition wt) throws MessagingException {
            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("workflowName", wt.getWorkflowName());
            context.setVariable("requestId", wt.getRequestId());
            context.setVariable("createdBy", wt.getCreatedBy());
            context.setVariable("status", wt.getStatus());
            context.setVariable("nextAction", wt.getNextAction());
            context.setVariable("currentRole", wt.getCurrentRole());
            context.setVariable("nextRole", wt.getNextRole());

            String body = templateEngine.process("workflow-reminder-email-template", context);

            // Example: fetch emails by role from database
            List<String> recipients = getEmailsForRole(wt.getNextRole());

            sendRemainderMail(recipients, "Pending Workflow Approval - Request ID: " + wt.getRequestId(), body);
        }
        /*

        private List<String> getEmailsForRole(String role) {
            // Replace with actual DB call to fetch emails for this role
            if (role.equals("Reporting Officer")) {
                return Arrays.asList("udaychowdhary743@gmail.com");
            } else if (role.equals("Project Head")) {
                return Arrays.asList("satish.k@iiap.res.in");
            }
            return new ArrayList<>();
        }
*/
        private List<String> getEmailsForRole(String role) {
            // Send all reminders to this single email
            return Arrays.asList("udaychowdhary743@gmail.com");
        }

    private void sendRemainderMail(List<String> toEmails, String subject, String body) throws MessagingException {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("sipm");
            helper.setTo(toEmails.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            mailSender.send(message);
        }

  /*  @Async
    public void sendPONotifications(List<PurchaseOrder> poList) {
        for (PurchaseOrder po : poList) {
            try {
                notifyPurchaseDeptGrnIsNotGenerated(po);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyPurchaseDeptGrnIsNotGenerated(PurchaseOrder po) throws MessagingException {
        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("poId", po.getPoId());
        context.setVariable("vendorName", po.getVendorName());
        context.setVariable("projectName", po.getProjectName());
        context.setVariable("deliveryDate", po.getDeliveryDate());
        context.setVariable("totalValue", po.getTotalValueOfPo());

        // Process the Thymeleaf template (create purchase-grn-notification-template.html)
        String body = templateEngine.process("purchase-grn-notification-template", context);

        // Fetch recipients - you can hardcode or fetch from DB
        List<String> recipients = getPurchaseDeptEmails();

        // Send the email
        sendNotificationMail(recipients,
                "PO Delivery Expiring Soon & GRN Not Generated - PO: " + po.getPoId(),
                body);
    }

    // Example method to get recipients
    private List<String> getPurchaseDeptEmails() {
        return Arrays.asList("udaychowdhary743@gmail.com");
    }

    // Method to send email
    private void sendNotificationMail(List<String> toEmails, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("iiapdkg@gmail.com");
        helper.setTo(toEmails.toArray(new String[0]));
        helper.setSubject(subject);
        helper.setText(body, true); // true = HTML

        mailSender.send(message);
    }*/
  @Async
  public void sendPONotification(PurchaseOrder po, List<String> recipients) {
      try {
          notifyPurchaseDeptGrnIsNotGenerated(po, recipients);
      } catch (Exception e) {
          e.printStackTrace();
          // log the error properly instead of just stack trace
      }
  }

    private void notifyPurchaseDeptGrnIsNotGenerated(PurchaseOrder po, List<String> recipients) throws MessagingException {
        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("poId", po.getPoId());
        context.setVariable("vendorName", po.getVendorName());
        context.setVariable("projectName", po.getProjectName());
        context.setVariable("deliveryDate", po.getDeliveryDate());
        context.setVariable("totalValue", po.getTotalValueOfPo());

        // Process the Thymeleaf template
        String body = templateEngine.process("purchase-grn-notification-template", context);

        // Send email to recipients
        sendNotificationMail(
                recipients,
                "PO Delivery Expiring Soon & GRN Not Generated - PO: " + po.getPoId(),
                body
        );
    }
    private void sendNotificationMail(List<String> toEmails, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sipm");
        helper.setTo(toEmails.toArray(new String[0]));
        helper.setSubject(subject);
        helper.setText(body, true); // true = HTML

        mailSender.send(message);
    }


    //mail to employee when purchase dept assigned indent to employee
    @Async
    public void notifyEmployeeAssigned(IndentCreation indent ,String  recipients, String indentorMail) throws MessagingException {
        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("indentId", indent.getIndentId());
        context.setVariable("employeeName", indent.getEmployeeName());
        context.setVariable("projectName", indent.getProjectName());
        context.setVariable("indentorName", indent.getIndentorName());
        context.setVariable("employeeId", indent.getEmployeeId());

        // Process Thymeleaf template
        String body = templateEngine.process("employee-assignment-notification", context);

        // Send email
        sendNotificationMailToEmployee(recipients, // or employee email if you store it
                "Indent Assigned to You - ID: " + indent.getIndentId(),
                body
        );
        if (indentorMail != null && !indentorMail.isEmpty()) {
            Context indentorContext = new Context();
            indentorContext.setVariable("indentId", indent.getIndentId());
            indentorContext.setVariable("employeeName", indent.getEmployeeName());
            indentorContext.setVariable("projectName", indent.getProjectName());
            indentorContext.setVariable("indentorName", indent.getIndentorName());
            indentorContext.setVariable("employeeId", indent.getEmployeeId());

           // String indentorBody = templateEngine.process("indentor-assignment-notification", indentorContext);
            String indentorBody = templateEngine.process("employee-assignment-notification", context);

            sendNotificationMailToEmployee(
                    indentorMail,
                    "Your Indent Assigned - ID: " + indent.getIndentId(),
                    indentorBody
            );
        }
    }

    private void sendNotificationMailToEmployee(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sipm");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }




    @Async
    public void sendRejectionWorkflowEmail(List<String> emails, WorkflowTransitionDto wt) throws MessagingException {
        if (emails == null || emails.isEmpty() || wt == null) {
            return;
        }

        // Subject
        String subject = "Workflow Rejection - Request ID: " + wt.getRequestId();

        // Determine workflow name
        String workflowName = null;
        if (wt.getWorkflowId() == 1) {
            workflowName = "Indent Workflow";
        } else if (wt.getWorkflowId() == 4) {
            workflowName = "Tender Approver Workflow";
        } else if (wt.getWorkflowId() == 7) {
            workflowName = "Tender Evaluator Workflow";
        } else if (wt.getWorkflowId() == 3) {
            workflowName = "Purchase Order Workflow";
        } else if (wt.getWorkflowId() == 5) {
            workflowName = "Service Order Workflow";
        } else if (wt.getWorkflowId() == 2) {
            workflowName = "Contingency Purchase Workflow";
        }

        // Prepare template data
        Context context = new Context();
        context.setVariable("requestId", wt.getRequestId());
        context.setVariable("action", wt.getAction());
        context.setVariable("status", wt.getStatus());
        context.setVariable("currentRole", wt.getCurrentRole());
        context.setVariable("nextRole", wt.getNextRole());
        context.setVariable("remarks", wt.getRemarks());
        context.setVariable("createdBy", wt.getCreatedBy());
        context.setVariable("workflowName", wt.getWorkflowName());
        context.setVariable("modifiedBy", wt.getModifiedBy());

        // Prepare email body using rejection-specific template
        String body = templateEngine.process("rejection-email-template", context);
           System.out.println(emails);
        // Send email to all recipients
        sendMail(emails, subject, body);
    }
    private void sendRejectionMail(List<String> toEmails, String subject, String htmlContent) throws MessagingException {
        for (String email : toEmails) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("sipm");

            mailSender.send(message);
        }
    }


    public void sendAMCNotification(ServiceOrder so, List<String> recipients) throws MessagingException {
        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("soId", so.getSoId());
        context.setVariable("vendorName", so.getVendorName());
        context.setVariable("projectName", so.getProjectName());
        context.setVariable("startDateAmc", so.getStartDateAmc());
        context.setVariable("endDateAmc", so.getEndDateAmc());
        context.setVariable("totalValue", so.getTotalValueOfSo());

        // Process the Thymeleaf template
        String body = templateEngine.process("amc-expiry-notification-template", context);

        // Send email to recipients
        sendNotificationMail(
                recipients,
                "AMC Expiry Reminder - SO: " + so.getSoId(),
                body
        );
    }

       @Async
       public void sendGtReciverEmail(List<String> toEmails, String subject, String templateName, Context context) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                String body = templateEngine.process(templateName, context);

                helper.setTo(toEmails.toArray(new String[0]));
                helper.setSubject(subject);
                helper.setText(body, true); // HTML content
                helper.setFrom("sipm");

                mailSender.send(message);
                System.out.println("Email sent successfully to " + toEmails);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

    }
    @Async
    public void sendGtReciverEmail(String toEmail, String subject, String templateName, Context context) {
        sendGtReciverEmail(List.of(toEmail).toString(), subject, templateName, context);
    }
    @Async
    public void sendGtReciverRejectedEmail(List<String> recipients, String subject, String templateName, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Process the template
            String body = templateEngine.process(templateName, context);

            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(body, true); // HTML content
            helper.setFrom("sipm");

            mailSender.send(message);
            System.out.println("Email sent successfully to " + recipients);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendAutoApprovalNotification(WorkflowTransition wt, String approverRole, Integer autoApproveHours) {
        try {
            Context context = new Context();
            context.setVariable("requestId", wt.getRequestId());
            context.setVariable("approverRole", approverRole);
            context.setVariable("autoApproveHours", autoApproveHours);
            context.setVariable("workflowName", wt.getWorkflowName());
            context.setVariable("nextRole", wt.getNextRole());
            context.setVariable("status", wt.getStatus());

            String body = templateEngine.process("auto-approval-email-template", context);

            List<String> recipients = Arrays.asList("udaychowdhary743@gmail.com");

            sendMail(recipients,
                    "Auto-Approved: Request " + wt.getRequestId() + " - " + approverRole + " did not act within " + autoApproveHours + " hours",
                    body);

            if (wt.getCreatedBy() != null) {
                UserMaster creator = userMasterRepository.findByUserId(wt.getCreatedBy());
                if (creator != null && creator.getEmail() != null) {
                    sendMailToUser(creator.getEmail(),
                            "Auto-Approved: Your Request " + wt.getRequestId(),
                            body);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendGiMails(String emailId,
                            String custodianName,
                            Integer custodianId,
                            String inspectionSubProcessId,
                            String gprnProcessId,
                            String status) throws MessagingException {


        String statusMessage;

        switch (status) {
            case "APPROVED":
                statusMessage = "Your goods inspection has been approved.";
                break;
            case "REJECTED":
                statusMessage = "Your goods inspection has been rejected.";
                break;
            case "CHANGE REQUEST":
                statusMessage = "A change request has been raised for your goods inspection.";
                break;
            default:
                statusMessage = "Your goods inspection status has been updated.";
        }


        Context ctx = new Context();
        ctx.setVariable("custodianName", custodianName);
        ctx.setVariable("custodianId", custodianId);
        ctx.setVariable("inspectionId", inspectionSubProcessId);
        ctx.setVariable("gprnProcessId", gprnProcessId);
        ctx.setVariable("status", status);
        ctx.setVariable("statusMessage", statusMessage);

        String htmlContent = templateEngine.process("gi-email-template", ctx);


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(emailId);
        helper.setSubject("GI Inspection Status Update – IIA");
        helper.setText(htmlContent, true);
        helper.setFrom("sipm");


        mailSender.send(message);
    }

}





