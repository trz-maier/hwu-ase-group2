package ase.cw.control;

import ase.cw.IO.FileReader;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.interfaces.OrderConsumer;
import ase.cw.interfaces.OrderHandler;
import ase.cw.interfaces.OrderProducerListener;
import ase.cw.log.Log;
import ase.cw.model.*;
import ase.cw.utlities.ReportGenerator;
import ase.cw.view.QueueFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;


public class OrderController implements OrderProducerListener, OrdersDoneEvent, OrderHandler {
    private static final int EXPECTED_CUSTOMER_ID_LENGTH = 8;

    private static final int SERVER_COUNT = 2;
    private static final String FILENAME = "Report.txt";
    private static final Log LOGGER = Log.getLogger();
    private static final int BASE_PROCESSING_TIME = 5000;

    private ThreadLocalRandom randomSeed = ThreadLocalRandom.current();
    private Map<String, Item> stockItems;
    private OrderQueue orderProducer;
    private Vector<Order> loadedOrders = new Vector<>(); // orders loaded from file
    private List<Order> processedOrders = new Vector<>(); // finished orders
    private BlockingQueue<Order> queuedOrders = new PriorityBlockingQueue<>(); // orders produced and placed in Queue
    private List<ServerController> serverList = new Vector<>();
    private QueueFrame queueFrame = new QueueFrame(this);

    private int currentServerNumber = 1;
    private int extraRandomOrdersAdded = 0;
    private boolean applicationClosing = false;

