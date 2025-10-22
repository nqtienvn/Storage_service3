package com.tien.storageservice_3.service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateStringToInstantService {
    public static Instant getInstant(String date) {
        if (date != null && !date.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
            return localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        }
        return null;
    }
}
