package S8.Es1;

import java.util.Random;

class Worker implements Runnable {
    int id;
    int lineSum = 0;
    int columnSum = 0;

    public Worker(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getLineSum() {
        return lineSum;
    }

    public int getColumnSum() {
        return columnSum;
    }

    // Calcola somma della riga
    public synchronized void sumLine(int[][] matrix) throws InterruptedException {
        for (int i = 0; i < matrix.length; i++) {
            this.lineSum += matrix[this.id][i];
        }
        while (S8Esercizio1_WaitNotify.lineSumCompleted != 10) {
            wait();
        }
    }

    // Calcola somma della colonna
    public synchronized void sumColumn(int[][] matrix) throws InterruptedException {
        for (int i = 0; i < matrix.length; i++) {
            this.columnSum += matrix[i][this.id];
        }
        while (S8Esercizio1_WaitNotify.columnSumCompleted != 10) {
            wait();
        }
    }


    @Override
    public void run() {

    }
}

public class S8Esercizio1_WaitNotify {
    final static int[][] matrix = new int[10][10];
    final static int[] rowSum = new int[matrix.length];
    final static int[] colSum = new int[matrix[0].length];
    static volatile int lineSumCompleted = 0;
    static volatile int columnSumCompleted = 0;

    public static void main(String[] args) {
        // Inizializza matrice con valori random
        initMatrix();

        // Stampa matrice
        System.out.println("Matrice:");
        printMatrix();

        // Calcola somma delle righe
        //for (int row = 0; row < matrix.length; row++)
        //    rowSum[row] = sumRow(row);

        // Stampa somma delle righe
        System.out.println("Somme delle righe:");
        printArray(rowSum);

        // Calcola somma delle colonne
        for (int col = 0; col < matrix[0].length; col++)
            colSum[col] = sumColumn(col);

        // Stampa somma delle colonne
        System.out.println("Somme delle colonne:");
        printArray(colSum);
    }

    public static int sumRow(final int row) {
        int result = 0;
        for (int col = 0; col < matrix[row].length; col++)
            result += matrix[row][col];
        return result;
    }

    public static int sumColumn(final int row) {
        int temp = 0;
        for (int col = 0; col < matrix.length; col++)
            temp += matrix[col][row];
        return temp;
    }

    private static void initMatrix() {
        Random r = new Random();
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                matrix[row][col] = 1 + r.nextInt(100);
            }
        }
    }

    private static void printMatrix() {
        for (int i = 0; i < matrix.length; i++)
            printArray(matrix[i]);
    }

    private static void printArray(final int[] array) {
        for (int i = 0; i < array.length; i++)
            System.out.print(array[i] + "\t");
        System.out.println();
    }
}