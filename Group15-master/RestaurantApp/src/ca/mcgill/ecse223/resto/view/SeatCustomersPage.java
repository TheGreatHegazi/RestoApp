package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;

import ca.mcgill.ecse223.resto.model.Table;

public class SeatCustomersPage extends JFrame {

	// Elements for UI
	private RestoAppPage mainPage;
	
	private JLabel errorMessage;
	private String error = null;

	// Elements for selecting a table
	private JLabel selectTableLabel;
	private JList<String> selectTableList;
	private DefaultListModel<String> model;
	private HashMap<Integer, Table> tables;
	private List<Table> selectedTables = new ArrayList<Table>();

	// Elements for cancel and save changes
	private JButton cancelButton;
	private JButton markInUseButton;

	public SeatCustomersPage(RestoAppPage mainPage) {
		this.mainPage = mainPage;
		initComponents();
		refreshData();
	}

	private void initComponents() {

		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		model = new DefaultListModel<>();
		selectTableLabel = new JLabel();
		selectTableList = new JList<String>(model);

		List<Table> currentTablesList = RestoAppController.getCurrentTables();
		for (Table table : currentTablesList) {
			model.addElement(table.getNumber() + "");

		}

		selectTableList = new JList<>(model);
		selectTableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectTableList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		selectTableList.setVisibleRowCount(5);
		JScrollPane listScroller = new JScrollPane(selectTableList);
		listScroller.setPreferredSize(new Dimension(250, 80));

		selectTableList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
				}

			}
		});

		selectTableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectTableList.setSelectedIndex(5);
		selectTableList.setVisibleRowCount(10);

		// Elements for cancel and save changes
		cancelButton = new JButton();
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		markInUseButton = new JButton();
		markInUseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				markInUseButtonActionPerformed(evt);
			}
		});

		// global settings
		selectTableLabel.setText("Select Table");
		cancelButton.setText("Cancel");
		markInUseButton.setText("Mark In Use");

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(errorMessage).addComponent(selectTableLabel)
				.addComponent(selectTableList)
				.addGroup(layout.createSequentialGroup().addComponent(cancelButton).addComponent(markInUseButton))

		);

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] { selectTableLabel, selectTableList });
		layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] { selectTableLabel, selectTableList });
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(errorMessage).addComponent(selectTableLabel)
				.addComponent(selectTableList)
				.addGroup(layout.createParallelGroup().addComponent(cancelButton).addComponent(markInUseButton)));

		pack();

	}

	private void refreshData() {
		errorMessage.setText(error);
		if (error == null || error.length() == 0) {
			// populate page with data
			// select category
			tables = new HashMap<Integer, Table>();
			selectTableList.removeAll();
			int index = 0;
			model = new DefaultListModel<>();
			for (Table table : RestoAppController.getCurrentTables()) {
				model.addElement(table.getNumber() + "");
				tables.put(index, table);
				index++;
			}

		}

		pack();
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}

	private void markInUseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = null;
		int i = 0;
		int[] selectedIndicesArray = selectTableList.getSelectedIndices();
		if (selectedIndicesArray.length == 0) {
			error = "Please select a table!";
			refreshData();
			return;
		}
		while (i < selectedIndicesArray.length) {
			selectedTables.add(tables.get(selectedIndicesArray[i]));
			i++;
		}

		try {
			error = RestoAppController.startOrder(selectedTables);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		} finally {
			refreshData();
			mainPage.plotTables();
		}

	}
}
