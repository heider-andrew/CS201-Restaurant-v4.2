package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.helpers.Order;
import restaurant.inter.CookInterface;
import restaurant.inter.MarketInterface;
import restaurant.inter.WaiterInterface;
import restaurant.layoutGUI.*;

import java.awt.Color;


/** Cook agent for restaurant.
 *  Keeps a list of orders for waiters
 *  and simulates cooking them.
 *  Interacts with waiters only.
 */
public class CookAgent extends Agent implements CookInterface {

    //List of all the orders
    private List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
    private Map<String,FoodData> inventory = new HashMap<String,FoodData>();
    public enum Status {pending, cooking, done}; // order status
    public Monitor orderRack;
    
    private int currentMarket = 0;
    public MarketInterface[] myMarkets = new MarketInterface[3];
    //Name of the cook
    private String name;

    //Timer for simulation
    Timer timer = new Timer();
    Timer orderCheck = new Timer();
    Restaurant restaurant; //Gui layout

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant, Monitor orderRack) {
	super();

	this.name = name;
	this.restaurant = restaurant;
	this.orderRack = orderRack;
	//Create the restaurant's inventory.
	inventory.put("Steak",new FoodData("Steak", 5, 0));
	inventory.put("Chicken",new FoodData("Chicken", 4, 0));
	inventory.put("Pizza",new FoodData("Pizza", 3, 0));
	inventory.put("Salad",new FoodData("Salad", 2, 0));
    
    }
    /** Private class to store information about food.
     *  Contains the food type, its cooking time, and ...
     */
    private class FoodData {
	String type; //kind of food
	double cookTime;
	int inventory;
	boolean ordered;
	// other things ...
	
	public FoodData(String type, double cookTime, int inventory){
	    this.type = type;
	    this.cookTime = cookTime;
	    this.inventory = inventory;
	    ordered = false;
	}
    }
    /** Private class to store order information.
     *  Contains the waiter, table number, food item,
     *  cooktime and status.
     */


    // *** MESSAGES ***

    /* (non-Javadoc)
	 * @see restaurant.CookInterface#msgHereIsAnOrder(restaurant.WaiterAgent, int, java.lang.String)
	 */
    @Override
	public void msgHereIsAnOrder(WaiterInterface waiter, int tableNum, String choice){
	orders.add(new Order(waiter, tableNum, choice));
	stateChanged();
    }
    
    /* (non-Javadoc)
	 * @see restaurant.CookInterface#foodDelivery(java.lang.String, int)
	 */
    @Override
	public void foodDelivery(String food, int amt){
    	inventory.get(food).inventory += amt;
    	inventory.get(food).ordered = false;
    	print("Got a delivery of " + food);
    	stateChanged();
    }
    
    /* (non-Javadoc)
	 * @see restaurant.CookInterface#cantDeliver(java.lang.String)
	 */
    @Override
	public void cantDeliver(String food){
    	print("Market couldnt get me " + food + ", switching markets");
    	inventory.get(food).ordered = false;
    	currentMarket = (currentMarket + 1) % 3;
    	stateChanged();
    }
    
    //TODO: ADD NOTIFY MESSAGE


    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
	//If there exists an order o whose status is done, place o.
	for(Order o:orders){
	    if(o.status == Status.done){
		placeOrder(o);
		return true;
	    }
	}
	for(String key: inventory.keySet()){
		if(inventory.get(key).inventory == 0 && !inventory.get(key).ordered){
			makeGroceryOrder(key);
			return true;
		}
	}
	//if there is an order on the wheel get it
	if(orderRack.hasObject()){
		orders.add(orderRack.remove());
		return true;
	}
	//If there exists an order o whose status is pending, cook o.
	for(Order o:orders){
	    if(o.status == Status.pending){
	    	if (inventory.get(o.choice).inventory != 0){
	    		cookOrder(o);
	    		return true;
	    	} else {
	    		outOfStock(o);
	    		return true;
	    	}
	    }
	}

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	orderCheck.schedule(new TimerTask(){
	    public void run(){//this routine is like a message reception    
	    	//System.out.println("WAKE UP!");
	    	stateChanged();
		}
	}, 50000);
	
	return false;
    }
    

    // *** ACTIONS ***
    
    /** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(Order order){
    inventory.get(order.choice).inventory -= 1;
	DoCooking(order);
	order.status = Status.cooking;
    }

    /* (non-Javadoc)
	 * @see restaurant.CookInterface#emptyInventory()
	 */
    @Override
	public void emptyInventory(){
    	for(String key: inventory.keySet()){
    		inventory.get(key).inventory = 0;
    	}
    }
    
    private void placeOrder(Order order){
	DoPlacement(order);
	order.waiter.msgOrderIsReady(order.tableNum, order.food);
	orders.remove(order);
    }
    
    private void outOfStock(Order order){
    	print("I dont have food for this order of " + order.choice + ", informing the waiter");
    	order.waiter.msgOutOfFood(order.tableNum);
    	orders.remove(order);
    }
    
    private void makeGroceryOrder(String food){
    	print("Making an order of " + food + " to market " + currentMarket);
    	switch(currentMarket){
	    	case 0:
	    		myMarkets[0].hereIsGroceryOrder(this, food, 10);
	    		break;
	    	case 1:
	    		myMarkets[1].hereIsGroceryOrder(this, food, 10);
	    		break;
	    	case 2:
	    		myMarkets[2].hereIsGroceryOrder(this, food, 10);
	    		break;
	    	default:
	    		currentMarket = 0;
	    		break;
	    }
    	inventory.get(food).ordered = true;
    }


    // *** EXTRA -- all the simulation routines***

    /* (non-Javadoc)
	 * @see restaurant.CookInterface#getName()
	 */
    @Override
	public String getName(){
        return name;
    }

    private void DoCooking(final Order order){
	print("Cooking:" + order + " for table:" + (order.tableNum+1));
	//put it on the grill. gui stuff
	order.food = new Food(order.choice.substring(0,2),new Color(0,255,255), restaurant);
	order.food.cookFood();

	timer.schedule(new TimerTask(){
	    public void run(){//this routine is like a message reception    
		order.status = Status.done;
		stateChanged();
	    }
	}, (int)(inventory.get(order.choice).cookTime*1000));
    }
    /* (non-Javadoc)
	 * @see restaurant.CookInterface#DoPlacement(restaurant.CookAgent.Order)
	 */
    @Override
	public void DoPlacement(Order order){
	print("Order finished: " + order + " for table:" + (order.tableNum+1));
	order.food.placeOnCounter();
    }
}


    
