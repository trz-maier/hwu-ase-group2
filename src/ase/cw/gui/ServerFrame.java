package ase.cw.gui;

import ase.cw.view.ServerView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Bartosz on 03.03.2019.
 */

public class ServerFrame extends JFrame implements ActionListener, ServerView {

    private JPanel content = new JPanel(new BorderLayout(10, 10));
    private JTextArea textArea = new JTextArea("Status: free");

    // Frame constructor
    public ServerFrame(int server_id) {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle("Server " + server_id);
        this.setPreferredSize(new Dimension(300, 200));
        this.setResizable(false);
        this.buildFrame();
        this.pack();
        this.setVisible(true);

        System.out.println("GUI: ServerFrame "+server_id+" opened.");
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
}
