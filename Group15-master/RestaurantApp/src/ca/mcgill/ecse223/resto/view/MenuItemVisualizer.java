package ca.mcgill.ecse223.resto.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;

public class MenuItemVisualizer extends JPanel{

	//UI elements
	private static final int RECTWIDTH = 200;
	private static final int RECTHEIGHT = 200;
	private static final int SMALL_RECTHEIGHT = 10;
	private static final int SPACING = 0;
	private static final int MAX_NUM_MENUITEMS_SHOWN = 20;

	//data elements
	MenuItem menuItem;
	private HashMap<Rectangle2D, MenuItem> menuItems;
	private List<MenuItem> menuItemList;
	private MenuItem selectedMenuItem;
	private int firstVisibleMenuItem;
	private ItemCategory itemCategory;


	public MenuItemVisualizer() {
		super();
		init();
	}

	private void init() {
		menuItem = null;
		menuItems = new HashMap<Rectangle2D, MenuItem>();
		selectedMenuItem = null;
		firstVisibleMenuItem = 0;
		repaint();
	}

	public void setCategory(ItemCategory itemCategory) {
		this.itemCategory = itemCategory;
	}

	public void setMenuItemList(List<MenuItem> menuItemList) {
		this.menuItemList = menuItemList;
		repaint();
	}


	private void doDrawing(Graphics g) throws InvalidInputException {
		Graphics2D g2d = (Graphics2D) g.create();
		BasicStroke thickStroke = new BasicStroke(4);
		g2d.setStroke(thickStroke);
		BasicStroke thinStroke = new BasicStroke(2);
		g2d.setStroke(thinStroke);
		Rectangle2D rectangle = new Rectangle2D.Float(0,0, RECTWIDTH, RECTHEIGHT);
		g2d.setColor(Color.WHITE);
		g2d.fill(rectangle);
		g2d.setColor(Color.BLACK);
		g2d.draw(rectangle);

		if (menuItemList != null) {

			for (int i = 0; i < menuItemList.size(); i++) {

				Rectangle2D small_rectangles = new Rectangle2D.Float(10, i * SMALL_RECTHEIGHT + 10, RECTWIDTH - 12, SMALL_RECTHEIGHT);
				g2d.setColor(Color.WHITE);
				g2d.fill(small_rectangles);
				g2d.setStroke(new BasicStroke(2));
				g2d.setColor(Color.BLACK);
				g2d.drawString("-" + menuItemList.get(i).getName() + " ", 10, i * SMALL_RECTHEIGHT);
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			doDrawing(g);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




}
