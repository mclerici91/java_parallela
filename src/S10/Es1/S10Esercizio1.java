package S10.Es1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static S10.Es1.S10Esercizio1.MATRIX_SIZE;

class Worker implements Callable<Integer> {

    final Random rand = new Random();
    @Override
    public Integer call() throws Exception {
        // Crea matrici
        final int[][] m0 = new int[MATRIX_SIZE][MATRIX_SIZE];
        final int[][] m1 = new int[MATRIX_SIZE][MATRIX_SIZE];
        final int[][] m2 = new int[MATRIX_SIZE][MATRIX_SIZE];

        // Inizializza gli array con numeri random
        for (int i = 0; i < MATRIX_SIZE; i++)
            for (int j = 0; j < MATRIX_SIZE; j++) {
                m0[i][j] = rand.nextInt(10);
                m1[i][j] = rand.nextInt(10);
            }

        // Moltiplica matrici
        for (int i = 0; i < m0[0].length; i++)
            for (int j = 0; j < m1.length; j++)
                for (int k = 0; k < m0.length; k++)
                    m2[i][j] += m0[i][k] * m1[k][j];

        return m2[63][63];
    }
}

public class S10Esercizio1 {
    public static final int NUM_OPERATIONS = 100_000;
    public static final int MATRIX_SIZE = 64;
    static final ExecutorService mainExecutor = Executors.newFixedThreadPool(100);
    static List<Future<Integer>> futures = new ArrayList<>();

    public static void main(final String[] args) {

        System.out.println("Simulazione iniziata");
        for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
            futures.add(mainExecutor.submit(new Worker()));

        }

        mainExecutor.shutdown();

        while (!mainExecutor.isTerminated()) {
            // Wait
        }

        System.out.println("Simulazione terminata");

    }
}