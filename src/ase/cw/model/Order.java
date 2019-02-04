package ase.cw.model;

import ase.cw.exceptions.InvalidCustomerIdException;

import java.util.Date;
import java.util.List;

/**
 * Created by User on 04.02.2019.
 */
public class Order {
    private String customerId;
    private Date timestamp;
    private List<OrderItem> orderItems;
    private Bill bill;

    public Order(String customerId) throws InvalidCustomerIdException {

    }

    public Order(String customerId, Date timestamp) throws InvalidCustomerIdException {


    }

    public void addOrderItem(Item item) {

    }
}
