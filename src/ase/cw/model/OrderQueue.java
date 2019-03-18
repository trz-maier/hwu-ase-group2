package ase.cw.model;

import ase.cw.interfaces.OrderProducerListener;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;


public class OrderQueue implements Runnable {

    private List<Order> loadedOrders;
    private OrderProducerListener listener;
    private int maxDelayTime = 1000;

    public OrderQueue(Vector<Order> loadedOrders, OrderProducerListener listener) {
        this.loadedOrders = loadedOrders;
        this.listener = listener;
    }

    public void setMaxDelayTime(int delay) {
        this.maxDelayTime = delay;
    }

    @Override
    public void run() {
        for (int counter =0; counter < this.loadedOrders.size();counter++) {
            int delay = (int) (Math.random() * maxDelayTime);
            Order order = loadedOrders.get(counter);
            try {
                Thread.sleep(delay);
                listener.onOrderProduced(order);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
