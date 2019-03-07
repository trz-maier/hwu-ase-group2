package ase.cw.model;

import ase.cw.gui.QueueFrame;
import ase.cw.view.QueueView;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Queue implements Runnable {

    private List<Order> readOrders;

    private BlockingQueue<Order> queuedOrders = new LinkedBlockingQueue<Order>();

    public Queue(List<Order> readOrders) {
        this.readOrders = readOrders;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (Order order : this.readOrders) {
                try {
                    Thread.sleep(2000);
                    queuedOrders.put(order);
                    System.out.println("Orders in queue: "+this.queuedOrders);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public BlockingQueue<Order> getQueue() {
        return this.queuedOrders;
    }

    public void addToQueue(Order order) {
        try {
            Thread.sleep(2000);
            queuedOrders.put(order);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Order takeFromQueue() {
        Order returnedOrder = null;
        try {
            returnedOrder = queuedOrders.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return returnedOrder;
    } 
    

}
