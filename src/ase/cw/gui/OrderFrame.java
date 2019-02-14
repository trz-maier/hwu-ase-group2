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
 * Created by User on 04.02.2019.
 */
public class OrderFrame extends JFrame implements ActionListener {

    // Top
    private JPanel Content = new JPanel(new BorderLayout(10,10));

    // Lists
    private JList<Item> StockItems = new JList<>();
    private JList<Item> StockItemsSubset = new JList<>();
    private JList<OrderItem> OrderItems = new JList<>();

    // Scroll panels
    private JScrollPane StockItemsScroll = new JScrollPane();
    private JScrollPane OrderItemScroll = new JScrollPane();

    // Buttons
    private JButton StartOrderButton    = new JButton("Start Order");
    private JButton SubmitOrderButton   = new JButton("Submit");
    private JButton CancelOrderButton   = new JButton("Cancel");
    private JButton ItemSearchButton    = new JButton("Search");
    private JButton ClearSearchButton   = new JButton("Clear");
    private JButton AddItemButton       = new JButton("Add");
    private JButton RemoveItemButton    = new JButton("Remove");

    // Labels
    private JLabel EnterCustomerIdLabel = new JLabel("Enter Customer ID:");
    private JLabel SearchItemLabel      = new JLabel("Search items by name:");
    private JLabel CurrentOrderLabel    = new JLabel("Current order:");
    private JLabel SelectItemLabel      = new JLabel("Select item from product list:");
    private JLabel SubtotalLabel        = new JLabel("Subtotal");
    private JLabel DiscountLabel        = new JLabel("Discount");
    private JLabel TotalLabel           = new JLabel("Total");

    // Text output areas
    private JTextArea Subtotal          = new JTextArea("£ 0.00");
    private JTextArea Discount          = new JTextArea("£ 0.00");
    private JTextArea Total             = new JTextArea("£ 0.00");
    private JTextArea BillString        = new JTextArea();

    // Text input areas
    private JTextField CustomerIdInput  = new JTextField(1);
    private JTextField SearchItemInput  = new JTextField(1);

    private OrderController orderController;

