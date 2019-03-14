package ase.cw.model;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class OrderQueue implements Runnable {

    private List<Order> loadedOrders;
    private OrderProducerListener opl;
    private int maxDelayTime = 1000;

    public OrderQueue(List<Order> loadedOrders, OrderProducerListener opl) {
        this.loadedOrders = loadedOrders;
        this.opl = opl;
    }

    @Override
    public void run() {
        for (Order order : this.loadedOrders) {
            int delay = (int) (Math.random() * maxDelayTime);
            try {
                Thread.sleep(delay);
                opl.onOrderProduced(order);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
