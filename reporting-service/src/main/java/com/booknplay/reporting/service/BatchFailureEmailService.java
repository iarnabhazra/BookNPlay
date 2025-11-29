package com.booknplay.reporting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for sending email notifications when batch jobs fail
 */
@Service
public class BatchFailureEmailService {
    
    private static final Logger log = LoggerFactory.getLogger(BatchFailureEmailService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final JavaMailSender mailSender;
    
    @Value("${batch.failure.email.recipients:admin@booknplay.com}")
    private String recipients;
    
    @Value("${batch.failure.email.from:noreply@booknplay.com}")
    private String fromEmail;
    
    @Value("${spring.application.name:reporting-service}")
    private String applicationName;
    
    public BatchFailureEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * Send email notification when a batch job fails
     * 
     * @param jobName Name of the failed job
     * @param failureTime When the failure occurred
     * @param failureReason Reason for the failure
     * @param stackTrace Optional stack trace
     */
    public void sendJobFailureNotification(String jobName, LocalDateTime failureTime, 
                                         String failureReason, String stackTrace) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            // Set recipients (can be comma-separated)
            String[] recipientEmails = recipients.split(",");
            message.setTo(recipientEmails);
            message.setFrom(fromEmail);
            
            // Set subject
            message.setSubject(String.format("[ALERT] Batch Job Failed: %s - %s", 
                    jobName, applicationName));
            
            // Build email body
            StringBuilder body = new StringBuilder();
            body.append("BATCH JOB FAILURE ALERT\n");
            body.append("=".repeat(50)).append("\n\n");
            body.append("Application: ").append(applicationName).append("\n");
            body.append("Job Name: ").append(jobName).append("\n");
            body.append("Failure Time: ").append(failureTime.format(DATE_FORMATTER)).append("\n");
            body.append("Failure Reason: ").append(failureReason).append("\n\n");
            
            if (stackTrace != null && !stackTrace.trim().isEmpty()) {
                body.append("Stack Trace:\n");
                body.append("-".repeat(30)).append("\n");
                body.append(stackTrace).append("\n\n");
            }
            
            body.append("Please investigate and take appropriate action.\n");
            body.append("This is an automated alert from the BooknPlay monitoring system.");
            
            message.setText(body.toString());
            
            // Send the email
            mailSender.send(message);
            
            log.info("Batch failure notification sent for job: {} to recipients: {}", 
                    jobName, recipients);
            
        } catch (Exception e) {
            log.error("Failed to send batch failure notification email for job: {}", jobName, e);
        }
    }
    
    /**
     * Send summary email with batch job statistics
     */
    public void sendJobSummaryNotification(String jobName, LocalDateTime executionTime,
                                         long duration, int itemsProcessed, List<String> warnings) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            String[] recipientEmails = recipients.split(",");
            message.setTo(recipientEmails);
            message.setFrom(fromEmail);
            
            message.setSubject(String.format("[INFO] Batch Job Summary: %s - %s", 
                    jobName, applicationName));
            
            StringBuilder body = new StringBuilder();
            body.append("BATCH JOB EXECUTION SUMMARY\n");
            body.append("=".repeat(50)).append("\n\n");
            body.append("Application: ").append(applicationName).append("\n");
            body.append("Job Name: ").append(jobName).append("\n");
            body.append("Execution Time: ").append(executionTime.format(DATE_FORMATTER)).append("\n");
            body.append("Duration: ").append(duration).append(" milliseconds\n");
            body.append("Items Processed: ").append(itemsProcessed).append("\n\n");
            
            if (warnings != null && !warnings.isEmpty()) {
                body.append("Warnings:\n");
                body.append("-".repeat(30)).append("\n");
                for (String warning : warnings) {
                    body.append("• ").append(warning).append("\n");
                }
                body.append("\n");
            }
            
            body.append("Job completed successfully with above statistics.\n");
            body.append("This is an automated report from the BooknPlay monitoring system.");
            
            message.setText(body.toString());
            mailSender.send(message);
            
            log.info("Batch job summary notification sent for job: {}", jobName);
            
        } catch (Exception e) {
            log.error("Failed to send batch job summary notification for job: {}", jobName, e);
        }
    }
}
