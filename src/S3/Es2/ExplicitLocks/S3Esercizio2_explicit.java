package S3.Es2.ExplicitLocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Sensore implements Runnable {
    private int id;
    private int soglia;
    private boolean isSuperato = false;

    public Sensore(int id, int soglia) {
        this.id = id;
        this.soglia = soglia;
    }

    public int getId() {
        return id;
    }

    public int getSoglia() {
        return soglia;
    }

    @Override
    public void run() {
        while (!isSuperato) {
            if (soglia < S3Esercizio2_explicit.contatore) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("Sensore" + id + " - Soglia superata.");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                S3Esercizio2_explicit.contatore = 0;
                isSuperato = true;
            }
        }
    }
}

public class S3Esercizio2_explicit {

    public static int contatore;
    public static final Lock lock = new ReentrantLock();


    public static void main(String[] args) {

        List<Thread> allThreads = new ArrayList<>();
        List<Sensore> allSensors = new ArrayList<>();

        // Creazione dei sensori
        for (int i = 1; i <= 10; i++) {
            final Sensore sensor = new Sensore(i, i * 10);
            allSensors.add(sensor);
            allThreads.add(new Thread(sensor));
            System.out.println("Creato sensore: Sensore" + sensor.getId() + " con soglia " + sensor.getSoglia());
        }

        // Partenza dei sensori
        allThreads.forEach(Thread::start);

        // Incremento contatore
        while (S3Esercizio2_explicit.contatore <= 120) {

            final int n = ThreadLocalRandom.current().nextInt(1, 9);
            S3Esercizio2_explicit.contatore += n;
            System.out.println("VALORE CONTATORE: " + S3Esercizio2_explicit.contatore);

            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(5, 11));
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("CONTATORE - VALORE MASSIMO RAGGIUNTO");
    }
}