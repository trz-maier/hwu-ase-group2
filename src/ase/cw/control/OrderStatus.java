package ase.cw.control;

import ase.cw.model.Order;
import ase.cw.model.OrderItem;

/**
 * Created by Thomas on 01.03.2019.
 */
public interface OrderStatus {
    /**
     * Called as soon as a order is by an OrderConsumer.
     * Thias means the server took the order and will proceed it.
     * @param currentOrder the order which is started
     * @param server ther Consumer which started the order
     */
    void orderTaken(Order currentOrder, OrderConsumer server);

    /**
     * Called as soon as a order is finished by an OrderConsumer.
     * @param currentOrder
     * @param server
     */
    void orderFinished(Order currentOrder, OrderConsumer server);

    /**
     *  Called when a item of a order is finished by an OrderConsumer.
     * @param currentOrder
     * @param item
     * @param server
     */
    void itemFinished(Order currentOrder, OrderItem item, OrderConsumer server);

    /**
     *  Called when a item of a order taken in the OrderConsumer
     * @param currentOrder the current order
     * @param item the current item of the order
     * @param server the consumer, who took the item
     */
    void itemTaken(Order currentOrder, OrderItem item, OrderConsumer server);
}
