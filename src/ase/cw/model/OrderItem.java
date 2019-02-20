/**
 *
 */
package ase.cw.model;

/**
 * @author Ram
 *
 */
public class OrderItem {
	private Item item;

	/**
	 *
	 */
	public OrderItem(Item item) {
		this.item = item;
	}

	public String toString(){
		return this.getItem().getName() + String.format(" (£%.2f)",this.getItem().getPrice());
	}
	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}



}
