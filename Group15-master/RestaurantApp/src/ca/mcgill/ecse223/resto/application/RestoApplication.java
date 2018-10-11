package ca.mcgill.ecse223.resto.application;

import ca.mcgill.ecse223.resto.model.Menu;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.persistence.PersistenceObjectStream;
import ca.mcgill.ecse223.resto.view.AddOrderItemPage;
import ca.mcgill.ecse223.resto.view.AddReservationPage;
import ca.mcgill.ecse223.resto.view.EndOrderPage;
import ca.mcgill.ecse223.resto.view.IssueBill;
import ca.mcgill.ecse223.resto.view.LoyaltyCardRegister;
import ca.mcgill.ecse223.resto.view.MenuPage;
import ca.mcgill.ecse223.resto.view.RestoAppPage;
import ca.mcgill.ecse223.resto.view.SeatCustomersPage;

public class RestoApplication {

	private static RestoApp resto = null;
	private static String filename = "menu.resto";

	/**
	 * Main method boots up app on program execution
	 * @param args
	 */
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new RestoAppPage().setVisible(true);
			    
			}
		});
	}

	public static RestoApp getRestoApp() {
		if (resto == null) {
			resto = load();
		}
		if (resto.getMenu() == null) {
			new Menu(resto);
		}
		return resto;

	}

	public static void save() {
		PersistenceObjectStream.serialize(resto);
	}

	public static RestoApp load() {
		PersistenceObjectStream.setFilename(filename);
		resto = (RestoApp) PersistenceObjectStream.deserialize();
		// model can't be loaded - create empty RestoApp
		if (resto == null) {
			resto = new RestoApp();
		}
		else {
			resto.reinitialize();
		}
		return resto;
	}


	public static void setFilename(String newFilename) {
		filename = newFilename;
	}

	public static void deleteRestoApp() {
		if(resto != null) {
			resto.delete();
		}
		resto = null;
	}

}
