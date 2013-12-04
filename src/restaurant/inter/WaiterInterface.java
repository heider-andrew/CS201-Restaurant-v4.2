package restaurant.inter;

import restaurant.layoutGUI.Food;

public interface WaiterInterface
{

	public abstract void wantBreak(boolean state);

	public abstract void breakPermission(boolean state);

	/** Host sends this to give the waiter a new customer.
	 * @param customer customer who needs seated.
	 * @param tableNum identification number for table */
	public abstract void msgSitCustomerAtTable(CustomerInterface customer,
			int tableNum);

	/** Customer sends this when they are ready.
	 * @param customer customer who is ready to order.
	 */
	public abstract void msgImReadyToOrder(CustomerInterface customer);

	/** Customer sends this when they have decided what they want to eat 
	 * @param customer customer who has decided their choice
	 * @param choice the food item that the customer chose */
	public abstract void msgHereIsMyChoice(CustomerInterface customer,
			String choice);

	/** Cook sends this when the order is ready.
	 * @param tableNum identification number of table whose food is ready
	 * @param f is the guiFood object */
	public abstract void msgOrderIsReady(int tableNum, Food f);

	public abstract void msgOutOfFood(int tableNum);

	/** Customer sends this when they are done eating.
	 * @param customer customer who is leaving the restaurant. */
	public abstract void msgDoneEatingAndLeaving(CustomerInterface customer);

	/** Sent from GUI to control breaks 
	 * @param state true when the waiter should go on break and 
	 *              false when the waiter should go off break 
	 *              Is the name onBreak right? What should it be?*/
	public abstract void setBreakStatus(boolean state);

	/** @return name of waiter */
	public abstract String getName();

	/** @return string representation of waiter */
	public abstract String toString();

	/** Hack to set the cook for the waiter */
	public abstract void setCook(CookInterface cook);

	/** Hack to set the host for the waiter */
	public abstract void setHost(HostInterface host);

	public abstract void setCashier(CashierInterface cashier);

	/** @return true if the waiter is on break, false otherwise */
	public abstract boolean isOnBreak();

}