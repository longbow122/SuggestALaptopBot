package me.longbow122.SuggestALaptopBot;


import me.longbow122.SuggestALaptopBot.configuration.ConfigHandler;
import me.longbow122.SuggestALaptopBot.db.CopypastaDB;
import me.longbow122.SuggestALaptopBot.events.SlashCopypastaHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.HashMap;

public class Main {

  static ConfigHandler configFile = new ConfigHandler("config.json");

  //FIXME THIS FEELS LIKE STATIC ABUSE? LOOK INTO IT
  // Would using the singleton design pattern here to avoid making this static really be the best idea?
  // Or is it better to just make it static, since we're only working with one instance of this at any one time?
  private static MessageChannel logChannel;



  public static void main(String[] args) throws InterruptedException {
    // Ensure that the database is connected
    new CopypastaDB().createTable();

    System.out.println("Starting bot...");
    String token = configFile.getString("token").replace("\"", "");
    JDA jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
      .addEventListeners(new SlashCopypastaHandler())
      .build();
    // TODO THE OLD HASHMAP HAS BEEN PHASED OUT IN FAVOUR OF READING FROM THE DB INSTEAD. REMOVE RELEVANT CODE ONCE DB IS WORKING.
    //HashMap<String, String> commandInfo = new CopypastaConfigHandler().returnAllCommandRegisterInformation();
    HashMap<String, String> commandInfo = new CopypastaDB().getAllCopypastaInformation();
    CommandListUpdateAction commands = jda.updateCommands();
    // Add all copypastas.
    for (String commandName : commandInfo.keySet()) {
      commands.addCommands(Commands.slash(commandName, commandInfo.get(commandName))
        .setGuildOnly(true));
    }
    //TODO MAYBE TOO MUCH WHITESPACE HERE? IDK. I LIKE SEPARATING THE COMMANDS BY NEWLINES.
    commands.addCommands(Commands.slash("copypasta", "Modify the copypastas available.")

      .addSubcommands(new SubcommandData("add", "Add a copypasta to the list of slash commands")
        .addOption(OptionType.STRING, "name", "The unique name of the copypasta command to be added. REQUIRED.", true)
        .addOption(OptionType.STRING, "description", "The description of the copypasta command to be added. REQUIRED.", true)
        .addOption(OptionType.STRING, "message", "The actual message behind the copypasta command. REQUIRED.", true))

      .addSubcommands(new SubcommandData("remove", "Remove a copypasta from the list of slash commands")
        .addOptions(new OptionData(OptionType.STRING, "name", "The name of the copypasta command to be removed. REQUIRED.", true, true)))
      //TODO NEED TO IMPLEMENT AUTOCOMPLETE LOGIC USING THE COMMAND LIST AND THE AUTOCOMPLETE EVENT

        .addSubcommands(new SubcommandData("update", "Change the name, description or message of an existing copypasta")
          .addOption(OptionType.STRING, "name", "The current name of the copypasta command to be updated. REQUIRED.", true)
          .addOption(OptionType.STRING, "field", "The field to update. (Name, Description, Message). REQUIRED.", true)
          .addOption(OptionType.STRING, "value", "The new value of the field. REQUIRED.", true))
      //TODO NEED TO IMPLEMENT AUTOCOMPLETE LOGIC USING THE COMMAND LIST AND THE AUTOCOMPLETE EVENT FOR THE NAMES OF THE COMMANDS FOR THE FIRST OPTION
      //TODO NEED TO IMPLEMENT AUTOCOMPLETE LOGIC USING THE THREE VALUES (NAME, DESCRIPTION, MESSAGE) FOR THE SECOND OPTION. SHOULD BE MUCH EASIER TO IMPLEMENT THAN THE OTHERS.

      .setGuildOnly(true));

    commands.queue();
    jda.awaitReady();
    System.out.println(configFile.getLong("logChannel"));
    logChannel = jda.getTextChannelById(configFile.getLong("logChannel"));
    log("Bot started.");
    }

  /**
   * A basic method to log a message to the designated log channel.
   * This should probably be replaced with a proper logging framework of some sort, once I look into it.
   * The logging I want to do is fairly simplistic, and I struggle to see the need for a file-based audit log in this case.
   * @param message {@link String} to send to the logs channel. This should be a message to be logged.
   */
  public static void log(String message) {
      logChannel.sendMessage(message).queue();
    }
  }