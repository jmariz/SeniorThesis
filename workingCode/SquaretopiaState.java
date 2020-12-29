package partition;

/**
 * Joshua Mariz
 * Squaretopia state representation of a given cell in an n by n square grid
 */
public class SquaretopiaState {
    
    public int row;
    public int col;
    public int districtNumber;
    public boolean checked; // used when checking for the validity of a district assignment
    public int direction; // relative to parent, this state is in this direction
                          // where -1 is not yet defined, 1 is above, 2 is right, 3 is below, 4 is left
    public double lowerBoundForProbability; // used in weighted partitioning
    public double upperBoundForProbability; // used in weighted partitioning
    
    /**
     * Constructs a new SquaretopiaState, which tracks the given row and column that it
     * represents in the Squaretopia.<br>
     * @param row Integer row number of this state (the top row is the first row)
     * @param col Integer column number of this state (the leftmost column is the first column)
     */
    SquaretopiaState (int row, int col) {
        this.row = row;
        this.col = col;
        this.districtNumber = 0;
        this.checked = false;
        this.direction = -1;
        
    }
    
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