    public OrderController() {
        try {
            loadedOrders = FileReader.parseOrders("Orders.csv");
            this.stockItems = FileReader.parseItems("Items.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create Order producer
        this.orderProducer = new OrderQueue(loadedOrders, this);
        Thread t = new Thread(orderProducer);
        t.start();

        //Create Servers
        for (int i = 0; i < SERVER_COUNT; i++) {
            addServer();
        }

        //Create application close Thread
        ApplicationCloseTask applicationCloseTask = new ApplicationCloseTask(this, t, queuedOrders);
        Thread applicationCloseThread = new Thread(applicationCloseTask);
        applicationCloseThread.setName("Application close thread");
        applicationCloseThread.start();
    }

    public static void main(String[] args) {
        new OrderController();
    }

    /**
     * Checks whether a customer's ID is properly formatted, consisting of 8 alphanumeric characters.
     *
     * @param customerId the customer ID to validate in String format
     * @throws InvalidCustomerIdException when the customer ID is invalid
     */
    public static void validateCustomerId(String customerId) throws InvalidCustomerIdException {
        if (customerId == null) throw new InvalidCustomerIdException("Customer ID is null");
        if (customerId.length() != EXPECTED_CUSTOMER_ID_LENGTH)
            throw new InvalidCustomerIdException(String.format("Customer ID \"%s\" should have a length of %d, found "
                    + "%d", customerId, EXPECTED_CUSTOMER_ID_LENGTH, customerId.length()));

        for (char character : customerId.toCharArray()) {
            if (character >= '0' && character <= '9') continue;
            if (character >= 'a' && character <= 'z') continue;
            if (character >= 'A' && character <= 'Z') continue;
            throw new InvalidCustomerIdException(String.format("Customer ID \"%s\" contains invalid character '%s'",
                    customerId, character));
        }
        return;
    }

    public void addRandomOrder(boolean priority) throws InvalidCustomerIdException {
        String customerId = "C" + randomSeed.nextInt(1000000, 9999999 + 1);
        int noOfItems = randomSeed.nextInt(1, 5);
        Order order = new Order(customerId, priority);
        for (int itemCount = 0; itemCount < noOfItems; itemCount++) {
            Object[] itemKeys = stockItems.keySet().toArray();
            int randomIndex = randomSeed.nextInt(0, itemKeys.length);
            Item item = stockItems.get(itemKeys[randomIndex]);
            order.addOrderItem(item);
        }
        orderProducer.increaseCounter();
        order.setCreationOrder(orderProducer.getCounter());
        onOrderProduced(order);
        this.extraRandomOrdersAdded++;
    }

    public void addServer() {
        synchronized (serverList) {
            if (!applicationClosing) { // If the application is currently closing(if the factory is done and the
                // queue is empty) it is not allowed to add new servers
                currentServerNumber = this.serverList.size() + 1;
                ServerController sc = new ServerController(currentServerNumber, queueFrame, queuedOrders, this);
                this.serverList.add(sc);
                sc.setOrderProcessTime(BASE_PROCESSING_TIME);

            }
        }
    }

    public void removeServer() {
        synchronized (serverList) {
            if (!applicationClosing) {// If the application is currently closing(if the factory is done and the queue
                // is empty) it is not allowed to remove servers
                if (serverList.size() > 0) {
                    ServerController closingServer = serverList.get(serverList.size() - 1);
                    closingServer.stop();
                    serverList.remove(serverList.size() - 1);
                }
            }
        }
    }

    public void setProcessingSpeed(double factor) {
        for (ServerController serverController : serverList) {
            int time = (int) (BASE_PROCESSING_TIME * factor);
            serverController.setOrderProcessTime(time);
            orderProducer.setMaxDelayTime(time);
        }
    }

    public void startProcessing() {
        for (ServerController serverController : serverList) {
            serverController.unPause();
            serverController.enableControls(true, false);

        }
        orderProducer.restartOrderProcess();
    }

    public void pauseProcessing() {
        for (ServerController serverController : serverList) {
            serverController.pause();
            serverController.enableControls(false, false);
        }
        orderProducer.pauseOrderProcess();
    }

    private void generateReportTo(String filename) {
        ReportGenerator.generateReportTo(filename, this.stockItems,
                this.processedOrders.toArray(new Order[processedOrders.size()]));
    }

    @Override
    public void onOrderProduced(Order producedOrder) {
        queuedOrders.add(producedOrder);
        updateQueueFrame();

        LOGGER.log("Order added. Produced: " + this.queuedOrders.size() + ". Pending creation: " + (this.loadedOrders.size() - this.queuedOrders.size()));
    }

    private void updateQueueFrame() {
        Object[] sortedOrders = queuedOrders.toArray();
        Arrays.sort(sortedOrders);

        List<Order> orders = new Vector<>();
        List<Order> priorityOrders = new Vector<>();
        for (Object obj : sortedOrders) {
            Order order = (Order) obj;
            if (order.hasPriority()) {
                priorityOrders.add(order);
            } else {
                orders.add(order);
            }
        }

        SwingUtilities.invokeLater(() -> {
            queueFrame.setOrdersInQueue(orders.toArray(new Order[orders.size()]));
            queueFrame.setOrdersInPriorityQueue(priorityOrders.toArray(new Order[priorityOrders.size()]));
        });
    }

    @Override
    public void allServersDone() {
        //Here we close the application, because the queue is empty and all orders are produced
        //Stop all servers
        synchronized (this.serverList) {
            applicationClosing = true;
        }
        for (ServerController controller : this.serverList) {
            controller.stop();
        }
        for (ServerController controller : this.serverList) {
            while (!controller.isStopped()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOGGER.log(String.format("Orders processed=%s, should be=%s", this.processedOrders.size(),
                this.loadedOrders.size() + this.extraRandomOrdersAdded));
        LOGGER.log("All orders produced and queue is empty, stopping servers and generating report...");

        //All servers are done, so we can close the application
        SwingUtilities.invokeLater(() -> {
            this.queueFrame.dispose();
            this.generateReportTo(FILENAME);
            LOGGER.writeToLogFile();
            System.exit(0);
        });
    }

    @Override
    public void orderReceivedByServer(Order currentOrder, OrderConsumer server) {
        updateQueueFrame();
    }

    @Override
    public void orderFinished(Order currentOrder, OrderConsumer server) {
        synchronized (this) {
            this.processedOrders.add(currentOrder);
        }
    }

    @Override
    public void itemFinished(Order currentOrder, OrderItem item, OrderConsumer server) {
//        throw new UnsupportedOperationException("NOT IMPLEMENTED.");
    }

    @Override
    public void itemTaken(Order currentOrder, OrderItem item, OrderConsumer server) {
//        throw new UnsupportedOperationException("NOT IMPLEMENTED.");
    }
}
