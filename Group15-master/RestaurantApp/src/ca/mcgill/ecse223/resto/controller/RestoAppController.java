package ca.mcgill.ecse223.resto.controller;

import java.awt.Rectangle;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.model.LoyaltyCard;
import ca.mcgill.ecse223.resto.model.Bill;
import ca.mcgill.ecse223.resto.model.Menu;
import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;
import ca.mcgill.ecse223.resto.model.Order;
import ca.mcgill.ecse223.resto.model.OrderItem;
import ca.mcgill.ecse223.resto.model.PricedMenuItem;
import ca.mcgill.ecse223.resto.model.Reservation;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Seat;
import ca.mcgill.ecse223.resto.model.Table;
import ca.mcgill.ecse223.resto.model.Table.Status;
import ca.mcgill.ecse223.resto.model.Bill;

public class RestoAppController {

  public RestoAppController() {

  }

  public static void updateTable(Table table, int newNumber, int numberOfSeats) throws InvalidInputException {
    String errorMsg = "";
    if (table == null) {
      errorMsg = "Table must be selected to be updated! ";
    }
    if (newNumber <= 0) {
      errorMsg = errorMsg + "Must input a positive integer for new table number! ";
    }
    if (numberOfSeats <= 0) {
      errorMsg = errorMsg + "Must input a positive integer for number of seats! ";
    }
    if (errorMsg.length() > 0) {
      errorMsg = errorMsg.trim();
      throw new InvalidInputException(errorMsg);
    }

    Boolean reserved = table.hasReservations();

    if (reserved) {
      throw new InvalidInputException("Cannot update a table while it has reservations!");
    }

    RestoApp r = RestoApplication.getRestoApp();
    List<Order> currentOrders = r.getCurrentOrders();

    for (Order order : currentOrders) {
      List<Table> tables = order.getTables();
      Boolean inUse = tables.contains(table);
      if (inUse) {

        throw new InvalidInputException("Cannot update a table while it currently has orders!");
      }
    }

    try {
      Boolean wasSet = table.setNumber(newNumber);
      if (!wasSet) {
        throw new InvalidInputException("Duplicate Number.");
      }
    } catch (RuntimeException e) {
      throw new InvalidInputException(e.getMessage());
    }

    int n = table.numberOfCurrentSeats();

    for (int i = 0; i < (numberOfSeats - n); i++) {
      Seat seat = table.addSeat();
      table.addCurrentSeat(seat);
    }

    for (int i = 0; i < (n - numberOfSeats); i++) {
      Seat seat = table.getCurrentSeat(0);
      table.removeCurrentSeat(seat);
    }

    save();
  }

  // menu controller methods

  /*
   * returns a list of the available menu item categories
   */
  public static ItemCategory[] getItemCategories() {
    ItemCategory[] i = ItemCategory.values();
    return i;
  }
  public static void checkOverlap(Rectangle mytable, Rectangle table) throws InvalidInputException {
    if (mytable.intersects(table)) {
      throw new InvalidInputException("You can not move the table here");
    }
  }


  public static void moveTable(Table table, int x, int y) throws InvalidInputException {
    RestoApp r = RestoApplication.getRestoApp();
    try {
      checkAllOverlap(x, y, table);
    } catch (InvalidInputException e) {
      throw new InvalidInputException(e.getMessage());
    }
    table.setX(x);
    table.setY(y);

    save();
  }

  /*
   * returns a list of menu items of a chosen category
   */
  public static List<MenuItem> getMenuItems(ItemCategory itemCategory) throws InvalidInputException {
    String errorMsg = "";
    if (itemCategory == null) {
      errorMsg = "Please select a menu item category";
      throw new InvalidInputException(errorMsg);
    }

    List<MenuItem> l = new ArrayList<MenuItem>();
    RestoApp r = RestoApplication.getRestoApp();

    Menu menu = r.getMenu();

    List<MenuItem> menuItems = menu.getMenuItems();

    boolean current = false;
    ItemCategory category;
    for (MenuItem menuItem : menuItems) {
      current = menuItem.hasCurrentPricedMenuItem();
      category = menuItem.getItemCategory();

      if (current && category.equals(itemCategory)) {
        l.add(menuItem);
      }
    }
    return l;
  }

