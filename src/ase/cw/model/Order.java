/**
 *
 */
package ase.cw.model;

import ase.cw.control.OrderController;
import ase.cw.exceptions.InvalidCustomerIdException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ram
 */
public class Order implements Comparable<Order> {

    private String customerId;
    private Date timestamp;
    private List<OrderItem> orderItems;
    private Bill bill;
    private boolean isPriorityOrder;
    private long creationOrder = 0L;


    public Order(String customerId) throws InvalidCustomerIdException {
        this(customerId, false);
    }

    public Order(String customerId, boolean isPriorityOrder) throws InvalidCustomerIdException {
        OrderController.validateCustomerId(customerId);
        this.customerId = customerId;
        this.orderItems = new ArrayList<>();
        this.isPriorityOrder = isPriorityOrder;
    }

    public void addOrderItem(Item item) {
        this.orderItems.add(new OrderItem(item));
    }

    public void removeOrderItem(Item item) {
        for (OrderItem oitem : orderItems) {
            if (oitem.getItem() == item) {
                this.orderItems.remove(oitem);
                break;
            }
        }
    }
    public void setCreationOrder(Long n) {
        this.creationOrder = n;
    }

    /**
     * @return the customerId
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the orderItems
     */
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    /**
     * @return the bill
     */
    public Bill getBill() {
        return new Bill(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (customerId != null ? !customerId.equals(order.customerId) : order.customerId != null) return false;
        return timestamp != null ? timestamp.equals(order.timestamp) : order.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = customerId != null ? customerId.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s] Customer: %s, Items: %o", creationOrder, customerId, orderItems.size());
    }

    public boolean hasPriority() {
        return this.isPriorityOrder;
    }

    @Override
    public int compareTo(Order other) {
        if (this.hasPriority() && !other.hasPriority()){
            return -1;
        } else if (!this.hasPriority() && other.hasPriority()) {
            return 1;
        }
        else if (this.creationOrder < other.creationOrder) { //
            return -1;
        }
        else {
            return 1;
        }
    }
}
