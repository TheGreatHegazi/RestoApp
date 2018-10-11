package ca.mcgill.ecse223.resto.view;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;
import ca.mcgill.ecse223.resto.model.Order;
import ca.mcgill.ecse223.resto.model.Seat;
import ca.mcgill.ecse223.resto.model.Table;

public class AddOrderItemPage extends JFrame{

	private RestoAppPage mainPage;

	//UI elements
	private JLabel errorMessage;

	//menu Item Category
	private JLabel categoryLabel;
	private JButton displayMenuButton;
	private JComboBox<String> categoryToggleList;

	//elements for displaying menu
	private JList<String> menuJList;

	//elements for selecting an order
	private JLabel setOrderLabel;
	private JComboBox<String> orderToggleList;

	//elements for editing menu
	private JButton addMenuItemButton;
	private JButton updateMenuItemButton;
	private JButton removeMenuItemButton;




	//elements for setting quantity
	private JLabel quantityLabel;
	private JTextField quantityTextField;

	//elements for selecting seats
	private JLabel seatsLabel;
	private JList<String> seatsJList;


	//elements for canceling and confirming
	private JButton cancelButton;
	private JButton orderItemButton;

	//data elements
	String error = null;
	private HashMap<Integer, ItemCategory> categories;
	private ItemCategory selectedCategory = ItemCategory.Appetizer;
	private Integer selectedCategoryIndex = 0;

	private Integer selectedOrderIndex = -1;
	private HashMap<Integer, Order> orders;
	private Order selectedOrder;

	private HashMap<Integer, MenuItem> menuItems;
	private MenuItem selectedMenuItem;

	private DefaultListModel<String> l1;


	private List<Seat> selectedSeats = new ArrayList<Seat>();
	private HashMap<Integer, Seat> seats;
	private DefaultListModel<String> l2;



	public AddOrderItemPage() {
		//this.mainPage = mainPage;
		initComponents();
		refreshData();
	}

