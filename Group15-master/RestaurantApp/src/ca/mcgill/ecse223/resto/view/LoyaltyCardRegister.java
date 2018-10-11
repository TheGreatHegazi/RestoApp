package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoAppController;
import ca.mcgill.ecse223.resto.model.LoyaltyCard;
import ca.mcgill.ecse223.resto.model.RestoApp;

public class LoyaltyCardRegister extends JFrame {

  // UI Elements
  
 
  private JLabel name;
  private JLabel email;
  private JLabel mobile;
  private JLabel error;

  private JButton cancel;
  private JButton confirm;

  private JTextField nameField;
  private JTextField emailField;
  private JTextField mobileField;
  
  // Data Elements
  int numberCard;
  
  public LoyaltyCardRegister() {
    initComponents();
  }

  private void initComponents() {
    
    this.setTitle("Loyalty Card Registration");
    
    mobile = new JLabel();
    mobile.setText("Mobile:");

    name = new JLabel();
    name.setText("Name:");
    email = new JLabel();
    email.setText("Email:");


    cancel = new JButton();
    cancel.setText("Cancel");
    confirm = new JButton();
    confirm.setText("Confirm");

    nameField = new JTextField();
    emailField = new JTextField();
    mobileField = new JTextField();
    
    error = new JLabel();
    error.setText("");

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setHorizontalGroup(layout.createParallelGroup()
        .addGroup(layout.createSequentialGroup().addComponent(name).addComponent(nameField))
        .addGroup(layout.createSequentialGroup().addComponent(email).addComponent(emailField))
        .addGroup(layout.createSequentialGroup().addComponent(mobile).addComponent(mobileField))
        .addGroup(layout.createSequentialGroup().addComponent(cancel).addComponent(confirm))
        .addComponent(error));
    
    
    mobileField.setPreferredSize(new Dimension(200, 10));
    mobileField.setMaximumSize(new Dimension(200, 10));
    layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {email, mobile, name});
    layout.linkSize(SwingConstants.HORIZONTAL,
        new java.awt.Component[] {mobileField, emailField, nameField});
    layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {cancel, confirm});
    layout.linkSize(SwingConstants.VERTICAL,
        new java.awt.Component[] {mobileField, emailField, nameField});

    
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup().addComponent(name).addComponent(nameField))
        .addGroup(layout.createParallelGroup().addComponent(email).addComponent(emailField))
        .addGroup(layout.createParallelGroup().addComponent(mobile).addComponent(mobileField))
        .addGroup(layout.createParallelGroup().addComponent(cancel).addComponent(confirm))
        .addComponent(error));

    pack();
    
    this.setBounds(0, 0, 300, 250);
    this.setPreferredSize(new Dimension(500,500));


    cancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
      }
    });
    
    confirm.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        String name = nameField.getText();
        String mobile = mobileField.getText();
        String email = emailField.getText();
        
        try{
         numberCard = RestoAppController.addLoyaltyCard(name, email, mobile);
          error.setText("LoyaltyCard ID: " + numberCard);
          
        } catch (InvalidInputException e){
          error.setText(e.getMessage());
        }

      }
    });

  }




}

