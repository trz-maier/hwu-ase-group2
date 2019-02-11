package ase.cw.IO;

import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.model.Item;
import ase.cw.model.Item.Category;
import ase.cw.model.Order;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by User on 04.02.2019.
 */
public class FileReader {
    /**
     * Read the file and create a new List with all Orders.
     * @param filename
     * @return
     * @throws IOException
     * @throws InvalidCustomerIdException
     * @throws ParseException
     */
    public static List<Order> parseOrders(String filename) throws IOException,NoSuchElementException {
        File file = parseFileName(filename);
        List<Order> orderList = new ArrayList<Order>();
        BufferedReader br = new BufferedReader(new java.io.FileReader(file));
        Scanner allOrderScanner=null;
        try {
            allOrderScanner= new Scanner(br);

            //add every order in list
            while(allOrderScanner.hasNextLine()) {
                parseSingleOrder(allOrderScanner, orderList);
            }
        } finally {
            if(allOrderScanner!=null) {
                allOrderScanner.close();
            }

        }
        return orderList;
    }

    /**
     * Create a single order object and add it to orderList
     * @param allOrderScanner
     * @param orderList
     * @throws InvalidCustomerIdException
     * @throws ParseException
     */
    private static void parseSingleOrder(Scanner allOrderScanner, List<Order> orderList) throws NoSuchElementException{

        Scanner singleOrderScanner = null;
        String customerId="";
        String dateStr = "";

        try {
            singleOrderScanner = new Scanner(allOrderScanner.nextLine());
            singleOrderScanner.useDelimiter(",");
            while (singleOrderScanner.hasNext()) {
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
        } catch (InvalidCustomerIdException e) {
            throw new InputMismatchException("Invalid CustomerID="+customerId);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new InputMismatchException("Invalid Date string="+dateStr);

        } finally {
            if (singleOrderScanner != null) {
                singleOrderScanner.close();
            }
        }
    }

    /**
     * Read the file and create a new TreeMap with all Items.
     * @param filename
     * @return
     * @throws IOException
     */
    public static TreeMap<String, Item> parseItems(String filename) throws IOException,NoSuchElementException {
        File file = parseFileName(filename);
        TreeMap<String,Item> items = new TreeMap<String,Item>();
        BufferedReader br = new BufferedReader(new java.io.FileReader(file));
        Scanner allItemsScanner=null;
        try {
            allItemsScanner= new Scanner(br);
            allItemsScanner.useLocale(Locale.UK);
            //add every order to map

            while(allItemsScanner.hasNextLine()) {
                Item item = parseSingleItem(allItemsScanner.next());
                items.put(item.getName(),item);
            }
        } finally {
            if(allItemsScanner!=null) {
                allItemsScanner.close();
            }
        }
        return items;
    }

    private static Item parseSingleItem(String itemString) throws NoSuchElementException{
        Scanner singleItemScanner = null;
        try {
            singleItemScanner = new Scanner(itemString);
            singleItemScanner.useDelimiter(",");
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

    private static float parsePrice(Scanner singleItemScanner) throws NoSuchElementException {
        //Parse the Category via the scanner
        String next = singleItemScanner.next();
        try {
            return Float.parseFloat(next);
        } catch(NumberFormatException e){
            throw new InputMismatchException();
        }
    }

    private static Category parseCategory(Scanner scanner) {
        //Parse the Category via the scanner
        Category category;
        String categoryString = scanner.next();
        try{
            category = Category.valueOf(categoryString);
            return category;
        } catch(IllegalArgumentException illegalArgumentException){
            throw new InputMismatchException("The category="+categoryString+" does not exist");
        }
    }

    private static UUID parseUUID(Scanner scanner) {
        //Parse the UUID via the scanner
        String uuidString="";
        try {
            uuidString = scanner.next();
            UUID uuid = UUID.fromString(uuidString);
            return uuid;
        } catch(NoSuchElementException e){
            throw new NoSuchElementException("No UUID is given="+scanner.nextLine());
        } catch(IllegalArgumentException e){
            throw new InputMismatchException("The UUID="+uuidString+" is not correctly formatted");
        }
    }

    /**
     * @param filename within the internal resources folder
     * @return the new created file Object
     * @throws InvalidParameterException if filename is not valid
     */
    private static File parseFileName(String filename) throws InvalidParameterException{
        if(filename ==null){
            throw new InvalidParameterException("File is null");
        }
        if(filename.equals("")){
            throw new InvalidParameterException("Empthy string");
        }
        //Get file from resources folder
        ClassLoader classLoader = FileReader.class.getClassLoader();
        File file=null;
        try {
            file = new File(classLoader.getResource(filename).getFile());
        } catch(NullPointerException e){
            throw new InvalidParameterException("File="+filename +"does not exist");
        }
        return file;
    }
}