package com.booknplay.reporting.batch;

import com.booknplay.reporting.service.BatchFailureEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JobExecutionListener that monitors batch job execution and sends email notifications
 * when jobs fail or complete with warnings
 */
@Component
public class BatchJobExecutionListener implements JobExecutionListener {
    
    private static final Logger log = LoggerFactory.getLogger(BatchJobExecutionListener.class);
    
    @Autowired
    private BatchFailureEmailService emailService;
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        log.info("Starting batch job: {} with execution ID: {}", 
                jobName, jobExecution.getId());
        
        // Log job parameters if any
        if (!jobExecution.getJobParameters().getParameters().isEmpty()) {
            log.info("Job parameters: {}", jobExecution.getJobParameters().getParameters());
        }
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        LocalDateTime startTime = LocalDateTime.ofInstant(
                jobExecution.getStartTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(
                jobExecution.getEndTime().toInstant(), ZoneId.systemDefault());
        
        long duration = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
        
        log.info("Batch job completed: {} with status: {} in {} ms", 
                jobName, jobExecution.getStatus(), duration);
        
        // Check if job failed
        if (jobExecution.getStatus().isUnsuccessful()) {
            handleJobFailure(jobExecution, jobName, endTime, duration);
        } else {
            handleJobSuccess(jobExecution, jobName, endTime, duration);
        }
    }
    
    private void handleJobFailure(JobExecution jobExecution, String jobName, 
                                LocalDateTime failureTime, long duration) {
        log.error("Batch job failed: {} with exit code: {}", 
                jobName, jobExecution.getExitStatus().getExitCode());
        
        // Collect failure information
        String failureReason = jobExecution.getExitStatus().getExitDescription();
        if (failureReason == null || failureReason.trim().isEmpty()) {
            failureReason = "Job failed with status: " + jobExecution.getStatus();
        }
        
        // Collect exceptions from all step executions
        StringBuilder stackTrace = new StringBuilder();
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        
        for (StepExecution stepExecution : stepExecutions) {
            if (stepExecution.getStatus().isUnsuccessful()) {
                List<Throwable> failures = stepExecution.getFailureExceptions();
                for (Throwable failure : failures) {
                    stackTrace.append("Step: ").append(stepExecution.getStepName()).append("\n");
                    stackTrace.append("Error: ").append(failure.getMessage()).append("\n");
                    
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    failure.printStackTrace(pw);
                    stackTrace.append(sw.toString()).append("\n\n");
                }
            }
        }
        
        // Send failure notification email
        emailService.sendJobFailureNotification(jobName, failureTime, 
                failureReason, stackTrace.toString());
        
        // Log detailed failure information
        log.error("Job failure details - Duration: {}ms, Reason: {}", duration, failureReason);
        if (stackTrace.length() > 0) {
            log.error("Job failure stack trace:\n{}", stackTrace);
        }
    }
    
    private void handleJobSuccess(JobExecution jobExecution, String jobName, 
                                LocalDateTime completionTime, long duration) {
        // Collect job statistics
        int totalItemsProcessed = 0;
        List<String> warnings = new ArrayList<>();
        
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        for (StepExecution stepExecution : stepExecutions) {
            totalItemsProcessed += stepExecution.getReadCount();
            
            // Check for warnings (skipped items, etc.)
            if (stepExecution.getSkipCount() > 0) {
                warnings.add(String.format("Step '%s' skipped %d items", 
                        stepExecution.getStepName(), stepExecution.getSkipCount()));
            }
            
            if (stepExecution.getProcessSkipCount() > 0) {
                warnings.add(String.format("Step '%s' had %d processing errors", 
                        stepExecution.getStepName(), stepExecution.getProcessSkipCount()));
            }
        }
        
        log.info("Job success summary - Items processed: {}, Duration: {}ms, Warnings: {}", 
                totalItemsProcessed, duration, warnings.size());
        
        // Send summary notification (optional - can be configured)
        // Uncomment the line below if you want success notifications too
        // emailService.sendJobSummaryNotification(jobName, completionTime, duration, totalItemsProcessed, warnings);
    }
}
