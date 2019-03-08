package ase.cw.model;
import java.util.concurrent.BlockingQueue;

public interface OrderProducerListener {

    void onOrderProduced(BlockingQueue order, Order producedOrder);

}
