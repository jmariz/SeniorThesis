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
        // System.out.println("size: " + size + " by " + size); // delete
        // System.out.println("Creating set of states..."); // delete
        Set<SquaretopiaState> freeSquares = generateSetOfFreeStates(Squaretopia);
        System.out.println("freeSquares.size(): " + freeSquares.size()); // delete
        //for(SquaretopiaState states : freeSquares) { //delete
            // System.out.println(states.toString()); // delete
        // } // delete
        // System.out.println("Num of Free States: " + freeSquares.size()); // delete
        // System.out.println("Picking random state..."); // delete
//        SquaretopiaState claimedState = randomState(freeSquares);
        // System.out.println("claimedState.row: " + claimedState.row); // delete
        // System.out.println("claimedState.col: " + claimedState.col); // delete
        // System.out.println("Removing from set: " + claimedState.toString()); // delete
//        freeSquares.remove(claimedState);
        // System.out.println("Num of Free States: " + freeSquares.size()); // delete
        // System.out.println("Updating matrix..."); // delete
//        updateMatrix(Squaretopia, freeSquares, claimedState);
        Set<SquaretopiaState> currentDistrict = new HashSet<>();
//        currentDistrict.add(claimedState);
        while(freeSquares.size() > 0) {
            recursiveDistricter(Squaretopia, freeSquares, currentDistrict, null);
            System.out.println("finished one district!"); // delete
            Squaretopia.checkers(); // delete
            System.out.println("currentDistrict.size(): " + currentDistrict.size()); // delete
            currentDistrict.removeAll(currentDistrict);
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
    
    // update the matrix
    public static SquaretopiaMatrix updateMatrix(SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates, SquaretopiaState claimedState) {
        System.out.println("claimedState.row: " + claimedState.row); // delete
        System.out.println("claimedState.col: " + claimedState.col); // delete
        System.out.println("getCurrentDistrictNumber(matrix, freeStates): " + getCurrentDistrictNumber(matrix, freeStates)); // delete
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
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates, Set<SquaretopiaState> currentDistrict, SquaretopiaState previousState) {
        if(previousState == null) {
          previousState = randomState(freeStates);
          freeStates.remove(previousState);
          updateMatrix(matrix, freeStates, previousState);
          currentDistrict.add(previousState);
        }
        System.out.println("currentDistrict.size() above if: " + currentDistrict.size()); // delete
        System.out.println("matrixdatalength: " + matrix.data.length); // delete
        System.out.println("modulo result: " + currentDistrict.size() % (matrix.data.length - 2)); // delete
        if(currentDistrict.size() % (matrix.data.length - 2) == 0) { // assumes matrix is a square
            System.out.println("valid map? " + matrix.validMap(matrix)); // delete
            if(matrix.validMap(matrix)) {
                currentDistrict.clear();
                System.out.println("currentDistrict.size() in if just cleared: " + currentDistrict.size()); // delete
                System.out.println("valid map!"); // delete
                return matrix;
            } else {
                return null;
            }
        }
        SquaretopiaState currentState = randomState(currentDistrict);
        Set<SquaretopiaState> temporarySet = new HashSet<>();
        while(getTransitions(matrix, currentState).size() == 0) { // temporarily remove states with no transitions
            temporarySet.add(currentState);
            currentDistrict.remove(currentState);
            currentState = randomState(currentDistrict);
        }
        currentDistrict.addAll(temporarySet); // add back states with no transitions 
        Set<SquaretopiaState> possibleTransitions = getTransitions(matrix, currentState);
        while(possibleTransitions.size() != 0) {
            SquaretopiaState nextState = randomState(possibleTransitions);
            currentDistrict.add(nextState);
            freeStates.remove(nextState);
            updateMatrix(matrix, freeStates, nextState);
            if(recursiveDistricter(matrix, freeStates, currentDistrict, nextState) == null) {
                possibleTransitions.remove(nextState);
                currentDistrict.remove(nextState);
                freeStates.add(nextState);
                updateMatrix(matrix, freeStates, nextState);
            } else {
                return matrix;
            }
        }
        System.out.println("returning null..."); // delete
        return null;
    }
}