package partition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Joshua Mariz
 * Partitioner that randomly partitions (with a uniform probability) an n by n Squaretopia into n evenly sized contiguous districts
 */
public class Partitioner {
    
    /**
     * Prepares the partitioning process.
     * @param size Integer number of the Squaretopia's size
     * @param numOfTrials Integer number for the number of Squaretopias we will partition
     * @return void
     */
    public static void Partition (int size, int numOfTrials) {
        int adjustedSize = size + 2; // includes the outer layer of padding states
        int trialsConducted = 0;
        Boolean isValidPartition;
        while(trialsConducted < numOfTrials) {
            isValidPartition = true;
            SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(adjustedSize, adjustedSize);
            Set<SquaretopiaState> freeSquares = Squaretopia.generateSetOfFreeStates();
            Set<SquaretopiaState> currentDistrict = new HashSet<>();
            Set<SquaretopiaState> currentDistrictFreeNeighbors = new HashSet<>();
            Set<SquaretopiaState> recentlyAddedTransitions = new HashSet<>();
            SquaretopiaState claimedState;
            while(freeSquares.size() > 0) {
                claimedState = deadEnd(Squaretopia, freeSquares);
                if(claimedState == null) {
                    claimedState = randomState(freeSquares);
                }
                claimer(Squaretopia, freeSquares, currentDistrict, claimedState);
                currentDistrictFreeNeighbors.addAll(getTransitions(Squaretopia, claimedState));
                recentlyAddedTransitions.addAll(getTransitions(Squaretopia, claimedState));
                if (recursiveDistricter(Squaretopia, freeSquares, currentDistrict, currentDistrictFreeNeighbors, recentlyAddedTransitions) == null) {
                    isValidPartition = false;
                    break;
                }
                currentDistrict.clear();
                currentDistrictFreeNeighbors.clear();
            }
            if(isValidPartition) {
                // Squaretopia.show();
                System.out.println(Math.round(Squaretopia.Schwartzberg()*100) + " " + Math.round(Squaretopia.PolsbyPopper()*100) + " " + Math.round(Squaretopia.Reock()*100) + " " + Math.round(Squaretopia.LengthWidthScore()*100));
                // System.out.println("");
                trialsConducted++;
            }
        }
        
    }
    
