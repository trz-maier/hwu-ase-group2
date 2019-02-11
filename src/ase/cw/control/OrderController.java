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

    public static void main(String[] args) {
        // Loading stock items
        try {
            stockItems = FileReader.parseItems("somefilename.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loading past orders
        try {
            orders = FileReader.parseOrders("filename.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidCustomerIdException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Starting up GUI
        OrderFrame of = new OrderFrame();

    }

    public Bill createNewOrder(String customerId) throws InvalidCustomerIdException, IllegalStateException {
        int idLength = customerId.length();
        if (idLength != EXPECTED_CUSTOMER_ID_LENGTH) {
            throw new InvalidCustomerIdException(String.format("Customer id is expected to have length %o, found %o",
                    EXPECTED_CUSTOMER_ID_LENGTH, idLength));
        }

        if (pendingOrder != null) throw new IllegalStateException("New order added while pending order exists");

        pendingOrder = new Order(customerId);
        return pendingOrder.getBill();
    }

    public OrderItem[] addItemToPendingOrder(Item itemToAdd) throws NoOrderException {
        if (pendingOrder == null) throw new NoOrderException("No pending order found");
        pendingOrder.addOrderItem(itemToAdd);
        List<OrderItem> itemsInOrder = pendingOrder.getOrderItems();
        return itemsInOrder.toArray(new OrderItem[itemsInOrder.size()]);
    }

    public OrderItem[] removeItemfromPendingOrder(Item itemToRemove) throws NoOrderException {
        if (pendingOrder == null) throw new NoOrderException("No pending order found");
        List<OrderItem> itemsInOrder = pendingOrder.getOrderItems();

        for(OrderItem item : itemsInOrder) {
            if (item.getItem().getId() == itemToRemove.getId()) {
                itemsInOrder.remove(item);
                break;
            }
        }

        return itemsInOrder.toArray(new OrderItem[itemsInOrder.size()]);
    }

    public void cancelPendingOrder() {
        pendingOrder = null;
    }

    public void finalizePendingOrder() throws NoOrderException, EmptyOrderException, InvalidCustomerIdException {
        orders.add(pendingOrder);
        pendingOrder = null;
    }

    public void generateReport() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
