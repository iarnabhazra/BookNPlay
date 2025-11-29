package com.booknplay.payment.service;

import com.booknplay.payment.domain.Payment;
import com.booknplay.payment.domain.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;

@Service
public class PaymentProcessingService {

    private final PaymentRepository repository;
    private final PricingService pricingService;

    private final ExecutorService paymentExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public PaymentProcessingService(PaymentRepository repository, PricingService pricingService) {
        this.repository = repository;
        this.pricingService = pricingService;
    }

    @Transactional
    public CompletableFuture<Payment> initiate(String bookingRef, BigDecimal baseAmount, int demandFactor, ZonedDateTime slot) {
        return CompletableFuture.supplyAsync(() -> {
        Payment p = Payment.createInitiated(
            bookingRef,
            pricingService.dynamicPrice(baseAmount, demandFactor, slot),
            "USD"
        );
            repository.save(p);
            // simulate gateway call
        p.markSuccess();
            repository.save(p);
            return p;
        }, paymentExecutor);
    }

    // Batch settlement simulation
    @Scheduled(cron = "0 */10 * * * *")
    public void settleBatch() {
    List<Payment> toSettle = repository.findAll().stream().filter(p -> "SUCCESS".equals(p.getStatus())).toList();
        if (toSettle.isEmpty()) return;
        List<List<Payment>> chunks = chunk(toSettle, 20);
        CountDownLatch latch = new CountDownLatch(chunks.size());
        AtomicInteger gatewayCounter = new AtomicInteger();
        for (List<Payment> chunk : chunks) {
            paymentExecutor.submit(() -> {
                try {
                    // simulate 2 gateways per chunk
                    CompletableFuture<?> g1 = CompletableFuture.runAsync(() -> processGateway("A", chunk));
                    CompletableFuture<?> g2 = CompletableFuture.runAsync(() -> processGateway("B", chunk));
                    CompletableFuture.allOf(g1, g2).join();
                    gatewayCounter.addAndGet(2);
                } finally {
                    latch.countDown();
                }
            });
        }
        try { latch.await(30, TimeUnit.SECONDS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void processGateway(String gateway, List<Payment> payments) {
        // simulate latency
        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        // metrics placeholder (Micrometer) would record timing & success counts
    }

    private static <T> List<List<T>> chunk(List<T> list, int size) {
        List<List<T>> res = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            res.add(list.subList(i, Math.min(list.size(), i + size)));
        }
        return res;
    }
}
