package partition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Joshua Mariz 05/05/2021
 * UnboundedSingleDistricter.java
 * UnboundedSingleDistricter uniformly at random generates a district of size n in a (2n-1) by (2n-1) Squaretopia
 */
public class UnboundedSingleDistricter {
    
    /**
     * Prepares the Squaretopia unbounded single-districting process.
     * NOTE: This method forces the central cell of the adjusted matrix to be the first cell in the district. This allows us to generate unbounded districts.
     * @param size Integer number of the Squaretopia's size
     * @param numOfTrials Integer number for the number of single districts we will generate
     * @return void
     */
    public static void Partition (int size, int numOfTrials) {
        int adjustedSize = ((size - 1) * 2 + 1) + 2; // add 2 for compatibility with outer padding of zeroes
        int trialsConducted = 0;
        while(trialsConducted < numOfTrials) {
            SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(adjustedSize, adjustedSize);            
            Set<SquaretopiaCell> freeCells = Squaretopia.generateSetOfFreeCells();
            SquaretopiaCell claimedCell = Squaretopia.data[size][size]; // we must claim the central cell first to allow free generation of districts
            Set<SquaretopiaCell> currentDistrict = new HashSet<>();
            Set<SquaretopiaCell> currentDistrictFreeNeighbors = new HashSet<>();
            Set<SquaretopiaCell> recentlyAddedTransitions = new HashSet<>();
            
            claimer(Squaretopia, freeCells, currentDistrict, claimedCell);
            currentDistrictFreeNeighbors.addAll(getTransitions(Squaretopia, claimedCell));
            recentlyAddedTransitions.addAll(getTransitions(Squaretopia, claimedCell));
            
            while(freeCells.size() > ((adjustedSize - 2) * (adjustedSize - 2) - size)) { // we only need to generate one district
                recursiveDistricter(Squaretopia, freeCells, currentDistrict, currentDistrictFreeNeighbors, recentlyAddedTransitions);
                currentDistrict.clear();
                currentDistrictFreeNeighbors.clear();
            }
            Squaretopia.show();
            System.out.println("");
            trialsConducted++;
        }
        
    }
    
