package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import restaurant.inter.CustomerInterface;
import restaurant.inter.WaiterInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.File;


/** Main GUI class.
 * Contains the main frame and subsequent panels */
public class RestaurantGui extends JFrame implements ActionListener{
   
    private final int WINDOWX = 450;
    private final int WINDOWY = 350;

    private RestaurantPanel restPanel = new RestaurantPanel(this);
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel(
    "<html><pre><i>(Click on a customer/waiter)</i></pre></html>");
    private JCheckBox stateCB = new JCheckBox();
	private JButton addTable = new JButton("Add Table");
	private JButton emptyFridge = new JButton("Empty Fridge");

    private Object currentPerson;

    /** Constructor for RestaurantGui class.
     * Sets up all the gui components. */
    public RestaurantGui(){

	super("Restaurant Application");

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(50,50, WINDOWX, WINDOWY);

	getContentPane().setLayout(new BoxLayout((Container)getContentPane(),BoxLayout.Y_AXIS));

	Dimension rest = new Dimension(WINDOWX, (int)(WINDOWY*.6));
	Dimension info = new Dimension(WINDOWX, (int)(WINDOWY*.25));
	restPanel.setPreferredSize(rest);
	restPanel.setMinimumSize(rest);
	restPanel.setMaximumSize(rest);
	infoPanel.setPreferredSize(info);
	infoPanel.setMinimumSize(info);
	infoPanel.setMaximumSize(info);
	infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

	stateCB.setVisible(false);
	stateCB.addActionListener(this);

	infoPanel.setLayout(new GridLayout(1,2, 30,0));
	infoPanel.add(infoLabel);
	infoPanel.add(stateCB);
	
	getContentPane().add(restPanel);
	getContentPane().add(addTable);
	getContentPane().add(emptyFridge);
	getContentPane().add(infoPanel);
	
	addTable.addActionListener(this);
    emptyFridge.addActionListener(this);
    }


    /** This function takes the given customer or waiter object and 
     * changes the information panel to hold that person's info.
     * @param person customer or waiter object */
    public void updateInfoPanel(Object person){
	stateCB.setVisible(true);
	currentPerson = person;
	
	if(person instanceof CustomerAgent){
	    CustomerInterface customer = (CustomerInterface) person;
	    stateCB.setText("Hungry?");
	    stateCB.setSelected(customer.isHungry());
	    stateCB.setEnabled(!customer.isHungry());
	    infoLabel.setText(
	    "<html><pre>     Name: " + customer.getName() + " </pre></html>");

	}else if(person instanceof WaiterAgent){
	    WaiterInterface waiter = (WaiterInterface) person;
	    stateCB.setText("On Break?");
	    stateCB.setSelected(waiter.isOnBreak());
	    stateCB.setEnabled(true);
	    infoLabel.setText(
	    "<html><pre>     Name: " + waiter.getName() + " </html>");
	}	   

	infoPanel.validate();
    }

    /** Action listener method that reacts to the checkbox being clicked */
    public void actionPerformed(ActionEvent e){

    	
    	if(e.getSource() == stateCB){
	    if(currentPerson instanceof CustomerAgent){
		CustomerInterface c = (CustomerInterface) currentPerson;
		c.setHungry();
		stateCB.setEnabled(false);

	    }else if(currentPerson instanceof WaiterAgent){
		WaiterInterface w = (WaiterInterface) currentPerson;
		w.wantBreak(stateCB.isSelected());
		//System.out.println("AM I ON BREAK? : " + w.getBreakStatus());
	    }
	}
	else if (e.getSource() == addTable)
	{
		try {
			System.out.println("[Gautam] Add Table!");
			//String XPos = JOptionPane.showInputDialog("Please enter X Position: ");
			//String YPos = JOptionPane.showInputDialog("Please enter Y Position: ");
			//String size = JOptionPane.showInputDialog("Please enter Size: ");
			//restPanel.addTable(10, 5, 1);
			//restPanel.addTable(Integer.valueOf(YPos).intValue(), Integer.valueOf(XPos).intValue(), Integer.valueOf(size).intValue());
			restPanel.addTable();
		}
		catch(Exception ex) {
			System.out.println("Unexpected exception caught in during setup:"+ ex);
		}
	}
	else if (e.getSource() == emptyFridge){
    		System.out.println("EMPTYING THE FRIDGE!!!");
    		restPanel.fridgeHelper();
    	}
	    
    }

    /** Message sent from a customer agent to enable that customer's 
     * "I'm hungery" checkbox.
     * @param c reference to the customer */
    public void setCustomerEnabled(CustomerInterface c){
	if(currentPerson instanceof CustomerAgent){
	    CustomerInterface cust = (CustomerInterface) currentPerson;
	    if(c.equals(cust)){
		stateCB.setEnabled(true);
		stateCB.setSelected(false);
	    }
	}
    }
	
	
    /** Main routine to get gui started */
    public static void main(String[] args){
	RestaurantGui gui = new RestaurantGui();
	gui.setVisible(true);
	gui.setResizable(false);
    }
}
