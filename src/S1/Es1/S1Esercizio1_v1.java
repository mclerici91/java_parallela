package S1.Es1;

import java.util.ArrayList;
import java.util.Collection;

class MyThread extends Thread {
    @Override
    public void run() {
        long fibo1 = 1, fibo2 = 1, fibonacci = 1;
        for (int i = 3; i <= 700; i++) {
            fibonacci = fibo1 + fibo2;
            fibo1 = fibo2;
            fibo2 = fibonacci;
        }
        /* Stampa risultato */
        System.out.println(this + ": " + fibonacci);
    }
}

public class S1Esercizio1_v1 {
    public static void main(final String[] args) {
        final Collection<Thread> allThreads = new ArrayList<Thread>();

        /* Creazione dei threads */
        for (int i = 1; i <= 5; i++) {
            System.out.println("Main: creo thread " + i);
            MyThread t = new MyThread();
            allThreads.add(t);
        }

        /* Avvio dei threads */
        for (final Thread t : allThreads)
            t.start();

        /* Attendo terminazione dei threads */
        for (final Thread t : allThreads) {
            try {
                System.out.println("Attendo la terminazione di " + t);
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}