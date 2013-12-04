package restaurant.test.Mock;

import restaurant.helpers.Bill;
import restaurant.helpers.Menu;
import restaurant.inter.CashierInterface;
import restaurant.inter.CustomerInterface;
import restaurant.inter.HostInterface;
import restaurant.inter.WaiterInterface;
import restaurant.layoutGUI.GuiCustomer;

public class MockCustomer extends MockAgent implements CustomerInterface {

	public MockCustomer(String name)
	{
		super(name);
	}

	public EventLog log = new EventLog();
	
	@Override
	public void setHungry()
	{
		log.add(new LoggedEvent("Recieved message setHungry from resturaunt system"));	
	}

	@Override
	public void msgFollowMeToTable(WaiterInterface waiter, Menu menu)
	{
		log.add(new LoggedEvent("Recieved message msgFollowMeToTable from " + waiter + " and was given a menu: " + menu));
		
	}

	@Override
	public void msgHeresYourBill(Bill b)
	{
		log.add(new LoggedEvent("Recieved message msgHeresYourBill from someone and given " + b));
	}

	@Override
	public void msgDecided()
	{
		log.add(new LoggedEvent("Revieved msgDecided from myself"));
	}

	@Override
	public void msgWhatWouldYouLike()
	{
		log.add(new LoggedEvent("Revieved message msgWhatWouldYouLike from my waiter"));
	}

	@Override
	public void msgHereIsYourFood(String choice)
	{
		log.add(new LoggedEvent("Reveieved message msgHereIsYourFood from my waiter and given a " + choice));
	}

	@Override
	public void msgDoneEating()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgResturauntFull(HostInterface h)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoStandInLine()
	{
		log.add(new LoggedEvent("I was told to go stand in Line"));
	}

	@Override
	public void msgTimeToPay(CashierInterface c)
	{
		log.add(new LoggedEvent("The cashier " + c + " told me it was time to pay"));
	}

	@Override
	public void msgGetOuttaMyHouse()
	{
		log.add(new LoggedEvent("The cashier recieved my money and told me it was ok to leave"));
		
	}

	@Override
	public void msgRepickFood()
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
	public boolean isHungry()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getHungerLevel()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHungerLevel(int hungerLevel)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public GuiCustomer getGuiCustomer()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
