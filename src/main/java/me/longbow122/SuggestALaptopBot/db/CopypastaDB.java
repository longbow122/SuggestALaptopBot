package me.longbow122.SuggestALaptopBot.db;

import java.sql.*;
import java.util.HashMap;

/**
 * This class will abstract away database operations, keeping method calls simpler.
 * This will handle the more complicated logic alongside its superclass to perform needed database operations,
 * which are then called on by their respective data classes to perform needed logic.
 */
public class CopypastaDB extends Database {

  /**
   * Empty constructor for the CopypastaDB class. Used to call methods within this class as needed.
   */
  public CopypastaDB() {}


  /*
  Making use of the template method design pattern here, to allow for extensibility when extending features within the bot.
  Hoping to use this to avoid any duplication of code and implementation where needed.

  Move the class-specific methods here, and make use of them in the superclass as an abstract method. Hope that
  class naming conventions will make the use of the methods clear.
   */
  @Override
  protected String getTableQuery() {
    // Had to use 10k char length for name and description, since it didn't feel right to use CLOB for those two.
    // CLOB is used for message, since messages could be very long at their worst case.
    // ? Also worth noting that the name of the command is the primary key. This should be unique and will be enforced elsewhere.
    return "CREATE TABLE IF NOT EXISTS Copypasta(name CHARACTER VARYING(10000) PRIMARY KEY, description CHARACTER VARYING(10000), message CHARACTER LARGE OBJECT);";
    //TODO UNSURE IF THE ABOVE STATEMENT IS RIGHT? TEST AND VERIFY
  }

  /**
   * Basic QOL method to check if a record exists within the table. <br>
   * It is worth noting that this simply checks for the precense of some primary key, which ideally indicates the existence
   * of the entire record within the database.
   * @param name {@link String} representing the name of the copypasta command to search for.
   * @return {@link Boolean} representing whether the copypasta command exists within the database.
   */
  public boolean doesCopypastaExist(String name) {
    //TODO THIS METHOD MOST DEFINITELY CAN BE MADE GENERIC, WORK ON THIS ONCE DEALS ARE IMPLEMENTED.
    Connection c = connect();
    try {
      PreparedStatement s = c.prepareStatement("SELECT 1 FROM Copypasta WHERE name=?");
      s.setString(1, name);
      ResultSet rs = s.executeQuery();
      if (rs.next()) {
        rs.close();
        s.close();
        disconnect();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    return false; // This should ideally not run. There will either be a result, or not be one.
  }

  /* TODO
  ! CRUD operations for this class are to go down here. I don't like the way these have been implemented, but can't
  ! see any way to improve on the design atm.

  ! I also like the idea of keeping the connect and disconnect methods inside the database class and restricting it's
  ! usage, but I don't see any good ways of doing this atm.
   */

  /**
   * Basic method that will add a copypasta message to the database. Takes in the values, pass them into the statement
   * as string literals and then execute the statement. <br>
   * It is also worth noting that the name of the command is the primary key and as such, this will need to be enforced
   * before calling this method.
   * @param name {@link String} representing the name of the command
   * @param description {@link String} representing the description of the command.
   * @param message {@link String} representing the message to be sent when the command is called.
   * @return {@link Boolean} representing whether or not the operation was successful.
   */
  public boolean addCopypasta(String name, String description, String message) {
    String pastaStatement = "INSERT INTO Copypasta(name, description, message) VALUES(?,?,?)";
    Connection c = connect();
    try {
      PreparedStatement s = c.prepareStatement(pastaStatement);
      s.setString(1, name);
      s.setString(2, description);
      s.setString(3, message);
      s.execute();
      s.close();
      disconnect();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * A method that will remove a copypasta message from the database. Takes in the name of the command to be removed,
   * since this serves as a primary key in this context.
   * @param name {@link String} representing the name of the command to be removed.
   * @return {@link Boolean} representing whether or not the operation was successful.
   */
  public boolean removeCopypasta(String name) {
    String s = "DELETE FROM Copypasta WHERE name=?";
    Connection c = connect();
    try {
      PreparedStatement statement = c.prepareStatement(s);
      statement.setString(1, name);
      statement.execute();
      statement.close();
      disconnect();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /* TODO
  ! These CRUD operations most definitely cannot be generalised and moved up to the sub-class. I feel like a better way
  ! of executing such statements is still possible. I don't like how this looks right now. Connect and disconnect methods should
  ! remain in their superclass...
   */

  /**
   * Method to retrieve all the needed information for registering the commands themselves with Discord's API upon startup. <br>
   * This will put both the name and description of the command into a {@link HashMap} for use as needed.
   * @return {@link HashMap} representing every name and description for slash copypasta commands that could be used.
   */
  public HashMap<String, String> getAllCopypastaInformation() {
    String s = "SELECT NAME, DESCRIPTION FROM Copypasta";
    Connection c = connect();
    try {
      Statement st = c.createStatement();
      ResultSet rs = st.executeQuery(s);
      HashMap<String, String> found = new HashMap<>();
      while(rs.next()) {
        found.put(rs.getString(1), rs.getString(2));
      }
      rs.close();
      st.close();
      disconnect();
      return found;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * This method is an easy abstraction behind the logic of retrieving the message to be sent by a command. Simple enough.
   * @param commandName {@link String} representing the name of the command, for which a message should exist.
   * @return {@link String} representing the description of the command, provided it exists. Null otherwise.
   */
  public String getMessage(String commandName) {
    String s = "SELECT MESSAGE FROM Copypasta WHERE name=?";
    Connection c = connect();
    try {
      PreparedStatement statement = c.prepareStatement(s);
      statement.setString(1, commandName);
      ResultSet rs = statement.executeQuery();
      String message;
      if (rs.next()) {
        message = rs.getString(1);
      } else { message = null; }
      if (message == null) {
        return null;
      }
      rs.close();
      statement.close();
      disconnect();
      return message;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
