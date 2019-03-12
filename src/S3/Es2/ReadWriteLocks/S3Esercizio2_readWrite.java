package S3.Es2.ReadWriteLocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Contatore {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    private int valore = 0;

    public int getValore() {
        readLock.lock();
        try {
            return valore;
        } finally {
            readLock.unlock();
        }
    }

    public void setValore(int valore) {
        writeLock.lock();
        try {
            this.valore = valore;
        } finally {
            writeLock.unlock();
        }
    }

    public void resetValore() {
        writeLock.lock();
        try {
            this.valore = 0;
        } finally {
            writeLock.unlock();
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

public class S3Esercizio2_readWrite {


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