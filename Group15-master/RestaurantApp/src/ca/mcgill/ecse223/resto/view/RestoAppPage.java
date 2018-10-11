package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.Reservation;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Table;

public class RestoAppPage extends JFrame {

  // Initialize RestoApp from persistence
  RestoApp r = RestoApplication.getRestoApp();
  RestoAppPage thisPage = this;

  // Initialize variables
  String error;

  boolean moveTable;
  boolean removeTable;
  boolean editTable;
  boolean issueBill;
  boolean viewOrder;

  int savedButtonX = -999;
  int savedButtonY = -999;

  // Two directional hash map mappings
  HashMap<Table, JButton> buttonsByTable;
  HashMap<JButton, Table> tables;

  JLabel errorLabel;
  JLabel instructionLabel;

  // Create task buttons
  JButton showMenuButton;
  JButton moveTableButton;
  JButton addTableButton;
  JButton editTableButton;
  JButton showMenuBotton;
  JButton removeTableButton;
  JButton addReservationButton;
  JButton seatCustomerButton;
  JButton endOrderButton;
  JButton issueBillButton;
  JButton loyaltyCardButton;

  JPanel panel;

  // List of JButtons
  List<JButton> tableList = new ArrayList<JButton>();
  List<JButton> buttonList = new ArrayList<JButton>();

  // Constructor
  public RestoAppPage() {
    initializeUI();
    refreshData();
    plotTables();
  }

