package ase.cw.interfaces;

import ase.cw.utlities.ServerStatusEnum.ServerStatus;


public interface OrderConsumer {

    void setName(String name);

    String getName();
    ServerStatus getStatus();

    OrderHandler getOrderHandler();

    /**
     * @param processTime the time how long it takes to proceed each item
     */
    void setOrderProcessTime(int processTime);

    int getOrderProcessTime();

    /**
     * Start a order Process
     */
    void startOrderProcess();

    /**
     * Pause a order Process
     */
    void pauseOrderProcess();


    /**
     * Restart a order Process
     */
    void restartOrderProcess();

    /**
     * Stops the OrderConsumer and all attached threads
     */
    void stopOrderProcess();

}
