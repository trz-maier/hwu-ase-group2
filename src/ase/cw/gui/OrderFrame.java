package ase.cw.gui;

import ase.cw.model.Item;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by User on 04.02.2019.
 */
public class OrderFrame extends JFrame implements ActionListener {

    // Top
    private JPanel content                  = new JPanel(new BorderLayout(10,10));

    // Lists
    private JScrollPane menu_items_scroll   = new JScrollPane();
    private JScrollPane order_items_scroll  = new JScrollPane();
    private JList menu_items                = new JList();
    private JList order_items               = new JList();

    // Buttons
    private JButton start_order_button      = new JButton("Start Order");
    private JButton pay_button              = new JButton("Pay");
    private JButton cancel_order_button     = new JButton("Cancel");
    private JButton item_search_button      = new JButton("Search");
    private JButton clear_search_button     = new JButton("Clear");
    private JButton add_item_button         = new JButton("Add");
    private JButton remove_item_button      = new JButton("Remove");
    private JButton exit_button             = new JButton("Exit");

    // Labels
    private JLabel enter_customer_id_label  = new JLabel("Enter Customer ID:");
    private JLabel search_items_label       = new JLabel("Search items by name:");
    private JLabel order_items_label        = new JLabel("Current order:");
    private JLabel select_items_label       = new JLabel("Select item from product list:");
    private JLabel subtotal_label           = new JLabel("Subtotal");
    private JLabel discount_label           = new JLabel("Discount");
    private JLabel total_label              = new JLabel("Total");

    // Text output areas
    private JTextArea subtotal              = new JTextArea("£ 0.00");
    private JTextArea discount              = new JTextArea("£ 0.00");
    private JTextArea total                 = new JTextArea("£ 0.00");

    // Text input areas
    private JTextField customer_id_input    = new JTextField(1);
    private JTextField search_items_input   = new JTextField(1);




    public void setStockItems(Item[] items) {

    }

    public ArrayList<String> getMenu() {

        ArrayList<String> menu = new ArrayList<>();
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");
        menu.add("Cheese Sandwich (£1.90)");
        menu.add("Ham Sandwich (£2.50)");
        menu.add("Orange Juice (£1.00)");
        menu.add("Apple Juice (£1.00)");
        menu.add("Oregano Crisps (£1.20)");

        return menu;
    }

    public ArrayList<String> getMenuSubset(String string) {
        ArrayList<String> menu = this.getMenu();
        ArrayList<String> menu_subset = new ArrayList<>();

        for (Object s : menu) {
            if (s.toString().toLowerCase().contains(string.toLowerCase()))
                menu_subset.add(s.toString());
        }

        return menu_subset;
    }

    // Frame builder

