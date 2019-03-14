package ase.cw.gui;

import ase.cw.control.OrderController;
import ase.cw.log.Log;
import ase.cw.model.Order;
import ase.cw.model.OrderConsumer;
import ase.cw.model.Server;
import ase.cw.view.ServerFrameView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Bartosz on 03.03.2019.
 */

public class ServerFrame extends JFrame implements ActionListener, ServerFrameView {

    private JPanel content = new JPanel(new BorderLayout(10, 10));
    private JTextArea textArea = new JTextArea("");
    private JButton breakButton = new JButton("On-Break");
    private JButton returnButton = new JButton("Restart");
    private OrderController controller;
    private int serverId;
    //private Server server;

    // Frame constructor
    public ServerFrame(int id, JFrame parentFrame, OrderController controller) {

        this.serverId = id;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle("Server "+serverId);
        this.setName("QueueFrame "+this.getTitle());
        this.setPreferredSize(new Dimension(300, 200));
        this.setLocation(parentFrame.getX()+parentFrame.getWidth()+(this.serverId*10), parentFrame.getY()+(this.serverId*10));
        this.setResizable(false);
        this.textArea.setEditable(false);
        this.buildFrame();
        this.pack();
        this.setVisible(true);
        this.controller = controller;
        //this.server = server;

        this.addWindowListener(new exitButtonPress());

        Log.getLogger().log("GUI: "+this.getName()+" opened.");
    }

    private void buildFrame() {
        //content.setBackground(Color.white);
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        textArea.setOpaque(false);
        top.add(textArea, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 5, 5));
        returnButton.setEnabled(false);
        breakButton.addActionListener(this);
        returnButton.addActionListener(this);
        bottom.add(breakButton);
        bottom.add(returnButton);
        top.add(bottom, BorderLayout.PAGE_END);

        this.add(top);
    }

    private void logButtonPress(JButton button) {
        Log.getLogger().log("GUI: "+this.getName()+" "+button.getText()+" button pressed.");
    }

    @Override
    public int getServerId() {
        return this.serverId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == breakButton) {
            logButtonPress(breakButton);
            breakButton.setEnabled(false);
            returnButton.setEnabled(true);
            controller.pauseOrderProcess(this.serverId);
        }

        if (e.getSource() == returnButton) {
            logButtonPress(returnButton);
            returnButton.setEnabled(false);
            breakButton.setEnabled(true);
            controller.restartOrderProcess(this.serverId);
        }
    }

    @Override
    public void updateView(OrderConsumer server, Order order) {
        this.textArea.setText(
                "Status: "+(server.getStatus()
                +"\nOrder: "+order.getCustomerId()
                +"\nItems: "+order.getOrderItems().size()
                +"\nSubtotal: £"+order.getBill().getSubtotal()
                +"\nTotal: £"+order.getBill().getTotal()
        ));
    }

    private class exitButtonPress extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            Log.getLogger().log("GUI: ServerFrame "+serverId+" window close button pressed.");
            //TODO: stop server thread and dispose of server frame after current order is processed
        }
    }
}
