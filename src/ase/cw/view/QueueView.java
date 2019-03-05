package ase.cw.view;

import ase.cw.model.Order;

public interface QueueView {

    /**
     * Called when the pendings Orders/Customers in the waiting queue have been changed.
     * @param orders
     */
    void setOrdersInQueue(Order[] orders);

}

