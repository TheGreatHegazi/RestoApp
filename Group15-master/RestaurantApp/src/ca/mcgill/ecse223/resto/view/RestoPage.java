package ca.mcgill.ecse223.resto.view;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;

public class RestoPage extends JFrame {
  private JLabel menuDisplayWindow;
  private JTable menuItems;
  private JButton displayMenuCategory;
  private JComboBox<String> menuToggleList;
  private JLabel selectMenuCategory;
  private JButton ok;
  private Integer selectedCategory = -1;
  private HashMap<Integer, ItemCategory> categories;
  private JScrollPane menuItemsScrollPane;
  private static final int WIDTH_MENU_ITEM_VISUALIZATION = 200;
  private static final int HEIGHT_MENU_ITEM_VISUALIZATION = 200;


  private static final long serialVersionUID = 1L;

  /** Creates new form RestoPage */
  public RestoPage() {
    initComponents();
    refreshData();
  }

  private void initComponents()  {
    menuDisplayWindow = new JLabel();
    menuItems = new JTable();
    displayMenuCategory = new JButton();
    menuToggleList = new JComboBox<String>(new String[0]);
    menuToggleList.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JComboBox<String> cb = (JComboBox<String>) evt.getSource();
        selectedCategory = cb.getSelectedIndex();
      }
    });
    selectMenuCategory = new JLabel();
    ok = new JButton();

    menuItems = new JTable() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
      public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!c.getBackground().equals(getSelectionBackground())) {
          Object obj = getModel().getValueAt(row, column);
          if (obj instanceof java.lang.String) {
            String str = (String)obj;
            c.setBackground(str.endsWith("sick)") ? Color.RED : str.endsWith("repair)") ? Color.YELLOW : Color.WHITE);
          }
          else {
            c.setBackground(Color.WHITE);
          }
        }
        return c;
      }
    };

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Menu Categories");

    menuDisplayWindow.setText("Menu Display Window");
    displayMenuCategory.setText("Display");
    ok.setText("OK");
    selectMenuCategory.setText("Select Menu Category");
    displayMenuCategory.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addDriverButtonActionPerformed(evt);
      }
      private void addDriverButtonActionPerformed(ActionEvent evt) {
        // TODO Auto-generated method stub 
      }
    });

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    layout.setHorizontalGroup(
        layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup()
            .addComponent(displayMenuCategory)
            .addComponent(ok)
            .addComponent(menuDisplayWindow)
            .addComponent(selectMenuCategory)
            .addComponent(menuToggleList)
            .addComponent(menuItems)));
    layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] {displayMenuCategory, ok, menuItems});
    layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] {menuDisplayWindow});
    layout.setVerticalGroup(
        layout.createParallelGroup()
        .addGroup(layout.createSequentialGroup()
            .addComponent(ok)
            .addComponent(selectMenuCategory)
            .addComponent(menuToggleList)

            .addGroup(layout.createParallelGroup()
                .addComponent(menuDisplayWindow)
                .addComponent(menuItems)
                .addComponent(displayMenuCategory))));
    pack();

  }


  private void refreshData() {

  }
}
