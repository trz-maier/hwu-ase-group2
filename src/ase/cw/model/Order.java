/**
 * 
 */
package ase.cw.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ram
 *
 */
public class Order {
	private String customerId;
	private Date timestamp;
	private List<OrderItem> orderItems;
	private Bill bill;

	/**
	 * 
	 */
	public Order(String customerId) {
		this(customerId, new Date());
	}

	public Order(String customerId, Date timestamp) {
		this.customerId = customerId;
		this.timestamp = timestamp;
		this.orderItems = new ArrayList<OrderItem>();
	}

	public void addOrderItem(Item item) {
		this.orderItems.add(new OrderItem (item));
	}

	public void removeOrderItem(Item item) {
//		this.orderItems.remove(orderItem);
		for (OrderItem oitem : orderItems) {
			if (oitem.getItem() == item) {
				this.orderItems.remove(oitem);
				break;
			}
		}
	}

	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the orderItems
	 */
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	/**
	 * @return the bill
	 */
	public Bill getBill() {
		return new Bill (this);
	}


}