  public static void removeTable(Table table) throws InvalidInputException {

    Boolean reserved = table.hasReservations();

    if (reserved) {
      throw new InvalidInputException("Cannot remove a table while it has reservations!");
    }

    RestoApp r = RestoApplication.getRestoApp();

    List<Order> currentOrders = r.getCurrentOrders();


    for (Order order : currentOrders) {
      List<Table> tables = order.getTables();
      Boolean inUse = tables.contains(table);
      if (inUse) {
        throw new InvalidInputException("Cannot remove a table while it currently has orders!");
      }
    }

    try {
      if (!r.removeCurrentTable(table)) {
        throw new InvalidInputException("Can't remove table unless it is current!");
      }
    } catch (RuntimeException e) {
      throw new InvalidInputException(e.getMessage());
    }

    save();
  }

  public static void checkAllOverlap(int x, int y, Table mytable) throws InvalidInputException {
    RestoApp r = RestoApplication.getRestoApp();
    List<Table> currentTables = r.getCurrentTables();
    Rectangle myrect = new Rectangle(x, y, mytable.getWidth(), mytable.getLength());
    for (Table table : currentTables) {
      if (table == mytable) {
        continue;
      }
      Rectangle rect = new Rectangle(table.getX(), table.getY(), table.getWidth(), table.getLength());
      try {
        checkOverlap(myrect, rect);
      } catch (InvalidInputException e) {
        throw new InvalidInputException(e.getMessage());
      }
    }


  }

  public static void createTable(int number, int x, int y, int width, int length, int numberOfSeats)
      throws InvalidInputException {

    if (number < 0 || x < 0 || y < 0 || width < 0 || length < 0 || numberOfSeats < 0) {
      throw new InvalidInputException("Error: Cannot enter negative numbers as input.");
    }

    RestoApp r = RestoApplication.getRestoApp();

    Table table;

    try {
      table = new Table(number, x, y, width, length, r);
    } catch (RuntimeException e) {
      throw new InvalidInputException(e.getMessage());
    }

    try {
      checkAllOverlap(x, y, table);
    } catch (InvalidInputException e) {
      throw new InvalidInputException(e.getMessage());

    }

    r.addCurrentTable(table);

    for (int i = 0; i < numberOfSeats; i++) {
      Seat seat = table.addSeat();
      table.addCurrentSeat(seat);
    }

    save();
  }

  // TODO implement error message append (IF TIME ALLOWS)
  public static void reserveTable(Date date, Time time, int numberInParty, String contactName,
      String contactEmailAddress, String contactPhoneNumber, List<Table> tables) throws InvalidInputException {
    if (tables.size() == 0) {
      throw new InvalidInputException("Please select a table to be reserved!");
    }
    RestoApp r = RestoApplication.getRestoApp();
    List<Table> currentTables = r.getCurrentTables();

    int seatCapacity = 0;

    for (Table table : tables) {
      if (!currentTables.contains(table)) {

        throw new InvalidInputException("Not all tables are in the list of current Tables!");
      }
      seatCapacity += table.numberOfCurrentSeats();
      List<Reservation> reservations = table.getReservations();

      for (Reservation reservation : reservations) {
        // TODO inject overlaps into Reservation class
        if (reservation.overlaps(date, time)) {
          throw new InvalidInputException(
              "Indicated Date / Time overlaps with the existing reservations of at least one of the tables.");
        }
      }
    }

    if (seatCapacity < numberInParty) {
      throw new InvalidInputException("Not enough space for party on tables!");
    }

    Table[] arrayTables = tables.toArray(new Table[0]);
    try {
      new Reservation(date, time, numberInParty, contactName, contactEmailAddress, contactPhoneNumber, r,
          arrayTables);
    } catch (RuntimeException e) {
      throw new InvalidInputException(e.getMessage());
    }

    save();
  }

