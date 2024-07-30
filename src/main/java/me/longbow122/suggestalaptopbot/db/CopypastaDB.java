package me.longbow122.suggestalaptopbot.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class will abstract away database operations, keeping method calls simpler.
 * This will handle the more complicated logic alongside its superclass to perform needed database operations,
 * which are then called on by their respective data classes to perform needed logic.
 *
 * This class handles the database that stores and handles copypasta messages.
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
		// We use 32 character length for discord copypasta names, since that is the limit enforced by Discord.
		// We use 100 character length for copypasta descriptions, since that is the limit enforced by Discord.
		// Messages are set to be 2000 characters long to enforce Discord's character limit for messages.
		// ? Also worth noting that the name of the command is the primary key. This should be unique and will be enforced elsewhere.
		return "CREATE TABLE IF NOT EXISTS Copypasta(name CHARACTER VARYING(32) PRIMARY KEY, description CHARACTER VARYING(100), message CHARACTER VARYING(2000));";
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

  ! These CRUD operations also have a boolean return value indicating success or failure. We should find a way to use this in the event that it is
  ! helpful.
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

	/**
	 * A method that will update a copypasta message within the database. Takes in the name of the command to be updated, along with an
	 * according field and value. The specified field will be updated with the value provided.
	 * @param commandName {@link String} representing the name of the command to be updated.
	 * @param field {@link String} representing the field to be updated.
	 * @param value {@link String} representing the value to be updated.
	 * @return {@link Boolean} representing whether the operation was successful.
	 */
	public boolean updateCopypasta(String commandName, int field, String value) {
		String s = switch (field) {
			case 0 -> "UPDATE Copypasta SET name=? WHERE name=?";
			case 1 -> "UPDATE Copypasta SET description=? WHERE name=?";
			case 2 -> "UPDATE Copypasta SET message=? WHERE name=?";
			// ? We should have it limited elsewhere that this case will never be reached.
			default -> throw new IllegalStateException("Unexpected value: " + field);
		};
		Connection c = connect();
		try {
			PreparedStatement statement = c.prepareStatement(s);
			statement.setString(1, value);
			statement.setString(2, commandName);
			statement.execute();
			statement.close();
			disconnect();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * A method that will select all copypasta messages from the database, that start with the specified string. This is to be used in
	 * autocomplete filtering of copypastas.
	 * @param start {@link String} representing the string to be used to filter the results.
	 * @return {@link List} of {@link String} representing the copypasta names that start with the specified string.
	 */
	public List<String> getAllCopypastaStartsWith(String start) {
		// * We use a separate query here to let H2 handle the optimisation and limit our results to 25.
		String s = "SELECT name FROM Copypasta WHERE name LIKE ? ORDER BY name ASC LIMIT 25";
		Connection c = connect();
		try {
			PreparedStatement statement = c.prepareStatement(s);
			statement.setString(1, start + "%");
			ResultSet rs = statement.executeQuery();
			List<String> names = new ArrayList<>();
			while (rs.next()) {
				names.add(rs.getString("name"));
			}
			rs.close();
			statement.close();
			disconnect();
			return names;
		} catch (SQLException e) {
			e.printStackTrace();
			return Collections.emptyList();
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
			return new HashMap<>();
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
