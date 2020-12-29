package partition;

import java.util.HashSet;
import java.util.Set;

/**
 * Joshua Mariz
 * Squaretopia representation using an array of SquaretopiaState arrays
 */
final public class SquaretopiaMatrix {
    
    private final int M;             // number of rows
    private final int N;             // number of columns
    public final SquaretopiaState[][] data;   // M-by-N SquaretopiaState array
    
    /**
     * Sets up an M by N representation of Squaretopia using an array of SquaretopiaState arrays.
     * <b>NOTE: This method initializes the outer padding layer of the Squaretopia matrix to -1. This is so that each inner state has four adjacent neighbors.</b>
     * @param M Integer number of rows in this Squaretopia
     * @param N Integer number of columns in this Squaretopia
     * @return void
     */
    public SquaretopiaMatrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new SquaretopiaState[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                data[i][j] = new SquaretopiaState(i, j);
                if(i == 0 || i == M - 1 || j == 0 || j == N - 1) {
                    data[i][j].districtNumber = -1;
                    data[i][j].checked = true;
                }
            }
        }
    }
    
    /**
     * Prints a String representation of this Squaretopia. Prints a matrix where each number corresponds to the district number of that Squaretopia state. 
     * <b>NOTE: This method does not print the outer padding layer of -1s.</b>
     * @return void
     */
    public void show() {
        for (int i = 1; i < M - 1; i++) { // exclude outer rows of -1s
            for (int j = 1; j < N - 1; j++) { // exclude outer columns of -1s
                int districtNum = data[i][j].districtNumber;
                if(districtNum >= 0 && districtNum <= 9) {
                    System.out.print("   " + data[i][j].districtNumber);
                }
                else {
                    System.out.print("  " + data[i][j].districtNumber);
                }
            }
            System.out.print("");
            System.out.println("");
        }
    }
    
    /**
     * Helpful method for debugging. Prints a matrix containing the SquaretopiaState.checked values for each state in this Squaretopia.
     * @return void
     */
    public void printCheckedValues() {
        for (int i = 1; i < M - 1; i++) {
            for (int j = 1; j < N - 1; j++) { 
                System.out.print(" " + data[i][j].checked);
            }
            System.out.print("");
            System.out.println("");
        }
    }
    
    /**
     * Determines if this Squaretopia matrix is valid. An invalid matrix contains at least one isolated group of k Squaretopia states, 
     * where n, the number of states in each district, doesn't divide k.
     * <b>NOTE: This method assumes that this Squaretopia matrix is a square.</b>
     * @param matrix SquaretopiaMatrix whose validity we will check
     * @return Boolean value for the statement: This matrix is valid.
     */ 
    public Boolean validMap() {
        int matrixLength = data.length;
        SquaretopiaMatrix matrixCopy = duplicateMatrix();
        for(int i = 1; i < matrixLength - 1; i++) {
            for (int j = 1; j < matrixLength - 1; j++) {
                if (matrixCopy.data[i][j].checked == false) {
                    // the set below contains all the unchecked neighbors that a rook chess piece can reach when starting from the i, j location
                    Set<SquaretopiaState> neighborStates = findContiguousNeighbors(matrixCopy, matrixCopy.data[i][j]);  
                        if(neighborStates.size() % (matrixLength - 2) !=  0) {
                            return false;
                        }
                }
            } 
        } 
        return true;
    }
    
    /**
     * Creates a SquaretopiaMatrix duplicate of this Squaretopia matrix.
     * <b>NOTE: This method assumes that this Squaretopia matrix is a square.</b>
     * @param matrix SquaretopiaMatrix that we will duplicate
     * @return void
     */
    public SquaretopiaMatrix duplicateMatrix() {
        int matrixLength = data.length; // assumes matrix is a square
        SquaretopiaMatrix matrixDuplicate = new SquaretopiaMatrix(matrixLength, matrixLength);
        for(int i = 0; i < matrixLength; i++) {
            for(int j = 0; j < matrixLength; j++) {
                // we only need the checked field to determine if a completed district is valid
                matrixDuplicate.data[i][j].checked = data[i][j].checked;
            }
        }
        return matrixDuplicate;
    }
    
    /**
     * Creates a set containing all the unchecked neighbors that a rook chess piece can reach when starting from a given location in this Squaretopia.
     * @param matrix SquaretopiaMatrix that contains Squaretopia states and some district assignments
     * @param currentLocation SquaretopiaState whose contiguous unchecked neighbors we will find
     * @return Set<SquaretopiaState> containing all the unchecked neighbors that a rook chess piece can reach when starting from currentLocation
     */
    public static Set<SquaretopiaState> findContiguousNeighbors (SquaretopiaMatrix matrix, SquaretopiaState currentLocation) {
        int curLocRow = currentLocation.row;
        int curLocCol = currentLocation.col;
        Set<SquaretopiaState> contiguousStates = new HashSet<>();
        contiguousStates.add(currentLocation);
        currentLocation.checked = true;
        if(matrix.data[curLocRow - 1][curLocCol].checked == false) { // check availability of the state above
            contiguousStates.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow - 1][curLocCol]));
        }
        if(matrix.data[curLocRow + 1][curLocCol].checked == false) { // check availability of the state below
            contiguousStates.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow + 1][curLocCol]));
        }
        if(matrix.data[curLocRow][curLocCol - 1].checked == false) { // check availability of the state to the left
            contiguousStates.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow][curLocCol - 1]));
        }
        if(matrix.data[curLocRow][curLocCol + 1].checked == false) { // check availability of the state to the right
            contiguousStates.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow][curLocCol + 1]));
        }
        return contiguousStates;
    }

}
