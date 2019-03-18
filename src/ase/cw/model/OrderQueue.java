package ase.cw.model;

import ase.cw.interfaces.OrderProducerListener;

import java.util.List;
import java.util.Vector;


public class OrderQueue implements Runnable {

    private List<Order> loadedOrders;
    private OrderProducerListener listener;
    private int maxDelayTime = 1000;

    private Long counter = 0L;

    public OrderQueue(Vector<Order> loadedOrders, OrderProducerListener listener) {
        this.loadedOrders = loadedOrders;
        this.listener = listener;
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
            int delay = (int) (Math.random() * maxDelayTime);
            Order order = loadedOrders.get(i);
            try {
                Thread.sleep(delay);
                counter++;
                order.setCreationOrder(this.counter);
                listener.onOrderProduced(order);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
