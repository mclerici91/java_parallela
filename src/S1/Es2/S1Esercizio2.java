package S1.Es2;

import java.util.Random;

class Runner implements Runnable {

    private int id;
    private int random;

    Runner(int id, int random) {
        this.id = id;
        this.random = random;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(random);
            System.out.println("Thread " + id + " risveglio dopo " + random + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class S1Esercizio2 {
    public static void main(final String[] args) throws InterruptedException {
        Random r = new Random();
        final Thread t0 = new Thread(new Runner(0, r.nextInt(2000-1500) + 1500));
        final Thread t1 = new Thread(new Runner(1, r.nextInt(2000-1500) + 1500));

        System.out.println("Partono tutti i threads.");
        //Threads start
        t0.start();
        t1.start();

        System.out.println("In attesa che i threads abbiano terminato.");
        t0.join();
        t1.join();

        System.out.println("Tutti i threads hanno terminato.");

    }
}
