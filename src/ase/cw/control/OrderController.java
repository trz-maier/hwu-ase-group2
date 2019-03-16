package ase.cw.control;

import ase.cw.IO.FileReader;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.gui.QueueFrame;
import ase.cw.gui.ServerFrame;
import ase.cw.model.Order;
import ase.cw.log.Log;
import ase.cw.model.*;
import ase.cw.view.ServerFrameView;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;


public class OrderController implements OrderProducerListener, ServerStatusListener, OrderHandler, OrdersDoneEvent {
    private static final int EXPECTED_CUSTOMER_ID_LENGTH = 8;
    private static final String ENDLINE = System.lineSeparator();
    private static final int SERVER_COUNT = 3;
    private static final String FILENAME = "Report.txt";
    private static final Log LOGGER = Log.getLogger();

    private Map<String, Item> stockItems;
    private OrderQueue orderProducer;
    private List<Order> processedOrders = new ArrayList<>();
    private List<Server> serverList = new ArrayList<>();
    private BlockingQueue<Order> queuedOrders = new PriorityBlockingQueue<>();
    private List<ServerFrameView> serverFrameViewList = new ArrayList<>();
    private QueueFrame queueFrame = new QueueFrame(this);


    // Total number of orders, which were produced
    private int totalProducedOrders = 0;
    private int totalOrdersHandled = 0;
    // Total number of orders in Orders.csv file
    private int totalOrders = 0;

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
        for (int counter = 1; counter < SERVER_COUNT + 1; counter++) {
            this.createServer(counter);
        }

        //Create application close Thread
        ApplicationCloseTask applicationCloseTask = new ApplicationCloseTask(this, t, serverList, queuedOrders);
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

    private void createServer(int serverId) {
        Server server = new Server(queuedOrders, this, this, serverId);
        server.setName("Server " + serverId);
        server.setOrderProcessTime(5000);
        serverList.add(server);
        serverFrameViewList.add(new ServerFrame(server.getId(), this.queueFrame, this));
        server.startOrderProcess();
    }

    private Server getServerById(int serverId) {
        Server result = null;
        for (Server server : serverList)
            if (server.getId() == serverId) {
                result = server;
            }
        return result;
    }

    private ServerFrameView getServerFrameById(int serverId) {
        ServerFrameView result = null;
        for (ServerFrameView frame : serverFrameViewList)
            if (frame.getServerId() == serverId) {
                result = frame;
            }
        return result;
    }

    public void startProcessing() {
        // TODO: Implement start processing method
    }

    public void pauseProcessing() {
        // TODO: Implement pause processing method
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

    @Override
    public void onServerStatusChange(OrderConsumer server) {
        ServerFrameView frame = getServerFrameById(server.getId());
        frame.updateView(server);
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
    public void orderReceivedByServer(Order currentOrder, OrderConsumer server) {
        //Set a timestamp to a order, as soon as the order is taken by a server
        //SwingUtilities.invokeLater(() -> {
        //We don't need SwingUtilities.invokeLater(()) in this case, but this would improve the stability of our
        // application
        // in case we change something and change values of a order in multiple threads because the order class is
        // not threadsafe.
        currentOrder.setTimestamp(new Date());
        LOGGER.log(server.getName() + " received" + (currentOrder.hasPriority() ? "PRIORITY" : "") + " order.");
        LOGGER.log("has priority: " + currentOrder.hasPriority());
        LOGGER.log("Pending orders in queue: " + this.queuedOrders.size());
        updateQueueFrame();
        getServerFrameById(server.getId()).updateView(server, currentOrder);
    }

    @Override
    public void orderFinished(Order currentOrder, OrderConsumer server) {
        LOGGER.log(server.getName() + " finished order " + this.queuedOrders.size() + " orders in queue");
        synchronized (this) {
            totalOrdersHandled++;
            processedOrders.add(currentOrder);
        }
        getServerFrameById(server.getId()).updateView(server);
    }

    @Override
    public void itemFinished(Order currentOrder, OrderItem item, OrderConsumer server) {
        LOGGER.log(server.getName() + ": finished item = " + item.getItem().toString() + " in Order = " + currentOrder.toString());
        getServerFrameById(server.getId()).updateView(server, currentOrder);
    }

    @Override
    public void itemTaken(Order currentOrder, OrderItem item, OrderConsumer server) {
        LOGGER.log(server.getName() + ": took item = " + item.getItem().toString() + " in Order = " + currentOrder.toString());
        getServerFrameById(server.getId()).updateView(server, currentOrder);
    }

    @Override
    public void allServersDone() {
        //Here we close the application, because the queue is empty and all orders are produced
        //Stop all servers

        LOGGER.log("All orders produced and queue is empty, stopping servers...");
        for (Server server : this.serverList) {
            server.stopOrderProcess();
        }
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

    public void pauseOrderProcess(int serverId) {
        getServerById(serverId).pauseOrderProcess();
    }

    public void restartOrderProcess(int serverId) {
        getServerById(serverId).restartOrderProcess();
    }

}
