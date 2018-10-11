package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;

@SuppressWarnings("serial")
public class AddNewMenuItem extends JFrame {


	// UI elements
	private JLabel errorMessage;

	// elements for setting number of seats
	private JLabel nameLabel;
	private JTextField nameField;

	// elements for setting thex position
	private JLabel categoryLabel;
	private HashMap<Integer, ItemCategory> categories;
	private JComboBox<String> categoryToggleList;
	private Integer selectedCategoryIndex = 0;

	// elements for setting the y position
	private JLabel priceLabel;
	private JTextField priceField;

	// elements for cancel button
	private JButton cancelButton;

	// elements for add table button
	private JButton addButton;

	// data elements
	private String error;

	public AddNewMenuItem() {
	
		initComponents();
		refreshData();
	}

	public void initComponents() {
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);
		// elements for addTable

		nameLabel = new JLabel();
		nameField = new JTextField();

		// elements for setting the x position
		priceLabel = new JLabel();
		priceField = new JTextField();

		// elements for setting the y position
		categoryLabel = new JLabel();
		categoryToggleList = new JComboBox<String>(new String[0]);
		categoryToggleList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JComboBox<String> cb = (JComboBox<String>) evt.getSource();
				selectedCategoryIndex = cb.getSelectedIndex();
			}
		});

		addButton = new JButton();
		cancelButton = new JButton();

		nameLabel.setText("Set name for the MenuItem");
		priceLabel.setText("Set price");
		categoryLabel.setText("Set item category");

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		addButton.setText("Save");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				UpdateButtonActionPerformed(evt);
			}
		});

		this.getContentPane().setBackground(new java.awt.Color(200, 200, 200));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(errorMessage).addComponent(nameLabel)
				.addComponent(nameField).addComponent(priceLabel).addComponent(priceField).addComponent(categoryLabel)
				.addComponent(categoryToggleList)

				.addGroup(layout.createSequentialGroup().addComponent(cancelButton).addComponent(addButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] { nameLabel, nameField, priceLabel,
				priceField, categoryLabel, categoryToggleList });
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] { cancelButton, addButton });

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(errorMessage).addComponent(nameLabel)
				.addComponent(nameField).addComponent(priceLabel).addComponent(priceField).addComponent(categoryLabel)
				.addComponent(categoryToggleList)
				.addGroup(layout.createParallelGroup().addComponent(cancelButton).addComponent(addButton)));

		pack();

	}

	public void refreshData() {
		errorMessage.setText(error);

		if (error == null || error.length() == 0) {
			//populate category toggle list
			categories = new HashMap<Integer, ItemCategory>();
			categoryToggleList.removeAllItems();
			Integer index = 0;
			for (ItemCategory category: RestoAppController.getItemCategories()) {
				categories.put(index, category);
				categoryToggleList.addItem(category.toString());
				index++;
			}
			selectedCategoryIndex = 0;
			categoryToggleList.setSelectedIndex(selectedCategoryIndex);

			nameField.setText("");
			priceField.setText("");
			
		}
		pack();

	}

	public void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);

	}

	public void UpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = "";
		String name = null;
		double price = 0;
		ItemCategory category = null;

		try {

			name = nameField.getText();
			price = Double.parseDouble(priceField.getText());
			category = categories.get(selectedCategoryIndex);
		}

		catch (NumberFormatException e) {
			error = "Please fill the text fields";
			refreshData();
			return;
		}
		try {
			RestoAppController.addMenuItem(name, category, price);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		refreshData();

	}
}

