/**
 *
 */
package ase.cw.model;

import ase.cw.model.Item.Category;

/**
 * @author Ram
 *
 */
public class Bill {
    private float total;
    private float subtotal;
    private float discount;



    private String billString;

    public Bill(Order order) {
        subtotal = calculateSubtotal(order);
        discount = calculateDiscount(order);
        total = subtotal - discount;
        billString = showBill(order);

    }

    private float calculateSubtotal(Order order) {
        this.subtotal = 0;

        for (OrderItem temp : order.getOrderItems()) {
            this.subtotal += temp.getItem().getPrice();
        }

        subtotal = Math.round(subtotal*100)/100;
        return subtotal;
    }

    private float calculateDiscount(Order order) {
        this.discount = 0;
        int countFood = 0;
        int countBeverage = 0;

        Category categoryFood = Category.valueOf("FOOD");

        for (OrderItem temp : order.getOrderItems()) {
            if (temp.getItem().getCategory() == categoryFood) {
                countFood++;
            } else {
                countBeverage++;
            }
        }

        if (countFood > 1 && countBeverage > 0) {
            discount = (float) (subtotal * 0.2);
        }
        discount = Math.round(discount*100)/100;
        return discount;
    }

    public String showBill(Order order) {
        billString = "==================================\n\n";
        billString += String.format("Customer: %s \nDate: %tc\n\n", order.getCustomerId(), order.getTimestamp());
        billString += "==================================\n\n";

        for (OrderItem temp : order.getOrderItems()) {
            billString += String.format("%-28s", temp.toString());
            billString += String.format("£%5.2f\n", temp.getItem().getPrice());
        }

        billString += "----------------------------------\n\n";
        billString += String.format("%-28s", "SUBTOTAL");
        billString += String.format("£%5.2f\n", this.subtotal);
        billString += String.format("%-28s", "DISCOUNT");
        billString += String.format("£%5.2f\n\n", this.discount);
        billString += "==================================\n";
        billString += String.format("%-28s", "TOTAL");
        billString += String.format("£%5.2f\n", this.total);
        billString += "==================================\n";
        return billString;
    }

    /**
     * @return the total
     */
    public float getTotal() {
        return total;
    }

    /**
     * @return the subtotal
     */
    public float getSubtotal() {
        return subtotal;
    }

    /**
     * @return the discount
     */
    public float getDiscount() {
        return discount;
    }

    public String getBillString() {
        return billString;
    }
}
