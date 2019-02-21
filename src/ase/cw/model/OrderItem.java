/**
 *
 */
package ase.cw.model;

/**
 * @author Ram
 *
 */
public class OrderItem implements Comparable<OrderItem>{
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

	public int compareTo(OrderItem o){
		if (this.getItem().getPrice() == o.getItem().getPrice()) return 0;
		else if (this.getItem().getPrice()> o.getItem().getPrice()) return 1;
		else return -1;

	}


}
