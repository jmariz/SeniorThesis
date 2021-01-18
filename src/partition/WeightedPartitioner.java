package partition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Joshua Mariz
 * Partitioner that randomly partitions (with a uniform probability) an n by n Squaretopia into n evenly sized contiguous districts
 */
public class WeightedPartitioner {
    
    /**
     * Prepares the partitioning process.
     * @param size Integer number of the Squaretopia's size
     * @param numOfTrials Integer number for the number of Squaretopias we will partition
     * @return void
     */
    public static void Partition (int size, int numOfTrials, double probability) {
        int adjustedSize = size + 2; // includes the outer layer of padding states
        int trialsConducted = 0;
        Boolean isValidPartition;
        while(trialsConducted < numOfTrials) {
            isValidPartition = true;
            SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(adjustedSize, adjustedSize);
            Set<SquaretopiaState> freeSquares = generateSetOfFreeStates(Squaretopia);
            Set<SquaretopiaState> currentDistrict = new HashSet<>();
            Set<SquaretopiaState> currentDistrictFreeNeighbors = new HashSet<>();
            Set<SquaretopiaState> recentlyAddedTransitions = new HashSet<>();
            SquaretopiaState claimedState;
            while(freeSquares.size() > 0) {
                claimedState = deadEnd(Squaretopia, freeSquares);
                if(claimedState == null) {
                    claimedState = randomState(freeSquares, null, probability);
                }
                claimer(Squaretopia, freeSquares, currentDistrict, claimedState);
                currentDistrictFreeNeighbors.addAll(getTransitions(Squaretopia, claimedState));
                recentlyAddedTransitions.addAll(getTransitions(Squaretopia, claimedState));
                if (recursiveDistricter(Squaretopia, freeSquares, currentDistrict, currentDistrictFreeNeighbors, recentlyAddedTransitions, claimedState, probability) == null) {
                    isValidPartition = false;
                    break;
                }
                currentDistrict.clear();
                currentDistrictFreeNeighbors.clear();
            }
            if(isValidPartition) {
                // Squaretopia.show();
                System.out.println(Math.round(Squaretopia.LengthWidthScore()*100));
                // System.out.println(Math.round(Squaretopia.Schwartzberg()*100) + " " + Math.round(Squaretopia.PolsbyPopper()*100) + " " + Math.round(Squaretopia.Reock()*100) + " " + Math.round(Squaretopia.LengthWidthScore()*100));
                // System.out.println("");
                trialsConducted++;
            }
        }
        
    }
    
