package ca.mcgill.ecse223.resto.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Rectangle;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.ecse223.resto.application.RestoApplication;
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

public class RestoAppControllerTest {

  private static String filename = "testdata.resto";

  @Before
  public void setUp() throws Exception {
    // Re-initialize RestoApp before each test
    RestoApplication.deleteRestoApp();
    RestoApplication.setFilename(filename);
    File f = new File(filename);
    f.delete();
  }

  @Test
  public void testGetMenuItemSuccess() {
    ArrayList<MenuItem> itemList = setUpTestMenu();
    List<MenuItem> l = null;
    try {
      l = RestoAppController.getMenuItems(ItemCategory.Main);
    } catch (InvalidInputException e) {
      fail();
    }

    assertEquals(itemList.get(0), l.get(0));
    assertEquals(itemList.get(1), l.get(1));
    assertEquals(itemList.get(3), l.get(3));
  }

  @Test
  public void testUpdateTableSuccess() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    int newNumber = 4;
    int numberOfSeats = 5;
    try {
      RestoAppController.updateTable(table, newNumber, numberOfSeats);
    } catch (InvalidInputException e) {
      // ensure no input exception
      fail(e.getMessage());
    }

    checkUpdatedTable(table, 10, 10, 10, 10, 4, 5, 0);

  }

  @Test
  public void testGetCategorySuccess() {

    // r = RestoApplication.getRestoApp();

    ItemCategory[] i = RestoAppController.getItemCategories();
    ItemCategory[] expected = {ItemCategory.Appetizer, ItemCategory.Main, ItemCategory.Dessert,
        ItemCategory.AlcoholicBeverage, ItemCategory.NonAlcoholicBeverage};

    for (int x = 0; x < expected.length; x++) {
      assertEquals(expected[x], i[x]);
    }
  }

  @Test
  public void testUpdateTableInvalidInput() {
    Table table = null;
    String errorMessage = "";
    int newNumber = -4;
    int numberOfSeats = -5;
    try {
      RestoAppController.updateTable(table, newNumber, numberOfSeats);
    } catch (InvalidInputException e) {
      errorMessage = e.getMessage();
    }
    assertEquals(
        "Table must be selected to be updated! Must input a positive integer for new table number! Must input a positive integer for number of seats!",
        errorMessage);
  }

  @Test
  public void testUpdateTableReserved() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    String errorMessage = "";
    int newNumber = 4;
    int numberOfSeats = 5;
    Date reservationDate = Date.valueOf("2018-03-30");
    Time reservationTime = Time.valueOf("18:00:00");
    r.addReservation(reservationDate, reservationTime, 2, "Imad Dodin", "imad.dodin@mail.mcgill.ca",
        "438-923-1678", table);
    try {
      RestoAppController.updateTable(table, newNumber, numberOfSeats);
    } catch (InvalidInputException e) {
      errorMessage = e.getMessage();
    }

    assertEquals("Cannot update a table while it has reservations!", errorMessage);
  }

  @Test
  public void testUpdateTableInUse() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    String errorMessage = "";
    int newNumber = 4;
    int numberOfSeats = 5;
    Date orderDate = Date.valueOf("2018-03-30");
    Time orderTime = Time.valueOf("18:00:00");
    Order order = r.addOrder(orderDate, orderTime, table);
    r.addCurrentOrder(order);
    try {
      RestoAppController.updateTable(table, newNumber, numberOfSeats);
    } catch (InvalidInputException e) {
      errorMessage = e.getMessage();
    }

    assertEquals("Cannot update a table while it currently has orders!", errorMessage);
  }

  @Test
  public void testCheckOverlap() {
    String errormsg = "";
    Rectangle table1 = new Rectangle(5, 5, 10, 10);
    Rectangle table2 = new Rectangle(10, 10, 10, 10);
    try {
      RestoAppController.checkOverlap(table1, table2);
    } catch (InvalidInputException e) {
      errormsg = e.getMessage();
    }
    assertEquals("You can not move the table here", errormsg);
  }

  @Test

  public void testCheckAllOverlapContained() {
    RestoApp r = RestoApplication.getRestoApp();
    String errormsg = "";
    Table table1 = new Table(1, 5, 5, 10, 10, r);
    Table table2 = new Table(2, 60, 80, 10, 10, r);
    Table table3 = new Table(3, 20, 20, 10, 10, r);
    r.addCurrentTable(table1);
    r.addCurrentTable(table2);
    r.addCurrentTable(table3);
    int x1 = 20;
    int y1 = 20;
    try {
      RestoAppController.checkAllOverlap(x1, y1, table1);
    } catch (InvalidInputException e) {
      errormsg = e.getMessage();
    }
    assertEquals("You can not move the table here", errormsg);
  }

  @Test
  public void testIntersection() {
    RestoApp r = RestoApplication.getRestoApp();

    String errormsg = "";
    Table table1 = new Table(1, 5, 5, 10, 10, r);
    Table table2 = new Table(2, 60, 80, 10, 10, r);
    Table table3 = new Table(3, 20, 20, 10, 10, r);
    r.addCurrentTable(table1);
    r.addCurrentTable(table2);
    r.addCurrentTable(table3);
    int x = 20;
    int y = 20;
    try {
      RestoAppController.checkAllOverlap(x, y, table1);
    } catch (InvalidInputException e) {
      errormsg = e.getMessage();
    }
    assertEquals("You can not move the table here", errormsg);
  }

  @Test
  public void testValidMove() {
    RestoApp r = RestoApplication.getRestoApp();

    Table table1 = new Table(1, 5, 5, 10, 10, r);
    Table table2 = new Table(2, 60, 80, 10, 10, r);
    Table table3 = new Table(3, 20, 20, 10, 10, r);
    List<Table> currentTables = new ArrayList<Table>();
    currentTables.add(table1);
    currentTables.add(table2);
    currentTables.add(table3);
    int x3 = 120;
    int y3 = 120;
    try {
      RestoAppController.checkAllOverlap(x3, y3, table1);
    } catch (InvalidInputException e) {
      fail();
    }
  }

  @Test
  public void checkMovetable() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table1 = new Table(1, 5, 5, 10, 10, r);

    try {
      RestoAppController.moveTable(table1, 15, 20);
    } catch (InvalidInputException e) {
      fail();
    }
    assertEquals(15, table1.getX());
    assertEquals(20, table1.getY());
  }

  @Test

  public void testRemoveTableSuccess() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(4, 5, 5, 10, 10);
    r.addCurrentTable(table);
    try {
      RestoAppController.removeTable(table);
    } catch (InvalidInputException e) {
      fail();
    }
    assertEquals(0, r.getCurrentTables().size());
  }

  @Test
  public void testRemoveTableReserved() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(4, 5, 5, 10, 10);
    String errorMessage = "";
    Date reservationDate = Date.valueOf("2018-03-30");
    Time reservationTime = Time.valueOf("18:00:00");
    r.addReservation(reservationDate, reservationTime, 5, "Azhar", "ahmedazhar10@gmail.com",
        "5140004770", table);

    try {
      RestoAppController.removeTable(table);
    } catch (InvalidInputException e) {
      errorMessage = e.getMessage();
    }

    assertEquals("Cannot remove a table while it has reservations!", errorMessage);

  }

  @Test
  public void testRemoveTableInUse() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(4, 5, 5, 10, 10);
    String errorMessage = "";
    Date orderDate = Date.valueOf("2018-03-30");
    Time orderTime = Time.valueOf("18:00:00");
    Order order = r.addOrder(orderDate, orderTime, table);
    r.addCurrentOrder(order);
    try {
      RestoAppController.removeTable(table);
    } catch (InvalidInputException e) {
      errorMessage = e.getMessage();
    }
    assertEquals("Cannot remove a table while it currently has orders!", errorMessage);
  }

  private ArrayList<MenuItem> setUpTestMenu() {
    RestoApp r = RestoApplication.getRestoApp();
    Menu menu = r.getMenu();
    RestoApplication.setFilename("menu.resto");
    MenuItem menuItem1 = menu.addMenuItem("Dish1");
    menuItem1.setCurrentPricedMenuItem(new PricedMenuItem(100, r, menuItem1));
    MenuItem menuItem2 = menu.addMenuItem("Dish2");
    menuItem2.setCurrentPricedMenuItem(new PricedMenuItem(100, r, menuItem2));
    MenuItem menuItem3 = menu.addMenuItem("Dish3");
    menuItem3.setCurrentPricedMenuItem(new PricedMenuItem(100, r, menuItem3));
    MenuItem menuItem4 = menu.addMenuItem("Dish4");
    menuItem4.setCurrentPricedMenuItem(new PricedMenuItem(100, r, menuItem4));
    MenuItem menuItem5 = menu.addMenuItem("Dish5");
    menuItem5.setCurrentPricedMenuItem(new PricedMenuItem(100, r, menuItem5));
    MenuItem menuItem6 = menu.addMenuItem("Dish6");
    menuItem6.setCurrentPricedMenuItem(new PricedMenuItem(100, r, menuItem6));
    MenuItem menuItem7 = menu.addMenuItem("Dish7");
    menuItem7.setCurrentPricedMenuItem(new PricedMenuItem(100, r, menuItem7));
    MenuItem menuItem8 = menu.addMenuItem("Dish8");
    menuItem8.setCurrentPricedMenuItem(new PricedMenuItem(100, r, menuItem8));

    menuItem1.setItemCategory(ItemCategory.Main);
    menuItem2.setItemCategory(ItemCategory.Main);
    menuItem3.setItemCategory(ItemCategory.Main);
    menuItem4.setItemCategory(ItemCategory.Main);
    menuItem5.setItemCategory(ItemCategory.Appetizer);
    menuItem6.setItemCategory(ItemCategory.Appetizer);
    menuItem7.setItemCategory(ItemCategory.Appetizer);
    menuItem8.setItemCategory(ItemCategory.Appetizer);

    ArrayList<MenuItem> testItems = new ArrayList<MenuItem>();
    testItems.add(menuItem1);
    testItems.add(menuItem2);
    testItems.add(menuItem3);
    testItems.add(menuItem4);
    testItems.add(menuItem5);
    testItems.add(menuItem6);
    testItems.add(menuItem7);
    testItems.add(menuItem8);
    return testItems;
  }

  private void checkUpdatedTable(Table table, int x, int y, int width, int length, int number,
      int numberOfSeats, int numberOfReservations) {
    assertEquals(0, table.getOrders().size());
    assertEquals(0, table.getReservations().size());
    assertEquals(x, table.getX());
    assertEquals(y, table.getY());
    assertEquals(length, table.getLength());
    assertEquals(width, table.getWidth());
    assertEquals(number, table.getNumber());
    assertEquals(numberOfSeats, table.getCurrentSeats().size());
    assertEquals(numberOfReservations, table.getReservations().size());
  }

  @Test
  public void checkReservationOverlap() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    // List<Table> tables= new ArrayList<Table>();
    // Table[] arrayTables = (Table[]) tables.toArray();
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Reservation r1 =
        new Reservation(date, time, 3, "aehwany", "www.aehwany@gmail.com", "23415421", r, table);

    Date date2 = Date.valueOf("2018-03-30");
    Time time2 = Time.valueOf("18:00:00");
    Boolean result = r1.overlaps(date2, time2);
    assertEquals(true, result);

    Date date3 = Date.valueOf("2018-03-30");
    Time time3 = Time.valueOf("16:00:01");
    Boolean result2 = r1.overlaps(date3, time3);
    assertEquals(true, result2);

    Date date4 = Date.valueOf("2018-03-30");
    Time time4 = Time.valueOf("19:59:59");
    Boolean result3 = r1.overlaps(date4, time4);
    assertEquals(true, result3);
  }

  @Test
  public void checkReservationNoOverlap() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Reservation r1 =
        new Reservation(date, time, 3, "aehwany", "www.aehwany@gmail.com", "23415421", r, table);

    Date date2 = Date.valueOf("2018-03-31");
    Time time2 = Time.valueOf("18:00:00");
    Boolean result = r1.overlaps(date2, time2);
    assertEquals(false, result);

    Date date3 = Date.valueOf("2018-03-30");
    Time time3 = Time.valueOf("20:00:00");
    Boolean result2 = r1.overlaps(date3, time3);
    assertEquals(false, result2);

    Date date4 = Date.valueOf("2018-03-30");
    Time time4 = Time.valueOf("16:00:00");
    Boolean result3 = r1.overlaps(date4, time4);
    assertEquals(false, result3);
  }

  @Test
  public void testStartOrderError() {
    /**
     * This test, checks if all tables are in the list of current tables
     */
    RestoApp r = RestoApplication.getRestoApp();

    Table table1 = new Table(1, 5, 5, 10, 10, r);
    Table table2 = new Table(2, 60, 80, 10, 10, r);
    Table table3 = new Table(3, 20, 20, 10, 10, r);

    List<Table> tables = r.getTables();

    r.addCurrentTable(table1);
    r.addCurrentTable(table2);
    // r.addCurrentTable(table3);

    String errorMessage = "";

    try {
      RestoAppController.startOrder(tables);
    } catch (InvalidInputException e) {
      errorMessage = e.getMessage();
    }

    assertEquals("Not all tables are in the list of current Tables!", errorMessage);

  }

  @Test
  public void testStartOrderNotCreated() {
    /**
     * This test checks if method gives error when Order is not created.
     */

    RestoApp r = RestoApplication.getRestoApp();

    Table table1 = new Table(1, 5, 5, 10, 10, r);
    Table table2 = new Table(2, 60, 80, 10, 10, r);
    Table table3 = new Table(3, 20, 20, 10, 10, r);

    List<Table> tables = r.getTables();

    r.addCurrentTable(table1);
    r.addCurrentTable(table2);
    r.addCurrentTable(table3);

    String errorMessage = "";

    try {
      RestoAppController.startOrder(tables);
      RestoAppController.startOrder(tables);
    } catch (InvalidInputException e) {
      errorMessage = e.getMessage();
    }

    assertEquals("Order was not able to be created!", errorMessage);
    // what is orderCreated?

  }

  @Test
  public void testStartOrderPartial() {

    RestoApp r = RestoApplication.getRestoApp();

    Table table1 = new Table(1, 5, 5, 10, 10, r);
    Table table2 = new Table(2, 60, 80, 10, 10, r);
    Table table3 = new Table(3, 20, 20, 10, 10, r);

    // List<Table> tables = r.getTables();

    r.addCurrentTable(table1);
    r.addCurrentTable(table2);
    r.addCurrentTable(table3);

    ArrayList<Table> toAddTables = new ArrayList<Table>();
    toAddTables.add(table1);
    toAddTables.add(table2);

    try {
      RestoAppController.startOrder(toAddTables);
    } catch (InvalidInputException e) {
      fail("Error in starting initial order");
    }

    toAddTables.remove(1);
    toAddTables.add(table3);
    String notAdded = "";
    try {
      notAdded = RestoAppController.startOrder(toAddTables);
    } catch (InvalidInputException e) {
      fail("Error in starting second order");
    }

    assertEquals("WARNING: These tables not added to order: 1 ", notAdded);

  }

  @Test

  public void testReserveTableTableNotCurrent() {
    String error = "";
    RestoApp r = RestoApplication.getRestoApp();
    Table table = new Table(1, 5, 5, 10, 10, r);
    List<Table> tables = new ArrayList<Table>();
    tables.add(table);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    try {
      RestoAppController.reserveTable(date, time, 3, "adam", "www.ad@gmail.com", "4453523434",
          tables);
    } catch (InvalidInputException e) {
      error += e.getMessage();
    }
    assertEquals("Not all tables are in the list of current Tables!", error);
  }

  @Test
  public void testReserveTableOverlap() {
    String error = "";
    RestoApp r = RestoApplication.getRestoApp();
    Table table = new Table(1, 5, 5, 10, 10, r);
    List<Table> tables = new ArrayList<Table>();
    tables.add(table);
    r.addCurrentTable(table);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Reservation res =
        new Reservation(date, time, 0, "adam", "www.ad@gmail.com", "54254554", r, table);
    table.addReservation(res);

    Date date1 = Date.valueOf("2018-03-30");
    Time time1 = Time.valueOf("18:00:00");

    try {
      RestoAppController.reserveTable(date, time, 3, "karim", "www.ka@gmail.com", "4453523434",
          tables);
    } catch (InvalidInputException e) {
      error += e.getMessage();
    }
    assertEquals(
        "Indicated Date / Time overlaps with the existing reservations of at least one of the tables.",
        error);
  }

  @Test
  public void testReserveTableInvalidNumberInParty() {
    String error = "";
    RestoApp r = RestoApplication.getRestoApp();
    Table table = new Table(1, 5, 5, 10, 10, r);
    List<Table> tables = new ArrayList<Table>();
    tables.add(table);
    r.addCurrentTable(table);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Seat seat1 = new Seat(table);
    table.addCurrentSeat(seat1);
    Seat seat2 = new Seat(table);
    table.addCurrentSeat(seat2);

    try {
      RestoAppController.reserveTable(date, time, 3, "karim", "www.ka@gmail.com", "4453523434",
          tables);
    } catch (InvalidInputException e) {
      error += e.getMessage();
    }
    assertEquals("Not enough space for party on tables!", error);
  }

  @Test
  public void testReserveTableValid() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table1 = new Table(1, 5, 5, 10, 10, r);
    Table table2 = new Table(2, 5, 5, 10, 10, r);

    ArrayList<Table> tables = new ArrayList<Table>();
    tables.add(table2);
    tables.add(table1);
    r.addCurrentTable(table1);
    r.addCurrentTable(table2);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");

    try {
      RestoAppController.reserveTable(date, time, 0, "karim", "www.ka@gmail.com", "4453523434",
          tables);
    } catch (InvalidInputException e) {
      fail();
    }
    assertEquals(1, table1.getReservations().size());
    assertEquals(1, table2.getReservations().size());
  }

  public void testEndOrderNull() {
    RestoApp r = RestoApplication.getRestoApp();
    String error = "";
    try {
      RestoAppController.endOrder(null);
    } catch (InvalidInputException e) {
      error += e.getMessage();
    }
    assertEquals("Please select an order to end! ", error);
  }

  @Test
  public void testEndOrderNotInCurrentOrder() {
    String error = "";
    RestoApp r = RestoApplication.getRestoApp();
    Table table = new Table(1, 5, 5, 10, 10, r);
    Order newOrder = new Order(null, null, r, table);
    try {
      RestoAppController.endOrder(newOrder);
    } catch (InvalidInputException e) {
      error += e.getMessage();
    }
    assertEquals("Order selected must be a current order!", error);
  }

  @Test
  public void testEndOrderInvalid() {
    String error = "";
    RestoApp r = RestoApplication.getRestoApp();
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("20:00:00");
    Table table = new Table(1, 5, 5, 10, 10, r);
    Order newOrder = new Order(date, time, r, table);
    Seat seat = new Seat(table);
    MenuItem menu = new MenuItem("menu", r.getMenu());
    PricedMenuItem pricedItem = new PricedMenuItem(10.99, r, menu);
    r.addCurrentOrder(newOrder);
    try {
      error = RestoAppController.endOrder(newOrder);
    } catch (InvalidInputException e) {
      fail();
    }

    assertEquals(
        "WARNING: These tables failed to end order: " + Integer.toString(table.getNumber()) + " ",
        error);
  }


  @Test
  public void testEndOrderValid() {
    String error = "";
    RestoApp r = RestoApplication.getRestoApp();
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("20:00:00");
    Table table = new Table(1, 5, 5, 10, 10, r);
    Order newOrder = new Order(date, time, r, table);
    Seat seat = new Seat(table);
    MenuItem menu = new MenuItem("menu", r.getMenu());
    PricedMenuItem pricedItem = new PricedMenuItem(10.99, r, menu);
    r.addCurrentOrder(newOrder);
    table.orderItem(2, newOrder, seat, pricedItem);
    r.addCurrentTable(table);
    table.addToOrder(newOrder);
    try {
      error = RestoAppController.endOrder(newOrder);
    } catch (InvalidInputException e) {
      fail();
    }

    assertEquals("", error);
  }

  @Test
  public void testTableAddtoOrderValid() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    Table table2 = new Table(5, 8, 10, 10, 10, r);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Order order = new Order(date, time, r, table);

    assertEquals(Table.Status.Available, table2.getStatus());
    table2.addToOrder(order);
    assertEquals(Table.Status.NothingOrdered, table2.getStatus());
  }


  @Test
  public void testTableEndOrderOrdered() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Order order = new Order(date, time, r, table);
    table.addToOrder(order);
    Seat seat = new Seat(table);
    MenuItem menu = new MenuItem("menu", r.getMenu());
    PricedMenuItem pricedMenu = new PricedMenuItem(13, r, menu);
    table.orderItem(3, order, seat, pricedMenu);
    assertEquals(Table.Status.Ordered, table.getStatus());
    table.endOrder(order);
    assertEquals(Table.Status.Available, table.getStatus());
    int result = table.indexOfOrder(order);
    assertEquals(0, result);
  }

  @Test
  public void testTableEndOrderNothingOrdered() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Order order = new Order(date, time, r, table);
    table.addToOrder(order);
    assertEquals(Table.Status.NothingOrdered, table.getStatus());
    table.endOrder(order);
    assertEquals(Table.Status.Available, table.getStatus());
    int result = table.indexOfOrder(order);
    assertEquals(-1, result);
  }

  @Test
  public void testTableStartOrderValid() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Order order = new Order(date, time, r, table);
    assertEquals(1, table.getOrders().size());
    assertEquals(Table.Status.Available, table.getStatus());
    table.startOrder();
    assertEquals(Table.Status.NothingOrdered, table.getStatus());
    assertEquals(2, table.getOrders().size());

  }

  @Test
  public void testStartOrderInvalid() {
    RestoApp r = RestoApplication.getRestoApp();
    Table table = r.addTable(3, 10, 10, 10, 10);
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");
    Order order = new Order(date, time, r, table);
    table.startOrder();
    assertEquals(2, table.getOrders().size());
    assertEquals(false, table.startOrder());
    assertEquals(Table.Status.NothingOrdered, table.getStatus());
    assertEquals(2, table.getOrders().size());

  }

  @Test
  public void testGetOrderItems() {
    RestoApp r = RestoApplication.getRestoApp();
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");

    Table table = r.addTable(4, 10, 10, 10, 10);
    r.addCurrentTable(table);

    Seat seat = new Seat(table);
    table.addCurrentSeat(seat);

    MenuItem menu = new MenuItem("menu", r.getMenu());
    PricedMenuItem pricedItem = new PricedMenuItem(10.99, r, menu);
    Order order = new Order(date, time, r, table);
    OrderItem aOrderItem = new OrderItem(1, pricedItem, order, seat);

    order.addOrderItem(aOrderItem);
    seat.addOrderItem(aOrderItem);
    table.startOrder();
    r.addCurrentOrder(order);
    table.addCurrentSeat(seat);

    List<OrderItem> list = new ArrayList<OrderItem>();
    List<OrderItem> listActual = new ArrayList<OrderItem>();
    listActual.addAll(table.getOrder(table.numberOfOrders() - 1).getOrderItems());
    try {
      list.addAll(RestoAppController.getOrderItems(table));
    } catch (InvalidInputException e) {
      e.printStackTrace();
    }
    assertEquals(listActual, list);
  }

  @Test
  public void testGetOrderItemsTableNotInUse() {
    String error = "";
    RestoApp r = RestoApplication.getRestoApp();
    Date date = Date.valueOf("2018-03-30");
    Time time = Time.valueOf("18:00:00");

    Table table = r.addTable(4, 10, 10, 10, 10);
    r.addCurrentTable(table);

    Seat seat = new Seat(table);
    table.addCurrentSeat(seat);

    MenuItem menu = new MenuItem("menu", r.getMenu());
    PricedMenuItem pricedItem = new PricedMenuItem(10.99, r, menu);
    Order order = new Order(date, time, r, table);
    OrderItem aOrderItem = new OrderItem(1, pricedItem, order, seat);

    order.addOrderItem(aOrderItem);
    seat.addOrderItem(aOrderItem);
    r.addCurrentOrder(order);
    table.addCurrentSeat(seat);

    List<OrderItem> list = new ArrayList<OrderItem>();
    List<OrderItem> listActual = new ArrayList<OrderItem>();
    listActual.addAll(table.getOrder(table.numberOfOrders() - 1).getOrderItems());
    try {
      list.addAll(RestoAppController.getOrderItems(table));
    } catch (InvalidInputException e) {
      error += e.getMessage();
    }
    assertEquals("Table is not in use!", error);
  }
  
  @Test
	public void testCancelOrder(){
		 String error = "";
		 int size=0;
	      RestoApp r = RestoApplication.getRestoApp();
	      Date date = Date.valueOf("2018-03-30");
	      Time time = Time.valueOf("20:00:00");
	      Table table = new Table(1, 5, 5, 10, 10, r);
	      Seat seat = new Seat(table);
	      table.addCurrentSeat(seat);
	      r.addCurrentTable(table);
	      
	      MenuItem menu = new MenuItem("menu", r.getMenu());
	      PricedMenuItem pricedItem = new PricedMenuItem(10.99, r, menu);
	      menu.setCurrentPricedMenuItem(pricedItem);
	      
	      if(!table.startOrder()){
	    	  fail("Could not start order!");
	      }
	      table.orderItem(2, table.getOrder(0), seat, pricedItem);

	      try {
	    	 System.out.println(table.getStatusFullName());
	    	System.out.println("before "+ table.getOrder(0).numberOfOrderItems());
	        RestoAppController.cancelOrder(table);
	        System.out.println("after "+ table.getOrder(0).numberOfOrderItems());
	        size=RestoAppController.getOrderItems(table).size();
	      } catch (InvalidInputException e) {
	    	 
	        assertEquals(0, size);
	      }
	}

}

    
