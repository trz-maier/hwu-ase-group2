package ase.cw.control;

import ase.cw.exceptions.EmptyOrderException;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.exceptions.NoOrderException;
import ase.cw.gui.OrderFrame;
import ase.cw.model.Bill;
import ase.cw.model.Item;
import ase.cw.model.Order;
import ase.cw.model.OrderItem;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by User on 04.02.2019.
 */
public class OrderController {
    private TreeMap<String, Item> stockItems;
    private List<Order> orders;
    private Order pendingOrder;

    public Bill createNewBill(String customerId) throws InvalidCustomerIdException {
        return null;
    }

    public OrderItem[] addItemToPendingOrder() throws NoOrderException {
        return null;

    }

    public OrderItem[] removeItemfromPendingOrder() throws NoOrderException {
        return null;
    }

    public void cancelPendingOrder() {

    }

    public void finalizePendingOrder() throws NoOrderException, EmptyOrderException, InvalidCustomerIdException {

    }

    public void generateReport() {

    }

    public static void main(String[] args) {

        new OrderFrame();

    }
}
