package ase.cw.model;

import ase.cw.exceptions.InvalidCustomerIdException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 04.02.2019.
 */
public class Order {
    private String customerId;
    private Date timestamp;
    private List<OrderItem> orderItems = new ArrayList<>();
    private Bill bill;

    public Order(String customerId) throws InvalidCustomerIdException {
        this.customerId=customerId;
    }

    public Order(String customerId, Date timestamp) throws InvalidCustomerIdException {
        this.customerId=customerId;
        this.timestamp=timestamp;

    }

    public void addOrderItem(Item item) {
        OrderItem orderItem = new OrderItem(item);
        orderItem.setCustomerId(customerId);
        orderItems.add(orderItem);
    }

    public String getCustomerId() {
        return customerId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Bill getBill() {
        return bill;
    }
}
