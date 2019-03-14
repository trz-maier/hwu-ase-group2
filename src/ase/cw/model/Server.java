package ase.cw.model;

import ase.cw.log.Log;
import com.sun.tools.javac.Main;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Thomas on 01.03.2019.
 * Important: Every time
 */
public class Server implements OrderConsumer {

    private enum status {FREE, BUSY, PAUSED}

    private final BlockingQueue<Order> orderQueue;
    private final OrderHandler orderHandler;
    private int processTime = 1000;
    private Thread serverThread;
    private String name = "Server";
    private int serverId;
    private boolean stopThread = false;
    private boolean pauseThread = false;
    private status serverStatus;

    public Server(BlockingQueue<Order> queue, OrderHandler orderHandler, int serverId) {

        if (queue == null || orderHandler == null)
            throw new InvalidParameterException("OrderQueue and orderHandler must be not null");
        this.orderQueue = queue;
        this.orderHandler = orderHandler;
        this.serverId = serverId;
        this.serverStatus = status.FREE;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        if (serverThread != null) {
            serverThread.setName(name);
        }
    }

    @Override
    public int getId() {
        return serverId;
    }


    @Override
    public String getStatus() {
        return serverStatus.toString();
    }

    public OrderHandler getOrderHandler() {
        return this.orderHandler;
    }


    @Override
    public void setOrderProcessTime(int processTime) {
        if (processTime < 0) throw new InvalidParameterException("Process time must be greater than 0");
        this.processTime = processTime;
    }

    @Override
    public int getOrderProcessTime() {
        return processTime;
    }

    @Override
    public void startOrderProcess() {
        if (serverThread == null) {
            serverThread = new Thread(new ServerRunnable());
            serverThread.setName(name);
            serverThread.start();
            serverStatus = status.BUSY;
        } else {
            System.out.println(getName() + "already started");
        }

    }

    @Override
    public void pauseOrderProcess() {
        pauseThread = true;
        serverStatus = status.PAUSED;
        logAction("order processing paused");
    }

    @Override
    public void restartOrderProcess() {
        pauseThread = false;
        synchronized (serverThread) {
            serverThread.notify();
        }
        serverStatus = status.FREE;
        logAction("order processing restarted");
    }

    /**
     * Stops the internal serverThread.
     * The Server will finish a started order process before stopping.
     * This function will block, until the server finished the a started order.
     */
    @Override
    public void stopOrderProcess() {
        if (serverThread != null) {
            serverThread.interrupt();
            stopThread = true;
            try {
                serverThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public String toString() {
        return "Server{" +
                "processTime=" + processTime +
                ", serverThread=" + serverThread +
                '}';
    }

    private void logAction(String string) {
        Log.getLogger().log(this.getName()+": "+string);
    }

    /**
     * This class is private, so nobody else can use it and create a potential misbehaviour.
     * If the Server class would implement the Runnable interface, multiple threads(not only the serverThread) could be started out of the same server object.
     * We want to prevent this:
     * Server s = new Server();
     * Thread t1 = new Thread(s);
     * Thread t2 = new Thread(s);
     * Therefore, The Server class is not allowed to implement the runnable interface.
     */
    private class ServerRunnable implements Runnable {

        @Override
        public void run() {
            while (!(Thread.currentThread().isInterrupted() && !stopThread)) {
                Order currentOrder;
                //We actually do not need this synchronized block, since our implemented orderQueue is already thread safe.
                //But to make things more robust and consistent(It is possible to pass a non thread safe queue to the Server, in this case we would need the synchronized block)
                try {
                    //Wait forever until an external interruption occurs
                    currentOrder = orderQueue.take();
                    serverStatus = status.BUSY;
                } catch (InterruptedException e) {
                    break;
                }
                //Tell listener, we proceed a new order
                orderHandler.orderTaken(currentOrder, Server.this);

                List<OrderItem> items = currentOrder.getOrderItems();
                for (OrderItem orderItem : items) {
                    //Proceed item
                    orderHandler.itemTaken(currentOrder, orderItem, Server.this);
                    try {
                        Thread.sleep(processTime);
                    } catch (InterruptedException e) {
                        //Finish the current order, then stop
                        stopThread = true;
                    }
                    //Item finished after processTime ms
                    orderHandler.itemFinished(currentOrder, orderItem, Server.this);

                }
                //Order finished
                orderHandler.orderFinished(currentOrder, Server.this);

                //Pause processing if server on break
                synchronized (serverThread) {
                    if (serverStatus.equals(status.PAUSED)) {
                        try {
                            serverThread.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else serverStatus = status.FREE;

                }
            }
        }
    }
}