package S3.Es3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Container {
    private int[] sharedContainer = new int[5];
    public final Lock lock = new ReentrantLock();

    public int getFromPosition(int i) {
        lock.lock();
        try {
            return sharedContainer[i];
        } finally {
            lock.unlock();
        }
    }

    public void addAtPosition(int i, int value) {
        lock.lock();
        try {
            sharedContainer[i] += value;
        } finally {
            lock.unlock();
        }
    }

    public void resetPosition(int i) {
        lock.lock();
        try {
            sharedContainer[i] = 0;
        } finally {
            lock.unlock();
        }
    }
}

class Worker implements Runnable {
    private int id;
    private Container sharedContainer;

    public Worker(int id, Container container) {
        this.id = id;
        this.sharedContainer = container;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        Random r = new Random();
        int index;
        int value;

        System.out.println("Worker" + id + ": avviato");

        for (int i = 1; i < 10000; i++) {
            // Scelta posizione array
            index = r.nextInt((4) + 1);

            // Somma di un valore random alla posizione scelta e eventuale reset
            value = r.nextInt((50 - 10) + 1) + 10;
            sharedContainer.addAtPosition(index, value);
            if (sharedContainer.getFromPosition(index) > 500) {
                sharedContainer.resetPosition(index);
                //System.out.println("Worker" + id + ": posizione " + index + " resettata");
            }
        }

        // Attesa per la prossima operazione
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(2, 5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Worker" + id + ": terminato");
    }
}

public class S3Esercizio3 {

    public static void main(String[] args) {

        Container array = new Container();

        List<Thread> allThreads = new ArrayList<>();
        List<Worker> allWorkers = new ArrayList<>();

        // Creazione dei workers
        for (int i = 1; i <= 10; i++) {
            final Worker worker = new Worker(i, array);
            allWorkers.add(worker);
            allThreads.add(new Thread(worker));
            System.out.println("Creato worker: Worker" + worker.getId());
        }

        // Inizializzazione array
        for (int i = 0; i < 5; i++) {
            array.resetPosition(i);
        }

        allThreads.forEach(Thread::start);

        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("PROGRAMMA TERMINATO");
    }
}
