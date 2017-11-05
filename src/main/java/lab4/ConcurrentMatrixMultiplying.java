package lab4;

import java.util.concurrent.Callable;

public class ConcurrentMatrixMultiplying implements Callable {
    private int[][] matrixA;
    private int[][] matrixB;
    private int[][] matrixProduct;
    private int rowStart;
    private int rowStop;
    private int colStart;
    private int colStop;

    public ConcurrentMatrixMultiplying(int[][] A, int[][] B, int[][] C, int rowStart, int rowStop, int colStart, int colStop) {
        this.matrixA = A;
        this.matrixB = B;
        this.matrixProduct = C;
        this.rowStart = rowStart;
        this.rowStop = rowStop;
        this.colStart = colStart;
        this.colStop = colStop;
    }

    @Override
    public Object call() throws Exception {
        for (int line = rowStart; line < rowStop; line++) {
            for (int col = colStart; col < matrixB.length; col++) {
                for (int k = 0; k < matrixB[0].length; k++) {
                    if(col > matrixProduct[0].length-1 || line > matrixProduct.length-1){
                        break;
                    }
                    if(col > colStop && line == rowStop - 1){
                        continue;
                    } else {
                        matrixProduct[line][col] += matrixA[line][k] * matrixB[k][col];
                    }
                }
            }
        }
        return matrixProduct;
    }
}