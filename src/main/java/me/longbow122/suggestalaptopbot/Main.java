package me.longbow122.suggestalaptopbot;


import me.longbow122.suggestalaptopbot.configuration.ConfigHandler;
import me.longbow122.suggestalaptopbot.db.CopypastaDB;
import me.longbow122.suggestalaptopbot.events.CopypastaAutocompleteListener;
import me.longbow122.suggestalaptopbot.events.CopypastaModalListener;
import me.longbow122.suggestalaptopbot.events.SlashCopypastaCommandListener;
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
import java.util.Map;

public class Main {

  static ConfigHandler configFile = new ConfigHandler("config.json");

  //FIXME THIS FEELS LIKE STATIC ABUSE? LOOK INTO IT
  // Would using the singleton design pattern here to avoid making this static really be the best idea?
  // Or is it better to just make it static, since we're only working with one instance of this at any one time?
  private static MessageChannel logChannel;

  //FIXME : WE NEED TO REWORK THE STRUCTURE OF THIS TO TAKE SPRING BOOT INTO ACCOUNT SO THAT WE CAN BEGIN EXPOSING RESTful ENDPOINTS TO WORK WITH
  // WE ARE NOT GOING TO HAVE MUCH TIME TO WORK WITH THIS IN BETWEEN APPLICATIONS, SO WE NEED TO PROGRESSIVELY WORK ON THINGS

  //FIXME : WE SHOULD ALSO WORK ON THE LOGGING STUFF TO MAKE IT MORE EFFICIENT AND FLEXIBLE
  // WE ARE CURRENTLY WANTING TO TAKE IN SOME LOGGING FRAMEWORK TO HANDLE THE LOGGING OF THINGS EASIER.

  //FIXME ONCE WE HAVE BROUGHT IN SPRING, WE WILL HAVE CONTROLLER-LEVEL AND SERVICE-LEVEL THINGS TO TEST, WHICH SHOULD LIKELY BE TESTED.

  //FIXME WE MIGHT ALSO BE ABLE TO IMPLEMENT A REPOSITORY LAYER, SO THAT WE CAN WORK WITH SPRING QUERY METHODS, AT LEAST FOR SOME THINGS.
  // SINCE WE ARE USING A LOCAL DB, THAT MIGHT NOT BE THE BEST THING, SINCE A REPOSITORY LAYER MIGHT BE OVER-ENGINEERING THINGS SLIGHTLY. WE CAN SEE HOW IT WORKS.

  //FIXME WORKING WITH A REPOSITORY LAYER IS PROBABLY A BIT TOO MUCH SINCE WE AREN'T SURE IF IT CAN HANDLE WHAT WE NEED IN THE WAY WE INTEND IT TO.
  // WE WOULD ALSO HAVE TO INTRODUCE MORE UNIT TESTING, BUT THAT IS JUST OUTRIGHT GOOD PRACTISE.

  //TODO REMOVE JAVADOC COMMENTS SINCE WE AREN'T ACTUALLY DOCUMENTING MUCH WITH THEM. WE SHOULD SAVE THIS FOR METHODS THAT WE WRITE THAT HAVE A
  // HIGHER COMPLEXITY, AND CAN BE AMBIGUOUS AND UNCLEAR.



  public static void main(String[] args) throws InterruptedException {
    // Ensure that the database is connected
    //TODO FIND A WAY TO GO THROUGH EVERY DB CLASS AND NEATLY find a way to call these methods
    new CopypastaDB().createTable();

    System.out.println("Starting bot...");
    String token = configFile.getString("token").replace("\"", "");
    JDA jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
        .addEventListeners(new SlashCopypastaCommandListener())
        .addEventListeners(new CopypastaModalListener())
        .addEventListeners(new CopypastaAutocompleteListener())
        .build();
    HashMap<String, String> commandInfo = new CopypastaDB().getAllCopypastaInformation();
    CommandListUpdateAction commands = jda.updateCommands();
    // Add all copypastas.
    for (Map.Entry<String, String> entry : commandInfo.entrySet()) {
	    commands.addCommands(Commands.slash(entry.getKey(), entry.getValue())
		    .setGuildOnly(true));
    }

    commands.addCommands(Commands.slash("copypasta", "Modify the copypastas available.")

      .addSubcommands(new SubcommandData("add", "Add a copypasta to the list of slash commands"))

      .addSubcommands(new SubcommandData("remove", "Remove a copypasta from the list of slash commands")
        .addOptions(new OptionData(OptionType.STRING, "name", "The name of the copypasta command to be removed. REQUIRED.", true, true)))

        .addSubcommands(new SubcommandData("update", "Change the name, description or message of an existing copypasta")
          .addOption(OptionType.STRING, "name", "The current name of the copypasta command to be updated. REQUIRED.", true, true)
          .addOption(OptionType.STRING, "field", "The field to update. (Name, Description, Message). REQUIRED.", true, true)
          .addOption(OptionType.STRING, "value", "The new value of the field. REQUIRED.", true))

        //.addSubcommands(new SubcommandData("list", "List all copypastas available."))

        //TODO CONSIDER IMPLEMENTING A COPYPASTA LIST COMMAND THAT CAN BE USED TO LIST ALL THE COPYPASTAS AVAILABLE, I DON'T SEE THE NEED CONSIDERING DISCORD WILL SHOW US ALL
	    //TODO REGISTERED COPYPASTAS. SHOULD ASK AROUND AND SEE IF WE EVENTUALLY MAY OR MAY NOT NEED A NEED FOR THIS TO BE A THING.

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