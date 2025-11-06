package com.demo.updating_brain.tools.datetime;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

public class DatetimeTools {

    public DatetimeTools() {}

    @Tool(description = "Get the current date and time in user's timezone")
    public static String getCurrentDateTime() {
        System.out.println("requested today's date");
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }
}
