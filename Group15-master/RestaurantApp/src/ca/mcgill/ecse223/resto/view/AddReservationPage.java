package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.awt.Dimension;
import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.Table;
import ca.mcgill.ecse223.resto.view.DateLabelFormatter;



public class AddReservationPage extends JFrame {

	private RestoAppPage mainPage;
	//UI elements
	private JLabel errorMessage;	

	//Elements for selecting a table
	private JLabel selectTablesLabel;
	private JList<String> tableJList;
	private JButton addTableButton;

	//	private JCheckBox addTableCheckBox;
	private JLabel selectedTablesLabel;

	//Elements setting the number of people
	private JLabel setNumOfPeopleLabel;
	private JTextField setNumOfPeopleTextField;

	//Elements for entering contact name:
	private JLabel setContactNameLabel;
	private JTextField setContactNameTextField;

	//Elements for entering email address
	private JLabel setEmailLabel;
	private JTextField setEmailTextField;

	//Elements for entering phone number
	private JLabel setPhoneNumLabel;
	private JTextField setPhoneNumTextField;

	//Elements for selecting Date
	private JLabel setDateLabel;
	private JDatePickerImpl datePicker;

	//Elements for selecting time
	private JLabel setTimeLabel;
	private com.github.lgooddatepicker.components.TimePicker timePicker;

	//Elements for Save Changes and Cancel
	private JButton cancelButton;
	private JButton saveChangesButton;

	//data elements
	String error = null;
	private HashMap<Integer, Table> tables;
	private List<Table> selectedTables = new ArrayList<Table>();
	private String addTablesString = "";
	private DefaultListModel<String> l1;


	public AddReservationPage(RestoAppPage mainPage) {
		this.mainPage = mainPage;
		initComponents();
		refreshData();
	}

