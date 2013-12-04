package restaurant;
import java.awt.Color;
import restaurant.gui.RestaurantGui;
import restaurant.helpers.Bill;
import restaurant.helpers.Menu;
import restaurant.helpers.Order;
import restaurant.inter.CashierInterface;
import restaurant.inter.CookInterface;
import restaurant.inter.CustomerInterface;
import restaurant.inter.HostInterface;
import restaurant.inter.WaiterInterface;
import restaurant.layoutGUI.*;
import agent.Agent;
import java.util.Timer;
import java.util.TimerTask;
import astar.*;
import java.util.*;
import java.util.concurrent.Semaphore;

/** Restaurant Waiter Agent.
 * Sits customers at assigned tables and takes their orders.
 * Takes the orders to the cook and then returns them 
 * when the food is done.  Cleans up the tables after the customers leave.
 * Interacts with customers, host, and cook */
public class SharedWaiter extends Agent implements WaiterInterface {

	//State variables for Waiter
	private boolean onBreak = false;
	private boolean askBreak = false;
	private boolean wantBreak = false;

	//State constants for Customers

	public enum CustomerState 
	{NEED_SEATED,READY_TO_ORDER,ORDER_PENDING,ORDER_READY,IS_DONE,NO_ACTION};

	Timer timer = new Timer();

	/** Private class to hold information for each customer.
	 * Contains a reference to the customer, his choice, 
	 * table number, and state */
	private class MyCustomer {
		public CustomerState state;
		public CustomerInterface cmr;
		public String choice;
		public int tableNum;
		public Food food; //gui thing

		/** Constructor for MyCustomer class.
		 * @param cmr reference to customer
		 * @param num assigned table number */
		public MyCustomer(CustomerInterface cmr, int num){
			this.cmr = cmr;
			tableNum = num;
			state = CustomerState.NO_ACTION;
		}
	}

	//Name of waiter
	private String name;

	//All the customers that this waiter is serving
	private List<MyCustomer> customers = new ArrayList<MyCustomer>();

	private HostInterface host;
	private CookInterface cook;
	private CashierInterface cashier;
	private Menu theMenu = new Menu();
	Monitor orderRack;
	//Animation Variables
	AStarTraversal aStar;
	Restaurant restaurant; //the gui layout
	GuiWaiter guiWaiter; 
	Position currentPosition; 
	Position originalPosition;
	Table[] tables; //the gui tables

	Semaphore action = new Semaphore(1,true);

	/** Constructor for SharedWaiter class
	 * @param name name of waiter
	 * @param gui reference to the gui */
	public SharedWaiter(String name, AStarTraversal aStar,
			Restaurant restaurant, Table[] tables, Monitor orderRack) {
		super();

		this.name = name;
		this.orderRack = orderRack;
		//initialize all the animation objects
		this.aStar = aStar;
		this.restaurant = restaurant;//the layout for astar
		guiWaiter = new GuiWaiter(name.substring(0,2), new Color(255, 0, 0), restaurant);
		currentPosition = new Position(guiWaiter.getX(), guiWaiter.getY());
		currentPosition.moveInto(aStar.getGrid());
		originalPosition = currentPosition;//save this for moving into
		this.tables = tables;
	} 

