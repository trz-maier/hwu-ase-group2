package ase.cw.gui;

import ase.cw.control.OrderController;
import ase.cw.exceptions.EmptyOrderException;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.exceptions.NoOrderException;
import ase.cw.model.Item;
import ase.cw.model.OrderItem;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Bartosz on 04.02.2019.
 */
public class OrderFrame extends JFrame implements ActionListener {

    // Top
    private JPanel content = new JPanel(new BorderLayout(10,10));

    // Lists
    private JList<Item> stockItems = new JList<>();
    private JList<Item> stockItemsSubset = new JList<>(); private int useStockItemsSubset = 0;
    private JList<OrderItem> orderItems = new JList<>();

    // Scroll panels
    private JScrollPane stockItemsScroll = new JScrollPane();
    private JScrollPane orderItemScroll = new JScrollPane();

    // Buttons
    private JButton startOrderButton = new JButton("Start Order");
    private JButton submitOrderButton = new JButton("Submit");
    private JButton cancelOrderButton = new JButton("Cancel");
    private JButton itemSearchButton = new JButton("Search");
    private JButton clearSearchButton = new JButton("Clear");
    private JButton addItemButton = new JButton("Add");
    private JButton removeItemButton = new JButton("Remove");

    // Labels
    private JLabel enterCustomerIdLabel = new JLabel("Enter Customer ID:");
    private JLabel searchItemLabel = new JLabel("Search items by name:");
    private JLabel currentOrderLabel = new JLabel("Current order:");
    private JLabel selectItemLabel = new JLabel("Select item from product list:");
    private JLabel subtotalLabel = new JLabel("Subtotal");
    private JLabel discountLabel = new JLabel("Discount");
    private JLabel totalLabel = new JLabel("Total");

    // Text output areas
    private JTextArea subtotal = new JTextArea("£ 0.00");
    private JTextArea discount = new JTextArea("£ 0.00");
    private JTextArea total = new JTextArea("£ 0.00");
    private JTextArea billString = new JTextArea();

    // Text input areas
    private JTextField customerIdInput = new JTextField(1);
    private JTextField searchItemInput = new JTextField(1);

    private OrderController orderController;

