package restaurant.test;

import static org.junit.Assert.*;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import restaurant.WaiterAgent;
import restaurant.CustomerAgent;
import restaurant.CashierAgent;
import restaurant.helpers.Bill;
import restaurant.inter.*;

import restaurant.test.Mock.MockCustomer;
import restaurant.test.Mock.MockWaiter;


public class CashierTest extends TestCase
{
	public CashierAgent cashier;
	
	@Test
	public void testTellingCustomerToStandInLine(){
		cashier = new CashierAgent();
		Bill bill = new Bill(10);
		Bill bill2 = new Bill(20);
		Bill bill3 = new Bill(50);
		assertNotNull("Making sure the cashier has been created", cashier);
		MockCustomer customer = new MockCustomer("Buster");
		MockCustomer customer2 = new MockCustomer("Derek");
		MockCustomer customer3 = new MockCustomer("Ashley");
		
		assertEquals(
				"Mock Customer should have an empty event log before the Waiter's scheduler is called. Instead, the mock customer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());

		cashier.msgHereIsABill(bill);
		
		assertSame("Cashier has a bill", bill, cashier.getTodaysBills().get(0));
		
		cashier.msgImReadyToPay(customer);

		assertTrue("Mock Customer should have been told to go stand in line", customer.log.containsString("I was told to go stand in Line"));
		
		assertNull("There should not be a customer currently paying", cashier.getCurrentCustomer());
		
		cashier.pickAndExecuteAnAction();
		
		assertSame("The only Customer should be the current one after scheduler is ran", cashier.getCurrentCustomer(), customer);
		
		assertTrue("Customer should have been told to pay", customer.log.containsString(" told me it was time to pay"));

		cashier.msgPayMyBill(customer, bill);
		
		assertEquals("Total for the day is equal to customer's bill", bill.total, cashier.totalMoney);
				
		
		
		//testing multiple customers
		assertEquals(
				"Mock Customer should have an empty event log before the Waiter's scheduler is called. Instead, the mock customer's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());

		assertEquals(
				"Mock Customer should have an empty event log before the Waiter's scheduler is called. Instead, the mock customer's event log reads: "
						+ customer3.log.toString(), 0, customer3.log.size());

		cashier.msgImReadyToPay(customer2);
		
		assertTrue("Mock Customer should have been told to go stand in line", customer2.log.containsString("I was told to go stand in Line"));
		
		assertNull("There should not be a customer currently paying", cashier.getCurrentCustomer());
		
		cashier.msgImReadyToPay(customer3);
		
		assertTrue("Mock Customer should have been told to go stand in line", customer3.log.containsString("I was told to go stand in Line"));
		
		assertEquals("Check that there are 2 people in line", cashier.getInLine().size(), 2);
		
		cashier.pickAndExecuteAnAction();
		
		assertSame("The only Customer should be the current one after scheduler is ran", cashier.getCurrentCustomer(), customer2);
		
		assertTrue("Customer should have been told to pay", customer2.log.containsString(" told me it was time to pay"));
		
		cashier.msgPayMyBill(customer2, bill2);
		
		assertEquals("Total for the day is equal to customer's bill", 30, cashier.totalMoney);
		
		assertEquals("Check that there are 2 people in line", cashier.getInLine().size(), 1);
		
		cashier.pickAndExecuteAnAction();
		
		assertSame("The only Customer should be the current one after scheduler is ran", cashier.getCurrentCustomer(), customer3);
		
		assertTrue("Customer should have been told to pay", customer3.log.containsString(" told me it was time to pay"));
		
		cashier.msgPayMyBill(customer3, bill3);
		
		assertEquals("Total for the day is equal to customer's bill", 80, cashier.totalMoney);
		
	}
	
}
