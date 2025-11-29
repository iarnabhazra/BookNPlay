package com.booknplay.payment;

import com.booknplay.payment.service.PricingService;
import com.booknplay.payment.service.HolidayCalendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

class PricingServiceTest {

    PricingService pricingService = new PricingService(new HolidayCalendar());

    @Test
    void weekendPremiumApplied() {
        BigDecimal base = new BigDecimal("100.00");
        ZonedDateTime saturday = ZonedDateTime.of(2025, 9, 13, 10, 0,0,0, ZoneId.of("UTC"));
        BigDecimal price = pricingService.dynamicPrice(base, 0, saturday);
        Assertions.assertTrue(price.compareTo(base) > 0, "Weekend should increase price");
    }
}
