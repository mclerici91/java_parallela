package Test.Lepre;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class Commissario implements Runnable {
    private int id;

    public Commissario(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        EsercizioGara.countdown.getAndDecrement();

        while (EsercizioGara.garaAperta.get()) {
            while ((EsercizioGara.percorsoLepre.get() <= 10000) && (EsercizioGara.percorsoTartaruga.get() <= 10000)) {
                // Gara
            }

            EsercizioGara.garaAperta.set(false);

        }

    }
}

public class EsercizioGara {

    static AtomicBoolean garaAperta = new AtomicBoolean(false);
    static AtomicInteger countdown = new AtomicInteger(10);
    static AtomicInteger percorsoLepre = new AtomicInteger(0);
    static AtomicInteger percorsoTartaruga = new AtomicInteger(0);

    public static void main(String[] args) {

        Thread lepre = new Thread(new Runnable() {
            @Override
            public void run() {
                EsercizioGara.countdown.getAndDecrement();

                while(!garaAperta.get()) {

                }

                while (garaAperta.get()) {
                    for (int i = 0; i <= 10; i++) {
                        if (garaAperta.get()) {
                            percorsoLepre.getAndAdd(10);
                            System.out.println("Lepre - incremento, valore: " + percorsoLepre.get());
                        }
                    }

                    try {
                        // pausa
                        Thread.sleep(ThreadLocalRandom.current().nextInt(250, 301));
                    } catch (final InterruptedException e) {
                    }
                }
            }
        });

        Thread tartaruga = new Thread(new Runnable() {
            @Override
            public void run() {
                EsercizioGara.countdown.getAndDecrement();

                while (!garaAperta.get()) {

                }

                while(garaAperta.get()) {
                    percorsoTartaruga.getAndIncrement();
                    System.out.println("Tartaruga - incremento, valore: " + percorsoTartaruga.get());
                }
            }
        });

        final List<Thread> allThreads = new ArrayList<>();
        allThreads.add(lepre);
        allThreads.add(tartaruga);
        for (int i = 0; i < 10; i++) {
            allThreads.add(new Thread(new Commissario(i)));
        }

        System.out.println("Simulation started");
        for (final Thread t : allThreads)
            t.start();

        while (countdown.get() > 0) {

        }

        garaAperta.set(true);


        for (final Thread t : allThreads)
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("Simulation finished");


        if (percorsoLepre.get() > percorsoTartaruga.get()) {
            System.out.println("VINCITORE - LEPRE con " + percorsoLepre);
        } else if (percorsoLepre.get() < percorsoTartaruga.get()) {
            System.out.println("VINCITORE - TARTARUGA con " + percorsoTartaruga);
        } else {
            System.out.println("PAREGGIO");
        }

    }
}
