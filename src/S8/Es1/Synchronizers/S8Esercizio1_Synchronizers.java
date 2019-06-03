package S8.Es1.Synchronizers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Worker implements Runnable {
    private int id;
    private int lineSum = 0;
    private int columnSum = 0;
    static final Object workLock = new Object();

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
    private synchronized void sumLine(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            this.lineSum += matrix[this.id][i];
        }
        S8Esercizio1_Synchronizers.lineSumCompleted++;
    }

    // Calcola somma della colonna
    private synchronized void sumColumn(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            this.columnSum += matrix[i][this.id];
        }
        S8Esercizio1_Synchronizers.columnSumCompleted++;
    }


    @Override
    public void run() {
        synchronized (workLock) {
            while (S8Esercizio1_Synchronizers.lineSumCompleted < 10) {
                sumLine(S8Esercizio1_Synchronizers.matrix);
                try {
                    workLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        synchronized (workLock) {
            while (S8Esercizio1_Synchronizers.columnSumCompleted < 10) {
                sumColumn(S8Esercizio1_Synchronizers.matrix);
                try {
                    workLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

public class S8Esercizio1_Synchronizers {
    final static int[][] matrix = new int[10][10];
    static volatile int lineSumCompleted = 0;
    static volatile int columnSumCompleted = 0;

    public static void main(String[] args) {
        int totalRows = 0;
        int totalColumns = 0;

        final List<Worker> allWorker = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Worker worker = new Worker(i);
            allWorker.add(worker);
            allThreads.add(new Thread(worker));
        }

        // Inizializza matrice con valori random
        initMatrix();

        // Stampa matrice
        System.out.println("Matrice:");
        printMatrix();

        // Partenza dei threads
        System.out.println("Simulation started");
        System.out.println("--------------------------------------------");
        for (final Thread t : allThreads)
            t.start();

        // Calcola somma delle righe
        while (lineSumCompleted < 10) {
            //
        }
        synchronized (Worker.workLock) {
            Worker.workLock.notifyAll();
        }
        for (int i = 0; i < 10; i++) {
            totalRows = totalRows + allWorker.get(i).getLineSum();
        }
        // Stampa somma delle righe
        System.out.println("Somma delle righe: " + totalRows);


        // Calcola somma delle colonne
        while (columnSumCompleted < 10) {
            //
        }
        synchronized (Worker.workLock) {
            Worker.workLock.notifyAll();
        }
        for (int i = 0; i < 10; i++) {
            totalColumns = totalColumns + allWorker.get(i).getColumnSum();
        }

        // Stampa somma delle colonne
        System.out.println("Somma delle colonne: " + totalColumns);
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