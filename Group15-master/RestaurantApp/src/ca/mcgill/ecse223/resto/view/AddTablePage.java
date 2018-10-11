package ca.mcgill.ecse223.resto.view;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.RestoApp;

public class AddTablePage extends JFrame {

	private RestoAppPage rootPage;

	//UI elements
	private JLabel errorMessage;

	//elements for setting table number;
	private JLabel tableNumberLabel;
	private JTextField tableNumberTextfield;

	//elements for setting number of seats
	private JLabel numberOfSeatsLabel;
	private JTextField numberOfSeatsTextfield;

	//elements for setting the x position
	private JLabel xPosLabel;
	private JTextField xPosTextfield;

	//elements for setting the y position
	private JLabel yPosLabel;
	private JTextField yPosTextfield;

	//elements for setting the width
	private JLabel widthLabel;
	private JTextField widthTextfield;

	//elements for setting the length
	private JLabel lengthLabel;
	private JTextField lengthTextfield;

	//elements for cancel button
	private JButton cancelButton;

	//elements for add table button
	private JButton addTableButton;

	//data elements
	private String error;


	public AddTablePage(RestoAppPage rootPage) {
		this.rootPage = rootPage;
		initComponents();
		refreshData();
	}



	private void initComponents() {

		// elements for error message
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		//elements for addTable
		tableNumberLabel = new JLabel();
		tableNumberTextfield = new JTextField();
		numberOfSeatsLabel = new JLabel();
		numberOfSeatsTextfield = new JTextField();

		//elements for setting the x position
		xPosLabel = new JLabel();
		xPosTextfield = new JTextField();

		//elements for setting the y position
		yPosLabel = new JLabel();
		yPosTextfield = new JTextField();

		//elements for setting the width
		widthLabel = new JLabel();
		widthTextfield = new JTextField();

		//elements for setting the length
		lengthLabel = new JLabel();
		lengthTextfield = new JTextField();

		//elements for cancel button
		cancelButton = new JButton();

		//elements for add table button
		addTableButton = new JButton();


		//global setting and listeners
		tableNumberLabel.setText("Set Table Number");
		numberOfSeatsLabel.setText("Set Number Of Seats");
		xPosLabel.setText("Set X-Position");
		yPosLabel.setText("Set Y-Position");
		widthLabel.setText("Set Width");
		lengthLabel.setText("Set length");

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		addTableButton.setText("Add Table");
		addTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addTableButtonActionPerformed(evt);
			}
		});


		this.getContentPane().setBackground(new java.awt.Color(200,200,200));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(errorMessage)
				.addComponent(tableNumberLabel)
				.addComponent(tableNumberTextfield)
				.addComponent(numberOfSeatsLabel)
				.addComponent(numberOfSeatsTextfield)
				.addComponent(xPosLabel)
				.addComponent(xPosTextfield)
				.addComponent(yPosLabel)
				.addComponent(yPosTextfield)
				.addComponent(widthLabel)
				.addComponent(widthTextfield)
				.addComponent(lengthLabel)
				.addComponent(lengthTextfield)
				.addGroup(layout.createSequentialGroup()
						.addComponent(cancelButton)
						.addComponent(addTableButton))
				);

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {tableNumberLabel, tableNumberTextfield,numberOfSeatsLabel,numberOfSeatsTextfield,xPosLabel,xPosTextfield,yPosLabel,yPosTextfield,
				widthLabel,widthTextfield, lengthLabel, lengthTextfield});
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, addTableButton});

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(errorMessage)
				.addComponent(tableNumberLabel)
				.addComponent(tableNumberTextfield)
				.addComponent(numberOfSeatsLabel)
				.addComponent(numberOfSeatsTextfield)
				.addComponent(xPosLabel)
				.addComponent(xPosTextfield)
				.addComponent(yPosLabel)
				.addComponent(yPosTextfield)
				.addComponent(widthLabel)
				.addComponent(widthTextfield)
				.addComponent(lengthLabel)
				.addComponent(lengthTextfield)
				.addGroup(layout.createParallelGroup()
						.addComponent(cancelButton)
						.addComponent(addTableButton))
				);

		pack();




	}

	private void refreshData() {
		errorMessage.setText(error);

		if (error == null || error.length() == 0) {
			tableNumberTextfield.setText("");
			xPosTextfield.setText("");
			yPosTextfield.setText("");
			widthTextfield.setText("");
			lengthTextfield.setText("");
			numberOfSeatsTextfield.setText("");
		}
		pack();


	}


	public void addTableButtonActionPerformed(java.awt.event.ActionEvent evt){
		RestoApp r = RestoApplication.getRestoApp();
		error = "";
		int tableNum = 0;
		int x = 0;
		int y = 0;
		int width = 0; 
		int length = 0;
		int numOfSeats = 0;
		try {
			tableNum = Integer.parseInt(tableNumberTextfield.getText());
			x = Integer.parseInt(xPosTextfield.getText());
			y = Integer.parseInt(yPosTextfield.getText());
			width = Integer.parseInt(widthTextfield.getText());
			length = Integer.parseInt(lengthTextfield.getText());
			numOfSeats = Integer.parseInt(numberOfSeatsTextfield.getText());
		}
		catch(NumberFormatException e) {
			error = "Please fill the text fields";
			refreshData();
			return;
		}


		try {
			RestoAppController.createTable(tableNum, x, y, width, length, numOfSeats);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}



		refreshData();

		if (error.length() == 0){

			int currentTableListSize = r.getCurrentTables().size();
			if (currentTableListSize > 0) {
				rootPage.displayTable(r.getCurrentTable(r.getCurrentTables().size()-1));
			}
		}

	}

	public void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}



}
