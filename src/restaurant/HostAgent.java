package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.inter.CustomerInterface;
import restaurant.inter.HostInterface;
import restaurant.inter.WaiterInterface;


/** Host agent for restaurant.
 *  Keeps a list of all the waiters and tables.
 *  Assigns new customers to waiters for seating and 
 *  keeps a list of waiting customers.
 *  Interacts with customers and waiters.
 */
public class HostAgent extends Agent implements HostInterface {

    /** Private class storing all the information for each table,
     * including table number and state. */
    private class Table {
		public int tableNum;
		public boolean occupied;
	
		/** Constructor for table class.
		 * @param num identification number
		 */
		public Table(int num){
		    tableNum = num;
		    occupied = false;
		}	
    }

    /** Private class to hold waiter information and state */
    private class MyWaiter {
	public WaiterInterface wtr;
	public boolean working = true;

	/** Constructor for MyWaiter class
	 * @param waiter
	 */
	public MyWaiter(WaiterInterface waiter){
	    wtr = waiter;
	}
    }

    //List of all the customers that need a table
    private List<CustomerAgent> waitList =
		Collections.synchronizedList(new ArrayList<CustomerAgent>());

    //List of all waiter that exist.
    private List<MyWaiter> waiters =
		Collections.synchronizedList(new ArrayList<MyWaiter>());
    private int nextWaiter =0; //The next waiter that needs a customer
    
    //List of all the tables
    int nTables;
    private Table tables[];

    //Name of the host
    private String name;

    /** Constructor for HostAgent class 
     * @param name name of the host */
    public HostAgent(String name, int ntables) {
	super();
	this.nTables = ntables;
	tables = new Table[nTables];

	for(int i=0; i < nTables; i++){
	    tables[i] = new Table(i);
	}
	this.name = name;
    }

    // *** MESSAGES ***

    /* (non-Javadoc)
	 * @see restaurant.HostInterface#msgIWantToEat(restaurant.CustomerAgent)
	 */
    @Override
	public void msgIWantToEat(CustomerAgent customer){
    	boolean fullFlag = true;
    	waitList.add(customer);
    	for(int i=0; i < nTables; i++){
    		if (!tables[i].occupied){
    			fullFlag = false;
    		}
    	}
    	if (fullFlag){
    		customer.msgResturauntFull(this);
    	}
    	stateChanged();
    }
    
    /* (non-Javadoc)
	 * @see restaurant.HostInterface#msgIAmLeaving(resturaunt.inter.CustomerInterface)
	 */
    @Override
	public void msgIAmLeaving(CustomerInterface customer){
    	waitList.remove(customer);
    	stateChanged();
    }
    
    /* (non-Javadoc)
	 * @see restaurant.HostInterface#msgIWantABreak(restaurant.WaiterAgent)
	 */
    @Override
	public void msgIWantABreak(WaiterInterface waiter){
    	boolean canBreakFlag = false;
    	for (MyWaiter w: waiters){
    		if (w.working == true && !w.wtr.equals(waiter)){
    			waiter.breakPermission(true);
    			canBreakFlag = true;
    		}
    	}
    	if (canBreakFlag){
    		for (MyWaiter w: waiters){
    			if (w.wtr.equals(waiter)){
    				w.working = false;
    			}
    		}
    	} else {
    		waiter.breakPermission(false);
    	}
    }
    
    /* (non-Javadoc)
	 * @see restaurant.HostInterface#msgImBackFromBreak(restaurant.WaiterAgent)
	 */
    @Override
	public void msgImBackFromBreak(WaiterInterface waiter){
    	for(MyWaiter w: waiters){
    		if (w.wtr.equals(waiter)){
    			w.working = true;
    		}
    	}
    }

    /* (non-Javadoc)
	 * @see restaurant.HostInterface#msgTableIsFree(int)
	 */
    @Override
	public void msgTableIsFree(int tableNum){
	tables[tableNum].occupied = false;
	stateChanged();
    }

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
	if(!waitList.isEmpty() && !waiters.isEmpty()){
	    synchronized(waiters){
		//Finds the next waiter that is working
		while(!waiters.get(nextWaiter).working){
		    nextWaiter = (nextWaiter+1)%waiters.size();
		}
	    }
	    print("picking waiter number:"+nextWaiter);
	    //Then runs through the tables and finds the first unoccupied 
	    //table and tells the waiter to sit the first customer at that table
	    for(int i=0; i < nTables; i++){

		if(!tables[i].occupied){
		    synchronized(waitList){
			tellWaiterToSitCustomerAtTable(waiters.get(nextWaiter),
			    waitList.get(0), i);
		    }
		    return true;
		}
	    }
	}

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    
    // *** ACTIONS ***
    
    /** Assigns a customer to a specified waiter and 
     * tells that waiter which table to sit them at.
     * @param waiter
     * @param customer
     * @param tableNum */
    private void tellWaiterToSitCustomerAtTable(MyWaiter waiter, CustomerInterface customer, int tableNum){
	print("Telling " + waiter.wtr + " to sit " + customer +" at table "+(tableNum+1));
	waiter.wtr.msgSitCustomerAtTable(customer, tableNum);
	tables[tableNum].occupied = true;
	waitList.remove(customer);
	nextWaiter = (nextWaiter+1)%waiters.size();
	stateChanged();
    }
	
    

    // *** EXTRA ***

    /* (non-Javadoc)
	 * @see restaurant.HostInterface#getName()
	 */
    @Override
	public String getName(){
        return name;
    }    

    /* (non-Javadoc)
	 * @see restaurant.HostInterface#setWaiter(restaurant.WaiterAgent)
	 */
    @Override
	public void setWaiter(WaiterInterface waiter){
	waiters.add(new MyWaiter(waiter));
	stateChanged();
    }
    
    //Gautam Nayak - Gui calls this when table is created in animation
    /* (non-Javadoc)
	 * @see restaurant.HostInterface#addTable()
	 */
    @Override
	public void addTable() {
	nTables++;
	Table[] tempTables = new Table[nTables];
	for(int i=0; i < nTables - 1; i++){
	    tempTables[i] = tables[i];
	}  		  			
	tempTables[nTables - 1] = new Table(nTables - 1);
	tables = tempTables;
    }
}
