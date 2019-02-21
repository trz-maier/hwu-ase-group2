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

	public enum Category {FOOD, BEVERAGE, OTHER}

	public Item(UUID id, Category category, String name, float price) {
		this.id = id;
		this.category = category;
		this.name = name;
		this.price = price;

	}

	public String toString(){
		return this.name + String.format(" (£%.2f)", this.getPrice());
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


	public void setCategory(Category category) {
		this.category = category;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(float price) {
		this.price = price;
	}
}