package lab4;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    private static int[][] matrixA;
    private static int[][] matrixB;
    private static int[][] matrixC;
    private static int colA = 0;
    private static int rowA = 0;
    private static int colB = 0;
    private static int rowB = 0;
    private static int rowC = 0;
    private static int colC = 0;

    public static void main(String[] args) throws InterruptedException {
        getDimensions();
        fillMatrix(matrixA);
        fillMatrix(matrixB);
        fillMatrix(matrixC);
        System.out.println("The resulted matrix A:");
        printBackMatrix(matrixA);
        System.out.println("The resulted matrix B:");
        printBackMatrix(matrixB);
        System.out.println("The resulted matrix C:");
        printBackMatrix(matrixC);

        int[][] matrixProduct = multiplyMatrixWithThreads(matrixA, matrixB, 3);

        System.out.println("Matrix product is being written to file result.txt");
        try {
            printMatrixToFile(matrixProduct);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int[][] multiplyMatrixWithThreads(int[][] matrixA, int[][] matrixB, int nrThreads) throws InterruptedException {

        int index = 0;
        int nrCellsThread = (rowA * colB)/ nrThreads;
        int copyNrLinesThread = nrCellsThread;
        int remained = (rowA * colB) % nrThreads;
        int[][] matrixProduct = new int[rowA][colB];
        int[][] matrixFinal = new int[rowA][colC];
        int startLine = 0;
        int endLine = 0;
        int startColumn = 0;
        int stopColumn = 0;
        double startTime = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(nrThreads);
        List<Future<int[][]>> list = new ArrayList<>();
        ExecutorService pool2 = Executors.newFixedThreadPool(nrThreads);
        List<Future<int[][]>> list2 = new ArrayList<>();

        for (int i = 0; i < nrThreads; i++) {
            if (remained > 0) {
                nrCellsThread = copyNrLinesThread + 1;
                remained--;
            } else {
                nrCellsThread = copyNrLinesThread;
            }

            //works, don't change this
            if(nrCellsThread == rowA){
                stopColumn = colB;
                endLine +=1;
                startColumn = 0;
            }

            if(nrCellsThread > rowA){
                if(nrCellsThread >= copyNrLinesThread) {
                    endLine += nrCellsThread / colB + 1;
                } else {
                    endLine += nrCellsThread/colB;
                }
                stopColumn = colB;
            }

            if(nrCellsThread < rowA){
                stopColumn = colB;
                if(endLine < rowA){
                    endLine+=1;
                    startColumn = 0;
                }
            }

            Callable<int[][]> worker = new ConcurrentMatrixMultiplying(matrixA, matrixB, matrixProduct, startLine, endLine, startColumn, stopColumn);
            Future<int[][]> submit = pool.submit(worker);
            list.add(submit);
            index++;
//            add start row and start column
            if(nrCellsThread < rowA){
                if(stopColumn+nrCellsThread >= colB){
                    startLine = endLine;
                }
            }
            if(nrCellsThread == rowA){
                startLine = endLine;
            }


            startLine = endLine;
            if(nrCellsThread < rowA){
                startColumn = stopColumn;
            }
        }

        endLine = rowA;
        startLine = 0;
        startColumn = 0;
        stopColumn = colC;

        int CF[][];
        for (Future<int[][]> future : list) {
            try {
                CF = future.get();
                for (int i=startLine; i < endLine; i++) {
                    for(int j=startColumn; j<stopColumn; j++){
                        Callable<int[][]> worker = new ConcurrentMatrixMultiplying(matrixProduct, matrixC, matrixFinal, startLine, endLine, startColumn, stopColumn);
                        Future<int[][]> submit2 = pool2.submit(worker);
                        list2.add(submit2);
//                    matrixProduct[i][j] = CF[i][j];
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        endLine = rowA;
        startLine = 0;
        startColumn = 0;
        stopColumn = colC;

        for (Future<int[][]> future : list2){
            try {
                CF = future.get();
                for (int i=startLine; i < endLine; i++) {
                    System.arraycopy(CF[i], startColumn, matrixFinal[i], startColumn, stopColumn - startColumn);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.HOURS);
        pool2.shutdown();
        pool2.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(System.currentTimeMillis() - startTime);
        return  matrixFinal;

    }

    private static void fillMatrix(int[][] matrix) {
        Random rand = new Random();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = rand.nextInt(10);
            }
        }
    }

    private static void printBackMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void getDimensions() {
        try (Scanner reader = new Scanner(new FileReader(new File("/Users/ecaterina/Dropbox/PPD/src/main/resources/matrix.txt")))) {
            if(reader.hasNextInt()){
                colA = reader.nextInt();
            }
            rowB = colA;
            if(reader.hasNextInt()){
                rowA = reader.nextInt();
            }
            matrixA = new int[rowA][colA];
            if(reader.hasNextInt()){
                colB = reader.nextInt();
            }
            matrixB = new int[rowB][colB];
            if(reader.hasNextInt()){
                colC = reader.nextInt();
            }
            matrixC = new int[rowA][colC];

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printMatrixToFile(int[][] matrix) throws IOException {
        PrintWriter userOutput = new PrintWriter(new FileWriter("/Users/ecaterina/Dropbox/PPD/src/main/resources/result.txt"));
        for (int[] aMatrix : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                userOutput.print(aMatrix[j] + " ");
            }
            userOutput.println();
        }
        userOutput.close();

    }

}
