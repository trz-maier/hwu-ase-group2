import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.interfaces.OrderConsumer;
import ase.cw.interfaces.OrderHandler;
import ase.cw.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


import static org.junit.Assert.*;
/**
 * Created by User on 07.03.2019.
 */
public class TestServer {


    @Test
    public void TestServerSuccess() throws InterruptedException {
        System.out.println("RUN=TestServerSuccess");
        int serverCount=5;
        int serverFactoryCount=2;
        int orderCount=100;
        int itemsPerOrderCount=4;
        ArrayList<Server> servers = new ArrayList<Server>();
        ArrayList<Thread> serverFactories = new ArrayList<>();
        BlockingDeque<Order> queue = new LinkedBlockingDeque<>();
        //Create factories
        for(int i=0;i<serverFactoryCount;i++){
            Thread thread = new Thread(new OrderFactory(orderCount,itemsPerOrderCount,queue));
            thread.start();
            serverFactories.add(thread);
        }

        //Create Servers
        TestServerSuccessListener listener = new TestServerSuccessListener();
        for(int i=1;i<serverCount+1;i++){
            Server s = new Server(queue,listener,null,i);
            s.setName("S="+i);
            s.setOrderProcessTime(50);
            s.startOrderProcess();
            servers.add(s);
        }
        System.out.println("Wait for factorys...");

        //Wait until all orders are produced
        for(Thread factory : serverFactories){
            factory.join();
        }
        System.out.println("Done");
        System.out.println("Wait for empty queue...");
        //Busy wait until all Orders are done
        while(!queue.isEmpty()){
            Thread.sleep(500);
            System.out.println(queue.size()+"orders remaining");
        }
        System.out.println("Wait that every server finished the last order...");

        for(Server s: servers){
            s.stopOrderProcess();
        }

        System.out.println("Done");

        int expectedOrders = serverFactoryCount*orderCount;
        int expectedItems  = serverFactoryCount*orderCount*itemsPerOrderCount;
        //Test if the expected Order count
        assertEquals(expectedOrders,listener.ordersProceeded);
        assertEquals(expectedOrders,listener.orderstaken);
        //Test if the expected Item count
        assertEquals(expectedItems,listener.itemsProceeded);
        assertEquals(expectedItems,listener.itemsTaken);

    }

    private class OrderFactory implements Runnable {
        private int orderCount=0;
        private int itemsPerOrderCount=0;
        private  BlockingDeque<Order> queue;
        public OrderFactory(int orderCount,int itemsPerOrderCount, BlockingDeque<Order> queue){
            this.orderCount=orderCount;
            this.queue=queue;
            this.itemsPerOrderCount=itemsPerOrderCount;
        }
        @Override
        public void run() {

            for(int i=0;i<orderCount;i++){
                try {
                    Order order = new Order("abc00002");
                    for(int j=0;j<itemsPerOrderCount;j++){
                        order.addOrderItem(new Item(UUID.randomUUID(), Item.Category.FOOD,"testName",1));
                    }
                    queue.add(order);
                } catch (InvalidCustomerIdException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class TestServerSuccessListener implements OrderHandler {
        private int ordersProceeded;
        private int orderstaken;
        private int itemsProceeded;
        private int itemsTaken;

        public TestServerSuccessListener() {
        }

        @Override
        public void orderReceivedByServer(Order currentOrder, OrderConsumer server) {
            synchronized (this){
                orderstaken++;
            }
        }

        @Override
        public void orderFinished(Order currentOrder, OrderConsumer server) {
            synchronized (this){
                ordersProceeded++;
            }
        }

        @Override
        public void itemFinished(Order currentOrder, OrderItem item, OrderConsumer server) {
            synchronized (this) {
                itemsProceeded++;
            }
        }

        @Override
        public void itemTaken(Order currentOrder, OrderItem item, OrderConsumer server) {
            synchronized (this){
                itemsTaken++;
            }
        }
    }
}
