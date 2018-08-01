/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msweep.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import msweep.controller.MouseController;
import msweep.model.MineField;
import msweep.model.MineField.MineFieldState;
import util.Utils;
import util.Utils.LoggerLevel;

/**
 * The view for a hexagonal minesweeper game.
 * @author Nick Smith
 */
public class Board implements Observer {
    
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;
    
    /**
     * Scaling for Polygons, must be >= 1.
     */
    private static final int MAP_SCALE = 2;
    /**
     * Offset for X-coordinates so the map is centered.
     */
    // for size 10
    private static final int X_OFFSET = 200;
    // for size 6
    //private static final int X_OFFSET = 80;
    /**
     * Offset for Y-coordinates so the bottom tiles show in full.
     */
    // for size 10
    private static final int Y_OFFSET = 200;
    // for size 6
    //private static final int Y_OFFSET = 122; 
    
    private final JFrame frame;
    private final MyPanel contents;
    
    private boolean highlight;
    private Point highlightPoint;
    
    /**
     * Enumeration for the various types of Node highlighting.
     */
    private enum HighlightType {
        FLIP,
        FLAG,
        NONE
    }
    
    private HighlightType highlightType;
    
    /**
     * Reset every time {@link Board.update} is called.
     */
    private MineField currentMineField;
    
    public Board() {
        super();
        this.highlight = false;
        // Set frame/panel properties
        frame = new JFrame();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        contents = new MyPanel();
        contents.setVisible(true);
        frame.add(contents);
        frame.setVisible(true);
    }
    
    /**
     * Set an initial value for the minefield drawn by this view.
     * @param mf 
     */
    public void setInitialMineField(MineField mf) {
        this.currentMineField = mf;
    }
    
    /**
     * Display a pop-up for when the user blows themselves up.
     */
    public void lostPopup() {
        Utils.log("Displaying game lost popup", LoggerLevel.LOW);
        JOptionPane.showMessageDialog(frame, 
                "Continue to reset", 
                "You're dead!", 
                JOptionPane.OK_OPTION, null);
    }
    
    /**
     * Display a pop-up for when the user has successfully cleared the MineField.
     */
    public void wonPopup() {
        Utils.log("Displaying game won popup", LoggerLevel.LOW);
        JOptionPane.showMessageDialog(frame, 
                "Continue to reset", 
                "You won!", 
                JOptionPane.OK_OPTION, null);
    }

    @Override
    public void update(Observable o, Object o1) {
        if (!(o instanceof MineField)) {
            throw new IllegalStateException("Bad Observable");
        }
        this.currentMineField = (MineField) o;
        this.contents.repaint();
        
    }
    
    /**
     * Sets the flag for drawing a highlight, sets the point of
     * the highlight, and sets highlightType to {@link HighlightType.NONE}.
     * @param x
     * @param y 
     */
    public void highlightHover(int x, int y) {
        this.highlight = true;
        this.highlightType = HighlightType.NONE;
        this.highlightPoint = new Point(x, y);
    }
    
    /**
     * Sets the flag for drawing a highlight, sets the point of
     * the highlight, and sets highlightType to {@link HighlightType.FLIP}.
     * @param x
     * @param y 
     */
    public void highlightFlip(int x, int y) {
        this.highlight = true;
        this.highlightType = HighlightType.FLIP;
        this.highlightPoint = new Point(x, y);
    }
    
    /**
     * Sets the flag for drawing a highlight, sets the point of
     * the highlight, and sets highlightType to {@link HighlightType.FLAG}.
     * @param x
     * @param y 
     */
    public void highlightFlag(int x, int y) {
        this.highlight = true;
        this.highlightType = HighlightType.FLAG;
        this.highlightPoint = new Point(x, y);
    }
    
    /**
     * Removes the highlight.
     */
    public void removeHighlight() {
        this.highlight = false;
    }
    
    /**
     * Determine whether a node is highlighted.
     * @return 
     */
    public boolean hasHighlight() {
        return this.highlight;
    }
    
    /**
     * Changes highlighted point (for when the mouse is dragged, etc).
     * @param x
     * @param y 
     */
    public void setHighlightPoint(int x, int y) {
        this.highlightPoint = new Point(x, y);
    }
    
    /**
     * Draw all of the elements on the given Graphics2D object.
     * @param g2d 
     */
    private void draw(Graphics2D g2d) {
        drawTimer(g2d);
        drawHighlight(g2d);
        drawMineField(g2d);
    }
    
    /**
     * Draw the timer in the top left corner of the window.
     * @param g2d 
     */
    private void drawTimer(Graphics2D g2d) {
        if (this.currentMineField == null) return;
        if (this.currentMineField.getState() != MineFieldState.IN_PROGRESS) {
            g2d.setColor(Color.red);
        }
        g2d.drawString(this.currentMineField.getTime().toString(), 30, 30);
        g2d.setColor(Color.black);
    }
    
    /**
     * Draw the highlighted node.
     * @param g2d 
     */
    private void drawHighlight(Graphics2D g2d) {
        // Make sure the flag is set
        if (this.highlight) {
            Polygon highlighted = this.getPolygon(highlightPoint.x, highlightPoint.y);
            if (this.highlightType == HighlightType.NONE) {
                g2d.setColor(Color.yellow);
            }
            else {
                g2d.setColor(Color.orange);
            }
            g2d.fillPolygon(highlighted);
            g2d.setColor(Color.black);
            Rectangle labelContainer = highlighted.getBounds();
            switch (this.highlightType) {
                case FLIP:
                    g2d.drawString("?", 
                        (int) ((labelContainer.getMaxX() + labelContainer.getMinX()) / 2), 
                        (int) ((labelContainer.getMaxY() + labelContainer.getMinY()) / 2));
                    break;
                case FLAG:
                    g2d.drawString("!", 
                        (int) ((labelContainer.getMaxX() + labelContainer.getMinX()) / 2), 
                        (int) ((labelContainer.getMaxY() + labelContainer.getMinY()) / 2));
                    break;
                case NONE:
                    // No label here
                    break;
                default:
                    throw new IllegalStateException("Invalid highlight type found");
            }
            
            
            g2d.setColor(Color.black);
        }
    }
    
