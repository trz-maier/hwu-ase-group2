package ase.cw.model;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Thomas on 08.03.2019.
 */
public class ApplicationClose implements Runnable {

    private final OrdersDoneEvent ordersDoneEvent;
    private final Collection<Thread> factoryThreads;
    private final Collection<Server> servers;
    private final BlockingQueue<Server> queue;

    public ApplicationClose(OrdersDoneEvent ordersDoneEvent, Collection<Thread> factoryThread, Collection<Server> servers, BlockingQueue<Server> queue) {
        this.ordersDoneEvent = ordersDoneEvent;
        this.factoryThreads = factoryThread;
        this.servers = servers;
        this.queue = queue;
    }

    @Override
    public void run() {

        //Wait until all orderFactories are done
        for(Thread t : factoryThreads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Wait until the orderQueue is empty
        synchronized (queue){
            while(!queue.isEmpty()){
                try {
                    queue.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //Stop all servers
        for(Server server : servers) {
            server.stopOrderProcess();
        }
        //All servers stopped
        ordersDoneEvent.allServersDone();
    }
}
