package ase.cw.view;

import ase.cw.model.Order;

public interface SimulationView {

    /**
     * Called when the pendings Orders/Customers in the waiting queue have been changed.
     * @param orders
     */
    void setOrdersInQueue(Order[] orders);

    /**
     * Passes IDs for server/waiter instances to the GUI. These IDs can be used to retrieve info from the controller
     * regarding each waiter, as well as to deduce their total count.
     *
     * @param serverIDs simple String instance representation of the unique server ID.
     */
    void setServers(String[] serverIDs);

    /**
     * Called when the information regarding a specific waiter/server has changed. Eg the server has moved on to
     * processing a different order.
     * @param information
     */
    void setServerInfo(String information);
}
