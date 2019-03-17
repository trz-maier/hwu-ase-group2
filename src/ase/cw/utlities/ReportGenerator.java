package ase.cw.utlities;

import ase.cw.model.Bill;
import ase.cw.model.Item;
import ase.cw.model.Order;
import ase.cw.model.OrderItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {

    private static final String ENDLINE = System.lineSeparator();

    /**
     * Generates the total report to be outputted in String format
     *
     * @return String representation of the report text body
     */
    private static String createReport(Map<String, Item> stockItems, Order[] processedOrders) {
        // all items in menu ✓
        // number of times each item sold ✓
        // income for all processedOrders ✓
        double sumTotal = 0;
        double sumSubtotal = 0;
        Map<Item, Integer> itemSoldQuantities = new HashMap<>();
        StringBuilder builder = new StringBuilder(stockItems.size() * 20);

        for (Order order : processedOrders) {
            Bill orderBill = order.getBill();
            sumSubtotal += orderBill.getSubtotal();
            sumTotal += orderBill.getTotal();

            for (OrderItem orderItem : order.getOrderItems()) {
                Item item = orderItem.getItem();

                Integer soldCount = itemSoldQuantities.get(item);
                if (soldCount == null) {
                    itemSoldQuantities.put(item, 1);
                } else {
                    itemSoldQuantities.put(item, soldCount + 1);
                }
            }
        }

        String leftHeader = "----------- Item -----------";
        String separator = " | ";
        String rightHeader = "--- Quantities Sold ---";
        builder.append(leftHeader).append(separator).append(rightHeader).append(ENDLINE);

        stockItems.forEach((itemId, item) -> {
            Integer itemSoldQuantity = itemSoldQuantities.containsKey(item) ? itemSoldQuantities.get(item) : 0;
            builder.append(ReportGenerator.padString(item.getName(), leftHeader.length())).append(separator).append(itemSoldQuantity.toString()).append(ENDLINE);
        });

        builder.append(ENDLINE).append(ENDLINE).append("Total Sales w/o discounts: ").append(String.format("£%.2f",
                sumSubtotal)).append(ENDLINE).append("Total Sales with discounts: ").append(String.format("£%.2f",
                sumTotal));

        return builder.toString();
    }

    private static String padString(String str, int width) {
        return ReportGenerator.padString(str, width, ' ');
    }

    private static String padString(String str, int width, char fill) {
        return String.format("%-" + width + "s", str).replace(' ', fill);
    }

    public static void generateReportTo(String filename, Map<String, Item> stockItems, Order[] processedOrders) {
        ReportGenerator.generateReportTo(new File(filename), stockItems, processedOrders);
    }

    public static void generateReportTo(File filename, Map<String, Item> stockItems, Order[] processedOrders) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(ReportGenerator.createReport(stockItems, processedOrders));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

