package restaurant.inter;

import restaurant.helpers.Order;

public interface CookInterface
{

	/** Message from a waiter giving the cook a new order.
	 * @param waiter waiter that the order belongs to
	 * @param tableNum identification number for the table
	 * @param choice type of food to be cooked
	 */
	public abstract void msgHereIsAnOrder(WaiterInterface waiter, int tableNum,
			String choice);

	public abstract void foodDelivery(String food, int amt);

	public abstract void cantDeliver(String food);

	public abstract void emptyInventory();

	/** Returns the name of the cook */
	public abstract String getName();

	public abstract void DoPlacement(Order order);

}