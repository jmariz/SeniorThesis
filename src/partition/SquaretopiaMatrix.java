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
     * Creates a set containing all the inner Squaretopia states.
     * <b>NOTE: This method assumes that all inner states are unclaimed, so it should only be called after the initialization of this Squaretopia.</b>
     * @return Set<SquaretopiaState> containing all the inner states of this Squaretopia matrix
     */
    public Set<SquaretopiaState> generateSetOfFreeStates() {
        int matrixLength = data.length; // assumes square matrix
        Set<SquaretopiaState> setOfStates = new HashSet<>();
        for(int i = 1; i < matrixLength - 1; i++) { // does not add perimeter padding states to the set 
            for(int j = 1; j < matrixLength - 1; j++) {
                setOfStates.add(new SquaretopiaState(i, j));
            }
        }
        return setOfStates;
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
    public Set<SquaretopiaState> findContiguousNeighbors (SquaretopiaMatrix matrix, SquaretopiaState currentLocation) {
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
    
    
    /**
     * Calculates the perimeter of each district in a finished Squaretopia partition.
     * @param matrix SquaretopiaMatrix that has been fully partitioned
     * @return int[] containing the perimeters of each district in the partition: with district 1's perimeter at index 0, district 2's perimeter at index 1, etc. 
     */
    public int[] calculatePerimeters() {
        int numOfDistricts = data.length - 2;
        int[] perimetersArray = new int[numOfDistricts];
        for(int i = 0; i < numOfDistricts; i++) {
            perimetersArray[i] = 4 * numOfDistricts;
        }
        for(int i = 1; i < data.length - 1; i++) {
            for(int j = 1; j < data.length - 1; j++) {
                int districtNumOfCurrentState = data[i][j].districtNumber;
                if(districtNumOfCurrentState == data[i - 1][j].districtNumber) {
                    perimetersArray[districtNumOfCurrentState - 1]--;
                }
                if(districtNumOfCurrentState == data[i + 1][j].districtNumber) {
                    perimetersArray[districtNumOfCurrentState - 1]--;
                }
                if(districtNumOfCurrentState == data[i][j - 1].districtNumber) {
                    perimetersArray[districtNumOfCurrentState - 1]--;
                }
                if(districtNumOfCurrentState == data[i][j + 1].districtNumber) {
                    perimetersArray[districtNumOfCurrentState - 1]--;
                }
            }
        }
      return perimetersArray;
  }
    
    // calculates the sum of the length-width ratios for all districts in a given Squaretopia 
    public double LengthWidthScore() {
        // locate and save extrema states
        double scoreTotal = 0;
        double numOfDistricts = data.length - 2;
        SquaretopiaState[][] extrema = new SquaretopiaState[(int) numOfDistricts][4]; // each district has four states in this order min length, max length, min width, max width
        for (int i = 1; i < data.length - 1; i++) {
            for (int j = 1; j < data.length - 1; j++) {
                int districtNumberOfThisState = data[i][j].districtNumber;
                SquaretopiaState thisState = data[i][j];
                if(extrema[districtNumberOfThisState - 1][0] == null) {
                    extrema[districtNumberOfThisState - 1][0] = thisState;
                    extrema[districtNumberOfThisState - 1][1] = thisState;
                    extrema[districtNumberOfThisState - 1][2] = thisState;
                    extrema[districtNumberOfThisState - 1][3] = thisState;
                }
                if(thisState.row < extrema[districtNumberOfThisState - 1][0].row) {
                    extrema[districtNumberOfThisState - 1][0] = thisState;
                }
                if(thisState.row > extrema[districtNumberOfThisState - 1][1].row) {
                    extrema[districtNumberOfThisState - 1][1] = thisState;
                }
                if(thisState.col < extrema[districtNumberOfThisState - 1][2].col) {
                    extrema[districtNumberOfThisState - 1][2] = thisState;
                }
                if(thisState.col > extrema[districtNumberOfThisState - 1][3].col) {
                    extrema[districtNumberOfThisState - 1][3] = thisState;
                }
            }
        }
        double length;
        double width;
        for (int i = 0; i < extrema.length; i++) {
            length = Math.abs(extrema[i][0].row - extrema[i][1].row) + 1; // add one to count properly
            width = Math.abs(extrema[i][2].col - extrema[i][3].col) + 1;
            if (length < width) {
                scoreTotal += length / width;
            } else {
                scoreTotal += width / length;
            }
        }
        double averageScore = scoreTotal / numOfDistricts;
        return averageScore;
    }
    
    // calculates the district area to minimum bounding square ratios for all districts in a given Squaretopia 
    public double Reock() {
        // locate and save extrema states
        double scoreTotal = 0;
        double numOfDistricts = data.length - 2;
        SquaretopiaState[][] extrema = new SquaretopiaState[(int) numOfDistricts][4]; // each district has four states in this order min length, max length, min width, max width
        for (int i = 1; i < data.length - 1; i++) {
            for (int j = 1; j < data.length - 1; j++) {
                int districtNumberOfThisState = data[i][j].districtNumber;
                SquaretopiaState thisState = data[i][j];
                if(extrema[districtNumberOfThisState - 1][0] == null) {
                    extrema[districtNumberOfThisState - 1][0] = thisState;
                    extrema[districtNumberOfThisState - 1][1] = thisState;
                    extrema[districtNumberOfThisState - 1][2] = thisState;
                    extrema[districtNumberOfThisState - 1][3] = thisState;
                }
                if(thisState.row < extrema[districtNumberOfThisState - 1][0].row) {
                    extrema[districtNumberOfThisState - 1][0] = thisState;
                }
                if(thisState.row > extrema[districtNumberOfThisState - 1][1].row) {
                    extrema[districtNumberOfThisState - 1][1] = thisState;
                }
                if(thisState.col < extrema[districtNumberOfThisState - 1][2].col) {
                    extrema[districtNumberOfThisState - 1][2] = thisState;
                }
                if(thisState.col > extrema[districtNumberOfThisState - 1][3].col) {
                    extrema[districtNumberOfThisState - 1][3] = thisState;
                }
            }
        }
        double length;
        double width;
        for (int i = 0; i < extrema.length; i++) {
            length = Math.abs(extrema[i][0].row - extrema[i][1].row) + 1; // add one to count properly
            width = Math.abs(extrema[i][2].col - extrema[i][3].col) + 1;
            if (length < width) {
                scoreTotal += numOfDistricts / (width * width);
            } else {
                scoreTotal += numOfDistricts / (length * length);
            }
        }
        double averageScore = scoreTotal / numOfDistricts;
        return averageScore;
    }
    
    // calculates the ratio of the district area to the area of a square with perimeter equal to the district's perimeter for all districts in a given Squaretopia
    public double PolsbyPopper() {
        int[] perimetersArray = this.calculatePerimeters(); 
        double scoreTotal = 0;
        int numOfDistricts = data.length - 2;
        double areaOfADistrict = data.length - 2;
        for(int i = 0; i < perimetersArray.length; i++) {
            scoreTotal += areaOfADistrict / Math.pow(Double.valueOf(perimetersArray[i]) / 4, 2);
        }
        double averageScore = scoreTotal / numOfDistricts;
        return averageScore;
    }
    
    // calculates the ratio of the district perimeter to the perimeter of a square with area equal to the district's area for all districts in a given Squaretopia
    public double Schwartzberg() {
        int[] perimetersArray = this.calculatePerimeters(); 
        double scoreTotal = 0;
        int numOfDistricts = data.length - 2;
        double areaOfADistrict = data.length - 2;
        for(int i = 0; i < perimetersArray.length; i++) {
            scoreTotal += (4 * Math.sqrt(Double.valueOf(areaOfADistrict))) / Double.valueOf(perimetersArray[i]);
        }
        double averageScore = scoreTotal / numOfDistricts;
        return averageScore;
    }
    
    // calculates the district area to minimum bounding square ratios for all districts in a given Squaretopia 
    public double singleReock() {
        // locate and save extrema states
        double scoreTotal = 0;
        double numOfDistricts = 1;
        SquaretopiaState[][] extrema = new SquaretopiaState[(int) numOfDistricts][4]; // each district has four states in this order min length, max length, min width, max width
        for (int i = 1; i < data.length - 1; i++) {
            for (int j = 1; j < data.length - 1; j++) {
                int districtNumberOfThisState = data[i][j].districtNumber;
                if(districtNumberOfThisState != 0) {
                    SquaretopiaState thisState = data[i][j];
                    if(extrema[districtNumberOfThisState - 1][0] == null) {
                        extrema[districtNumberOfThisState - 1][0] = thisState;
                        extrema[districtNumberOfThisState - 1][1] = thisState;
                        extrema[districtNumberOfThisState - 1][2] = thisState;
                        extrema[districtNumberOfThisState - 1][3] = thisState;
                    }
                    if(thisState.row < extrema[districtNumberOfThisState - 1][0].row) {
                        extrema[districtNumberOfThisState - 1][0] = thisState;
                    }
                    if(thisState.row > extrema[districtNumberOfThisState - 1][1].row) {
                        extrema[districtNumberOfThisState - 1][1] = thisState;
                    }
                    if(thisState.col < extrema[districtNumberOfThisState - 1][2].col) {
                        extrema[districtNumberOfThisState - 1][2] = thisState;
                    }
                    if(thisState.col > extrema[districtNumberOfThisState - 1][3].col) {
                        extrema[districtNumberOfThisState - 1][3] = thisState;
                    }
                }
            }
        }
        double length;
        double width;
        for (int i = 0; i < extrema.length; i++) {
            length = Math.abs(extrema[i][0].row - extrema[i][1].row) + 1; // add one to count properly
            width = Math.abs(extrema[i][2].col - extrema[i][3].col) + 1;
            if (length < width) {
                scoreTotal += (data.length - 2) / (width * width);
            } else {
                scoreTotal += (data.length - 2) / (length * length);
            }
        }
        double averageScore = scoreTotal / numOfDistricts;
        return averageScore;
    }

}
