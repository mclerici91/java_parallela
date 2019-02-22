package S1.Es3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class S1Esercizio3 {
    public static void main(String[] args) {
        Random r = new Random();
        int array[] = new int[10000];
        final Collection<Thread> allThreads = new ArrayList<Thread>();

        for (int i = 0; i < 10000; i++) {
            array[i] = r.nextInt((100 - 1) + 1) + 1;
        }

        /* Creazione dei threads */
        for (int i = 0; i < 10; i++) {
            final int m = i;
            final Thread t = new Thread() {

                @Override
                public void run() {
                    int somma = 0;
                    for (int j = (1000*m); j <= (1000*(m+1)-1); j++) {
                        somma = somma + array[j];
                    }
                    System.out.println("Somma degli elementi nell'intervallo [" + 1000*m + ";" + (1000*(m+1)-1) + "] = " + somma);
                }
            };
            allThreads.add(t);
        }

        /* Avvio dei threads */
        for (final Thread t : allThreads)
            t.start();

        /* Attendo terminazione dei threads */
        for (final Thread t : allThreads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
