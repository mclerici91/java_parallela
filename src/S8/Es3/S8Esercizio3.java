package S8.Es3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class Testimone {
    private int id;

    public Testimone(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

class Squadra {
    private int id;
    private AtomicInteger totalRunTime = new AtomicInteger(0);
    private int completedRun = 0;
    private boolean vincitore = false;
    private List<Corridore> corridori = new ArrayList<>();

    public Squadra(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getTotalRunTime() {
        return totalRunTime.get();
    }

    public int getCompletedRun() {
        return completedRun;
    }

    public void incrementCompletedRun() {
        this.completedRun++;
    }

    public void addTotalRunTime(int time) {
        this.totalRunTime.getAndAdd(time);
    }

    public void addCorridore(Corridore corridore) {
        this.corridori.add(corridore);
    }

    public Corridore getCorridore(int id) {
        return this.corridori.get(id);
    }

}

class Corridore implements Runnable {
    private int id;
    private int runTime;
    Squadra squadra;
    volatile Testimone testimone = null;
    static CountDownLatch countdown = new CountDownLatch(41);
    ReentrantLock passaggio = new ReentrantLock();

    public Corridore(int id, Squadra squadra) {
        this.id = id;
        this.squadra = squadra;
        this.runTime = 0;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        countdown.countDown();
        System.out.println("SQUADRA" + this.squadra.getId() + " - Corridore" + this.getId() + ": Attendo il countdown..." + countdown.getCount());
        try {
            countdown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (this.testimone == null) {

        }

        // Corsa
        this.runTime = (ThreadLocalRandom.current().nextInt(100, 150));
        this.squadra.addTotalRunTime(this.runTime);
        try {
            Thread.sleep(this.runTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("SQUADRA" + this.squadra.getId() + " - Corridore" + this.getId() + ": Corso per " + this.runTime + "ms");
        this.squadra.incrementCompletedRun();

        if (this.squadra.getCompletedRun() < 10) {
            passaggio.lock();
            try {
                this.squadra.getCorridore(((this.id + 1) - ((this.squadra.getId() - 1) * 10)) - 1).testimone = this.testimone;
                System.out.println("SQUADRA" + this.squadra.getId() + " - Corridore" + this.getId() + ": Assegnamento del testimone nr. " + this.squadra.getCorridore(((this.id + 1) - ((this.squadra.getId() - 1) * 10)) - 1).testimone.getId() + " al Corridore " + this.squadra.getCorridore(((this.id + 1) - ((this.squadra.getId() - 1) * 10)) - 1).getId());
                this.testimone = null;
            } finally {
                passaggio.unlock();
            }
        }
    }
}

public class S8Esercizio3 {

    static void getWinner(Squadra s1, Squadra s2, Squadra s3, Squadra s4) {
        if ((s1.getTotalRunTime() < s2.getTotalRunTime()) && (s1.getTotalRunTime() < s3.getTotalRunTime()) && (s1.getTotalRunTime() < s4.getTotalRunTime())) {
            System.out.println("SQUADRA" + s1.getId() + " - Corridore" + s1.getCorridore(9).getId() + ": HO VINTO!");
            System.out.println("SQUADRA" + s2.getId() + " - Corridore" + s2.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s3.getId() + " - Corridore" + s3.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s4.getId() + " - Corridore" + s4.getCorridore(9).getId() + ": HO PERSO.");
        } else if ((s2.getTotalRunTime() < s1.getTotalRunTime()) && (s2.getTotalRunTime() < s3.getTotalRunTime()) && (s2.getTotalRunTime() < s4.getTotalRunTime())) {
            System.out.println("SQUADRA" + s1.getId() + " - Corridore" + s1.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s2.getId() + " - Corridore" + s2.getCorridore(9).getId() + ": HO VINTO!");
            System.out.println("SQUADRA" + s3.getId() + " - Corridore" + s3.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s4.getId() + " - Corridore" + s4.getCorridore(9).getId() + ": HO PERSO.");
        } else if ((s3.getTotalRunTime() < s1.getTotalRunTime()) && (s3.getTotalRunTime() < s2.getTotalRunTime()) && (s3.getTotalRunTime() < s4.getTotalRunTime())) {
            System.out.println("SQUADRA" + s1.getId() + " - Corridore" + s1.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s2.getId() + " - Corridore" + s2.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s3.getId() + " - Corridore" + s3.getCorridore(9).getId() + ": HO VINTO!");
            System.out.println("SQUADRA" + s4.getId() + " - Corridore" + s4.getCorridore(9).getId() + ": HO PERSO.");
        } else {
            System.out.println("SQUADRA" + s1.getId() + " - Corridore" + s1.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s2.getId() + " - Corridore" + s2.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s3.getId() + " - Corridore" + s3.getCorridore(9).getId() + ": HO PERSO.");
            System.out.println("SQUADRA" + s4.getId() + " - Corridore" + s4.getCorridore(9).getId() + ": HO VINTO!");
        }
    }

    public static void main(String[] args) {
        Squadra squadra1 = new Squadra(1);
        Squadra squadra2 = new Squadra(2);
        Squadra squadra3 = new Squadra(3);
        Squadra squadra4 = new Squadra(4);

        final List<Corridore> allCorridore = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();

        // Creazione dei corridori e assegnamento alle squadre
        for (int i = 1; i <= 10; i++) {
            final Corridore corridore = new Corridore(i, squadra1);
            squadra1.addCorridore(corridore);
            allCorridore.add(corridore);
            allThreads.add(new Thread(corridore));
        }
        for (int i = 11; i <= 20; i++) {
            final Corridore corridore = new Corridore(i, squadra2);
            squadra2.addCorridore(corridore);
            allCorridore.add(corridore);
            allThreads.add(new Thread(corridore));
        }
        for (int i = 21; i <= 30; i++) {
            final Corridore corridore = new Corridore(i, squadra3);
            squadra3.addCorridore(corridore);
            allCorridore.add(corridore);
            allThreads.add(new Thread(corridore));
        }
        for (int i = 31; i <= 40; i++) {
            final Corridore corridore = new Corridore(i, squadra4);
            squadra4.addCorridore(corridore);
            allCorridore.add(corridore);
            allThreads.add(new Thread(corridore));
        }

        // Assegnamento dei testimoni
        squadra1.getCorridore(0).testimone = new Testimone(1);
        squadra2.getCorridore(0).testimone = new Testimone(2);
        squadra3.getCorridore(0).testimone = new Testimone(3);
        squadra4.getCorridore(0).testimone = new Testimone(4);


        // Partenza dei threads
        System.out.println("Simulation started");
        System.out.println("--------------------------------------------");
        for (final Thread t : allThreads)
            t.start();

        Corridore.countdown.countDown();
        try {
            Corridore.countdown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        System.out.println("SQUADRA1 - Tempo totale: " + squadra1.getTotalRunTime() + "ms");
        System.out.println("SQUADRA2 - Tempo totale: " + squadra2.getTotalRunTime() + "ms");
        System.out.println("SQUADRA3 - Tempo totale: " + squadra3.getTotalRunTime() + "ms");
        System.out.println("SQUADRA4 - Tempo totale: " + squadra4.getTotalRunTime() + "ms");

        // Verifica vincitore
        getWinner(squadra1, squadra2, squadra3, squadra4);
    }
}