    // Frame builder
    public OrderFrame() {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle("Café Register");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new exitButtonPress());
        this.setPreferredSize(new Dimension(600,700));
        this.setResizable(false);
        this.buildFrame();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        System.out.println("GUI: Program opened.");
    }

    // Frame setters

    public void setOrderController(OrderController orderController) {
        this.orderController = orderController;
    }

    public void setStockItems(Item[] items) {
        stockItems.setListData(items);
        stockItemsScroll.setViewportView(stockItems);
    }

    public void setOrderItems(OrderItem[] orderItems) {
        this.orderItems.setListData(orderItems);
        orderItemScroll.setViewportView(this.orderItems);
    }

    public void setOrderTotals(Float subtotal, Float discount, Float total) {
        this.subtotal.setText("£"+subtotal.toString());
        this.discount.setText("£"+discount.toString());
        this.total.setText("£"+total.toString());
    }

    public void setBillString(String billString) {
        this.billString.setText(billString);
    }

    private void setStockItemsSubset(Item[] items) {
        stockItemsSubset.setListData(items);
        stockItemsScroll.setViewportView(stockItemsSubset);
    }

    private void searchMenu(String string) {
        ArrayList<Item> stock_items_subset_list  = new ArrayList<>();
        for (int i = 0; i < stockItems.getModel().getSize(); i++) {
            if (stockItems.getModel().getElementAt(i).getName().toLowerCase().contains(string.toLowerCase()))
                stock_items_subset_list.add(stockItems.getModel().getElementAt(i));
        }
        setStockItemsSubset(stock_items_subset_list.toArray(new Item[0]));
    }


    // Frame structure

    private void buildFrame() {

        startOrderButton.setEnabled(true);
        submitOrderButton.setEnabled(false);
        cancelOrderButton.setEnabled(false);
        removeItemButton.setEnabled(false);
        addItemButton.setEnabled(false);
        clearSearchButton.setEnabled(false);
        content.setBackground(Color.white);

        JPanel top = new JPanel(new GridLayout(1,2, 5, 5));

        // Left panel
        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel left_top = new JPanel(new GridLayout(4,1, 5, 5));
        left_top.add(searchItemLabel);
        searchItemInput.addKeyListener(new searchEnterPress());
        left_top.add(searchItemInput);

        JPanel left_top_nav = new JPanel(new GridLayout(1,2, 5, 5));
        searchItemInput.addActionListener(this);
        itemSearchButton.addActionListener(this);
        left_top_nav.add(itemSearchButton);
        clearSearchButton.addActionListener(this);
        left_top_nav.add(clearSearchButton);
        left_top.add(left_top_nav);
        left_top.add(selectItemLabel);

        JPanel left_bottom = new JPanel(new GridLayout(1,1, 5, 5));
        left_bottom.setBackground(Color.white);
        addItemButton.addActionListener(this);
        left_bottom.add(addItemButton);
        left.add(left_top, BorderLayout.PAGE_START);
        stockItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockItemsScroll.setViewportView(stockItems);
        left.add(stockItemsScroll, BorderLayout.CENTER);
        left.add(left_bottom, BorderLayout.PAGE_END);

        top.add(left);

        // Right panel
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel right_top = new JPanel(new GridLayout(4,1, 5, 5));
        right_top.add(enterCustomerIdLabel);
        customerIdInput.addActionListener(this);
        customerIdInput.addKeyListener(new startOrderEnterPress());
        right_top.add(customerIdInput);
        startOrderButton.addActionListener(this);
        right_top.add(startOrderButton);
        right_top.add(currentOrderLabel);

        JPanel right_bottom = new JPanel(new GridLayout(8,1, 5, 5));
        removeItemButton.addActionListener(this);
        right_bottom.add(removeItemButton);
        right_bottom.add(subtotalLabel);
        subtotal.setOpaque(false);
        right_bottom.add(subtotal);
        right_bottom.add(discountLabel);
        discount.setOpaque(false);
        right_bottom.add(discount);
        right_bottom.add(totalLabel);
        total.setOpaque(false);
        right_bottom.add(total);

        JPanel right_bottom_buttons = new JPanel(new GridLayout(1,2, 5, 5));
        submitOrderButton.addActionListener(this);
        right_bottom_buttons.add(submitOrderButton);
        cancelOrderButton.addActionListener(this);
        right_bottom_buttons.add(cancelOrderButton);
        right_bottom.add(right_bottom_buttons);
        right.add(right_top, BorderLayout.PAGE_START);
        orderItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderItemScroll.setViewportView(orderItems);
        right.add(orderItemScroll, BorderLayout.CENTER);
        right.add(right_bottom, BorderLayout.PAGE_END);

        top.add(right);

        this.add(top);

    }


    // Key press and mouse click actions

    private void fileWriter(File file, String content) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            System.out.println("GUI: Report saved as "+file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveReportOnExit(String report) {

        int result;
        int replace_existing;

        JFileChooser saveAs = new JFileChooser();
        saveAs.setSelectedFile(new File("report.txt"));
        saveAs.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
        saveAs.setDialogTitle("Save Report As");
        result = saveAs.showDialog(OrderFrame.this, "Save");
        File file = saveAs.getSelectedFile();

        if (result == JFileChooser.APPROVE_OPTION) {
            if (saveAs.getSelectedFile().exists()) {
                replace_existing = JOptionPane.showConfirmDialog(saveAs,
                        "Do you want to replace existing file?",
                        "Overwrite Existing File",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (replace_existing == JOptionPane.YES_OPTION) {
                    fileWriter(file, report);
                }
                if (replace_existing == JOptionPane.NO_OPTION) {
                    saveReportOnExit(report);
                }
            } else {
                fileWriter(file, report);
            }
        }
    }

    private class exitButtonPress extends WindowAdapter {
        public void  windowClosing(WindowEvent evt) {
            int dialog_box = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to close?\nAny pending order will be discarded.",
                    "Closing Register", JOptionPane.YES_NO_OPTION);

            if (dialog_box == JOptionPane.YES_OPTION) {
                cancelOrderButton.doClick();

                        String report = "This is your final report String";
                        saveReportOnExit(report);

                        System.out.println("GUI: Program closed.");
                        System.exit(0);

                    }
                }
            }

    private class searchEnterPress extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                itemSearchButton.doClick();
            }
        }
    }

    private class startOrderEnterPress extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                startOrderButton.doClick();
            }
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == itemSearchButton) {
            System.out.println("GUI: Item search button pressed.");
            clearSearchButton.setEnabled(true);
            searchMenu(searchItemInput.getText());
            useStockItemsSubset = 1;
        }

        if (e.getSource() == clearSearchButton) {
            System.out.println("GUI: Clear search button pressed.");
            searchItemInput.setText("");
            clearSearchButton.setEnabled(false);
            stockItemsScroll.setViewportView(stockItems);
            useStockItemsSubset = 0;
        }

        if (e.getSource() == startOrderButton) {
            System.out.println("GUI: Start order button pressed.");
            try {
                orderController.createNewOrder(customerIdInput.getText());
                customerIdInput.setEnabled(false);
                startOrderButton.setEnabled(false);
                submitOrderButton.setEnabled(true);
                cancelOrderButton.setEnabled(true);
                addItemButton.setEnabled(true);
            } catch (InvalidCustomerIdException exc) {
                JOptionPane.showMessageDialog(new JFrame(), "Error creating new order:\nCustomer ID has to be 8 characters", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == addItemButton) {
            System.out.println("GUI: Add item button pressed.");
            Item item;
            if (useStockItemsSubset==0) {
                item = stockItems.getSelectedValue();
            }
            else {
                item = stockItemsSubset.getSelectedValue();
            }
            try {
                orderController.addItemToPendingOrder(item);
                orderItems.setSelectedIndex(orderItems.getModel().getSize()-1);
                orderItems.ensureIndexIsVisible(orderItems.getModel().getSize()-1);
            } catch (NoOrderException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Error adding item:\nInvalid order", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Select item to add", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (orderItems.getModel().getSize() > 0) {
                submitOrderButton.setEnabled(true); removeItemButton.setEnabled(true);}
        }

        if (e.getSource() == removeItemButton) {
            System.out.println("GUI: Remove item button pressed.");
            OrderItem item = orderItems.getSelectedValue();
            int idx = orderItems.getSelectedIndex();
            try {
                orderController.removeItemFromPendingOrder(item);
                if (idx == orderItems.getModel().getSize()) {
                    orderItems.setSelectedIndex(orderItems.getModel().getSize()-1);
                }
                else
                    orderItems.setSelectedIndex(idx);

            } catch (NoOrderException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Error removing item:\nInvalid order", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Select item to remove", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (orderItems.getModel().getSize()==0) {
                submitOrderButton.setEnabled(false);
                removeItemButton.setEnabled(false);
            }
        }

        if (e.getSource() == submitOrderButton) {
            System.out.println("GUI: Submit order button pressed.");
            try {
                orderController.finalizePendingOrder();

                JFrame billFrame = new JFrame(customerIdInput.getText());

                billString.setMargin(new Insets(10,10,10,10));
                billString.setFont( new Font("monospaced", Font.PLAIN, 12) );
                billString.setEditable(false);
                billString.setLineWrap(false);


                JScrollPane sp = new JScrollPane(billString);
                Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
                sp.setBorder(border);

                billFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
                billFrame.setPreferredSize(new Dimension(280,700));
                billFrame.setResizable(true);
                billFrame.setLocation(this.getX()+this.getWidth(), this.getY());
                billFrame.add(sp);
                billFrame.pack();
                billFrame.setVisible(true);

                customerIdInput.setText("");
                customerIdInput.setEnabled(true);
                startOrderButton.setEnabled(true);
                submitOrderButton.setEnabled(false);
                cancelOrderButton.setEnabled(false);
                removeItemButton.setEnabled(false);
                addItemButton.setEnabled(false);

            } catch (NoOrderException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error submitting order:\nInvalid order", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (EmptyOrderException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error submitting order:\nEmpty order can't be submitted", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidCustomerIdException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error submitting order\ninvalid customer ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == cancelOrderButton) {
            System.out.println("GUI: Cancel order button pressed.");

            int dialog_box = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to cancel pending order?",
                    "Cancelling", JOptionPane.YES_NO_OPTION);

            if (dialog_box == JOptionPane.YES_OPTION) {
                customerIdInput.setText("");
                customerIdInput.setEnabled(true);
                startOrderButton.setEnabled(true);
                submitOrderButton.setEnabled(false);
                cancelOrderButton.setEnabled(false);
                removeItemButton.setEnabled(false);
                addItemButton.setEnabled(false);
                orderController.cancelPendingOrder();

            }
        }
    }
}


