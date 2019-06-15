package Esame.Es7;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

class Sciatore implements Runnable {

    static CountDownLatch countdown = new CountDownLatch(6);

    private int id;
    private int elapsedTime;

    public Sciatore(int id) {
        this.id = id;
        this.elapsedTime = 0;
    }

    public int getId() {
        return id;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public void run() {
        int tempTime;

        countdown.countDown();

        for (int i = 0; i < 6; i++) {

            // Percorre tappa
            tempTime = ThreadLocalRandom.current().nextInt(4, 8);
            try {
                Thread.sleep(tempTime);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            this.elapsedTime = this.elapsedTime + tempTime;

            SkiRace.tappe.get(i).addConcorrente(this);

            synchronized (SkiRace.tappe.get(i)) {
                if (SkiRace.tappe.get(i).completataDaTutti()) {
                    SkiRace.tappe.get(i).notify();
                }
            }
        }
    }
}

class Tappa {
    private int id;
    private CopyOnWriteArrayList<Sciatore> concorrenti = new CopyOnWriteArrayList<>();

    public Tappa(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addConcorrente(Sciatore sciatore) {
        this.concorrenti.add(sciatore);
    }

    public boolean completataDaTutti() {
        if (this.concorrenti.size() >= 5) {
            return true;
        } else {
            return false;
        }
    }

    public void printResults() {
        for (int i= 0; i < 5; i++) {
            System.out.println("Tappa numero " + this.getId() + ": posizione" + (i+1) + " - Sciatore" + (concorrenti.get(i).getId()+1));
        }
    }
}

public class SkiRace {

    static CopyOnWriteArrayList<Tappa> tappe = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {

        for (int i = 1; i <= 6; i++) {
            tappe.add(new Tappa(i));
        }

        final List<Sciatore> allSciatori = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Sciatore sciatore = new Sciatore(i);
            allSciatori.add(sciatore);
            allThreads.add(new Thread(sciatore));
        }

        Thread commissario = new Thread(new Runnable() {

            @Override
            public void run() {
                Sciatore.countdown.countDown();
                Sciatore vincitore = allSciatori.get(0);

                for (int i = 1; i <= 6; i++) {

                    synchronized (tappe.get(i-1)) {
                        while (!tappe.get(i-1).completataDaTutti()) {
                            try {
                                tappe.get(i-1).wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        System.out.println("Commissario: tappa numero " + i + " completata");
                        tappe.get(i-1).printResults();
                    }
                }

                for (int i = 1; i < 5; i++) {
                    if (allSciatori.get(i).getElapsedTime() < vincitore.getElapsedTime()) {
                        vincitore = allSciatori.get(i);
                    }
                }

                System.out.println("VINCITORE: Sciatore" + (vincitore.getId()+1));
                System.out.println("Risultati:");
                for (int i = 0; i < 5; i++) {
                    System.out.println("Sciatore" + (allSciatori.get(i).getId()+1) + ": " + allSciatori.get(i).getElapsedTime());
                }
            }
        });

        allThreads.add(commissario);

        // Partenza dei threads
        System.out.println("Simulation started");
        System.out.println("--------------------------------------------");
        for (final Thread t : allThreads)
            t.start();

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
