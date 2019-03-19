package ase.cw.model;

import ase.cw.interfaces.OrderProducerListener;

import java.util.List;
import java.util.Vector;


public class OrderQueue implements Runnable {

    private List<Order> loadedOrders;
    private OrderProducerListener listener;
    private int maxDelayTime = 1000;
    private Thread orderQueueThread;
    private boolean shouldPause = false;
    private String name = "Order Queue";


    private Long counter = 0L;

    public OrderQueue(Vector<Order> loadedOrders, OrderProducerListener listener) {
        this.loadedOrders = loadedOrders;
        this.listener = listener;
            if (orderQueueThread == null) {
                orderQueueThread = new Thread(this);
                orderQueueThread.setName(name);
                orderQueueThread.start();
            } else {
                System.out.println(getName() + "already started");
            }
    }

    public String getName() {
        return name;

    }

    public void setMaxDelayTime(int delay) {
        this.maxDelayTime = delay;
    }


    @Override
    public void run() {
        for (Order order : this.loadedOrders) {
            synchronized (orderQueueThread) {

                while (shouldPause) {
                    try {
                        orderQueueThread.wait();
                    } catch (InterruptedException e) {
                    }
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

}

    public void pauseOrderProcess(){
        shouldPause = true;
        synchronized (orderQueueThread) {
            orderQueueThread.interrupt();
        }
    }

    public void restartOrderProcess() {

        shouldPause=false;
        synchronized (orderQueueThread) {
            orderQueueThread.notify();
        }

    }

}
