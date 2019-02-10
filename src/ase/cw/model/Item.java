package ase.cw.model;

import java.util.UUID;

/**
 * Created by User on 04.02.2019.
 */
public class Item {
    private UUID id;
    private String name;
    private float price;

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public UUID getId() {
        return id;
    }

    public enum Category {
        FOOD, NO_FOOD, ETC,
    }

    private Category category;


    public Item(UUID id, Category category, String name, float price) {
        this.id=id;
        this.category=category;
        this.price=price;
        this.name=name;

    }
    public Category getCategory(){
        return category;
    }
}
