/**
 *
 */
package ase.cw.model;

import ase.cw.model.Item.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ram
 *
 */
public class Bill {
    private float total;
    private float subtotal;
    private float discount;
    private String billString;

    Category categoryFood = Category.valueOf("FOOD");
    Category categoryBeverage = Category.valueOf("BEVERAGE");
    Category categoryOther = Category.valueOf("OTHER");


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

        subtotal = (float)(Math.round(subtotal*100))/100;
        return subtotal;
    }

    private float calculateDiscount(Order order) {
        this.discount = 0;
        List<OrderItem> foodItems = new ArrayList<OrderItem>();
        List<OrderItem> beverageItems = new ArrayList<OrderItem>();
        int comboCount = 0;
        int i = 0;

        for (OrderItem temp : order.getOrderItems()) {
            if (temp.getItem().getCategory() == categoryFood) {
                foodItems.add(temp);
            }
            else if (temp.getItem().getCategory() == categoryBeverage) {
                beverageItems.add(temp);
            }
        }
        Collections.sort(foodItems);
        Collections.sort(beverageItems);

        comboCount = Math.min(foodItems.size(),beverageItems.size() );

        for( OrderItem temp : foodItems){
            if (i < comboCount){
                discount += (float) (temp.getItem().getPrice()*0.2);
            }
            i++;
        }
        i =0;
        for( OrderItem temp : beverageItems){
            if (i < comboCount){
                discount += (float) (temp.getItem().getPrice()*0.2);
            }
            i++;
        }

        for (OrderItem temp : order.getOrderItems()) {
            if (temp.getItem().getName().equals("Coffee")) {
                for (OrderItem ot : order.getOrderItems()){
                    if ( ot.getItem().getName().equals("Newspaper")){
                        discount +=  (ot.getItem().getPrice());
                        break;
                    }
                }
                break;
            }
        }

        int countJamDoughnut =0;

        for (OrderItem temp : order.getOrderItems()) {
            if(temp.getItem().getName().equals("Jam Doughnut")){
                countJamDoughnut++;
                if (countJamDoughnut>2){
                    discount += temp.getItem().getPrice();
                    break;
                }
            }
        }

        discount = (float)(Math.round(discount*100))/100;
        return discount;
    }

    public String showBill(Order order) {
        billString = "================================\n\n";
        billString += String.format("Customer: %s \n%tc\n\n", order.getCustomerId(), order.getTimestamp());
        billString += "================================\n\n";

        for (OrderItem temp : order.getOrderItems()) {
            billString += String.format("%-25.25s", temp.getItem().getName());
            billString += String.format("£%5.2f\n", temp.getItem().getPrice());
        }

        billString += "--------------------------------\n\n";
        billString += String.format("%-25.25s", "SUBTOTAL");
        billString += String.format("£%5.2f\n", this.subtotal);
        billString += String.format("%-25.25s", "DISCOUNT");
        billString += String.format("£%5.2f\n\n", this.discount);
        billString += "================================\n";
        billString += String.format("%-25.25s", "TOTAL");
        billString += String.format("£%5.2f\n", this.total);
        billString += "================================\n";
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
