package S9.Es4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

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

    }
}

public class S9Esercizio4 {

    public static BlockingQueue<Cliente> sala = new LinkedBlockingQueue();

    public static void main(String[] args) {

        final List<Cliente> allClienti = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Cliente cliente = new Cliente(i+1);
            allClienti.add(cliente);
            allThreads.add(new Thread(cliente));
        }

        Thread barbiere = new Thread(new Runnable() {
            public void run() {

                // Taglio capelli
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Controllo sala d'attesa
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Cliente c = sala.poll();
            }
        });
        barbiere.start();
    }
}
