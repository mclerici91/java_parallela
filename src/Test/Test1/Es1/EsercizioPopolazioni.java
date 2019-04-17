package Test.Test1.Es1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe che simula periodi di carestia per i paesi
 */
class Carestia implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();
        // ad ogni giro introduce carestia per un determinato paese scelto a caso
        for (int i = 0; i < 50; i++) {
            final int popoloScelto = random.nextInt(EsercizioPopolazioni.popolazione.length());

            // 0.1 .. 0.7
            final double fattoreDecimazione = random.nextDouble() * 0.6 + 0.1;
            final long popolazioneAggiornata;

            // decima la popolazione
            //EsercizioPopolazioni.lock.lock()
            long oldValue;
            long newValue;
            do {
                oldValue = EsercizioPopolazioni.popolazione.get(popoloScelto);
                newValue = (long) (oldValue*fattoreDecimazione);
                //EsercizioPopolazioni.popolazione[popoloScelto] *= fattoreDecimazione;
                //popolazioneAggiornata = newValue;
            } while (!EsercizioPopolazioni.popolazione.compareAndSet(popoloScelto, oldValue, newValue));
            //EsercizioPopolazioni.lock.unlock();

            popolazioneAggiornata = EsercizioPopolazioni.popolazione.get(popoloScelto);

            System.out.println("Carestia: Popolazione " + popoloScelto + " diminuita a " + popolazioneAggiornata
                    + " del fattore " + fattoreDecimazione);

            try {
                // pausa fra un periodo di carestia e l'altro
                Thread.sleep(100);
            } catch (final InterruptedException e) {
            }
        }
    }
}

/**
 * Classe che simula periodi di prosperita per i paesi
 */
class Prosperita implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();
        // ad ogni giro introduce prosperità per un determinato paese scelto a caso
        for (int i = 0; i < 100; i++) {
            final int popoloScelto = random.nextInt(EsercizioPopolazioni.popolazione.length());

            final double fattoreCrescita = random.nextDouble() * 0.55 + 1.05;
            final long popolazioneAggiornata;

            // incrementa la popolazione
            //EsercizioPopolazioni.lock.lock();
            long oldValue;
            long newValue;
            do {
                oldValue = EsercizioPopolazioni.popolazione.get(popoloScelto);
                newValue = (long) (oldValue*fattoreCrescita);
            } while (!EsercizioPopolazioni.popolazione.compareAndSet(popoloScelto, oldValue, newValue));

            popolazioneAggiornata = EsercizioPopolazioni.popolazione.get(popoloScelto);

            System.out.println("Prosperita: Popolazione " + popoloScelto + " cresciuta a " + popolazioneAggiornata
                    + " del fattore " + fattoreCrescita);

            try {
                // pausa fra un periodo di prosperità e l'altro
                Thread.sleep(50);
            } catch (final InterruptedException e) {
            }
        }
    }
}

/**
 * Programma che simula la variazione demografica di 5 paesi
 */
public class EsercizioPopolazioni {
    static AtomicLongArray popolazione = new AtomicLongArray(5);
    //static volatile long popolazione[] = new long[5];
    static ReentrantLock lock = new ReentrantLock();

    public static void main(final String[] args) {
        // la popolazione iniziale è di 1000 abitanti per ogni paese
        for (int i = 0; i < 5; i++)
            popolazione.set(i, 1000);

        final List<Thread> allThreads = new ArrayList<>();
        allThreads.add(new Thread(new Prosperita()));
        allThreads.add(new Thread(new Prosperita()));
        allThreads.add(new Thread(new Carestia()));

        System.out.println("Simulation started");
        for (final Thread t : allThreads)
            t.start();

        for (final Thread t : allThreads)
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("Simulation finished");
    }
}