    // recursion algorithm
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates, Set<SquaretopiaState> currentDistrict, Set<SquaretopiaState> allPossibleTransitions, Set<SquaretopiaState> recentlyAddedTransitions, SquaretopiaState recentlyAddedState, double probability) {
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
                  nextState = randomState(newAllPossibleNeighbors, recentlyAddedState, probability);
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
              if(recursiveDistricter(matrix, freeStates, currentDistrict, newAllPossibleNeighbors, newRecentlyAddedTransitions, nextState, probability) == null) {
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
    // this method includes weighting
    // p is the minimum p of selecting the sequential state (the state that follows if we continue in the same direction)
    public static SquaretopiaState randomState(Set<SquaretopiaState> set, SquaretopiaState recentlyClaimedState, double p) {
        p = p / 100;
        SquaretopiaState chosenState = new SquaretopiaState(-1, -1);
        SquaretopiaState targetState = nextSequentialState(recentlyClaimedState); // state that will be assigned the highest p
        int indexOfTargetStateInSet = findIndex(set, targetState);
        if(indexOfTargetStateInSet == -1) { // each state in set has equal p
            int numOfStates = set.size();
            int chosenStateIndex = (int) Math.ceil((Math.random() * numOfStates));
            Iterator<SquaretopiaState> it = set.iterator();
            for (int i = 0; i < chosenStateIndex; i++) {
                chosenState = it.next();
            }
            return chosenState;
        } else { // weigh target state with a greater p
            double numOfStates = set.size();
            double remainingProbability = 1 - p;
            double nonTargetStateProbability;
            if(set.size() == 1) {
                nonTargetStateProbability = 0;
            } else {
                nonTargetStateProbability = remainingProbability / (numOfStates - 1);
            }
            double previousUpperBound = 0;
            for(SquaretopiaState state : set) {
                state.lowerBoundForProbability = previousUpperBound;
                if(state.equals(targetState)) {
                    state.upperBoundForProbability = previousUpperBound + p;
                } else {
                    state.upperBoundForProbability = previousUpperBound + nonTargetStateProbability;
                }
                previousUpperBound = state.upperBoundForProbability;
            }
            double randomNumber = Math.random();
            for(SquaretopiaState state : set) {
                if((state.lowerBoundForProbability <= randomNumber) && (randomNumber < state.upperBoundForProbability)) {
                    return state;
                }
            }
            return null; // something went wrong
        }
    }
    
    // determines the index of a given state in some given set
    // return -1 if state is not in set
    public static int findIndex(Set<SquaretopiaState> someSet, SquaretopiaState desiredState) {
        if(desiredState != null) {
            Iterator<SquaretopiaState> it = someSet.iterator();
            int indexOfDesiredState = 0;
            int maxIterations = someSet.size();
            SquaretopiaState currentState;
            for (int i = 0; i < maxIterations; i++) {
                currentState = it.next();
                if(currentState.equals(desiredState)) {
                    return indexOfDesiredState;
                }
                indexOfDesiredState++;
            }
        }
        return -1;
    }
    
    // determines the next state in some direction given a current state and direction 
    public static SquaretopiaState nextSequentialState(SquaretopiaState state) {
        SquaretopiaState sequentialState = null;
        if(state != null) {
            if(state.direction == 1) {
                sequentialState = new SquaretopiaState(state.row - 1, state.col);
                // sequentialState.direction = 1;
            } else if (state.direction == 2) {
                sequentialState = new SquaretopiaState(state.row, state.col + 1);
                sequentialState.direction = 2;
            } else if (state.direction == 3) {
                sequentialState = new SquaretopiaState(state.row + 1, state.col);
                // sequentialState.direction = 3;
            } else if (state.direction == 4) {
                sequentialState = new SquaretopiaState(state.row, state.col - 1);
                // sequentialState.direction = 4;
            }
        }
        return sequentialState;
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
        // int parentDirection = currentLocation.direction;
        Set<SquaretopiaState> possibleTransitions = new HashSet<>();
        if(matrix.data[curLocRow - 1][curLocCol].districtNumber == 0) { // check availability of the state above
            SquaretopiaState possibleTransitionAbove = new SquaretopiaState(curLocRow - 1, curLocCol);
            // possibleTransitionAbove.direction = parentDirection == -1 ? 1 : parentDirection;
            possibleTransitionAbove.direction = 1;
            possibleTransitions.add(possibleTransitionAbove);
        }
        if(matrix.data[curLocRow + 1][curLocCol].districtNumber == 0) { // check availability of the state below
            SquaretopiaState possibleTransitionBelow = new SquaretopiaState(curLocRow + 1, curLocCol);
            // possibleTransitionBelow.direction = parentDirection == -1 ? 3 : parentDirection;
            possibleTransitionBelow.direction = 3;
            possibleTransitions.add(possibleTransitionBelow);
        }
        if(matrix.data[curLocRow][curLocCol - 1].districtNumber == 0) { // check availability of the state to the left
            SquaretopiaState possibleTransitionLeft = new SquaretopiaState(curLocRow, curLocCol - 1);
            // possibleTransitionLeft.direction = parentDirection == -1 ? 4 : parentDirection;
            possibleTransitionLeft.direction = 4;
            possibleTransitions.add(possibleTransitionLeft);
        }
        if(matrix.data[curLocRow][curLocCol + 1].districtNumber == 0) { // check availability of the state to the right
            SquaretopiaState possibleTransitionRight = new SquaretopiaState(curLocRow, curLocCol + 1);
            // possibleTransitionRight.direction = parentDirection == -1 ? 2 : parentDirection;
            possibleTransitionRight.direction = 2;
            possibleTransitions.add(possibleTransitionRight);
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
