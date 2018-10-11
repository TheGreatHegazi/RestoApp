package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.Order;
import ca.mcgill.ecse223.resto.model.OrderItem;
import ca.mcgill.ecse223.resto.model.PricedMenuItem;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Seat;
import ca.mcgill.ecse223.resto.model.Table;
import ca.mcgill.ecse223.resto.model.Table.Status;

public class ViewOrder extends JFrame {

  //UI constants;
  private RestoAppPage rootPage;
  Table table;

  //UI elements
  private JLabel errorMessage;

  //elements for table
  private JList<String> tableJList;
  private HashMap<Integer, OrderItem> orderItems;
  private DefaultListModel<String> l1;
  private List<OrderItem> selectedItems= new ArrayList<OrderItem>();

  //elements for okay button
  private JButton okayButton;

  //elements for items
  private JLabel orderMenuItems;

  //elements for refresh button
  private JButton refreshButton;

  //elements for cancel order button
  private JButton cancelOrderButton;

  //elements for cancel order item button
  private JButton cancelOrderItemButton;

  //data elements
  private String error = null;

  public ViewOrder(RestoAppPage rootPage, Table table) {
    this.rootPage = rootPage;
    this.table = table;
    initComponents();
    refreshData();
  }

  private void initComponents() {
    //initialize list
    selectedItems  = new ArrayList<OrderItem>();

    //elements for table
    l1 = new DefaultListModel<>();
    tableJList = new JList<>(l1);
    tableJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tableJList.setLayoutOrientation(JList.VERTICAL_WRAP);
    tableJList.setVisibleRowCount(15);
    JScrollPane listScroller = new JScrollPane(tableJList);
    listScroller.setPreferredSize(new Dimension(250, 80));
    tableJList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
        }
      }
    });

    // elements for error message
    errorMessage = new JLabel();
    errorMessage.setForeground(Color.RED);

    //elements for setting the y position
    orderMenuItems = new JLabel();
    orderMenuItems.setText("View Order");

    //elements for okay button
    okayButton = new JButton();
    okayButton.setText("Okay");

    //elements for refresh button
    refreshButton = new JButton();
    refreshButton.setText("Refresh");

    //elements for cancel order button
    cancelOrderButton = new JButton();
    cancelOrderButton.setText("Cancel Order");

    //elements for cancel order button
    cancelOrderItemButton = new JButton();
    cancelOrderItemButton.setText("Cancel Order Item");

    setTitle("View Order Items for Table");
    error = "";

    //global setting and listeners
    okayButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okayButtonActionPerformed(evt);
      }
    });

    refreshButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        refreshButtonActionPerformed(evt);
      }
    });

    cancelOrderButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelOrderButtonActionPerformed(evt);
      }
    });

    cancelOrderItemButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelOrderItemButtonActionPerformed(evt);
      }
    });

    this.getContentPane().setBackground(new java.awt.Color(200,200,200));
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    layout.setHorizontalGroup(
        layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup()
            .addComponent(orderMenuItems)
            .addComponent(errorMessage)
            .addComponent(tableJList)
            .addComponent(cancelOrderItemButton)
            )
        .addGroup(layout.createParallelGroup()
            .addComponent(refreshButton)
            .addComponent(cancelOrderButton)
            .addComponent(okayButton)
            ));

    layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {tableJList, okayButton, refreshButton, cancelOrderItemButton, cancelOrderButton});
    layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] {okayButton, refreshButton, cancelOrderItemButton, cancelOrderButton});
    layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {errorMessage, orderMenuItems});
    layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[] {errorMessage, orderMenuItems});

    layout.setVerticalGroup(
        layout.createSequentialGroup()
        .addGroup(layout.createSequentialGroup()
            .addComponent(orderMenuItems)
            .addComponent(errorMessage))            
            .addGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(tableJList)
                .addComponent(cancelOrderItemButton)
                )
            .addGroup(layout.createSequentialGroup()
                .addComponent(refreshButton)
                .addComponent(cancelOrderButton)
                .addComponent(okayButton)
                )));

        pack();
  }

  private void refreshData() {

    //populate page with data
    //select table
    l1.removeAllElements();
    int i = 0;
    List<OrderItem> orderItemsList = null;      
    orderItems = new HashMap<>();
    try {
      orderItemsList = RestoAppController.getOrderItems(table);
      if(orderItemsList.size() == 0){
        error = "No menu items ordered!";
      }
      if(orderItemsList != null && orderItemsList.size() > 0){
        for (OrderItem orderItem: orderItemsList) {
          l1.addElement(orderItem.getPricedMenuItem().getMenuItem().getName()+""); 
          orderItems.put(i, orderItem);
          i++;
        }
      }
    } catch (InvalidInputException e) {
      if (table == null) {
        error = e.getMessage();
      }
      if (table.getStatus() == Status.Available){
        error = e.getMessage();
      }
      if (!(table.numberOfOrders() > 0)){
        error = e.getMessage();
      }
    }
    if(error != null && !error.isEmpty() && error.length() > 0){
      errorMessage.setText(error);
    }
    pack();
  }


  public void okayButtonActionPerformed(java.awt.event.ActionEvent evt) {
    this.setVisible(false);
  }

  private void refreshButtonActionPerformed(ActionEvent evt) {
    refreshData();
  }

  private void cancelOrderButtonActionPerformed(ActionEvent evt) {
    if(this.table.getStatus() == Status.NothingOrdered){
      error = "Table has no order items!";
    }
    try {
      RestoAppController.cancelOrder(table);
      refreshData();
    } catch (InvalidInputException e) {
      error=e.getMessage();
    }
    refreshData();
  }


  private void cancelOrderItemButtonActionPerformed(ActionEvent evt) {
    int i = 0;
    int[] selectedIndicesArray = tableJList.getSelectedIndices();
    if(this.table.getStatus() == Status.NothingOrdered){
      error = "Table has no order items!";
    }
    if(selectedIndicesArray.length > 0){
      while (i < selectedIndicesArray.length) {
        if (!selectedItems.contains(orderItems.get(selectedIndicesArray[i]))) {
          selectedItems.add(orderItems.get(selectedIndicesArray[i]));
        }
        i++;
      } 
    }
    if(selectedItems.size() == 0){
      error = "Please select an order item";
    }
    for (OrderItem orderItem : selectedItems) {
      try {
        RestoAppController.cancelOrderItem(orderItem);
        refreshData();
      } catch (InvalidInputException e) {
        error=e.getMessage();
      }
    }
    refreshData();
  }    




}
