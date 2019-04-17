package Test.Test1.EsTest1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class Rectangle {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    public Rectangle(final int newX1, final int newY1, final int newX2, final int newY2) {
        x1 = newX1;
        y1 = newY1;
        x2 = newX2;
        y2 = newY2;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public Rectangle resize() {
        final Random random = new Random();
        // genera variazione per punti tra -2 e 2
        final int deltaX2 = random.nextInt(5) - 2;
        final int deltaY2 = random.nextInt(5) - 2;
        // calcola nuove coordinate x2 e y2
        final int newX2 = EsercizioRettangolo.rect.get().getX2() + deltaX2;
        final int newY2 = EsercizioRettangolo.rect.get().getY2() + deltaY2;

        final boolean isLine = (EsercizioRettangolo.rect.get().getX1() == newX2)
                || (EsercizioRettangolo.rect.get().getY1() == newY2);
        final boolean isPoint = (EsercizioRettangolo.rect.get().getX1() == newX2)
                && (EsercizioRettangolo.rect.get().getY1() == newY2);
        final boolean isNegative = (EsercizioRettangolo.rect.get().getX1() > newX2)
                || (EsercizioRettangolo.rect.get().getY1() > newY2);

        if (!isLine && !isPoint && !isNegative) {
            return new Rectangle(EsercizioRettangolo.rect.get().getX1(), EsercizioRettangolo.rect.get().getY1(), newX2, newY2);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "[" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + "]";
    }
}

class Resizer implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(random.nextInt(3) + 2);
            } catch (final InterruptedException e) {
            }

            Rectangle original = null;
            Rectangle resized = null;

            do {
            original = EsercizioRettangolo.rect.get();
            resized = EsercizioRettangolo.rect.get().resize();
        }while (!EsercizioRettangolo.rect.compareAndSet(original, resized));
            System.out.println(EsercizioRettangolo.rect);
        }
    }
}

/**
 * Programma che simula variazioni continue delle dimensioni di un rettangolo
 */
public class EsercizioRettangolo {
    static AtomicReference<Rectangle> rect = new AtomicReference<Rectangle>(new Rectangle(10, 10, 20, 20));

    public static void main(final String[] args) {
        final List<Thread> allThreads = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++)
            allThreads.add(new Thread(new Resizer()));

        System.out.println("Simulation started");
        for (final Thread t : allThreads) {
            t.start();
        }

        for (final Thread t : allThreads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Simulation finished");
    }
}