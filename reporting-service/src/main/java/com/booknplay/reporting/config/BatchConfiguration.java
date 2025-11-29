package com.booknplay.reporting.config;

import com.booknplay.reporting.batch.BatchJobExecutionListener;
import com.booknplay.reporting.batch.ServiceDataProcessor;
import com.booknplay.reporting.batch.ServiceDataReader;
import com.booknplay.reporting.batch.ServiceDataWriter;
import com.booknplay.reporting.model.ReportData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch configuration for daily reporting job
 */
@Configuration
public class BatchConfiguration {
    
    @Autowired
    private ServiceDataReader serviceDataReader;
    
    @Autowired
    private ServiceDataProcessor serviceDataProcessor;
    
    @Autowired
    private ServiceDataWriter serviceDataWriter;
    
    @Autowired
    private BatchJobExecutionListener batchJobExecutionListener;
    
    @Bean
    public Job dailyReportJob(JobRepository jobRepository, Step reportingStep) {
        return new JobBuilder("dailyReportJob", jobRepository)
                .listener(batchJobExecutionListener)
                .start(reportingStep)
                .build();
    }
    
    @Bean
    public Step reportingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("reportingStep", jobRepository)
                .<ReportData, ReportData>chunk(3, transactionManager) // Process in chunks of 3
                .reader(serviceDataReader)
                .processor(serviceDataProcessor)
                .writer(serviceDataWriter)
                .faultTolerant()
                .skipLimit(2) // Allow up to 2 item processing failures
                .skip(RuntimeException.class)
                .build();
    }
}
