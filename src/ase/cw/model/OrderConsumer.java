package ase.cw.model;

/**
 * Created by Thomas on 01.03.2019.
 */
public interface OrderConsumer {

    void setName(String name);

    String getName();
    String getStatus();
    int getId();

    OrderHandler getOrderHandler();

    /**
     * @param processTime the time how long it takes to proceed each item
     */
    void setOrderProcessTime(int processTime);

    void setStatus(String status);


    int getOrderProcessTime();

    /**
     * Start a order Process
     */
    void startOrderProcess();

    /**
     * Stops the OrderConsumer and all attached threads
     */
    void stopOrderProcess();

}