    public OrderFrame() {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("coffee.png")));
        this.setTitle("Café Register");

        this.setPreferredSize(new Dimension(600,900));
        this.setResizable(false);
        this.BuildFrame();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        System.out.println("GUI: Program opened.");
    }

    // Frame structure

    private void BuildFrame() {

        start_order_button.setEnabled(true);
        pay_button.setEnabled(false);
        cancel_order_button.setEnabled(false);
        remove_item_button.setEnabled(false);
        add_item_button.setEnabled(false);
        clear_search_button.setEnabled(false);

        content.setBackground(Color.white);

        JPanel top = new JPanel(new GridLayout(1,2, 5, 5));
        //top.setBackground(Color.white);


        // Left panel
        JPanel left = new JPanel(new BorderLayout(5, 5));
        //left.setBackground(Color.white);
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel left_top = new JPanel(new GridLayout(4,1, 5, 5));
        //left_top.setBackground(Color.white);
        left_top.add(search_items_label);
        search_items_input.addKeyListener(new search_enter_press());
        left_top.add(search_items_input);
        JPanel left_top_nav = new JPanel(new GridLayout(1,2, 5, 5));
        search_items_input.addActionListener(this);

        item_search_button.addActionListener(this);
        left_top_nav.add(item_search_button);
        clear_search_button.addActionListener(this);
        left_top_nav.add(clear_search_button);
        left_top.add(left_top_nav);
        left_top.add(select_items_label);

        JPanel left_bottom = new JPanel(new GridLayout(1,1, 5, 5));
        left_bottom.setBackground(Color.white);
        add_item_button.addActionListener(this);
        left_bottom.add(add_item_button);

        left.add(left_top, BorderLayout.PAGE_START);

        menu_items.setListData(getMenu().toArray());
        menu_items.addMouseListener(new double_click_to_add());
        menu_items.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menu_items_scroll.setViewportView(menu_items);
        left.add(menu_items_scroll, BorderLayout.CENTER);
        left.add(left_bottom, BorderLayout.PAGE_END);

        top.add(left);

        // Right panel
        JPanel right = new JPanel(new BorderLayout(5, 5));
        //right.setBackground(Color.white);
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel right_top = new JPanel(new GridLayout(4,1, 5, 5));
        //right_top.setBackground(Color.white);
        right_top.add(enter_customer_id_label);
        customer_id_input.addActionListener(this);
        customer_id_input.addKeyListener(new start_order_enter_press());
        right_top.add(customer_id_input);
        start_order_button.addActionListener(this);
        right_top.add(start_order_button);
        right_top.add(order_items_label);



        JPanel right_bottom = new JPanel(new GridLayout(9,1, 5, 5));

        remove_item_button.addActionListener(this);
        right_bottom.add(remove_item_button);

        right_bottom.add(subtotal_label);
        subtotal.setOpaque(false);
        right_bottom.add(subtotal);
        right_bottom.add(discount_label);
        discount.setOpaque(false);
        right_bottom.add(discount);
        right_bottom.add(total_label);
        total.setOpaque(false);
        right_bottom.add(total);


        JPanel right_bottom_buttons = new JPanel(new GridLayout(1,2, 5, 5));
        pay_button.addActionListener(this);
        right_bottom_buttons.add(pay_button);
        cancel_order_button.addActionListener(this);
        right_bottom_buttons.add(cancel_order_button);


        right_bottom.add(right_bottom_buttons);
        exit_button.addActionListener(this);
        right_bottom.add(exit_button);

        right.add(right_top, BorderLayout.PAGE_START);
        order_items.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        order_items.addMouseListener(new double_click_to_remove());
        order_items_scroll.setViewportView(order_items);
        right.add(order_items_scroll, BorderLayout.CENTER);
        right.add(right_bottom, BorderLayout.PAGE_END);

        top.add(right);

        this.add(top);

    }


    // Key press and mouse click actions

    public class search_enter_press extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                item_search_button.doClick();
            }
        }
    }

    public class start_order_enter_press extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                start_order_button.doClick();
            }
        }
    }

    public class double_click_to_add extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                add_item_button.doClick();
            }
        }
    }

    public class double_click_to_remove extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                remove_item_button.doClick();
            }
        }
    }

    //TODO: Add window action listener, i.e. dispose of frame and close program upon clicking windows close button

    // Action performed definition

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == item_search_button) {
            System.out.println("GUI: Item search button pressed.");
            clear_search_button.setEnabled(true);

            menu_items.setListData(getMenuSubset(search_items_input.getText()).toArray());

            //TODO: create a search mechanism by createing a subset of menu items that matches search criteria

        }

        if (e.getSource() == clear_search_button) {
            System.out.println("GUI: Clear search button pressed.");
            search_items_input.setText("");
            clear_search_button.setEnabled(false);

            menu_items.setListData(getMenu().toArray());

            //TODO: set menu_items back to the full set of items

        }

        if (e.getSource() == start_order_button) {
            System.out.println("GUI: Start order button pressed.");
            customer_id_input.setEnabled(false);
            start_order_button.setEnabled(false);
            pay_button.setEnabled(true);
            cancel_order_button.setEnabled(true);
            add_item_button.setEnabled(true);

            //TODO: create a new order object

        }

        if (e.getSource() == add_item_button) {
            System.out.println("GUI: Add item button pressed.");
            if (order_items.getModel().getSize() > 0) {
                pay_button.setEnabled(true); remove_item_button.setEnabled(true);}

            //TODO: add selected object to pending order

            System.out.println("ORDER: Item added - "+menu_items.getSelectedValue());
        }

        if (e.getSource() == remove_item_button) {
            System.out.println("GUI: Remove item button pressed.");
            if (order_items.getModel().getSize() == 0) {
                pay_button.setEnabled(false); remove_item_button.setEnabled(false);}

            //TODO: remove selected object from pending order

            System.out.println("ORDER: Item XXX has been removed from current order.");
        }

        if (e.getSource() == pay_button) {
            System.out.println("GUI: Pay button pressed.");
            customer_id_input.setText("");
            customer_id_input.setEnabled(true);
            start_order_button.setEnabled(true);
            pay_button.setEnabled(false);
            cancel_order_button.setEnabled(false);
            remove_item_button.setEnabled(false);
            add_item_button.setEnabled(false);


            JFrame billFrame = new JFrame("Bill");

            billFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("coffee.png")));
            billFrame.setPreferredSize(new Dimension(300,900));
            billFrame.setResizable(false);
            billFrame.setLocation(this.getX()+this.getWidth(), this.getY());
            //TODO: add string representation of the bill
            //newFrame.add(report_output);
            billFrame.pack();
            billFrame.setVisible(true);


            //TODO: add pending order to list of orders

            System.out.println("ORDER: Order has been submitted.");
        }

        if (e.getSource() == cancel_order_button) {
            System.out.println("GUI: Cancel order button pressed.");
            customer_id_input.setText("");
            customer_id_input.setEnabled(true);
            start_order_button.setEnabled(true);
            pay_button.setEnabled(false);
            cancel_order_button.setEnabled(false);
            remove_item_button.setEnabled(false);
            add_item_button.setEnabled(false);

            //TODO: remove order object from memory

            System.out.println("ORDER: Order has been cancelled.");
        }

        if (e.getSource() == exit_button) {
            cancel_order_button.doClick();
            System.out.println("GUI: Program closed.");

            super.dispose();
            System.exit(0);

        }




    }
}


