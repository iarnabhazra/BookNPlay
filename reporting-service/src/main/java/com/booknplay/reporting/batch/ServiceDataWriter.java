package com.booknplay.reporting.batch;

import com.booknplay.reporting.model.ReportData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ItemWriter that writes processed report data to output destinations
 * (file, database, external API, etc.)
 */
@Component
public class ServiceDataWriter implements ItemWriter<ReportData> {
    
    private static final Logger log = LoggerFactory.getLogger(ServiceDataWriter.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void write(Chunk<? extends ReportData> chunk) throws Exception {
        List<? extends ReportData> items = chunk.getItems();
        
        if (items.isEmpty()) {
            log.info("No items to write");
            return;
        }
        
        log.info("Writing {} report entries", items.size());
        
        // Write to log for demonstration
        for (ReportData item : items) {
            log.info("REPORT: Service={}, Status={}, Time={}, Info={}", 
                    item.getServiceName(), 
                    item.getHealthStatus(),
                    item.getTimestamp().format(FORMATTER),
                    item.getAdditionalInfo());
        }
        
        // Write to file (optional - for persistent reporting)
        try {
            writeToFile(items);
        } catch (IOException e) {
            log.error("Failed to write report to file", e);
            throw e; // Re-throw to trigger batch job failure
        }
    }
    
    private void writeToFile(List<? extends ReportData> items) throws IOException {
        String fileName = "daily-report-" + System.currentTimeMillis() + ".csv";
        
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("ServiceName,HealthStatus,Timestamp,AdditionalInfo,RecordCount\n");
            
            for (ReportData item : items) {
                writer.write(String.format("%s,%s,%s,%s,%d\n",
                        item.getServiceName(),
                        item.getHealthStatus(),
                        item.getTimestamp().format(FORMATTER),
                        item.getAdditionalInfo(),
                        item.getRecordCount()
                ));
            }
        }
        
        log.info("Report written to file: {}", fileName);
    }
}
