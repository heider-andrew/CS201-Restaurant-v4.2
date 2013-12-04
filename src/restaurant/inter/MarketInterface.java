package restaurant.inter;

import restaurant.helpers.groceryOrder;

public interface MarketInterface
{

	//Messages
	public abstract void hereIsGroceryOrder(CookInterface c, String choice,
			int amtOrdered);

	//Actions
	public abstract void fufillOrder(groceryOrder order);

}