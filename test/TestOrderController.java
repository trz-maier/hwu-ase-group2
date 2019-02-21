import ase.cw.IO.FileReader;
import ase.cw.control.OrderController;
import ase.cw.exceptions.EmptyOrderException;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.exceptions.NoOrderException;
import ase.cw.model.Item;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class TestOrderController {

    private static OrderController controller;
    private static Map<String, Item> loadedItems;

    /*
     =========================
            PREPARATION
     =========================
     */

    @BeforeClass
    public static void setUpOnce() throws IOException {
        controller = new OrderController();
        loadedItems = FileReader.parseItems("Items.csv");
    }

    @After
    public void tearDown() {
        controller.cancelPendingOrder();
    }

    // rule for testing exception messages
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /*
     =========================
            UNIT TESTS
     =========================
     */

    @Test
    public void addItemToPendingOrderSuccess() throws InvalidCustomerIdException, NoOrderException {
        controller.createNewOrder("abcdef88");
        controller.addItemToPendingOrder(loadedItems.entrySet().iterator().next().getValue());
        assertEquals("Method finalized", "Method finalized");
    }

    @Test (expected = NoOrderException.class)
    public void addItemToPendingOrderFailNoOrder() throws NoOrderException {
        controller.addItemToPendingOrder(loadedItems.entrySet().iterator().next().getValue());
        fail("No Exception was thrown");
    }

    @Test (expected = IllegalArgumentException.class)
    public void addItemToPendingOrderFailNullItem() throws NoOrderException, InvalidCustomerIdException {
        controller.createNewOrder("abcdef88");
        controller.addItemToPendingOrder(null);
        fail("No Exception was thrown");
    }

    @Test
    public void createNewOrderSuccess() throws InvalidCustomerIdException {
        controller.createNewOrder("abcabc12");
        assertEquals("Method finalized", "Method finalized");
    }

    @Test (expected = IllegalStateException.class)
    public void createNewOrderFailExists() throws InvalidCustomerIdException {
        controller.createNewOrder("abcabc12");
        controller.createNewOrder("abcabc13");
        // should only reach this point if no exception is thrown
        fail("No exception was thrown");
    }

    @Test
    public void finalizePendingOrderSuccess() throws InvalidCustomerIdException, NoOrderException, EmptyOrderException {
        controller.createNewOrder("azaz2424");
        controller.addItemToPendingOrder(loadedItems.entrySet().iterator().next().getValue());
        controller.finalizePendingOrder();
        assertEquals("Method finalized", "Method finalized");
    }

    @Test (expected = NoOrderException.class)
    public void finalizePendingOrderFailNoOrder() throws NoOrderException, EmptyOrderException {
        controller.finalizePendingOrder();
        fail("No exception was thrown");
    }

    @Test (expected = EmptyOrderException.class)
    public void finalizePendingOrderFailEmpty() throws InvalidCustomerIdException, NoOrderException, EmptyOrderException {
        controller.createNewOrder("azaz2424");
        controller.finalizePendingOrder();
        fail("No exception was thrown");
    }

    @Test
    public void validateCustomerIdSuccess() throws InvalidCustomerIdException {
        OrderController.validateCustomerId("abdDef12");
        OrderController.validateCustomerId("2bdDef12");
        OrderController.validateCustomerId("2bdDef1D");
        OrderController.validateCustomerId("aaaaaaaa");
        OrderController.validateCustomerId("BBOASDBT");
        OrderController.validateCustomerId("12312305");
        // should only reach this point if no exception is thrown
        assertEquals("Method finalized", "Method finalized");
    }

    @Test
    public void validateCustomerFailHashtag() throws InvalidCustomerIdException {
        thrown.expect(InvalidCustomerIdException.class);
        thrown.expectMessage("Customer ID \"abdDef1#\" contains invalid character '#'");

        OrderController.validateCustomerId("abdDef1#");
    }

    @Test
    public void validateCustomerFailUnderscore() throws InvalidCustomerIdException {
        thrown.expect(InvalidCustomerIdException.class);
        thrown.expectMessage("Customer ID \"abd_ef10\" contains invalid character '_'");

        OrderController.validateCustomerId("abd_ef10");
    }

    @Test
    public void validateCustomerFailShort() throws InvalidCustomerIdException {
        thrown.expect(InvalidCustomerIdException.class);
        thrown.expectMessage("Customer ID \"ab10\" should have a length of 8, found 4");

        OrderController.validateCustomerId("ab10");
    }

    @Test
    public void validateCustomerFailLong() throws InvalidCustomerIdException {
        thrown.expect(InvalidCustomerIdException.class);
        thrown.expectMessage("Customer ID \"ab10ab20365\" should have a length of 8, found 11");

        OrderController.validateCustomerId("ab10ab20365");
    }

    @Test
    public void validateCustomerFailNull() throws InvalidCustomerIdException {
        thrown.expect(InvalidCustomerIdException.class);
        thrown.expectMessage("Customer ID is null");

        OrderController.validateCustomerId(null);
    }

}