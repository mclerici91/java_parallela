package S8.Es4.Synchronizers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

class Fantino implements Runnable {
    private int id;
    private long comeTime = 0; // tempo di arrivo alla linea di partenza
    private long waitTime = 0; // tempo di attesa
    static CountDownLatch countdown = new CountDownLatch(10);
    static CountDownLatch countTime = new CountDownLatch(1);

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
        System.out.println("Fantino" + this.id + ": arrivo alla linea di partenza");
        this.waitTime = System.currentTimeMillis();
        countdown.countDown();

        // Fine dell'attesa e report
        try {
            countdown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            countTime.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Fantino" + this.id + ": ha atteso " + (S8Esercizio4.readyTime - this.waitTime) + "ms");
    }
}

public class S8Esercizio4 {
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

        // Partenza
        try {
            Fantino.countdown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        readyTime = System.currentTimeMillis();

        Fantino.countTime.countDown();

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
