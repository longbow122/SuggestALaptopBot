package me.longbow122.SuggestALaptopBot.configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.longbow122.SuggestALaptopBot.Main;

import java.io.*;
import java.net.URISyntaxException;

/**
 * This class is used to handle the files used within the bot. Any file handling logic is to be stored here.
 */
public class ConfigHandler {

  protected File file;

  // TODO BREAK UP CONFIG HANDLERS INTO INDIVIDUAL COMMAND AND MESSAGES FILES. HAVE THIS BE THE GENERIC, AND LET THE COMMANDS AND MESSAGES FILES HAVE THEIR OWN HANDLERS.
  public ConfigHandler(String fileName) {
    this.file = getFile(fileName);
  }

  /**
   * This method is used to read the file and retrieve the value of any keys, returning a {@link JsonObject}.
   * Other methods within this class are used to wrap this method, handling casting logic to allow for the retrieval of certain types.
   *
   * @param key - {@link String} representing the key to be read from the file.
   * @return {@link String} representing the value of the key, or null if the key doesn't exist.
   */
  private JsonElement getJsonElement(String key) {
    JsonElement tree;
    System.out.println(file.getName());
    try {
      // Make this line universal to all three config files by using the name of their file as the main tree for values.
      tree = JsonParser.parseReader(new FileReader(file)).getAsJsonObject().get(file.getName().replace(".json", ""));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
    if (tree == null || !tree.isJsonArray()) return null;
    JsonArray configArray = tree.getAsJsonArray();
    System.out.println(configArray);
    for (JsonElement x : configArray) {
      if (!(x.isJsonObject())) continue;
      JsonObject potentialString = x.getAsJsonObject();
      System.out.println(potentialString);
      if (!(potentialString.has(key))) continue;
      return potentialString.get(key);
    }
    return null;
  }

  /**
   * This method wraps the {@link #getJsonElement(String)} method, returning a {@link Long} value where applicable.
   *
   * @param key - {@link String} representing the key to be read from the file.
   * @return {@link String} representing the value of the key, or -1 if the key doesn't exist.
   */
  public long getLong(String key) {
    JsonElement element = getJsonElement(key);
    if (element == null) return -1; // Ideally longs will be used for role IDs and as such, -1 can be reserved for null.
    System.out.println(element.getAsLong());
    return element.getAsLong();
  }

  /**
   * This method wraps the {@link #getJsonElement(String)} method, returning a {@link String} value where applicable.
   *
   * @param key {@link String} representing the key to be read from the file.
   * @return {@link String} representing the value of the key, or "" if the key doesn't exist.
   */
  public String getString(String key) {
    JsonElement element = getJsonElement(key);
    if (element == null) return null; // Return null if the element is null. Safety checks will need to be done elsewhere
    return element.toString();
  }

  /**
   * This method is used to write the file into SuggestALaptopBot's directory.
   * If file exists in the following path, then there's no need to do anything.
   * If it doesn't exist, create it.
   * Handles {@link javax.print.URIException} catching due to the call made at #createNewFile()
   *
   * @param fileName The name of the file to be created/read.
   * @return {@link File} object which should be a valid configuration file.
   */
  public static File getFile(String fileName) {
    String filePath;
    try {
      filePath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
    File file = new File(filePath + "/" + fileName);
    if (file.exists()) {
      return file;
    } //Self-explanatory code. Not sure how well this would work
    InputStream fileInput = Main.class.getResourceAsStream("/" + fileName); //This should get the contents of the file
    try {
      file.createNewFile();
      FileOutputStream fileOutput = new FileOutputStream(file);
      int i;
      byte[] bytes = new byte[1024];
      while ((i = fileInput.read(bytes)) != -1 && fileInput != null) {
        fileOutput.write(bytes, 0, i);
      }
      System.out.println("System should have written the file contents to the new file!");
      return file;
    } catch (IOException e) {
      System.out.println("Please take a look at the following stack trace!");
      e.printStackTrace();
      return null;
    }
  }
}