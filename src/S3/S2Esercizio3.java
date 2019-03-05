package S3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Conto {
    private int saldo;
    public final Lock lock = new ReentrantLock();

    public void incrementaSaldo(int valore) {
        saldo += valore;
    }

    public void decrementaSaldo(int valore) {
        lock.lock();
        try {
            saldo -= valore;
        } finally {
            lock.unlock();
        }
    }

    public int getSaldo() {
        lock.lock();
        try {
            return saldo;
        } finally {
            lock.unlock();
        }
    }

    public boolean empty() {
        lock.lock();
        try {
            if (saldo > 0) {
                return false;
            } else {
                return true;
            }
        } finally {
            lock.unlock();
        }
    }
}

class Utente implements Runnable{
    private int id;
    private final Conto contoComune;
    private int saldo;

    public Utente(int id, Conto contoComune) {
        this.id = id;
        this.contoComune = contoComune;
        this.saldo = 0;
    }

    public void prelievo(int valore) {
        saldo += valore;
        contoComune.decrementaSaldo(valore);
    }

    public int getSaldo() {
        return saldo;
    }

    @Override
    public void run() {
        Random r = new Random();
        int prelievo;
        System.out.println(this + " inizio operazione di prelevamento");
        while (!contoComune.empty()) {
            prelievo = r.nextInt((50-5) + 5);
            if (contoComune.getSaldo() >= prelievo)
                prelievo(prelievo);

            // Simula il tempo prima di prelevare nuovamente
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(1, 5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(this + " termino operazione di prelievo");
    }
}

public class S2Esercizio3 {
    public static void main(String[] args) {
        Conto conto = new Conto();
        int saldoIniziale = 3000;
        int totalePrelevamenti = 0;
        conto.incrementaSaldo(saldoIniziale);

        List<Thread> allThreads = new ArrayList<>();
        List<Utente> allUsers = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            final Utente user = new Utente(i, conto);
            allUsers.add(user);
            allThreads.add(new Thread(user));
            System.out.println("Creato utente: " + user);
        }

        allThreads.forEach(Thread::start);

        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Utente utente : allUsers) {
            System.out.println(
                    utente + ": saldo: " + utente.getSaldo());
            totalePrelevamenti += utente.getSaldo();
        }
        System.out.println("Totale saldo iniziale sul conto comune: " + saldoIniziale);
        System.out.println("Totale saldo sul conto comune dopo operazioni di prelievo: " + conto.getSaldo());
        System.out.println("Totale prelevamenti degli utenti: " + totalePrelevamenti);
    }
}