    // Frame builder
    public OrderFrame() {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("coffee.png")));
        this.setTitle("Café Register");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new exitButtonPress());
        this.setPreferredSize(new Dimension(600,800));
        this.setResizable(false);
        this.buildFrame();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        System.out.println("GUI: Program opened.");
    }

    public void setOrderController(OrderController orderController) {
        this.orderController = orderController;
    }

    public void setStockItems(Item[] items) {
        StockItems.setListData(items);
        StockItemsScroll.setViewportView(StockItems);
    }

    public void setOrderItems(OrderItem[] orderItems) {
        OrderItems.setListData(orderItems);
        OrderItemScroll.setViewportView(OrderItems);
    }

    public void setOrderTotals(Float subtotal, Float discount, Float total) {
        Subtotal.setText("£"+subtotal.toString());
        Discount.setText("£"+discount.toString());
        Total.setText("£"+total.toString());
    }

    public void setBillString(String billString) {
        BillString.setText(billString);
    }

    private void setStockItemsSubset(Item[] items) {
        StockItemsSubset.setListData(items);
        StockItemsScroll.setViewportView(StockItemsSubset);
    }

    private void searchMenu(String string) {
        ArrayList<Item> stock_items_subset_list  = new ArrayList<>();
        for (int i = 0; i < StockItems.getModel().getSize(); i++) {
            Item item = StockItems.getModel().getElementAt(i);
            if (item.toString().toLowerCase().contains(string.toLowerCase()))
                stock_items_subset_list.add(item);
        }
        setStockItemsSubset((Item[])stock_items_subset_list.toArray());
    }


    // Frame structure

    private void buildFrame() {

        StartOrderButton.setEnabled(true);
        SubmitOrderButton.setEnabled(false);
        CancelOrderButton.setEnabled(false);
        RemoveItemButton.setEnabled(false);
        AddItemButton.setEnabled(false);
        ClearSearchButton.setEnabled(false);
        Content.setBackground(Color.white);

        JPanel top = new JPanel(new GridLayout(1,2, 5, 5));

        // Left panel
        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel left_top = new JPanel(new GridLayout(4,1, 5, 5));
        left_top.add(SearchItemLabel);
        SearchItemInput.addKeyListener(new searchEnterPress());
        left_top.add(SearchItemInput);

        JPanel left_top_nav = new JPanel(new GridLayout(1,2, 5, 5));
        SearchItemInput.addActionListener(this);
        ItemSearchButton.addActionListener(this);
        left_top_nav.add(ItemSearchButton);
        ClearSearchButton.addActionListener(this);
        left_top_nav.add(ClearSearchButton);
        left_top.add(left_top_nav);
        left_top.add(SelectItemLabel);

        JPanel left_bottom = new JPanel(new GridLayout(1,1, 5, 5));
        left_bottom.setBackground(Color.white);
        AddItemButton.addActionListener(this);
        left_bottom.add(AddItemButton);
        left.add(left_top, BorderLayout.PAGE_START);
        StockItems.addMouseListener(new doubleClickToAdd());
        StockItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        StockItemsScroll.setViewportView(StockItems);
        left.add(StockItemsScroll, BorderLayout.CENTER);
        left.add(left_bottom, BorderLayout.PAGE_END);

        top.add(left);

        // Right panel
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel right_top = new JPanel(new GridLayout(4,1, 5, 5));
        right_top.add(EnterCustomerIdLabel);
        CustomerIdInput.addActionListener(this);
        CustomerIdInput.addKeyListener(new startOrderEnterPress());
        right_top.add(CustomerIdInput);
        StartOrderButton.addActionListener(this);
        right_top.add(StartOrderButton);
        right_top.add(CurrentOrderLabel);

        JPanel right_bottom = new JPanel(new GridLayout(8,1, 5, 5));
        RemoveItemButton.addActionListener(this);
        right_bottom.add(RemoveItemButton);
        right_bottom.add(SubtotalLabel);
        Subtotal.setOpaque(false);
        right_bottom.add(Subtotal);
        right_bottom.add(DiscountLabel);
        Discount.setOpaque(false);
        right_bottom.add(Discount);
        right_bottom.add(TotalLabel);
        Total.setOpaque(false);
        right_bottom.add(Total);

        JPanel right_bottom_buttons = new JPanel(new GridLayout(1,2, 5, 5));
        SubmitOrderButton.addActionListener(this);
        right_bottom_buttons.add(SubmitOrderButton);
        CancelOrderButton.addActionListener(this);
        right_bottom_buttons.add(CancelOrderButton);
        right_bottom.add(right_bottom_buttons);
        right.add(right_top, BorderLayout.PAGE_START);
        OrderItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        OrderItems.addMouseListener(new doubleClickToRemove());
        OrderItemScroll.setViewportView(OrderItems);
        right.add(OrderItemScroll, BorderLayout.CENTER);
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
                    "  Are you sure you want to close?  \n  Any open order will be discarded.  ",
                    "Closing Register", JOptionPane.YES_NO_OPTION);

            if (dialog_box == JOptionPane.YES_OPTION) {
                CancelOrderButton.doClick();

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
                ItemSearchButton.doClick();
            }
        }
    }

    private class startOrderEnterPress extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                StartOrderButton.doClick();
            }
        }
    }

    private class doubleClickToAdd extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                AddItemButton.doClick();
            }
        }
    }

    private class doubleClickToRemove extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                RemoveItemButton.doClick();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == ItemSearchButton) {
            System.out.println("GUI: Item search button pressed.");
            ClearSearchButton.setEnabled(true);
            searchMenu(SearchItemInput.getText());
        }

        if (e.getSource() == ClearSearchButton) {
            System.out.println("GUI: Clear search button pressed.");
            SearchItemInput.setText("");
            ClearSearchButton.setEnabled(false);
            StockItemsScroll.setViewportView(StockItems);
        }

        if (e.getSource() == StartOrderButton) {
            System.out.println("GUI: Start order button pressed.");
            try {
                orderController.createNewOrder(CustomerIdInput.getText());
                CustomerIdInput.setEnabled(false);
                StartOrderButton.setEnabled(false);
                SubmitOrderButton.setEnabled(true);
                CancelOrderButton.setEnabled(true);
                AddItemButton.setEnabled(true);
                System.out.println(OrderItems);
            } catch (InvalidCustomerIdException exc) {
                JOptionPane.showMessageDialog(new JFrame(), "Customer ID has to be 8 characters", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == AddItemButton) {
            System.out.println("GUI: Add item button pressed.");
            Item item = StockItems.getSelectedValue();
            try {
                orderController.addItemToPendingOrder(item);
                //OrderItems.setSelectedIndex(OrderItems.getModel().getSize()-1);
                //OrderItems.ensureIndexIsVisible(OrderItems.getModel().getSize()-1);
                System.out.println("ORDER: Item "+item.toString()+" has been added to pending order.");
            } catch (NoOrderException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error adding item", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (OrderItems.getModel().getSize() > 0) {
                SubmitOrderButton.setEnabled(true); RemoveItemButton.setEnabled(true);}
        }

        if (e.getSource() == RemoveItemButton) {
            System.out.println("GUI: Remove item button pressed.");
            OrderItem item = OrderItems.getSelectedValue();
            try {
                orderController.removeItemFromPendingOrder(item);
                System.out.println("ORDER: Item "+item.toString()+" has been removed from pending order.");
            } catch (NoOrderException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error removing item", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (OrderItems.getModel().getSize() == 0) {
                SubmitOrderButton.setEnabled(false); RemoveItemButton.setEnabled(false);}
        }

        if (e.getSource() == SubmitOrderButton) {
            System.out.println("GUI: Submit order button pressed.");
            try {
                orderController.finalizePendingOrder();

                JFrame billFrame = new JFrame("Customer: "+ CustomerIdInput.getText());

                BillString.setMargin(new Insets(10,10,10,10));
                BillString.setFont( new Font("monospaced", Font.PLAIN, 14) );
                BillString.setEditable(false);
                BillString.setLineWrap(false);


                JScrollPane sp = new JScrollPane(BillString);
                Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
                sp.setBorder(border);

                billFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("coffee.png")));
                billFrame.setPreferredSize(new Dimension(300,800));
                billFrame.setResizable(false);
                billFrame.setLocation(this.getX()+this.getWidth(), this.getY());
                billFrame.add(sp);
                billFrame.pack();
                billFrame.setVisible(true);

                CustomerIdInput.setText("");
                CustomerIdInput.setEnabled(true);
                StartOrderButton.setEnabled(true);
                SubmitOrderButton.setEnabled(false);
                CancelOrderButton.setEnabled(false);
                RemoveItemButton.setEnabled(false);
                AddItemButton.setEnabled(false);

            } catch (NoOrderException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error submitting order", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (EmptyOrderException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error submitting order", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidCustomerIdException exception) {
                exception.getStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error submitting order", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == CancelOrderButton) {
            System.out.println("GUI: Cancel order button pressed.");
            CustomerIdInput.setText("");
            CustomerIdInput.setEnabled(true);
            StartOrderButton.setEnabled(true);
            SubmitOrderButton.setEnabled(false);
            CancelOrderButton.setEnabled(false);
            RemoveItemButton.setEnabled(false);
            AddItemButton.setEnabled(false);
            orderController.cancelPendingOrder();
        }
    }
}


