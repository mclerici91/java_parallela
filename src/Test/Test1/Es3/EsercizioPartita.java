package Test.Test1.Es3;

import Test.Test1.Es1.EsercizioPopolazioni;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

class Bersaglio {
    private final int id;
    private boolean colpito = false;

    Bersaglio(int id) {
        this.id = id;
    }

    public boolean isColpito() {
        return colpito;
    }

    public void setColpito(boolean colpito) {
        this.colpito = colpito;
        System.out.println("Bersaglio" + this.id + ": colpito");
    }
}

class Giocatore implements Runnable {
    private final int id;
    private int punti = 0;
    private int colpiDisponibili = 20;
    Bersaglio bersaglioPuntato;

    Giocatore(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getPunti() {
        return punti;
    }

    public int getColpiDisponibili() {
        return colpiDisponibili;
    }

    public Bersaglio getBersaglioPuntato() {
        return bersaglioPuntato;
    }

    @Override
    public void run() {

        do {
            int lancio = ThreadLocalRandom.current().nextInt(20);

            bersaglioPuntato = EsercizioPartita.listaBersagli.get(ThreadLocalRandom.current().nextInt(10));

            if (lancio < 10 && !bersaglioPuntato.isColpito()) {
                bersaglioPuntato.setColpito(true);
                this.punti++;
            }

            this.colpiDisponibili--;
            EsercizioPartita.totaleColpi.getAndIncrement();

            try {
                // pausa tra un lancio e l'altro
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
            } catch (final InterruptedException e) {
            }
        }while (EsercizioPartita.isRunning);

    }
}

public class EsercizioPartita {
    static CopyOnWriteArrayList<Bersaglio> listaBersagli = new CopyOnWriteArrayList<>();
    static volatile boolean isRunning = true;
    static AtomicInteger totaleColpi = new AtomicInteger(0);

    public static void main(final String[] args) {
        final List<Giocatore> allGiocatori = new ArrayList<>();
        int colpiEsauriti = 0;
        int bersagliColpiti = 0;

        for (int i = 0; i < 10; i++) {
            allGiocatori.add(new Giocatore(i));
        }

        // la popolazione iniziale è di 1000 abitanti per ogni paese
        for (int i = 0; i < 10; i++)
            listaBersagli.add(new Bersaglio(i));

        final List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            allThreads.add(new Thread(allGiocatori.get(i)));

        System.out.println("Simulation started");
        for (final Thread t : allThreads)
            t.start();

        while (isRunning) {
            for (int i = 0; i < 10; i++) {
                if (allGiocatori.get(i).getColpiDisponibili() == 0) {
                    colpiEsauriti++;
                }
            }
            if (colpiEsauriti == 10) { System.out.println("FINE - TUTTI I COLPI ESAURITI"); }

            for (int i = 0; i < 10; i++) {
                if (listaBersagli.get(i).isColpito()) {
                    bersagliColpiti++;
                }
            }
            if (bersagliColpiti == 10) { System.out.println("FINE - TUTTI I BERSAGLI COLPITI"); }

            if ((colpiEsauriti == 10) || (bersagliColpiti == 10)) {
                isRunning = false;
            }

            for (int i = 0; i < 10; i++) {
                if (listaBersagli.get(i).isColpito()) {
                    isRunning = false;
                }
            }
            colpiEsauriti = 0;
            bersagliColpiti = 0;
        }

        for (final Thread t : allThreads)
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("Simulation finished");
        System.out.println("TOT COLPI " + totaleColpi.get());
    }
}
