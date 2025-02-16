package S5.Es2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Event {
    private final long num;

    public Event(final long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Event: " + num;
    }
}

class EventSource implements Runnable {
    private final Map<Integer, EventListener> allListeners = new HashMap<Integer, EventListener>();

    @Override
    public void run() {
        for (long i = 0; i < 30_000_000; i++) {
            // Crea un nuovo evento
            final Event e = new Event(i);

            // blocco synchronized per evitare accessi concorrenti alla map
            synchronized (this) {
                // Gestisce l'evento per ogni eventListener che si e' messo in
                // ascolto della sorgente.
                for (final int id : allListeners.keySet()) {
                    final EventListener listener = allListeners.get(id);
                    listener.onEvent(id, e);
                }
            }
        }
    }

    // Metodo synchronized per evitare accessi concorrenti alla map
    public synchronized void registerListener(final int id,
                                              final EventListener listener) {
        allListeners.put(id, listener);
    }
}

class EventListener {
    private final int id;

    public EventListener(final int id) {
        this.id = id;
    }

    public void onEvent(final int listenerID, final Event e) {
        // Verifica semplicemente che l'id del listener chiamato dalla sorgente
        // corrisponda con l'istanza del listener
        if (listenerID != id)
            System.out.println("Inconsistent listener ID" + listenerID + " : "
                    + e);
    }
}

public class S5Esercizio2 {
    public static void main(final String[] args) {
        final EventSource eventSource = new EventSource();
        final Thread eventSourceThread = new Thread(eventSource);

        // Fa partire il thread
        eventSourceThread.start();

        // Crea e registra il listener alla sorgente
        final List<EventListener> allListeners = new ArrayList<>();
        for (int i = 1; i <= 20; i++)
        {
            final EventListener listener = new EventListener(i);
            eventSource.registerListener(i, listener);
            allListeners.add(listener);
        }

        // Attende che il Thread termini
        try {
            eventSourceThread.join();
        } catch (final InterruptedException e) {
            // Thread interrotto
        }
    }
}
