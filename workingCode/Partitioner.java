package partition;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Partition
 */
public class Partitioner {
    
    /**
     * Public interface for Partition
     */
    
    // sets everything up
    public static void Partition (int size) {
        int adjustedSize = size + 2;
        SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(adjustedSize, adjustedSize);
        Squaretopia.show();
        Set<SquaretopiaState> freeSquares = generateSetOfFreeStates(Squaretopia);
        System.out.println("freeSquares.size(): " + freeSquares.size()); // delete
        Set<SquaretopiaState> currentDistrict = new HashSet<>();
        Set<SquaretopiaState> currentDistrictFreeNeighbors = new HashSet<>();
        Set<SquaretopiaState> recentlyAddedTransitions = new HashSet<>();
        SquaretopiaState claimedState;
//        currentDistrict.add(claimedState);
        while(freeSquares.size() > 0) {
            claimedState = deadEnd(Squaretopia, freeSquares);
            if(claimedState == null) {
                claimedState = randomState(freeSquares);
            }
            // claimedState = randomState(freeSquares);
            claimer(Squaretopia, freeSquares, currentDistrict, claimedState);
            
            currentDistrictFreeNeighbors.addAll(getTransitions(Squaretopia, claimedState));
            recentlyAddedTransitions.addAll(getTransitions(Squaretopia, claimedState));
            System.out.println("currentDistrictFreeNeighbors.size(): " + currentDistrictFreeNeighbors.size()); // delete
            System.out.println("recentlyAddedTransitions.size(): " + recentlyAddedTransitions.size()); // delete
            if (recursiveDistricter(Squaretopia, freeSquares, currentDistrict, currentDistrictFreeNeighbors, recentlyAddedTransitions) == null) {
                System.out.println("NO SOLUTION!"); // delete;
                break;
            }
            System.out.println("finished one district!"); // delete
            Squaretopia.checkers(); // delete
            currentDistrict.clear();
            currentDistrictFreeNeighbors.clear();
            System.out.println("currentDistrict.size(): " + currentDistrict.size()); // delete
            System.out.println("end of while loop"); // delete
            // currentDistrict.removeAll(currentDistrict);
//            if(freeSquares.size() != 0) {
//                claimedState = randomState(freeSquares);
//                freeSquares.remove(claimedState);
//                updateMatrix(Squaretopia, freeSquares, claimedState);
//                currentDistrict.add(claimedState);
//            }
        }
        Squaretopia.show();
        System.out.println("reached end :) "); // delete
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
        System.out.println("number of choices for current state: " + numOfStates); // delete
        int chosenStateIndex = (int) Math.ceil((Math.random() * numOfStates));
        Iterator<SquaretopiaState> it = set.iterator();
        // System.out.println("chosenStateIndex: " + chosenStateIndex); // delete
        for (int i = 0; i < chosenStateIndex; i++) {
            chosenState = it.next();
        }
        System.out.println("chose this state: " + chosenState.toString()); // delete
        return chosenState;
    }
    
    // determine which district we are constructing
    public static int getCurrentDistrictNumber(SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates) {
        System.out.println("freeStates.size(): " + freeStates.size()); // delete
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
    
    // get a specified state's number of free neighbors
    public int getTransitionsScore (SquaretopiaMatrix matrix, SquaretopiaState currentLocation) {
        int transitionsScore = 0;
        int curLocRow = currentLocation.row;
        int curLocCol = currentLocation.col;
        if(matrix.data[curLocRow - 1][curLocCol].districtNumber == 0) { // check availability of the state above
            transitionsScore++;
        }
        if(matrix.data[curLocRow + 1][curLocCol].districtNumber == 0) { // check availability of the state below
            transitionsScore++;
        }
        if(matrix.data[curLocRow][curLocCol - 1].districtNumber == 0) { // check availability of the state to the left
            transitionsScore++;
        }
        if(matrix.data[curLocRow][curLocCol + 1].districtNumber == 0) { // check availability of the state to the right
            transitionsScore++;
        }
        return transitionsScore;
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
        return matrix;
    }
    
    // recursion algorithm
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates, Set<SquaretopiaState> currentDistrict, Set<SquaretopiaState> allPossibleTransitions, Set<SquaretopiaState> recentlyAddedTransitions) {
          if(currentDistrict.size() == (matrix.data.length - 2)) { // assumes matrix is a square
              System.out.println("valid map? " + matrix.validMap(matrix)); // delete
              if(matrix.validMap(matrix)) {
                  recentlyAddedTransitions.clear();
                  System.out.println("currentDistrict.size() in if just cleared: " + currentDistrict.size()); // delete
                  System.out.println("valid map!"); // delete
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
              SquaretopiaState nextState = deadEnd(matrix, newAllPossibleNeighbors);
              if(nextState == null) {
                  nextState = randomState(newAllPossibleNeighbors);
              }
              System.out.println("nextState... " + nextState.toString()); // delete
              claimer(matrix, freeStates, currentDistrict, nextState);
              newAllPossibleNeighbors.remove(nextState);
              newRecentlyAddedTransitions = recentlyAddedTransitions(newAllPossibleNeighbors, getTransitions(matrix, nextState));
              System.out.println("recentlyAddedTransitions.size(): " + newRecentlyAddedTransitions.size()); // delete
              newAllPossibleNeighbors.addAll(getTransitions(matrix, nextState));
              System.out.println("newAllPossibleNeighbors.size(): " + newAllPossibleNeighbors.size()); // delete
              for(SquaretopiaState state : newAllPossibleNeighbors) { // delete
                  System.out.println(state.toString()); // delete
              }
              System.out.println(""); // delete
              System.out.println(""); // delete
              System.out.println(""); // delete
              if(recursiveDistricter(matrix, freeStates, currentDistrict, newAllPossibleNeighbors, newRecentlyAddedTransitions) == null) {
                  System.out.println("came back to line 200"); // delete
                  newAllPossibleNeighbors.remove(nextState);
                  returner(matrix, freeStates, currentDistrict, nextState);
                  newAllPossibleNeighbors.removeAll(newRecentlyAddedTransitions);
              } else {
                  return matrix;
              }
              System.out.println("reached end of while loop in recurser"); // delete
          }
          System.out.println("returning null from the end of the recurser..."); // delete
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