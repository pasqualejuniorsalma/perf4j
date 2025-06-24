package org.perf4j.helpers;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Utility class for secure deserialization to prevent CWE-502 vulnerabilities.
 */
public final class SerializationSecurityUtils {
    
    private SerializationSecurityUtils() {
        // Utility class should not be instantiated
    }
    
    /**
     * Safely reads a logger name from ObjectInputStream with validation.
     * This prevents potential security vulnerabilities in deserialization.
     * 
     * @param stream The ObjectInputStream to read from
     * @return The validated logger name
     * @throws IOException If there's an error reading from the stream
     * @throws SecurityException If the logger name is invalid or potentially dangerous
     */
    public static String readLoggerNameSafely(ObjectInputStream stream) throws IOException {
        String loggerName = stream.readUTF();
        
        // Validate the logger name to prevent security issues
        if (loggerName == null || loggerName.trim().isEmpty()) {
            throw new SecurityException("Logger name cannot be null or empty");
        }
        
        // Check for potentially dangerous characters or patterns
        if (loggerName.contains("..") || loggerName.contains("/") || loggerName.contains("\\")) {
            throw new SecurityException("Logger name contains potentially dangerous characters: " + loggerName);
        }
        
        // Limit the length to prevent memory exhaustion attacks
        if (loggerName.length() > 1000) {
            throw new SecurityException("Logger name too long: " + loggerName.length() + " characters");
        }
        
        return loggerName;
    }
}
