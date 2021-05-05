package partition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Joshua Mariz 05/05/2021
 * WeightedPartitioner.java
 * WeightedPartitioner utilizes a weighting factor probability to partition an n by n Squaretopia into n equally sized contiguous districts
 */
public class WeightedPartitioner {
    
    /**
     * Prepares the Squaretopia weighted partitioning process.
     * NOTE: The weighting factor probability argument must be greater than or equal to 0 AND less than or equal to 100.
     * @param size Integer number of the Squaretopia's size
     * @param numOfTrials Integer number for the number of Squaretopias we will partition
     * @param probability Double value for our weighting factor probability
     * @return void
     */
    public static void Partition (int size, int numOfTrials, double probability) {
        int adjustedSize = size + 2; // includes the outer layer of padding cells
        int trialsConducted = 0;
        Boolean isValidPartition;
        while(trialsConducted < numOfTrials) {
            isValidPartition = true;
            SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(adjustedSize, adjustedSize);
            Set<SquaretopiaCell> freeCell = Squaretopia.generateSetOfFreeCells();
            Set<SquaretopiaCell> currentDistrict = new HashSet<>();
            Set<SquaretopiaCell> currentDistrictFreeNeighbors = new HashSet<>();
            Set<SquaretopiaCell> recentlyAddedTransitions = new HashSet<>();
            SquaretopiaCell claimedCell;
            while(freeCell.size() > 0) {
                claimedCell = deadEnd(Squaretopia, freeCell);
                if(claimedCell == null) {
                    claimedCell = randomCell(freeCell, null, probability);
                }
                claimer(Squaretopia, freeCell, currentDistrict, claimedCell);
                currentDistrictFreeNeighbors.addAll(getTransitions(Squaretopia, claimedCell));
                recentlyAddedTransitions.addAll(getTransitions(Squaretopia, claimedCell));
                if (recursiveDistricter(Squaretopia, freeCell, currentDistrict, currentDistrictFreeNeighbors, recentlyAddedTransitions, claimedCell, probability) == null) {
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
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaCell> freeCells, Set<SquaretopiaCell> currentDistrict, Set<SquaretopiaCell> allPossibleTransitions, Set<SquaretopiaCell> recentlyAddedTransitions, SquaretopiaCell recentlyAddedCell, double probability) {
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
                  nextCell = randomCell(newAllPossibleNeighbors, recentlyAddedCell, probability);
              }
              claimer(matrix, freeCells, currentDistrict, nextCell);
              newAllPossibleNeighbors.remove(nextCell);
              newRecentlyAddedTransitions = recentlyAddedTransitions(newAllPossibleNeighbors, getTransitions(matrix, nextCell));
              newAllPossibleNeighbors.addAll(getTransitions(matrix, nextCell));
              if(recursiveDistricter(matrix, freeCells, currentDistrict, newAllPossibleNeighbors, newRecentlyAddedTransitions, nextCell, probability) == null) {
                  newAllPossibleNeighbors.remove(nextCell);
                  returner(matrix, freeCells, currentDistrict, nextCell);
                  newAllPossibleNeighbors.removeAll(newRecentlyAddedTransitions);
              } else {
                  return matrix;
              }
          }
          return null;
    }
    
    // chooses a cell at random from the set of free cells (this method includes the weighting factor)
    // w, where 0 <= w <= 100, is the minimum probability of selecting the next sequential cell (the cell that follows if we continue in the same direction)
    public static SquaretopiaCell randomCell(Set<SquaretopiaCell> set, SquaretopiaCell recentlyClaimedCell, double w) {
        w = w / 100;
        SquaretopiaCell chosenCell = new SquaretopiaCell(-1, -1);
        SquaretopiaCell targetCell = nextSequentialCell(recentlyClaimedCell); // cell that will be assigned a w% probability of being chosen
        int indexOfTargetCellInSet = findIndex(set, targetCell);
        if(indexOfTargetCellInSet == -1) { // each cell in the set has an equal probability of being chosen if the targetCell is not in the set
            int numOfCells = set.size();
            int chosenCellIndex = (int) Math.ceil((Math.random() * numOfCells));
            Iterator<SquaretopiaCell> it = set.iterator();
            for (int i = 0; i < chosenCellIndex; i++) {
                chosenCell = it.next();
            }
            return chosenCell;
        } else { // weigh targetCell with a w% probability 
            double numOfCells = set.size();
            double remainingProbability = 1 - w;
            double nonTargetCellProbability;
            if(set.size() == 1) {
                Iterator<SquaretopiaCell> it = set.iterator();
                return it.next();
            } else {
                nonTargetCellProbability = remainingProbability / (numOfCells - 1);
                double previousUpperBound = 0;
                for(SquaretopiaCell cell : set) {
                    cell.lowerBoundForProbability = previousUpperBound;
                    if(cell.equals(targetCell)) {
                        cell.upperBoundForProbability = previousUpperBound + w;
                    } else {
                        cell.upperBoundForProbability = previousUpperBound + nonTargetCellProbability;
                    }
                    previousUpperBound = cell.upperBoundForProbability;
                }
                double randomNumber = Math.random();
                for(SquaretopiaCell cell : set) {
                    if((cell.lowerBoundForProbability <= randomNumber) && (randomNumber < cell.upperBoundForProbability)) {
                        return cell;
                    }
                }
            }
            System.out.println("Something went wrong");
            return null; // Something went wrong
        }
    }
    
    // determines the index of a given cell in some given set (returns -1 if the cell is not in the set)
    public static int findIndex(Set<SquaretopiaCell> someSet, SquaretopiaCell desiredCell) {
        if(desiredCell != null) {
            Iterator<SquaretopiaCell> it = someSet.iterator();
            int indexOfDesiredCell = 0;
            int maxIterations = someSet.size();
            SquaretopiaCell currentCell;
            for (int i = 0; i < maxIterations; i++) {
                currentCell = it.next();
                if(currentCell.equals(desiredCell)) {
                    return indexOfDesiredCell;
                }
                indexOfDesiredCell++;
            }
        }
        return -1;
    }
    
    // determines the next cell in some direction given a current cell and the current cell's direction 
    public static SquaretopiaCell nextSequentialCell(SquaretopiaCell cell) {
        SquaretopiaCell sequentialCell = null;
        if(cell != null) {
            if(cell.direction == 1) {
                sequentialCell = new SquaretopiaCell(cell.row - 1, cell.col);
            } else if (cell.direction == 2) {
                sequentialCell = new SquaretopiaCell(cell.row, cell.col + 1);
            } else if (cell.direction == 3) {
                sequentialCell = new SquaretopiaCell(cell.row + 1, cell.col);
            } else if (cell.direction == 4) {
                sequentialCell = new SquaretopiaCell(cell.row, cell.col - 1);
            }
        }
        return sequentialCell;
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
            SquaretopiaCell possibleTransitionAbove = new SquaretopiaCell(curLocRow - 1, curLocCol);
            possibleTransitionAbove.direction = 1;
            possibleTransitions.add(possibleTransitionAbove);
        }
        if(matrix.data[curLocRow + 1][curLocCol].districtNumber == 0) { // check availability of the cell below
            SquaretopiaCell possibleTransitionBelow = new SquaretopiaCell(curLocRow + 1, curLocCol);
            possibleTransitionBelow.direction = 3;
            possibleTransitions.add(possibleTransitionBelow);
        }
        if(matrix.data[curLocRow][curLocCol - 1].districtNumber == 0) { // check availability of the cell to the left
            SquaretopiaCell possibleTransitionLeft = new SquaretopiaCell(curLocRow, curLocCol - 1);
            possibleTransitionLeft.direction = 4;
            possibleTransitions.add(possibleTransitionLeft);
        }
        if(matrix.data[curLocRow][curLocCol + 1].districtNumber == 0) { // check availability of the cell to the right
            SquaretopiaCell possibleTransitionRight = new SquaretopiaCell(curLocRow, curLocCol + 1);
            possibleTransitionRight.direction = 2;
            possibleTransitions.add(possibleTransitionRight);
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
