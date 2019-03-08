package ase.cw.model;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;


public class OrderQueue implements Runnable {

    private List<Order> loadedOrders;
    private BlockingQueue<Order> queuedOrders = new LinkedBlockingQueue<>();
    private OrderProducerListener opl;

    public OrderQueue(List<Order> loadedOrders, OrderProducerListener opl) {
        this.loadedOrders = loadedOrders;
        this.opl = opl;
    }

    @Override
    public void run() {
        for (Order order : this.loadedOrders) {
            int delay = ThreadLocalRandom.current().nextInt(1000, 9000);
            try {
                Thread.sleep(delay);
                queuedOrders.put(order);
                opl.onOrderProduced(queuedOrders, order);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public BlockingQueue<Order> getQueue() {
        return this.queuedOrders;
    }
}
