package ase.cw.control;

import ase.cw.IO.FileReader;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.interfaces.OrderConsumer;
import ase.cw.interfaces.OrderHandler;
import ase.cw.interfaces.OrderProducerListener;
import ase.cw.log.Log;
import ase.cw.model.*;
import ase.cw.view.QueueFrame;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;


public class OrderController implements OrderProducerListener,OrdersDoneEvent, OrderHandler {
    private static final int EXPECTED_CUSTOMER_ID_LENGTH = 8;
    private static final String ENDLINE = System.lineSeparator();
    private static final int SERVER_COUNT = 2;
    private static final String FILENAME = "Report.txt";
    private static final Log LOGGER = Log.getLogger();
    private static final int BASE_PROCESSING_TIME = 5000;

    private Map<String, Item> stockItems;
    private OrderQueue orderProducer;
    private List<Order> processedOrders = new ArrayList<>();
    private List<ServerController> serverList = new ArrayList<>();
    private BlockingQueue<Order> queuedOrders = new PriorityBlockingQueue<>();
    private QueueFrame queueFrame = new QueueFrame(this);


    // Total number of orders, which were produced
    private int totalProducedOrders = 0;
    private int totalOrdersHandled = 0;
    // Total number of orders in Orders.csv file
    private int totalOrders = 0;

    private int currentServerNumber =1;
    private boolean applicationClosing=false;

    public OrderController() {
        List<Order> loadedOrders = null;
        try {
            loadedOrders = FileReader.parseOrders("Orders.csv");
            this.stockItems = FileReader.parseItems("Items.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        totalOrders = loadedOrders.size();

        //Create Order producer
        this.orderProducer = new OrderQueue(loadedOrders, this);
        Thread t = new Thread(orderProducer);
        t.start();

        //Create Servers
        for (int i=0; i<SERVER_COUNT; i++) {
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
        String customerId = "C"+ThreadLocalRandom.current().nextInt(1000000, 9999999);
        int noOfItems = ThreadLocalRandom.current().nextInt(1, 5);
        Order order = new Order(customerId, priority);
        for (int i = 0; i < noOfItems; i++) {
            int rIdx = ThreadLocalRandom.current().nextInt(stockItems.size());
            Object[] keys = stockItems.keySet().toArray();
            Object key = keys[rIdx];
            Item item = stockItems.get(key);
            order.addOrderItem(item);
        }
        onOrderProduced(order);
    }

    public void addServer() {
        synchronized (serverList){
            if(!applicationClosing){ // If the application is currently closing(if the factory is done and the queue is empty) it is not allowed to add new servers
                currentServerNumber=this.serverList.size()+1;
                this.serverList.add(new ServerController(currentServerNumber,queueFrame,queuedOrders,this));
            }
        }
/*
        int max = 0;
        for (Server server : serverList)
            if (server.getId() > max) {
                max = server.getId();
            }
        int serverId = max+1;
        Server server = new Server(queuedOrders, this, this, serverId);
        server.setName("Server " + serverId);
        server.setOrderProcessTime(BASE_PROCESSING_TIME);
        serverList.add(server);
        serverFrameViewList.add(new ServerFrame(server.getId(), this.queueFrame, this));
        server.startOrderProcess();*/
    }

    public void removeServer() {
        synchronized (serverList){
            if(!applicationClosing) {// If the application is currently closing(if the factory is done and the queue is empty) it is not allowed to remove servers
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
            int time = (int) (BASE_PROCESSING_TIME*factor);
            serverController.setOrderProcessTime(time);
            orderProducer.setMaxDelayTime(time);
        }
    }

    public void startProcessing() {
        for (ServerController serverController : serverList) {
            serverController.unPause();
        }
        orderProducer.restartOrderProcess();
    }

    public void pauseProcessing() {
        for (ServerController serverController : serverList) {
            serverController.pause();
        }
        orderProducer.pauseOrderProcess();
    }

    /**
     * Generates the total report to be outputted in String format
     *
     * @return String representation of the report text body
     */
    private String createReport() {
        // all items in menu ✓
        // number of times each item sold ✓
        // income for all processedOrders ✓
        double sumTotal = 0;
        double sumSubtotal = 0;
        Map<Item, Integer> itemSoldQuantities = new HashMap<>();
        StringBuilder builder = new StringBuilder(this.stockItems.size() * 20);

        for (Order order : this.processedOrders) {
            Bill orderBill = order.getBill();
            sumSubtotal += orderBill.getSubtotal();
            sumTotal += orderBill.getTotal();

            for (OrderItem orderItem : order.getOrderItems()) {
                Item item = orderItem.getItem();

                Integer soldCount = itemSoldQuantities.get(item);
                if (soldCount == null) {
                    itemSoldQuantities.put(item, 1);
                } else {
                    itemSoldQuantities.put(item, soldCount + 1);
                }
            }
        }
        String leftHeader = "----------- Item -----------";
        String separator = " | ";
        String rightHeader = "--- Quantities Sold ---";
        builder.append(leftHeader).append(separator).append(rightHeader).append(ENDLINE);

        this.stockItems.forEach((itemId, item) -> {
            Integer itemSoldQuantity = itemSoldQuantities.containsKey(item) ? itemSoldQuantities.get(item) : 0;
            builder.append(this.padString(item.getName(), leftHeader.length())).append(separator).append(itemSoldQuantity.toString()).append(ENDLINE);
        });
        builder.append(ENDLINE).append(ENDLINE).append("Total Sales w/o discounts: ").append(String.format("£%.2f",
                sumSubtotal)).append(ENDLINE).append("Total Sales with discounts: ").append(String.format("£%.2f",
                sumTotal));

        return builder.toString();
    }

    private String padString(String str, int width) {
        return this.padString(str, width, ' ');
    }

    private String padString(String str, int width, char fill) {
        return String.format("%-" + width + "s", str).replace(' ', fill);
    }

    private void generateReportTo(String filename) {
        this.generateReportTo(new File(filename));
    }

    private void generateReportTo(File filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(this.createReport());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOrderProduced(Order producedOrder) {
        LOGGER.log("Order produced = " + this.queuedOrders.size() + " orders remaining");
        queuedOrders.add(producedOrder);
        updateQueueFrame();
        synchronized (this) {
            //Synchronized not needed, since only one thread will call this method, but if we decide to add multiple
            // order producers, we need the synchronization.
            //To avoid that we will search for bugs later Thomas added the synchronized

            totalProducedOrders++;
        }
        LOGGER.log("Order added: "+producedOrder+". Total order count: "+totalProducedOrders);
    }

    private void updateQueueFrame() {
        List<Order> orders = new ArrayList<>();
        List<Order> priorityOrders = new ArrayList<>();
        for (Order order : this.queuedOrders) {
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
        LOGGER.log("All orders produced and queue is empty, stopping servers...");

        LOGGER.log(totalOrdersHandled + " orders handled should be = " + this.totalProducedOrders);
        LOGGER.log("Stopping servers done, closing application and generating Report");

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
        synchronized (this){
            this.totalOrdersHandled++;
            this.processedOrders.add(currentOrder);
        }

    }

    @Override
    public void itemFinished(Order currentOrder, OrderItem item, OrderConsumer server) {

    }

    @Override
    public void itemTaken(Order currentOrder, OrderItem item, OrderConsumer server) {

    }
}
