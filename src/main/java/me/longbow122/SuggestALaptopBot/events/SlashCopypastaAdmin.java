package me.longbow122.SuggestALaptopBot.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCopypastaAdmin extends ListenerAdapter {
  /*
  Worth noting that all of this logic was originally meant to be in SlashCopypasta's class but since

   */

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    //TODO THIS FEELS VERY HARDCODED AND NOT THAT INTUITIVE. IS THERE A BETTER WAY OF DOING THIS?
    switch (event.getFullCommandName()) {
      case "copypasta add": {

        System.out.println("Command is to be added.");

      }
      case "copypasta list": {

      }
      case "copypasta remove": {

      }
      default: {
        event.reply("SOMETHING HAS GONE WRONG WITH THE COPYPASTA COMMANDS. PLEASE CONTACT AN ADMIN ASAP.").setEphemeral(false).queue();
        return;
      }
    }
  }
}