  // TODO add message for tables that weren't added to order.
  public static String startOrder(List<Table> tables) throws InvalidInputException {
    RestoApp r = RestoApplication.getRestoApp();
    List<Table> currentTables = r.getCurrentTables();
    String tablesNotAdded = "";

    for (Table table : tables) {
      if (!currentTables.contains(table)) {
        throw new InvalidInputException("Not all tables are in the list of current Tables!");
      }
    }

    Boolean orderCreated = false;
    Order newOrder = null;

    for (Table table : tables) {
      if (orderCreated) {
        if (!table.addToOrder(newOrder)) {
          tablesNotAdded = tablesNotAdded + Integer.toString(table.getNumber()) + " ";
        }
      }

      else {
        Order lastOrder = null;
        if (table.numberOfOrders() > 0) {
          lastOrder = table.getOrder(table.numberOfOrders() - 1);
        }
        table.startOrder();

        if ((table.numberOfOrders() > 0) && !table.getOrder(table.numberOfOrders() - 1).equals(lastOrder)) {
          orderCreated = true;
          newOrder = table.getOrder(table.numberOfOrders() - 1);
        } else {
          tablesNotAdded += tablesNotAdded + Integer.toString(table.getNumber()) + " ";
        }
      }
    }
    if (!orderCreated) {
      throw new InvalidInputException("Order was not able to be created!");
    }

    r.addCurrentOrder(newOrder);

    save();

    if (tablesNotAdded.length() > 0) {
      tablesNotAdded = "WARNING: These tables not added to order: " + tablesNotAdded;
    }
    return tablesNotAdded;

  }

  // add message for tables whose orders were not ended.
  public static String endOrder(Order order) throws InvalidInputException {
    String tablesOrderNotEnded = "";

    if (order == null) {
      throw new InvalidInputException("Please select an order to end! ");
    }

    RestoApp r = RestoApplication.getRestoApp();

    List<Order> currentOrders = r.getCurrentOrders();

    if (!currentOrders.contains(order)) {
      throw new InvalidInputException("Order selected must be a current order!");
    }

    List<Table> tables = order.getTables();
    ArrayList<Table> tablesToEnd = new ArrayList<Table>();

    for (Table table : tables) {
      if (table.getStatusFullName() == "Available") {
        tablesOrderNotEnded = tablesOrderNotEnded + Integer.toString(table.getNumber()) + " ";
      } else {
        tablesToEnd.add(table);
      }
    }

    for (Table table : tablesToEnd) {
      table.endOrder(order);
    }

    // TODO create controller method that checks that all tables are either
    // available or serving a new order
    if (allTablesAvailableOrDifferentCurrentOrder(tables, order)) {
      r.removeCurrentOrder(order);
    }

    save();

    if (tablesOrderNotEnded.length() > 0) {
      tablesOrderNotEnded = "WARNING: These tables failed to end order: " + tablesOrderNotEnded;
    }

    return tablesOrderNotEnded;
  }

  /*
   * returns a list of the current tables
   */
  public static List<Table> getCurrentTables() {
    RestoApp r = RestoApplication.getRestoApp();
    return r.getCurrentTables();
  }

  private static boolean allTablesAvailableOrDifferentCurrentOrder(List<Table> tables, Order order) {
    boolean checkAllTables = true;

    for (Table table : tables) {
      if ((table.getStatusFullName() != "Available") && (table.getOrder(table.numberOfOrders() - 1) == order)) {
        checkAllTables = false;
      }
    }
    return checkAllTables;
  }

  /*
   * returns a list of the current orders
   */
  public static List<Order> getCurrentOrders() {
    RestoApp r = RestoApplication.getRestoApp();
    return r.getCurrentOrders();
  }

