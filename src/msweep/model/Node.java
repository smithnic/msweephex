/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msweep.model;

/**
 * Represents a Node in the MineField.
 * <p> A Node holds references to it's neighbors once initialized by its
 * containing MineField. It also holds the information of whether it contains
 * a bomb or not, and how many of its neighbors contain bombs.</p>
 * @author Nick Smith
 */
public class Node {
    
    /* Values */
    
    /**
     * Whether or not this Node contains a mine.
     */
    private boolean mine;
    
    /**
     * Whether this Node has been uncovered.
     */
    private boolean flipped;
    
    /**
     * Whether this Node has been flagged.
     */
    private boolean flagged;
    
    /**
     * Count of surrounding mines in the eight adjacent Nodes.
     */
    private int count;
    
    /* Neighbor links */
    
    /**
     * Top left neighbor.
     */
    private Node topLeft;
    /**
     * Top neighbor.
     */
    private Node top;
    /**
     * Top right neighbor.
     */
    private Node topRight;
    /**
     * Bottom right neighbor.
     */
    private Node bottomRight;
    /**
     * Bottom neighbor.
     */
    private Node bottom;
    /**
     * Bottom left neighbor.
     */
    private Node bottomLeft;

    
    public Node() {
        // All links initially null and no mine/flip/flag. Zero count
        this.topLeft = null;
        this.top = null;
        this.topRight = null;
        this.bottomRight = null;
        this.bottom = null;
        this.bottomLeft = null;
        this.mine = false;
        this.flipped = false;
        this.flagged = false;
        this.count = 0;
    }
    
    /**
     * Show the contents of this node, and its neighbors if zero count.
     */
    public void flip() {
        this.flipped = true;
        if (this.count == 0) this.flipNeighbors();
    }
    
    /**
     * Toggle whether this Node is flagged (only if not yet flipped).
     */
    public void toggleFlag() {
        // Only do anything if not already flipped
        if (!this.isFlipped()) {
            this.flagged = !this.flagged;
        }
    }
    
    /**
     * Flip  neighbors
     */
    private void flipNeighbors() {
        if (this.topLeft != null && !this.topLeft.isFlipped()) {
            this.topLeft.flip();
        }
        if (this.top != null && !this.top.isFlipped()) {
            this.top.flip();
        }
        if (this.topRight != null && !this.topRight.isFlipped()) {
            this.topRight.flip();
        }
        if (this.bottomRight != null && !this.bottomRight.isFlipped()) {
            this.bottomRight.flip();
        }
        if (this.bottom != null && !this.bottom.isFlipped()) {
            this.bottom.flip();
        }
        if (this.bottomLeft != null && !this.bottomLeft.isFlipped()) {
            this.bottomLeft.flip();
        }
    }
    
    /* Setters to be used on initialization */
    
    /**
     * Set this Node to contain a mine.
     */
    public void setMine() {
        this.mine = true;
    }
    
    /**
     * Calculates and sets this Node's count following linking of neighbors.
     */
    public void setCount() {
        if (this.topLeft != null) {
            if (this.topLeft.hasMine()) count++;
        }
        if (this.top != null) {
            if (this.top.hasMine()) count++;
        }
        if (this.topRight != null) {
            if (this.topRight.hasMine()) count++;
        }
        if (this.bottomRight != null) {
            if (this.bottomRight.hasMine()) count++;
        }
        if (this.bottom != null) {
            if (this.bottom.hasMine()) count++;
        }
        if (this.bottomLeft != null) {
            if (this.bottomLeft.hasMine()) count++;
        }
    }
    
    /**
     * Link a top left neighbor.
     * @param ref 
     */
    public void setTopLeft(Node ref) {
        this.topLeft = ref;
    }
    
    /**
     * Link a top neighbor.
     * @param ref 
     */
    public void setTop(Node ref) {
        this.top = ref;
    }
    
    /**
     * Link a top right neighbor.
     * @param ref 
     */
    public void setTopRight(Node ref) {
        this.topRight = ref;
    }
    
    /**
     * Link a bottom right neighbor.
     * @param ref 
     */
    public void setBottomRight(Node ref) {
        this.bottomRight = ref;
    }
    
    /**
     * Link a bottom neighbor.
     * @param ref 
     */
    public void setBottom(Node ref) {
        this.bottom = ref;
    }
    
    /**
     * Link a bottom left neighbor.
     * @param ref 
     */
    public void setBottomLeft(Node ref) {
        this.bottomLeft = ref;
    }
    
    /* Accessors */
    
    /**
     * Determine whether this Node contains a mine.
     * @return 
     */
    public boolean hasMine() {
        return this.mine;
    }
    
    /**
     * Determine whether this Node has been flipped.
     * @return 
     */
    public boolean isFlipped() {
        return this.flipped;
    }
    
    /**
     * Determine whether this Node has been flagged.
     * @return 
     */
    public boolean isFlagged() {
        return this.flagged;
    }
    
    /**
     * Get the surrounding mine count.
     * @return 
     */
    public int getCount() {
        return this.count;
    }
}
