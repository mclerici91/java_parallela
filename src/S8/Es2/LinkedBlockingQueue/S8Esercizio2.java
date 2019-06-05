package S8.Es2.LinkedBlockingQueue;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class S8Esercizio2 {
    private static class Amico implements Runnable {
        private final String nome;
        private final Queue<String> postaEntrata;
        private Amico other;

        public Amico(final String nome) {
            this.nome = nome;
            this.postaEntrata = new LinkedBlockingQueue<>();
        }

        @Override
        public void run() {
            final Random random = new Random();
            final int nextInt = 1 + random.nextInt(5);
            for (int i = 0; i < nextInt; i++) {
                final String msg = new String("Messaggio" + i + " da " + nome);
                // Mette la lettera nella bucalettere dell'amico
                other.postaEntrata.add(msg);
            }
            int lettere = 0;
            while (true) {
                String inMessge;
                do {
                    // Controlla la propria bucalettera
                    if (postaEntrata.isEmpty())
                        inMessge = null;
                    else
                        inMessge = postaEntrata.poll();
                } while (inMessge == null);
                log("Ricevuto " + inMessge);
                if (lettere == 150) {
                    log("Ho finito le lettere!");
                    break;
                }
                try {
                    Thread.sleep(5 + random.nextInt(46));
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                final String msg = new String("Risposta" + lettere + " da " + nome);
                // Metti la lettera nella bucalettere dell'amico.
                synchronized (other.postaEntrata) {
                    other.postaEntrata.add(msg);
                }
                lettere++;
            }
        }

        public void setAmico(final Amico other) {
            this.other = other;
        }

        private final void log(final String msg) {
            System.out.println(nome + ": " + msg);
        }
    }

    public static void main(final String[] args) {
        final Amico uno = new Amico("Pippo");
        final Amico due = new Amico("Peppa");
        uno.setAmico(due);
        due.setAmico(uno);
        final Thread tUno = new Thread(uno);
        final Thread tDue = new Thread(due);
        System.out.println("Simulation started!");
        tUno.start();
        tDue.start();
        try {
            tUno.join();
            tDue.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Simulation finished!");
    }
}