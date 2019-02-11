/**
 * 
 */
package ase.cw.model;

import java.util.UUID;


/**
 * @author Ram
 *
 */
public class Item {
	private UUID id;
	private Category category;
	private String name;
	private float price;
	
	public enum Category {FOOD, BEVERAGE}

	public Item(UUID id, Category category, String name, float price) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;

	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}
	
}
