package com.booknplay.payment.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HolidayCalendar {
    private final Set<LocalDate> holidays = ConcurrentHashMap.newKeySet();

    public HolidayCalendar() {
        // seed example holidays
        holidays.add(LocalDate.of(2025,1,1));
        holidays.add(LocalDate.of(2025,12,25));
    }

    public boolean isHoliday(LocalDate date) { return holidays.contains(date); }
    public void addHoliday(LocalDate date) { holidays.add(date); }
}
