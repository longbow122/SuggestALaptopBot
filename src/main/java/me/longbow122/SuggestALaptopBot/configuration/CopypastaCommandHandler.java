package me.longbow122.SuggestALaptopBot.configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

/**
 * This class is used to handle the files used to handle slash command copypastas.
 * Any file handling logic specific to the "commands.json" file is to be stored here.
 */
public class CopypastaCommandHandler extends ConfigHandler {

  /**
   * A constructor for the CopypastaCommandHandler class.
   * This constructor is empty, as we know where the data for slash copypastas is stored.
   * We can hardcode the file name, and have this handle everything to do with commands.
   */
  public CopypastaCommandHandler() {
    super("commands.json");
  }

  /**
   * Basic utility method to get all commands within the commands.json file as a {@link JsonArray}, containing several {@link JsonObject}s,
   * holding the information used to create a slash command copy pasta.
   * @return {@link JsonArray} containing several {@link JsonObject}s representing every copypasta command that can be used within the bot.
   */
  private JsonArray getCommands() {
    JsonElement commandsTree;
    try {
      commandsTree = JsonParser.parseReader(new FileReader(super.file)).getAsJsonObject().get(super.file.getName().replace(".json", ""));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
    if(commandsTree == null || !commandsTree.isJsonArray()) {
      System.out.println("No commands found in commands file.");
      return null;
    }
    return commandsTree.getAsJsonArray();
  }

  /**
   * Method used to retrieve the name and the description of all copypasta commands within the bot at the time.
   * This can then be used within the main class and method to register the commands with Discord, letting them be run.
   * Implementing the messages to be sent by the bot is done elsewhere within the main class, using the information from this hashmap.
   *
   * @return {@link HashMap} representing the set of commands and their respective descriptions. Returns null if no commands are found.
   */
  public HashMap<String, String> returnAllCommandRegisterInformation() {
    JsonArray commandsArray = getCommands();
    HashMap<String, String> commandInfo = new HashMap<>();
    for (JsonElement x : commandsArray) {
      if (!(x.isJsonObject())) {
        System.out.println("Invalid command found in config file.");
        continue;
      }
      JsonObject potentialCommand = x.getAsJsonObject();
      if (!(potentialCommand.has("message"))) {
        System.out.println("Doesn't have a message");
        continue;
      }
      String commandName = potentialCommand.get("name").toString().replace("\"", "");
      String description = potentialCommand.get("description").getAsString();
      commandInfo.put(commandName, description);
    }
    return commandInfo;
  }

  /**
   * Method used to retrieve the message for a specific command.
   * @param command {@link String} representing the name of the command to retrieve the message for.
   *                              This method does not assume that the command exists.
   * @return {@link String} representing the copypasta message to be printed.
   */
  public String getMessageForCommand(String command) {
    JsonArray commandsArray = getCommands();
    for (JsonElement x : commandsArray) {
      if (!(x.isJsonObject())) {
        System.out.println("Invalid command found in config file.");
        continue;
      }
      JsonObject potentialCommand = x.getAsJsonObject();
      if (!(potentialCommand.has("message"))) {
        System.out.println("Doesn't have a message");
        continue;
      }
      String commandName = potentialCommand.get("name").toString().replace("\"", "");
      if (commandName.equals(command)) {
        return potentialCommand.get("message").getAsString();
      }
    }
    return null;
  }
}
