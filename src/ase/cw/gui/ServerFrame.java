package ase.cw.gui;

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
    private Server server;

    // Frame constructor
    public ServerFrame(Server server, JFrame parentFrame) {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle(server.getName());
        this.setName("QueueFrame "+this.getTitle());
        this.setPreferredSize(new Dimension(300, 200));
        this.setLocation(parentFrame.getX()+parentFrame.getWidth()+(server.getId()*10), parentFrame.getY()+(server.getId()*10));
        this.setResizable(false);
        this.textArea.setEditable(false);
        this.buildFrame();
        this.pack();
        this.setVisible(true);
        this.server = server;

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
        breakButton.addActionListener(this);
        returnButton.addActionListener(this);
        returnButton.setEnabled(false);
        bottom.add(breakButton);
        bottom.add(returnButton);
        top.add(bottom, BorderLayout.PAGE_END);

        this.add(top);
    }

    private void logButtonPress(JButton button) {
        Log.getLogger().log("GUI: "+this.getName()+" "+button.getText()+" button pressed.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == breakButton) {
            logButtonPress(breakButton);
            breakButton.setEnabled(false);
            returnButton.setEnabled(true);
            server.pauseOrderProcess();
        }

        if (e.getSource() == returnButton) {
            logButtonPress(returnButton);
            returnButton.setEnabled(false);
            breakButton.setEnabled(true);
            server.restartOrderProcess();
        }
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

    private class exitButtonPress extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            Log.getLogger().log("GUI: ServerFrame "+server.getId()+" window close button pressed.");
            //TODO: stop server thread and dispose of server frame after current order is processed
        }
    }
}
