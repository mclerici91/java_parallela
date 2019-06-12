package S9.Es4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

class Cliente implements Runnable {
    private int id;

    public Cliente(int id) {
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

        // Arrivo del cliente
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(450, 700));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verifica se il barbiere dorme
        synchronized (S9Esercizio4.lock1) {
            while (S9Esercizio4.barbiereDormiente.get()) {
                S9Esercizio4.barbiereDormiente.set(false);
                System.out.println("Cliente" + this.id + ": Il barbiere dorme! Lo sveglio...");
                S9Esercizio4.lock1.notify();
            }
        }
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(80, 160));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Cliente" + this.id + ": Vado nella Sala d'Attesa");
                S9Esercizio4.salaAttesa.offer(this);


    }
}

public class S9Esercizio4 {

    public static BlockingQueue<Cliente> salaAttesa = new ArrayBlockingQueue<Cliente>(10);
    static AtomicBoolean barbiereDormiente = new AtomicBoolean(false);
    static Object lock1 = new Object();
    static volatile int clientiAccolti = 0;

    public static void main(String[] args) {

        final List<Cliente> allClienti = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();

        // Creazione clienti
        for (int i = 0; i < 10; i++) {
            final Cliente cliente = new Cliente(i+1);
            allClienti.add(cliente);
            allThreads.add(new Thread(cliente));
        }

        // Creazione barbiere
        Thread barbiere = new Thread(() -> {

            while (clientiAccolti < 10) {
                // Controllo sala d'attesa
                System.out.println("Barbiere: Verifico sala d'attesa");
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {
                    while (salaAttesa.isEmpty()) {
                        try {
                            System.out.println("Barbiere: Non ci sono clienti, vado a dormire...");
                            barbiereDormiente.set(true);
                            lock1.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("Barbiere: Taglio i capelli");
                salaAttesa.poll();
                // Taglio capelli
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clientiAccolti++;
            }
        });

        allThreads.add(barbiere);

        // Partenza dei threads
        System.out.println("Simulation started");
        for (final Thread t : allThreads) {
            t.start();
        }


        for (final Thread t : allThreads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Simulation finished");
    }
}
