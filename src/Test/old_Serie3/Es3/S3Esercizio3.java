package Test.old_Serie3.Es3;

import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ConcurrentIntegerArray {
    private Lock lock = new ReentrantLock();
    private int[] array;

    public ConcurrentIntegerArray(int size) {
        array = new int[size];
    }

    public int get(int index) {
        return array[index];
    }

    public void set(int index, int value) {
        lock.lock();
        try {
            array[index] = value;
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }
}

class Worker implements Runnable {
    private int id;
    public static ConcurrentIntegerArray sharedArray = new ConcurrentIntegerArray(5);

    public Worker(int id) {
        this.id = id;
    }

    @Override
    public void run() {

    }
}

public class S3Esercizio3 {

    public static void main(String[] args) {
        final List<Worker> allWorkers = new ArrayList<>();
        final List<Thread> allThread = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            final Worker target = new Worker(i);
            allWorkers.add(target);
            final Thread e = new Thread(target);
            allThread.add(e);
            //e.start();
        }

        Worker.sharedArray.reset();

        for (int i = 1; i <= 10; i++) {
            allThread.get(i).start();
        }
    }
}
