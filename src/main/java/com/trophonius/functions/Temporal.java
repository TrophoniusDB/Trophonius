package com.trophonius.functions;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Temporal {

    public long timestamp() {
        return System.currentTimeMillis();
    }

    public LocalDate date() {
        return LocalDate.now();
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public Integer year() {
        return LocalDate.now().getYear();
    }

    public Integer year(LocalDate date) {
        return date.getYear();
    }

    public Integer month() {
        return LocalDate.now().getMonthValue();
    }

    public Integer month(LocalDate date) {
        return date.getMonthValue();
    }

    public Integer dayOfMonth() {
        return LocalDate.now().getDayOfMonth();
    }

    public Integer dayOfMonth(LocalDate date) {
        return date.getDayOfMonth();
    }

    public Integer dayOfYear() {
        return LocalDate.now().getDayOfYear();
    }

    public Integer dayOfYear(LocalDate date) {
        return date.getDayOfYear();
    }
}

