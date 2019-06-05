package S8.Es4.Conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Fantino implements Runnable {
    private int id;
    private long comeTime = 0; // tempo di arrivo alla linea di partenza
    private long waitTime = 0; // tempo di attesa
    static final Lock lock = new ReentrantLock();
    static final Condition ready = lock.newCondition();

    public Fantino(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        // Arrivo alla linea di partenza
        this.comeTime = ThreadLocalRandom.current().nextInt(1000, 1050);
        try {
            Thread.sleep(this.comeTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Inizio dell'attesa
        lock.lock();
        try {
            System.out.println("Fantino" + this.id + ": arrivo alla linea di partenza");
            this.waitTime = System.currentTimeMillis();
            S8Esercizio4.arrivati.getAndIncrement();
            ready.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        // Fine dell'attesa e report
        System.out.println("Fantino" + this.id + ": ha atteso " + (S8Esercizio4.readyTime - this.waitTime) + "ms");
    }
}

public class S8Esercizio4 {
    static AtomicInteger arrivati = new AtomicInteger(0);
    static long readyTime = 0; // Tempo in cui tutti saranno arrivati

    public static void main(String[] args) {

        final List<Fantino> allFantini = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Fantino fantino = new Fantino(i);
            allFantini.add(fantino);
            allThreads.add(new Thread(fantino));
        }

        // Partenza dei threads
        System.out.println("Simulation started");
        System.out.println("--------------------------------------------");
        for (final Thread t : allThreads)
            t.start();

        while (arrivati.get() < 10) {
            // Attende che tutti arrivino
        }

        // Partenza
        Fantino.lock.lock();
        try {
            // Aggiornamento tempo di partenza
            readyTime = System.currentTimeMillis();
            Fantino.ready.signalAll();
        } finally {
            Fantino.lock.unlock();
        }

        for (final Thread t : allThreads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("--------------------------------------------");
        System.out.println("Simulation finished");
    }
}
