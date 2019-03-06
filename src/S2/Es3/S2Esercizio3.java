package S2.Es3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Conto {
    private int saldo;
    public final Lock lock = new ReentrantLock();

    // Incrementa il saldo sul conto
    public void incrementaSaldo(int valore) {
        saldo += valore;
    }

    // Decrementa il saldo sul conto
    public void decrementaSaldo(int valore) {
        lock.lock();
        try {
            saldo -= valore;
        } finally {
            lock.unlock();
        }
    }

    // Restituisce il saldo disponibile sul conto
    public int getSaldo() {
        lock.lock();
        try {
            return saldo;
        } finally {
            lock.unlock();
        }
    }

    // Verifica se il conto è vuoto
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

    // Operazione di prelievo
    public void prelievo(int valore) {
        saldo += valore;
        contoComune.decrementaSaldo(valore);
        System.out.println("Utente" + this.getId() + ": prelevo " + valore + "$ dal conto contenente " + (contoComune.getSaldo()+valore) + "$. Nuovo saldo " + contoComune.getSaldo() + "$");
    }

    public int getId() {
        return id;
    }

    // Restituisce il saldo dell'utente
    public int getSaldo() {
        return saldo;
    }

    @Override
    public void run() {
        Random r = new Random();
        int prelievo;
        int residuo;
        System.out.println("Utente" + this.getId() + " inizio operazione di prelevamento");

        while (!contoComune.empty()) {
            prelievo = r.nextInt(45) + 5;

            if (contoComune.getSaldo() >= prelievo) {
                prelievo(prelievo);
            } else {
                residuo = contoComune.getSaldo();
                saldo += residuo;
                contoComune.decrementaSaldo(residuo);
                System.out.println("Utente" + this.getId() + ": sono riuscito a prelevare solo " + residuo + "$ invece di " + prelievo + "$");
            }

            // Simula il tempo prima di prelevare nuovamente
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(5, 20));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Utente" + this.getId() + " termino operazione di prelievo");
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

        // Creazione degli utenti
        for (int i = 1; i <= 5; i++) {
            final Utente user = new Utente(i, conto);
            allUsers.add(user);
            allThreads.add(new Thread(user));
            System.out.println("Creato utente: Utente" + user.getId());
        }

        allThreads.forEach(Thread::start);

        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Reports
        for (Utente utente : allUsers) {
            System.out.println(
                    "Utente" + utente.getId() + ": saldo: " + utente.getSaldo());
            totalePrelevamenti += utente.getSaldo();
        }
        System.out.println("Totale saldo iniziale sul conto comune: " + saldoIniziale);
        System.out.println("Totale saldo sul conto comune dopo operazioni di prelievo: " + conto.getSaldo());
        System.out.println("Totale prelevamenti degli utenti: " + totalePrelevamenti);
    }
}