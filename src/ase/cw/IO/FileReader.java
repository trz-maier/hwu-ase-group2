package ase.cw.IO;

import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.model.Item;
import ase.cw.model.Item.Category;
import ase.cw.model.Order;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Thomas on 04.02.2019.
 */
public class FileReader {
    /**
     *
     * @param filename the filename inside the res path.
     * @return a list of all parsed orders
     * @throws IOException if the filename is not correct
     */
    public static List<Order> parseOrders(String filename) throws IOException{
        File file = parseFileName(filename);
        List<Order> orderList = new ArrayList<Order>();
        BufferedReader br = new BufferedReader(new java.io.FileReader(file));
        Scanner allOrderScanner=null;
        try {
            allOrderScanner= new Scanner(br);

            //add every order in list
            while(allOrderScanner.hasNextLine()) {
                try {
                    parseSingleOrder(allOrderScanner, orderList);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } finally {
            if(allOrderScanner!=null) {
                allOrderScanner.close();
            }

        }
        return orderList;
    }

    /*
     * Create a single order object and add it to orderList
     */

    /**
     *
     * @param allOrderScanner
     * @param orderList
     * @throws InvalidCustomerIdException
     * @throws ParseException
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    private static void parseSingleOrder(Scanner allOrderScanner, List<Order> orderList) throws InvalidCustomerIdException,ParseException,IllegalArgumentException{

        Scanner singleOrderScanner = null;
        String customerId="";
        String dateStr = "";

        try {
            singleOrderScanner = new Scanner(allOrderScanner.nextLine());
            singleOrderScanner.useDelimiter(",");
            while (singleOrderScanner.hasNext()) {
                //A Order is saved in the following format: CustomerId,date,Item1,Item2,...ItemN
                //Where evey item has the following format: UUID,NAME,CATEGORY,Price

                customerId = singleOrderScanner.next();
                dateStr = singleOrderScanner.next();
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = df.parse(dateStr);

                Order order = new Order(customerId,date);
                while(singleOrderScanner.hasNext()){
                    String nextLineItem = "";
                    nextLineItem = nextLineItem + singleOrderScanner.next()+",";
                    nextLineItem = nextLineItem + singleOrderScanner.next()+",";
                    nextLineItem = nextLineItem + singleOrderScanner.next()+",";
                    nextLineItem = nextLineItem + singleOrderScanner.next()+",";
                    order.addOrderItem(parseSingleItem(nextLineItem));
                }
                orderList.add(order);
            }
        }  finally {
            if (singleOrderScanner != null) {
                singleOrderScanner.close();
            }
        }
    }

    /**
     *
     * @param filename the filename inside the res path.
     * @return a list of all items
     * @throws IOException if the filename is not correct
     * @throws NoSuchElementException if a parameter for a Item is missing
     * @throws NumberFormatException if the price is not correctly formatted
     * @throws IllegalArgumentException if UUID,Category is not correctly formatted
     */
    public static TreeMap<String, Item> parseItems(String filename) throws IOException {
        File file = parseFileName(filename);
        TreeMap<String,Item> items = new TreeMap<String,Item>();
        BufferedReader br = new BufferedReader(new java.io.FileReader(file));
        Scanner allItemsScanner=null;
        try {
            allItemsScanner= new Scanner(br);
            allItemsScanner.useLocale(Locale.UK);
            //add every order to map

            while(allItemsScanner.hasNextLine()) {
                try {
                    String itemString = allItemsScanner.nextLine();
                    Item item = parseSingleItem(itemString);
                    if (item != null) {
                        items.put(item.getName(), item);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } finally {
            if(allItemsScanner!=null) {
                allItemsScanner.close();
            }
        }
        return items;
    }

    private static Item parseSingleItem(String itemString) throws NumberFormatException,IllegalArgumentException{
        if(itemString==null || itemString.equals(""))return null;
        Scanner singleItemScanner = null;
        try {
            singleItemScanner = new Scanner(itemString);
            singleItemScanner.useDelimiter(",");
             //A Item is saved in the following format: UUID,NAME,CATEGORY,Price
                UUID uuid = parseUUID(singleItemScanner);
                String name = singleItemScanner.next();
                Category category = parseCategory(singleItemScanner);
                float price = parsePrice(singleItemScanner);
                 Item item = new Item(uuid, category, name, price);
                return item;
        } finally {
            if (singleItemScanner != null) {
                singleItemScanner.close();
            }
        }
    }

    private static float parsePrice(Scanner singleItemScanner) throws NumberFormatException {
        //Parse the Category via the scanner
        String next = singleItemScanner.next();
        return Float.parseFloat(next);
    }

    private static Category parseCategory(Scanner scanner) throws IllegalArgumentException{
        //Parse the Category via the scanner
        Category category;             //A Item is saved in the following format: UUID,NAME,CATEGORY,Price

        String categoryString = scanner.next();
        try{
            category = Category.valueOf(categoryString);
            return category;
        } catch(IllegalArgumentException illegalArgumentException){
            throw new IllegalArgumentException("The category="+categoryString+" does not exist",illegalArgumentException);
        }
    }

    private static UUID parseUUID(Scanner scanner) throws IllegalArgumentException,NoSuchElementException{
        //Parse the UUID via the scanner
        String uuidString="";
        try {
            uuidString = scanner.next();
            UUID uuid = UUID.fromString(uuidString);
            return uuid;
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException("The UUID="+uuidString+" is not correctly formatted",e);
        }
    }

    /**
     * @param filename within the internal resources folder
     * @return the new created file Object
     * @throws InvalidParameterException if filename is not valid
     */
    private static File parseFileName(String filename) throws IllegalArgumentException, IOException {
        //Get file from resources folder
        ClassLoader classLoader = FileReader.class.getClassLoader();
        File file=null;
        java.net.URL url =classLoader.getResource(filename);
        if(url ==null)throw new FileNotFoundException("File="+filename+" does not exist");
        file = new File(classLoader.getResource(filename).getFile());
        return file;
    }
}