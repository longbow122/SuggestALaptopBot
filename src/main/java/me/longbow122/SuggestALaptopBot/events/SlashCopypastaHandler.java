package me.longbow122.SuggestALaptopBot.events;

import me.longbow122.SuggestALaptopBot.db.CopypastaDB;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
        if (db.doesCopypastaExist(nameEntered)) {
          event.reply("Looks like a command with that name already exists. Change the name of the command and try adding it again. Here is your message: **" + messageEntered + "**").setEphemeral(true).queue();
          return;
        }
        db.addCopypasta(nameEntered, event.getOption("description").getAsString(), messageEntered);
        //TODO TURN THIS REPLY INTO A FANCIER EMBED
        event.reply("Command added.").setEphemeral(true).queue();
        return;
      }
      case "copypasta remove": {
        String nameEntered = event.getOption("name").getAsString();
        if (!(db.doesCopypastaExist(nameEntered))) {
          event.reply("Looks like a command with that name does NOT exist. Try deleting a command that exists.").setEphemeral(true).queue();
          return;
        }
        db.removeCopypasta(nameEntered);
        //TODO TURN THIS REPLY INTO AN EMBED?
        event.reply("Command has been removed successfully. Recommend you give the bot a restart to ensure the list of commands is up to date.").setEphemeral(true).queue();
        return;
      }
      default: {
        event.reply("SOMETHING HAS GONE WRONG WITH THE COPYPASTA COMMANDS. PLEASE CONTACT AN ADMIN ASAP.").setEphemeral(false).queue();
        return;
      }
    }
  }
}
