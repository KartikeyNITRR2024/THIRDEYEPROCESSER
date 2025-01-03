package com.thirdeye.processer.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class TimeManagementUtil {
	
	public Timestamp getCurrentTime() {
        ZonedDateTime indianTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime localDateTime = indianTime.toLocalDateTime();
        return Timestamp.valueOf(localDateTime);
    }
	
	public String getCurrentTimeString() {
        ZonedDateTime zonedTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return zonedTime.format(formatter);
    }
	
	public String getCurrentTimeString(Timestamp timestamp) {
        Instant instant = timestamp.toInstant();
        ZonedDateTime zonedTime = instant.atZone(ZoneId.of("Asia/Kolkata"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return zonedTime.format(formatter);
    }
	
	public long getDifferenceInMinutes(Timestamp timestamp1, Timestamp timestamp2) {
        long millisDiff = Math.abs(timestamp1.getTime() - timestamp2.getTime());
        return TimeUnit.MILLISECONDS.toMinutes(millisDiff);
    }



}
