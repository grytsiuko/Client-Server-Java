package org.fidoshenyata.Pract2;

import lombok.SneakyThrows;

public class Worker extends Thread {

    private int id;
    private final Data data;

    public Worker(int id, Data d) {
        this.id = id;
        data = d;
        this.start();
    }

    @SneakyThrows
    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 5; i++) {
            synchronized (data) {
                while (data.getState() != id) data.wait();
                if (id == 1) {
                    data.Tic();
                } else if (id == 2) {
                    data.Tak();
                } else {
                    data.Toy();
                }
                data.notifyAll();
            }

        }
    }

}
