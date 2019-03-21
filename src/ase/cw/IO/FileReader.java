package ase.cw.IO;

import ase.cw.exceptions.InvalidCustomerIdException;
import ase.cw.log.Log;
import ase.cw.model.Item;
import ase.cw.model.Item.Category;
import ase.cw.model.Order;

import java.io.*;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Thomas on 04.02.2019.
 */
public class FileReader {

    private static final Log LOGGER = Log.getLogger();
    private static HashMap<UUID, Item> itemMap = new HashMap<>();

    /**
     * Create a orderlist out of a file.
     * If an order in the file is invalid, it is ignored.
     *
     * @param filename the filename inside the res path.
     * @return a list of all parsed orders
     * @throws IOException if the filename is not correct
     */
    public static Vector<Order> parseOrders(String filename) throws IOException {
        File file = parseFileName(filename);
        Vector<Order> orderList = new Vector<>();
    //    InputStream in = FileReader.class.getClass().getResourceAsStream("res/"+filename);
       URL url =  FileReader.class.getClassLoader().getResource(filename);
        InputStreamReader inputStream = new InputStreamReader(url.openStream());
        BufferedReader br = new BufferedReader(inputStream);

     //   BufferedReader br = new BufferedReader(new java.io.FileReader(file));
        Scanner allOrderScanner = null;

        try {
            allOrderScanner = new Scanner(br);

            //add every order in list
            while (allOrderScanner.hasNextLine()) {
                String nextOrder = "";
                try {
                    nextOrder = allOrderScanner.nextLine();
                    parseSingleOrder(nextOrder, orderList);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!"".equals(nextOrder) && !nextOrder.startsWith("//"))
                        LOGGER.log("Invalid Order=" + nextOrder + " skip order");
                }
            }
        } finally {
            if (allOrderScanner != null) {
                allOrderScanner.close();
            }

        }

        return orderList;
    }

    /*
     * Create a single order object and add it to orderList
     */

    /**
     * @param orderString a String which represents a order
     * @param orderList   the list where the new order should be added
     * @throws InvalidCustomerIdException if customerID is wrong
     * @throws NumberFormatException      if price is wrong
     * @throws ParseException             if the orderString has to many/few items
     * @throws IllegalArgumentException
     */
    private static void parseSingleOrder(String orderString, List<Order> orderList) throws InvalidCustomerIdException
            , IllegalArgumentException {

        Scanner singleOrderScanner = null;
        String customerId = "";

        try {
            singleOrderScanner = new Scanner(orderString);
            singleOrderScanner.useDelimiter(",");
            while (singleOrderScanner.hasNext()) {
                //A Order is saved in the following format: CustomerId,date,Item1,Item2,...ItemN
                //Where evey item has the following format: UUID,NAME,CATEGORY,Price

                customerId = singleOrderScanner.next();
                UUID uuid = parseUUID(singleOrderScanner);
                Item item = getItemById(uuid);
                boolean isPriorityOrder = (singleOrderScanner.next().equals("Y")) ? true : false;
                Order order = new Order(customerId, isPriorityOrder);

                //If we already created this specific order, add the Item to the existing order.
                //Else create a new order and add the item to the new order.
                if (orderList.contains(order)) {
                    int index = orderList.indexOf(order);
                    Order currentOrder = orderList.get(index);
                    currentOrder.addOrderItem(item);
                } else {
                    order.addOrderItem(item);
                    orderList.add(order);
                }
            }
        } finally {
            if (singleOrderScanner != null) {
                singleOrderScanner.close();
            }
        }
    }

    private static Item getItemById(UUID uuid) {
        Item item;
        //Check if we already created an item with the given UUID
        if (itemMap.containsKey(uuid)) {
            item = itemMap.get(uuid);
        } else {

            item = new Item(uuid, Category.OTHER, "", 0);
            itemMap.put(uuid, item);
        }
        return item;
    }

    /**
     * Create a treemap of Items out of a file.
     * If an item in the file is invalid, it is ignored.
     *
     * @param filename the filename inside the res path.
     * @return a list of all items
     * @throws IOException              if the filename is not correct
     * @throws NoSuchElementException   if a parameter for an Item is missing
     * @throws NumberFormatException    if the price is not correctly formatted
     * @throws IllegalArgumentException if UUID,Category is not correctly formatted
     */
    public static TreeMap<String, Item> parseItems(String filename) throws IOException {
        TreeMap<String, Item> items = new TreeMap<String, Item>();


        URL url =  FileReader.class.getClassLoader().getResource(filename);
        InputStreamReader inputStream = new InputStreamReader(url.openStream());
        BufferedReader br = new BufferedReader(inputStream);

        Scanner allItemsScanner = null;
        try {
            allItemsScanner = new Scanner(br);
            allItemsScanner.useLocale(Locale.UK);
            //add every order to map

            while (allItemsScanner.hasNextLine()) {
                String itemString = "";
                try {
                    itemString = allItemsScanner.nextLine();
                    Item item = parseSingleItem(itemString);
                    if (item != null) {
                        items.put(item.getName() + item.getId().toString(), item);
                    }
                } catch (Exception e) {
                    //Skip invalid Items
                    if (!"".equals(itemString)) System.out.println("Invalid Item=" + itemString + " skip item");
                }
            }
        } finally {
            if (allItemsScanner != null) {
                allItemsScanner.close();
            }
        }
        return items;
    }

    private static Item parseSingleItem(String itemString) throws NumberFormatException, IllegalArgumentException {
        if (itemString == null || itemString.equals("")) return null;
        Scanner singleItemScanner = null;
        try {
            singleItemScanner = new Scanner(itemString);
            singleItemScanner.useDelimiter(",");
            //An Item is saved in the following format: UUID,NAME,CATEGORY,Price
            UUID uuid = parseUUID(singleItemScanner);
            String name = singleItemScanner.next();
            Category category = parseCategory(singleItemScanner);
            float price = parsePrice(singleItemScanner);
            Item item = getItemById(uuid);
            item.setName(name);
            item.setCategory(category);
            item.setPrice(price);
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

    private static Category parseCategory(Scanner scanner) throws IllegalArgumentException {
        //Parse the Category via the scanner
        Category category;

        String categoryString = scanner.next();
        try {
            category = Category.valueOf(categoryString);
            return category;
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalArgumentException("The category=" + categoryString + " does not exist",
                    illegalArgumentException);
        }
    }

    private static UUID parseUUID(Scanner scanner) throws IllegalArgumentException, NoSuchElementException {
        //Parse the UUID via the scanner
        String uuidString = "";
        try {
            uuidString = scanner.next();
            UUID uuid = UUID.fromString(uuidString);
            return uuid;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The UUID=" + uuidString + " is not correctly formatted", e);
        }
    }

    /**
     * @param filename within the internal resources folder
     * @return the new created file Object
     * @throws InvalidParameterException if filename is not valid
     */
    private static File parseFileName(String filename) throws IllegalArgumentException, IOException {
        //Get file from resources folder
    /*    ClassLoader classLoader = FileReader.class.getClassLoader();
        File file = null;

        java.net.URL url = classLoader.getResource(filename);
        if (url == null) throw new FileNotFoundException("File=" + filename + " does not exist");
        file = new File(classLoader.getResource(filename).getFile());
        return file;*/
    return null;
    }
}