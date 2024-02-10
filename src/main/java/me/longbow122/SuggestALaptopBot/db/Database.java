package me.longbow122.SuggestALaptopBot.db;

import java.sql.*;

/**
 * A basic utilities class representing the local, embedded database that is to be used by the bot. This will
 * store and manage all data relevant to the bot.

 * This class in particular will be used to handle generic operations that would be used by both commands and deals
 * when handling data.
 */
public abstract class Database {

  Connection conn;

  private Connection getConnection() { return conn; }

  private boolean isConnActive() { return conn != null; }

  private void setConnection() {
    try {
      // Make sure the H2 driver actually exists before moving on.
      Class.forName("org.h2.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return;
    }
    try {
      //TODO MAKE SURE THAT THIS IS TESTED
      conn = DriverManager.getConnection("jdbc:h2:./data.db");
    } catch (SQLException e) {
      e.printStackTrace();
      return;
    }
  }

  /**
   * This method will connect to the database and return the connection object. If the connection object is already
   * active, then that will be returned instead, ensuring that we only connect to the database once. <br>
   * Call this method before performing any database operations.
   * @return {@link Connection} object representing the active connection to the database.
   */
  protected Connection connect() {
    if (isConnActive()) {
      return getConnection();
    }
    setConnection();
    return getConnection();
  }

  /**
   * This method will attempt to disconnect from the database. If the connection object is not active, then this will simply
   * set the current connection object to null, ensuring that we cannot connect to a connection that is not active. <br>
   * Call this method AFTER performing any database operations.
   */
  protected void disconnect() {
    if (isConnActive()) {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
        return;
      }
      conn = null;
    }
  }

  public boolean createTable() {
    Connection c = connect();
    try {
      if (c != null) {
        String makeTable = getTableQuery();
        Statement s = c.createStatement();
        s.execute(makeTable);
        s.close();
        disconnect();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /*
  TODO
  ! Can't currently think of a way to write something generic for CRUD operations. This isn't the end of the world,
  ! but would be a good step towards my goal of being able to write cleaner code :(

  ! Since we're only working with one class for now, everything has been implemented within that subclass.
   */

protected abstract String getTableQuery();
}

