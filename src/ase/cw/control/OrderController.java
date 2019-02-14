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
    private static TreeMap<String, Item> stockItems;
    private static List<Order> orders;
    private static Order pendingOrder;

    private OrderFrame orderFrame;
    private OrderController() {
        try {
            this.orders = FileReader.parseOrders("orders.csv");
            this.stockItems = FileReader.parseItems("items.csv");
            orderFrame = new OrderFrame();
            orderFrame.setOrderController(this);
            orderFrame.setStockItems(this.stockItems.values().toArray(new Item[stockItems.size()]));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
        }

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
        orderFrame.setOrderItems(new OrderItem[] {});
        orderFrame.setOrderTotals((float) 0.0, (float) 0.0, (float) 0.0);
        pendingOrder = new Order(customerId);
    }

    public void addItemToPendingOrder(Item itemToAdd) throws NoOrderException {
        if (pendingOrder == null) throw new NoOrderException("No pending order found");
        pendingOrder.addOrderItem(itemToAdd);
        orderFrame.setOrderItems((pendingOrder.getOrderItems().toArray(new OrderItem[stockItems.size()])));
        //orderFrame.setOrderTotals();
    }

    public void removeItemFromPendingOrder(OrderItem itemToRemove) throws NoOrderException {
        if (pendingOrder == null) throw new NoOrderException("No pending order found");
        List<OrderItem> itemsInOrder = pendingOrder.getOrderItems();
        for(OrderItem item : itemsInOrder) {
            if (item == itemToRemove) {
                itemsInOrder.remove(item);
                break;
            }
        }
        orderFrame.setOrderItems((pendingOrder.getOrderItems().toArray(new OrderItem[stockItems.size()])));
        //orderFrame.setOrderTotals();
    }

    public void cancelPendingOrder() {
        orderFrame.setOrderItems(new OrderItem[] {});
        orderFrame.setOrderTotals((float) 0.0, (float) 0.0, (float) 0.0);
        //orderFrame.setOrderTotals();
        pendingOrder = null;
    }

    public void finalizePendingOrder() throws NoOrderException, EmptyOrderException, InvalidCustomerIdException {
        orderFrame.setOrderItems(new OrderItem[] {});
        orderFrame.setOrderTotals((float) 0.0, (float) 0.0, (float) 0.0);
        //orderFrame.setOrderTotals();
        orders.add(pendingOrder);
        pendingOrder = null;
    }

    public void generateReport() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
