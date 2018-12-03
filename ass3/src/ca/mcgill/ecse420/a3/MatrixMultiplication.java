package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {

    private static final int NUMBER_THREADS = 4;
    private static final int MATRIX_SIZE = 2000;

    public static void main(String[] args) {
        long startTime;
        long endTime;
        long duration;
        // Generate two random matrices, same size
        double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
        double[] b = generateRandomVector(MATRIX_SIZE);

        startTime = System.nanoTime();
        double[] res_seq = sequentialMultiply(a, b);
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        System.out.print("Sequential Execution Time: " + duration + "\n");

        startTime = System.nanoTime();
        double[] res_par = parallelMultiply(a, b);
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        System.out.print("Sequential Execution Time: " + duration + "\n");

        if(Arrays.equals(res_seq,res_par)) System.out.print("The results match!\n");
    }

    /**
     * Returns the result of a sequential matrix multiplication
     * The two matrices are randomly generated
     * @param a is the first matrix
     * @param b is the second matrix
     * @return the result of the multiplication
     * */
    public static double[] sequentialMultiply(double[][] a, double[] b) {
        int matrix_size = a.length;
        double[] res = new double[matrix_size];
        for (int i=0; i < matrix_size; i++){
            for (int j=0; j < matrix_size; j++){
                res[i] += a[i][j] * b[j];
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
    public static double[] parallelMultiply(double[][] a, double[] b) {
        int matrix_size = a.length;;
        int task_size = (int) Math.ceil((double)matrix_size/(double)NUMBER_THREADS);
        double[] res = new double[matrix_size];

        class MultiplyTask implements Runnable{
            int mStart;
            int mStop;
            double[] mRes;
            public MultiplyTask(int start, int stop){
                mStart = start;
                mStop = stop;
                mRes = new double[stop-start];
            }
            public void run(){
                for (int i = mStart; i < mStop; i++){
                    for (int j = 0; j < matrix_size; j++){
                            double ans = a[i][j] * b[j];
                            mRes[i-mStart] += ans;
                    }
                }
                synchronized (res){
                    for (int i = mStart; i<mStop; i++){
                        res[i] = mRes[i-mStart];
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
        while (!e.isTerminated()){};
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

    private static double[] generateRandomVector (int numCols) {
        double vector[] = new double[numCols];
        for (int col = 0 ; col < numCols ; col++ ) {
            vector[col] = (double) ((int) (Math.random() * 10.0));
        }
        return vector;
    }
}
