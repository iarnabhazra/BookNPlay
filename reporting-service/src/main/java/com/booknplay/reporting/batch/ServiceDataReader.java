package com.booknplay.reporting.batch;

import com.booknplay.reporting.model.ReportData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * ItemReader implementation that reads service names to process for daily reports
 */
@Component
public class ServiceDataReader implements ItemReader<ReportData> {
    
    private static final Logger log = LoggerFactory.getLogger(ServiceDataReader.class);
    
    private final List<String> serviceNames = Arrays.asList(
            "auth-service", 
            "booking-service", 
            "payment-service", 
            "turf-service", 
            "search-service",
            "notification-service"
    );
    
    private Iterator<String> serviceIterator;
    
    @Override
    public ReportData read() throws Exception {
        if (serviceIterator == null) {
            log.info("Initializing service data reader for {} services", serviceNames.size());
            serviceIterator = serviceNames.iterator();
        }
        
        if (serviceIterator.hasNext()) {
            String serviceName = serviceIterator.next();
            log.debug("Reading service: {}", serviceName);
            return new ReportData(serviceName);
        }
        
        log.info("Finished reading all services");
        return null; // End of data
    }
}
