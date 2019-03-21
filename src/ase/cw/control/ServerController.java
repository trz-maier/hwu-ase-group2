package ase.cw.control;

import ase.cw.interfaces.OrderConsumer;
import ase.cw.interfaces.OrderHandler;
import ase.cw.log.Log;
import ase.cw.model.*;
import ase.cw.utlities.ServerStatusEnum;
import ase.cw.view.ServerFrame;
import ase.cw.view.ServerFrameView;

import javax.swing.*;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Thomas on 17.03.2019.
 */
public class ServerController implements Pausable {

    private static final Log LOGGER = Log.getLogger();
    private final OrderConsumer server;
    private final ServerFrameView serverFrame;
    private final OrderHandler callback;

    public ServerController(int serverID, JFrame parentFrame, BlockingQueue<Order> orders, OrderHandler handler) {
        ServerStatusListener serverStatusListener = new ServerControllerStatusListener();
        OrderHandler orderHandlerListener = new ServerControllerOrderHandler();
        this.serverFrame = new ServerFrame(serverID, parentFrame, this);
        this.server = new Server(orders, orderHandlerListener, serverStatusListener, serverID);
        server.setOrderProcessTime(3000);
        server.setName("Server=" + serverID);
        server.startOrderProcess();
        this.callback = handler;
    }

    public void enableControls(boolean pauseButton, boolean restartButton){
        serverFrame.enableControls(pauseButton, restartButton);
    }

    @Override
    public void pause() {
        server.pauseOrderProcess();
    }

    @Override
    public void unPause() {
        server.restartOrderProcess();
    }

    @Override
    public void stop() {
        //since stopOrderProcess is a blocking method, perform this task in a new thread, otherwise we would block
        // the ui.
        Thread t = new Thread(server::stopOrderProcess);
        t.setName("Server close task");
        t.start();
        serverFrame.enableControls(false, false);
        enableControls(false, false);
    }

    public void setOrderProcessTime(int orderProcessTime) {
        int prevTime = server.getOrderProcessTime();
        if (orderProcessTime < 0) LOGGER.log("Processing time has not been changed as the value must be greater than 0");
        if (orderProcessTime != prevTime) {
            LOGGER.log("Processing time set from "+prevTime+" to "+orderProcessTime);
            server.setOrderProcessTime(orderProcessTime);
        }
    }

    public boolean isStopped() {
        return server.getStatus() == ServerStatusEnum.ServerStatus.STOPPED;
    }


    private class ServerControllerStatusListener implements ServerStatusListener {
        @Override
        public void onServerStatusChange(OrderConsumer server) {
            serverFrame.updateView(server);
        }
    }

    private class ServerControllerOrderHandler implements OrderHandler {
        @Override
        public void orderReceivedByServer(Order currentOrder, OrderConsumer server) {
            //Set a timestamp to a order, as soon as the order is taken by a server
            currentOrder.setTimestamp(new Date());
            LOGGER.log(server.getName() + ": received order=" + currentOrder.toString());

            serverFrame.updateView(server, currentOrder);
            callback.orderReceivedByServer(currentOrder, server);
        }

        @Override
        public void orderFinished(Order currentOrder, OrderConsumer server) {
            LOGGER.log(server.getName() + ": finished order=" + currentOrder.toString());

            serverFrame.updateView(server, currentOrder);
            callback.orderFinished(currentOrder, server);
        }

        @Override
        public void itemFinished(Order currentOrder, OrderItem item, OrderConsumer server) {
            LOGGER.log(server.getName() + ": finished item=" + item.getItem().toString() + " in Order=" + currentOrder.toString());

            serverFrame.updateView(server, currentOrder);
            callback.itemFinished(currentOrder, item, server);
        }

        @Override
        public void itemTaken(Order currentOrder, OrderItem item, OrderConsumer server) {
            LOGGER.log(server.getName() + ": took item=" + item.getItem().toString() + " in Order=" + currentOrder.toString());

            serverFrame.updateView(server, currentOrder);
            callback.itemTaken(currentOrder, item, server);
        }
    }
}