    // recursive algorithm
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaCell> freeCells, Set<SquaretopiaCell> currentDistrict, Set<SquaretopiaCell> allPossibleTransitions, Set<SquaretopiaCell> recentlyAddedTransitions) {
          if(currentDistrict.size() == (int) (((matrix.data.length - 2) - 1) / 2 + 1)) { // assumes matrix is a square! And expands grid so that all single district classes can be freely generated
              return matrix;
          }
          
          Set<SquaretopiaCell> newAllPossibleNeighbors = new HashSet<>();
          newAllPossibleNeighbors.addAll(allPossibleTransitions);
          Set<SquaretopiaCell> newRecentlyAddedTransitions = new HashSet<>();
          
          while(newAllPossibleNeighbors.size() != 0) {
              SquaretopiaCell nextCell = isolatedCell(matrix, newAllPossibleNeighbors);
              if(nextCell == null) {
                  nextCell = randomCell(newAllPossibleNeighbors);
              }
              claimer(matrix, freeCells, currentDistrict, nextCell);
              newAllPossibleNeighbors.remove(nextCell);
              newRecentlyAddedTransitions = recentlyAddedTransitions(newAllPossibleNeighbors, getTransitions(matrix, nextCell));
              newAllPossibleNeighbors.addAll(getTransitions(matrix, nextCell));
              if(recursiveDistricter(matrix, freeCells, currentDistrict, newAllPossibleNeighbors, newRecentlyAddedTransitions) == null) {
                  newAllPossibleNeighbors.remove(nextCell);
                  returner(matrix, freeCells, currentDistrict, nextCell);
                  newAllPossibleNeighbors.removeAll(newRecentlyAddedTransitions);
              } else {
                  return matrix;
              }
          }
          return null;
    }
    
    // chooses a cell uniformly at random from the set of free cells
    public static SquaretopiaCell randomCell(Set<SquaretopiaCell> set) {
        SquaretopiaCell chosenCell = new SquaretopiaCell(-1, -1);
        int numOfCells = set.size();
        int chosenCellIndex = (int) Math.ceil((Math.random() * numOfCells));
        Iterator<SquaretopiaCell> it = set.iterator();
        for (int i = 0; i < chosenCellIndex; i++) {
            chosenCell = it.next();
        }
        return chosenCell;
    }
    
    // determines which district we are constructing
    public static int getCurrentDistrictNumber(SquaretopiaMatrix matrix, Set<SquaretopiaCell> freeCells) {
        int n = matrix.data.length - 2; // assumes matrix is a square and subtract 2 because of outer layer
        int currentDistrictNumber = (int) Math.ceil( Double.valueOf(n * n - freeCells.size()) / n );
        return currentDistrictNumber;
    }
    
    // gets a specified cell's free neighbors
    public static Set<SquaretopiaCell> getTransitions (SquaretopiaMatrix matrix, SquaretopiaCell currentLocation) {
        int curLocRow = currentLocation.row;
        int curLocCol = currentLocation.col;
        Set<SquaretopiaCell> possibleTransitions = new HashSet<>();
        if(matrix.data[curLocRow - 1][curLocCol].districtNumber == 0) { // check availability of the cell above
            possibleTransitions.add(new SquaretopiaCell(curLocRow - 1, curLocCol));
        }
        if(matrix.data[curLocRow + 1][curLocCol].districtNumber == 0) { // check availability of the cell below
            possibleTransitions.add(new SquaretopiaCell(curLocRow + 1, curLocCol));
        }
        if(matrix.data[curLocRow][curLocCol - 1].districtNumber == 0) { // check availability of the cell to the left
            possibleTransitions.add(new SquaretopiaCell(curLocRow, curLocCol - 1));
        }
        if(matrix.data[curLocRow][curLocCol + 1].districtNumber == 0) { // check availability of the cell to the right
            possibleTransitions.add(new SquaretopiaCell(curLocRow, curLocCol + 1));
        }
        return possibleTransitions;
    }
    
    // updates the matrix
    public static SquaretopiaMatrix updateMatrix(SquaretopiaMatrix matrix, Set<SquaretopiaCell> freeCells, SquaretopiaCell claimedCell) {
        int claimedCellDistrict = matrix.data[claimedCell.row][claimedCell.col].districtNumber;
        if(claimedCellDistrict == 0) {
            matrix.data[claimedCell.row][claimedCell.col].districtNumber = getCurrentDistrictNumber(matrix, freeCells);
            matrix.data[claimedCell.row][claimedCell.col].checked = true;
        } else {
            matrix.data[claimedCell.row][claimedCell.col].districtNumber = 0;
            matrix.data[claimedCell.row][claimedCell.col].checked = false;
        }
        return matrix;
    }
    
    // returns a SquaretopiaCell with one neighbor if it exists 
    public static SquaretopiaCell deadEnd (SquaretopiaMatrix matrix, Set<SquaretopiaCell> allPossibleTransitions) {
        for(SquaretopiaCell cell : allPossibleTransitions) {
            if(getTransitions(matrix, cell).size() == 1) {
                return cell;
            }
        }
        return null;
    }
    
    // returns a SquaretopiaCell with one neighbor if it exists 
    public static SquaretopiaCell isolatedCell (SquaretopiaMatrix matrix, Set<SquaretopiaCell> allPossibleTransitions) {
        for(SquaretopiaCell cell : allPossibleTransitions) {
            if(getTransitions(matrix, cell).size() == 0) {
                return cell;
            }
        }
        return null;
    }
    
    // takes care of everything when claiming a free cell
    public static void claimer (SquaretopiaMatrix matrix, Set<SquaretopiaCell> freeCells, Set<SquaretopiaCell> currentDistrict, SquaretopiaCell claimedCell) {
        freeCells.remove(claimedCell);
        updateMatrix(matrix, freeCells, claimedCell);
        currentDistrict.add(claimedCell);
    }
    
    // takes care of everything when returning an already claimed cell
    public static void returner (SquaretopiaMatrix matrix, Set<SquaretopiaCell> freeCells, Set<SquaretopiaCell> currentDistrict, SquaretopiaCell returnedCell) {
        freeCells.add(returnedCell);
        currentDistrict.remove(returnedCell);
        updateMatrix(matrix, freeCells, returnedCell);
    }
    
    // determines the recently added transitions
    public static Set<SquaretopiaCell> recentlyAddedTransitions (Set<SquaretopiaCell> freeCells, Set<SquaretopiaCell> transitions) {
        Set<SquaretopiaCell> recentlyAddedTransitions = new HashSet<>();
        for(SquaretopiaCell cell : transitions) {
            if(freeCells.contains(cell) == false) {
                recentlyAddedTransitions.add(cell);
            }
        }
        return recentlyAddedTransitions;
    }
    
}
