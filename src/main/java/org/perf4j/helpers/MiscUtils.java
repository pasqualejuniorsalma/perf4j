/* Copyright (c) 2008-2009 HomeAway, Inc.
 * All rights reserved.  http://www.perf4j.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perf4j.helpers;

import org.perf4j.GroupedTimingStatistics;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Miscellaneous static utility functions, mainly having to do with String parsing/formatting.
 *
 * @author Alex Devine
 */
public final class MiscUtils {
     /* Using System.lineSeparator() is preferred in Java 7+, but we maintain compatibility.
     */
    public static final String NEWLINE = System.lineSeparator();
    
    /**
     * ISO-8601 date formatter for consistent date formatting.
     */
    private static final DateTimeFormatter ISO_8601_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MiscUtils() {
        // Utility class should not be instantiated
    }    /**
     * Escapes the specified string for use in a comma-separated values file.
     * Enhanced for better security and performance.
     *
     * @param string   The String to escape, must not be null
     * @param toAppend The StringBuilder to which the escaped String should be appended, must not be null
     * @return The StringBuilder passed in
     * @throws IllegalArgumentException if string or toAppend is null
     */
    public static StringBuilder escapeStringForCsv(String string, StringBuilder toAppend) {
        if (string == null) {
            throw new IllegalArgumentException("String to escape cannot be null");
        }
        if (toAppend == null) {
            throw new IllegalArgumentException("StringBuilder cannot be null");
        }
        
        // Always quote the string for consistency and safety
        toAppend.append('"');

        // More efficient replacement of quotes
        String escapedString = string.replace("\"", "\"\"");
        toAppend.append(escapedString);

        return toAppend.append('"');
    }    /**
     * Pads the specified int to two digits, prefixing with 0 if the value is less than 10.
     * Enhanced with validation.
     * 
     * @param i        The value to pad, should be between 0 and 99
     * @param toAppend The StringBuilder to which the padded value should be appended, must not be null
     * @return The StringBuilder passed in
     * @throws IllegalArgumentException if toAppend is null or i is out of range
     */
    public static StringBuilder padIntToTwoDigits(int i, StringBuilder toAppend) {
        if (toAppend == null) {
            throw new IllegalArgumentException("StringBuilder cannot be null");
        }
        if (i < 0 || i > 99) {
            throw new IllegalArgumentException("Value must be between 0 and 99, got: " + i);
        }
        
        if (i < 10) {
            toAppend.append('0');
        }
        return toAppend.append(i);
    }    /**
     * Formats the specified time in yyyy-MM-dd HH:mm:ss format using modern Java 8 time API.
     * Falls back to legacy Calendar API for compatibility.
     * 
     * @param timeInMillis The time in milliseconds since 1970.
     * @return The formatted date/time String
     */
    public static String formatDateIso8601(long timeInMillis) {
        try {
            // Use modern Java 8 time API when available
            ZoneId zoneId = GroupedTimingStatistics.getTimeZone().toZoneId();
            return Instant.ofEpochMilli(timeInMillis)
                    .atZone(zoneId)
                    .format(ISO_8601_FORMATTER);
        } catch (Exception e) {
            // Fallback to legacy implementation for compatibility
            return formatDateIso8601Legacy(timeInMillis);
        }
    }
    
    /**
     * Legacy date formatting using Calendar API for backward compatibility.
     * 
     * @param timeInMillis The time in milliseconds since 1970.
     * @return The formatted date/time String
     */
    private static String formatDateIso8601Legacy(long timeInMillis) {
        StringBuilder retVal = new StringBuilder(19);

        Calendar cal = Calendar.getInstance(GroupedTimingStatistics.getTimeZone());
        cal.setTimeInMillis(timeInMillis);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        retVal.append(year).append('-');
        MiscUtils.padIntToTwoDigits(month + 1, retVal).append('-');
        MiscUtils.padIntToTwoDigits(day, retVal).append(' ');
        MiscUtils.padIntToTwoDigits(hour, retVal).append(':');
        MiscUtils.padIntToTwoDigits(minute, retVal).append(':');
        return MiscUtils.padIntToTwoDigits(second, retVal).toString();
    }    /**
     * Splits a string using the specified delimiter, and also trims all the resultant strings in the returned array.
     * Enhanced with Java 8 features and better validation.
     *
     * @param stringToSplit The String to be split, must not be null
     * @param delimiter     The delimiter to use to split the string, must not be null.
     * @return The split and trimmed Strings
     * @throws IllegalArgumentException if stringToSplit or delimiter is null
     */
    public static String[] splitAndTrim(String stringToSplit, String delimiter) {
        if (stringToSplit == null) {
            throw new IllegalArgumentException("String to split cannot be null");
        }
        if (delimiter == null) {
            throw new IllegalArgumentException("Delimiter cannot be null");
        }
        
        // Use Java 8 streams for more functional approach
        return Arrays.stream(stringToSplit.split(delimiter))
                .map(String::trim)
                .toArray(String[]::new);
    }
}
