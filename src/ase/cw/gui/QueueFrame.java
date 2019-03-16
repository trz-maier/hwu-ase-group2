package ase.cw.gui;

import ase.cw.control.OrderController;
import ase.cw.log.Log;
import ase.cw.model.Order;
import ase.cw.view.QueueFrameView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Bartosz on 03.03.2019.
 */

public class QueueFrame extends JFrame implements ActionListener, QueueFrameView {

    private JScrollPane queueScroll = new JScrollPane();
    private JList<Order> queueJList = new JList<>();
    private JScrollPane priorityQueueScroll = new JScrollPane();
    private JList<Order> priorityQueueJList = new JList<>();
    private JButton startButton = new JButton("Start");
    private JButton pauseButton = new JButton("Pause");
    private JButton addOrderButton = new JButton("Add Order");
    private JButton addOrderPriorityButton = new JButton("Add Priority Order");
    private JSlider speedSlider = new JSlider();
    private OrderController oc;

    // Frame constructor
    public QueueFrame(OrderController oc) {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setName("QueueFrame");
        this.setTitle("Café Queue");
        this.setPreferredSize(new Dimension(300, 800));
        this.setResizable(true);
        this.buildFrame();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.oc = oc;
        Log.getLogger().log("GUI: "+this.getName()+" opened.");
    }

    private void buildFrame() {
        startButton.addActionListener(this);
        pauseButton.addActionListener(this);
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel controls = new JPanel(new GridLayout(4, 1, 5, 5));
        controls.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel controlsInner = new JPanel(new GridLayout(1, 2, 5, 5));
        controlsInner.add(startButton);
        controlsInner.add(pauseButton);
        controls.add(controlsInner);
        //controls.add(speedSlider);
        controls.setBorder(BorderFactory.createTitledBorder("Controls"));
        top.add(controls, BorderLayout.PAGE_START);

        JPanel lists = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel list1 = new JPanel(new BorderLayout(5, 5));
        list1.setBorder(BorderFactory.createTitledBorder("Queue"));
        queueScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        queueScroll.setOpaque(false);
        queueJList.setOpaque(false);
        queueScroll.setViewportView(queueJList);
        list1.add(queueScroll, BorderLayout.CENTER);
        addOrderButton.addActionListener(this);
        list1.add(addOrderButton, BorderLayout.PAGE_END);

        JPanel list2 = new JPanel(new BorderLayout(5, 5));
        list2.setBorder(BorderFactory.createTitledBorder("Priority Queue"));
        priorityQueueScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        priorityQueueScroll.setOpaque(false);
        priorityQueueJList.setOpaque(false);
        priorityQueueScroll.setViewportView(priorityQueueJList);
        list2.add(priorityQueueScroll, BorderLayout.CENTER);
        addOrderPriorityButton.addActionListener(this);
        list2.add(addOrderPriorityButton, BorderLayout.PAGE_END);

        lists.add(list1);
        lists.add(list2);

        top.add(lists, BorderLayout.CENTER);

        this.add(top);
    }

    private void logButtonPress(JButton button) {
        Log.getLogger().log("GUI: "+this.getName()+" "+button.getText()+" button pressed.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == startButton) {
            logButtonPress(startButton);
            oc.startProcessing();
            //TODO: this currently has no effect
        }

        if (e.getSource() == pauseButton) {
            logButtonPress(startButton);
            oc.pauseProcessing();
            //TODO: this currently has no effect
        }

        if (e.getSource() == addOrderButton) {
            logButtonPress(addOrderButton);
            //TODO: this currently has no effect
        }

        if (e.getSource() == addOrderPriorityButton) {
            logButtonPress(addOrderPriorityButton);
            //TODO: this currently has no effect
        }

    }

    @Override
    public void setOrdersInQueue(Order[] orders) {
        this.queueJList.setListData(orders);
        queueScroll.setViewportView(this.queueJList);
    }

    @Override
    public void setOrdersInPriorityQueue(Order[] orders) {
        this.priorityQueueJList.setListData(orders);
        priorityQueueScroll.setViewportView(this.priorityQueueJList);
    }
}
