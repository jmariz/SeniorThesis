package partition;

import java.util.HashSet;
import java.util.Set;

/**
 * Joshua Mariz 05/05/2021
 * Squaretopia representation using an array of SquaretopiaCell arrays
 */
final public class SquaretopiaMatrix {
    
    private final int M;             // number of rows
    private final int N;             // number of columns
    public final SquaretopiaCell[][] data;   // M-by-N SquaretopiaCell array
    
    /**
     * Sets up an M by N representation of Squaretopia using an array of SquaretopiaCell arrays.
     * NOTE: This method initializes the outer padding layer of the Squaretopia matrix to -1. This is so that each inner cell has four adjacent neighbors.
     * @param M Integer number of rows in this Squaretopia
     * @param N Integer number of columns in this Squaretopia
     * @return void
     */
    public SquaretopiaMatrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new SquaretopiaCell[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                data[i][j] = new SquaretopiaCell(i, j);
                if(i == 0 || i == M - 1 || j == 0 || j == N - 1) {
                    data[i][j].districtNumber = -1;
                    data[i][j].checked = true;
                }
            }
        }
    }
    
    /**
     * Prints a String representation of this Squaretopia. Prints a matrix where each number corresponds to the district number of that Squaretopia cell. 
     * NOTE: This method does not print the outer padding layer of -1s.
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
     * Creates a set containing all the inner Squaretopia cells.
     * NOTE: This method assumes that all inner cells are unclaimed, so it should only be called after the initialization of this Squaretopia.
     * @return Set<SquaretopiaCell> containing all the inner cells of this Squaretopia matrix
     */
    public Set<SquaretopiaCell> generateSetOfFreeCells() {
        int matrixLength = data.length; // assumes square matrix
        Set<SquaretopiaCell> setOfCells = new HashSet<>();
        for(int i = 1; i < matrixLength - 1; i++) { // does not add perimeter padding cells to the set 
            for(int j = 1; j < matrixLength - 1; j++) {
                setOfCells.add(new SquaretopiaCell(i, j));
            }
        }
        return setOfCells;
    }
    
    /**
     * Helpful method for debugging. Prints a matrix containing the SquaretopiaCell.checked values for each cell in this Squaretopia.
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
     * Determines if this Squaretopia matrix is valid. An invalid matrix contains at least one isolated group of k Squaretopia cells, 
     * where n, the number of cells in each district, doesn't divide k.
     * NOTE: This method assumes that this Squaretopia matrix is a square.
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
                    Set<SquaretopiaCell> neighborCells = findContiguousNeighbors(matrixCopy, matrixCopy.data[i][j]);  
                        if(neighborCells.size() % (matrixLength - 2) !=  0) {
                            return false;
                        }
                }
            } 
        } 
        return true;
    }
    
    /**
     * Creates a SquaretopiaMatrix duplicate of this Squaretopia matrix.
     * NOTE: This method assumes that this Squaretopia matrix is a square.
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
     * @param matrix SquaretopiaMatrix that contains Squaretopia cells and some district assignments
     * @param currentLocation SquaretopiaCell whose contiguous unchecked neighbors we will find
     * @return Set<SquaretopiaCell> containing all the unchecked neighbors that a rook chess piece can reach when starting from currentLocation
     */
    public Set<SquaretopiaCell> findContiguousNeighbors (SquaretopiaMatrix matrix, SquaretopiaCell currentLocation) {
        int curLocRow = currentLocation.row;
        int curLocCol = currentLocation.col;
        Set<SquaretopiaCell> contiguousCells = new HashSet<>();
        contiguousCells.add(currentLocation);
        currentLocation.checked = true;
        if(matrix.data[curLocRow - 1][curLocCol].checked == false) { // check availability of the cell above
            contiguousCells.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow - 1][curLocCol]));
        }
        if(matrix.data[curLocRow + 1][curLocCol].checked == false) { // check availability of the cell below
            contiguousCells.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow + 1][curLocCol]));
        }
        if(matrix.data[curLocRow][curLocCol - 1].checked == false) { // check availability of the cell to the left
            contiguousCells.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow][curLocCol - 1]));
        }
        if(matrix.data[curLocRow][curLocCol + 1].checked == false) { // check availability of the cell to the right
            contiguousCells.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow][curLocCol + 1]));
        }
        return contiguousCells;
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
                int districtNumOfCurrentCell = data[i][j].districtNumber;
                if(districtNumOfCurrentCell == data[i - 1][j].districtNumber) {
                    perimetersArray[districtNumOfCurrentCell - 1]--;
                }
                if(districtNumOfCurrentCell == data[i + 1][j].districtNumber) {
                    perimetersArray[districtNumOfCurrentCell - 1]--;
                }
                if(districtNumOfCurrentCell == data[i][j - 1].districtNumber) {
                    perimetersArray[districtNumOfCurrentCell - 1]--;
                }
                if(districtNumOfCurrentCell == data[i][j + 1].districtNumber) {
                    perimetersArray[districtNumOfCurrentCell - 1]--;
                }
            }
        }
      return perimetersArray;
    }
    
    /**
     * Calculates the Length-Width score of each district in a Squaretopia partition, then finds the Length-Width
     * score of the partition by taking the average of the districts' Length-Width scores
     * @return double Length-Width compactness score (unrounded) of the partition 
     */
    public double LengthWidth() {
        // locate and save extrema cells
        double scoreTotal = 0;
        double numOfDistricts = data.length - 2;
        SquaretopiaCell[][] extrema = new SquaretopiaCell[(int) numOfDistricts][4]; // each district has four cells in this order min length, max length, min width, max width
        for (int i = 1; i < data.length - 1; i++) {
            for (int j = 1; j < data.length - 1; j++) {
                int districtNumberOfThisCell = data[i][j].districtNumber;
                SquaretopiaCell thisCell = data[i][j];
                if(extrema[districtNumberOfThisCell - 1][0] == null) {
                    extrema[districtNumberOfThisCell - 1][0] = thisCell;
                    extrema[districtNumberOfThisCell - 1][1] = thisCell;
                    extrema[districtNumberOfThisCell - 1][2] = thisCell;
                    extrema[districtNumberOfThisCell - 1][3] = thisCell;
                }
                if(thisCell.row < extrema[districtNumberOfThisCell - 1][0].row) {
                    extrema[districtNumberOfThisCell - 1][0] = thisCell;
                }
                if(thisCell.row > extrema[districtNumberOfThisCell - 1][1].row) {
                    extrema[districtNumberOfThisCell - 1][1] = thisCell;
                }
                if(thisCell.col < extrema[districtNumberOfThisCell - 1][2].col) {
                    extrema[districtNumberOfThisCell - 1][2] = thisCell;
                }
                if(thisCell.col > extrema[districtNumberOfThisCell - 1][3].col) {
                    extrema[districtNumberOfThisCell - 1][3] = thisCell;
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
    
    /**
     * Calculates the Reock score of each district in a Squaretopia partition, then finds the Reock
     * score of the partition by taking the average of the districts' Reock scores 
     * @return double Reock compactness score (unrounded) of the partition 
     */ 
    public double Reock() {
        // locate and save extrema cells
        double scoreTotal = 0;
        double numOfDistricts = data.length - 2;
        SquaretopiaCell[][] extrema = new SquaretopiaCell[(int) numOfDistricts][4]; // each district has four cells in this order min length, max length, min width, max width
        for (int i = 1; i < data.length - 1; i++) {
            for (int j = 1; j < data.length - 1; j++) {
                int districtNumberOfThisCell = data[i][j].districtNumber;
                SquaretopiaCell thisCell = data[i][j];
                if(extrema[districtNumberOfThisCell - 1][0] == null) {
                    extrema[districtNumberOfThisCell - 1][0] = thisCell;
                    extrema[districtNumberOfThisCell - 1][1] = thisCell;
                    extrema[districtNumberOfThisCell - 1][2] = thisCell;
                    extrema[districtNumberOfThisCell - 1][3] = thisCell;
                }
                if(thisCell.row < extrema[districtNumberOfThisCell - 1][0].row) {
                    extrema[districtNumberOfThisCell - 1][0] = thisCell;
                }
                if(thisCell.row > extrema[districtNumberOfThisCell - 1][1].row) {
                    extrema[districtNumberOfThisCell - 1][1] = thisCell;
                }
                if(thisCell.col < extrema[districtNumberOfThisCell - 1][2].col) {
                    extrema[districtNumberOfThisCell - 1][2] = thisCell;
                }
                if(thisCell.col > extrema[districtNumberOfThisCell - 1][3].col) {
                    extrema[districtNumberOfThisCell - 1][3] = thisCell;
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
    
    /**
     * Calculates the Polsby-Popper score of each district in a Squaretopia partition, then finds the Polsby-Popper
     * score of the partition by taking the average of the districts' Polsby-Popper scores 
     * @return double Polsby-Popper compactness score (unrounded) of the partition 
     */
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
    
    /**
     * Calculates the Schwartzberg score of each district in a Squaretopia partition, then finds the Schwartzberg
     * score of the partition by taking the average of the districts' Schwartzberg scores 
     * @return double Schwartzberg compactness score (unrounded) of the partition 
     */
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
    
    /**
     * Calculates the Reock score of the FIRST AND ONLY district in an INCOMPLETE Squaretopia partition
     * @return double Reock compactness score (unrounded) of the only district in the incomplete partition 
     */ 
    public double singleReock() {
        // locate and save extrema cells
        double scoreTotal = 0;
        double numOfDistricts = 1;
        SquaretopiaCell[][] extrema = new SquaretopiaCell[(int) numOfDistricts][4]; // each district has four cells in this order min length, max length, min width, max width
        for (int i = 1; i < data.length - 1; i++) {
            for (int j = 1; j < data.length - 1; j++) {
                int districtNumberOfThisCell = data[i][j].districtNumber;
                if(districtNumberOfThisCell != 0) {
                    SquaretopiaCell thisCell = data[i][j];
                    if(extrema[districtNumberOfThisCell - 1][0] == null) {
                        extrema[districtNumberOfThisCell - 1][0] = thisCell;
                        extrema[districtNumberOfThisCell - 1][1] = thisCell;
                        extrema[districtNumberOfThisCell - 1][2] = thisCell;
                        extrema[districtNumberOfThisCell - 1][3] = thisCell;
                    }
                    if(thisCell.row < extrema[districtNumberOfThisCell - 1][0].row) {
                        extrema[districtNumberOfThisCell - 1][0] = thisCell;
                    }
                    if(thisCell.row > extrema[districtNumberOfThisCell - 1][1].row) {
                        extrema[districtNumberOfThisCell - 1][1] = thisCell;
                    }
                    if(thisCell.col < extrema[districtNumberOfThisCell - 1][2].col) {
                        extrema[districtNumberOfThisCell - 1][2] = thisCell;
                    }
                    if(thisCell.col > extrema[districtNumberOfThisCell - 1][3].col) {
                        extrema[districtNumberOfThisCell - 1][3] = thisCell;
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
