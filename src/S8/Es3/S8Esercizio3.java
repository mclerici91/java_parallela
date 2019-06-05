package S8.Es3;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
    private int totalRunTime;
    private List<Corridore> corridori = new ArrayList<>();

    public Squadra(int id) {
        this.id = id;
        this.totalRunTime = 0;
    }

    public int getId() {
        return id;
    }

    public int getTotalRunTime() {
        return totalRunTime;
    }

    public void addCorridore(Corridore corridore) {
        this.corridori.add(corridore);
    }
}

class Corridore implements Runnable {
    private int id;
    private int runTime;
    Squadra squadra;
    Testimone testimone;
    CountDownLatch countdown = new CountDownLatch(41);

    public Corridore(int id, Squadra squadra) {
        this.id = id;
        this.squadra = squadra;
        this.runTime = 0;
    }

    public int getId() {
        return id;
    }

    public int getRunTime() {
        return runTime;
    }

    @Override
    public void run() {

        //Se ho il testimone mi preparo a partire
        //Attendo
        //Parto
        //Corro tot tempo
        // Assegno il testimone al prossimo
        //Se sono l'ultimo provo a segnare il traguardo, se non è già stato segnato

    }
}

public class S8Esercizio3 {

    public static void main(String[] args) {
        Squadra squadra1 = new Squadra(1);
        Squadra squadra2 = new Squadra(2);
        Squadra squadra3 = new Squadra(3);
        Squadra squadra4 = new Squadra(4);

        final List<Corridore> allCorridore = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();

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

    }
}
