package ase.cw.model;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Thomas on 08.03.2019.
 */
public class ApplicationCloseTask implements Runnable {

    private final OrdersDoneEvent ordersDoneEvent;
    private final Thread producerThread;
    private final BlockingQueue<Order> queue;

    public ApplicationCloseTask(OrdersDoneEvent ordersDoneEvent, Thread producerThread, BlockingQueue<Order> queue) {
        this.ordersDoneEvent = ordersDoneEvent;
        this.producerThread = producerThread;
        this.queue = queue;
    }

    @Override
    public void run() {

        //Wait until producer Thread is done
        try {
            producerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Wait until the orderQueue is empty
        synchronized (queue) {
            while (!queue.isEmpty()) {
                try {
                    queue.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //All servers stopped
        ordersDoneEvent.allServersDone();
    }
}
