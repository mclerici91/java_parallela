package S3;

import java.util.ArrayList;
import java.util.List;

class Conto {
    private int saldo;

    public void incrementaSaldo(int valore) {

    }

    public void decrementaSaldo(int valore) {

    }

    public boolean empty() {
        if (saldo > 0) {
            return false;
        } else {
            return true;
        }
    }
}

class Utente implements Runnable{
    private int id;
    private final Conto contoComune;

    public Utente(int id, Conto contoComune) {
        this.id = id;
        this.contoComune = contoComune;
    }

    public void prelievo() {

    }

    @Override
    public void run() {

    }
}

public class S2Esercizio3 {
    public static void main(String[] args) {
        Conto conto = new Conto();

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
    }
}
