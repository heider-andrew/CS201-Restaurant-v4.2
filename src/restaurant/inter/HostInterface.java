package restaurant.inter;

import restaurant.CustomerAgent;

public interface HostInterface
{

	/** Customer sends this message to be added to the wait list 
	 * @param customer customer that wants to be added */
	public abstract void msgIWantToEat(CustomerAgent customer);

	public abstract void msgIAmLeaving(CustomerInterface customer);

	public abstract void msgIWantABreak(WaiterInterface waiter);

	public abstract void msgImBackFromBreak(WaiterInterface waiter);

	/** Waiter sends this message after the customer has left the table 
	 * @param tableNum table identification number */
	public abstract void msgTableIsFree(int tableNum);

	/** Returns the name of the host 
	 * @return name of host */
	public abstract String getName();

	/** Hack to enable the host to know of all possible waiters 
	 * @param waiter new waiter to be added to list
	 */
	public abstract void setWaiter(WaiterInterface waiter);

	//Gautam Nayak - Gui calls this when table is created in animation
	public abstract void addTable();

}