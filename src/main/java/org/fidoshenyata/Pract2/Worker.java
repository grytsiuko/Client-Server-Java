package org.fidoshenyata.Pract2;

import lombok.SneakyThrows;

import java.util.concurrent.Semaphore;

public class Worker extends Thread {

    private int id;
    private final Data data;
    private final Semaphore mutex;

    public Worker(int id, Data d, Semaphore mutex) {
        this.id = id;
        data = d;
        this.mutex = mutex;
        this.start();
    }

    @SneakyThrows
    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 5; i++) {
            while (data.getState() != id) mutex.release();
            mutex.acquire();
            if (id == 1) {
                data.Tic();
            } else if (id == 2) {
                data.Tak();
            } else {
                data.Toy();
            }
            mutex.release();
        }
    }

}