    // recursion algorithm
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates, Set<SquaretopiaState> currentDistrict, Set<SquaretopiaState> allPossibleTransitions, Set<SquaretopiaState> recentlyAddedTransitions) {
          if(currentDistrict.size() == (matrix.data.length - 2)) { // assumes matrix is a square
              // System.out.println("valid map? " + matrix.validMap(matrix)); // delete
              if(matrix.validMap()) {
                  recentlyAddedTransitions.clear();
                  // System.out.println("currentDistrict.size() in if just cleared: " + currentDistrict.size()); // delete
                  // System.out.println("valid map!"); // delete
                  return matrix;
              } else {
                  allPossibleTransitions.removeAll(recentlyAddedTransitions);
                  return null;
              }
          }
          
          Set<SquaretopiaState> newAllPossibleNeighbors = new HashSet<>();
          newAllPossibleNeighbors.addAll(allPossibleTransitions);
          Set<SquaretopiaState> newRecentlyAddedTransitions = new HashSet<>();
          
//          SquaretopiaState currentState = randomState(currentDistrict);
//          Set<SquaretopiaState> temporarySet = new HashSet<>();
//          while(getTransitions(matrix, currentState).size() == 0) { // temporarily remove states with no transitions
//              temporarySet.add(currentState);
//              currentDistrict.remove(currentState);
//              currentState = randomState(currentDistrict);
//          }
//          currentDistrict.addAll(temporarySet); // add back states with no transitions
          while(newAllPossibleNeighbors.size() != 0) {
              SquaretopiaState nextState = isolatedState(matrix, newAllPossibleNeighbors);
              if(nextState == null) {
                  nextState = randomState(newAllPossibleNeighbors);
              }
              // System.out.println("nextState... " + nextState.toString()); // delete
              claimer(matrix, freeStates, currentDistrict, nextState);
              newAllPossibleNeighbors.remove(nextState);
              newRecentlyAddedTransitions = recentlyAddedTransitions(newAllPossibleNeighbors, getTransitions(matrix, nextState));
              // System.out.println("recentlyAddedTransitions.size(): " + newRecentlyAddedTransitions.size()); // delete
              newAllPossibleNeighbors.addAll(getTransitions(matrix, nextState));
              // System.out.println("newAllPossibleNeighbors.size(): " + newAllPossibleNeighbors.size()); // delete
              // for(SquaretopiaState state : newAllPossibleNeighbors) { // delete
                  // System.out.println(state.toString()); // delete
              // }
              // System.out.println(""); // delete
              // System.out.println(""); // delete
              // System.out.println(""); // delete
              if(recursiveDistricter(matrix, freeStates, currentDistrict, newAllPossibleNeighbors, newRecentlyAddedTransitions) == null) {
                  // System.out.println("came back to line 200"); // delete
                  newAllPossibleNeighbors.remove(nextState);
                  returner(matrix, freeStates, currentDistrict, nextState);
                  newAllPossibleNeighbors.removeAll(newRecentlyAddedTransitions);
              } else {
                  return matrix;
              }
              // System.out.println("reached end of while loop in recurser"); // delete
          }
          // System.out.println("returning null from the end of the recurser..."); // delete
          return null;
    }
    
    // pick a random state from the set of free states
    public static SquaretopiaState randomState(Set<SquaretopiaState> set) {
        SquaretopiaState chosenState = new SquaretopiaState(-1, -1);
        int numOfStates = set.size();
        // System.out.println("number of choices for current state: " + numOfStates); // delete
        int chosenStateIndex = (int) Math.ceil((Math.random() * numOfStates));
        Iterator<SquaretopiaState> it = set.iterator();
        // System.out.println("chosenStateIndex: " + chosenStateIndex); // delete
        for (int i = 0; i < chosenStateIndex; i++) {
            chosenState = it.next();
        }
        // System.out.println("chose this state: " + chosenState.toString()); // delete
        return chosenState;
    }
    
    // determine which district we are constructing
    public static int getCurrentDistrictNumber(SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates) {
        // System.out.println("freeStates.size(): " + freeStates.size()); // delete
        int n = matrix.data.length - 2; // assumes matrix is a square and subtract 2 because of outer layer
        int currentDistrictNumber = (int) Math.ceil( Double.valueOf(n * n - freeStates.size()) / n );
        return currentDistrictNumber;
    }
    
    // get a specified state's free neighbors
    public static Set<SquaretopiaState> getTransitions (SquaretopiaMatrix matrix, SquaretopiaState currentLocation) {
        int curLocRow = currentLocation.row;
        int curLocCol = currentLocation.col;
        Set<SquaretopiaState> possibleTransitions = new HashSet<>();
        if(matrix.data[curLocRow - 1][curLocCol].districtNumber == 0) { // check availability of the state above
            possibleTransitions.add(new SquaretopiaState(curLocRow - 1, curLocCol));
        }
        if(matrix.data[curLocRow + 1][curLocCol].districtNumber == 0) { // check availability of the state below
            possibleTransitions.add(new SquaretopiaState(curLocRow + 1, curLocCol));
        }
        if(matrix.data[curLocRow][curLocCol - 1].districtNumber == 0) { // check availability of the state to the left
            possibleTransitions.add(new SquaretopiaState(curLocRow, curLocCol - 1));
        }
        if(matrix.data[curLocRow][curLocCol + 1].districtNumber == 0) { // check availability of the state to the right
            possibleTransitions.add(new SquaretopiaState(curLocRow, curLocCol + 1));
        }
        return possibleTransitions;
    }
    
    // update the matrix
    public static SquaretopiaMatrix updateMatrix(SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates, SquaretopiaState claimedState) {
        // System.out.println("claimedState.row: " + claimedState.row); // delete
        // System.out.println("claimedState.col: " + claimedState.col); // delete
        // System.out.println("getCurrentDistrictNumber(matrix, freeStates): " + getCurrentDistrictNumber(matrix, freeStates)); // delete
        int claimedStateDistrict = matrix.data[claimedState.row][claimedState.col].districtNumber;
        if(claimedStateDistrict == 0) {
            matrix.data[claimedState.row][claimedState.col].districtNumber = getCurrentDistrictNumber(matrix, freeStates);
            matrix.data[claimedState.row][claimedState.col].checked = true;
        } else {
            matrix.data[claimedState.row][claimedState.col].districtNumber = 0;
            matrix.data[claimedState.row][claimedState.col].checked = false;
        }
        // matrix.show(); // delete
        return matrix;
    }
    
    // returns a squaretopiaState with one neighbor if it exists 
    public static SquaretopiaState deadEnd (SquaretopiaMatrix matrix, Set<SquaretopiaState> allPossibleTransitions) {
        for(SquaretopiaState state : allPossibleTransitions) {
            if(getTransitions(matrix, state).size() == 1) {
                return state;
            }
        }
        return null;
    }
    
    // returns a squaretopiaState with one neighbor if it exists 
    public static SquaretopiaState isolatedState (SquaretopiaMatrix matrix, Set<SquaretopiaState> allPossibleTransitions) {
        for(SquaretopiaState state : allPossibleTransitions) {
            if(getTransitions(matrix, state).size() == 0) {
                return state;
            }
        }
        return null;
    }
    
    // takes care of everything when claiming a free state
    public static void claimer (SquaretopiaMatrix matrix, Set<SquaretopiaState> freeSquares, Set<SquaretopiaState> currentDistrict, SquaretopiaState claimedState) {
        freeSquares.remove(claimedState);
        updateMatrix(matrix, freeSquares, claimedState);
        currentDistrict.add(claimedState);
    }
    
    // takes care of everything when returning an already claimed state
    public static void returner (SquaretopiaMatrix matrix, Set<SquaretopiaState> freeSquares, Set<SquaretopiaState> currentDistrict, SquaretopiaState returnedState) {
        freeSquares.add(returnedState);
        currentDistrict.remove(returnedState);
        updateMatrix(matrix, freeSquares, returnedState);
    }
    
    // determines the recently added transitions
    public static Set<SquaretopiaState> recentlyAddedTransitions (Set<SquaretopiaState> freeStates, Set<SquaretopiaState> transitions) {
        Set<SquaretopiaState> recentlyAddedTransitions = new HashSet<>();
        for(SquaretopiaState state : transitions) {
            if(freeStates.contains(state) == false) {
                recentlyAddedTransitions.add(state);
            }
        }
        return recentlyAddedTransitions;
    }
    
}
