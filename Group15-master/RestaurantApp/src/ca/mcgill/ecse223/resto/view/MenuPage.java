package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;
public class MenuPage extends JFrame {


	// UI elements
	private JLabel errorMessage;

	//Menu Item Category
	private JLabel categoryLabel;
	private JButton selectCategoryButton;
	private JComboBox<String> categoryToggleList;

	//Menu Items
	private JLabel menuItemLabel;
	private JComboBox<String> menuItemToggleList;
	//menu item visualizer
	private JButton upButton;
	private JButton downButton;
	private MenuItemVisualizer menuItemVisualizer;
	private static final int WIDTH_MENU_ITEM_VISUALIZATION = 300;
	private static final int HEIGHT_MENU_ITEM_VISUALIZATION = 300;

	public static int refreshCount;


	//data elements

	private String error = null;
	//toggle Category
	private Integer selectedCategoryIndex = -1;
	private Integer selectedIndexToUse;
	private ItemCategory selectedCategory;
	private HashMap<Integer, ItemCategory> categories;
	private List<MenuItem> menuItemList;


	public MenuPage() {
		initComponents();
		refreshData();

	}

	private void initComponents() {
		//getContentPane().setBackground(Color.DARK_GRAY);
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		categoryLabel = new JLabel();
		categoryToggleList = new JComboBox<String>(new String[0]);
		categoryToggleList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JComboBox<String> cb = (JComboBox<String>) evt.getSource();
				selectedCategoryIndex = cb.getSelectedIndex();
			}
		});
		selectCategoryButton = new JButton();


		//elements for menu item visualization

		upButton = new JButton();
		downButton = new JButton();

		menuItemVisualizer  = new MenuItemVisualizer();
		menuItemVisualizer.setMinimumSize(new Dimension(WIDTH_MENU_ITEM_VISUALIZATION, HEIGHT_MENU_ITEM_VISUALIZATION));


		// global settings and listeners

		//setting text for objects
		setTitle("Display Menu Item According To Category");
		
		
		categoryLabel.setText("Category:");
		selectCategoryButton.setText("Select Category");
		selectCategoryButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectCategoryButtonActionPerformed(evt);
			}
		});
		upButton.setText("UP");
		downButton.setText("DOWN");
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addComponent(categoryLabel)
				.addGroup(layout.createParallelGroup()
						.addComponent(errorMessage)
						.addComponent(selectCategoryButton)
						.addComponent(categoryToggleList)
						.addGroup(layout.createSequentialGroup()
								.addComponent(upButton)
								.addComponent(downButton))
						.addComponent(menuItemVisualizer)
						));
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {selectCategoryButton, categoryToggleList});
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {categoryLabel});
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {upButton, downButton});

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(categoryLabel).
						addGroup(layout.createSequentialGroup()	
								.addComponent(errorMessage)
								.addComponent(categoryToggleList )
								.addComponent(selectCategoryButton)
								.addGroup(layout.createParallelGroup()
										.addComponent(upButton)
										.addComponent(downButton))
								.addComponent(menuItemVisualizer))));
		pack();

	}

	private void refreshData() {
		errorMessage.setText(error);
		if (error == null || error.length() == 0) {
			//populate page with data
			//select category
			categories = new HashMap<Integer, ItemCategory>();
			categoryToggleList.removeAllItems();
			Integer index = 0;
			for (ItemCategory category: RestoAppController.getItemCategories()) {
				categories.put(index, category);
				categoryToggleList.addItem(category.toString());
				index++;
			}

			selectedCategoryIndex = -1;
			categoryToggleList.setSelectedIndex(selectedCategoryIndex);
			refreshCount++;

		}

		pack();
	}

	private void selectCategoryButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = "";
			selectedIndexToUse = selectedCategoryIndex;
			try {
				menuItemVisualizer.setMenuItemList(RestoAppController.getMenuItems(categories.get(selectedIndexToUse)));
			} catch (InvalidInputException e) {
				error = e.getMessage();
			}
		
		refreshData();
	}
}