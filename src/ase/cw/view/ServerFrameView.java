package ase.cw.view;

import ase.cw.model.OrderConsumer;
import ase.cw.model.Order;

public interface ServerFrameView {
    int getServerId();
    void updateView(OrderConsumer server);
    void updateView(OrderConsumer server, Order order);
}