    /**
     * Draw the MineField on the given Graphics2D object.
     * @param g2d 
     */
    private void drawMineField(Graphics2D g2d) {
        if (this.currentMineField == null) return;
        Point[] tiles = this.currentMineField.getTilePoints();
        for (Point p: tiles) {
            Polygon drawnTile = getPolygon(p.x, p.y);
            Rectangle labelContainer = drawnTile.getBounds();
            String value = this.currentMineField.getTileValue(p.x, p.y);
            switch (value) {
                case "*":
                    g2d.setColor(Color.red);
                    g2d.fillPolygon(drawnTile);
                    g2d.setColor(Color.black);
                    break;
                case "!":
                    g2d.setColor(Color.pink);
                    g2d.fillPolygon(drawnTile);
                    g2d.setColor(Color.black);
                    break;
            // No fill
                case " ":
                    break;
                default:
                    int count = Integer.parseInt(value);
                    switch (count) {
                        case 0:
                            // No fill
                            g2d.setColor(Color.white);
                            g2d.fillPolygon(drawnTile);
                            break;
                        case 1:
                            g2d.setColor(new Color(191, 175, 32));
                            g2d.fillPolygon(drawnTile);
                            break;
                        case 2:
                            g2d.setColor(new Color(191, 135, 32));
                            g2d.fillPolygon(drawnTile);
                            break;
                        case 3:
                            g2d.setColor(new Color(191, 115, 32));
                            g2d.fillPolygon(drawnTile);
                            break;
                        case 4:
                            g2d.setColor(new Color(191, 100, 32));
                            g2d.fillPolygon(drawnTile);
                            break;
                        case 5:
                            g2d.setColor(new Color(191, 90, 32));
                            g2d.fillPolygon(drawnTile);
                            break;
                        case 6:
                            g2d.setColor(new Color(191, 70, 32));
                            g2d.fillPolygon(drawnTile);
                            break;
                    }   break;
            }
            g2d.setColor(Color.black);
            g2d.drawPolygon(drawnTile);
            g2d.drawString(value, 
                    (int) ((labelContainer.getMaxX() + labelContainer.getMinX()) / 2), 
                    (int) ((labelContainer.getMaxY() + labelContainer.getMinY()) / 2));
        }
    }
    
    /**
     * Get the Polygon object corresponding to the map coordinates.
     * @param xcoord
     * @param ycoord
     * @return 
     */
    private Polygon getPolygon(int xcoord, int ycoord) {
        
        // The ratios we need to get good hexes
        int baseX =  14 * xcoord;
        int baseY = (16 * ycoord - 8 * xcoord);
        // Arrays of coordinates, scaled to desired size
        int[] xs = {(baseX + 4) * MAP_SCALE, (baseX + 14) * MAP_SCALE, 
            (baseX + 18) * MAP_SCALE, (baseX + 14) * MAP_SCALE, 
            (baseX + 4) * MAP_SCALE, baseX * MAP_SCALE};
        int[] ys = {baseY * MAP_SCALE, baseY * MAP_SCALE, 
            (baseY + 8) * MAP_SCALE, (baseY + 16) * MAP_SCALE, 
            (baseY + 16) * MAP_SCALE, (baseY + 8) * MAP_SCALE};
        // Reverse ys (bottom is zero coordinate), adjust by offset
        for (int i = 0; i < ys.length; i++) {
            ys[i] = FRAME_HEIGHT - ys[i] - Y_OFFSET;
        }
        // Adjust xs by offset
        for (int i = 0; i < xs.length; i++) {
            xs[i] += X_OFFSET;
        }
        return new Polygon(xs, ys, 6);
    }
    
    /**
     * Get the corresponding MineField coordinates from mouse coordinates.
     * @param mouseX
     * @param mouseY
     * @return 
     */
    public Point getCoords(int mouseX, int mouseY) {
        // Get the set of Polygons, and figure out which one contains the point
        int xApproximation = (mouseX - X_OFFSET) / (14 * MAP_SCALE);
        int yApproximation = ((FRAME_HEIGHT - (mouseY + Y_OFFSET)) + (xApproximation * 8 * MAP_SCALE)) / (16 * MAP_SCALE);
        // Use the approximations
        for (int x = xApproximation - 1; x <= xApproximation + 1; x++) {
            for (int y = yApproximation - 1; y <= yApproximation + 1; y++) {
                if (this.currentMineField.pointInRange(x, y)) {
                    Point p = new Point(mouseX, mouseY);
                    if (getPolygon(x, y).contains(p)) {
                        return new Point(x, y);
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Add a mouse controller to this view.
     * @param controller 
     */
    public void addController(MouseController controller) {
        this.contents.addMouseListener(controller);
        this.contents.addMouseMotionListener(controller);
    }
    
    /**
     * Embedded class to represent the view panel.
     */
    class MyPanel extends JPanel {
        
        MyPanel() {
            super();
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // drawing is handled by the containing object.
            Board.this.draw(g2d);
        }
    }
}
