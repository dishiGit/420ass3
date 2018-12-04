package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MatrixMultiplication {

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
        System.out.print("Parallel Execution Time: " + duration + "\n");

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
        ExecutorService e = Executors.newFixedThreadPool(matrix_size*matrix_size);
        double[] res = new double[matrix_size];
        AtomicInteger cnt = new AtomicInteger();

        class MultiplyTask implements Callable<Double> {
            int mStart;
            int mStop;
            int mElement;
            double partialSum;
            public MultiplyTask(int start, int stop, int element){
                mStart = start;
                mStop = stop;
                mElement = element;
            }
            public Double call(){
                if (mStop-mStart<=1){
                    partialSum = a[mElement][mStart] * b[mStart];
                } else {
                    int mid = (mStop+mStart) / 2;
                    Future<Double> f1 = e.submit(new MultiplyTask(mStart, mid, mElement));
                    Future<Double> f2 = e.submit(new MultiplyTask(mid, mStop, mElement));
                    while(!f1.isDone() || !f2.isDone()) Thread.yield();
                    try {
                        partialSum = f1.get() + f2.get();
                    } catch (Exception e){
                        System.out.print("Something went wrong in MultiplyTask");
                    }
                }
                return partialSum;
            }
        }

        class SplitVectorTask implements Runnable {
            int mStart;
            int mStop;

            SplitVectorTask (int start, int stop){
                mStart = start;
                mStop = stop;
            }

            public void run(){
                if (mStop-mStart<=1){
                    Future<Double> r = e.submit(new MultiplyTask(0, matrix_size, mStart));
                    while(!r.isDone()) Thread.yield();
                    try {
                        res[mStart] = r.get();
                    } catch (Exception e){
                        System.out.print("Something went wrong in SplitVectorTask");
                    }
                    int currentCount = cnt.incrementAndGet();
                    if (currentCount==matrix_size) e.shutdown(); // Shutdown the Executor when all is done
                } else {
                    int mid = (mStop+mStart) / 2;
                    e.execute(new SplitVectorTask(mStart, mid));
                    e.execute(new SplitVectorTask(mid, mStop));
                }
            }
        }

        e.execute(new SplitVectorTask(0, matrix_size));
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

