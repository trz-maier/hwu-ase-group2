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

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by User on 04.02.2019.
 */
public class OrderController {
    private static final int EXPECTED_CUSTOMER_ID_LENGTH = 8;
    private TreeMap<String, Item> stockItems;
    private List<Order> orders;
    private Order pendingOrder;

    private OrderFrame orderFrame;

    private OrderController() {
        try {
            this.stockItems = FileReader.parseItems("Items.csv");
            this.orders = FileReader.parseOrders("Orders.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.orderFrame = new OrderFrame();
        this.orderFrame.setOrderController(this);
        this.orderFrame.setStockItems(stockItems.values().toArray(new Item[stockItems.size()]));
    }

    public static void main(String[] args) {
        new OrderController();
    }


    public void createNewOrder(String customerId) throws InvalidCustomerIdException, IllegalStateException {
        int idLength = customerId.length();
        if (idLength != EXPECTED_CUSTOMER_ID_LENGTH) {
            throw new InvalidCustomerIdException(String.format("Customer id is expected to have length %o, found %o",
                    EXPECTED_CUSTOMER_ID_LENGTH, idLength));
        }

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

        for (OrderItem item : itemsInOrder) {
            if (item == itemToRemove) {
                itemsInOrder.remove(item);
                break;
            }
        }

        this.updateOrderFrameOrderItems();
        this.updateOrderFrameBill();
    }

    public void cancelPendingOrder() {
        pendingOrder = null;
        this.orderFrame.setOrderItems(new OrderItem[]{});
        this.orderFrame.setOrderTotals((float) 0.0, (float) 0.0, (float) 0.0);
    }

    public void finalizePendingOrder() throws NoOrderException, EmptyOrderException {
        if (this.pendingOrder == null ) throw new NoOrderException("There is no pending order to submit");
        if (this.pendingOrder.getOrderItems().size() < 1) throw new EmptyOrderException("Cannot submit empty order");

        orderFrame.setOrderItems(new OrderItem[]{});
        orderFrame.setOrderTotals((float) 0.0, (float) 0.0, (float) 0.0);
        orderFrame.setBillString(pendingOrder.getBill().getBillString());
        orders.add(pendingOrder);
        pendingOrder = null;
    }

    public void generateReport() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
