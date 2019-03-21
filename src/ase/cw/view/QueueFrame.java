package ase.cw.view;

import ase.cw.control.OrderController;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.log.Log;
import ase.cw.model.Order;
import ase.cw.view.QueueFrameView;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

/**
 * Created by Bartosz on 03.03.2019.
 */

public class QueueFrame extends JFrame implements ActionListener, ChangeListener, QueueFrameView {

    private JScrollPane queueScroll = new JScrollPane();
    private JList<Order> queueJList = new JList<>();
    private JScrollPane priorityQueueScroll = new JScrollPane();
    private JList<Order> priorityQueueJList = new JList<>();
    private JButton startButton = new JButton("Start");
    private JButton pauseButton = new JButton("Pause");
    private JButton addServerButton = new JButton("Add");
    private JButton removeServerButton = new JButton("Remove");
    private JButton addOrderButton = new JButton("Add Order");
    private JButton addOrderPriorityButton = new JButton("Add Priority Order");
    private JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 19, 10);
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
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Log.getLogger().log("GUI: "+this.getName()+" opened.");
    }

    @Override
    public void setOrdersInQueue(Order[] orders) {
        queueJList.setListData(orders);
        queueScroll.setViewportView(queueJList);
    }

    @Override
    public void setOrdersInPriorityQueue(Order[] orders) {
        this.priorityQueueJList.setListData(orders);
        priorityQueueScroll.setViewportView(this.priorityQueueJList);
    }

    private void buildFrame() {

        startButton.addActionListener(this);
        pauseButton.addActionListener(this);
        addServerButton.addActionListener(this);
        removeServerButton.addActionListener(this);
        addOrderButton.addActionListener(this);
        addOrderPriorityButton.addActionListener(this);
        speedSlider.addChangeListener(this);

        startButton.setEnabled(false);

        Hashtable labelTable = new Hashtable();
        labelTable.put(19, new JLabel("Fast") );
        labelTable.put(1, new JLabel("Slow") );
        speedSlider.setLabelTable(labelTable);
        speedSlider.setPaintLabels(true);


        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel controls = new JPanel(new BorderLayout(5, 5));

        JPanel controls1 = new JPanel(new GridLayout(1, 2, 5, 5));
        controls1.setBorder(BorderFactory.createTitledBorder("Controls"));
        controls1.add(startButton);
        controls1.add(pauseButton);

        JPanel controls2 = new JPanel(new GridLayout(1, 2, 5, 5));
        controls2.setBorder(BorderFactory.createTitledBorder("Servers"));
        controls2.add(addServerButton);
        controls2.add(removeServerButton);

        JPanel controls3 = new JPanel(new GridLayout(1, 1, 5, 5));
        controls3.setBorder(BorderFactory.createTitledBorder("Speed"));
        controls3.add(speedSlider);

        controls.add(controls1, BorderLayout.PAGE_START);
        controls.add(controls2, BorderLayout.CENTER);
        controls.add(controls3, BorderLayout.PAGE_END);

        top.add(controls, BorderLayout.PAGE_START);

        JPanel lists = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel list1 = new JPanel(new BorderLayout(5, 5));
        list1.setBorder(BorderFactory.createTitledBorder("Queue"));
        queueScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        queueScroll.setOpaque(false);
        queueJList.setOpaque(false);
        queueScroll.setViewportView(queueJList);
        list1.add(queueScroll, BorderLayout.CENTER);
        list1.add(addOrderButton, BorderLayout.PAGE_END);

        JPanel list2 = new JPanel(new BorderLayout(5, 5));
        list2.setBorder(BorderFactory.createTitledBorder("Priority Queue"));
        priorityQueueScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        priorityQueueScroll.setOpaque(false);
        priorityQueueJList.setOpaque(false);
        priorityQueueScroll.setViewportView(priorityQueueJList);
        list2.add(priorityQueueScroll, BorderLayout.CENTER);

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
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == speedSlider) {
            if (!speedSlider.getValueIsAdjusting()){
                oc.setProcessingSpeed((20-speedSlider.getValue())/10.0);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == startButton) {
            logButtonPress(startButton);
            oc.startProcessing();
            pauseButton.setEnabled(true);
            startButton.setEnabled(false);
            addServerButton.setEnabled(true);
            removeServerButton.setEnabled(true);
        }

        if (e.getSource() == pauseButton) {
            logButtonPress(startButton);
            oc.pauseProcessing();
            pauseButton.setEnabled(false);
            startButton.setEnabled(true);
            addServerButton.setEnabled(false);
            removeServerButton.setEnabled(false);
        }

        if (e.getSource() == addServerButton) {
            logButtonPress(addServerButton);
            oc.addServer();
        }

        if (e.getSource() == removeServerButton) {
            logButtonPress(removeServerButton);
            oc.removeServer();
        }

        if (e.getSource() == addOrderButton) {
            logButtonPress(addOrderButton);
            try {
                oc.addRandomOrder(false);
            } catch (InvalidCustomerIdException e1) {
                e1.printStackTrace();
            }
        }

        if (e.getSource() == addOrderPriorityButton) {
            logButtonPress(addOrderPriorityButton);
            try {
                oc.addRandomOrder(true);
            } catch (InvalidCustomerIdException e1) {
                e1.printStackTrace();
            }
        }

    }



}
