package ase.cw.view;

import ase.cw.interfaces.OrderConsumer;
import ase.cw.model.Pausable;
import ase.cw.log.Log;
import ase.cw.model.Order;
import ase.cw.utlities.ServerStatusEnum;
import ase.cw.view.ServerFrameView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Bartosz on 03.03.2019.
 */

public class ServerFrame extends JFrame implements ActionListener, ServerFrameView {

    private JTextArea textArea = new JTextArea("");
    private JTextArea statusArea = new JTextArea("");
    private JButton breakButton = new JButton("On-Break");
    private JButton restartButton = new JButton("Restart");
    private Pausable pausable;
    private int serverId;

    // Frame constructor
    public ServerFrame(int id, JFrame parentFrame, Pausable pausable) {
        this.serverId = id;
        this.textArea.setEditable(false);
        this.statusArea.setEditable(false);
        this.pausable = pausable;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle("Server "+serverId);
        this.setName("QueueFrame "+this.getTitle());
        this.setPreferredSize(new Dimension(300, 200));
        this.setLocation(parentFrame.getX()+parentFrame.getWidth()+(id*10), parentFrame.getY()+(id*10));
        this.setResizable(false);
        this.buildFrame();
        this.pack();
        this.setVisible(true);
        this.addWindowListener(new exitButtonPress());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Log.getLogger().log("GUI: "+this.getName()+" opened.");
    }

    private void buildFrame() {
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        statusArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        textArea.setOpaque(false);
        statusArea.setOpaque(false);
        top.add(textArea, BorderLayout.CENTER);
        top.add(statusArea,BorderLayout.NORTH);
        JPanel bottom = new JPanel(new GridLayout(1, 2, 5, 5));
        restartButton.setEnabled(false);
        breakButton.addActionListener(this);
        restartButton.addActionListener(this);
        bottom.add(breakButton);
        bottom.add(restartButton);
        top.add(bottom, BorderLayout.PAGE_END);

        this.add(top);
    }

    private void logButtonPress(JButton button) {
        Log.getLogger().log("GUI: "+this.getName()+" "+button.getText()+" button pressed.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == breakButton) {
            if(pausable!=null) {
                logButtonPress(breakButton);
                breakButton.setEnabled(false);
                restartButton.setEnabled(true);
                pausable.pause();
            }

        }

        if (e.getSource() == restartButton) {
            if(pausable!=null){
                logButtonPress(restartButton);
                restartButton.setEnabled(false);
                breakButton.setEnabled(true);
                pausable.unPause();
            }
        }
    }

    @Override
    public void updateView(OrderConsumer server, Order order) {

        this.statusArea.setText("Status: " + (server.getStatus()));
        if(server.getStatus()== ServerStatusEnum.ServerStatus.STOPPED){
            this.dispose();
        } else if (server.getStatus() == ServerStatusEnum.ServerStatus.BUSY && order != null) {
            this.textArea.setText(
                             "Order: " + order.getCustomerId()
                            + "\nItems: " + order.getOrderItems().size()
                            + "\nSubtotal: £" + order.getBill().getSubtotal()
                            + "\nTotal: £" + order.getBill().getTotal()
                    );
        } else if(server.getStatus()== ServerStatusEnum.ServerStatus.FREE || server.getStatus()== ServerStatusEnum.ServerStatus.PAUSED){
            this.textArea.setText("");
        } else {

        }
    }

    @Override
    public void updateView(OrderConsumer server) {
        updateView(server,null);
    }

    @Override
    public void enableControls(boolean pauseButton, boolean returnButton) {
        breakButton.setEnabled(pauseButton);
        restartButton.setEnabled(returnButton);
    }

    private class exitButtonPress extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            Log.getLogger().log("GUI: ServerFrame "+serverId+" window close button pressed.");
            pausable.stop();

        }
    }
}