	private void initComponents() {
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		//Elements for selecting a table
		selectTablesLabel = new JLabel();

		//-----------------------------------//


		l1 = new DefaultListModel<>();



		List<Table> currentTablesList = RestoAppController.getCurrentTables();
		for (Table table: currentTablesList) {
			l1.addElement(table.getNumber() + "");	

		}
		tableJList = new JList<>(l1);
		tableJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tableJList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		tableJList.setVisibleRowCount(5);
		JScrollPane listScroller = new JScrollPane(tableJList);
		listScroller.setPreferredSize(new Dimension(250, 80));


		tableJList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
				}
				
			}
		});

		addTableButton = new JButton();
		addTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addTableButtonActionPerformed(evt);
			}
		});


		//------------------------------------//

		selectedTablesLabel = new JLabel();


		//Elements setting the number of people
		setNumOfPeopleLabel = new JLabel();
		setNumOfPeopleTextField = new JTextField();

		//Elements for entering contact name:
		setContactNameLabel = new JLabel();
		setContactNameTextField = new JTextField();

		//Elements for entering email address
		setEmailLabel = new JLabel();
		setEmailTextField = new JTextField();

		//Elements for entering phone number
		setPhoneNumLabel = new JLabel();
		setPhoneNumTextField = new JTextField();

		//Elements for selecting Date
		setDateLabel = new JLabel();
		SqlDateModel model = new SqlDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		//elements for selecting time
		setTimeLabel = new JLabel();
		timePicker = new com.github.lgooddatepicker.components.TimePicker();

		//elements for cancel and save
		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		saveChangesButton = new JButton();
		saveChangesButton.setText("Save Changes");
		saveChangesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveChangesButtonActionPerformed(evt);
			}
		});


		//global setting and listeners
		addTableButton.setText("Add selected tables");
		selectTablesLabel.setText("Select Tables For Reservation");
		selectedTablesLabel.setText("Selected tables for reservation: ");
		setNumOfPeopleLabel.setText("Enter Number Of People In Party");
		setContactNameLabel.setText("Enter Contact Name");
		setEmailLabel.setText("Enter E-mail Address");
		setPhoneNumLabel.setText("Enter Phone Numbet");
		setDateLabel.setText("Select Date For Reservation");
		setTimeLabel.setText("Select Time For Reservation");
		cancelButton.setText("Cancel");
		saveChangesButton.setText("Save Changes");



		this.getContentPane().setBackground(new java.awt.Color(200,200,200));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(errorMessage)
				.addComponent(selectTablesLabel)
				.addComponent(tableJList)
				.addComponent(addTableButton)
				.addComponent(selectedTablesLabel)
				.addComponent(setNumOfPeopleLabel)
				.addComponent(setNumOfPeopleTextField)
				.addComponent(setContactNameLabel)
				.addComponent(setContactNameTextField)
				.addComponent(setEmailLabel)
				.addComponent(setEmailTextField)
				.addComponent(setPhoneNumLabel)
				.addComponent(setPhoneNumTextField)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(setDateLabel)
								.addComponent(datePicker))
						.addGroup(layout.createParallelGroup()
								.addComponent(setTimeLabel)
								.addComponent(timePicker)))
				.addGroup(layout.createSequentialGroup()
						.addComponent(cancelButton)
						.addComponent(saveChangesButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {selectTablesLabel, selectedTablesLabel, setNumOfPeopleLabel, setNumOfPeopleTextField,setContactNameLabel, setContactNameTextField, setEmailLabel, setEmailTextField, setPhoneNumLabel, 
				setPhoneNumTextField, setDateLabel, datePicker, tableJList, cancelButton, saveChangesButton, setTimeLabel, timePicker});
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, saveChangesButton});
		layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] {selectTablesLabel, selectedTablesLabel, setNumOfPeopleLabel, setNumOfPeopleTextField,setContactNameLabel, setContactNameTextField, setEmailLabel, setEmailTextField, setPhoneNumLabel, 
				setPhoneNumTextField, setDateLabel, datePicker, cancelButton, saveChangesButton, setTimeLabel, timePicker});

		layout.setVerticalGroup(
				layout.createSequentialGroup()	
				.addComponent(errorMessage)
				.addComponent(selectTablesLabel)
				.addComponent(tableJList)
				.addComponent(addTableButton)
				.addComponent(selectedTablesLabel)
				.addComponent(setNumOfPeopleLabel)
				.addComponent(setNumOfPeopleLabel)
				.addComponent(setNumOfPeopleTextField)
				.addComponent(setContactNameLabel)
				.addComponent(setContactNameTextField)
				.addComponent(setEmailLabel)
				.addComponent(setEmailTextField)
				.addComponent(setPhoneNumLabel)
				.addComponent(setPhoneNumTextField)
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(setDateLabel)
								.addComponent(datePicker))
						.addGroup(layout.createSequentialGroup()
								.addComponent(setTimeLabel)
								.addComponent(timePicker)))
				.addGroup(layout.createParallelGroup()
						.addComponent(cancelButton)
						.addComponent(saveChangesButton)));



		pack();
	}

	private void refreshData() {
		errorMessage.setText(error);

		if (error == null || error.length() == 0) {
			//populate page with data
			//select table
			int i = 0;
			tables = new HashMap<Integer, Table>();
			l1 = new DefaultListModel<>();
			List<Table> currentTablesList = RestoAppController.getCurrentTables();
			for (Table table: currentTablesList) {
				l1.addElement(table.getNumber() + "");	
				tables.put(i, table);
				i++;
			}

			setNumOfPeopleTextField.setText("");
			setContactNameTextField.setText("");
			setPhoneNumTextField.setText("");
			setEmailTextField.setText("");

		}



		pack();
	}


	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}
	private void saveChangesButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = null;
		int numOfPeople = 0;
		if (selectedTables == null) {
			error = "Invalid INput Exception: Please select a table to reserve";
		}
		else {
			try{
				numOfPeople = Integer.parseInt(setNumOfPeopleTextField.getText());
			}
			catch (NumberFormatException e) {
				error = "Invalid Input: Party Size must be an integer!";
				refreshData();
				return;
			}
			String contactName = setContactNameTextField.getText();
			String phoneNumber = setPhoneNumTextField.getText();
			String email = setEmailTextField.getText();
			java.sql.Date date = (Date) datePicker.getModel().getValue();
			if(date == null) {
				error = "Invalid Input: date needs to be selected";
				refreshData();
				return;
			}
			LocalTime time = timePicker.getTime();
			if(time == null) {
				error = "Invalid Input: time needs to be selected";
				refreshData();
				return;
			}
			java.sql.Time time1 = new java.sql.Time(time.getHour(), time.getMinute(), time.getSecond());
			
			try {
				RestoAppController.reserveTable(date, time1, numOfPeople, contactName, email, phoneNumber, selectedTables);
			}
			catch(InvalidInputException e) {
				error = e.getMessage();
			} finally {
				refreshData();
				mainPage.plotTables();
			}
		}
	}


	private void addTableButtonActionPerformed(java.awt.event.ActionEvent evt) {
		int i = 0;
		int[] selectedIndicesArray = tableJList.getSelectedIndices();
		while (i < selectedIndicesArray.length) {
			if (!selectedTables.contains(tables.get(selectedIndicesArray[i]))) {
				selectedTables.add((Table)tables.get(selectedIndicesArray[i]));
			}
			i++;
		}
		addTablesString = "";
		for (int x = 0; x < selectedTables.size(); x++) {
			addTablesString += (selectedTables.get(x).getNumber() + " ");	
		}
		selectedTablesLabel.setText("Selected tables for reservation: " + addTablesString );



	}


}