	private void initComponents() {
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		//menu Item Category
		categoryLabel = new JLabel();
		categoryToggleList = new JComboBox<String>(new String[0]);
		categoryToggleList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JComboBox<String> cb = (JComboBox<String>) evt.getSource();
				selectedCategoryIndex = cb.getSelectedIndex();
			}
		});

		//display menu
		displayMenuButton = new JButton();
		displayMenuButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				displayMenuButtonActionPerformed(evt);
			}
		});

		l1 = new DefaultListModel<>();
		List<MenuItem> currentMenuItems = new ArrayList<MenuItem>();
		try {
			currentMenuItems = RestoAppController.getMenuItems(selectedCategory);
		}
		catch(InvalidInputException e) {}
		for (MenuItem menuItem: currentMenuItems) {
			l1.addElement(menuItem.getName() + "");	
		}
		menuJList = new JList<>(l1);
		menuJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		menuJList.setLayoutOrientation(JList.VERTICAL_WRAP);
		menuJList.setVisibleRowCount(11);
		JScrollPane listScroller = new JScrollPane(menuJList);
		listScroller.setPreferredSize(new Dimension(250, 80));


		menuJList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
				}

			}
		});


		//elements for editing menu
		addMenuItemButton = new JButton();
		addMenuItemButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addMenuItemButtonActionPerformed(evt);
			}
		});

		updateMenuItemButton  = new JButton();
		updateMenuItemButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateMenuItemButtonActionPerformed(evt);
			}
		});
		removeMenuItemButton  = new JButton();
		removeMenuItemButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				removeMenuItemButtonActionPerformed(evt);
			}
		});


		//elements for setting quantity
		quantityLabel = new JLabel();
		quantityTextField = new JTextField();

		//elements for selecting order
		setOrderLabel = new JLabel();
		orderToggleList = new JComboBox<String>(new String[0]);
		orderToggleList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JComboBox<String> cb = (JComboBox<String>) evt.getSource();
				selectedOrderIndex = cb.getSelectedIndex();
				selectOrderActionPerformed(evt);
				//				l2.removeAllElements();
			}
		});


		//selecting seats
		seatsLabel = new JLabel();
		l2 = new DefaultListModel<>();
		List<Seat> currentSeats = new ArrayList<Seat>();
		currentSeats = RestoAppController.getCurrentSeats();
		int j = 0;
		seats = new HashMap<Integer, Seat>();
		for (Seat seat: currentSeats) {
			l2.addElement("Table " + seat.getTable().getNumber() + ": " + "seat" + " " + j);
			seats.put(j, seat);
			j++;
		}
		seatsJList = new JList<>(l2);
		seatsJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		seatsJList.setLayoutOrientation(JList.VERTICAL_WRAP);
		seatsJList.setVisibleRowCount(5);
		JScrollPane listScroller2 = new JScrollPane(seatsJList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		seatsJList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
				}

			}
		});

		//elements for canceling and confirming
		cancelButton = new JButton();
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		orderItemButton = new JButton();
		orderItemButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				orderItemButtonActionPerformed(evt);
			}
		});


		//universal settings
		this.setTitle("Menu Page");

		categoryLabel.setText("Select Category");
		displayMenuButton.setText("Display Menu");
		quantityLabel.setText("Set Quantity");
		seatsLabel.setText("Select Seats");
		setOrderLabel.setText("Select Order");
		orderItemButton.setText("Add Order Item");
		cancelButton.setText("Cancel");
		addMenuItemButton.setText("Add Menu Item");
		removeMenuItemButton.setText("Remove Menu Item");
		updateMenuItemButton.setText("Update Menu Item");

		this.getContentPane().setBackground(new java.awt.Color(200,200,200));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(errorMessage)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(categoryLabel)
								.addComponent(categoryToggleList))
						.addGroup(layout.createParallelGroup()
								.addComponent(displayMenuButton)
								.addComponent(addMenuItemButton)
								.addComponent(removeMenuItemButton)
								.addComponent(updateMenuItemButton)))
				.addComponent(menuJList)
				.addComponent(setOrderLabel)
				.addComponent(orderToggleList)
				.addComponent(seatsLabel)
				.addComponent(seatsJList)
				.addComponent(quantityLabel)
				.addComponent(quantityTextField)
				.addGroup(layout.createSequentialGroup()
						.addComponent(cancelButton)
						.addComponent(orderItemButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {categoryLabel, categoryToggleList, seatsJList, menuJList}) ;
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, orderItemButton});
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {displayMenuButton, addMenuItemButton, removeMenuItemButton, updateMenuItemButton});
		layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] { categoryLabel, categoryToggleList, quantityLabel, quantityTextField, displayMenuButton, 
				cancelButton, orderItemButton});

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(errorMessage)
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(categoryLabel)
								.addComponent(categoryToggleList))
						.addGroup(layout.createSequentialGroup()
								.addComponent(displayMenuButton)
								.addComponent(addMenuItemButton)
								.addComponent(removeMenuItemButton)
								.addComponent(updateMenuItemButton)))
				.addComponent(menuJList)
				.addComponent(setOrderLabel)
				.addComponent(orderToggleList)
				.addComponent(seatsLabel)
				.addComponent(seatsJList)
				.addComponent(quantityLabel)
				.addComponent(quantityTextField)
				.addGroup(layout.createParallelGroup()
						.addComponent(cancelButton)
						.addComponent(orderItemButton)));

		pack();
	}


	private void refreshData() {
		int savedIndex = selectedCategoryIndex;
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
			//populate order toggle list
			orders = new HashMap<Integer, Order>();
			orderToggleList.removeAllItems();

			Integer index2 = 0;
			for (Order order: RestoAppController.getCurrentOrders()) {
				orders.put(index2, order);
				String boxMessage = "Order: " + order.getNumber() + " (Tables " + order.getTable(0).getNumber();
				for(int i = 1; i<order.getTables().size(); i++) {
					boxMessage += ", " + order.getTable(i).getNumber() + " ";
				}
				boxMessage += ")";
				orderToggleList.addItem(boxMessage);
				index++;
			}
			selectedOrderIndex = -1;
			orderToggleList.setSelectedIndex(selectedOrderIndex);
			orderToggleList.setSelectedItem(orders.get(selectedOrderIndex));
//			System.out.println(menuItems.values().toString());
			quantityTextField.setText("");
			error = null;
			
			selectedCategoryIndex = savedIndex ;
		
			categoryToggleList.setSelectedIndex(selectedCategoryIndex);
			List<MenuItem> currentMenuItems = new ArrayList<MenuItem>();
			try {
				currentMenuItems = RestoAppController.getMenuItems(selectedCategory);
				int i = 0;
				menuItems = new HashMap<Integer, MenuItem>();
				l1.removeAllElements();
				for (MenuItem menuItem: currentMenuItems) {
					l1.addElement(menuItem.getName() + "");	
					menuItems.put(i, menuItem);
					i++;
				}
			}
			catch(InvalidInputException e) {
				error = e.getMessage();

			}

		}
		pack();
	}


	private void orderItemButtonActionPerformed(java.awt.event.ActionEvent evt){
		error = null;
		Integer i = 0;
		try {
			i = Integer.parseInt(quantityTextField.getText());
		}
		catch(NumberFormatException e) {
			error = "Please Enter Quantity";
		}
		int x = 0;
		int y = menuJList.getSelectedIndex();
		if (y == -1) {
			selectedMenuItem = null;
		}
		else {
			selectedMenuItem = menuItems.get(menuJList.getSelectedIndex());
		}
		int[] selectedSeatsIndicesArray = seatsJList.getSelectedIndices();
		while (x < selectedSeatsIndicesArray.length) {
			selectedSeats.add((Seat)seats.get(selectedSeatsIndicesArray[x]));
			x++;
		}

		try {
			RestoAppController.orderMenuItem(selectedMenuItem, i, selectedSeats);
		}
		catch (InvalidInputException e) {
			error = e.getMessage();
		}
		refreshData();
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt){
		this.setVisible(false);
	}

	private void displayMenuButtonActionPerformed(java.awt.event.ActionEvent evt){
		error = null;
		selectedCategory = categories.get(selectedCategoryIndex);
//		List<MenuItem> currentMenuItems = new ArrayList<MenuItem>();
//		try {
//			currentMenuItems = RestoAppController.getMenuItems(selectedCategory);
//			int i = 0;
//			menuItems = new HashMap<Integer, MenuItem>();
//			l1.removeAllElements();
//			for (MenuItem menuItem: currentMenuItems) {
//				l1.addElement(menuItem.getName() + "");	
//				menuItems.put(i, menuItem);
//				i++;
//			}
//		}
//		catch(InvalidInputException e) {
//			error = e.getMessage();
//
//		}

		refreshData();

	}


	private void selectOrderActionPerformed(java.awt.event.ActionEvent evt) {
		selectedOrder = orders.get(selectedOrderIndex);
		//populate seats list according to selected order
		int i = 0;
		l2.removeAllElements();
		seats = new HashMap<Integer, Seat>();

		if (selectedOrder != null) {
			for (Table table: selectedOrder.getTables()) {
				for (Seat seat: table.getCurrentSeats()) {
					l2.addElement("Table " + table.getNumber() + ": " + " Seat: " + (i+1));	
					seats.put(i, seat);
					i++;
				}
			}
			seatsJList.setVisibleRowCount(l2.getSize());
		}
		pack();
	}

	private void addMenuItemButtonActionPerformed(java.awt.event.ActionEvent evt){
		new AddNewMenuItem().setVisible(true);
		selectedCategory = categories.get(selectedCategoryIndex);
		refreshData();
	}
	private void updateMenuItemButtonActionPerformed(java.awt.event.ActionEvent evt){
		MenuItem MI = menuItems.get(menuJList.getSelectedIndex());
		error = null;
		if (MI == null) {
			error = "Please select a menu item to update";
		}
		else {
		new UpdateMenuItem(MI).setVisible(true);
		selectedCategory = categories.get(selectedCategoryIndex);
		}
		refreshData();
	}
	private void removeMenuItemButtonActionPerformed(java.awt.event.ActionEvent evt){
		error = null;
		
		try {
			RestoAppController.removeMenuItem(menuItems.get(menuJList.getSelectedIndex()));
			
		}
		catch(InvalidInputException e) {
			error = e.getMessage();
		}
		selectedCategory = categories.get(selectedCategoryIndex);
		refreshData();
	}
}
