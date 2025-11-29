package com.booknplay.payment.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;

@Service
public class PricingService {

    private final HolidayCalendar holidayCalendar;

    public PricingService(HolidayCalendar holidayCalendar) {
        this.holidayCalendar = holidayCalendar;
    }

    public BigDecimal dynamicPrice(BigDecimal base, int demandFactor, ZonedDateTime slot) {
        BigDecimal price = base.multiply(BigDecimal.valueOf(1 + (demandFactor / 100.0))); // demand uplift
        DayOfWeek dow = slot.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            price = price.multiply(BigDecimal.valueOf(1.15)); // weekend premium
        }
        if (holidayCalendar.isHoliday(slot.toLocalDate())) {
            price = price.multiply(BigDecimal.valueOf(1.20)); // holiday premium
        }
    return price.setScale(2, RoundingMode.HALF_UP);
    }
}
