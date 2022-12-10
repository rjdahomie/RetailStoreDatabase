/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class Retail {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");
		System.out.println("10. Check Manager Order Info");
		System.out.println("11. Admin Update");
                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 5: updateProduct(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewPopularProducts(esql); break;
                   case 8: viewPopularCustomers(esql); break;
                   case 9: placeProductSupplyRequests(esql); break;
		   case 10: checkManagerOrderInfo(esql); break;
		   case 11: adminUpdate(esql); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String getUserType(Retail esql) {
       try {
	   /*
   	   - Returns a string of a users type ("manager", "customer", "admin")
 	   - Matches globalName to User, and retrieves the type (in SQL)
   	   */
	   String query = String.format("SELECT U.type FROM USERS U WHERE U.name = '%s'", globalName);
           List<List<String>> userType = esql.executeQueryAndReturnResult(query);
           String userTypeString = "";
	   if (userType.size() != 0) {
              userTypeString = userType.get(0).get(0);
           }
           else {
              return "";
           }
           return userTypeString.trim();
        }
        catch (Exception e){
           System.err.println(e.getMessage());
           return null;
        }
   }  

   public static String globalName = "";
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
	 globalName = name;
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return name;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql) {
     try {
          String query1 = String.format("SELECT U.latitude, U.longitude FROM Users U WHERE U.name = '%s'", globalName);
          List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
	  double userLat = Double.parseDouble(temp.get(0).get(0));
	  double userLong = Double.parseDouble(temp.get(0).get(1));
	    
	  String query2 = String.format("SELECT S.latitude, S.longitude, S.storeID, S.name FROM Store S");
	  temp = esql.executeQueryAndReturnResult(query2);
	  System.out.println("Store ID\tStore name\t\tLatitude\tLongitude");
          for (List<String> i : temp) {
	     if (esql.calculateDistance (userLat, userLong, Double.parseDouble(i.get(0)), Double.parseDouble(i.get(1))) <= 30){
		System.out.println(i.get(2) + "\t\t" + i.get(3) + "\t" + i.get(0) + "\t" +  i.get(1));
	     }
	  }
      }
      catch (Exception e) {
          System.err.println(e.getMessage());
      }
   }

   public static void viewProducts(Retail esql) {
      try {
	  String query1 = String.format("SELECT COUNT(*) FROM Store S");
          List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
          int storeMax = Integer.parseInt(temp.get(0).get(0));
	  boolean validStoreID = false;
	  String storeID = "";
          while (!validStoreID){
              System.out.print("\tEnter store ID: ");
              storeID = in.readLine();
              if (Integer.parseInt(storeID) <= storeMax){
                  validStoreID = true;
              } else {
                  System.out.println("\tInvalid store ID. ");
              }
          }
	  String query = String.format("SELECT * FROM Product P WHERE P.storeID = '%s'", storeID);
	  
          int rowCount = esql.executeQueryAndPrintResult(query);
      }
      catch (Exception e) {
          System.err.println(e.getMessage());
      }
   }

   public static void placeOrder(Retail esql) {
      try {
	  //Variables
	  String storeID = "";
	  String productName = "";
	  String numberOfUnits = "";

	  //Find current user's latitude and longitude
          String query1 = String.format("SELECT U.latitude, U.longitude, U.userID FROM Users U WHERE U.name = '%s'", globalName);
          List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
          double userLat = Double.parseDouble(temp.get(0).get(0));
          double userLong = Double.parseDouble(temp.get(0).get(1));
	  int userID = Integer.parseInt(temp.get(0).get(2));

	  //Ask user for store ID and validate that it is within 30 miles
	  boolean validStoreID = false;
	  while (!validStoreID){
	      System.out.print("\tEnter store ID: ");
	      storeID = in.readLine();
              String query2 = String.format("SELECT S.latitude, S.longitude, S.storeID, S.name FROM Store S WHERE S.storeID = '%s'", storeID);
	      temp = esql.executeQueryAndReturnResult(query2);
              if (esql.calculateDistance (userLat, userLong, Double.parseDouble(temp.get(0).get(0)), Double.parseDouble(temp.get(0).get(1))) <= 30){
                  validStoreID = true;
              } else {
	          System.out.println("\tInvalid store ID. Store is not within 30 miles. ");
	      }
	  }

	  //Ask user for product name and validate that it is available in the designated store
	  boolean validProductName = false;
          while (!validProductName){
              System.out.print("\tEnter product name: ");
              productName = in.readLine();
	      String query3 = String.format("SELECT P.productName, P.numberOfUnits, P.pricePerUnit FROM Product P WHERE P.productName = '%s' AND P.storeID = '%s'", productName, storeID);
              temp = esql.executeQueryAndReturnResult(query3);
              if (temp.size() != 0){
                  validProductName = true;
		  if(Double.parseDouble(temp.get(0).get(1)) <= 0){
		      validProductName = false;
		      System.out.println("\tThe store is out of stock on this product. Please choose another product. ");
		  }
              } else {
                  System.out.println("\tInvalid product name. Store does not carry this product. ");
              }
          }

	  //Ask user for number of units of the stated product and validate that the designated store has enough in stock
	  boolean validNumberOfUNits = false;
          while (!validNumberOfUNits){
              System.out.print("\tEnter number of units: ");
              numberOfUnits = in.readLine();
              if (Double.parseDouble(temp.get(0).get(1)) >= Double.parseDouble(numberOfUnits) && Double.parseDouble(numberOfUnits) != 0){
                  validNumberOfUNits = true;
              } else if (Double.parseDouble(numberOfUnits) <= 0){
		  System.out.println("\tPlease enter a number bigger than 0.");
	      } else {
                  System.out.println("\tInvalid number of units. Store does not carry enough in stock. ");
              }
          }

	  //Inserting order information into the orders table
	  String query4 = "SELECT current_timestamp";
	  temp = esql.executeQueryAndReturnResult(query4);
	  String orderTime = temp.get(0).get(0);
	  String query5 = String.format("INSERT INTO Orders (customerID, storeID, productName, unitsOrdered, orderTime) VALUES ('%s', '%s', '%s', '%s', '%s')", userID, storeID, productName, numberOfUnits, orderTime);
	  esql.executeUpdate(query5);

	  //Updating the product table
	  String query6 = String.format("UPDATE Product SET numberOfUnits = numberOfUnits-%s WHERE (storeID = %s) AND (productName = '%s')", numberOfUnits, storeID, productName);
	  esql.executeUpdate(query6);

	  //Show confirmation message to user
	  String orderConfirmationMessage = String.format("\tOrder for %s items of %s has been confirmed. ", numberOfUnits, productName);
	  System.out.println(orderConfirmationMessage);
      }
      catch (Exception e) {
          System.err.println(e.getMessage());
      }
   }

   public static void viewRecentOrders(Retail esql) {
      try{
	  String query1 = String.format("SELECT U.userID FROM Users U WHERE U.name = '%s'", globalName);
          List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
	  String userID = temp.get(0).get(0);
	  String query2 = String.format("\tSELECT O.storeID, S.name, O.productName, O.unitsOrdered, O.orderTime FROM Store S, Orders O WHERE (O.storeID = S.storeID) AND (O.customerID = %s) ORDER BY orderTime DESC LIMIT 5", userID);
          esql.executeQueryAndPrintResult(query2);
      }
      catch (Exception e) {
          System.err.println(e.getMessage());
      }
   }

   public static void updateProduct(Retail esql) {
       try{
       // userType contains a string of a users' type  
       String userType = getUserType(esql);
       if (userType.equals("customer")) {
           System.out.println("You must be a manager or an admin to update products information. ");
           return;
       }
       // If they are type "manager":
       else if (userType.equals("manager")){
	    String query1 = String.format("SELECT U.userID FROM Users U WHERE U.name = '%s'", globalName);
            List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
            String userID = temp.get(0).get(0);
            String query2 = String.format("SELECT S.storeID FROM Store S WHERE S.managerID = %s", userID);
            temp = esql.executeQueryAndReturnResult(query2);
	    boolean validStoreID = false;
	    String storeID = "";
	    while(!validStoreID){
	        System.out.print("\tEnter store ID: ");
		storeID = in.readLine();
		//Validate store ID (Meaning manager does manage that store)
		for (List<String> i : temp) {
                    if (Integer.parseInt(i.get(0)) == Integer.parseInt(storeID)){
                       validStoreID = true;
                    }
                }
		if(!validStoreID){
		     System.out.println("\tInvalid store ID. You do not manage this store. ");
		}
	    }

	    //Validate Product Name  
	    String productName = "";
	    boolean validProductName = false;
            while (!validProductName){
                System.out.print("\tEnter product name: ");
                productName = in.readLine();
                String query3 = String.format("SELECT P.productName, P.numberOfUnits, P.pricePerUnit FROM Product P WHERE P.productName = '%s' AND P.storeID = '%s'", productName, storeID);
                temp = esql.executeQueryAndReturnResult(query3);
                if (temp.size() != 0){
                    validProductName = true;
                } else {
                    System.out.println("\tInvalid product name. Store does not carry this product. ");
                }
            }
	    //Prompt manager with choices
            System.out.print("\t1. Change number of units.\n\t2. Change the price per unit. \n");
	    System.out.print("\tEnter your selection: ");
            String selectionNumber = in.readLine();
            int selectionInteger = Integer.parseInt(selectionNumber);
            if (selectionInteger == 1) {
               System.out.print("\tEnter new amount of units: ");
               String takenewNumUnits = in.readLine();
               int newNumUnits = Integer.parseInt(takenewNumUnits);
	       String query3 = String.format("UPDATE Product SET numberOfUnits = '%s' WHERE (storeID = '%s') AND (productName='%s')", newNumUnits, storeID, productName);
               esql.executeUpdate(query3);
               System.out.println("Product quantity Updated. ");
	       String query4 = "SELECT current_timestamp";
               temp = esql.executeQueryAndReturnResult(query4);
               String updateTime = temp.get(0).get(0);
               String query5 = String.format("INSERT INTO ProductUpdates (managerID, storeID, productName, updatedOn) VALUES ('%s', '%s', '%s', '%s')", userID, storeID, productName, updateTime);
               esql.executeUpdate(query5);
            }
            else if (selectionInteger == 2){
               System.out.print("\tEnter new price per unit for " + productName + ": ");
	       String ppu = in.readLine();
               int pricePer = Integer.parseInt(ppu);
               String query6 = String.format("UPDATE Product SET pricePerUnit = '%s' WHERE (storeID = '%s') AND (productName='%s')", pricePer, storeID, productName);
               esql.executeUpdate(query6);
               System.out.println("Product price updated. ");
	       String query7 = "SELECT current_timestamp";
               temp = esql.executeQueryAndReturnResult(query7);
               String updateTime = temp.get(0).get(0);
               String query8 = String.format("INSERT INTO ProductUpdates (managerID, storeID, productName, updatedOn) VALUES ('%s', '%s', '%s', '%s')", userID, storeID, productName, updateTime);
               esql.executeUpdate(query8);
            }
         }
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void viewRecentUpdates(Retail esql) {
       try{
          String query1 = String.format("SELECT U.userID FROM Users U WHERE U.name = '%s'", globalName);
          List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
          String userID = temp.get(0).get(0);
          String query2 = String.format("\tSELECT U.updateNumber, U.storeID, U.productName, U.updatedOn FROM ProductUpdates U WHERE (U.managerID = %s) ORDER BY updatedOn DESC LIMIT 5", userID);
          esql.executeQueryAndPrintResult(query2);
      }
      catch (Exception e) {
          System.err.println(e.getMessage());
      }
   }

   public static void viewPopularProducts(Retail esql) {
	try {
	String userType = getUserType(esql);
	if (userType.equals("customer")) {
		System.out.println("You do not have access to this feature!");
		return;
	}
	else if (userType.equals("manager")){
		String managerID = "";
		String query1 = String.format("SELECT U.userID FROM Users U WHERE U.name = '%s'", globalName);
                List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
                managerID = temp.get(0).get(0);
		String query2 = String.format("SELECT O.productName, COUNT(*) AS numberOfOrders FROM Orders O, Store S WHERE O.storeID = S.storeID AND S.managerID = %s GROUP BY (O.productName) ORDER BY COUNT(*) DESC LIMIT 5", managerID);
		esql.executeQueryAndPrintResult(query2);
	}
	}
	catch (Exception e) {
          System.err.println(e.getMessage());
      }
	
   }
   public static void viewPopularCustomers(Retail esql) {
	try {
        String userType = getUserType(esql);
        if (userType.equals("customer")) {
                System.out.println("You do not have access to this feature!");
                return;
        }
        else if (userType.equals("manager")) {
                String managerID = "";
                String query1 = String.format("SELECT U.userID FROM Users U WHERE U.name = '%s'", globalName);
                List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
                managerID = temp.get(0).get(0);
                String query2 = String.format("SELECT U.name, COUNT(*) AS numberOfOrders FROM Orders O, Store S, Users U WHERE O.storeID = S.storeID AND S.managerID = %s AND U.userID = O.customerID GROUP BY (U.userID) ORDER BY COUNT(*) DESC LIMIT 5", managerID);
                esql.executeQueryAndPrintResult(query2);
        }
        }
        catch (Exception e) {
          System.err.println(e.getMessage());
      }
}
   public static void placeProductSupplyRequests(Retail esql) {

	try {
		String userType = getUserType(esql);
		if (userType.equals("customer")) {
                    System.out.println("You do not have access to this feature!");
                    return;
        	}
	else if (userType.equals("manager")) {
	  String storeID = "";
	  String productName = "";
	  String numberOfUnits = "";
	  String warehouseID = "";
	  String query1 = String.format("SELECT U.userID FROM Users U WHERE U.name = '%s'", globalName);
          List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
	  int userID = Integer.parseInt(temp.get(0).get(0));
	  boolean validStoreID = false;
	  while (!validStoreID){
	      System.out.print("\tEnter store ID: ");
	      storeID = in.readLine();
              String query2 = String.format("SELECT S.storeID, S.managerID FROM Store S WHERE S.storeID = '%s'", storeID);
	      temp = esql.executeQueryAndReturnResult(query2);
              for (List<String> i : temp) {
                    if (Integer.parseInt(i.get(0)) == Integer.parseInt(storeID)){
                       validStoreID = true;
                    }
	      }
	      if(!validStoreID){
		     System.out.println("\tInvalid store ID. You do not manage this store. ");
	      }
	    }
	  boolean validProductName = false;
               while (!validProductName){
                  System.out.print("\tEnter product name: ");
                  productName = in.readLine();
                  String query3 = String.format("SELECT P.productName, P.numberOfUnits, P.pricePerUnit FROM Product P WHERE P.productName = '%s' AND P.storeID = '%s'", productName, storeID);
                  temp = esql.executeQueryAndReturnResult(query3);
                  if (temp.size() != 0){
                     validProductName = true;
                     productName = temp.get(0).get(0);
                  }
                  else {
                     System.out.println("\tInvalid product name. Store does not carry this product. ");
                  }
               }
	  System.out.print("\tEnter units: ");
	  String unitsRequested = in.readLine();
	  boolean validWarehouse = false;
               while (!validWarehouse){
                  System.out.print("\tEnter warehouse ID: ");
                  warehouseID = in.readLine();
                  String query3 = String.format("SELECT * FROM Warehouse W WHERE W.WarehouseID = '%s'", warehouseID);
                  temp = esql.executeQueryAndReturnResult(query3);
                  if (temp.size() != 0){
                     validWarehouse = true;
                     warehouseID = temp.get(0).get(0);
                  }
                  else {
                     System.out.println("\tInvalid warehouse ID.");
                  }
               }
	   String query4 = String.format("INSERT INTO ProductSupplyRequests (managerID, warehouseID, storeID, productName, unitsRequested) VALUES ('%s','%s','%s','%s','%s')", userID, warehouseID, storeID, productName, unitsRequested); 
	   esql.executeUpdate(query4);
	   System.out.println("\tRequest placed.\n");
	   String query5 = String.format("UPDATE Product SET numberOfUnits = numberOfUnits+%s WHERE (storeID = %s) AND (productName = '%s')", unitsRequested, storeID, productName);
	   esql.executeUpdate(query5); 
	}
	}		
	catch (Exception e) {
          System.err.println(e.getMessage());
      	}
}

	public static void checkManagerOrderInfo(Retail esql) {
		try {
		String userType = getUserType(esql);
        	if (userType.equals("customer")) {
                	System.out.println("You do not have access to this feature!");
                	return;
        	}
        	else if (userType.equals("manager")){
                	String managerID = "";
                	String query1 = String.format("SELECT U.userID FROM Users U WHERE U.name = '%s'", globalName);
                	List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
                	managerID = temp.get(0).get(0);
                	String query2 = String.format("SELECT DISTINCT O.orderNumber, U.name, O.storeID, O.productName, O.orderTime FROM Orders O, Users U, Store S WHERE S.managerID = %s AND O.customerID = U.userID AND S.storeID = O.storeID", managerID);
                	esql.executeQueryAndPrintResult(query2);
        }
		}
		catch (Exception e) {
          		System.err.println(e.getMessage());
        	}
	}

	public static void adminUpdate(Retail esql) {
		try {
         String userType = getUserType(esql);
         if (userType.equals("customer") || userType.equals("manager")) {
            System.out.println("You do not have access to this feature!");
            return;
         }
         else if (userType.equals("admin")) {
            String sID = "";
            String productName = "";
            String numberOfUnits = "";
            String userID = "1";
            
            System.out.print("\tPress 1 for product update\n\tPress 2 for user update\n\tPress 3 to view users\n\tPress 4 to add a user\n\tPress 5 to delete a user\n\tPress 6 to add a product\n\tPress 7 to delete a product: ");
            String choiceNumber = in.readLine();
            int choice = Integer.parseInt(choiceNumber);
            if (choice == 1) {	
	       boolean validStore = false;
		while(!validStore){
			System.out.print("\tEnter store ID: ");
               		sID = in.readLine();
			String query = String.format("SELECT * FROM Store S WHERE S.storeID = '%s'", sID);
                  List<List<String>> temp = esql.executeQueryAndReturnResult(query);
                  if (temp.size() != 0){
                     validStore = true;
                     productName = temp.get(0).get(0);
                  }
                  else {
                     System.out.println("\tInvalid Store ID. ");
                  }
		}
               int storeID = Integer.parseInt(sID);
               boolean validProductName = false;
               while (!validProductName){
                  System.out.print("\tEnter product name: ");
                  productName = in.readLine();
                  String query3 = String.format("SELECT P.productName, P.numberOfUnits, P.pricePerUnit FROM Product P WHERE P.productName = '%s' AND P.storeID = '%s'", productName, storeID);
                  List<List<String>> temp = esql.executeQueryAndReturnResult(query3);
                  if (temp.size() != 0){
                     validProductName = true;
                     productName = temp.get(0).get(0);
                  } 
                  else {
                     System.out.println("\tInvalid product name. Store does not carry this product. ");
                  }
               }
               System.out.print("\t1. Change number of units.\n\t2. Change the price per unit.\n");
               System.out.print("\tEnter your selection: ");
               String selectionNumber = in.readLine();
               int selectionInteger = Integer.parseInt(selectionNumber);
               if (selectionInteger == 1) {
                  System.out.print("\tEnter new amount of units: ");
                  String takenewNumUnits = in.readLine();
                  int newNumUnits = Integer.parseInt(takenewNumUnits);
                  String query3 = String.format("UPDATE Product SET numberOfUnits = '%s' WHERE (storeID = '%s') AND (productName='%s')", newNumUnits, sID, productName);
                  esql.executeUpdate(query3);
                  System.out.println("Product quantity Updated. ");
                  String query4 = "SELECT current_timestamp";
                  List<List<String>> temp = esql.executeQueryAndReturnResult(query4);
                  String updateTime = temp.get(0).get(0);
                  String query5 = String.format("INSERT INTO ProductUpdates (managerID, storeID, productName, updatedOn) VALUES ('%s', '%s', '%s', '%s')", userID, sID, productName, updateTime);
                  esql.executeUpdate(query5);
               }
               else if (selectionInteger == 2) {
		  String tempout = String.format("\tEnter new price per unit: ");
                  System.out.print(tempout);
                  String ppu = in.readLine();
                  int pricePer = Integer.parseInt(ppu);
                  String query6 = String.format("UPDATE Product SET pricePerUnit = '%s' WHERE (storeID = '%s') AND (productName='%s')", pricePer, sID, productName);
                  esql.executeUpdate(query6);
                  System.out.println("Product price updated. ");
                  String query7 = "SELECT current_timestamp";
                  List<List<String>> temp  = esql.executeQueryAndReturnResult(query7);
                  String updateTime = temp.get(0).get(0);
                  String query8 = String.format("INSERT INTO ProductUpdates (managerID, storeID, productName, updatedOn) VALUES ('%s', '%s', '%s', '%s')", userID, sID, productName, updateTime);
                  esql.executeUpdate(query8);
               }
               // We didn't touch this because product name is part of the primary key.

		/*else if (selectionInteger == 3) {
                  System.out.print("\tEnter new product name for " + productName + ": ");
                  String newName = in.readLine();
                  String query6 = String.format("UPDATE Product SET productName = '%s' WHERE (storeID = '%s') AND (productName='%s')", newName, sID, productName);
                  esql.executeUpdate(query6);
                  System.out.println("Product name updated. ");
                  String query7 = "SELECT current_timestamp";
                  List<List<String>> temp  = esql.executeQueryAndReturnResult(query7);
                  String updateTime = temp.get(0).get(0);
                  String query8 = String.format("INSERT INTO ProductUpdates (managerID, storeID, productName, updatedOn) VALUES ('%s', '%s', '%s', '%s')", userID, sID, productName, updateTime);
                  esql.executeUpdate(query8);	
                  }*/
            }
         else if (choice == 2) {
 		  System.out.print("\tEnter userID: ");
                  String uID = in.readLine();
            boolean validUserID= false;
               while (!validUserID){
                  String query3 = String.format("SELECT U.userID FROM Users U WHERE U.userID = %s", uID);
                  List<List<String>> temp = esql.executeQueryAndReturnResult(query3);
                  if (temp.size() != 0){
                     validUserID = true;
                  } else {
                     System.out.println("\tInvalid userID.");
		     System.out.print("\tEnter userID: ");
                     uID = in.readLine();
                  }
               }
               System.out.print("\t1. Change user name.\n\t2. Change user password. \n\t3. Change user location.\n\t4. Change user type.\n");
               System.out.print("\tEnter your selection: ");
               String selectionNumber = in.readLine();
               int selectionInteger = Integer.parseInt(selectionNumber);
         if (selectionInteger == 1) {
            System.out.print("\tEnter new user name for user with userID " + uID + ": ");
            String newName = in.readLine();
            String query6 = String.format("UPDATE Users SET name = '%s' WHERE (userID = '%s')", newName, uID);
            esql.executeUpdate(query6);
            System.out.println("User name updated. ");
         }
         else if (selectionInteger == 2) {
            System.out.print("\tEnter new password for user with userID " + uID + ": ");
            String newPass = in.readLine();
            String query6 = String.format("UPDATE Users SET password = '%s' WHERE (userID = '%s')", newPass, uID);
            esql.executeUpdate(query6);
            System.out.println("Password updated. ");	
            }
	 else if (selectionInteger == 3) {
		System.out.print("\tEnter new latitude: ");		
		String newLat = in.readLine();
		System.out.print("\tEnter new longitude: ");
                String newLong = in.readLine();
		String query7 = String.format("UPDATE Users SET longitude = '%s', latitude = '%s' WHERE (userID = '%s')", newLong, newLat, uID);
		esql.executeUpdate(query7);
		System.out.println("Location updated.");
	   }
	 else if (selectionInteger == 4) {
                System.out.print("\tEnter new user type: ");
                String newType = in.readLine();
		newType = newType.trim();
		boolean validType = false;
		if (newType.equals("customer") || newType.equals("manager") || newType.equals("admin")) {
			validType = true;
		}
		while (!validType) {
			System.out.print("\tInvalid user type. Choose either manager, customer, or admin: ");
			newType = in.readLine();
			newType = newType.trim();
			if (newType.equals("customer") || newType.equals("manager") || newType.equals("admin")) {
                                validType = true;
                        }
		}
		String query7 = String.format("UPDATE Users SET type = '%s' WHERE (userID = '%s')", newType, uID);
		esql.executeUpdate(query7);
		System.out.print("\tUser type updated\n");
	 }
         } else if (choice == 3){
		System.out.print("\tEnter user name: ");
                String uName = in.readLine();
            	boolean validUserID= false;
               	while (!validUserID){
                	String query3 = String.format("SELECT U.name FROM Users U WHERE U.name = '%s'", uName);
                  	List<List<String>> temp = esql.executeQueryAndReturnResult(query3);
                  	if (temp.size() != 0){
                     		validUserID = true;
                  	} else {
                     		System.out.println("\tInvalid user name.");
				System.out.print("\tEnter user name: ");
                     		uName = in.readLine();
                  	}
               	}
		String query4 = String.format("SELECT * FROM Users U WHERE U.name = '%s'", uName);
		esql.executeQueryAndPrintResult(query4);
          } else if (choice == 4) {
                System.out.print("\tEnter user name: ");
                String name = in.readLine();
                System.out.print("\tEnter new user pass: ");
                String pass = in.readLine();
                System.out.print("\tEnter new user latitude: ");
                String userlat = in.readLine();
                System.out.print("\tEnter new user longitude: ");
                String userlong = in.readLine();
                System.out.print("\tEnter new user type: ");
                String usertype = in.readLine();
                String query1 = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, pass, userlat, userlong, usertype);
                esql.executeUpdate(query1);
        } else if (choice == 5) {
                System.out.print("\tEnter user name: ");
                String uName = in.readLine();
                boolean validName = false;
                while (!validName){
                        String query3 = String.format("SELECT U.name FROM Users U WHERE U.name = '%s'", uName);
                        List<List<String>> temp = esql.executeQueryAndReturnResult(query3);
                        if (temp.size() != 0){
                                validName = true;
                        } else {
                                System.out.println("\tInvalid user name.");
                                System.out.print("\tEnter user name: ");
                                uName = in.readLine();
                        }
                }
		String query1 = String.format("SELECT * FROM USERS U WHERE U.name = '%s'", uName);
		esql.executeQueryAndPrintResult(query1);
		System.out.print("\tEnter user ID: ");
                String uDelete = in.readLine();
                String query2 = String.format("DELETE FROM USERS U WHERE U.userID = '%s'", uDelete);
		esql.executeUpdate(query2);
        } else if (choice == 6) {
                System.out.print("\tEnter product name: ");
                String name = in.readLine();
                System.out.print("\tEnter product store ID: ");
                String pSID = in.readLine();
                System.out.print("\tEnter price per unit of product: ");
                String pnu = in.readLine();
                System.out.print("\tEnter number of units of product: ");
                String pppu = in.readLine();
                String query1 = String.format("INSERT INTO Product (productName, storeID, numberOfUnits, pricePerUnit) VALUES ('%s','%s', '%s', '%s')", name, pSID, pnu, pppu);
                esql.executeUpdate(query1);
        } else if (choice == 7) {
		System.out.print("\tEnter store ID: ");
                String pSID = in.readLine();
                boolean validSID = false;
                while (!validSID){
                        String query1 = String.format("SELECT S.storeID FROM Store S WHERE S.storeID = '%s'", pSID);
                        List<List<String>> temp = esql.executeQueryAndReturnResult(query1);
                        if (temp.size() != 0){
                                validSID = true;
                        } else {
                                System.out.println("\tInvalid store ID.");
                                System.out.print("\tEnter store ID: ");
                                pSID = in.readLine();
                        }
                }

                System.out.print("\tEnter product name: ");
                String pName = in.readLine();
                boolean validName = false;
                while (!validName){
                        String query2 = String.format("SELECT P.productName FROM Product P WHERE P.productName = '%s'", pName);
                        List<List<String>> temp = esql.executeQueryAndReturnResult(query2);
                        if (temp.size() != 0){
                                validName = true;
                        } else {
                                System.out.println("\tInvalid product name.");
                                System.out.print("\tEnter product name: ");
                                pName = in.readLine();
                        }
                }
                String query3 = String.format("DELETE FROM Product P WHERE P.productName = '%s' AND P.storeID = '%s'", pName, pSID);
                esql.executeUpdate(query3);
        }
       }
      }
		catch (Exception e) {
         System.err.println(e.getMessage());
      }	
}

}//end Retail

