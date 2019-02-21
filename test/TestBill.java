import ase.cw.model.Item;
import ase.cw.model.Order;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestBill {

    private static Order order1;
    private static Order order2;
    private static Order order3;


    @BeforeClass
    public static void setUp() throws Exception {

        order1 = new Order("00000000");
        order2 = new Order("00000001");
        order3 = new Order("00000002");

        Item item1 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b1"), Item.Category.FOOD, "Jam Doughnut", (float) 1.40);
        Item item2 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b2"), Item.Category.FOOD, "Cheese Sandwich", (float) 2.50);
        Item item3 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b3"), Item.Category.FOOD, "Cherry Pie", (float) 3.10);
        Item item4 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b4"), Item.Category.BEVERAGE, "Coffee", (float) 4);
        Item item5 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b5"), Item.Category.BEVERAGE, "Water", (float) 2);
        Item item6 = new Item(UUID.fromString("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b6"), Item.Category.OTHER, "Newspaper", (float) 1);

        order1.addOrderItem(item1);
        order1.addOrderItem(item1);
        order1.addOrderItem(item1);
        order1.addOrderItem(item4);
        order1.addOrderItem(item6);

        order2.addOrderItem(item1);
        order2.addOrderItem(item4);
        order2.addOrderItem(item4);
        order2.addOrderItem(item6);

        order3.addOrderItem(item4);
        order3.addOrderItem(item6);
        order3.addOrderItem(item6);
        order3.addOrderItem(item1);
        order3.addOrderItem(item1);
        order3.addOrderItem(item5);

    }

    @Test
    public void getSubtotalSuccess() {

        double expected_value1 = 9.2;
        assertEquals(expected_value1, order1.getBill().getSubtotal(), 0.01);

        double expected_value2 = 10.39;
        assertEquals(expected_value2, order2.getBill().getSubtotal(), 0.01);

        double expected_value3 = 10.80;
        assertEquals(expected_value3, order3.getBill().getSubtotal(), 0.01);

    }

    @Test
    public void getDiscountSuccess() {

        double expected_value1 = 3.48;
        assertEquals(expected_value1, order1.getBill().getDiscount(), 0.01);

        double expected_value2 = 2.08;
        assertEquals(expected_value2, order2.getBill().getDiscount(), 0.01);

        double expected_value3 = 2.76;
        assertEquals(expected_value3, order3.getBill().getDiscount(), 0.01);

    }

    @Test
    public void getTotalSuccess() {

        double expected_value1 = 5.72;
        assertEquals(expected_value1, order1.getBill().getTotal(), 0.01);

        double expected_value2 = 8.32;
        assertEquals(expected_value2, order2.getBill().getTotal(), 0.01);

        double expected_value3 = 8.04;
        assertEquals(expected_value3, order3.getBill().getTotal(), 0.01);

    }

}