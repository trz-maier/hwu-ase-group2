package ase.cw.gui;

import ase.cw.log.Log;
import ase.cw.model.Order;
import ase.cw.model.OrderConsumer;
import ase.cw.model.Server;
import ase.cw.view.ServerView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Bartosz on 03.03.2019.
 */

public class ServerFrame extends JFrame implements ActionListener, ServerView {

    private JPanel content = new JPanel(new BorderLayout(10, 10));
    private JTextArea textArea = new JTextArea("");

    // Frame constructor
    public ServerFrame(Server server, JFrame parentFrame) {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle(server.getName());
        this.setPreferredSize(new Dimension(300, 200));
        this.setLocation(parentFrame.getX()+parentFrame.getWidth()+(server.getId()*10), parentFrame.getY()+(server.getId()*10));
        this.setResizable(false);
        this.textArea.setEditable(false);
        this.buildFrame();
        this.pack();
        this.setVisible(true);

        Log.getLogger().log("GUI: ServerFrame "+server.getId()+" opened.");
    }
    private void buildFrame() {
        content.setBackground(Color.white);
        JPanel top = new JPanel(new BorderLayout(5, 5));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        top.add(textArea, BorderLayout.CENTER);
        this.add(top);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void setServerInfo(String information) {
        textArea.setText(information);
    }

    @Override
    public void updateView(OrderConsumer server, Order order) {
        if (!server.isBusy()) {
            this.textArea.setText("Busy: "+server.isBusy());
        }
        else {
            this.textArea.setText("Busy: "+server.isBusy()+"\nProcessing: "+order.getCustomerId()+"\nItems: "+order.getOrderItems().size()+"\nBill: £"+order.getBill().getTotal());
        }
    }
}
