package ase.cw.gui;

import ase.cw.control.OrderController;
import ase.cw.exceptions.EmptyOrderException;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.exceptions.NoOrderException;
import ase.cw.model.Item;
import ase.cw.model.OrderItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Created by Bartosz on 04.02.2019.
 */
public class OrderFrame extends JFrame implements ActionListener {

    // Top
    private JPanel content = new JPanel(new BorderLayout(10, 10));

    // Lists
    private JList<Item> stockItemsJList = new JList<>();
    private JList<Item> stockItemsSubsetJList = new JList<>();
    private JList<OrderItem> orderItemsJList = new JList<>();

    private List<Item> stockItemsList;

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
    private JTextArea subtotal = new JTextArea(String.format("£ %.2f", (float) 0.0));
    private JTextArea discount = new JTextArea(String.format("£ %.2f", (float) 0.0));
    private JTextArea total = new JTextArea(String.format("£ %.2f", (float) 0.0));
    private JTextArea billString = new JTextArea();

    // Text input areas
    private JTextField customerIdInput = new JTextField(1);
    private JTextField searchItemInput = new JTextField(1);

    // Order Controller
    private OrderController orderController;


    // Frame constructor

    public OrderFrame() {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("coffee.png")));
        this.setTitle("Café Register");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new exitButtonPress());
        this.setPreferredSize(new Dimension(600, 700));
        this.setResizable(false);
        this.buildFrame();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        System.out.println("GUI: Program opened.");
    }

    // Frame setters

    /**
     *  provide instance of OrderController class
     *
     * @param orderController
     */
    public void setOrderController(OrderController orderController) {
        this.orderController = orderController;
    }

    /**
     * Set items to display in Frame
     *
     * @param items set list of items to display
     */
    public void setStockItems(Item[] items) {
        stockItemsList = Arrays.asList(items);
        stockItemsJList.setListData(items);
        stockItemsScroll.setViewportView(stockItemsJList);
    }

    /**
     * Set order items to display in Frame
     *
     * @param orderItems set list of order items to display
     */
    public void setOrderItems(OrderItem[] orderItems) {
        this.orderItemsJList.setListData(orderItems);
        orderItemScroll.setViewportView(this.orderItemsJList);
    }

    /**
     * Set values for order totals to display in Frame
     *
     * @param subtotal monetary value of subtotal to display
     * @param discount monetary value of discount to display
     * @param total monetary value of total to display
     */
    public void setOrderTotals(Float subtotal, Float discount, Float total) {
        this.subtotal.setText(String.format("£ %.2f", subtotal));
        this.discount.setText(String.format("£ %.2f", discount));
        this.total.setText(String.format("£ %.2f", total));
    }

    /**
     * Provide String representation of the bill to display
     *
     * @param billString
     */
    public void setBillString(String billString) {
        this.billString.setText(billString);
    }

    /**
     * Search for items using a string, case insensitive
     *
     * @param searchString string to search for
     */
    private void searchStockItems(String searchString) {
        if (searchString == null) {
            stockItemsSubsetJList.setListData(new Item[0]);
            stockItemsScroll.setViewportView(stockItemsJList);
        }
        else {
            List<Item> result = stockItemsList.stream()
                    .filter(i -> i.getName().toLowerCase().contains(searchString.toLowerCase().trim()))
                    .collect(Collectors.toList());

            stockItemsSubsetJList.setListData(result.toArray(new Item[0]));
            stockItemsScroll.setViewportView(stockItemsSubsetJList);
        }
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

        JPanel top = new JPanel(new GridLayout(1, 2, 5, 5));

        // Left panel
        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel left_top = new JPanel(new GridLayout(4, 1, 5, 5));
        left_top.add(searchItemLabel);
        searchItemInput.addKeyListener(new searchEnterPress());
        left_top.add(searchItemInput);

        JPanel left_top_nav = new JPanel(new GridLayout(1, 2, 5, 5));
        searchItemInput.addActionListener(this);
        itemSearchButton.addActionListener(this);
        left_top_nav.add(itemSearchButton);
        clearSearchButton.addActionListener(this);
        left_top_nav.add(clearSearchButton);
        left_top.add(left_top_nav);
        left_top.add(selectItemLabel);

        JPanel left_bottom = new JPanel(new GridLayout(1, 1, 5, 5));
        left_bottom.setBackground(Color.white);
        addItemButton.addActionListener(this);
        left_bottom.add(addItemButton);
        left.add(left_top, BorderLayout.PAGE_START);
        stockItemsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockItemsScroll.setViewportView(stockItemsJList);
        left.add(stockItemsScroll, BorderLayout.CENTER);
        left.add(left_bottom, BorderLayout.PAGE_END);

        top.add(left);

        // Right panel
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel right_top = new JPanel(new GridLayout(4, 1, 5, 5));
        right_top.add(enterCustomerIdLabel);
        customerIdInput.addActionListener(this);
        customerIdInput.addKeyListener(new startOrderEnterPress());
        right_top.add(customerIdInput);
        startOrderButton.addActionListener(this);
        right_top.add(startOrderButton);
        right_top.add(currentOrderLabel);

        JPanel right_bottom = new JPanel(new GridLayout(8, 1, 5, 5));
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

        JPanel right_bottom_buttons = new JPanel(new GridLayout(1, 2, 5, 5));
        submitOrderButton.addActionListener(this);
        right_bottom_buttons.add(submitOrderButton);
        cancelOrderButton.addActionListener(this);
        right_bottom_buttons.add(cancelOrderButton);
        right_bottom.add(right_bottom_buttons);
        right.add(right_top, BorderLayout.PAGE_START);
        orderItemsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderItemScroll.setViewportView(orderItemsJList);
        right.add(orderItemScroll, BorderLayout.CENTER);
        right.add(right_bottom, BorderLayout.PAGE_END);

        top.add(right);

        this.add(top);
    }


    // Key press and mouse click actions

    private void saveReportOnExit() {
        int result;
        int replace_existing;

        JFileChooser saveAs = new JFileChooser();
        saveAs.setSelectedFile(new File("report.txt"));
        saveAs.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
        saveAs.setDialogTitle("Save Report As");
        result = saveAs.showDialog(OrderFrame.this, "Save");
        File reportFile = saveAs.getSelectedFile();

        if (result == JFileChooser.APPROVE_OPTION) {
            if (reportFile.exists()) {
                replace_existing = JOptionPane.showConfirmDialog(saveAs, "Do you want to replace existing file?",
                        "Overwrite Existing File", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (replace_existing == JOptionPane.YES_OPTION) orderController.generateReportTo(reportFile);
                if (replace_existing == JOptionPane.NO_OPTION) saveReportOnExit();
            } else {
                orderController.generateReportTo(reportFile);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == itemSearchButton) {
            System.out.println("GUI: Item search button pressed.");
            clearSearchButton.setEnabled(true);
            searchStockItems(searchItemInput.getText());
            stockItemsScroll.setViewportView(stockItemsSubsetJList);

        }

        if (e.getSource() == clearSearchButton) {
            System.out.println("GUI: Clear search button pressed.");
            searchItemInput.setText("");
            clearSearchButton.setEnabled(false);
            searchStockItems(null);
            stockItemsScroll.setViewportView(stockItemsJList);

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
                JOptionPane.showMessageDialog(new JFrame(), exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == addItemButton) {
            System.out.println("GUI: Add item button pressed.");
             try {
                 if (stockItemsSubsetJList.getModel().getSize()>0) {
                     orderController.addItemToPendingOrder(stockItemsSubsetJList.getSelectedValue());
                 }
                 else {
                     orderController.addItemToPendingOrder(stockItemsJList.getSelectedValue());
                 }
                orderItemsJList.setSelectedIndex(orderItemsJList.getModel().getSize() - 1);
                orderItemsJList.ensureIndexIsVisible(orderItemsJList.getModel().getSize() - 1);
            } catch (NoOrderException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Error adding item:\nInvalid order", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Select item to add", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (orderItemsJList.getModel().getSize() > 0) {
                submitOrderButton.setEnabled(true);
                removeItemButton.setEnabled(true);
            }
        }

        if (e.getSource() == removeItemButton) {
            System.out.println("GUI: Remove item button pressed.");
            OrderItem item = orderItemsJList.getSelectedValue();
            int idx = orderItemsJList.getSelectedIndex();
            try {
                orderController.removeItemFromPendingOrder(item);
                if (idx == orderItemsJList.getModel().getSize()) {
                    orderItemsJList.setSelectedIndex(orderItemsJList.getModel().getSize() - 1);
                } else orderItemsJList.setSelectedIndex(idx);

            } catch (NoOrderException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Error removing item:\nInvalid order", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(new JFrame(), "Select item to remove", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            if (orderItemsJList.getModel().getSize() == 0) {
                submitOrderButton.setEnabled(false);
                removeItemButton.setEnabled(false);
            }
        }

        if (e.getSource() == submitOrderButton) {
            System.out.println("GUI: Submit order button pressed.");
            try {
                orderController.finalizePendingOrder();

                JFrame billFrame = new JFrame(customerIdInput.getText());

                billString.setMargin(new Insets(10, 10, 10, 10));
                billString.setFont(new Font("monospaced", Font.PLAIN, 12));
                billString.setEditable(false);
                billString.setLineWrap(false);


                JScrollPane sp = new JScrollPane(billString);
                Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
                sp.setBorder(border);

                billFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
                        "coffee.png")));
                billFrame.setPreferredSize(new Dimension(270, 700));
                billFrame.setResizable(true);
                billFrame.setLocation(this.getX() + this.getWidth(), this.getY());
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
                JOptionPane.showMessageDialog(new JFrame(), "Error submitting order:\nInvalid order", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (EmptyOrderException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error submitting order:\nEmpty order can't be submitted"
                        , "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == cancelOrderButton) {
            System.out.println("GUI: Cancel order button pressed.");

            int dialog_box = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel pending order?",
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

    private class exitButtonPress extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            if (!customerIdInput.isEnabled()) {
                int dialog_box = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel pending order?"
                        , "Cancelling", JOptionPane.YES_NO_OPTION);
                if (dialog_box == JOptionPane.NO_OPTION) {
                    return;
                }
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
            saveReportOnExit();
            System.out.println("GUI: Program closed.");
            System.exit(0);

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
}


