package com.company.usermicroservice.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TransactionIdUtil {

    public static String generate() {
        return "TX" +
                LocalTime.now()
                        .format(DateTimeFormatter.ofPattern("HHmmssSSS"));
    }
}
