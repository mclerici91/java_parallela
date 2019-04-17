/*Sviluppate un programma che simuli un grande magazzino con 10 clienti.
All'inizio, ogni cliente, rappresentato da un thread indipendente, avrà a
disposizione 20 soldi da spendere nel grande magazzino per acquistare 10 prodotti
(numerati da 0 a 9) disponibili a tutti i clienti. Durante l'inizializzazione,
per ogni prodotto dovrà venir fissato un quantitativo disponibile (generato casualmente
fra 1 e 10) e un prezzo (generato casualmente fra 1 e 5). L'acquisto di un prodotto dovrà
essere simulato, generando un numero casuale fra 0 e 9. Se il prodotto sarà disponibile,
il cliente lo acquisterà, spendendo i soldi a sua disposizione, altrimenti l'acquisto andrà a
vuoto. Fra un acquisto e l'altro, ogni cliente dovrà eseguire un pausa per un tempo causale fra
1 e 5 ms. Il grande magazzino chiuderà quando tutti i prodotti saranno stati venduti o quando tutti
i clienti avranno finito i soldi a disposizione. Un cliente che dopo 10 tentativi non dovesse riuscire
a fare nessun acquisto, sarà obbligato a smettere ed abbandonare il grande magazzino.

        Assicuratevi che tutti i clienti comincino ad acquistare i prodotti
        contemporaneamente, simulando l'apertura delle porte del grande magazzino.
        Fate in modo che il programma, al termine, stampi a schermo gli eventuali soldi
        rimasti ad ogni cliente o gli eventuali prodotti ancora disponibili nel grande magazzino.
        Assicuratevi che il programma sia thread-safe e che esegua con il massimo di concorrenzialità possibile.*//*


package Test.Test1.EsTest3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

class Prodotto {
    final private int id;
    private int quantita;
    final private int prezzo;

    Prodotto(int id, int quantita, int prezzo) {
        this.id = id;
        this.quantita = quantita;
        this.prezzo = prezzo;
    }

    public int getId() {
        return id;
    }

    public int getQuantita() {
        return quantita;
    }

    public int getPrezzo() {
        return prezzo;
    }

    public synchronized boolean acquisto() {
        if (this.quantita > 0) {
            quantita--;
            return true;
        } else {
            return false;
        }
    }
}

class Cliente implements Runnable {
    final private int id;
    private int soldiDisponibili = 20;

    Cliente(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getSoldiDisponibili() {
        return soldiDisponibili;
    }

    public void checkSol

    @Override
    public void run() {
        int prodottoDaAcquistare = ThreadLocalRandom.current().nextInt(10);

        if ((this.soldiDisponibili >= EsercizioMagazzino.listaProdotti.get(prodottoDaAcquistare).getPrezzo()) && (EsercizioMagazzino.listaProdotti.get(prodottoDaAcquistare).getQuantita() > 0)) {
            if (EsercizioMagazzino.listaProdotti.get(prodottoDaAcquistare).acquisto()) {
                this.soldiDisponibili = this.soldiDisponibili - EsercizioMagazzino.listaProdotti.get(prodottoDaAcquistare).getPrezzo();
            }
        }

        try {
            // pausa tra un acquisto e l'altro
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
        } catch (final InterruptedException e) {
        }
    }
}

public class EsercizioMagazzino {
    static CopyOnWriteArrayList<Prodotto> listaProdotti = new CopyOnWriteArrayList<>();
    static AtomicInteger clientiSenzaSoldi = new AtomicInteger(0);
    static AtomicInteger prodottiNonDisponibili = new AtomicInteger(0);
    static volatile boolean isRunning = true;

    public static void main(final String[] args) {
        final List<Cliente> allClienti = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            allClienti.add(new Cliente(i));
        }

        // Inizializzazione dei prodotti in magazzino
        for (int i = 0; i < 10; i++)
            listaProdotti.add(new Prodotto(i, ThreadLocalRandom.current().nextInt(1, 11), ThreadLocalRandom.current().nextInt(1, 6)));

        final List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            allThreads.add(new Thread(allClienti.get(i)));

        System.out.println("Simulation started");
        for (final Thread t : allThreads)
            t.start();

        while (isRunning) {
            for (int i = 0; i < 10; i++) {
                if (allClienti.get(i).getSoldiDisponibili())
            }
            //Se tutti i clienti sono rimasti senza soldi clientiSenzaSoldi++
            //Se tutti i prodotti sono finiti prodottiNonDisponibili++
        }

        for (final Thread t : allThreads)
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("Simulation finished");
    }
}
*/
