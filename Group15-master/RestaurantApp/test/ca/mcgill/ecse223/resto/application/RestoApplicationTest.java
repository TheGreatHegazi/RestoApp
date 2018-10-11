package ca.mcgill.ecse223.resto.application;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Date;
import java.sql.Time;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.Order;
import ca.mcgill.ecse223.resto.model.PricedMenuItem;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Table;

public class RestoApplicationTest {
	
	private static String filename = "testdata.resto";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Set filename to testdata for tests
		RestoApplication.setFilename(filename);
	}

	@Before
	public void setUp() throws Exception { 
		// Re-initialize RestoApp before each test
		File f = new File(filename);
		f.delete();
		RestoApp r = RestoApplication.getRestoApp();
		r.delete();
	}

	@Test
	public void testPersistence() {
		RestoApp r = RestoApplication.getRestoApp();
		String itemName = "Chips";
		int itemQuantity = 3;
		
		Table table = r.addTable(1, 2, 3, 4, 5);
		table.addSeat();
		Order order = r.addOrder(Date.valueOf("2018-09-13"), Time.valueOf("18:00:00"), table);
		r.addCurrentOrder(order);
		MenuItem menuItem = r.getMenu().addMenuItem(itemName);
		PricedMenuItem pricedItem = menuItem.addPricedMenuItem(10.50, r);
		menuItem.setCurrentPricedMenuItem(pricedItem);
		order.addOrderItem(itemQuantity, menuItem.getCurrentPricedMenuItem(), table.getSeat(0));
		
		int numberOfSeats = table.getSeats().size();

		
		
		RestoApplication.save();
		
		
		// Load domain again and check
		RestoApp r2 = (RestoApp) RestoApplication.load();
		assertEquals(r2.getTable(0).getNumber(), r.getTable(0).getNumber());
		checkCurrentOrder(numberOfSeats, itemName, itemQuantity, r2);
		
	}
	
	//TODO Fix Reinitialization Test - Make Sure that next order, reservation don't conflict
	@Test
	public void testPersistenceReinitialization() {
		RestoApp r = RestoApplication.getRestoApp(); 
		String itemName = "Chips";
		int itemQuantity = 3;
		
		Table table = r.addTable(1, 2, 3, 4, 5);
		table.addSeat();
		Order order = r.addOrder(Date.valueOf("2018-09-13"), Time.valueOf("18:00:00"), table);
		r.addCurrentOrder(order);
		MenuItem menuItem = r.getMenu().addMenuItem(itemName);
		PricedMenuItem pricedItem = menuItem.addPricedMenuItem(10.50, r);
		menuItem.setCurrentPricedMenuItem(pricedItem);
		order.addOrderItem(itemQuantity, menuItem.getCurrentPricedMenuItem(), table.getSeat(0));
		
		int numberOfSeats = table.getSeats().size();
		
		RestoApplication.save();

		// Simulate Application Shutdown
		RestoApplication.deleteRestoApp();
		
		
		RestoApplication.load();
		checkCurrentOrder(numberOfSeats, itemName, itemQuantity, r);
		
	}
	
	
	

	private void checkCurrentOrder(int numberOfSeats, String itemName, int itemQuantity, RestoApp r) {
		assertEquals(numberOfSeats, r.getTable(0).getSeats().size());
		assertEquals(itemName, r.getCurrentOrder(0).getOrderItem(0).getPricedMenuItem().getMenuItem().getName());
		assertEquals(itemQuantity, r.getCurrentOrder(0).getOrderItem(0).getQuantity());
	}

}
