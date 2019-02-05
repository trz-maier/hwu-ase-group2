package ase.cw.model;

import java.util.UUID;

/**
 * Created by User on 04.02.2019.
 */
public class Item {
    private UUID id;
    private String name;
    private float price;

    public enum Category {
        FOOD, NO_FOOD, ETC,
    }

    private Category category;


    public Item(UUID id, Category category, String name, float price) {

    }
}
