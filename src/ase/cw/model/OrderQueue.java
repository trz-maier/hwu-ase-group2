package ase.cw.model;

import ase.cw.interfaces.OrderProducerListener;

import java.util.List;


public class OrderQueue implements Runnable {

    private List<Order> loadedOrders;
    private OrderProducerListener opl;
    private int maxDelayTime = 1000;
    private Thread orderQueueThread;
    private boolean shouldPause = false;



    private String name = "Order Queue";
//    private boolean shouldStop = false;

    public OrderQueue(List<Order> loadedOrders, OrderProducerListener opl) {
        this.loadedOrders = loadedOrders;
        this.opl = opl;

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
