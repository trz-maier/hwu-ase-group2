package ase.cw.view;

import ase.cw.model.Order;
import ase.cw.model.OrderConsumer;

public interface ServerView {

    /**
     * Called when the information regarding a specific waiter/server has changed. Eg the server has moved on to
     * processing a different order.
     * @param information
     */
    void setServerInfo(String information);

    void updateView(OrderConsumer server, Order order);
}

