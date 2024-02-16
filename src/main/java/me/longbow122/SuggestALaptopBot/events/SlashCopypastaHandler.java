package me.longbow122.SuggestALaptopBot.events;

import me.longbow122.SuggestALaptopBot.db.CopypastaDB;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.h2.util.StringUtils;

import java.util.HashMap;

public class SlashCopypastaHandler extends ListenerAdapter {
  /*
  Worth noting that all of this logic was originally meant to be in SlashCopypasta's class but since

   */

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    //TODO THIS FEELS VERY HARDCODED AND NOT THAT INTUITIVE. IS THERE A BETTER WAY OF DOING THIS?
    // We also have all the handling for copypastas under one event. This is okay for now, but
    // I'm not too happy with it.

    // Something bad must have happened for the guild to return null here.
    if(event.getGuild() == null) return;
    // * The standard copypasta handling goes here.
    // Slash command handling goes here for outputting standard copypastas.
    CopypastaDB db = new CopypastaDB();
    HashMap<String, String> commandInfo = db.getAllCopypastaInformation();
    if (commandInfo.containsKey(event.getName())) {
      event.reply(db.getMessage(event.getName())).setEphemeral(false).queue();
      return;
    }

    // * The "admin" portion of the command handling. This is where copypasta adding and removal is handled.
    switch (event.getFullCommandName()) {
      case "copypasta add": {
        // * Ignore any potential NPEs from a required parameter.
        String nameEntered = event.getOption("name").getAsString();
        String messageEntered = event.getOption("message").getAsString();
        String descriptionEntered = event.getOption("description").getAsString();
        if (db.doesCopypastaExist(nameEntered)) {
          event.reply("Looks like a command with that name already exists. Change the name of the command and try adding it again. Here is your message: **" + messageEntered + "**" + " Description: **" + descriptionEntered + "**").setEphemeral(true).queue();
          return;
        }
        if (!(checkName(nameEntered))) {
          event.reply("The name you have entered is not valid. A name must consist of only lowercase alphabetical characters, with dashes and be between 1-32 characters. Try again. \n Description: " + event.getOption("description").getAsString() + "\n Message: " + messageEntered).setEphemeral(true).queue();
          return;
        }
        if (!(descriptionEntered.length() <= 100)) {
          event.reply("The description you have entered is not valid. A description must consist of only 1-100 characters. Try again. \n Name: " + nameEntered + "\n Message: " + messageEntered).setEphemeral(true).queue();
          return;
        }
        db.addCopypasta(nameEntered, descriptionEntered, messageEntered);
        event.reply(MessageCreateData.fromEmbeds(getCommandAddedEmbed(nameEntered, descriptionEntered, messageEntered))).setEphemeral(false).queue();
        return;
      }
      case "copypasta remove": {
        String nameEntered = event.getOption("name").getAsString();
        if (!(db.doesCopypastaExist(nameEntered))) {
          event.reply("Looks like a command with that name does NOT exist. Try deleting a command that exists.").setEphemeral(true).queue();
          return;
        }
        db.removeCopypasta(nameEntered);
        event.reply(nameEntered + " command has been removed successfully. Recommend you give the bot a restart to ensure the list of commands is up to date.").setEphemeral(true).queue();
        return;
      }
      case "copypasta update": {
        //TODO IMPLEMENT THIS BEHAVIOUR, EVERYTHING ELSE IS TESTED AND LOOKS GOOD
      }
      default: {
        event.reply("SOMETHING HAS GONE WRONG WITH THE COPYPASTA COMMANDS. PLEASE CONTACT AN ADMIN ASAP.").setEphemeral(false).queue();
        return;
      }
    }
  }


  /**
   * Minor utility method to check and validate the name of the command to be added, ensuring that it lies within Discord's limits.
   * The limits for the name of a command within Discord are that the name must be between 1-32 characters, and lowercase alphabetic, with dashes.
   * @param name {@link String} representing the name of the command to be added.
   * @return {@link Boolean} representing whether the name is valid. Returns True if valid, False otherwise.
  */
  private boolean checkName(String name) {
    if (name.length() > 32 || name.isEmpty()) return false;
    char[] nameChars = name.toCharArray();
    for (char i : nameChars) {
      // Ignore dashes, since they are allowed in names.
      if (i == '-') continue;
      // Make sure that it is both alphabetic and lowercase.
      if (!(Character.isLowerCase(i)) || !(Character.isAlphabetic(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Basic method to generate a nicer formatted embed for the "command added" message, displaying the information passed through the bot in a much neater format. <br>
   * It is worth noting that this method and others that provide similar behaviour may have to be moved into their own class, but for the time being, they can remain as private methods
   * within the class that they are being used in.
   * @param name {@link String} representing the name of the command that was added
   * @param description {@link String} representing the name of the description that is to be added to the bot for the copypasta command just added.
   * @param message {@link String} representing the message that is to be added to the bot for the copypasta command just added.
   * @return {@link MessageEmbed} representing the embed that is to be sent to the user, showing the information that was just added to the bot for the relevant copypasta command.
   */
  private MessageEmbed getCommandAddedEmbed(String name, String description, String message) {
    EmbedBuilder b = new EmbedBuilder();
    b.setAuthor("Command Added!");
    b.addField("Name", name, true);
    b.addField("Description", description, true);
    b.addField("Message", message, false);
    b.addField("Restart me!", "Due to current limitations, it is advised that you restart me after adding/removing a set of commands. Please restart me :(", false);
    b.setFooter("Contact longbow122 if there are issues with this bot.");
    return b.build();
  }
}
