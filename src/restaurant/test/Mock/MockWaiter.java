package restaurant.test.Mock;

import restaurant.inter.CashierInterface;
import restaurant.inter.CookInterface;
import restaurant.inter.CustomerInterface;
import restaurant.inter.HostInterface;
import restaurant.inter.WaiterInterface;
import restaurant.layoutGUI.Food;

public class MockWaiter extends MockAgent implements WaiterInterface{

	public MockWaiter(String name)
	{
		super(name);
	}

	@Override
	public void wantBreak(boolean state)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void breakPermission(boolean state)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgSitCustomerAtTable(CustomerInterface customer, int tableNum)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgImReadyToOrder(CustomerInterface customer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsMyChoice(CustomerInterface customer, String choice)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderIsReady(int tableNum, Food f)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOutOfFood(int tableNum)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEatingAndLeaving(CustomerInterface customer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBreakStatus(boolean state)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCook(CookInterface cook)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHost(HostInterface host)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCashier(CashierInterface cashier)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOnBreak()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
