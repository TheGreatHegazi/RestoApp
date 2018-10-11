/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse223.resto.model;
import java.io.Serializable;
import java.util.*;

// line 38 "../../../../../RestoAppPersistence.ump"
// line 1 "../../../../../RestoAppTableStateMachine.ump"
// line 30 "../../../../../RestoApp v3.ump"
public class Table implements Serializable
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static Map<Integer, Table> tablesByNumber = new HashMap<Integer, Table>();

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Table Attributes
  private int number;
  private int x;
  private int y;
  private int width;
  private int length;

  //Table State Machines
  public enum Status { Available, NothingOrdered, Ordered }
  private Status status;

  //Table Associations
  private List<Seat> seats;
  private List<Seat> currentSeats;
  private RestoApp restoApp;
  private List<Reservation> reservations;
  private List<Order> orders;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Table(int aNumber, int aX, int aY, int aWidth, int aLength, RestoApp aRestoApp)
  {
    x = aX;
    y = aY;
    width = aWidth;
    length = aLength;
    if (!setNumber(aNumber))
    {
      throw new RuntimeException("Cannot create due to duplicate number");
    }
    seats = new ArrayList<Seat>();
    currentSeats = new ArrayList<Seat>();
    boolean didAddRestoApp = setRestoApp(aRestoApp);
    if (!didAddRestoApp)
    {
      throw new RuntimeException("Unable to create table due to restoApp");
    }
    reservations = new ArrayList<Reservation>();
    orders = new ArrayList<Order>();
    setStatus(Status.Available);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setNumber(int aNumber)
  {
    boolean wasSet = false;
    Integer anOldNumber = getNumber();
    if (hasWithNumber(aNumber)) {
      return wasSet;
    }
    number = aNumber;
    wasSet = true;
    if (anOldNumber != null) {
      tablesByNumber.remove(anOldNumber);
    }
    tablesByNumber.put(aNumber, this);
    return wasSet;
  }

  public boolean setX(int aX)
  {
    boolean wasSet = false;
    x = aX;
    wasSet = true;
    return wasSet;
  }

  public boolean setY(int aY)
  {
    boolean wasSet = false;
    y = aY;
    wasSet = true;
    return wasSet;
  }

  public boolean setWidth(int aWidth)
  {
    boolean wasSet = false;
    width = aWidth;
    wasSet = true;
    return wasSet;
  }

  public boolean setLength(int aLength)
  {
    boolean wasSet = false;
    length = aLength;
    wasSet = true;
    return wasSet;
  }

  public int getNumber()
  {
    return number;
  }

  public static Table getWithNumber(int aNumber)
  {
    return tablesByNumber.get(aNumber);
  }

  public static boolean hasWithNumber(int aNumber)
  {
    return getWithNumber(aNumber) != null;
  }

  public int getX()
  {
    return x;
  }

  public int getY()
  {
    return y;
  }

  public int getWidth()
  {
    return width;
  }

  public int getLength()
  {
    return length;
  }

  public String getStatusFullName()
  {
    String answer = status.toString();
    return answer;
  }

  public Status getStatus()
  {
    return status;
  }

  public boolean startOrder()
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Available:
        // line 4 "../../../../../RestoAppTableStateMachine.ump"
        new Order(new java.sql.Date(Calendar.getInstance().getTime().getTime()), new java.sql.Time(Calendar.getInstance().getTime().getTime()), this.getRestoApp(), this);
        setStatus(Status.NothingOrdered);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean addToOrder(Order o)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Available:
        // line 7 "../../../../../RestoAppTableStateMachine.ump"
        o.addTable(this);
        setStatus(Status.NothingOrdered);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean orderItem(int quantity,Order o,Seat s,PricedMenuItem i)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case NothingOrdered:
        if (quantityIsPositive(quantity))
        {
        // line 12 "../../../../../RestoAppTableStateMachine.ump"
          // create a new order item with the provided quantity, order, seat, and priced menu item
            new OrderItem(quantity, i, o, s);
          setStatus(Status.Ordered);
          wasEventProcessed = true;
          break;
        }
        break;
      case Ordered:
        if (quantityIsPositive(quantity))
        {
        // line 31 "../../../../../RestoAppTableStateMachine.ump"
          // create a new order item with the provided quantity, order, seat, and priced menu item
        	new OrderItem(quantity, i, o, s);
          setStatus(Status.Ordered);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean addToOrderItem(OrderItem i,Seat s)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case NothingOrdered:
        // line 16 "../../../../../RestoAppTableStateMachine.ump"
        // add provided seat to provided order item unless seat has already been added, in which case nothing needs to be done
            if (i.indexOfSeat(s) == -1){
            	i.addSeat(s);
            }
        setStatus(Status.Ordered);
        wasEventProcessed = true;
        break;
      case Ordered:
        // line 35 "../../../../../RestoAppTableStateMachine.ump"
        // add provided seat to provided order item unless seat has already been added, in which case nothing needs to be done
        	 if (i.indexOfSeat(s) == -1){
            	i.addSeat(s);
            }
        setStatus(Status.Ordered);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean endOrder(Order o)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case NothingOrdered:
        // line 22 "../../../../../RestoAppTableStateMachine.ump"
        if (!o.removeTable(this)) {
               if (o.numberOfTables() == 1) {
                  o.delete();
               }
            }
        setStatus(Status.Available);
        wasEventProcessed = true;
        break;
      case Ordered:
        if (allSeatsBilled())
        {
        // line 125 "../../../../../RestoAppTableStateMachine.ump"
          
          setStatus(Status.Available);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean cancelOrderItem(OrderItem i)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Ordered:
        if (iIsLastItem(i))
        {
        // line 41 "../../../../../RestoAppTableStateMachine.ump"
          // delete order item unless the order item is shared in which case only the 
         	// seats of this table are removed from the order item.
            ArrayList<Seat> seats = new ArrayList<Seat>();
         	for(Seat seat : i .getSeats()) {
         		seats.add(seat);
         	}
            for(Seat seat : seats) {
				if(seat.getTable()==this) {
					if(i.numberOfSeats()>1){
						seat.removeOrderItem(i);
					} else {
						i.delete();
					}
				}
			}
			if(i.getSeats().size()==0) {
				i.delete();
			}
          setStatus(Status.NothingOrdered);
          wasEventProcessed = true;
          break;
        }
        if (!(iIsLastItem(i)))
        {
        // line 61 "../../../../../RestoAppTableStateMachine.ump"
          // delete order item unless the order item is shared in which case only the 
         	// seats of this table are removed from the order item.
         	ArrayList<Seat> seats = new ArrayList<Seat>();
         	for(Seat seat : i .getSeats()) {
         		seats.add(seat);
         	}
            for(Seat seat : seats) {
				if(seat.getTable()==this) {
					if(i.numberOfSeats()>1){
						seat.removeOrderItem(i);
					} else {
						i.delete();
					}
				}
			}
			if(i.getSeats().size()==0) {
				i.delete();
			}
          setStatus(Status.Ordered);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean cancelOrder()
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Ordered:
        // line 81 "../../../../../RestoAppTableStateMachine.ump"
        // delete all order items of the table (if an order item is shared, then only the seat(s) of this table are
            // removed from the order item
            ArrayList<OrderItem> items = new ArrayList<OrderItem>();
            for(OrderItem i : getOrder(numberOfOrders()-1).getOrderItems()){
            	items.add(i);
            }
            for(OrderItem i : items){
            	cancelOrderItem(i);
            }
        setStatus(Status.NothingOrdered);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean billForSeat(Order o,Seat s)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Ordered:
        // line 93 "../../../../../RestoAppTableStateMachine.ump"
        // create a new bill with the provided order and seat; if the provided seat is already assigned to
            // another bill for the current order, then the seat is first removed from the other bill and if no seats
            // are left for the bill, the bill is deleted
            for(int i= 0; i<o.getBills().size(); i++){
             if (o.getBill(i).getIssuedForSeats().contains(s)){
             	o.getBill(i).removeIssuedForSeat(s);
             	if (o.getBill(i).getIssuedForSeats().size() == 0){
             	  o.getBill(i).delete();
            	}
            }
            }
            new Bill(o, restoApp, s);
        setStatus(Status.Ordered);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean addToBill(Bill b,Seat s)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Ordered:
        // line 108 "../../../../../RestoAppTableStateMachine.ump"
        // add provided seat to provided bill unless seat has already been added, in which case nothing needs
            // to be done; if the provided seat is already assigned to another bill for the current order, then the
            // seat is first removed from the other bill and if no seats are left for the bill, the bill is deleted
             Order o = getOrder(getOrders().size() - 1);
			for (int i = 0; i < getOrder(getOrders().size() - 1).getBills().size(); i++) {
				if (o.getBill(i).getIssuedForSeats().contains(s)) {
					o.getBill(i).removeIssuedForSeat(s);
					if (o.getBill(i).getIssuedForSeats().size() == 0) {
						o.getBill(i).delete();
					}
				}
			}
			if (!b.getIssuedForSeats().contains(s)) {
				b.addIssuedForSeat(s);
			}
        setStatus(Status.Ordered);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void setStatus(Status aStatus)
  {
    status = aStatus;
  }

  public Seat getSeat(int index)
  {
    Seat aSeat = seats.get(index);
    return aSeat;
  }

  public List<Seat> getSeats()
  {
    List<Seat> newSeats = Collections.unmodifiableList(seats);
    return newSeats;
  }

  public int numberOfSeats()
  {
    int number = seats.size();
    return number;
  }

  public boolean hasSeats()
  {
    boolean has = seats.size() > 0;
    return has;
  }

  public int indexOfSeat(Seat aSeat)
  {
    int index = seats.indexOf(aSeat);
    return index;
  }

  public Seat getCurrentSeat(int index)
  {
    Seat aCurrentSeat = currentSeats.get(index);
    return aCurrentSeat;
  }

  /**
   * subsets seats
   */
  public List<Seat> getCurrentSeats()
  {
    List<Seat> newCurrentSeats = Collections.unmodifiableList(currentSeats);
    return newCurrentSeats;
  }

  public int numberOfCurrentSeats()
  {
    int number = currentSeats.size();
    return number;
  }

  public boolean hasCurrentSeats()
  {
    boolean has = currentSeats.size() > 0;
    return has;
  }

  public int indexOfCurrentSeat(Seat aCurrentSeat)
  {
    int index = currentSeats.indexOf(aCurrentSeat);
    return index;
  }

  public RestoApp getRestoApp()
  {
    return restoApp;
  }

  public Reservation getReservation(int index)
  {
    Reservation aReservation = reservations.get(index);
    return aReservation;
  }

  public List<Reservation> getReservations()
  {
    List<Reservation> newReservations = Collections.unmodifiableList(reservations);
    return newReservations;
  }

  public int numberOfReservations()
  {
    int number = reservations.size();
    return number;
  }

  public boolean hasReservations()
  {
    boolean has = reservations.size() > 0;
    return has;
  }

  public int indexOfReservation(Reservation aReservation)
  {
    int index = reservations.indexOf(aReservation);
    return index;
  }

  public Order getOrder(int index)
  {
    Order aOrder = orders.get(index);
    return aOrder;
  }

  public List<Order> getOrders()
  {
    List<Order> newOrders = Collections.unmodifiableList(orders);
    return newOrders;
  }

  public int numberOfOrders()
  {
    int number = orders.size();
    return number;
  }

  public boolean hasOrders()
  {
    boolean has = orders.size() > 0;
    return has;
  }

  public int indexOfOrder(Order aOrder)
  {
    int index = orders.indexOf(aOrder);
    return index;
  }

  public boolean isNumberOfSeatsValid()
  {
    boolean isValid = numberOfSeats() >= minimumNumberOfSeats();
    return isValid;
  }

  public static int minimumNumberOfSeats()
  {
    return 1;
  }

  public Seat addSeat()
  {
    Seat aNewSeat = new Seat(this);
    return aNewSeat;
  }

  public boolean addSeat(Seat aSeat)
  {
    boolean wasAdded = false;
    if (seats.contains(aSeat)) { return false; }
    Table existingTable = aSeat.getTable();
    boolean isNewTable = existingTable != null && !this.equals(existingTable);

    if (isNewTable && existingTable.numberOfSeats() <= minimumNumberOfSeats())
    {
      return wasAdded;
    }
    if (isNewTable)
    {
      aSeat.setTable(this);
    }
    else
    {
      seats.add(aSeat);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeSeat(Seat aSeat)
  {
    boolean wasRemoved = false;
    //Unable to remove aSeat, as it must always have a table
    if (this.equals(aSeat.getTable()))
    {
      return wasRemoved;
    }

    //table already at minimum (1)
    if (numberOfSeats() <= minimumNumberOfSeats())
    {
      return wasRemoved;
    }

    seats.remove(aSeat);
    wasRemoved = true;
    return wasRemoved;
  }

  public boolean addSeatAt(Seat aSeat, int index)
  {  
    boolean wasAdded = false;
    if(addSeat(aSeat))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfSeats()) { index = numberOfSeats() - 1; }
      seats.remove(aSeat);
      seats.add(index, aSeat);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveSeatAt(Seat aSeat, int index)
  {
    boolean wasAdded = false;
    if(seats.contains(aSeat))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfSeats()) { index = numberOfSeats() - 1; }
      seats.remove(aSeat);
      seats.add(index, aSeat);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addSeatAt(aSeat, index);
    }
    return wasAdded;
  }

  public static int minimumNumberOfCurrentSeats()
  {
    return 0;
  }

  public boolean addCurrentSeat(Seat aCurrentSeat)
  {
    boolean wasAdded = false;
    if (currentSeats.contains(aCurrentSeat)) { return false; }
    currentSeats.add(aCurrentSeat);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeCurrentSeat(Seat aCurrentSeat)
  {
    boolean wasRemoved = false;
    if (currentSeats.contains(aCurrentSeat))
    {
      currentSeats.remove(aCurrentSeat);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  public boolean addCurrentSeatAt(Seat aCurrentSeat, int index)
  {  
    boolean wasAdded = false;
    if(addCurrentSeat(aCurrentSeat))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCurrentSeats()) { index = numberOfCurrentSeats() - 1; }
      currentSeats.remove(aCurrentSeat);
      currentSeats.add(index, aCurrentSeat);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveCurrentSeatAt(Seat aCurrentSeat, int index)
  {
    boolean wasAdded = false;
    if(currentSeats.contains(aCurrentSeat))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCurrentSeats()) { index = numberOfCurrentSeats() - 1; }
      currentSeats.remove(aCurrentSeat);
      currentSeats.add(index, aCurrentSeat);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addCurrentSeatAt(aCurrentSeat, index);
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
      existingRestoApp.removeTable(this);
    }
    restoApp.addTable(this);
    wasSet = true;
    return wasSet;
  }

  public static int minimumNumberOfReservations()
  {
    return 0;
  }

  public boolean addReservation(Reservation aReservation)
  {
    boolean wasAdded = false;
    if (reservations.contains(aReservation)) { return false; }
    reservations.add(aReservation);
    if (aReservation.indexOfTable(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aReservation.addTable(this);
      if (!wasAdded)
      {
        reservations.remove(aReservation);
      }
    }
    return wasAdded;
  }

  public boolean removeReservation(Reservation aReservation)
  {
    boolean wasRemoved = false;
    if (!reservations.contains(aReservation))
    {
      return wasRemoved;
    }

    int oldIndex = reservations.indexOf(aReservation);
    reservations.remove(oldIndex);
    if (aReservation.indexOfTable(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aReservation.removeTable(this);
      if (!wasRemoved)
      {
        reservations.add(oldIndex,aReservation);
      }
    }
    return wasRemoved;
  }

  public boolean addReservationAt(Reservation aReservation, int index)
  {  
    boolean wasAdded = false;
    if(addReservation(aReservation))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfReservations()) { index = numberOfReservations() - 1; }
      reservations.remove(aReservation);
      reservations.add(index, aReservation);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveReservationAt(Reservation aReservation, int index)
  {
    boolean wasAdded = false;
    if(reservations.contains(aReservation))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfReservations()) { index = numberOfReservations() - 1; }
      reservations.remove(aReservation);
      reservations.add(index, aReservation);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addReservationAt(aReservation, index);
    }
    return wasAdded;
  }

  public static int minimumNumberOfOrders()
  {
    return 0;
  }

  public boolean addOrder(Order aOrder)
  {
    boolean wasAdded = false;
    if (orders.contains(aOrder)) { return false; }
    orders.add(aOrder);
    if (aOrder.indexOfTable(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aOrder.addTable(this);
      if (!wasAdded)
      {
        orders.remove(aOrder);
      }
    }
    return wasAdded;
  }

  public boolean removeOrder(Order aOrder)
  {
    boolean wasRemoved = false;
    if (!orders.contains(aOrder))
    {
      return wasRemoved;
    }

    int oldIndex = orders.indexOf(aOrder);
    orders.remove(oldIndex);
    if (aOrder.indexOfTable(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aOrder.removeTable(this);
      if (!wasRemoved)
      {
        orders.add(oldIndex,aOrder);
      }
    }
    return wasRemoved;
  }

  public boolean addOrderAt(Order aOrder, int index)
  {  
    boolean wasAdded = false;
    if(addOrder(aOrder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfOrders()) { index = numberOfOrders() - 1; }
      orders.remove(aOrder);
      orders.add(index, aOrder);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveOrderAt(Order aOrder, int index)
  {
    boolean wasAdded = false;
    if(orders.contains(aOrder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfOrders()) { index = numberOfOrders() - 1; }
      orders.remove(aOrder);
      orders.add(index, aOrder);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addOrderAt(aOrder, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    tablesByNumber.remove(getNumber());
    while (seats.size() > 0)
    {
      Seat aSeat = seats.get(seats.size() - 1);
      aSeat.delete();
      seats.remove(aSeat);
    }
    
    currentSeats.clear();
    RestoApp placeholderRestoApp = restoApp;
    this.restoApp = null;
    if(placeholderRestoApp != null)
    {
      placeholderRestoApp.removeTable(this);
    }
    ArrayList<Reservation> copyOfReservations = new ArrayList<Reservation>(reservations);
    reservations.clear();
    for(Reservation aReservation : copyOfReservations)
    {
      if (aReservation.numberOfTables() <= Reservation.minimumNumberOfTables())
      {
        aReservation.delete();
      }
      else
      {
        aReservation.removeTable(this);
      }
    }
    ArrayList<Order> copyOfOrders = new ArrayList<Order>(orders);
    orders.clear();
    for(Order aOrder : copyOfOrders)
    {
      if (aOrder.numberOfTables() <= Order.minimumNumberOfTables())
      {
        aOrder.delete();
      }
      else
      {
        aOrder.removeTable(this);
      }
    }
  }

  // line 44 "../../../../../RestoAppPersistence.ump"
   public static  void reinitializeUniqueNumber(List<Table> tables){
    tablesByNumber = new HashMap<Integer, Table>();
		for (Table table: tables) {
			tablesByNumber.put(table.getNumber(), table);
		}
  }


  /**
   * check that the provided quantity is an integer greater than 0
   */
  // line 132 "../../../../../RestoAppTableStateMachine.ump"
   private boolean quantityIsPositive(int quantity){
    boolean isPositive = false;
      if(quantity>0){
      	isPositive = true;
      }
      return isPositive;
  }


  /**
   * check that the provided order item is the last item of the current order of the table
   */
  // line 141 "../../../../../RestoAppTableStateMachine.ump"
   private boolean iIsLastItem(OrderItem i){
    boolean isLastItem = false;
      if(i.getOrder().getOrderItems().size() == 1){
      	isLastItem = true;
      }
      return isLastItem;
  }


  /**
   * check that all seats of the table have a bill that belongs to the current order of the table
   */
  // line 150 "../../../../../RestoAppTableStateMachine.ump"
   public boolean allSeatsBilled(){
    boolean allSeatsBilled = false;
      for(Seat seat : this.getCurrentSeats()){
      	if(seat.getOrderItems().size() == 0){
      		continue;
      	}
      	if(seat.getBills().size() == 0) {
      		return allSeatsBilled;
      	}
      	for(Bill bill : seat.getBills()){
      		if(this.getOrder(this.getOrders().size()-1).getBills().contains(bill)){
      			continue;
      		}
      		return allSeatsBilled;
      	}
      }
      allSeatsBilled = true;
      return allSeatsBilled;
  }


  public String toString()
  {
    return super.toString() + "["+
            "number" + ":" + getNumber()+ "," +
            "x" + ":" + getX()+ "," +
            "y" + ":" + getY()+ "," +
            "width" + ":" + getWidth()+ "," +
            "length" + ":" + getLength()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "restoApp = "+(getRestoApp()!=null?Integer.toHexString(System.identityHashCode(getRestoApp())):"null");
  }  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 41 "../../../../../RestoAppPersistence.ump"
  private static final long serialVersionUID = 8896099581655989380L ;

  
}