package S7.Es1.ConcurrentCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class TestWorker implements Runnable {
    private final int id;
    private final static Map<String, Integer> sharedMap = new ConcurrentHashMap<String, Integer>();
    private int counter = 0;

    public TestWorker(final int id) {
        this.id = id;
    }

    @Override
    public void run() {
        final Random random = new Random();
        final Integer int1 = new Integer(1);
        final Integer int5 = new Integer(5);
        final Integer int10 = new Integer(10);
        int cnt = 100000;

        while (--cnt > 0) {
            final String key = getClass().getSimpleName()
                    + random.nextInt(S7Esercizio1.NUM_WORKERS);
            updateCounter(random.nextBoolean());

            if (counter == 0) {
                sharedMap.remove(key, int1);
                log("{" + key + "} remove 1");
            } else if (counter == 1) {
                sharedMap.putIfAbsent(key, int1);
                log("{" + key + "} put 1");

            } else if (counter == 5) {
                if (sharedMap.replace(key, 10, int5)) {
                    final Integer prev = sharedMap.put(key, int5);
                    log("{" + key + "} replace " + prev.intValue() + " with 5");
                }
            } else if (counter == 10) {
                final Integer prev = sharedMap.replace(key, int10);
                if (prev != null) {
                    log("{" + key + "} replace " + prev.intValue() + " with 10");
                }
            }
        }
    }

    private final void updateCounter(final boolean increment) {
        if (increment) {
            if (++counter > 10)
                counter = 0;
        } else {
            if (--counter < 0)
                counter = 10;
        }
    }

    private final void log(final String msg) {
        System.out.println(getClass().getSimpleName() + id + ": " + msg);
    }
}

public class S7Esercizio1 {
    static final int NUM_WORKERS = 50;

    public static void main(final String[] args) {
        final List<Thread> allThreads = new ArrayList<Thread>();

        for (int i = 0; i < NUM_WORKERS; i++) {
            allThreads.add(new Thread(new TestWorker(i)));
        }

        System.out.println("---------------------------");
        for (final Thread t : allThreads)
            t.start();

        for (final Thread t : allThreads)
            try {
                t.join();
            } catch (final InterruptedException e) {
                /* do nothing */
            }
        System.out.println("---------------------------");
    }
}