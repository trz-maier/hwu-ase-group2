package ase.cw.view;

import ase.cw.interfaces.OrderConsumer;
import ase.cw.model.Order;

public interface ServerFrameView {
    void updateView(OrderConsumer server);
    void updateView(OrderConsumer server, Order order);
    void enableControls(boolean pauseButton, boolean returnButton);
}

