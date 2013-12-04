package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import restaurant.helpers.groceryOrder;
import restaurant.inter.CookInterface;
import restaurant.inter.MarketInterface;

import agent.Agent;

public class MarketAgent extends Agent implements MarketInterface{

	
	private class inventoryItem{
		public String food;
		public int amtInStock;
		public boolean ordered;
		
		inventoryItem(String food, int amtInStock){
			this.food = food;
			this.amtInStock = amtInStock;
			ordered = false;
		}
	}
	
	
	List<inventoryItem> myInventory = Collections.synchronizedList(new ArrayList<inventoryItem>());
	List<groceryOrder> openOrders = Collections.synchronizedList(new ArrayList<groceryOrder>());
	Timer timer = new Timer();
	String name;
	
	
	public MarketAgent(String name, int inventoryOption){
		super();
		this.name = name;
		switch (inventoryOption){
		case 1:
			myInventory.add(new inventoryItem("Steak", 0));
			myInventory.add(new inventoryItem("Pizza", 0));
			myInventory.add(new inventoryItem("Chicken", 0));
			myInventory.add(new inventoryItem("Salad", 0));
			break;
		case 2:
			myInventory.add(new inventoryItem("Steak", 50));
			myInventory.add(new inventoryItem("Pizza", 0));
			myInventory.add(new inventoryItem("Chicken", 50));
			myInventory.add(new inventoryItem("Salad", 50));
			break;
		case 3:
			myInventory.add(new inventoryItem("Steak", 50));
			myInventory.add(new inventoryItem("Pizza", 50));
			myInventory.add(new inventoryItem("Chicken", 50));
			myInventory.add(new inventoryItem("Salad", 50));
			break;
		}
	}
	
	//Messages
	/* (non-Javadoc)
	 * @see restaurant.MarketInterface#hereIsGroceryOrder(resturaunt.inter.CookInterface, java.lang.String, int)
	 */
	@Override
	public void hereIsGroceryOrder(CookInterface c, String choice, int amtOrdered){
		openOrders.add(new groceryOrder(c,choice,amtOrdered));
		stateChanged();
	}
	
	//Scheduler
	@Override
	protected boolean pickAndExecuteAnAction()
	{
		for(groceryOrder g: openOrders){
			fufillOrder(g);
			return true;
		}
		for(inventoryItem i: myInventory){
			if (i.amtInStock == 0){
				placeWherehouseOrder(i.food);
				return true;
			}
		}
		return false;
	}
	
	
	//Actions
	/* (non-Javadoc)
	 * @see restaurant.MarketInterface#fufillOrder(restaurant.MarketAgent.groceryOrder)
	 */
	@Override
	public void fufillOrder(groceryOrder order){
		for(inventoryItem i: myInventory){
			if (i.food == order.itemOrdered){
				if (i.amtInStock > 10){
					i.amtInStock -= 10;
					print("Fufilling order for " + order.orderOwner);
					order.orderOwner.foodDelivery(order.itemOrdered, 10);
					openOrders.remove(order);
					return;
				}
			}
			print("Cant fill order for " + order.orderOwner);
			order.orderOwner.cantDeliver(order.itemOrdered);
			openOrders.remove(order);
			stateChanged();
		}
	}
	
	private void placeWherehouseOrder(final String choice){
		for(inventoryItem i: myInventory){
			if (i.food == choice && !i.ordered){
				print("Out of " + choice + ". Ordering more.");
				i.ordered = true;
				timer.schedule(new TimerTask(){ 
					public void run(){
						wherehouseDelivery(choice);	
					}
				}, (long)10000);
				stateChanged();
			}
		}
	}
	
	private void wherehouseDelivery(String choice){
		for(inventoryItem i: myInventory){
			if (i.food == choice){
				i.ordered = false;
				i.amtInStock += 50;
			}
		}
		stateChanged();
	}

}
