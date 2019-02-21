package ase.cw.control;

import ase.cw.IO.FileReader;
import ase.cw.exceptions.EmptyOrderException;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.exceptions.NoOrderException;
import ase.cw.gui.OrderFrame;
import ase.cw.model.Bill;
import ase.cw.model.Item;
import ase.cw.model.Order;
import ase.cw.model.OrderItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 04.02.2019.
 */
public class OrderController {
    private static final int EXPECTED_CUSTOMER_ID_LENGTH = 8;

    private Map<String, Item> stockItems;
    private List<Order> orders;
    private Order pendingOrder;
    private OrderFrame orderFrame;

    private OrderController() {
        try {
            this.stockItems = FileReader.parseItems("Items.csv");
            this.orders = FileReader.parseOrders("Orders.csv");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
        }

        this.orderFrame = new OrderFrame();
        this.orderFrame.setOrderController(this);
        this.orderFrame.setStockItems(stockItems.values().toArray(new Item[stockItems.size()]));
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

    public void createNewOrder(String customerId) throws InvalidCustomerIdException, IllegalStateException {
        validateCustomerId(customerId);

        if (pendingOrder != null) throw new IllegalStateException("New order added while pending order exists");
        pendingOrder = new Order(customerId);

        this.updateOrderFrameBill();
    }

    private void updateOrderFrameOrderItems() {
        List<OrderItem> itemsInOrder = pendingOrder.getOrderItems();
        this.orderFrame.setOrderItems(itemsInOrder.toArray(new OrderItem[itemsInOrder.size()]));
    }

    private void updateOrderFrameBill() {
        Bill bill = pendingOrder.getBill();
        orderFrame.setOrderTotals(bill.getSubtotal(), bill.getDiscount(), bill.getTotal());
    }

    public void addItemToPendingOrder(Item itemToAdd) throws NoOrderException {
        if (pendingOrder == null) throw new NoOrderException("No pending order found");
        if (itemToAdd == null) throw new IllegalArgumentException("Null item added to order");
        pendingOrder.addOrderItem(itemToAdd);

        this.updateOrderFrameOrderItems();
        this.updateOrderFrameBill();
    }

    public void removeItemFromPendingOrder(OrderItem itemToRemove) throws NoOrderException {
        if (pendingOrder == null) throw new NoOrderException("No pending order found");
        if (itemToRemove == null) throw new IllegalArgumentException("Removing null item from order");

        List<OrderItem> itemsInOrder = pendingOrder.getOrderItems();
        if (itemsInOrder.size() < 1) throw new IllegalStateException("Pending order doesn't have any items");

        this.pendingOrder.removeOrderItem(itemToRemove.getItem());

        this.updateOrderFrameOrderItems();
        this.updateOrderFrameBill();
    }

    public void cancelPendingOrder() {
        pendingOrder = null;
        this.orderFrame.setOrderItems(new OrderItem[]{});
        this.orderFrame.setOrderTotals((float) 0.0, (float) 0.0, (float) 0.0);
    }

    public void finalizePendingOrder() throws NoOrderException, EmptyOrderException {
        if (this.pendingOrder == null) throw new NoOrderException("There is no pending order to submit");
        if (this.pendingOrder.getOrderItems().size() < 1) throw new EmptyOrderException("Cannot submit empty order");

        orderFrame.setOrderItems(new OrderItem[]{});
        orderFrame.setOrderTotals((float) 0.0, (float) 0.0, (float) 0.0);
        orderFrame.setBillString(pendingOrder.getBill().getBillString());
        orders.add(pendingOrder);
        pendingOrder = null;
    }

    private String createReport() {
        // all items in menu ✓
        // number of times each item sold ✓
        // income for all orders ✓
        double sumTotal = 0;
        double sumSubtotal = 0;
        Map<Item, Integer> itemSoldQuantities = new HashMap<>();
        StringBuilder builder = new StringBuilder(this.stockItems.size() * 20);

        for (Order order : this.orders) {
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
        String leftHeader = "------------ Item ------------";
        String separator = " | ";
        String rightHeader = "------ Quantities Sold ------";
        builder.append(leftHeader).append(separator).append(rightHeader).append("\n");

        this.stockItems.forEach((itemId, item) -> {
            Integer itemSoldQuantity = itemSoldQuantities.containsKey(item) ? itemSoldQuantities.get(item) : 0;
            builder.append(this.padString(item.getName(), leftHeader.length())).append(separator).append(itemSoldQuantity.toString()).append("\n");
        });
        builder.append("\n\n").append("Total Sales w/o discounts: ").append(sumSubtotal).append("Total Sales with " +
                "discounts: ").append(sumTotal);

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
//            System.out.println(this.createReport());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
