package S7.Es3.SyncBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class Letter {
    private int sender;

    public Letter(int sender) {
        this.sender = sender;
    }
}

class Friend implements Runnable
{
    private int id;

    public Friend(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        int totLetters = 150;

        synchronized (S7Esercizio3.sharedPost) {
            // Scrittura delle prime 2-5 lettere
            for (int i = 0; i < ThreadLocalRandom.current().nextInt(2,5); i++) {
                S7Esercizio3.sharedPost.add(new Letter(this.id));
                System.out.println("Amico" + this.id + ": spedito lettera nr. " + (i+1));
            }

            //Scrittura delle risposte e sleep
            while (totLetters > 0) {
                //synchronized (S7Esercizio3.sharedPost) {
                    S7Esercizio3.sharedPost.add(new Letter(this.id));
                //}
                System.out.println("Amico" + this.id + ": spedito risposta nr. " + (151-totLetters));
                totLetters--;

                //Tempo di ricezione lettera
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(5,50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

public class S7Esercizio3 {
    static List<Letter> sharedPost = new ArrayList<>();

    public static void main(final String[] args) {
        final List<Friend> allFriends = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final Friend friend = new Friend(i+1);
            allFriends.add(friend);
            allThreads.add(new Thread(friend));
        }

        System.out.println("Simulation started");
        System.out.println("--------------------------------------------");
        for (final Thread t : allThreads)
            t.start();

        for (final Thread t : allThreads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("--------------------------------------------");
        System.out.println("Simulation finished");
    }
}
