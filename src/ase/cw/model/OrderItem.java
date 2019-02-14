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

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	public String toString() {
		return item.toString();
	}

}
