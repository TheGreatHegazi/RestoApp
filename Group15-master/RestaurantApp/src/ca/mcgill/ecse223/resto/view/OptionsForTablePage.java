package ca.mcgill.ecse223.resto.view;

import javax.swing.JFrame;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.Table;

public class OptionsForTablePage extends JFrame {

	RestoAppPage rootPage;
	Table table;

	// UI elements
	private JLabel errorMessage;

	// elements for set table number
	private JLabel setTableNumberLabel;
	private JTextField tableNumberTextfield;

	// elements for set nuber of seats
	private JLabel setNumberOfSeatsLabel;
	private JTextField numberOfSeatsTextfield;

	// buttons
	private JButton cancelButton;
	private JButton saveChangesButton;

	// date elements
	private String error = null;

	public OptionsForTablePage(RestoAppPage rootPage, Table table) {
		this.rootPage = rootPage;
		this.table = table;
		initComponents();
		refreshData();
	}

	private void initComponents() {
		// elements for error message
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		// elements for set options for table

		setTableNumberLabel = new JLabel();
		tableNumberTextfield = new JTextField();
		setNumberOfSeatsLabel = new JLabel();
		numberOfSeatsTextfield = new JTextField();
		cancelButton = new JButton();
		saveChangesButton = new JButton();

		// global listeners and setting

		setTitle("Set Options for Table");
		setTableNumberLabel.setText("Set Table Number");
		setNumberOfSeatsLabel.setText("Set Number of Seats");

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		saveChangesButton.setText("Save Changes");
		saveChangesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveChangesButtonActionPerformed(evt);
			}
		});

		// layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(errorMessage)
				.addComponent(setTableNumberLabel).addComponent(tableNumberTextfield)
				.addComponent(setNumberOfSeatsLabel).addComponent(numberOfSeatsTextfield)
				.addGroup(layout.createSequentialGroup().addComponent(cancelButton).addComponent(saveChangesButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] { errorMessage, setTableNumberLabel,
				tableNumberTextfield, setNumberOfSeatsLabel, numberOfSeatsTextfield });

		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(errorMessage)
				.addComponent(setTableNumberLabel).addComponent(tableNumberTextfield)
				.addComponent(setNumberOfSeatsLabel).addComponent(numberOfSeatsTextfield)
				.addGroup(layout.createParallelGroup().addComponent(cancelButton).addComponent(saveChangesButton)));

		pack();

	}

	private void refreshData() {
		errorMessage.setText(error);
		if (error == null || error.length() == 0) {
			tableNumberTextfield.setText(Integer.toString(table.getNumber()));
			numberOfSeatsTextfield.setText(Integer.toString(table.getCurrentSeats().size()));
		}
		pack();

	}

	public void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}

	public void saveChangesButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = "";
		int newNumber = 0;
		int numberOfSeats = 0;
		try {
			newNumber = Integer.parseInt(tableNumberTextfield.getText());
			numberOfSeats = Integer.parseInt(numberOfSeatsTextfield.getText());
		} catch (NumberFormatException e) {
			if (tableNumberTextfield.getText().isEmpty() || numberOfSeatsTextfield.getText().isEmpty()) {
				error = "Please fill all the text fields!";
			}
			if (!(tableNumberTextfield.getText().isEmpty())){
				error = "Must input a positive integer for new table number! ";
			}
			if (!(numberOfSeatsTextfield.getText().isEmpty())){
				error  = error + "Must input a positive integer for number of seats!";
			}
			refreshData();
			return;
		}

		try {
			RestoAppController.updateTable(table, newNumber, numberOfSeats);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		refreshData();

		rootPage.updateTable(table);
	}
}
