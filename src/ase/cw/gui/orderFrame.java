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
public class orderFrame extends JFrame implements ActionListener {

    // Top
    private JPanel Content              = new JPanel(new BorderLayout(10,10));

    // Lists
    private JScrollPane MenuItemScroll  = new JScrollPane();
    private JScrollPane OrderItemScroll = new JScrollPane();
    private JList<Item> MenuItems       = new JList<>();
    private JList<Item> MenuItemsSubset = new JList<>();
    private JList<OrderItem> OrderItems = new JList<>();

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

    // Text input areas
    private JTextField CustomerIdInput  = new JTextField(1);
    private JTextField SearchItemInput  = new JTextField(1);

    // Frame builder
    public orderFrame() {

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

    public JList setStockItems(Item[] items) {
        MenuItems.setListData(items);
        return MenuItems;
    }

    public JList setOrderItems(OrderItem[] orderItems) {
        OrderItems.setListData(orderItems);
        return OrderItems;
    }

    private void setMenuSubset(Item[] items) {
        MenuItemsSubset.setListData(items);

    }

    private void searchMenu(String string) {
        ArrayList<Item> stock_items_subset_list  = new ArrayList<>();
        for (int i = 0; i < MenuItems.getModel().getSize(); i++) {
            Item item = MenuItems.getModel().getElementAt(i);
            if (item.toString().toLowerCase().contains(string.toLowerCase()))
                stock_items_subset_list.add(item);
        }
        setMenuSubset((Item[])stock_items_subset_list.toArray());
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
        MenuItems.addMouseListener(new doubleClickToAdd());
        MenuItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MenuItemScroll.setViewportView(MenuItems);
        left.add(MenuItemScroll, BorderLayout.CENTER);
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
        // On exit as where to save final report, check if file exists. Cancel to exit.
        int result;
        int replace_existing;

        JFileChooser saveAs = new JFileChooser();
        saveAs.setSelectedFile(new File("report.txt"));
        saveAs.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
        saveAs.setDialogTitle("Save Report As");
        result = saveAs.showDialog(orderFrame.this, "Save");
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

    public class exitButtonPress extends WindowAdapter {
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

    public class searchEnterPress extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                ItemSearchButton.doClick();
            }
        }
    }

    public class startOrderEnterPress extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                StartOrderButton.doClick();
            }
        }
    }

    public class doubleClickToAdd extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                AddItemButton.doClick();
            }
        }
    }

    public class doubleClickToRemove extends MouseAdapter {
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
            MenuItemScroll.setViewportView(MenuItemsSubset);
        }

        if (e.getSource() == ClearSearchButton) {
            System.out.println("GUI: Clear search button pressed.");
            SearchItemInput.setText("");
            ClearSearchButton.setEnabled(false);
            MenuItemScroll.setViewportView(MenuItems);
        }

        if (e.getSource() == StartOrderButton) {
            System.out.println("GUI: Start order button pressed.");
            try {
                OrderController.createNewOrder(CustomerIdInput.getText());
                CustomerIdInput.setEnabled(false);
                StartOrderButton.setEnabled(false);
                SubmitOrderButton.setEnabled(true);
                CancelOrderButton.setEnabled(true);
                AddItemButton.setEnabled(true);
            } catch (InvalidCustomerIdException exc) {
                JOptionPane.showMessageDialog(new JFrame(), "Customer ID has to be 8 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == AddItemButton) {
            System.out.println("GUI: Add item button pressed.");
            if (OrderItems.getModel().getSize() > 0) {
                SubmitOrderButton.setEnabled(true); RemoveItemButton.setEnabled(true);}
            try {
                Item item = (Item) MenuItems.getSelectedValue();
                OrderItem[] items = OrderController.addItemToPendingOrder(item);
                OrderItems.setListData(items);
                OrderItems.setSelectedIndex(OrderItems.getModel().getSize()-1);
                OrderItems.ensureIndexIsVisible(OrderItems.getModel().getSize()-1);
                System.out.println("ORDER: Item "+item.toString()+" has been added to pending order.");
                //TODO: Update bill fields
            } catch (NoOrderException exception) {
                exception.getStackTrace();
                //TODO: Display appropriate error message
            }
        }

        if (e.getSource() == RemoveItemButton) {
            System.out.println("GUI: Remove item button pressed.");
            if (OrderItems.getModel().getSize() == 0) {
                SubmitOrderButton.setEnabled(false); RemoveItemButton.setEnabled(false);}
            try {
                OrderItem orderitem = OrderItems.getSelectedValue();
                OrderItem[] items = OrderController.removeItemfromPendingOrder(orderitem.getItem());
                OrderItems.setListData(items);
                System.out.println("ORDER: Item "+orderitem.toString()+" has been removed from pending order.");
                //TODO: Update bill fields
            } catch (NoOrderException exception) {
                exception.getStackTrace();
                //TODO: Display appropriate error message
            }
        }

        if (e.getSource() == SubmitOrderButton) {
            System.out.println("GUI: Submit order button pressed.");

            CustomerIdInput.setEnabled(true);
            StartOrderButton.setEnabled(true);
            SubmitOrderButton.setEnabled(false);
            CancelOrderButton.setEnabled(false);
            RemoveItemButton.setEnabled(false);
            AddItemButton.setEnabled(false);

            JFrame billFrame = new JFrame("Bill: "+ CustomerIdInput.getText());
            CustomerIdInput.setText("");

            JTextArea bill_content = new JTextArea();
            bill_content.setMargin(new Insets(10,10,10,10));
            bill_content.setFont( new Font("monospaced", Font.PLAIN, 14) );
            bill_content.setEditable(false);
            bill_content.setLineWrap(false);
            //TODO: add string representation of the bill
            //bill_content.setText(OrderController.getBillString());
            bill_content.setText(
                    "------------------------- \nBill for order XXXXX\n09/02/2019 12:56\n------------------------" +
                    "\nItem 1            £1.90\nItem 2            £1.90\nItem 3            £1.90\nItem 4            £1.90\nItem 5            £1.90\n------------------------" +
                    "\nSubtotal          £9.50\nDiscount          £0.90\n------------------------\nTotal             £8.60\n------------------------\n"

            );
            JScrollPane sp = new JScrollPane(bill_content);
            Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
            sp.setBorder(border);


            billFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("coffee.png")));
            billFrame.setPreferredSize(new Dimension(300,800));
            billFrame.setResizable(false);
            billFrame.setLocation(this.getX()+this.getWidth(), this.getY());
            billFrame.add(sp);
            billFrame.pack();
            billFrame.setVisible(true);
            try {
                OrderController.finalizePendingOrder();
            } catch (NoOrderException exception) {
                exception.getStackTrace();
            } catch (EmptyOrderException exception) {
                exception.getStackTrace();
            } catch (InvalidCustomerIdException exception) {
                exception.getStackTrace();
            }

            System.out.println("ORDER: Order has been submitted.");
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

            OrderController.cancelPendingOrder();
            //TODO: Update list of order items to display
            //TODO: Update bill values to display
           //OrderItems.setListData();

            System.out.println("ORDER: Order has been cancelled.");
        }
    }
}


