package ca.mcgill.ecse223.resto.view;

import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ca.mcgill.ecse223.resto.model.Bill;
import ca.mcgill.ecse223.resto.model.OrderItem;
import ca.mcgill.ecse223.resto.model.Seat;

public class BillRender extends JFrame{
	
	private JLabel content;
	private JButton doneButton;
	private JPanel contentPannel;
	private JScrollPane scrollPane;
	private Bill bill;
	
	public BillRender(Bill bill) {
		this.bill = bill;
		setSize(300, 600);
		initComponents();
	}
	
	private void initComponents() {

		// elements for error message
		content = new JLabel();
		contentPannel = new JPanel();
		scrollPane = new JScrollPane();
		
		doneButton = new JButton();
		
		doneButton.setText("Save Changes");
		doneButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setVisible(false);
			}
		});
		
		String contentMessage = "<html> <b> Bill </b>";
		
		double total = 0;
		
		for(Seat seat : bill.getIssuedForSeats()) {
			
			int seatNumber = 1;
			for(Seat checkSeat : seat.getTable().getCurrentSeats()) {
				if(checkSeat == seat) {
					break;
				}
				seatNumber ++;
			}
			
			ArrayList<OrderItem> items = new ArrayList<OrderItem>();
			
			for(OrderItem oItem : seat.getOrderItems()) {
				if(oItem.getOrder() == bill.getOrder()) {
					items.add(oItem);
				}
			}
			
			if(items.isEmpty()) {
				continue;
			}
			
			contentMessage += "<br> Table " + seat.getTable().getNumber() + "Seat " + seatNumber + "<br>";
			
			for(OrderItem oItem : seat.getOrderItems()) {
				if(oItem.getOrder() == bill.getOrder()) {
					contentMessage += oItem.getPricedMenuItem().getMenuItem().getName() + " (x" + Double.toString((double)(Math.round(((double)oItem.getQuantity()/oItem.getSeats().size())*1000))/1000) + ") - $" + Double.toString((double)(Math.round(((double)oItem.getQuantity()*oItem.getPricedMenuItem().getPrice()/oItem.getSeats().size())*100))/100) + "<br>";
				}
			}
			
			for(OrderItem oItem : items) {
				total += (double)oItem.getQuantity()*oItem.getPricedMenuItem().getPrice()/oItem.getSeats().size();
			}
			
			
		}
		
		contentMessage += "<br> Total <br>" + total + "<br>";
		
		contentMessage += "</html>";
		
		content.setText(contentMessage);
		
		contentPannel.add(content);
		scrollPane.setViewportView(contentPannel);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(scrollPane)
				.addComponent(doneButton));

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(scrollPane)
				.addComponent(doneButton));
		
		
		
		pack();
		setSize(300, 600);
		
		
		
	}
	
}
