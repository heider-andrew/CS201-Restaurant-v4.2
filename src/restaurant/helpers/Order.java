package restaurant.helpers;

import restaurant.CookAgent;
import restaurant.CookAgent.Status;
import restaurant.inter.WaiterInterface;
import restaurant.layoutGUI.Food;

public class Order {
	
public WaiterInterface waiter;
public int tableNum;
public String choice;
public Status status;
public Food food; //a gui variable

/** Constructor for Order class 
 * @param waiter waiter that this order belongs to
 * @param tableNum identification number for the table
 * @param choice type of food to be cooked 
 */
public Order(WaiterInterface waiter, int tableNum, String choice){
    this.waiter = waiter;
    this.choice = choice;
    this.tableNum = tableNum;
    this.status = Status.pending;
}

/** Represents the object as a string */
public String toString(){
    return choice + " for " + waiter ;
}
}