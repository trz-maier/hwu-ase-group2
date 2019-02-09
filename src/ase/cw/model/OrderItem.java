package ase.cw.model;

/**
 * Created by User on 04.02.2019.
 */
public class OrderItem {


    private String customerId;
    private Item item;

    public OrderItem(Item item) {
        this.item=item;

    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Item getItem() {
        return item;
    }
}