  public static List<OrderItem> getOrderItems(Table table) throws InvalidInputException {
    if (table == null) {
      throw new InvalidInputException("Please select a table to view order items!");
    }

    RestoApp r = RestoApplication.getRestoApp();
    List<Table> currentTables = r.getCurrentTables();
    Boolean current = currentTables.contains(table);

    if (current == false) {
      throw new InvalidInputException("Table is not current!");
    }

    Status status = table.getStatus();
    if (status == Status.Available) {
      throw new InvalidInputException("Table is not in use!");
    }

    Order lastOrder = null;
    if (table.numberOfOrders() > 0) {
      lastOrder = table.getOrder(table.numberOfOrders() - 1);
    } else {
      throw new InvalidInputException("Table has no order!");
    }

    List<Seat> currentSeats = table.getCurrentSeats();

    List<OrderItem> result = new ArrayList<OrderItem>();

    for (Seat seat : currentSeats) {
      List<OrderItem> orderItems = seat.getOrderItems();

      for (OrderItem orderItem : orderItems) {
        Order order = orderItem.getOrder();

        if (lastOrder.equals(order) && !result.contains(orderItem)) {
          result.add(orderItem);
        }
      }
    }

    return result;

  }

  // Controller methods for Loyalty Card

  public static int addLoyaltyCard(String name, String emailAdd, String phoneNum) throws InvalidInputException {
    if (name == null || name.trim().length() == 0 || emailAdd == null || emailAdd.trim().length() == 0
            || phoneNum == null || phoneNum.trim().length() == 0) {
        throw new InvalidInputException("Cannot leave an entry field blank!");
    }

    RestoApp r = RestoApplication.getRestoApp();
    LoyaltyCard loyaltyCard;
   try {
	   loyaltyCard = r.addLoyaltyCard(name, emailAdd, phoneNum);
   }
   catch (Exception e) {
	   throw new InvalidInputException(e.getMessage());
   }

    save();
    
    return loyaltyCard.getNumber();
}

  public static void assignLoyaltyCard(LoyaltyCard loyaltyCard, Bill bill) throws InvalidInputException {
    if (loyaltyCard == null || bill == null) {
      throw new InvalidInputException("An entry field has been left empty");
    }
    loyaltyCard.addBill(bill);
    save();

  }

  private static void save() throws InvalidInputException {
    try {
      RestoApplication.save();
    } catch (RuntimeException e) {
      throw new InvalidInputException(e.getMessage());
    }
  }