  // Sets up UI initially
  private void initializeUI() {

    error = "";

    // Setting JFrame
    setSize(2000, 1000);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Set Null Layout
    panel = new JPanel(null);

    // Set Error Label
    errorLabel = new JLabel();
    errorLabel.setForeground(Color.RED);
    errorLabel.setBounds(800, 10, 400, 40);

    // Set Instruction Label
    instructionLabel = new JLabel();
    instructionLabel.setForeground(Color.BLACK);
    instructionLabel.setBounds(800, 70, 400, 40);

    // Set Task Buttons
    showMenuButton = new JButton("Show Menu");
    editTableButton = new JButton("Edit Table");
    showMenuBotton = new JButton("Show Menu");
    removeTableButton = new JButton("Remove Table");
    moveTableButton = new JButton("Move Table");
    addTableButton = new JButton("Add Table");
    addReservationButton = new JButton("Add Reservation");
    seatCustomerButton = new JButton("Seat Customers");
    endOrderButton = new JButton("Unseat Customers");
    issueBillButton = new JButton("Issue Bill");
    loyaltyCardButton = new JButton("Add Loyalty Card");

    tables = new HashMap<JButton, Table>();
    buttonsByTable = new HashMap<Table, JButton>();

    // Add content to panel
    panel.add(errorLabel);
    panel.add(instructionLabel);
    panel.add(editTableButton);
    panel.add(showMenuBotton);
    panel.add(removeTableButton);
    panel.add(moveTableButton);
    panel.add(addTableButton);
    panel.add(addReservationButton);
    panel.add(seatCustomerButton);
    panel.add(endOrderButton);
    panel.add(issueBillButton);
    panel.add(loyaltyCardButton);


    // Set bounds for the task buttons
    setButtonBounds();

    // Store task buttons in JButton array
    JButton[] buttons = { showMenuButton, moveTableButton, addTableButton, editTableButton, showMenuBotton,
        removeTableButton, addReservationButton, seatCustomerButton, endOrderButton, issueBillButton, loyaltyCardButton };


    // Adds buttons to buttonList
    for (int i = 0; i < buttons.length; i++) {
      buttonList.add(buttons[i]);
    }

    // Assigned action listeners to each button
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].addActionListener(stateListener);
    }
    setContentPane(panel);
  }

  // Added Listener for mouse dragging
  MouseMotionListener overlapDragListener = new MouseAdapter() {
    @Override
    public void mouseDragged(MouseEvent e) {
      if (tables.get(e.getSource()).getStatusFullName() != "Available") {
        ((JButton) e.getSource()).setBackground(Color.YELLOW);
      } else {
        ((JButton) e.getSource()).setBackground(Color.GREEN);
      }
      // When Move Table mode on, drag table
      if (moveTable) {
        try {
          actionPerformedCheckOverlap(e);
        } catch (InvalidInputException errorThrown) {
          ((JButton) e.getSource()).setBackground(Color.RED);
        }
      }

    }
  };

  // Added Listener for mouse release
  MouseListener moveTableReleaseListener = new MouseAdapter() {
    @Override
    public void mouseReleased(MouseEvent e) {
      // When Move Table mode on, set the table to new spot
      if (moveTable) {
        actionPerformedMoveTable(e);
      }
    }
  };

  // Added Listener for JButton press
  ActionListener stateListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      JButton button = (JButton) e.getSource();
      String buttonString = "";
      buttonString = button.getText();
      // Case: Right Hand Task Buttons;
      switch (buttonString) {
        case "Add Loyalty Card" :
          error = "";
          refreshData();
          resetStates();
          addLoyaltyCardActionPerformed();
          break;
        case "Move Table":
          error = "";
          refreshData();
          resetStates();
          moveTable = true;
          break;
        case "Remove Table":
          resetStates();
          removeTable = true;
          break;
        case "Add Table":
          resetStates();
          addTableActionPerformed();
          break;
        case "Edit Table":
          resetStates();
          editTable = true;
          break;
          // TODO
        case "Add Seats":
          resetStates();
          break;
        case "Show Menu":
          resetStates();
          showMenuActionPerformed();
          break;
        case "Add Reservation":
          resetStates();
          addReservationActionPerformed();
          break;
        case "Seat Customers":
          resetStates();
          seatCustomerActionPerformed();
          break;
        case "Unseat Customers":
          resetStates();
          endOrderActionPerformed();
          break;
        case "Issue Bill":
          resetStates();
          issueBill = true;
          break;
        case "View Order":
          resetStates();
          viewOrder = true;
          break;
          // Case: buttons representing tables
        default:

          // Remove Table mode
          if (removeTable) {
            Table table = tables.get(button);
            try {
              RestoAppController.removeTable(table);
            } catch (InvalidInputException errorThrown) {
              error = errorThrown.getMessage();
            }
            button.setVisible(false);
            panel.remove(button);
            resetStates();
            refreshData();
            break;
          }

          // Move Table mode
          if (moveTable) {
            Table selectedTable = tables.get(button);
            if (error.length() > 0) {
              button.setBounds(savedButtonX, savedButtonY, selectedTable.getWidth(),
                  selectedTable.getLength());
            }
            resetStates();
            break;
          }

          // Edit Table mode
          if (editTable) {
            editTableActionPerformed(button);
            resetStates();
            break;
          }

          if (issueBill) {
            issueBillActionPerformed(button);
            break;
          }

          // View Order mode
          if(!editTable){
            Table table = tables.get(button);
            removeTable = false;
            viewOrderActionPerformed(button, table);
          }
      }
    }

    private void addReservationActionPerformed() {
      new AddReservationPage(thisPage).setVisible(true);

    }

    private void endOrderActionPerformed() {
      new EndOrderPage(thisPage).setVisible(true);

    }

    private void seatCustomerActionPerformed() {
      new SeatCustomersPage(thisPage).setVisible(true);

    }

    private void issueBillActionPerformed(JButton button) {
      Table requestedTable = tables.get(button);
      if(requestedTable.getStatusFullName()!="Ordered") {
        error = "Please select a table that has ordered an item!";
        refreshData();
      }
      else {
        new IssueBill(requestedTable).setVisible(true);
      }
    }

    private void resetStates() {
      moveTable = false;
      removeTable = false;
      editTable = false;
      issueBill = false;
    }
  };

  // Show Menu
  private void showMenuActionPerformed() {
    new AddOrderItemPage().setVisible(true);
  }

  // Show error, if exists, upon refreshing
  private void refreshData() {
    errorLabel.setText(error);
    instructionLabel.setText("Hover over a button for instructions! Hover over a table for information!");
  }

  // Set options frame as visible
  private void editTableActionPerformed(JButton button) {
    new OptionsForTablePage(this, tables.get(button)).setVisible(true);
    editTable = false;
  }

  // Set view order frame as visible
  private void viewOrderActionPerformed(JButton button, Table table) {
    new ViewOrder(this, tables.get(button)).setVisible(true);
    viewOrder = false;
  }

  // Update table information
  public void updateTable(Table table) {
    JButton button = buttonsByTable.get(table);
    String tableMessage = "#" + ((Integer) table.getNumber()).toString();
    button.setText(tableMessage);
  }

  // Make tables visible, from persistence
  public void plotTables() {
    for (JButton button : tableList) {
      panel.remove(button);
    }
    RestoApp r = RestoApplication.getRestoApp();
    List<Table> tableList = r.getCurrentTables();
    for (Table table : tableList) {
      displayTable(table);
    }
  }

  // Create table onto UI
  public void displayTable(Table table) {
    JButton newTableButton = new JButton();
    String tableMessage = "#" + ((Integer) table.getNumber()).toString();
    String toolTip = "<html> Seats: " + table.getSeats().size();
    for (Reservation reservation : table.getReservations()) {
      toolTip = toolTip + "<br> Reservation: " + reservation.getContactName() + " - " + reservation.getDate() + " - " + reservation.getTime();
    }

    tableList.add(newTableButton);

    // Initialize Table's JButton info
    if (table.getStatusFullName() == "Available") {
      newTableButton.setText(tableMessage);
      newTableButton.setBackground(Color.GREEN);
      newTableButton.setBounds(table.getX(), table.getY(), table.getWidth(), table.getLength());
    } else {
      tableMessage = "[In Use] - " + tableMessage;
      newTableButton.setText(tableMessage);
      newTableButton.setBackground(Color.YELLOW);
      newTableButton.setBounds(table.getX(), table.getY(), table.getWidth(), table.getLength());
    }
    toolTip = toolTip + " </html>";
    newTableButton.setToolTipText(toolTip);

    panel.add(newTableButton);
    setContentPane(panel);

    // Add Listeners to table
    newTableButton.addActionListener(stateListener);
    newTableButton.addMouseMotionListener(overlapDragListener);
    newTableButton.addMouseListener(moveTableReleaseListener);

    // Link Table to its JButton and vice versa using Hash Maps
    tables.put(newTableButton, table);
    buttonsByTable.put(table, newTableButton);
  }

  // Opening the add table frame
  public void addTableActionPerformed() {
    new AddTablePage(this).setVisible(true);
  }

  public void addLoyaltyCardActionPerformed() {
    new LoyaltyCardRegister().setVisible(true);
  }
  
  // Check if position table dragged to is overlapping existing table
  private void actionPerformedCheckOverlap(MouseEvent e) throws InvalidInputException {
    JButton button = (JButton) e.getSource();
    if (savedButtonX == -999 && savedButtonY == -999) {
      savedButtonX = button.getX();
      savedButtonY = button.getY();
    }
    int buttonX = button.getX();
    int buttonY = button.getY();
    Table selectedTable = tables.get(button);
    button.setBounds(buttonX + e.getX(), buttonY + e.getY(), selectedTable.getWidth(), selectedTable.getLength());
    try {
      RestoAppController.checkAllOverlap(buttonX + e.getX(), buttonY + e.getY(), selectedTable);
    } catch (InvalidInputException errorThrown) {
      throw errorThrown;
    }
  }

  // Moving the table
  private void actionPerformedMoveTable(MouseEvent e) {
    error = "";

    // Gets table's initial coordinates
    JButton button = (JButton) e.getSource();
    int buttonX = button.getX();
    int buttonY = button.getY();
    if(tables.get(button).getStatusFullName() != "Available") {
      button.setBackground(Color.YELLOW);
    }
    else {
      button.setBackground(Color.GREEN);
    }
    Table selectedTable = tables.get(button);
    try {
      RestoAppController.moveTable(selectedTable, buttonX + e.getX(), buttonY + e.getY());
    } catch (InvalidInputException errorThrown) {

      // Returns table back to initial position in case of failure
      error = errorThrown.getMessage();
      button.setBounds(savedButtonX, savedButtonY, selectedTable.getWidth(), selectedTable.getLength());
      savedButtonX = -999;
      savedButtonY = -999;
      moveTable = false;
      refreshData();
      return;
    }

    // If no error, set table to new position, reinitialize saved button coordinates
    button.setBounds(buttonX + e.getX(), buttonY + e.getY(), selectedTable.getWidth(), selectedTable.getLength());
    savedButtonX = -999;
    savedButtonY = -999;
    moveTable = false;
    refreshData();

  }

  // Sets Task Buttons to initial positions
  private void setButtonBounds() {
    moveTableButton.setBounds(1600, 100, 280, 50);
    moveTableButton.setToolTipText("Click on move table button and then drag table to move!");
    addTableButton.setBounds(1600, 150, 280, 50);
    addTableButton.setToolTipText("Click on this button to add a new table to the restaurant!");
    editTableButton.setBounds(1600, 200, 280, 50);
    editTableButton.setToolTipText("Click on this button to edit table settings!");
    showMenuBotton.setBounds(1600, 250, 280, 50);
    showMenuButton.setToolTipText("Click on this button to see the menu, edit the menu, or to order an item!");
    removeTableButton.setBounds(1600, 300, 280, 50);
    removeTableButton.setToolTipText("Click on this button and then click on a table to remove it from the restaurant!");
    addReservationButton.setBounds(1600, 350, 280, 50);
    addReservationButton.setToolTipText("Click on this button to add reservations to a table!");
    seatCustomerButton.setBounds(1600, 400, 280, 50);
    seatCustomerButton.setToolTipText("Click on this button to seat customers!");
    endOrderButton.setBounds(1600, 450, 280, 50);
    endOrderButton.setToolTipText("Click on this button to unseat customers!");
    issueBillButton.setBounds(1600, 500, 280, 50);
    issueBillButton.setToolTipText("Click on this button to issue a bill to customers!");
    loyaltyCardButton.setBounds(1600, 550, 280, 50);
    loyaltyCardButton.setToolTipText("Click on this button to issue a loyalty card!");
  }

}
