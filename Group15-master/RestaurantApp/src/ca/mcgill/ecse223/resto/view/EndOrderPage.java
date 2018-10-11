package ca.mcgill.ecse223.resto.view;

import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Color;

import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.Order;
import ca.mcgill.ecse223.resto.model.Table;

public class EndOrderPage extends JFrame {

	// UI elements
	
	private RestoAppPage mainPage;

	private JLabel errorMessage;

	// elements for select order
	private JLabel selectOrderLabel;
	private JComboBox<String> orderToggleList;

	// elements for cancel and end order button
	private JButton cancelButton;
	private JButton endOrderButton;

	// data elements
	private String error = null;
	private Integer selectedOrderIndex = -1;
	private HashMap<Integer, Order> orders;

	public EndOrderPage(RestoAppPage mainPage) {
		this.mainPage = mainPage;
		initComponents();
		refreshData();

	}

	private void initComponents() {
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		// elements for select order
		selectOrderLabel = new JLabel();
		orderToggleList = new JComboBox<String>(new String[0]);
		orderToggleList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JComboBox<String> cb = (JComboBox<String>) evt.getSource();
				selectedOrderIndex = cb.getSelectedIndex();
			}
		});

		// elements for cancel and end order button
		cancelButton = new JButton();
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		endOrderButton = new JButton();
		endOrderButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				endOrderActionPerformed(evt);
			}
		});

		// global settings
		selectOrderLabel.setText("Select the order you want to end");
		cancelButton.setText("Cancel");
		endOrderButton.setText("End Order");

		this.setSize(cancelButton.getWidth() + endOrderButton.getWidth() + 200, 350 + 4 * cancelButton.getHeight());
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(errorMessage).addComponent(selectOrderLabel)
				.addComponent(orderToggleList).addGap(200)
				.addGroup(layout.createSequentialGroup().addComponent(cancelButton).addComponent(endOrderButton))

		);

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] { selectOrderLabel, orderToggleList });
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] { cancelButton, endOrderButton });
		layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] { selectOrderLabel, orderToggleList });

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(errorMessage).addComponent(selectOrderLabel)
				.addComponent(orderToggleList).addGap(200)
				.addGroup(layout.createParallelGroup().addComponent(cancelButton).addComponent(endOrderButton))

		);

		// pack();

	}

	private void refreshData() {
		errorMessage.setText(error);
		if (error == null || error.length() == 0) {
			// populate page with data
			orders = new HashMap<Integer, Order>();
			orderToggleList.removeAllItems();
			Integer index = 0;
			for (Order order : RestoAppController.getCurrentOrders()) {
				orders.put(index, order);
				String boxContent = "Tables: ";
				for (Table table : order.getTables()) {
					boxContent = boxContent + Integer.toString(table.getNumber()) + " ";
				}
				orderToggleList.addItem(boxContent);
				index++;
			}

			selectedOrderIndex = -1;
			orderToggleList.setSelectedIndex(selectedOrderIndex);
		}
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}

	private void endOrderActionPerformed(java.awt.event.ActionEvent evt) {
		error = null;
		Order order = orders.get(selectedOrderIndex);
		try {
			error = RestoAppController.endOrder(order);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		} finally {
			refreshData();
			mainPage.plotTables();
		}

	}

}
