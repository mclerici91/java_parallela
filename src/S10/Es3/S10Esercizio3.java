package S10.Es3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

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

        // Somma dei risultati
        int result = 0;
        for (int i = 0; i < m2[0].length; i++)
            for (int j = 0; j < m2.length; j++)
                result = result + m2[i][j];

        return result;
    }
}

public class S10Esercizio3 {
    public static final int NUM_OPERATIONS = 100_000;
    public static final int MATRIX_SIZE = 64;
    static final ExecutorService mainExecutor = Executors.newFixedThreadPool(100);
    static CompletionService<Integer> completionService = new ExecutorCompletionService<>(mainExecutor);

    public static void main(final String[] args) {

        System.out.println("Simulazione iniziata");
        for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
            completionService.submit(new Worker());
        }

        mainExecutor.shutdown();

        int maxValue = 0;

        for (int i = 0; i < NUM_OPERATIONS; i++) {
            try {
                final Future<Integer> future = completionService.take();
                final int bigger = future.get();
                if (maxValue < bigger)
                    maxValue = bigger;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("MaxValue: " + maxValue);

        System.out.println("Simulazione terminata");

    }
}