/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import msweep.Msweep;

/**
 * Utility class implemented for logging functionality.
 * @author Nick Smith
 */
public class Utils {
    
    public static final boolean CONSOLE_LOGGING = Msweep.getDebug();
    
    /**
     * 
     */
    public static final LoggerLevel MINIMUM_LEVEL = LoggerLevel.MEDIUM;
    
    /**
     * Logs to the console when CONSOLE_LOGGING is enabled.
     * @param s 
     */
    public static void log(String s) {
        if (CONSOLE_LOGGING) {
            System.out.println(s);
        }
    }
    
    /**
     * Logs to the console if level >= {@link MINIMUM_LEVEL}.
     * @param s
     * @param level 
     */
    public static void log(String s, LoggerLevel level) {
        if (level.value >= MINIMUM_LEVEL.value) {
            System.out.println(s);
        }
    }
    
    public enum LoggerLevel {
        LOW(0),
        MEDIUM(1),
        HIGH(2);
        
        private int value;    

        private LoggerLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
