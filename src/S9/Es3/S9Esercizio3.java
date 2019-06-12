package S9.Es3;

import java.util.Random;

class Fork {
    public static final char FORK = '|';
    public static final char NO_FORK = ' ';
    int id;
    boolean busy = false;

    public Fork(final int id) {
        this.id = id;
    }

    public boolean use() {
        if (!busy) {
            busy = true;
            return true;
        } else {
            return false;
        }
    }

    public void setAvailable() {
        busy = false;
    }
}

class Philosopher extends Thread {
    public static final char PHIL_THINKING = '-';
    public static final char PHIL_LEFT_FORK = '=';
    public static final char PHIL_EATING = 'o';
    public static final char PHIL_HUNGRY = '!';
    private final int id;

    public Philosopher(final int id) {
        this.id = id;
    }

    @Override
    public void run() {
        final Random random = new Random();
        final int tableOffset = 4 * id;

        final Fork leftFork = S9Esercizio3.listOfLocks[id];
        final Fork rightFork = S9Esercizio3.listOfLocks[(id + 1) % S9Esercizio3.NUM_PHILOSOPHERS];

        final int table__farL = tableOffset + 0;
        final int table__left = tableOffset + 1;
        final int table_philo = tableOffset + 2;
        final int table_right = tableOffset + 3;
        final int table__farR = (tableOffset + 4) % (4 * S9Esercizio3.NUM_PHILOSOPHERS);

        boolean gotTwoForks = false;

        final int whoAtLeft;
        if (id == 0) {
            whoAtLeft = S9Esercizio3.NUM_PHILOSOPHERS - 1;
        } else {
            whoAtLeft = (id - 1);
        }
        final int whoAtRight = ((id + 1) % S9Esercizio3.NUM_PHILOSOPHERS);

        while (!isInterrupted()) {
            try {
                Thread.sleep(S9Esercizio3.UNIT_OF_TIME * (random.nextInt(6)));
            } catch (final InterruptedException e) {
                break;
            }

            synchronized (S9Esercizio3.class) {
                S9Esercizio3.dinerTable[table_philo] = PHIL_HUNGRY;
                // Try to get the fork on the left
                if (leftFork.use()) {
                    S9Esercizio3.dinerTable[table__farL] = Fork.NO_FORK;
                    S9Esercizio3.dinerTable[table__left] = Fork.FORK;

                    // Try to get the fork on the right
                    if (rightFork.use()) {
                        S9Esercizio3.dinerTable[table_philo] = PHIL_EATING;
                        S9Esercizio3.dinerTable[table_right] = Fork.FORK;
                        S9Esercizio3.dinerTable[table__farR] = Fork.NO_FORK;
                        gotTwoForks = true;
                    } else {
                        leftFork.setAvailable();
                        S9Esercizio3.dinerTable[table__farL] = Fork.FORK;
                        S9Esercizio3.dinerTable[table__left] = Fork.NO_FORK;
                    }
                }

                if (!gotTwoForks) {
                    try {
                        while (S9Esercizio3.dinerTable[table_philo] != PHIL_EATING) {
                            S9Esercizio3.class.wait();
                        }

                        S9Esercizio3.dinerTable[table__farL] = Fork.NO_FORK;
                        S9Esercizio3.dinerTable[table__left] = Fork.FORK;
                        S9Esercizio3.dinerTable[table_philo] = PHIL_EATING;
                        S9Esercizio3.dinerTable[table_right] = Fork.FORK;
                        S9Esercizio3.dinerTable[table__farR] = Fork.NO_FORK;
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
            }

            // Eating
            try {
                sleep(S9Esercizio3.UNIT_OF_TIME * 1);
            } catch (final InterruptedException e) {
                break;
            }

            // Thinking
            synchronized (S9Esercizio3.class) {
                leftFork.setAvailable();
                S9Esercizio3.dinerTable[table__farL] = Fork.FORK;
                S9Esercizio3.dinerTable[table__left] = Fork.NO_FORK;

                rightFork.setAvailable();
                S9Esercizio3.dinerTable[table_right] = Fork.NO_FORK;
                S9Esercizio3.dinerTable[table__farR] = Fork.FORK;

                S9Esercizio3.dinerTable[table_philo] = PHIL_THINKING;

                checkAndResume(whoAtLeft);
                checkAndResume(whoAtRight);
            }
        }
    }

    private static void checkAndResume(final int neighborPhilosopher) {
        final int tableOffset = neighborPhilosopher * 4;
        final int whoAtLeft;
        if ((tableOffset - 2) < 0)
            whoAtLeft = (4 * S9Esercizio3.NUM_PHILOSOPHERS - 2);
        else
            whoAtLeft = (tableOffset - 2);
        final int rightPhilo = (tableOffset + 6) % (4 * S9Esercizio3.NUM_PHILOSOPHERS);
        final int table_philo = tableOffset + 2;
        if (S9Esercizio3.dinerTable[table_philo] == PHIL_HUNGRY
                && S9Esercizio3.dinerTable[whoAtLeft] != PHIL_EATING
                && S9Esercizio3.dinerTable[rightPhilo] != PHIL_EATING) {
            S9Esercizio3.dinerTable[table_philo] = PHIL_EATING;
            final Fork leftFork = S9Esercizio3.listOfLocks[neighborPhilosopher];
            final Fork rightFork = S9Esercizio3.listOfLocks[(neighborPhilosopher + 1)
                    % S9Esercizio3.NUM_PHILOSOPHERS];
            leftFork.use();
            rightFork.use();
            S9Esercizio3.class.notifyAll();
        }
    }
}

public class S9Esercizio3 {
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
