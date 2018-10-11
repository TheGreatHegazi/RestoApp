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
public class UpdateMenuItem extends JFrame {

	// UI elements
	private JLabel errorMessage;

	// elements for setting table number;


	// elements for setting number of seats
	private JLabel newNameLabel;
	private JTextField newNameField;

	// elements for setting thex position
	private JLabel categoryLabel;
	private HashMap<Integer, ItemCategory> categories;
	private ItemCategory selectedCategory = ItemCategory.Appetizer;


	private JButton displayMenuButton;
	private JComboBox<String> categoryToggleList;
	private Integer selectedCategoryIndex = 0;

	// elements for setting the y position
	private JLabel priceLabel;
	private JTextField priceField;

	// elements for cancel button
	private JButton cancelButton;

	// elements for add table button
	private JButton UpdateButton;
	private MenuItem menuitems;
	// data elements
	private String error;

	public UpdateMenuItem(MenuItem menuitems) {
		this.menuitems = menuitems;
		initComponents();
		refreshData();
	}

	public void initComponents() {
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		newNameLabel = new JLabel();
		newNameField = new JTextField();

		// elements for setting the x position
		priceLabel = new JLabel();
		priceField = new JTextField();

		// elements for setting the y position
		categoryLabel = new JLabel();
		categoryLabel = new JLabel();
		categoryToggleList = new JComboBox<String>(new String[0]);
		categoryToggleList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JComboBox<String> cb = (JComboBox<String>) evt.getSource();
				selectedCategoryIndex = cb.getSelectedIndex();
			}
		});
		UpdateButton = new JButton();
		cancelButton = new JButton();


		newNameLabel.setText("Set new name for the MenuItem");
		priceLabel.setText("Set new price");
		categoryLabel.setText("Set item category");

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		UpdateButton.setText("Update");
		UpdateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				UpdateButtonActionPerformed(evt);
			}
		});

		this.getContentPane().setBackground(new java.awt.Color(200, 200, 200));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(errorMessage)
				.addComponent(newNameLabel).addComponent(newNameField)
				.addComponent(priceLabel).addComponent(priceField).addComponent(categoryLabel)
				.addComponent(categoryToggleList)

				.addGroup(layout.createSequentialGroup().addComponent(cancelButton).addComponent(UpdateButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {  newNameLabel,
				newNameField, priceLabel, priceField, categoryLabel, categoryToggleList });
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] { cancelButton, UpdateButton });

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(errorMessage)
				.addComponent(newNameLabel).addComponent(newNameField)
				.addComponent(priceLabel).addComponent(priceField).addComponent(categoryLabel)
				.addComponent(categoryToggleList)
				.addGroup(layout.createParallelGroup().addComponent(cancelButton).addComponent(UpdateButton)));

		pack();

	}

	public void refreshData() {
		errorMessage.setText(error);

		if (error == null || error.length() == 0) {
			newNameField.setText("");
			priceField.setText("");
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

		}
		pack();

	}

	public void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	
	}

	public void UpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = "";
		MenuItem MI = null;
		String name = null;
		double price = 0;
		ItemCategory category = null;

		try {

			MI = menuitems;
			name = newNameField.getText();
			price = Double.parseDouble(priceField.getText());
		category = categories.get(selectedCategoryIndex);
		}

		catch (NumberFormatException e) {
			error = "Please fill the text fields";
			refreshData();
			return;
		}
		try {
			RestoAppController.updateMenuItem(MI, name, category, price);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		refreshData();

	}
}
