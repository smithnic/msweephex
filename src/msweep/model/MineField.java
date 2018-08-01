/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msweep.model;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import javax.swing.Timer;
import util.Utils;
import util.Utils.LoggerLevel;

/**
 * Represents a hexagonal mine field, and all of its related data to the game.
 * @author Nick Smith
 */
public class MineField extends Observable implements ActionListener {
    
    /**
     * Integer value representing the size of the hexagonal map.
     * <p> One indicates a single hexagon, two indicates a single hexagon
     * surrounded by a ring of hexagons, three indicates two rings, etc. </p>
     */
    private final int size;
    
    /**
     * The hexagon map is represented by a mapping of {@link java.awt.Point}
     * objects (representing hexagonal coordinates to {@link Node} objects 
     * representing the Tile itself.
     */
    private final HashMap<Point, Node> map;
    
    /**
     * Set of {@link java.awt.Point} which represent the locations within 
     * this MineField objects map which contain mines.
     */
    private Set<Point> mineLocations;
    
    /**
     * A value which defines the game state which the model currently
     * represents.
     */
    private MineFieldState gameState;
    
    /**
     * {@link javax.swing.Timer} to handle tracking time spent playing any
     * particular game.
     */
    private Timer timer;
    
    /**
     * Flag for whether the timer has yet started.
     */
    private boolean started;
    
    /**
     * Initial time + value for the time passed in seconds.
     */
    private int time;
    
    /**
     * Construct and initialize a new MineField which is a hex made of smaller ones.
     * @param size number of tiles from center to an edge (including the center).
     */
    public MineField(int size) {
        this.size = size;
        map = new HashMap<>();
        init();
    }
    
    /**
     * Flip the tile at the given location.
     * @param x
     * @param y 
     */
    public void flip(int x, int y) {
        if (!this.started) {
            // Start the timer
            this.started = true;
            this.timer.start();
        }
        if (!pointInRange(x, y)) {
            // Flip should only be called when the coords are in range
            throw new IllegalArgumentException("Coordinate out of range");
        }
        Point p = new Point(x, y);
        if (!this.map.get(p).isFlipped()) {
            if (this.map.get(p).isFlagged()) {
                // Can't be flipped until flag is removed!
                return;
            }
            Utils.log("Flipping x: " + x + " y: " + y, LoggerLevel.LOW);
            if (this.map.get(p).hasMine()) {
                // Game over
                this.mineFlipped();
            }
            this.map.get(p).flip();
            this.update();
        }
    }
    
    /**
     * Toggle the flag for the tile at the given location.
     * @param x
     * @param y 
     */
    public void flag(int x, int y) {
        if (!this.started) {
            // Start the timer
            this.started = true;
            this.timer.start();
        }
        if (!pointInRange(x, y)) {
            throw new IllegalArgumentException("Coordinate out of range");
        }
        Point p = new Point(x, y);
        if (!this.map.get(p).isFlipped()) {
            // Only do anything if it isn't flipped.
            Utils.log("Flagging x: " + x + " y: " + y, LoggerLevel.LOW);
            this.map.get(p).toggleFlag();
            checkAndHandleWin();
            this.update();
        }
    }
    
    /**
     * Update all current observers.
     */
    public void update() {
        this.setChanged();
        this.notifyObservers();
    }
    
    /**
     * Return an array of the Points in this MineField's map.
     * @return An array of {@link java.awt.Point} which contains the key set
     * of this MineField's map.
     */
    public Point[] getTilePoints() {
        Set<Point> points = this.map.keySet();
        return points.toArray(new Point[points.size()]);
    }
    
    /**
     * Return the displayed value for the tile at the given coordinates.
     * @param x
     * @param y
     * @return Textual representation of the current displayed value.
     */
    public String getTileValue(int x, int y) {
        if (!pointInRange(x, y)) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
        Point p = new Point(x, y);
        if (this.map.get(p).isFlagged()) return "!";
        if (!this.map.get(p).isFlipped()) return " ";
        if (this.map.get(p).hasMine()) {
            return "*";
        }
        else {
            return Integer.toString(this.map.get(p).getCount());
        }
    }
    
    /**
     * Checks to see if every mine tile has been flagged, and sets the state
     * to WON if so.
     */
    private void checkAndHandleWin() {
        boolean allFlagged = true;
        for (Point minePoint: this.mineLocations) {
            if (!pointInRange(minePoint.x, minePoint.y)) {
                throw new IllegalStateException("Mine locations not consistent with map");
            }
            if (!this.map.get(minePoint).isFlagged()) {
                allFlagged = false;
                break;
            }
        }
        if (allFlagged) {
            // All mines have been cleared; stop the timer and change state.
            this.timer.stop();
            this.gameState = MineFieldState.WON;
        }
    }
    
    /**
     * Handles actions to be performed when a mine Node is flipped, including
     * setting the state to LOST.
     */
    private void mineFlipped() {
        // Stop the timer
        this.timer.stop();
        // Show mine locations
        this.mineLocations.stream().forEach((_item) -> {
            if (!pointInRange(_item.x, _item.y)) {
                throw new IllegalStateException("Mine locations not consistent with map");
            }
            this.map.get(_item).flip();
        });
        // Set the appropriate game state
        this.gameState = MineFieldState.LOST;
    }
    
