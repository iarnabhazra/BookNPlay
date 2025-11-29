package com.booknplay.notification.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender mailSender;
    private final MeterRegistry meterRegistry;

    private final io.micrometer.core.instrument.Counter onboardingCounter;
    private final io.micrometer.core.instrument.Counter bookingCounter;

    public NotificationListener(SimpMessagingTemplate messagingTemplate, JavaMailSender mailSender, MeterRegistry meterRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.mailSender = mailSender;
        this.meterRegistry = meterRegistry;
        this.onboardingCounter = meterRegistry.counter("notification.events", "type", "onboarding");
        this.bookingCounter = meterRegistry.counter("notification.events", "type", "booking");
    }

    @KafkaListener(topics = {"onboarding-events"}, groupId = "notification")
    public void onboarding(String event) {
        messagingTemplate.convertAndSend("/topic/onboarding", event);
        onboardingCounter.increment();
    }

    @KafkaListener(topics = {"booking-events"}, groupId = "notification")
    public void booking(String event) {
        messagingTemplate.convertAndSend("/topic/booking", event);
        bookingCounter.increment();
        // demo email (would lookup recipient)
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("demo@example.com");
        msg.setSubject("Booking Update");
        msg.setText("Event: " + event);
        try { mailSender.send(msg); } catch (Exception ignored) {}
    }
}
