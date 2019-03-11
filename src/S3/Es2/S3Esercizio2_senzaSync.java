package S3.Es2;

import java.util.ArrayList;
import java.util.List;

class Sensore implements Runnable {
    private int id;
    private int soglia;
    public static int contatore;

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

    }
}

public class S3Esercizio2_senzaSync {
    public static void main(String[] args) {

        List<Thread> allThreads = new ArrayList<>();
        List<Sensore> allSensors = new ArrayList<>();

        // Creazione dei sensori
        for (int i = 1; i <= 10; i++) {
            final Sensore sensor = new Sensore(i, i*10);
            allSensors.add(sensor);
            allThreads.add(new Thread(sensor));
            System.out.println("Creato sensore: Sensore" + sensor.getId() + " con soglia " + sensor.getSoglia());
        }

        allThreads.forEach(Thread::start);

        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
