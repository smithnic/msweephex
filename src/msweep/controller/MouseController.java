/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msweep.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.SwingUtilities;
import msweep.model.MineField;
import msweep.model.MineParameters;
import msweep.view.Board;
import util.Utils;
import util.Utils.LoggerLevel;

/**
 * Provides the Controller operations for the game.
 * @author Nick Smith
 */
public class MouseController implements MouseListener, MouseMotionListener {

    // Contains both the board and minefield to access their public methods.
    private Board board;
    private MineField minefield;
    
    /**
     * Construct a new mouse controller.
     */
    public MouseController() {}
    
    /**
     * Set this controller's corresponding view.
     * @param b 
     */
    public void addBoard(Board b) {
        this.board = b;
    }
    
    /**
     * Set this controller's corresponding model.
     * @param mf 
     */
    public void addMineField(MineField mf) {
        this.minefield = mf;
    }
    
    /**
     * Reset the model (construct a new one), and fix up the observable/observer
     * relationships.
     */
    private void reset() {
        Utils.log("Generating and linking a new MineField", LoggerLevel.MEDIUM);
        this.minefield.deleteObservers();
        this.minefield = new MineField(MineParameters.FIELD_SIZE);
        this.board.setInitialMineField(this.minefield);
        this.minefield.addObserver(this.board);
        this.minefield.update();
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        Utils.log("Mouse pressed", LoggerLevel.LOW);
        Point coords = this.board.getCoords(me.getX(), me.getY());
        if (coords != null) {
            // Highlight the selected tile
            Utils.log("Tile found: x " + coords.x + " y " + coords.y, LoggerLevel.LOW);
            switch (me.getButton()) {
                // Left click
                case MouseEvent.BUTTON1:
                    this.board.highlightFlip(coords.x, coords.y);
                    this.minefield.update();
                    break;
                // Right click
                case MouseEvent.BUTTON3:
                    this.board.highlightFlag(coords.x, coords.y);
                    this.minefield.update();
                    break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Utils.log("Mouse released", LoggerLevel.LOW);
        // Turn mouse coords into coords corresponding to the model.
        this.board.removeHighlight();
        this.minefield.update();
        Point coords = this.board.getCoords(me.getX(), me.getY());
        if (coords != null) {
            // Mouse was released on a tile
            switch (me.getButton()) {
                // Left click
                case MouseEvent.BUTTON1:
                    this.minefield.flip(coords.x, coords.y);
                    // Check if we lost, and handle
                    switch (this.minefield.getState()) {
                        case LOST:
                            this.board.lostPopup();
                            Utils.log("Game lost popup closed", LoggerLevel.LOW);
                            this.reset();
                            break;
                        default:
                            break;
                    }
                    break;
                // Right click
                case MouseEvent.BUTTON3:
                    this.minefield.flag(coords.x, coords.y);
                    switch (this.minefield.getState()) {
                        // Check if we won and handle
                        case WON:
                            this.board.wonPopup();
                            Utils.log("Game won popup closed", LoggerLevel.LOW);
                            this.reset();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    // Nothing to do for the rest of the mouse buttons.
                    break;
            }
            
        }
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point coords = this.board.getCoords(me.getX(), me.getY());
        if (coords != null) {
            if (this.board.hasHighlight()) {
                this.board.setHighlightPoint(coords.x, coords.y);
                this.minefield.update();
            }
            else {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    this.board.highlightFlip(coords.x, coords.y);
                    this.minefield.update();
                }
                else if (SwingUtilities.isRightMouseButton(me)) {
                    this.board.highlightFlag(coords.x, coords.y);
                    this.minefield.update();
                }
            }
        }
        else if (this.board.hasHighlight()) {
            this.board.removeHighlight();
            this.minefield.update();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        Point coords = this.board.getCoords(me.getX(), me.getY());
        if (coords != null) {
            this.board.highlightHover(coords.x, coords.y);
            this.minefield.update();
        }
        else if (this.board.hasHighlight()) {
            this.board.removeHighlight();
            this.minefield.update();
        }
    }
    
}
