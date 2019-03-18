package ase.cw.model;

import ase.cw.interfaces.OrderConsumer;
import ase.cw.interfaces.OrderHandler;
import ase.cw.log.Log;
import ase.cw.utlities.ServerStatusEnum.ServerStatus;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Thomas on 01.03.2019.
 * Important: Every time
 */
public class Server implements OrderConsumer {

    private final Log LOGGER = Log.getLogger();
    private final BlockingQueue<Order> orderQueue;
    private final OrderHandler orderHandler;
    private int processTime = 1000;
    private Thread serverThread;
    private String name = "Server";
    private int serverId;
    private boolean shouldStop = false;
    private boolean shouldPause = false;
    private ServerStatus serverStatus;
    private ServerStatusListener ssl;
    private ServerStatus rawStatus;

    public Server(BlockingQueue<Order> queue, OrderHandler orderHandler, ServerStatusListener ssl, int serverId) {
        if (queue == null || orderHandler == null)
            throw new InvalidParameterException("OrderQueue and orderHandler must be not null");
        this.orderQueue = queue;
        this.orderHandler = orderHandler;
        this.serverId = serverId;
        this.ssl = ssl;
        this.serverStatus = ServerStatus.FREE;
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

    private synchronized void updateStatus(ServerStatus status) {
        this.rawStatus = status;
        ServerStatus newStatus = status;
        if (shouldStop && status != ServerStatus.STOPPED) {
            newStatus = ServerStatus.TO_BE_STOPPED;
        } else if (shouldPause && status != ServerStatus.PAUSED && status != ServerStatus.TO_BE_STOPPED && !shouldStop) {
            newStatus = ServerStatus.TO_BE_PAUSED;
        }

        if (newStatus != serverStatus) {

            serverStatus = newStatus;
            LOGGER.log("Status set to " + newStatus);
            if (ssl != null) {
                ssl.onServerStatusChange(this);
            }
        }
    }

    private synchronized void clearStatus() {
        ServerStatus newStatus = rawStatus;

        if ((!shouldStop && serverStatus == ServerStatus.TO_BE_STOPPED) || (!shouldPause && serverStatus == ServerStatus.TO_BE_PAUSED)) {
            serverStatus = rawStatus;
            LOGGER.log("Status set to " + newStatus);
            if (ssl != null) {
                ssl.onServerStatusChange(this);
            }
        }
    }

    public ServerStatus getStatus() {
        return serverStatus;
    }

    public OrderHandler getOrderHandler() {
        return this.orderHandler;
    }

    @Override
    public int getOrderProcessTime() {
        return processTime;
    }

    @Override
    public void setOrderProcessTime(int processTime) {
        if (processTime < 0) throw new InvalidParameterException("Process time must be greater than 0");
        this.processTime = processTime;
    }

    @Override
    public void startOrderProcess() {
        if (serverThread == null) {
            serverThread = new Thread(new ServerRunnable());
            serverThread.setName(name);
            serverThread.start();
        } else {
            System.out.println(getName() + "already started");
        }
    }

    @Override
    public void pauseOrderProcess() {
        // this does not pause the Status immediately but waits until current order is processed
        shouldPause = true;
        synchronized (serverThread) {
            serverThread.interrupt();
        }
    }

    @Override
    public void restartOrderProcess() {
        shouldPause = false;
        clearStatus();

        synchronized (serverThread) {
            serverThread.notify();
        }
    }

    /**
     * Stops the internal serverThread.
     * The Server will finish a started order process before stopping.
     * This function will block, until the server finished the a started order.
     */
    @Override
    public void stopOrderProcess() {
        if (serverThread != null) {
            shouldStop = true;
            serverThread.interrupt();
            try {
                serverThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "Server{" + "processTime=" + processTime + ", serverThread=" + serverThread + '}';
    }

    /**
     * This class is private, so nobody else can use it and create a potential misbehaviour.
     * If the Server class would implement the Runnable interface, multiple threads(not only the serverThread) could
     * be started out of the same server object.
     * We want to prevent this:
     * Server s = new Server();
     * Thread t1 = new Thread(s);
     * Thread t2 = new Thread(s);
     * Therefore, The Server class is not allowed to implement the runnable interface.
     */
    private class ServerRunnable implements Runnable {

        @Override
        public void run() {
            while (!shouldStop) {
                Order currentOrder = null;
                boolean takeNextOrder = true;
                while (takeNextOrder) {
                    try {
                        //Wait forever until an external interruption occurs
                        currentOrder = orderQueue.take();
                        pauseIfneeded();
                        takeNextOrder = false;
                    } catch (InterruptedException e) {
                        //If interrupt happens, we know we should either pause or stop
                        if (shouldStop) {
                            //Stop
                            break;
                        } else {
                            pauseIfneeded();
                            //If pause is done, take next order
                            takeNextOrder = true;
                        }
                    }
                }
                if (shouldStop) {
                    break;
                }

                updateStatus(ServerStatus.BUSY);

                if (currentOrder == null) {
                    //Should never happen
                    throw new java.lang.IllegalStateException("Current order is null");
                }
                //Tell listener, we proceed a new order
                orderHandler.orderReceivedByServer(currentOrder, Server.this);

                List<OrderItem> items = currentOrder.getOrderItems();
                for (OrderItem orderItem : items) {
                    //Proceed item
                    orderHandler.itemTaken(currentOrder, orderItem, Server.this);
                    long sleepTime = processTime;
                    while (true) {

                        long startSleep = System.currentTimeMillis();
                        try {
                            Thread.sleep(sleepTime);
                            break;
                        } catch (InterruptedException e) {
                            updateStatus(serverStatus);
                            long delta = startSleep - System.currentTimeMillis();
                            if (sleepTime >= delta) break;
                            else {
                                sleepTime = sleepTime - delta;
                            }
                            //If interrupt happens, we know we should either pause or stop
                            //If the server should stop, continue finishing the order and then stop
                        }
                    }
                    //Item finished after processTime ms
                    orderHandler.itemFinished(currentOrder, orderItem, Server.this);
                    updateStatus(serverStatus);


                }
                //Order finished
                orderHandler.orderFinished(currentOrder, Server.this);

                //Server is free until the server takes a order
                updateStatus(ServerStatus.FREE);

                //Pause processing if server on break
                pauseIfneeded();

            }
            updateStatus(ServerStatus.STOPPED);
        }

        private void pauseIfneeded() {
            synchronized (serverThread) {
                ServerStatus previous = Server.this.getStatus();
                /**
                 * If a server should stop, we dont allow a pause.
                 */
                while (shouldPause && !shouldStop) {
                    try {
                        updateStatus(ServerStatus.PAUSED);
                        serverThread.wait();
                    } catch (InterruptedException e) {
                        //Interrupted when the server should stop.
                    }
                }
                //After we are done with the pause, set the state to the previous state.
                updateStatus(previous);

            }

        }
    }
}