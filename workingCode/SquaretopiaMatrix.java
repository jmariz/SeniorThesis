package partition;

import java.util.HashSet;
import java.util.Set;

/******************************************************************************
 *  Compilation:  javac Matrix.java
 *  Execution:    java Matrix
 *
 *  A bare-bones immutable data type for M-by-N matrices.
 *
 ******************************************************************************/

final public class SquaretopiaMatrix {
    private final int M;             // number of rows
    private final int N;             // number of columns
    public final SquaretopiaState[][] data;   // M-by-N array

    // create M-by-N matrix of 0's
    public SquaretopiaMatrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new SquaretopiaState[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                data[i][j] = new SquaretopiaState(i, j);
                if(i == 0 || i == M - 1 || j == 0 || j == N - 1) {
                    data[i][j].districtNumber = -1;
                    data[i][j].checked = true;
                }
            }
        }
    }
    
    // print matrix to standard output
    public void show() {
        for (int i = 1; i < M - 1; i++) { // exclude perimeter of -1s
            for (int j = 1; j < N - 1; j++) {
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
    
    public void checkers() {
        for (int i = 1; i < M - 1; i++) {
            for (int j = 1; j < N - 1; j++) { 
                System.out.print(" " + data[i][j].checked);
            }
            System.out.print("");
            System.out.println("");
        }
    }
    
    // creates a duplicate of our matrix so we can change stuff
    public static SquaretopiaMatrix duplicateMatrix(SquaretopiaMatrix matrix) {
        int matrixLength = matrix.data.length; // assumes matrix is a square
        SquaretopiaMatrix matrixDuplicate = new SquaretopiaMatrix(matrixLength, matrixLength);
        for(int i = 0; i < matrixLength; i++) {
            for(int j = 0; j < matrixLength; j++) {
                matrixDuplicate.data[i][j].checked = matrix.data[i][j].checked; // only care about unchecked states
            }
        }
        return matrixDuplicate;
    }
    
    // determines if this mapping is valid by propagating through neighbors and determining isolated groups of less than n states 
    public static Boolean validMap(SquaretopiaMatrix matrix) {
        int matrixLength = matrix.data.length;
        SquaretopiaMatrix matrixCopy = duplicateMatrix(matrix);
        for(int i = 1; i < matrixLength - 1; i++) {
            for (int j = 1; j < matrixLength - 1; j++) {
                if (matrixCopy.data[i][j].checked == false) {
//                    System.out.println("checking: (" + i + " , " + j + ")" ); // delete
                    Set<SquaretopiaState> neighborStates = findContiguousNeighbors(matrixCopy, matrixCopy.data[i][j]);
                    // System.out.println("contiguousStates.size(): " + findContiguousNeighbors(matrixCopy, matrixCopy.data[i][j]).size()); // delete
//                    for(SquaretopiaState state : neighborStates) {
//                        System.out.println(state.toString()); // delete
//                    }
                    // System.out.println("crap: " + findContiguousNeighbors(matrixCopy, matrixCopy.data[i][j]).size()); // delete
//                    System.out.println("neighbors.size(): " + neighborStates.size()); // delete
                        if(neighborStates.size() % (matrixLength - 2) !=  0) {
                            return false;
                        }
                }
            } 
        } 
        return true;
    }
    
    // adds a specified state's contiguous neighbors to a set 
    public static Set<SquaretopiaState> findContiguousNeighbors (SquaretopiaMatrix matrix, SquaretopiaState currentLocation) {
//        System.out.println("passed state " + currentLocation.toString()); // delete
        int curLocRow = currentLocation.row;
        int curLocCol = currentLocation.col;
        Set<SquaretopiaState> contiguousStates = new HashSet<>();
        contiguousStates.add(currentLocation);
        currentLocation.checked = true;
        if(matrix.data[curLocRow - 1][curLocCol].checked == false) { // check availability of the state above
            contiguousStates.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow - 1][curLocCol]));
        }
        if(matrix.data[curLocRow + 1][curLocCol].checked == false) { // check availability of the state below
            contiguousStates.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow + 1][curLocCol]));
        }
        if(matrix.data[curLocRow][curLocCol - 1].checked == false) { // check availability of the state to the left
            contiguousStates.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow][curLocCol - 1]));
        }
        if(matrix.data[curLocRow][curLocCol + 1].checked == false) { // check availability of the state to the right
            contiguousStates.addAll(findContiguousNeighbors(matrix, matrix.data[curLocRow][curLocCol + 1]));
        }
//        System.out.println("from check method contiguousstates.size(): " + contiguousStates.size()); // delete
        return contiguousStates;
    }

//    // create matrix based on 2d array
//    public Matrix(int[][] data) {
//        M = data.length;
//        N = data[0].length;
//        this.data = new int[M][N];
//        for (int i = 0; i < M; i++) {
//            for (int j = 0; j < N; j++) {
//                if(i == 0 || i == M - 1 || j == 0 || j == N - 1) {
//                    this.data[i][j] = -1;
//                } else {
//                    this.data[i][j] = data[i][j];
//                }
//            }
//        }
//    }

//    // copy constructor
//    private Matrix(Matrix A) { this(A.data); }

//    // create and return a random M-by-N matrix with values between 0 and 1
//    public static Matrix random(int M, int N) {
//        Matrix A = new Matrix(M, N);
//        for (int i = 0; i < M; i++)
//            for (int j = 0; j < N; j++)
//                A.data[i][j] = Math.random();
//        return A;
//    }

//    // create and return the N-by-N identity matrix
//    public static Matrix identity(int N) {
//        Matrix I = new Matrix(N, N);
//        for (int i = 0; i < N; i++)
//            I.data[i][i] = 1;
//        return I;
//    }
//
//    // swap rows i and j
//    private void swap(int i, int j) {
//        int[] temp = data[i];
//        data[i] = data[j];
//        data[j] = temp;
//    }

//    // return C = A + B
//    public Matrix plus(Matrix B) {
//        Matrix A = this;
//        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
//        Matrix C = new Matrix(M, N);
//        for (int i = 0; i < M; i++)
//            for (int j = 0; j < N; j++)
//                C.data[i][j] = A.data[i][j] + B.data[i][j];
//        return C;
//    }



}


// Copyright © 2000–2017, Robert Sedgewick and Kevin Wayne.
// Last updated: Fri Oct 20 14:12:12 EDT 2017.
