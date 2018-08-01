/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msweep.model;

/**
 * Defines constant parameters.
 * @author Nick Smith
 */
public class MineParameters {
 
    /**
     * Size of the MineField. May make this vary in the future.
     */
    public static final int FIELD_SIZE = 10;
    
    /**
     * Number of Mines. Make a function of {@link FIELD_SIZE} to this if that
     * becomes a variable in the future.
     */
    public static final int NUMBER_MINES = 55;
    
    /**
     * {@link String} to be used by the MineField's {@link javax.swing.Timer}
     * when sending an ActionEvent to increment time.
     */
    public static final String COMMAND_INC_TIMER = "incTimer";
    
    /**
     * Initial value of the MineFields {@code time} to show hh:mm:ss as 00:00:00.
     */
    public static final int INITIAL_TIME = -1 * 60 * 60 * 19;
}
