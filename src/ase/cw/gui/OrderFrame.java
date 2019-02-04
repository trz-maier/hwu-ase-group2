package ase.cw.gui;

import ase.cw.model.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by User on 04.02.2019.
 */
public class OrderFrame extends JFrame implements ActionListener {

    private JPanel content = new JPanel(new BorderLayout(10,10));

    // Drop-downs
    private JComboBox<String> dropdown_list;
    private JList order_items = new JList();

    // Buttons
    private JButton start_order = new JButton("Start Order");
    private JButton add_item = new JButton("Add");
    private JButton remove_item = new JButton("Remove");
    private JButton submit_order = new JButton("Submit");
    private JButton cancel_order = new JButton("Cancel");
    private JButton exit_button = new JButton("Exit");

    // Labels
    private JLabel enter_customer_id = new JLabel("Enter Customer ID:");
    private JLabel select_items = new JLabel("Select item from product list:");
    private JLabel discount_label = new JLabel("Discount");
    private JLabel total_label = new JLabel("Total");

    // Text output areas
    private JTextArea subtotal = new JTextArea("£ 0.00");
    private JTextArea discount   = new JTextArea("£ 0.00");
    private JTextArea total = new JTextArea("£ 0.00");

    // Text input areas
    private JTextField customer_id_input   = new JTextField(1);

    // Display frame


    public void setStockItems(Item[] items) {

    }

    public OrderFrame() {

        this.setTitle("Register");
        this.setLayout(new GridLayout(1, 2));
        this.setPreferredSize(new Dimension(600,600));
        this.setResizable(false);
        this.BuildFrame();
        this.pack();
        this.setVisible(true);
    }

    private void BuildFrame() {
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        content.setBackground(Color.white);

        JPanel top = new JPanel(new GridLayout(6,2, 5, 5));
        top.setPreferredSize(new Dimension(600,300));
        top.setBackground(Color.white);
    }


    public class KeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                start_order.doClick();
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
