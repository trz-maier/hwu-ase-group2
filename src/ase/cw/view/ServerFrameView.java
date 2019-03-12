package ase.cw.view;

import ase.cw.model.Order;
import ase.cw.model.OrderConsumer;

public interface ServerFrameView {

    void updateView(OrderConsumer server, Order order);
}

