package S7.Es4;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

class RemoveWorker implements Runnable {
    private int id;

    public RemoveWorker(int id) {
        this.id = id;
    }


    @Override
    public void run() {

        while (true) {
            int tentativi = 0;
            Day selectedDay = Day.values()[ThreadLocalRandom.current().nextInt(Day.values().length)];

            String oldString = null;
            String newString = null;

            do {
                oldString = S7Esercizio4.week.get(selectedDay);
                if (oldString.equals("")) {
                    S7Esercizio4.week.remove(selectedDay);
                    if (S7Esercizio4.week.isEmpty()) {
                        System.out.println("Tutte le stringhe sono state eliminate");
                        return;
                    }
                    break;
                }

                newString = oldString.substring(1);
                tentativi++;
            } while (!S7Esercizio4.week.replace(selectedDay, oldString, newString));

            if (tentativi > 1) {
                System.out.println("RemoveWorker" + this.id + ": Updated " + selectedDay + " after " + tentativi + " tries");
            }
        }
    }
}

enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
}


public class S7Esercizio4 {
    static ConcurrentHashMap<Day, String> week = new ConcurrentHashMap<>(7);

    public static String getRandomString(int length) {
        final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+";
        StringBuilder result = new StringBuilder();
        while (length > 0) {
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            length--;
        }
        return result.toString();
    }


    public static void main(String[] args) {

        for (Day d : Day.values()) {
            week.put(d, getRandomString(10000));
        }

        final List<RemoveWorker> allWorker = new ArrayList<>();
        final List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            final RemoveWorker worker = new RemoveWorker(i + 1);
            allWorker.add(worker);
            allThreads.add(new Thread(worker));
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
