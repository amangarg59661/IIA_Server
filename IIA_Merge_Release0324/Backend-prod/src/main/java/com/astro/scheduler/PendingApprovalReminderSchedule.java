package com.astro.scheduler;

import com.astro.entity.ProcurementModule.PurchaseOrder;
import com.astro.entity.ProcurementModule.ServiceOrder;
import com.astro.entity.UserMaster;
import com.astro.entity.WorkflowTransition;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;
import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository;
import com.astro.repository.UserMasterRepository;
import com.astro.repository.WorkflowTransitionRepository;
import com.astro.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PendingApprovalReminderSchedule {
    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;
    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private EmailService emailService; // or internal notification service


    // Run every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
   // @Scheduled(cron = "0 40 11 * * ?")
    public void sendPendingApprovalReminders() {
        // Calculate the threshold date (3 days ago)
        LocalDateTime thresholdLdt = LocalDateTime.now().minusDays(3);
        Date thresholdDate = Date.from(thresholdLdt.atZone(ZoneId.systemDefault()).toInstant());
        // Fetch pending workflow transitions older than 3 days
        List<WorkflowTransition> pendingTransitions = workflowTransitionRepository
                .findPendingOlderThan(thresholdDate);

        // Group by nextRole or approver
        Map<String, List<WorkflowTransition>> groupedByApprover = pendingTransitions.stream()
                .collect(Collectors.groupingBy(t -> t.getNextRole() != null ? t.getNextRole() : "UNKNOWN"));

        // Send reminders
        groupedByApprover.forEach((approverRole, transitions) -> {
            emailService.sendReminder(approverRole, transitions);
        });
    }


//Notifications to the Purchase Department for purchase orders where the delivery period is expiring within a week and Provisional GRN has not yet been generated.
  //  @Scheduled(cron = "0 0 9 * * ?")
    //@Scheduled(cron = "0 24 11 * * ?")
   // @Scheduled(cron = "0 8 13 * * ?")
    public void sendExpiringPONotifications() {
        LocalDate endDate = LocalDate.now().plusWeeks(1); // Next 7 days
        List<PurchaseOrder> expiringPOs = purchaseOrderRepository.findPOsExpiringWithoutGRN(endDate);

        for (PurchaseOrder po : expiringPOs) {
            List<String> recipients = new ArrayList<>();

            // Get user details (PO creator) from UserMaster using createdBy
            UserMaster user = userMasterRepository.findById(po.getCreatedBy())
                    .orElse(null);
            if (user != null && user.getEmail() != null) {
                recipients.add(user.getEmail());
            }

            // Get Store Purchase Officer by role name
          //  UserMaster storeOfficer = userMasterRepository.findByRoleName("STORE_PURCHASE_OFFICER")
              //      .orElse(null);
           // if (storeOfficer != null && storeOfficer.getEmail() != null) {
             //   recipients.add(storeOfficer.getEmail());
            //}
            // recipients.add("kudaykiran.9949@gmail.com");

            // Send notification if we have any recipients
            if (!recipients.isEmpty()) {
                emailService.sendPONotification(po, recipients);
            }
        }
    }

//Auto-alerts to the Purchase Department and the indentor, two months before AMC expiry
@Scheduled(cron = "0 23 15 * * ?")
public void sendExpiringAMCNotifications() throws MessagingException {
        LocalDate alertDate = LocalDate.now().plusMonths(2); // 2 months before expiry
        List<ServiceOrder> expiringSOs = serviceOrderRepository.findExpiringServiceOrders(alertDate);

        for (ServiceOrder so : expiringSOs) {
            List<String> recipients = new ArrayList<>();

            // Get indentor (creator of SO)
            UserMaster indentor = userMasterRepository.findById(so.getCreatedBy()).orElse(null);
            if (indentor != null && indentor.getEmail() != null) {
                recipients.add(indentor.getEmail());
            }

            // Get Purchase Department officer by role
           /* UserMaster purchaseDept = userMasterRepository.findByRoleName("PURCHASE_DEPARTMENT")
                    .orElse(null);
            if (purchaseDept != null && purchaseDept.getEmail() != null) {
                recipients.add(purchaseDept.getEmail());
            }*/

            recipients.add("udaychowdhary743@gmail.com");

            if (!recipients.isEmpty()) {
                emailService.sendAMCNotification(so, recipients);
            }
        }
    }




}
