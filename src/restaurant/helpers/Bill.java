package restaurant.helpers;

public class Bill
{
	public int total;
	boolean paid;
	
	public Bill(int total){
		this.total = total;
		paid = false;
	}
}
