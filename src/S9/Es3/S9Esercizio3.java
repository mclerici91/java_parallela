package S9.Es3;

import java.util.Random;
import java.util.concurrent.Semaphore;

class Fork {
    public static final char FORK = '|';
    public static final char NO_FORK = ' ';
    int id;

    public Fork(final int id) {
        this.id = id;
    }
}

class Philosopher extends Thread {
    public static final char PHIL_THINKING = '-';
    public static final char PHIL_LEFT_FORK = '=';
    public static final char PHIL_EATING = 'o';

    private final int id;
    private boolean leftAcquired = false;
    private boolean rightAcquired = false;

    public Philosopher(final int id) {
        this.id = id;
    }

    public boolean isLeftAcquired() {
        return leftAcquired;
    }

    public void setLeftAcquired(boolean leftAcquired) {
        this.leftAcquired = leftAcquired;
    }

    public boolean isRightAcquired() {
        return rightAcquired;
    }

    public void setRightAcquired(boolean rightAcquired) {
        this.rightAcquired = rightAcquired;
    }

    @Override
    public void run() {
        final Random random = new Random();
        final int tableOffset = 4 * id;
        final Object leftLock = S9Esercizio3.listOfLocks[id];
        final Object rightLock = S9Esercizio3.listOfLocks[(id + 1) % S9Esercizio3.NUM_PHILOSOPHERS];
        final int table__farL = tableOffset + 0;
        final int table__left = tableOffset + 1;
        final int table_philo = tableOffset + 2;
        final int table_right = tableOffset + 3;
        final int table__farR = (tableOffset + 4) % (4 * S9Esercizio3.NUM_PHILOSOPHERS);

        while (!isInterrupted()) {
            try {
                Thread.sleep(S9Esercizio3.UNIT_OF_TIME * (random.nextInt(6)));
            } catch (final InterruptedException e) {
                break;
            }
            // Try to get the fork on the left
            synchronized (S9Esercizio3.class) {
                do {
                    if (S9Esercizio3.risorsa.tryAcquire()) {

                        S9Esercizio3.dinerTable[table__farL] = Fork.NO_FORK;
                        S9Esercizio3.dinerTable[table__left] = Fork.FORK;
                        S9Esercizio3.dinerTable[table_philo] = PHIL_LEFT_FORK;
                    }
                    this.setLeftAcquired(true);
                }
             while (!this.isLeftAcquired()) ;
        }



                /*try {
                    sleep(S9Esercizio3.UNIT_OF_TIME * 1);
                } catch (final InterruptedException e) {
                    break;
                }*/
        // Try to get the fork on the right
        synchronized (S9Esercizio3.class) {
            do {
                if (S9Esercizio3.risorsa.tryAcquire()) {

                    S9Esercizio3.dinerTable[table_philo] = PHIL_EATING;
                    S9Esercizio3.dinerTable[table_right] = Fork.FORK;
                    S9Esercizio3.dinerTable[table__farR] = Fork.NO_FORK;
                }
                this.setRightAcquired(true);
            }
         while (!this.isRightAcquired());
    }

            try {
                sleep(S9Esercizio3.UNIT_OF_TIME * 1);
            } catch (final InterruptedException e) {
                break;
            }
            // Release fork
            synchronized (S9Esercizio3.class) {
                //synchronized (S9Esercizio3.class) {
                S9Esercizio3.dinerTable[table__farL] = Fork.FORK;
                S9Esercizio3.dinerTable[table__left] = Fork.NO_FORK;
                S9Esercizio3.dinerTable[table_philo] = PHIL_THINKING;
                S9Esercizio3.dinerTable[table_right] = Fork.NO_FORK;
                S9Esercizio3.dinerTable[table__farR] = Fork.FORK;
                S9Esercizio3.risorsa.release(2);
            }


                //}

        }
    }
}

public class S9Esercizio3 {
    public static Semaphore risorsa = new Semaphore(5);
    public static final int NUM_PHILOSOPHERS = 5;
    public static final int UNIT_OF_TIME = 50;
    public static final Fork[] listOfLocks = new Fork[NUM_PHILOSOPHERS];
    public static char[] dinerTable = null;

    static {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++)
            listOfLocks[i] = new Fork(i);
    }

    public static void main(final String[] a) {
        final char[] lockedDiner = new char[4 * NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            lockedDiner[4 * i + 0] = Fork.NO_FORK;
            lockedDiner[4 * i + 1] = Fork.FORK;
            lockedDiner[4 * i + 2] = Philosopher.PHIL_LEFT_FORK;
            lockedDiner[4 * i + 3] = Fork.NO_FORK;
        }
        final String lockedString = new String(lockedDiner);

        // safe publication of the initial representation
        synchronized (S9Esercizio3.class) {
            dinerTable = new char[4 * NUM_PHILOSOPHERS];
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                dinerTable[4 * i + 0] = Fork.FORK;
                dinerTable[4 * i + 1] = Fork.NO_FORK;
                dinerTable[4 * i + 2] = Philosopher.PHIL_THINKING;
                dinerTable[4 * i + 3] = Fork.NO_FORK;
            }
        }

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            final Thread t = new Philosopher(i);
            // uses this solution to allow terminating the application even if
            // there is a deadlock
            t.setDaemon(true);
            t.start();
        }

        System.out.println("The diner table:");
        long step = 0;
        while (true) {
            step++;

            String curTableString = null;
            synchronized (S9Esercizio3.class) {
                curTableString = new String(dinerTable);
            }
            System.out.println(curTableString + "   " + step);

            if (lockedString.equals(curTableString))
                break;
            try {
                Thread.sleep(UNIT_OF_TIME);
            } catch (final InterruptedException e) {
                System.out.println("Interrupted.");
            }
        }
        System.out.println("The diner is locked.");
    }
}