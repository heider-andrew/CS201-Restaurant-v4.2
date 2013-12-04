package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import restaurant.helpers.Bill;
import restaurant.inter.CashierInterface;
import restaurant.inter.CustomerInterface;

import agent.Agent;

public class CashierAgent extends Agent implements CashierInterface
{

	private List<Bill> todaysBills = Collections.synchronizedList(new ArrayList<Bill>());
	private List<CustomerInterface> inLine = Collections.synchronizedList(new ArrayList<CustomerInterface>());
	private CustomerInterface currentCustomer;
	public int totalMoney;
	String name = "Mr Moneybags (the cashier)";
	
	//Messages
	/* (non-Javadoc)
	 * @see restaurant.CashierInterface#msgImReadyToPay(restaurant.CustomerAgent)
	 */
	@Override
	public void msgImReadyToPay(CustomerInterface c){
		getInLine().add(c);
		makeCustomerWait(c);
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see restaurant.CashierInterface#msgHereIsABill(restaurant.Bill)
	 */
	@Override
	public void msgHereIsABill(Bill b){
		getTodaysBills().add(b);
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see restaurant.CashierInterface#msgPayMyBill(restaurant.CustomerAgent, restaurant.Bill)
	 */
	@Override
	public void msgPayMyBill(CustomerInterface c, Bill b){
		thanksForTheCashBro(c,b);
		stateChanged();
	}
	
	//Scheduler

	@Override
	public boolean pickAndExecuteAnAction()
	{
		if (!getInLine().isEmpty()){
			setCurrentCustomer(getInLine().remove(0));
			print("Its " + getCurrentCustomer() + "'s turn to pay");
			getCurrentCustomer().msgTimeToPay(this);
			return true;
		}
		
		return false;
	}
	
	//Actions
	private void makeCustomerWait(CustomerInterface c){
		print("Making " + c + " wait in line");
		c.msgGoStandInLine();
	}
	
	private void thanksForTheCashBro(CustomerInterface c, Bill b){
		print("Taking " + c + "'s money, the total is " + b.total);
		totalMoney += b.total;
		currentCustomer = null;
		c.msgGetOuttaMyHouse();
	}
	
	public CashierAgent(){
		totalMoney = 0;
	}

	public CustomerInterface getCurrentCustomer()
	{
		return currentCustomer;
	}

	public void setCurrentCustomer(CustomerInterface currentCustomer)
	{
		this.currentCustomer = currentCustomer;
	}

	public List<Bill> getTodaysBills()
	{
		return todaysBills;
	}

	public void setTodaysBills(List<Bill> todaysBills)
	{
		this.todaysBills = todaysBills;
	}

	public List<CustomerInterface> getInLine()
	{
		return inLine;
	}

	public void setInLine(List<CustomerInterface> inLine)
	{
		this.inLine = inLine;
	}
}