	public static Bill issueBill(List<Seat> seats, int loyaltyNumber) throws InvalidInputException {
		if (seats == null | seats.isEmpty()) {
			throw new InvalidInputException("Please select seats to issue bill!");
		}
    
    RestoApp r = RestoApplication.getRestoApp();
    
    LoyaltyCard foundCard = null;
		if (loyaltyNumber != -1) {
			for (LoyaltyCard card : r.getLoyaltyCards()) {
				if (card.getNumber() == loyaltyNumber) {
					foundCard = card;
				}
			}

			if (foundCard == null) {
				throw new InvalidInputException("Please enter a valid Loyalty Card");
			}
		}
		

		List<Table> currentTables = r.getCurrentTables();

    Order lastOrder = null;

    for (Seat seat : seats) {
      Table table = seat.getTable();
      if (!currentTables.contains(table)) {
        throw new InvalidInputException("Please select seats that are associated to a current table!");
      }
      List<Seat> currentSeats = table.getCurrentSeats();
      if (!currentSeats.contains(seat)) {
        throw new InvalidInputException("Please select seats that are currently in the restaurant!");
      }
      if (lastOrder == null) {
        if (table.numberOfOrders() > 0) {
          lastOrder = table.getOrder(table.numberOfOrders() - 1);
        } else {
          throw new InvalidInputException("Please select a table that has a current order!");
        }
      } else {
        Order comparedOrder = null;
        if (table.numberOfOrders() > 0) {
          comparedOrder = table.getOrder(table.numberOfOrders() - 1);
        } else {
          throw new InvalidInputException("Please ensure all seats are being served by an order!");
        }
        if (!comparedOrder.equals(lastOrder)) {
          throw new InvalidInputException("Please ensure all seats are being served by the same order!");
        }
      }
    }

    if (lastOrder == null) {
      throw new InvalidInputException("Last order is null!");
    }

    Boolean billCreated = false;
    Bill newBill = null;

    for (Seat seat : seats) {
      Table table = seat.getTable();
      if (billCreated) {
        table.addToBill(newBill, seat);
      } else {
        Bill lastBill = null;
        if (lastOrder.numberOfBills() > 0) {
          lastBill = lastOrder.getBill(lastOrder.numberOfBills() - 1);
        }
        table.billForSeat(lastOrder, seat);
        if (lastOrder.numberOfBills() > 0
            && !lastOrder.getBill(lastOrder.numberOfBills() - 1).equals(lastBill)) {
          billCreated = true;
          newBill = lastOrder.getBill(lastOrder.numberOfBills() - 1);
        }
      }
    }

    if (!billCreated) {
      throw new InvalidInputException("Unable to create bill! Please ensure all seats have ordered an item!");
    }

    if(loyaltyNumber != -1) {
			foundCard.addBill(newBill);
		}
		
		try {
			RestoApplication.save();
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

    return newBill;

  }

  public static void cancelOrderItem(OrderItem item) throws InvalidInputException{
    if(item==null){
      throw new InvalidInputException("Please select an order item");
    }
    List<Seat> seats = item.getSeats();
    Order order= item.getOrder();
    List<Table> tables = new ArrayList<Table>();
    for(Seat seat: seats){
      Table table=seat.getTable();
      Order lastOrder=null;
      if(table.numberOfOrders()>0){
        lastOrder=table.getOrder(table.numberOfOrders()-1);         
      }
      else{
        throw new InvalidInputException("One of the tables for the order item does not have an order!");
      }
      if( (lastOrder.equals(order)) && (!tables.contains(table)) ){
        tables.add(table);
      }
    }
    for(Table table:tables){
      table.cancelOrderItem(item);
    }
    save();

  }

  public static void cancelOrder(Table table) throws InvalidInputException{
    if(table==null){
      throw new InvalidInputException("Please select a table");
    }
    RestoApp r= RestoApplication.getRestoApp();
    List<Table> currentTables= r.getCurrentTables();
    Boolean current=currentTables.contains(table);
    if(current==false){
      throw new InvalidInputException("table selected is not a current table");
    }
    table.cancelOrder();
    save();

  }

  public static void orderMenuItem(MenuItem menuItem, int quantity, List<Seat> seats) throws InvalidInputException {

    if (menuItem == null) {
      throw new InvalidInputException("Invalid Input: Please select a menu item");
    }
    if (seats == null) {
      throw new InvalidInputException("Invalid Input: Please select at least one seat");
    }
    if (quantity <= 0) {
      throw new InvalidInputException("Invalid Input: quantity must be positive");
    }

    RestoApp r = RestoApplication.getRestoApp();
    boolean current = menuItem.hasCurrentPricedMenuItem();
    if (!current) {
      throw new InvalidInputException("Menu Item does not have a current priced menuItem");
    }
    List<Table> currentTables = r.getCurrentTables();
    Order lastOrder = null;
    Table table;
    List<Seat> currentSeats;
    for (Seat seat : seats) {
      table = seat.getTable();
      current = currentTables.contains(table);
      if (!current) {
        throw new InvalidInputException(" ");
      }
      currentSeats = table.getCurrentSeats();
      current = currentSeats.contains(seat);
      if (!current) {
        throw new InvalidInputException("Seat is not current! ");
      }

      if (lastOrder == null) {
        if (table.numberOfOrders() > 0) {
          lastOrder = table.getOrder(table.numberOfOrders() - 1);
        } else {
          throw new InvalidInputException(
              "The table you're adding the order item to is does not have an order ");
        }
      } else {
        Order comparedOrder = null;
        if (table.numberOfOrders() > 0) {
          comparedOrder = table.getOrder(table.numberOfOrders() - 1);
        } else {
          throw new InvalidInputException(
              "The table you're adding the order item to is does not have an order");
        }

        if (!comparedOrder.equals(lastOrder)) {
          throw new InvalidInputException("last orders of all tables of all seats are NOT the same ");
        }
      }
    }

    if (lastOrder == null) {
      throw new InvalidInputException("Invalid Input: Please select at least one seat");
    }

    PricedMenuItem pmi = menuItem.getCurrentPricedMenuItem();
    boolean itemCreated = false;
    OrderItem newItem = null;

    for (Seat seat : seats) {
      table = seat.getTable();
      if (itemCreated) {
        table.addToOrderItem(newItem, seat);
      } else {
        OrderItem lastItem = null;
        if (lastOrder.numberOfOrderItems() > 0) {
          lastItem = lastOrder.getOrderItem(lastOrder.numberOfOrderItems() - 1);
        }
        table.orderItem(quantity, lastOrder, seat, pmi);
        if (lastOrder.numberOfOrderItems() > 0
            && !lastOrder.getOrderItem(lastOrder.numberOfOrderItems() - 1).equals(lastItem)) {
          itemCreated = true;
          newItem = lastOrder.getOrderItem(lastOrder.numberOfOrderItems() - 1);
        }
      }
    }
    if (!itemCreated) {
      throw new InvalidInputException("Error: Item was not created! ");
    }

    save();

  }

	/*
	 * helper method that returns a list of the current seats
	 */
	public static List<Seat> getCurrentSeats() {
		List<Seat> currentSeats = new ArrayList<Seat>();
		List<Table> currentTables = getCurrentTables();
		for (Table table : currentTables) {
			for (Seat seat : table.getCurrentSeats()) {
				currentSeats.add(seat);
			}
		}
		return currentSeats;
	}

	public static void addMenuItem(String name, ItemCategory category, double price) throws InvalidInputException {
		if (name.equals(null) || category == null || price <= 0) {
			throw new InvalidInputException(" Name is empty or Category is empty or price is not positive");
		}
		RestoApp r = RestoApplication.getRestoApp();
		Menu menu = r.getMenu();
		try {
			MenuItem MI = menu.addMenuItem(name);
			MI.setItemCategory(category);
			PricedMenuItem PMI = MI.addPricedMenuItem(price, r);
			MI.setCurrentPricedMenuItem(PMI);
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
		save();
	}

	public static void updateMenuItem(MenuItem MI, String name, ItemCategory category, double price)
			throws InvalidInputException {
		boolean dupped = true;

		if (name == null || category == null || price < 0 && MI == null) {

			throw new InvalidInputException(
					" menu item is empty or name is empty or category is null or price is not positive");
		}
		if (MI == null) {
			throw new InvalidInputException("Error 404: Menu item does not exist");
		}
		if (MI.hasCurrentPricedMenuItem()) {

			dupped = MI.setName(name);
			if (!dupped) {
				throw new InvalidInputException("New name is same as old name");
			}

			if (!MI.getItemCategory().equals(category)) {
				MI.setItemCategory(category);
			}
			if (price != MI.getCurrentPricedMenuItem().getPrice()) {
				RestoApp r = RestoApplication.getRestoApp();
				PricedMenuItem PMI = MI.addPricedMenuItem(price, r);
				MI.setCurrentPricedMenuItem(PMI);
			}
		} else {
			throw new InvalidInputException("menu item has no current priced menu items");
		}
		save();
	}

	public static void removeMenuItem(MenuItem MI) throws InvalidInputException {

		if (MI == null) {
			throw new InvalidInputException("Please select a menu item");
		}
		if (MI.hasCurrentPricedMenuItem()) {
			MI.setCurrentPricedMenuItem(null);
		} else {
			throw new InvalidInputException("menu item is not priced");
		}
		save();

	}


}
