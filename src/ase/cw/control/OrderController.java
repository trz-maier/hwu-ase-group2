package ase.cw.control;

import ase.cw.IO.FileReader;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.gui.QueueFrame;
import ase.cw.model.*;
import ase.cw.view.QueueView;
import ase.cw.view.ServerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;


public class OrderController implements OrderProducerListener {
    private static final int EXPECTED_CUSTOMER_ID_LENGTH = 8;
    private static final String ENDLINE = System.lineSeparator();

    private Map<String, Item> stockItems;
    private List<Order> loadedOrders;
    private OrderQueue queuedOrders;
    private List<Order> processedOrders;
    private QueueView queueView;
    private ServerView serverView;
    private QueueFrame qf = new QueueFrame();


    public OrderController() {
        try {
            this.stockItems = FileReader.parseItems("Items.csv");
            this.loadedOrders = FileReader.parseOrders("Orders.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.queuedOrders = new OrderQueue(this.loadedOrders, this);
        Thread t = new Thread(queuedOrders);
        t.start();

        // TODO: initialize this.view
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

    public void generateReportTo(String filename) {
        this.generateReportTo(new File(filename));
    }

    public void generateReportTo(File filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(this.createReport());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOrderProduced(BlockingQueue order, Order producedOrder) {
        qf.setOrdersInQueue(this.queuedOrders.getQueue().toArray(new Order[0]));
    }
}
// TODO: Add logging
