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

    public Bill(Order order) {
        subtotal = calculateSubtotal(order);
        discount = calculateDiscount(order);
        total = subtotal - discount;
    }

    private float calculateSubtotal(Order order) {
        this.subtotal = 0;

        for (OrderItem temp : order.getOrderItems()) {
            this.subtotal += temp.getItem().getPrice();
        }
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
        return discount;
    }

    private String showBill() {
        String stringBill;
        stringBill = String.format("SUBTOTAL: %.3g \n DISCOUNT: %.3g \n TOTAL: %.3g \n", this.subtotal, this.discount, this.total);
        return stringBill;
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


}
