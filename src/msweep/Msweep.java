/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msweep;

import msweep.controller.MouseController;
import msweep.model.MineField;
import msweep.model.MineParameters;
import msweep.view.Board;

/**
 * The container for the main method.
 * @author Nick Smith
 */
public class Msweep {

    /**
     * Flag for debugging, only set if launched with {@link DEBUG_ARG}
     */
    private static boolean debug = false;
    /**
     * The command line argument for debugging.
     */
    private static final String DEBUG_ARG = "debug";
    
    /**
     * If an argument is passed which is the same as {@link DEBUG_ARG} console
     * logging and other debug options will be enabled.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Check argument for debug
        for (String s: args) {
            if (s.equals(DEBUG_ARG)) {
                debug = true;
            }
        }
        // Generate a model
        MineField mf = new MineField(MineParameters.FIELD_SIZE);
        // Generate a view
        Board brd = new Board();
        // Generate a controller
        MouseController controller = new MouseController();
        // Link the controller to model and view
        controller.addMineField(mf);
        controller.addBoard(brd);
        // Add the mouse controlling properties to the view
        brd.addController(controller);
        // Set up the view to be an observer of the model
        brd.setInitialMineField(mf);
        mf.addObserver(brd);
        // Any resetting of the model is handled by the controller after this.
    }
    
    /**
     * Check whether the debug flag has been set.
     * @return 
     */
    public static boolean getDebug() {
        return debug;
    }
    
}
