package restaurant.inter;

import restaurant.helpers.Bill;
import restaurant.helpers.Menu;
import restaurant.layoutGUI.GuiCustomer;

public interface CustomerInterface
{

	// *** MESSAGES ***
	/** Sent from GUI to set the customer as hungry */
	public abstract void setHungry();

	/** Waiter sends this message so the customer knows to sit down 
	 * @param waiter the waiter that sent the message
	 * @param menu a reference to a menu */
	public abstract void msgFollowMeToTable(WaiterInterface waiter, Menu menu);

	public abstract void msgHeresYourBill(Bill b);

	/** Waiter sends this message to take the customer's order */
	public abstract void msgDecided();

	/** Waiter sends this message to take the customer's order */
	public abstract void msgWhatWouldYouLike();

	/** Waiter sends this when the food is ready 
	 * @param choice the food that is done cooking for the customer to eat */
	public abstract void msgHereIsYourFood(String choice);

	/** Timer sends this when the customer has finished eating */
	public abstract void msgDoneEating();

	public abstract void msgResturauntFull(HostInterface h);

	public abstract void msgGoStandInLine();

	public abstract void msgTimeToPay(CashierInterface c);

	public abstract void msgGetOuttaMyHouse();

	public abstract void msgRepickFood();

	/** establish connection to host agent. 
	 * @param host reference to the host */
	public abstract void setHost(HostInterface host);

	public abstract void setCashier(CashierInterface cashier);

	/** Returns the customer's name
	 *@return name of customer */
	public abstract String getName();

	/** @return true if the customer is hungry, false otherwise.
	 ** Customer is hungry from time he is created (or button is
	 ** pushed, until he eats and leaves.*/
	public abstract boolean isHungry();

	/** @return the hungerlevel of the customer */
	public abstract int getHungerLevel();

	/** Sets the customer's hungerlevel to a new value
	 * @param hungerLevel the new hungerlevel for the customer */
	public abstract void setHungerLevel(int hungerLevel);

	public abstract GuiCustomer getGuiCustomer();

	/** @return the string representation of the class */
	public abstract String toString();

}