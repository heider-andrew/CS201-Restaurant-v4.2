package restaurant.helpers;

import restaurant.inter.CookInterface;

public class groceryOrder{
	public CookInterface orderOwner;
	public String itemOrdered;
	public int amtOrdered;
	
	public groceryOrder(CookInterface c, String i, int a){
		orderOwner = c;
		itemOrdered = i;
		amtOrdered = a;
	}
	
	public String toString(){
		return amtOrdered + " " + itemOrdered + " for " + orderOwner;
	}
}