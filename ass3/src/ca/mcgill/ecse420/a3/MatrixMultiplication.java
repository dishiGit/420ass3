package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {

    private static final int NUMBER_THREADS = 1;
    private static final int MATRIX_SIZE = 3;

    public static void main(String[] args) {

        // Generate two random matrices, same size
        double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
        double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
        sequentialMultiplyMatrix(a, b);
        parallelMultiplyMatrix(a, b);
    }

    /**
     * Returns the result of a sequential matrix multiplication
     * The two matrices are randomly generated
     * @param a is the first matrix
     * @param b is the second matrix
     * @return the result of the multiplication
     * */
    public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
        int matrix_size = a.length;
        double[][] res = new double[matrix_size][matrix_size];
        for (int i=0; i < matrix_size; i++){
            for (int j=0; j < matrix_size; j++){
                for (int k=0; k < matrix_size; k++) {
                    res[i][j] += a[i][k] * b[k][j];
                }
            }

        }
        return res;
    }

    /**
     * Returns the result of a concurrent matrix multiplication
     * The two matrices are randomly generated
     * @param a is the first matrix
     * @param b is the second matrix
     * @return the result of the multiplication
     * */
    public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
        int matrix_size = a.length;;
        int task_size = 1;
        double[][] res = new double[matrix_size][matrix_size];

        class MultiplyTask implements Runnable{
            int mStart;
            int mStop;
            public MultiplyTask(int start, int stop){
                mStart = start;
                mStop = stop;
            }
            public void run(){
                for (int i = mStart; i < mStop; i++){
                    for (int j = 0; j < matrix_size; j++){
                        for (int k=0; k < matrix_size; k++) {
                            double ans = a[i][k] * b[k][j];
                            res[i][j] += ans;
                        }
                    }
                }
            }
        }

        ExecutorService e = Executors.newFixedThreadPool(NUMBER_THREADS);
        for (int t = 0; t < matrix_size; t += task_size){
            if (matrix_size - t < task_size){
                e.execute(new MultiplyTask(t, matrix_size));
            } else {
                e.execute(new MultiplyTask(t, t+task_size));
            }
        }
        e.shutdown();
        return res;
    }

    /**
     * Populates a matrix of given size with randomly generated integers between 0-10.
     * @param numRows number of rows
     * @param numCols number of cols
     * @return matrix
     */
    private static double[][] generateRandomMatrix (int numRows, int numCols) {
        double matrix[][] = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }
}
