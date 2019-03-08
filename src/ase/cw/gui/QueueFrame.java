package ase.cw.gui;

import ase.cw.control.OrderController;
import ase.cw.model.Order;
import ase.cw.view.QueueView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Bartosz on 03.03.2019.
 */

public class QueueFrame extends JFrame implements ActionListener, QueueView {

    private OrderController orderController;
    private JPanel content = new JPanel(new BorderLayout(10, 10));
    private JScrollPane queueScroll = new JScrollPane();
    private JList<Order> queueJList = new JList<>();
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");

    // Frame constructor
    public QueueFrame() {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle("Café OrderQueue");
        this.setPreferredSize(new Dimension(300, 700));
        this.setResizable(false);
        this.buildFrame();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        System.out.println("GUI: QueueFrame opened.");
    }

    //Setters
    public void setOrderController(OrderController orderController) {
        this.orderController = orderController;
    }

    private void buildFrame() {
        content.setBackground(Color.white);
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel left_top = new JPanel(new GridLayout(1, 2, 5, 5));
        left_top.add(startButton);
        left_top.add(stopButton);
        top.add(left_top, BorderLayout.PAGE_START);
        queueScroll.setViewportView(queueJList);
        top.add(queueScroll, BorderLayout.CENTER);
        this.add(top);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == startButton) {
            System.out.println("GUI: Start button pressed.");
        }

        if (e.getSource() == stopButton) {
            System.out.println("GUI: Stop button pressed.");
        }

    }

    @Override
    public void setOrdersInQueue(Order[] orders) {
        this.queueJList.setListData(orders);
        queueScroll.setViewportView(this.queueJList);
    }
}
