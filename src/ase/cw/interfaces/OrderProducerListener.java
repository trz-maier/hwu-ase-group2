package ase.cw.interfaces;

import ase.cw.model.Order;

import java.util.concurrent.BlockingQueue;

public interface OrderProducerListener {

    void onOrderProduced(Order producedOrder);

}