	// *** MESSAGES ***

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#wantBreak(boolean)
	 */
	@Override
	public void wantBreak(boolean state){
		wantBreak = state;

		if (!wantBreak){
			if (onBreak){
				host.msgImBackFromBreak(this);
			}
			this.setBreakStatus(false);
			askBreak = false;
			print(name + " doesnt want to be on break");
		} else {
			host.msgIWantABreak(this);
			print(name + " is trying to go on break");
		}
		stateChanged();
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#breakPermission(boolean)
	 */
	@Override
	public void breakPermission(boolean state){
		askBreak = state;

		if (askBreak){
			print(name + " has permission to take his break");
		} else {
			wantBreak(false);
			print(name + " doesnt get a break");
		}
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#msgSitCustomerAtTable(resturaunt.inter.CustomerInterface, int)
	 */
	@Override
	public void msgSitCustomerAtTable(CustomerInterface customer, int tableNum){
		MyCustomer c = new MyCustomer(customer, tableNum);
		c.state = CustomerState.NEED_SEATED;
		customers.add(c);
		stateChanged();
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#msgImReadyToOrder(resturaunt.inter.CustomerInterface)
	 */
	@Override
	public void msgImReadyToOrder(CustomerInterface customer){
		//print("received msgImReadyToOrder from:"+customer);
		try
		{
			action.acquire();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		for(int i=0; i < customers.size(); i++){
			//if(customers.get(i).cmr.equals(customer)){
			if (customers.get(i).cmr == customer){
				customers.get(i).state = CustomerState.READY_TO_ORDER;
				stateChanged();
				return;
			}
		}
		System.out.println("msgImReadyToOrder in SharedWaiter, didn't find him?");
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#msgHereIsMyChoice(resturaunt.inter.CustomerInterface, java.lang.String)
	 */
	@Override
	public void msgHereIsMyChoice(CustomerInterface customer, String choice){
		action.release();
		for(MyCustomer c:customers){
			if(c.cmr.equals(customer)){
				c.choice = choice;
				c.state = CustomerState.ORDER_PENDING;
				stateChanged();
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#msgOrderIsReady(int, restaurant.layoutGUI.Food)
	 */
	@Override
	public void msgOrderIsReady(int tableNum, Food f){
		for(MyCustomer c:customers){
			if(c.tableNum == tableNum){
				c.state = CustomerState.ORDER_READY;
				c.food = f; //so that later we can remove it from the table.
				stateChanged();
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#msgOutOfFood(int)
	 */
	@Override
	public void msgOutOfFood(int tableNum){
		for(MyCustomer c:customers){
			if(c.tableNum == tableNum){
				print(c + "'s order was bad, making him repick");
				c.cmr.msgRepickFood();
			}
		}
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#msgDoneEatingAndLeaving(resturaunt.inter.CustomerInterface)
	 */
	@Override
	public void msgDoneEatingAndLeaving(CustomerInterface customer){
		for(MyCustomer c:customers){
			if(c.cmr.equals(customer)){
				c.state = CustomerState.IS_DONE;
				stateChanged();
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#setBreakStatus(boolean)
	 */
	@Override
	public void setBreakStatus(boolean state){
		onBreak = state;
		if (onBreak){
			print(name + " is going on break!");
		} else {
			print(name + " is going back to work");
		}
		stateChanged();
	}



	/** Scheduler.  Determine what action is called for, and do it. */
	protected boolean pickAndExecuteAnAction() {
		//print("in waiter scheduler");

		//Runs through the customers for each rule, so 
		//the waiter doesn't serve only one customer at a time
		if(!customers.isEmpty()){
			//System.out.println("in scheduler, customers not empty:");
			//Gives food to customer if the order is ready
			for(MyCustomer c:customers){
				if(c.state == CustomerState.ORDER_READY) {
					giveFoodToCustomer(c);
					return true;
				}
			}
			//Clears the table if the customer has left
			for(MyCustomer c:customers){
				if(c.state == CustomerState.IS_DONE) {
					clearTable(c);
					return true;
				}
			}

			//Seats the customer if they need it
			for(MyCustomer c:customers){
				if(c.state == CustomerState.NEED_SEATED){
					seatCustomer(c);
					return true;
				}
			}

			//Gives all pending orders to the cook
			for(MyCustomer c:customers){
				if(c.state == CustomerState.ORDER_PENDING){
					giveOrderToCook(c);
					return true;
				}
			}

			//Takes new orders for customers that are ready
			for(MyCustomer c:customers){
				//print("testing for ready to order"+c.state);
				if(c.state == CustomerState.READY_TO_ORDER) {
					takeOrder(c);
					return true;
				}
			}	    
		}
		if (!currentPosition.equals(originalPosition)) {
			DoMoveToOriginalPosition();//Animation thing
			return true;
		}
		if (askBreak && wantBreak && !onBreak){
			setBreakStatus(true);
			return true;
		}

		//we have tried all our rules and found nothing to do. 
		// So return false to main loop of abstract agent and wait.
		//print("in scheduler, no rules matched:");
		return false;
	}

	// *** ACTIONS ***


	/** Seats the customer at a specific table 
	 * @param customer customer that needs seated */
	private void seatCustomer(MyCustomer customer) {
		DoSeatCustomer(customer); //animation	
		customer.state = CustomerState.NO_ACTION;
		customer.cmr.msgFollowMeToTable(this, new Menu());
		stateChanged();
	}
	/** Takes down the customers order 
	 * @param customer customer that is ready to order */
	private void takeOrder(MyCustomer customer) {
		DoTakeOrder(customer); //animation
		customer.state = CustomerState.NO_ACTION;
		customer.cmr.msgWhatWouldYouLike();
		stateChanged();
	}

	/** Gives any pending orders to the cook 
	 * @param customer customer that needs food cooked */
	private void giveOrderToCook(MyCustomer customer) {
		//In our animation the waiter does not move to the cook in
		//order to give him an order. We assume some sort of electronic
		//method implemented as our message to the cook. So there is no
		//animation analog, and hence no DoXXX routine is needed.
		print("Giving " + customer.cmr + "'s choice of " + customer.choice + " to cook");


		customer.state = CustomerState.NO_ACTION;
		//cook.msgHereIsAnOrder(this, customer.tableNum, customer.choice);
		//TODO: CHANGE TO SHARED DATA;
		orderRack.insert(new Order(this, customer.tableNum, customer.choice));
		//print("LOOK AT ME, IM WORKING!");
		stateChanged();

		//Here's a little animation hack. We put the first two
		//character of the food name affixed with a ? on the table.
		//Simply let's us see what was ordered.
		tables[customer.tableNum].takeOrder(customer.choice.substring(0,2)+"?");
		restaurant.placeFood(tables[customer.tableNum].foodX(),
				tables[customer.tableNum].foodY(),
				new Color(255, 255, 255), customer.choice.substring(0,2)+"?");
	}

	/** Gives food to the customer 
	 * @param customer customer whose food is ready */
	private void giveFoodToCustomer(MyCustomer customer) {
		DoGiveFoodToCustomer(customer);//Animation
		customer.state = CustomerState.NO_ACTION;
		customer.cmr.msgHereIsYourFood(customer.choice);
		int total = 0;
		for (int i = 0; i < 4; i++){
			if (customer.choice.equals(theMenu.choices[i])){
				total = theMenu.prices[i];
			}
		}
		print("Giving " + customer.cmr + " his bill");
		customer.cmr.msgHeresYourBill(new Bill(total));
		print("Giving a copy of " + customer.cmr + "'s bill to " + cashier);
		cashier.msgHereIsABill(new Bill(total));
		stateChanged();
	}
	/** Starts a timer to clear the table 
	 * @param customer customer whose table needs cleared */
	private void clearTable(MyCustomer customer) {
		DoClearingTable(customer);
		customer.state = CustomerState.NO_ACTION;
		stateChanged();
	}

	// Animation Actions
	void DoSeatCustomer (MyCustomer customer){
		print("Seating " + customer.cmr + " at table " + (customer.tableNum+1));
		//move to customer first.
		GuiCustomer guiCustomer = customer.cmr.getGuiCustomer();
		guiMoveFromCurrentPostionTo(new Position(guiCustomer.getX()+1,guiCustomer.getY()));
		guiWaiter.pickUpCustomer(guiCustomer);
		Position tablePos = new Position(tables[customer.tableNum].getX()-1,
				tables[customer.tableNum].getY()+1);
		guiMoveFromCurrentPostionTo(tablePos);
		guiWaiter.seatCustomer(tables[customer.tableNum]);
	}
	void DoTakeOrder(MyCustomer customer){
		print("Taking " + customer.cmr +"'s order.");
		Position tablePos = new Position(tables[customer.tableNum].getX()-1,
				tables[customer.tableNum].getY()+1);
		guiMoveFromCurrentPostionTo(tablePos);
	}
	void DoGiveFoodToCustomer(MyCustomer customer){
		print("Giving finished order of " + customer.choice +" to " + customer.cmr);
		Position inFrontOfGrill = new Position(customer.food.getX()-1,customer.food.getY());
		guiMoveFromCurrentPostionTo(inFrontOfGrill);//in front of grill
		guiWaiter.pickUpFood(customer.food);
		Position tablePos = new Position(tables[customer.tableNum].getX()-1,
				tables[customer.tableNum].getY()+1);
		guiMoveFromCurrentPostionTo(tablePos);
		guiWaiter.serveFood(tables[customer.tableNum]);
	}
	void DoClearingTable(final MyCustomer customer){
		print("Clearing table " + (customer.tableNum+1) + " (1500 milliseconds)");
		timer.schedule(new TimerTask(){
			public void run(){		    
				endCustomer(customer);
			}
		}, 1500);
	}
	/** Function called at the end of the clear table timer
	 * to officially remove the customer from the waiter's list.
	 * @param customer customer who needs removed from list */
	private void endCustomer(MyCustomer customer){ 
		print("Table " + (customer.tableNum+1) + " is cleared!");
		customer.food.remove(); //remove the food from table animation
		host.msgTableIsFree(customer.tableNum);
		customers.remove(customer);
		stateChanged();
	}
	private void DoMoveToOriginalPosition(){
		print("Nothing to do. Moving to original position="+originalPosition);
		guiMoveFromCurrentPostionTo(originalPosition);
	}

	//this is just a subroutine for waiter moves. It's not an "Action"
	//itself, it is called by Actions.
	void guiMoveFromCurrentPostionTo(Position to){
		//System.out.println("[Gaut] " + guiWaiter.getName() + " moving from " + currentPosition.toString() + " to " + to.toString());

		AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, to);
		List<Position> path = aStarNode.getPath();
		Boolean firstStep   = true;
		Boolean gotPermit   = true;

		for (Position tmpPath: path) {
			//The first node in the path is the current node. So skip it.
			if (firstStep) {
				firstStep   = false;
				continue;
			}

			//Try and get lock for the next step.
			int attempts    = 1;
			gotPermit       = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());

			//Did not get lock. Lets make n attempts.
			while (!gotPermit && attempts < 3) {
				//System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

				//Wait for 1sec and try again to get lock.
				try { Thread.sleep(1000); }
				catch (Exception e){}

				gotPermit   = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());
				attempts ++;
			}

			//Did not get lock after trying n attempts. So recalculating path.            
			if (!gotPermit) {
				//System.out.println("[Gaut] " + guiWaiter.getName() + " No Luck even after " + attempts + " attempts! Lets recalculate");
				guiMoveFromCurrentPostionTo(to);
				break;
			}

			//Got the required lock. Lets move.
			//System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
			currentPosition.release(aStar.getGrid());
			currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
			guiWaiter.move(currentPosition.getX(), currentPosition.getY());
		}
		/*
	boolean pathTaken = false;
	while (!pathTaken) {
	    pathTaken = true;
	    //print("A* search from " + currentPosition + "to "+to);
	    AStarNode a = (AStarNode)aStar.generalSearch(currentPosition,to);
	    if (a == null) {//generally won't happen. A* will run out of space first.
		System.out.println("no path found. What should we do?");
		break; //dw for now
	    }
	    //dw coming. Get the table position for table 4 from the gui
	    //now we have a path. We should try to move there
	    List<Position> ps = a.getPath();
	    Do("Moving to position " + to + " via " + ps);
	    for (int i=1; i<ps.size();i++){//i=0 is where we are
		//we will try to move to each position from where we are.
		//this should work unless someone has moved into our way
		//during our calculation. This could easily happen. If it
		//does we need to recompute another A* on the fly.
		Position next = ps.get(i);
		if (next.moveInto(aStar.getGrid())){
		    //tell the layout gui
		    guiWaiter.move(next.getX(),next.getY());
		    currentPosition.release(aStar.getGrid());
		    currentPosition = next;
		}
		else {
		    System.out.println("going to break out path-moving");
		    pathTaken = false;
		    break;
		}
	    }
	}
		 */
	}

	// *** EXTRA ***

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#getName()
	 */
	@Override
	public String getName(){
		return name;
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#toString()
	 */
	@Override
	public String toString(){
		return "waiter " + getName();
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#setCook(resturaunt.inter.CookInterface)
	 */
	@Override
	public void setCook(CookInterface cook){
		this.cook = cook;
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#setHost(resturaunt.inter.HostInterface)
	 */
	@Override
	public void setHost(HostInterface host){
		this.host = host;
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#setCashier(resturaunt.inter.CashierInterface)
	 */
	@Override
	public void setCashier(CashierInterface cashier){
		this.cashier = cashier;
	}

	/* (non-Javadoc)
	 * @see restaurant.WaiterInterface#isOnBreak()
	 */
	@Override
	public boolean isOnBreak(){
		return onBreak;
	}

}

