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
        assertEquals(map.size(),4);
        int i=0;
        for(Item item : map.values()){
            if(i==0){
                checkItem(item,"Apple","cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0",5.5,Item.Category.FOOD);
            }
            if(i==3){
                checkItem(item,"Pineapple","8ee08dfc-e299-427c-b097-e60066ef2c56",Float.NaN,Item.Category.FOOD);
            }
            i++;
        }

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

    @Test(expected = IllegalArgumentException.class)
    public void parseItemWrongCategoryFail() throws IOException {
        FileReader.parseItems("ItemsWrongCategory.csv");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parseItemWrongUUIDFail() throws IOException {
        FileReader.parseItems("ItemsWrongUUID.csv");
    }


    @Test(expected = NumberFormatException.class)
    public void parseItemWrongPriceFail() throws IOException {
        FileReader.parseItems("ItemsWrongPrice.csv");
    }



    @Test
    public void parseOrderSuccess() throws IOException, InvalidCustomerIdException, ParseException {
        List<Order> orders = FileReader.parseOrders("OrderSuccess.csv");
        assertEquals(orders.size(),4);
        int i=0;
        for(Order order : orders){
            if(i==0){
                assertEquals(order.getCustomerId(),"00001");
                assertEquals(order.getOrderItems().size(),3);

                //01/01/2000 15:15:15
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = df.parse("01/01/2000 15:15:15");
                assertEquals(date,order.getTimestamp());
                checkItem(order.getOrderItems().get(i).getItem(),"Apple","cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0",5.5,Item.Category.FOOD);
            }
            if(i==3){
                assertEquals(order.getCustomerId(),"00004");
                assertEquals(order.getOrderItems().size(),3);

                //01/01/2000 15:15:15
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = df.parse("01/01/2000 15:15:15");
                assertEquals(date,order.getTimestamp());
                checkItem(order.getOrderItems().get(1).getItem(),"Banana","cdd15ee5-a560-4aa2-be61-fe5ec82ab3b0",5.5,Item.Category.FOOD);

            }

            i++;
        }

    }
}
