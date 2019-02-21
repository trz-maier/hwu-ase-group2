import ase.cw.IO.FileReader;
import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.model.Item;
import ase.cw.model.Order;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by User on 09.02.2019.
 */
public class TestFileReader {

    @Test
    public void parseItemsSuccess() throws IOException {
        TreeMap<String, Item> map;
        map = FileReader.parseItems("ItemsSuccess.csv");
        assertEquals(map.size(),23);
        map.containsKey("cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0");

    }


    @Test(expected=FileNotFoundException.class)
    public void parseItemFileIsDictFail() throws IOException {
        FileReader.parseItems("folder");
    }

    @Test(expected=NullPointerException.class)
    public void parseNullFileFail() throws IOException {
        FileReader.parseItems(null);
    }
    @Test(expected= FileNotFoundException.class)
    public void parseZeroLengthFileFail() throws IOException {
        FileReader.parseItems("");
    }

    private void checkItem(Item item, String name, String uuid, double price, Item.Category cat) {
        assertEquals(item.getName(),name);
        assertEquals(item.getId(),UUID.fromString(uuid));
        assertEquals(item.getPrice(),price,0.000000001);
        assertEquals(item.getCategory(), cat);
    }

    @Test
    public void parseEmptyItemsFileSuccess() throws IOException {
        TreeMap<String, Item> map;
        map = FileReader.parseItems("ItemsEmpth.csv");
        assertEquals(map.size(),0);
    }

    @Test
    public void parseItemManySeperatorsPass() throws IOException {
        TreeMap<String, Item> map = FileReader.parseItems("ItemsManySeperators.csv");
        assertEquals(1,map.size());
    }

    @Test
    public void parseItemWrongCategoryFail() throws IOException {
        TreeMap<String, Item> map = FileReader.parseItems("ItemsWrongCategory.csv");
        assertEquals(0,map.size());

    }


    @Test
    public void parseItemWrongUUIDFail() throws IOException {
        TreeMap<String, Item> map = FileReader.parseItems("ItemsWrongUUID.csv");
        assertEquals(0,map.size());

    }

    @Test
    public void parseItemWrongPriceFail() throws IOException {
        TreeMap<String, Item> map = FileReader.parseItems("ItemsWrongPrice.csv");
        assertEquals(map.size(),1);
    }



    @Test
    public void parseOrderSuccess() throws IOException, InvalidCustomerIdException, ParseException {
        List<Order> orders = FileReader.parseOrders("OrderSuccess.csv");
        assertEquals(orders.size(),5);
        FileReader.parseItems("ItemsSuccess.csv");
        int i=0;
        for(Order order : orders){
            if(i==0){
                assertEquals(order.getCustomerId(),"abc00001");
                assertEquals(order.getOrderItems().size(),3);

                //01/01/2000 15:15:15
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = df.parse("01/01/2000 15:15:15");
                assertEquals(date,order.getTimestamp());
                checkItem(order.getOrderItems().get(i).getItem(),"Apple","cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0",5.5,Item.Category.FOOD);
            }
            if(i==3){
                assertEquals(order.getCustomerId(),"abc00002");
                assertEquals(order.getOrderItems().size(),1);

                //01/01/2000 15:15:15
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = df.parse("12/01/2000 20:15:15");
                assertEquals(date,order.getTimestamp());
                checkItem(order.getOrderItems().get(0).getItem(),"Apple","cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0",5.5,Item.Category.FOOD);

            }

            i++;
        }

    }
}
