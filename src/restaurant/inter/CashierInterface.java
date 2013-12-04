package restaurant.inter;

import restaurant.CustomerAgent;
import restaurant.helpers.Bill;

public interface CashierInterface
{

	//Messages
	public abstract void msgImReadyToPay(CustomerInterface c);

	public abstract void msgHereIsABill(Bill b);

	public abstract void msgPayMyBill(CustomerInterface c, Bill b);

}