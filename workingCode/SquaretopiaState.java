package partition;

/**
 * Joshua Mariz
 * Maze Pathfinding representation of a given state, i.e., an occupiable position
 * in the given maze.
 */
public class SquaretopiaState {
    
    public int row; 
    public int col;
    public int districtNumber;
    public boolean checked;
    
    /**
     * Constructs a new MazeState, which tracks the given row and column that it
     * represents in the Maze.<br>
     * <b>NOTE: Row 0, Column 0 is located at the upper-left-hand corner of the maze!</b>
     * @param col Integer column number of this state (X coord in a Cartesian plane)
     * @param row Integer row number of this state (Y coord in a Cartesian plane)
     */
    SquaretopiaState (int row, int col) {
        this.row = row;
        this.col = col;
        this.districtNumber = 0;
        this.checked = false; // has this state been accounted for
        
    }
    
//    /**
//     * [Mutator] Adds the coordinates of the given other MazeState to this one's; useful
//     * for computing offsets given in MazeProblem transitions.
//     * @param other The other MazeState to add to this one.
//     */
//    public void add (SquaretopiaState other) {
//        this.col += other.col;
//        this.row += other.row;
//    }
    
    @Override
    public boolean equals (Object other) {
        return other instanceof SquaretopiaState 
            ? this.row == ((SquaretopiaState) other).row && this.col == ((SquaretopiaState) other).col
            : false;
    }
    
    @Override
    public int hashCode () {
        return row * col;
    }
    
    @Override
    public String toString () {
        return "(" + row + ", " + col + ")";
    }
    
}