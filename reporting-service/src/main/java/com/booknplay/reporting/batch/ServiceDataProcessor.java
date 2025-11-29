package com.booknplay.reporting.batch;

import com.booknplay.reporting.model.ReportData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * ItemProcessor that processes service data by fetching health information
 * and enriching the ReportData with actual service status
 */
@Component
public class ServiceDataProcessor implements ItemProcessor<ReportData, ReportData> {
    
    private static final Logger log = LoggerFactory.getLogger(ServiceDataProcessor.class);
    private final HttpClient httpClient;
    
    public ServiceDataProcessor() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }
    
    @Override
    public ReportData process(ReportData item) throws Exception {
        if (item == null) {
            return null;
        }
        
        log.debug("Processing service: {}", item.getServiceName());
        
        try {
            // Simulate fetching health data from each service
            String healthEndpoint = getHealthEndpoint(item.getServiceName());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(healthEndpoint))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            item.setHealthStatus(response.statusCode() == 200 ? "UP" : "DOWN");
            item.setAdditionalInfo("Status Code: " + response.statusCode());
            item.setTimestamp(LocalDateTime.now());
            item.setRecordCount(1);
            
            log.info("Processed service: {} - Status: {}", item.getServiceName(), item.getHealthStatus());
            
            // Simulate processing failure for demonstration (remove in production)
            if ("search-service".equals(item.getServiceName())) {
                throw new RuntimeException("Simulated processing failure for search-service");
            }
            
        } catch (Exception e) {
            log.error("Failed to process service: {}", item.getServiceName(), e);
            item.setHealthStatus("ERROR");
            item.setAdditionalInfo("Error: " + e.getMessage());
            item.setTimestamp(LocalDateTime.now());
            item.setRecordCount(0);
        }
        
        return item;
    }
    
    private String getHealthEndpoint(String serviceName) {
        // Map service names to their health endpoints
        return switch (serviceName) {
            case "auth-service" -> "http://localhost:8081/actuator/health";
            case "booking-service" -> "http://localhost:8083/actuator/health";
            case "payment-service" -> "http://localhost:8084/actuator/health";
            case "turf-service" -> "http://localhost:8082/actuator/health";
            case "search-service" -> "http://localhost:8085/actuator/health";
            case "notification-service" -> "http://localhost:8087/actuator/health";
            default -> "http://localhost:8080/actuator/health";
        };
    }
}
