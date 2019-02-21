import ase.cw.model.Item;
import ase.cw.model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class TestBill {

    private static Order order;

    @Before
    public void setUp() throws Exception {

        order = new Order("00000000");

        Item item1 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0"), Item.Category.FOOD, "Sandwich", (float) 1);
        Item item2 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0"), Item.Category.FOOD, "Salad", (float) 2);
        Item item3 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0"), Item.Category.FOOD, "Pie", (float) 3);
        Item item4 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0"), Item.Category.BEVERAGE, "Coffee", (float) 4);
        Item item5 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0"), Item.Category.BEVERAGE, "Coffee", (float) 4);
        Item item6 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0"), Item.Category.OTHER, "Newspaper", (float) 5);

        order.addOrderItem(item1);
        order.addOrderItem(item2);
        order.addOrderItem(item3);
        order.addOrderItem(item4);
        order.addOrderItem(item5);
        order.addOrderItem(item6);
    }

    @Test
    public void getSubtotalSuccess() {

        double expected_value = 15;
        assertEquals(expected_value, order.getBill().getSubtotal(), 0);

    }

    @Test
    public void getDiscountSuccess() {

        double expected_value = 15;
        assertEquals(expected_value, order.getBill().getDiscount(), 0);

    }

    @Test
    public void getTotalSuccess() {

        double expected_value = 15;
        assertEquals(expected_value, order.getBill().getTotal(), 0);

    }

}