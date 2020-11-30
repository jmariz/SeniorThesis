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
public class WeightedPartitioner {
    
    /**
     * Public interface for Partition
     */
    
    // sets everything up
    public static void Partition (int size, int numOfTrials, double threshold) {
        int adjustedSize = size + 2;
        int trialsConducted = 0;
        
        while(trialsConducted < numOfTrials) {
            Boolean isValidPartition = true;
            SquaretopiaMatrix Squaretopia = new SquaretopiaMatrix(adjustedSize, adjustedSize);
            // Squaretopia.show();
            Set<SquaretopiaState> freeSquares = generateSetOfFreeStates(Squaretopia);
            // System.out.println("freeSquares.size(): " + freeSquares.size()); // delete
            Set<SquaretopiaState> currentDistrict = new HashSet<>();
            Set<SquaretopiaState> currentDistrictFreeNeighbors = new HashSet<>();
            Set<SquaretopiaState> recentlyAddedTransitions = new HashSet<>();
            SquaretopiaState claimedState;
    //        currentDistrict.add(claimedState);
            while(freeSquares.size() > 0) {
                claimedState = deadEnd(Squaretopia, freeSquares);
                if(claimedState == null) {
                    claimedState = randomState(freeSquares, null, threshold);
                }
                // claimedState = randomState(freeSquares);
                claimer(Squaretopia, freeSquares, currentDistrict, claimedState);
                
                currentDistrictFreeNeighbors.addAll(getTransitions(Squaretopia, claimedState));
                recentlyAddedTransitions.addAll(getTransitions(Squaretopia, claimedState));
                // System.out.println("currentDistrictFreeNeighbors.size(): " + currentDistrictFreeNeighbors.size()); // delete
                // System.out.println("recentlyAddedTransitions.size(): " + recentlyAddedTransitions.size()); // delete
                if (recursiveDistricter(Squaretopia, freeSquares, currentDistrict, currentDistrictFreeNeighbors, recentlyAddedTransitions, claimedState, threshold) == null) {
                    // System.out.println("NO SOLUTION!"); // delete;
                    isValidPartition = false;
                    break;
                }
                // System.out.println("finished one district!"); // delete
                // Squaretopia.checkers(); // delete
                currentDistrict.clear();
                currentDistrictFreeNeighbors.clear();
                // System.out.println("currentDistrict.size(): " + currentDistrict.size()); // delete
                // System.out.println("end of while loop"); // delete
                // currentDistrict.removeAll(currentDistrict);
    //            if(freeSquares.size() != 0) {
    //                claimedState = randomState(freeSquares);
    //                freeSquares.remove(claimedState);
    //                updateMatrix(Squaretopia, freeSquares, claimedState);
    //                currentDistrict.add(claimedState);
    //            }
            }
            if(isValidPartition) {
                Squaretopia.show();
                // System.out.println(Math.round(Schwartzberg(Squaretopia)*100) + " " + Math.round(PolsbyPopper(Squaretopia)*100) + " " + Math.round(Reock(Squaretopia)*100) + " " + Math.round(LengthWidthScore(Squaretopia)*100));
                System.out.println(""); // delete
                trialsConducted++;
            } else {
                isValidPartition = true;
            }
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
    
    // calculate the perimeters of each district in a partition
    public static int[] calculatePerimeters(SquaretopiaMatrix matrix) {
        int numOfDistricts = matrix.data.length - 2;
        int[] perimetersArray = new int[numOfDistricts];
        for(int i = 0; i < numOfDistricts; i++) {
            perimetersArray[i] = 4 * numOfDistricts;
        }
        // System.out.println(""); // delete
        for(int i = 1; i < matrix.data.length - 1; i++) {
            for(int j = 1; j < matrix.data.length - 1; j++) {
                int districtNumOfCurrentState = matrix.data[i][j].districtNumber;
                if(districtNumOfCurrentState == matrix.data[i - 1][j].districtNumber) {
                    perimetersArray[districtNumOfCurrentState - 1]--;
                }
                if(districtNumOfCurrentState == matrix.data[i + 1][j].districtNumber) {
                    perimetersArray[districtNumOfCurrentState - 1]--;
                }
                if(districtNumOfCurrentState == matrix.data[i][j - 1].districtNumber) {
                    perimetersArray[districtNumOfCurrentState - 1]--;
                }
                if(districtNumOfCurrentState == matrix.data[i][j + 1].districtNumber) {
                    perimetersArray[districtNumOfCurrentState - 1]--;
                }
            }
        }
        
//        System.out.println("PerimetersArray"); // delete
//        for(int i = 0; i < perimetersArray.length; i++) { // delete
//            System.out.print(perimetersArray[i] + " "); // delete
//        }
//        System.out.println(""); // delete
        return perimetersArray;
    }
    
    // pick a random state from the set of free states
    // this method includes weighting
    // threshold is the minimum probability of selecting the sequential state (the state that follows if we continue in the same direction)
    public static SquaretopiaState randomState(Set<SquaretopiaState> set, SquaretopiaState recentlyClaimedState, double threshold) {
        SquaretopiaState chosenState = new SquaretopiaState(-1, -1);
        SquaretopiaState targetState = nextSequentialState(recentlyClaimedState); // state that will be assigned the highest probability
        int indexOfTargetStateInSet = findIndex(set, targetState);
        int chosenStateIndex;
        Iterator<SquaretopiaState> it = set.iterator();
        chosenState = it.next();
        if(indexOfTargetStateInSet == -1) { // each state in set has equal probability
            int numOfStates = set.size();
            // System.out.println("number of choices for current state: " + numOfStates); // delete
            chosenStateIndex = (int) Math.floor((Math.random() * numOfStates));
            // System.out.println("chosenStateIndex: " + chosenStateIndex); // delete
        } else { // weigh target state with a greater probability
            int numOfOtherStates = set.size() - 1;
            int numOfAdditionalCopies = (int) Math.ceil(Double.valueOf(numOfOtherStates) / (1 - threshold / 100));
            int[] arrayOfIndexes = new int[1 + numOfOtherStates + numOfAdditionalCopies];
            for(int i = 0; i < numOfOtherStates + 1; i++) {
                arrayOfIndexes[i] = i;
            }
            for(int i = 0; i < numOfAdditionalCopies; i++) {
                arrayOfIndexes[numOfOtherStates + 1 + i] = indexOfTargetStateInSet;
            }
            chosenStateIndex = arrayOfIndexes[(int) Math.floor((Math.random() * arrayOfIndexes.length))];
        }
        for (int i = 0; i < chosenStateIndex; i++) {
            chosenState = it.next();
        }
        return chosenState;
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
                sequentialState.direction = 1;
            } else if (state.direction == 2) {
                sequentialState = new SquaretopiaState(state.row, state.col + 1);
                sequentialState.direction = 2;
            } else if (state.direction == 3) {
                sequentialState = new SquaretopiaState(state.row + 1, state.col);
                sequentialState.direction = 3;
            } else if (state.direction == 4) {
                sequentialState = new SquaretopiaState(state.row, state.col - 1);
                sequentialState.direction = 4;
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
        int parentDirection = currentLocation.direction;
        Set<SquaretopiaState> possibleTransitions = new HashSet<>();
        if(matrix.data[curLocRow - 1][curLocCol].districtNumber == 0) { // check availability of the state above
            SquaretopiaState possibleTransitionAbove = new SquaretopiaState(curLocRow - 1, curLocCol);
            possibleTransitionAbove.direction = parentDirection == -1 ? 1 : parentDirection;
            possibleTransitions.add(possibleTransitionAbove);
        }
        if(matrix.data[curLocRow + 1][curLocCol].districtNumber == 0) { // check availability of the state below
            SquaretopiaState possibleTransitionBelow = new SquaretopiaState(curLocRow + 1, curLocCol);
            possibleTransitionBelow.direction = parentDirection == -1 ? 3 : parentDirection;
            possibleTransitions.add(possibleTransitionBelow);
        }
        if(matrix.data[curLocRow][curLocCol - 1].districtNumber == 0) { // check availability of the state to the left
            SquaretopiaState possibleTransitionLeft = new SquaretopiaState(curLocRow, curLocCol - 1);
            possibleTransitionLeft.direction = parentDirection == -1 ? 4 : parentDirection;
            possibleTransitions.add(possibleTransitionLeft);
        }
        if(matrix.data[curLocRow][curLocCol + 1].districtNumber == 0) { // check availability of the state to the right
            SquaretopiaState possibleTransitionRight = new SquaretopiaState(curLocRow, curLocCol + 1);
            possibleTransitionRight.direction = parentDirection == -1 ? 2 : parentDirection;
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
    
    // recursion algorithm
    public static SquaretopiaMatrix recursiveDistricter (SquaretopiaMatrix matrix, Set<SquaretopiaState> freeStates, Set<SquaretopiaState> currentDistrict, Set<SquaretopiaState> allPossibleTransitions, Set<SquaretopiaState> recentlyAddedTransitions, SquaretopiaState recentlyAddedState, double threshold) {
          if(currentDistrict.size() == (matrix.data.length - 2)) { // assumes matrix is a square
              // System.out.println("valid map? " + matrix.validMap(matrix)); // delete
              if(matrix.validMap(matrix)) {
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
                  nextState = randomState(newAllPossibleNeighbors, recentlyAddedState, threshold);
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
              if(recursiveDistricter(matrix, freeStates, currentDistrict, newAllPossibleNeighbors, newRecentlyAddedTransitions, nextState, threshold) == null) {
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
    
    // calculates the sum of the length-width ratios for all districts in a given Squaretopia 
    public static double LengthWidthScore(SquaretopiaMatrix matrix) {
        // locate and save extrema states
        double scoreTotal = 0;
        double numOfDistricts = matrix.data.length - 2;
        SquaretopiaState[][] extrema = new SquaretopiaState[(int) numOfDistricts][4]; // each district has four states in this order min length, max length, min width, max width
        for (int i = 1; i < matrix.data.length - 1; i++) {
            for (int j = 1; j < matrix.data.length - 1; j++) {
                int districtNumberOfThisState = matrix.data[i][j].districtNumber;
                SquaretopiaState thisState = matrix.data[i][j];
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
//        for (int i = 0; i < extrema.length; i++) {
//            System.out.println("district " + (i + 1)); // delete
//            System.out.println("min row: " + extrema[i][0].toString()); // delete
//            System.out.println("max row: " + extrema[i][1].toString()); // delete
//            System.out.println("min col: " + extrema[i][2].toString()); // delete
//            System.out.println("max col: " + extrema[i][3].toString()); // delete
//            System.out.println(""); // delete
//            System.out.println(""); // delete
//        }
        for (int i = 0; i < extrema.length; i++) {
            // System.out.println("for district " + (i + 1) + " ..."); // delete
            length = Math.abs(extrema[i][0].row - extrema[i][1].row) + 1; // add one to count properly
            // System.out.println("length: " + length); // delete
            width = Math.abs(extrema[i][2].col - extrema[i][3].col) + 1;
            // System.out.println("width: " + width); // delete
            if (length < width) {
                // System.out.println("district " + (i + 1) +  " score: " + length / width); // delete
                // System.out.println("-----"); // delete
                scoreTotal += length / width;
            } else {
                // System.out.println("district " + (i + 1) +  " score: " + width / length); // delete
                // System.out.println("-----"); // delete
                scoreTotal += width / length;
            }
        }
        double averageScore = scoreTotal / numOfDistricts;
        // System.out.println(averageScore); // delete
        // System.out.println("\n\n\n"); // delete
        return averageScore;
    }
    
    // calculates the district area to minimum bounding square ratios for all districts in a given Squaretopia 
    public static double Reock(SquaretopiaMatrix matrix) {
        // locate and save extrema states
        double scoreTotal = 0;
        double numOfDistricts = matrix.data.length - 2;
        SquaretopiaState[][] extrema = new SquaretopiaState[(int) numOfDistricts][4]; // each district has four states in this order min length, max length, min width, max width
        for (int i = 1; i < matrix.data.length - 1; i++) {
            for (int j = 1; j < matrix.data.length - 1; j++) {
                int districtNumberOfThisState = matrix.data[i][j].districtNumber;
                SquaretopiaState thisState = matrix.data[i][j];
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
//        for (int i = 0; i < extrema.length; i++) {
//            System.out.println("district " + (i + 1)); // delete
//            System.out.println("min row: " + extrema[i][0].toString()); // delete
//            System.out.println("max row: " + extrema[i][1].toString()); // delete
//            System.out.println("min col: " + extrema[i][2].toString()); // delete
//            System.out.println("max col: " + extrema[i][3].toString()); // delete
//            System.out.println(""); // delete
//            System.out.println(""); // delete
//        }
        for (int i = 0; i < extrema.length; i++) {
            // System.out.println("for district " + (i + 1) + " ..."); // delete
            length = Math.abs(extrema[i][0].row - extrema[i][1].row) + 1; // add one to count properly
            // System.out.println("length: " + length); // delete
            width = Math.abs(extrema[i][2].col - extrema[i][3].col) + 1;
            // System.out.println("width: " + width); // delete
            if (length < width) {
                // System.out.println("district " + (i + 1) +  " score: " + numOfDistricts / (width * width)); // delete
                // System.out.println("-----"); // delete
                scoreTotal += numOfDistricts / (width * width);
            } else {
                // System.out.println("district " + (i + 1) +  " score: " + numOfDistricts / (length * length)); // delete
                // System.out.println("-----"); // delete
                scoreTotal += numOfDistricts / (length * length);
            }
        }
        double averageScore = scoreTotal / numOfDistricts;
        // System.out.println(averageScore); // delete
        //System.out.println("\n\n\n"); // delete
        return averageScore;
    }
    
    // calculates the ratio of the district area to the area of a square with perimeter equal to the district's perimeter for all districts in a given Squaretopia
    public static double PolsbyPopper(SquaretopiaMatrix matrix) {
        int[] perimetersArray = calculatePerimeters(matrix); 
        double scoreTotal = 0;
        int numOfDistricts = matrix.data.length - 2;
        double areaOfADistrict = matrix.data.length - 2;
        for(int i = 0; i < perimetersArray.length; i++) {
            scoreTotal += areaOfADistrict / Math.pow(Double.valueOf(perimetersArray[i]) / 4, 2);
        }
        double averageScore = scoreTotal / numOfDistricts;
        // System.out.println(averageScore); // delete
        // System.out.println("\n\n\n"); // delete
        return averageScore;
    }
    
    // calculates the ratio of the district perimeter to the perimeter of a square with area equal to the district's area for all districts in a given Squaretopia
    public static double Schwartzberg(SquaretopiaMatrix matrix) {
        int[] perimetersArray = calculatePerimeters(matrix); 
        double scoreTotal = 0;
        int numOfDistricts = matrix.data.length - 2;
        double areaOfADistrict = matrix.data.length - 2;
        for(int i = 0; i < perimetersArray.length; i++) {
            scoreTotal += (4 * Math.sqrt(Double.valueOf(areaOfADistrict))) / Double.valueOf(perimetersArray[i]);
        }
        double averageScore = scoreTotal / numOfDistricts;
        // System.out.println(averageScore); // delete
        // System.out.println("\n\n\n"); // delete
        return averageScore;
    }
}
