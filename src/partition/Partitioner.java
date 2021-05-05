package partition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Joshua Mariz 05/05/2021
 * Partitioner.java
 * Partitioner uniformly at random partitions an n by n Squaretopia into n equally sized contiguous districts
 */
public class Partitioner {
    
    /**
     * Prepares the Squaretopia partitioning process.
     * @param size Integer number of the Squaretopia's size
     * @param numOfTrials Integer number for the number of Squaretopias we will partition
     * @return void
     */
    public static void Partition (int size, int numOfTrials) {
        int adjustedSize = size + 2; // includes the outer layer of padding cells
        int trialsConducted = 0;
        Boolean isValidPartition;
        while(trialsConducted < numOfTrials) {
            isValidPartition = true;
            SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(adjustedSize, adjustedSize);
            Set<SquaretopiaCell> freeCells = Squaretopia.generateSetOfFreeCells();
            Set<SquaretopiaCell> currentDistrict = new HashSet<>();
            Set<SquaretopiaCell> currentDistrictFreeNeighbors = new HashSet<>();
            Set<SquaretopiaCell> recentlyAddedTransitions = new HashSet<>();
            SquaretopiaCell claimedCell;
            while(freeCells.size() > 0) {
                claimedCell = deadEnd(Squaretopia, freeCells);
                if(claimedCell == null) {
                    claimedCell = randomCell(freeCells);
                }
                claimer(Squaretopia, freeCells, currentDistrict, claimedCell);
                currentDistrictFreeNeighbors.addAll(getTransitions(Squaretopia, claimedCell));
                recentlyAddedTransitions.addAll(getTransitions(Squaretopia, claimedCell));
                if (recursiveDistricter(Squaretopia, freeCells, currentDistrict, currentDistrictFreeNeighbors, recentlyAddedTransitions) == null) {
                    isValidPartition = false;
                    break;
                }
                currentDistrict.clear();
                currentDistrictFreeNeighbors.clear();
            }
            if(isValidPartition) { // output partition and rounded compactness scores (LW RE SB PP)
                // COMMENT OUT THE LINE BELOW TO NOT OUTPUT THE PARTITIONED SQUARETOPIA
                Squaretopia.show();
                // COMMENT OUT THE LINE BELOW TO NOT OUTPUT THE SCORES OF THE PARTITIONED SQUARETOPIA
                System.out.println(Math.round(Squaretopia.LengthWidth()*100) + " " + Math.round(Squaretopia.Reock()*100) + " " + Math.round(Squaretopia.Schwartzberg()*100) + " " + Math.round(Squaretopia.PolsbyPopper()*100));
                System.out.println("");
                trialsConducted++;
            }
        }
        
    }
    
    // recursive algorithm
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaCell> freeCells, Set<SquaretopiaCell> currentDistrict, Set<SquaretopiaCell> allPossibleTransitions, Set<SquaretopiaCell> recentlyAddedTransitions) {
          if(currentDistrict.size() == (matrix.data.length - 2)) { // assumes matrix is a square!
              if(matrix.validMap()) {
                  recentlyAddedTransitions.clear();
                  return matrix;
              } else {
                  allPossibleTransitions.removeAll(recentlyAddedTransitions);
                  return null;
              }
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
