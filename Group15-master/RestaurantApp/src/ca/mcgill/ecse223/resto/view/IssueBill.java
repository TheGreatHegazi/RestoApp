package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.Bill;
import ca.mcgill.ecse223.resto.model.LoyaltyCard;
import ca.mcgill.ecse223.resto.model.Order;
import ca.mcgill.ecse223.resto.model.Seat;
import ca.mcgill.ecse223.resto.model.Table;

public class IssueBill extends JFrame {

	private static final long serialVersionUID = -2683593616927798071L;

	// UI elements

	private JLabel errorMsg;
	private JLabel orderLabel;
	private JCheckBox seatsToggleAll;
	private JLabel seatsLabel;
	private JPanel seatsPanel;
	private JScrollPane scrollPane;
	private JLabel loyaltyLabel;
	private JTextField loyaltyInput;
	private JLabel loyaltyPoints;

	private JButton saveChanges;
	private JButton cancel;

	private HashMap<JButton, Seat> seats;
	
	private Order order;

	private String error = "";

	public IssueBill(Table table) {
		order = table.getOrder(table.numberOfOrders()-1);
		initComponents();
		refreshData();

	}

	private void initComponents() {

		// elements for error message
		errorMsg = new JLabel();
		errorMsg.setForeground(Color.RED);

		// elements for tables
		String orderString = "Issuing bill for Order serving tables: ";
		orderString += order.getTable(0).getNumber();
		for(int i = 1; i<order.getTables().size(); i++) {
			orderString += ", " + order.getTable(i).getNumber();
		}
		
		orderLabel = new JLabel();
		orderLabel.setText(orderString);


		seatsToggleAll = new JCheckBox("Select All Seats");
		seatsToggleAll.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				if (seats != null) {
					if (cb.isSelected()) {
						for (JButton button : seats.keySet()) {
							button.setText("Selected");
							;
						}
					} else {
						for (JButton button : seats.keySet()) {
							button.setText("Not Selected");
						}
					}
				}
			}
		});
		
		loyaltyPoints = new JLabel();
		loyaltyPoints.setForeground(Color.GREEN);

		seatsLabel = new JLabel("Please select Seats to Issue a bill for!");
		seatsPanel = new JPanel();
		scrollPane = new JScrollPane();
		seatsPanel.setLayout(new BoxLayout(seatsPanel, BoxLayout.Y_AXIS));
		scrollPane.setBounds(14, 100, 310, 310);
		scrollPane.setViewportView(seatsPanel);

		saveChanges = new JButton();
		cancel = new JButton();

		setTitle("Issuing Bill");

		orderLabel.setText("Issuing a bill for Order " + order.getNumber());
		seatsLabel.setText("Please select the seats to issue a bill for: ");

		saveChanges.setText("Issue Bill");
		saveChanges.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveChangesActionPerformed(evt);
			}
		});

		cancel.setText("Cancel Changes");
		cancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelActionPerformed(evt);
			}
		});
		
		loyaltyLabel = new JLabel();
		loyaltyLabel.setText("Enter a Loyalty Number");
		
		loyaltyInput = new JTextField();
		

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(errorMsg)
				.addComponent(orderLabel)
				.addGroup(layout.createSequentialGroup()
						.addComponent(seatsToggleAll))
				.addComponent(seatsLabel)
				.addComponent(scrollPane)
				.addGroup(layout.createSequentialGroup()
						.addComponent(cancel)
						.addComponent(saveChanges))
				.addComponent(loyaltyLabel)
				.addComponent(loyaltyInput)
				.addComponent(loyaltyPoints));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(errorMsg)
				.addComponent(orderLabel)
				.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(seatsToggleAll))
				.addComponent(seatsLabel)
				.addComponent(scrollPane)
				.addGroup(layout.createParallelGroup()
						.addComponent(cancel)
						.addComponent(saveChanges))
				.addComponent(loyaltyLabel)
				.addComponent(loyaltyInput)
				.addComponent(loyaltyPoints));

		pack();
		setSize(500, 450);
	}

	private void saveChangesActionPerformed(java.awt.event.ActionEvent evt) {

		error = "";

		List<Seat> checkedSeats = new ArrayList<Seat>();

		if (seats != null) {
			for (JButton button : seats.keySet()) {
				if (button.getText().equals("Selected")) {
					checkedSeats.add(seats.get(button));
				}
			}
		}
		
		int loyaltyNumber = -9;
		
		try {
			loyaltyNumber = Integer.parseInt(loyaltyInput.getText());
		} catch (NumberFormatException e) {
			if(loyaltyInput.getText().trim().length()!=0) {
				error = "Please enter a number for the loyalty card or leave it empty";
			}
		}
		
		if(loyaltyInput.getText().trim().equals("")) {
			loyaltyNumber = -1;
		}

		try {
			if(loyaltyNumber != -9) {
				Bill newBill = RestoAppController.issueBill(checkedSeats, loyaltyNumber);
				new BillRender(newBill).setVisible(true);
			}
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		if(error == null | error.trim().length() == 0) {
			if(loyaltyNumber >= 0) {
				LoyaltyCard card = null;
				for(LoyaltyCard loyalty : RestoApplication.getRestoApp().getLoyaltyCards()) {
					if(loyalty.getNumber() == loyaltyNumber) card = loyalty;
				}
				loyaltyPoints.setText("Success! Your Points are now: " + card.getPoints());
			}
		}

		refreshData();
	}

	private void cancelActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);

		pack();
		setSize(500, 450);
	}


	public void refreshData() {

		errorMsg.setText(error);

		if (error == null || error.length() == 0) {

			seatsPanel.removeAll();
			seats = new HashMap<JButton, Seat>();
			for (Table table : order.getTables()) {
				int index = 1;
				for (Seat seat : table.getCurrentSeats()) {
					JLabel newLabel = new JLabel("Table: " + table.getNumber() + " Seat: " + index);
					JButton newButton = new JButton("Not Selected");
					newButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent evt) {
							JButton newButton = (JButton) evt.getSource();
							if (newButton.getText().equals("Not Selected")) {
								newButton.setText("Selected");
							} else {
								newButton.setText("Not Selected");
							}
							boolean allSelected = true;
							for(JButton button : seats.keySet()) {
								if(button.getText().equals("Not Selected")) {
									allSelected = false;
								}
							}
							if(!allSelected) {
								seatsToggleAll.setSelected(false);
							}
						}
					});
					seatsPanel.add(newLabel);
					seatsPanel.add(newButton);
					seats.put(newButton, seat);
					index++;

				}
			}

		}
		loyaltyInput.setText("");
		
	}

}