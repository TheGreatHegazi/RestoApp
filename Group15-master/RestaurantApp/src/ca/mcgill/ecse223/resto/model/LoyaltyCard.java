/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse223.resto.model;
import java.io.Serializable;
import java.util.*;

// line 114 "../../../../../RestoAppPersistence.ump"
// line 78 "../../../../../RestoApp v3.ump"
public class LoyaltyCard implements Serializable
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static Map<String, LoyaltyCard> loyaltycardsByEmail = new HashMap<String, LoyaltyCard>();
  private static int nextNumber = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //LoyaltyCard Attributes
  private String name;
  private String email;
  private String phoneNumber;
  private int points;

  //Autounique Attributes
  private int number;

  //LoyaltyCard Associations
  private List<Bill> bills;
  private RestoApp restoApp;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public LoyaltyCard(String aName, String aEmail, String aPhoneNumber, RestoApp aRestoApp)
  {
    name = aName;
    phoneNumber = aPhoneNumber;
    points = 0;
    if (!setEmail(aEmail))
    {
      throw new RuntimeException("Cannot create due to duplicate email");
    }
    number = nextNumber++;
    bills = new ArrayList<Bill>();
    boolean didAddRestoApp = setRestoApp(aRestoApp);
    if (!didAddRestoApp)
    {
      throw new RuntimeException("Unable to create loyaltyCard due to restoApp");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setEmail(String aEmail)
  {
    boolean wasSet = false;
    String anOldEmail = getEmail();
    if (hasWithEmail(aEmail)) {
      return wasSet;
    }
    email = aEmail;
    wasSet = true;
    if (anOldEmail != null) {
      loyaltycardsByEmail.remove(anOldEmail);
    }
    loyaltycardsByEmail.put(aEmail, this);
    return wasSet;
  }

  public boolean setPhoneNumber(String aPhoneNumber)
  {
    boolean wasSet = false;
    phoneNumber = aPhoneNumber;
    wasSet = true;
    return wasSet;
  }

  public boolean setPoints(int aPoints)
  {
    boolean wasSet = false;
    points = aPoints;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public String getEmail()
  {
    return email;
  }

  public static LoyaltyCard getWithEmail(String aEmail)
  {
    return loyaltycardsByEmail.get(aEmail);
  }

  public static boolean hasWithEmail(String aEmail)
  {
    return getWithEmail(aEmail) != null;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public int getPoints()
  {
    return points;
  }

  public int getNumber()
  {
    return number;
  }

  public Bill getBill(int index)
  {
    Bill aBill = bills.get(index);
    return aBill;
  }

  public List<Bill> getBills()
  {
    List<Bill> newBills = Collections.unmodifiableList(bills);
    return newBills;
  }

  public int numberOfBills()
  {
    int number = bills.size();
    return number;
  }

  public boolean hasBills()
  {
    boolean has = bills.size() > 0;
    return has;
  }

  public int indexOfBill(Bill aBill)
  {
    int index = bills.indexOf(aBill);
    return index;
  }

  public RestoApp getRestoApp()
  {
    return restoApp;
  }

  public static int minimumNumberOfBills()
  {
    return 0;
  }

  public boolean addBill(Bill aBill)
  {
    boolean wasAdded = false;
    if (bills.contains(aBill)) { return false; }
    LoyaltyCard existingLoyaltyCard = aBill.getLoyaltyCard();
    if (existingLoyaltyCard == null)
    {
      aBill.setLoyaltyCard(this);
    }
    else if (!this.equals(existingLoyaltyCard))
    {
      existingLoyaltyCard.removeBill(aBill);
      addBill(aBill);
    }
    else
    {
      bills.add(aBill);
    }
    wasAdded = true;
    // line 88 "../../../../../RestoApp v3.ump"
    double balance = 0;
        if (wasAdded) {
              for (Seat seat: aBill.getIssuedForSeats()){
                for(OrderItem orderItem: seat.getOrderItems()){
                  balance += orderItem.getPricedMenuItem().getPrice();
                }
              }
              points += (int)balance/1000;
              
       }
    // END OF UMPLE AFTER INJECTION
    return wasAdded;
  }

  public boolean removeBill(Bill aBill)
  {
    boolean wasRemoved = false;
    if (bills.contains(aBill))
    {
      bills.remove(aBill);
      aBill.setLoyaltyCard(null);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  public boolean addBillAt(Bill aBill, int index)
  {  
    boolean wasAdded = false;
    if(addBill(aBill))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBills()) { index = numberOfBills() - 1; }
      bills.remove(aBill);
      bills.add(index, aBill);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveBillAt(Bill aBill, int index)
  {
    boolean wasAdded = false;
    if(bills.contains(aBill))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBills()) { index = numberOfBills() - 1; }
      bills.remove(aBill);
      bills.add(index, aBill);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addBillAt(aBill, index);
    }
    return wasAdded;
  }

  public boolean setRestoApp(RestoApp aRestoApp)
  {
    boolean wasSet = false;
    if (aRestoApp == null)
    {
      return wasSet;
    }

    RestoApp existingRestoApp = restoApp;
    restoApp = aRestoApp;
    if (existingRestoApp != null && !existingRestoApp.equals(aRestoApp))
    {
      existingRestoApp.removeLoyaltyCard(this);
    }
    restoApp.addLoyaltyCard(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    loyaltycardsByEmail.remove(getEmail());
    while( !bills.isEmpty() )
    {
      bills.get(0).setLoyaltyCard(null);
    }
    RestoApp placeholderRestoApp = restoApp;
    this.restoApp = null;
    if(placeholderRestoApp != null)
    {
      placeholderRestoApp.removeLoyaltyCard(this);
    }
  }

  // line 120 "../../../../../RestoAppPersistence.ump"
   public static  void reinitializeAutouniqueNumber(List<LoyaltyCard> loyaltyCards){
    nextNumber = 0; 
		for (LoyaltyCard loyaltyCard : loyaltyCards) {
			if(loyaltyCard.getNumber() > nextNumber) {
				nextNumber = loyaltyCard.getNumber();
			}
		}
		nextNumber++;
  }

  // line 130 "../../../../../RestoAppPersistence.ump"
   public static  void reinitializeUniqueName(List<LoyaltyCard> loyaltyCards){
    loyaltycardsByEmail = new HashMap<String, LoyaltyCard>();
 		for (LoyaltyCard loyaltyCard: loyaltyCards) {
 			loyaltycardsByEmail.put(loyaltyCard.getEmail(), loyaltyCard);
 		}
  }


  public String toString()
  {
    return super.toString() + "["+
            "number" + ":" + getNumber()+ "," +
            "name" + ":" + getName()+ "," +
            "email" + ":" + getEmail()+ "," +
            "phoneNumber" + ":" + getPhoneNumber()+ "," +
            "points" + ":" + getPoints()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "restoApp = "+(getRestoApp()!=null?Integer.toHexString(System.identityHashCode(getRestoApp())):"null");
  }  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 117 "../../../../../RestoAppPersistence.ump"
  private static final long serialVersionUID = 3875119530984412317L ;

  
}