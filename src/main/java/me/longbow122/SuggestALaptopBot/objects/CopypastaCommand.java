package me.longbow122.SuggestALaptopBot.objects;

/**
 * This class is used to handle the internal logic for CopypastaCommands, and is used to abstract away the
 * reading of data, the writing of data, and the running of the slash commands.
 * @author longbow122
 */
public class CopypastaCommand {

  /**
   * The name of the command itself. This is used by Discord to call the slash command itself.
   * The format for this when calling the command on Discord would be /%command%.
   */
  private String command;
  /**
   * The description of the command, to be used by Discord when trying to run the slash command.
   * It is worth noting that this may be null, or an empty string where a description has not been given.
   */
  private String description;
  /**
   * The message to be sent when the copypasta command is called.
   */
  private String message;

  /**
   * Constructor for the {@link CopypastaCommand} class. Can be used to represent a slash command
   * that is to be run by a user to provide some form of specified message.
   * @param command {@link String} representing the name of the command.
   * @param description {@link String} representing the description of the command.
   * @param message {@link String} representing the message to be sent when the command is called.
   */
  public CopypastaCommand(String command, String description, String message) {
    this.command = command;
    this.description = description;
    this.message = message;
  }

  /**
   * Basic getter method to return the message of the command.
   * @return {@link String} representing the message that is to be sent to users when the command is run.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Basic getter method to return the description of the command.
   * @return {@link String} representing the description of the command.
   */
  public String getDescription() {
    return description;
  }

//  public CopypastaCommand addCopypastaCommand(String name, String description, String message) {
//    //TODO CALL DATABASE OPERATION HERE LETTING YOU CALL ON THIS METHOD
      //DOESN'T ACTUALLY MAKE SENSE TO HAVE THIS METHOD HERE, SINCE WE NEED TO BE ABLE TO USE THE TEMPLATE METHOD
      //TO CALL THIS METHOD PROPERLY
//    //return new CopypastaCommand(name, description, message);)
//  }
}
