package ase.cw.model;

import ase.cw.interfaces.OrderConsumer;

public interface ServerStatusListener {

    void onServerStatusChange(OrderConsumer server);

}
