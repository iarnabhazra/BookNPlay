package com.booknplay.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model class representing report data processed by Spring Batch
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {
    private String serviceName;
    private String healthStatus;
    private LocalDateTime timestamp;
    private String additionalInfo;
    private int recordCount;
    
    public ReportData(String serviceName) {
        this.serviceName = serviceName;
        this.timestamp = LocalDateTime.now();
    }
}