    /**
     * Initialize Nodes, populate their fields.
     */
    private void init() {
        int adder = size - 1;
        // Fancy loop which will generate a perfect Hex map.
        for (int x = 0; x < size + adder; x++) {
            for (int y = 0; y < size + adder; y++) {
                if (Math.abs(x - y) < this.size) {
                    this.map.put(new Point(x, y), new Node());
                }
            }
        }
        // Add mines randomly into the map
        addMines();
        // Add links between neighboring nodes
        setNeighbors();
        // Prepare the timer
        this.time = MineParameters.INITIAL_TIME;
        this.started = false;
        this.timer = new Timer(1000, this);
        this.timer.setActionCommand(MineParameters.COMMAND_INC_TIMER);
        // Set the appropriate game state.
        this.gameState = MineFieldState.IN_PROGRESS;
    }
    
    /**
     * Add mines to random Nodes.
     */
    private void addMines() {
        // Get a copy of the key set for this MineFields map
        Set<Point> allPoints = new HashSet<>();
        this.map.keySet().stream().forEach((p) -> {
            allPoints.add(p);
        });
        // Ensure we are not trying to place more Mines than possible
        if (allPoints.size() < MineParameters.NUMBER_MINES) {
            throw new IllegalStateException("Can't have more mines than nodes");
        }
        // New set representing where we are putting mines.
        Set<Point> bombPoints = new HashSet<>();
        // Choose a point in allPoints to place a mine, remove from allPoints
        // and add to bombPoints. Repeat until bombPoints has enough.
        while (bombPoints.size() < MineParameters.NUMBER_MINES) {
            int i = (int) (Math.random() * (double) allPoints.size());
            Point p = (Point) allPoints.toArray()[i];
            bombPoints.add(p);
            allPoints.remove(p);
        }
        // Save the new set
        this.mineLocations = bombPoints;
        // Add the mines
        bombPoints.stream().forEach((p) -> {
            this.addMine(p.x, p.y);
        });
    }
    
    /**
     * Populate the neighbor fields for each Node in the MineField, and then
     * calculate and set their count.
     */
    private void setNeighbors() {
        // Use a lambda function which takes a point and map over it
        this.map.keySet().stream().map((p) -> {
            int x = p.x;
            int y = p.y;
            // Top
            if (pointInRange(x, y + 1)) {
                this.map.get(p).setTop(map.get(new Point(x, y + 1)));
            }
            // Top right
            if (pointInRange(x + 1, y + 1)) {
                this.map.get(p).setTopRight(map.get(new Point(x + 1, y + 1)));
            }
            // Bottom right
            if (pointInRange(x + 1, y)) {
                this.map.get(p).setBottomRight(map.get(new Point(x + 1, y)));
            }
            // Bottom
            if (pointInRange(x, y - 1)) {
                this.map.get(p).setBottom(map.get(new Point(x, y - 1)));
            }
            // Bottom left
            if (pointInRange(x - 1, y - 1)) {
                this.map.get(p).setBottomLeft(map.get(new Point(x - 1, y - 1)));
            }
            // Top left
            if (pointInRange(x - 1, y)) {
                this.map.get(p).setTopLeft(map.get(new Point(x - 1, y)));
            }
            return p;
        }).forEach((p) -> {
            // Update count
            this.map.get(p).setCount();
        });
    }
    
    /**
     * Sets a mine at the given coordinates.
     * @param x
     * @param y 
     */
    private void addMine(int x, int y) {
        if (!pointInRange(x, y)) {
            // Must be called with a valid coordinate
            throw new IllegalArgumentException("Coordinate out of range");
        }
        this.map.get(new Point(x, y)).setMine();
    }
    
    /**
     * Increments the time and updates.
     */
    private void incrementTime() {
        this.time += 1;
        this.update();
    }
    
    /**
     * Determine whether the given map coordinates are in range.
     * @param x
     * @param y
     * @return 
     */
    public boolean pointInRange(int x, int y) {
        return this.map.containsKey(new Point(x, y));
    }
    
    /**
     * Get the current state of the MineField.
     * @return 
     */
    public MineFieldState getState() {
        return this.gameState;
    }
    
    /**
     * Access for the time passed since the game began.
     * @return {@link java.sql.Time} representation of the time since the first
     * flag/flip in this Minefield.
     */
    public Time getTime() {
        return new Time((long) (this.time) * 1000);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals(MineParameters.COMMAND_INC_TIMER)) {
            this.incrementTime();
        }
        else {
            // No other action events yet defined, something went wrong.
            throw new UnsupportedOperationException("Unsupported action event for MineField");
        }
    }
    
    /**
     * Enumeration of the three possible states of a MineField instance.
     * <ul>
     * 
     * <li>{@code IN_PROGRESS} is the state before the game has started, and
     * throughout playing until the player has either won or lost the game.</li>
     * 
     * <li>{@code WON} is the state where every {@link Node} in the map which
     * contains a mine has been flagged. It can only occur in transition 
     * from the {@code IN_PROGRESS} state and is permanent for an instance of
     * MineField.</li>
     * 
     * <li>{@code LOST} is the state where a {@link Node} containing a mine has
     * been flipped. It can only occur in transition from the {@code IN_PROGRESS}
     * state and is permanent for an instance of MineField.</li>
     * </ul>
     */
    public enum MineFieldState {
        IN_PROGRESS,
        WON,
        LOST
    }
}
