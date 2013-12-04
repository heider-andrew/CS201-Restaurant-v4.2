package restaurant;

import java.util.Vector;
import restaurant.helpers.Order;

public class Monitor extends Thread {
    private final int N = 500;
    private int count = 0;
    private Vector<Order> theData;
    
    synchronized public void insert(Order data) {
        while (count == N) {
            try{ 
                System.out.println("\tFull, waiting");
                wait(5000);                         // Full, wait to add
            } catch (InterruptedException ex) {};
        }
            
        insert_item(data);
        count++;
        if(count == 1) {
            System.out.println("\tNot Empty, notify");
            notify();                               // Not empty, notify a 
                                                    // waiting consumer
        }
    }
    
    synchronized public Order remove() {
        Order data;
        while(count == 0)
            try{ 
                System.out.println("\tEmpty, waiting");
                wait(5000);                         // Empty, wait to consume
            } catch (InterruptedException ex) {};

        data = remove_item();
        count--;
        if(count == N-1){ 
            System.out.println("\tNot full, notify");
            notify();                               // Not full, notify a 
                                                    // waiting producer
        }
        return data;
    }
    
    private void insert_item(Order data){
        theData.addElement(data);
    }
    
    private Order remove_item(){
        Order data = (Order) theData.firstElement();
        theData.removeElementAt(0);
        return data;
    }
    
    public Monitor(){
        theData = new Vector();
    }
    
    public boolean hasObject(){
    	if (theData != null && !theData.isEmpty()){
    		return true;
    	} else {
    		return false;
    	}
    }
    
}
