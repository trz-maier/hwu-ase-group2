package ase.cw.model;

import ase.cw.interfaces.OrderProducerListener;

import java.util.List;
import java.util.Vector;


public class OrderQueue implements Runnable {

    private List<Order> loadedOrders;
    private OrderProducerListener listener;
    private int maxDelayTime = 1000;
    private Thread currentThread;
    private boolean shouldPause = false;

    private Long counter = 0L;

    public OrderQueue(Vector<Order> loadedOrders, OrderProducerListener listener) {
        this.loadedOrders = loadedOrders;
        this.listener = listener;

        currentThread = Thread.currentThread();
    }

    public void setMaxDelayTime(int delay) {
        this.maxDelayTime = delay;
    }

    public void increaseCounter() {
        this.counter++;
    }

    public Long getCounter() {
        return this.counter;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.loadedOrders.size(); i++) {
            synchronized (currentThread) {
                while (shouldPause) {
                    try {
                        currentThread.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            int delay = (int) (Math.random() * maxDelayTime);
            Order order = loadedOrders.get(i);
            try {
                Thread.sleep(delay);
                increaseCounter();
                order.setCreationOrder(this.counter);
                listener.onOrderProduced(order);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pauseOrderProcess() {
        shouldPause = true;
    }

    public void restartOrderProcess() {
        shouldPause = false;
        synchronized (currentThread) {
            currentThread.notify();
        }
    }


}
