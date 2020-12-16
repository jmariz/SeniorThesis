package partition;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.jdi.DoubleValue;

/**
 * Partition
 */
public class UnboundedSingleDistricter {
    
    /**
     * Public interface for Partition
     */
    
    // sets everything up
    public static void Partition (int size, int numOfTrials) {
        int adjustedSize = ((size - 1) * 2 + 1) + 2; // add 2 for compatibility with outer perimeter of zeroes
        int trialsConducted = 0;
        
        while(trialsConducted < numOfTrials) {
            Boolean isValidPartition = true;
            SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(adjustedSize, adjustedSize);
            // Squaretopia.show();
            
            Set<SquaretopiaState> freeSquares = generateSetOfFreeStates(Squaretopia);
            SquaretopiaState claimedState = Squaretopia.data[size][size]; // claim center state
            Set<SquaretopiaState> currentDistrict = new HashSet<>();
            Set<SquaretopiaState> currentDistrictFreeNeighbors = new HashSet<>();
            Set<SquaretopiaState> recentlyAddedTransitions = new HashSet<>();
            
            claimer(Squaretopia, freeSquares, currentDistrict, claimedState);
            currentDistrictFreeNeighbors.addAll(getTransitions(Squaretopia, claimedState));
            recentlyAddedTransitions.addAll(getTransitions(Squaretopia, claimedState));
            
            while(freeSquares.size() > ((adjustedSize - 2) * (adjustedSize - 2) - size)) { // only need to generate 1 district
                
                recursiveDistricter(Squaretopia, freeSquares, currentDistrict, currentDistrictFreeNeighbors, recentlyAddedTransitions);
                    // System.out.println("NO SOLUTION!"); // delete;
                    // isValidPartition = false;
                    // break;
                
                // System.out.println("finished one district!"); // delete
                // Squaretopia.checkers(); // delete
                currentDistrict.clear();
                currentDistrictFreeNeighbors.clear();
            }
//            if(isValidPartition) {
                Squaretopia.show();
//                // System.out.println(Math.round(Schwartzberg(Squaretopia)*100) + " " + Math.round(PolsbyPopper(Squaretopia)*100) + " " + Math.round(Reock(Squaretopia)*100) + " " + Math.round(LengthWidthScore(Squaretopia)*100));
                System.out.println(""); // delete
                trialsConducted++;
//            } else {
//                isValidPartition = true;
//            }
        }
        
    }
    
    // generate a set of all the free states
    public static Set<SquaretopiaState> generateSetOfFreeStates(SquaretopiaMatrix matrix) {
        int matrixLength = matrix.data.length; // assumes square matrix!
        Set<SquaretopiaState> setOfStates = new HashSet<>();
        for(int i = 1; i < matrixLength - 1; i++) { // don't want states around the perimeter 
            for(int j = 1; j < matrixLength - 1; j++) {
                setOfStates.add(new SquaretopiaState(i, j));
            }
        }
        return setOfStates;
    }
    
    // pick a random state from the set of free states
    public static SquaretopiaState randomState(Set<SquaretopiaState> set) {
        SquaretopiaState chosenState = new SquaretopiaState(-1, -1);
        int numOfStates = set.size();
        System.out.println("num of neighbor states: " + numOfStates); // delete
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
        matrix.show(); // delete
        System.out.println(""); // delete
        return matrix;
    }
    
    // recursion algorithm
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates, Set<SquaretopiaState> currentDistrict, Set<SquaretopiaState> allPossibleTransitions, Set<SquaretopiaState> recentlyAddedTransitions) {
          if(currentDistrict.size() == (int) (((matrix.data.length - 2) - 1) / 2 + 1)) { // assumes matrix is a square
              // System.out.println("valid map? " + matrix.validMap(matrix)); // delete
              return matrix;
          }
          
          Set<SquaretopiaState> newAllPossibleNeighbors = new HashSet<>();
          newAllPossibleNeighbors.addAll(allPossibleTransitions);
          Set<SquaretopiaState> newRecentlyAddedTransitions = new HashSet<>();
          
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
              if(recursiveDistricter(matrix, freeStates, currentDistrict, newAllPossibleNeighbors, newRecentlyAddedTransitions) == null) {
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
