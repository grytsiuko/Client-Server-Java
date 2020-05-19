package org.fidoshenyata.Pract2;

import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Data d = new Data();
        Semaphore mutex = new Semaphore(1);

        Worker w2 = new Worker(2, d, mutex);
        Worker w1 = new Worker(1, d, mutex);
        Worker w3 = new Worker(3, d, mutex);

        w1.join();
        w2.join();
        w3.join();
        System.out.println("end of main...");
    }
}
