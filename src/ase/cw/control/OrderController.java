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
    private static final int EXPECTED_CUSTOMER_ID_LENGTH = 8;
    private static TreeMap<String, Item> stockItems;
    private static List<Order> orders;
    private static Order pendingOrder;

    private OrderFrame orderFrame;
    public OrderController() {
        orderFrame = new OrderFrame();
        orderFrame.setOrderController(this);
    }

    public static void main(String[] args) {
        new OrderController();
    }



    public Bill createNewOrder(String customerId) throws InvalidCustomerIdException, IllegalStateException {
        int idLength = customerId.length();
        if (idLength != EXPECTED_CUSTOMER_ID_LENGTH) {
            throw new InvalidCustomerIdException(String.format("Customer id is expected to have length %o, found %o",
                    EXPECTED_CUSTOMER_ID_LENGTH, idLength));
        }

        if (pendingOrder != null) throw new IllegalStateException("New order added while pending order exists");
        orderFrame.setOrderTotals((float) 0.0, (float) 0.0, (float) 0.0);
        pendingOrder = new Order(customerId);
        return pendingOrder.getBill();
    }

    public OrderItem[] addItemToPendingOrder(Item itemToAdd) throws NoOrderException {
        if (pendingOrder == null) throw new NoOrderException("No pending order found");
        pendingOrder.addOrderItem(itemToAdd);
        List<OrderItem> itemsInOrder = pendingOrder.getOrderItems();
        //orderFrame.setOrderItems();
        //orderFrame.setOrderTotals();
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
        //of.setOrderItems();
        //of.setOrderTotals();
        return itemsInOrder.toArray(new OrderItem[itemsInOrder.size()]);
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
        //orderFrame.setBillString();
        orders.add(pendingOrder);
        pendingOrder = null;
    }

    public void generateReport() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
