package S3.Es2.ExplicitLocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Contatore {
    private final Lock lock = new ReentrantLock();
    private int valore = 0;

    public int getValore() {
        lock.lock();
        try {
            return valore;
        } finally {
            lock.unlock();
        }
    }

    public void setValore(int valore) {
        lock.lock();
        try {
            this.valore = valore;
        } finally {
            lock.unlock();
        }
    }

    public void resetValore() {
        lock.lock();
        try {
            this.valore = 0;
        } finally {
            lock.unlock();
        }
    }
}

class Sensore implements Runnable {
    private int id;
    private int soglia;
    private boolean isSuperato = false;
    private Contatore contatore;

    public Sensore(int id, int soglia, Contatore contatore) {
        this.id = id;
        this.soglia = soglia;
        this.contatore = contatore;
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
            if (soglia < contatore.getValore()) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("Sensore" + id + " - Soglia superata.");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                contatore.resetValore();
                isSuperato = true;
            }
        }
    }
}

public class S3Esercizio2_explicit {


    public static void main(String[] args) {

        Contatore contatore = new Contatore();

        List<Thread> allThreads = new ArrayList<>();
        List<Sensore> allSensors = new ArrayList<>();

        // Creazione dei sensori
        for (int i = 1; i <= 10; i++) {
            final Sensore sensor = new Sensore(i, i * 10, contatore);
            allSensors.add(sensor);
            allThreads.add(new Thread(sensor));
            System.out.println("Creato sensore: Sensore" + sensor.getId() + " con soglia " + sensor.getSoglia());
        }

        // Partenza dei sensori
        allThreads.forEach(Thread::start);

        // Incremento contatore
        while (contatore.getValore() <= 120) {

            final int n = ThreadLocalRandom.current().nextInt(1, 9);
            contatore.setValore(contatore.getValore() + n);
            System.out.println("VALORE CONTATORE: " + contatore.getValore());

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