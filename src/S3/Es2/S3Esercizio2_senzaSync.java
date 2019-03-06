package S3.Es2;

import java.util.ArrayList;
import java.util.List;

class Contatore {
    private int valore;

    public Contatore(int valore) {
        this.valore = valore;
    }

    public int getValore() {
        return valore;
    }

    public void incrementaValore(int n) {
        this.valore += n;
    }

    public void azzeraContatore() {
        this.valore = 0;
    }
}

class Sensore implements Runnable {
    private int id;
    private int soglia;

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
        Contatore contatore = new Contatore(0);

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
