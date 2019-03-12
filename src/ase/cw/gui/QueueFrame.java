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

    private JPanel content = new JPanel(new BorderLayout(10, 10));
    private JScrollPane queueScroll = new JScrollPane();
    private JList<Order> queueJList = new JList<>();
    private JButton startButton = new JButton("Start");
    private JButton pauseButton = new JButton("Pause");
    private OrderController oc;

    // Frame constructor
    public QueueFrame(OrderController oc) {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setName("QueueFrame");
        this.setTitle("Café Queue");
        this.setPreferredSize(new Dimension(300, 700));
        this.setResizable(false);
        this.buildFrame();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.oc = oc;
        Log.getLogger().log("GUI: "+this.getName()+" opened.");
    }

    private void buildFrame() {
        content.setBackground(Color.white);
        startButton.addActionListener(this);
        pauseButton.addActionListener(this);
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel left_top = new JPanel(new GridLayout(1, 2, 5, 5));
        left_top.add(startButton);
        left_top.add(pauseButton);
        top.add(left_top, BorderLayout.PAGE_START);
        queueScroll.setViewportView(queueJList);
        top.add(queueScroll, BorderLayout.CENTER);
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

    }

    @Override
    public void setOrdersInQueue(Order[] orders) {
        this.queueJList.setListData(orders);
        queueScroll.setViewportView(this.queueJList);
    }
}
