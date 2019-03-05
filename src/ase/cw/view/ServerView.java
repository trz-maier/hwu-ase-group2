package ase.cw.view;

import ase.cw.model.Order;

public interface ServerView {

    /**
     * Called when the information regarding a specific waiter/server has changed. Eg the server has moved on to
     * processing a different order.
     * @param information
     */
    void setServerInfo(String information);
}

