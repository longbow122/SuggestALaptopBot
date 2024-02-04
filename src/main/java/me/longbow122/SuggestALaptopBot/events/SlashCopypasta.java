package me.longbow122.SuggestALaptopBot.events;

import me.longbow122.SuggestALaptopBot.configuration.CopypastaCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class SlashCopypasta extends ListenerAdapter {
  //FIXME THERE SHOULD BE A WAY OF HAVING MULTIPLE EVENT HANDLERS IN ONE? LOOK INTO IT???

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    /*
    This event handler will handle the copypasta commands themselves, defined within the commands file.
     */
    if(event.getGuild() == null) return;
    CopypastaCommandHandler commands = new CopypastaCommandHandler();
    HashMap<String, String> commandInfo = commands.returnAllCommandRegisterInformation();
    // If the command isn't in the file, give up.
    if (!(commandInfo.containsKey(event.getName()))) return;
    // We know the command exists now
    event.reply(commands.getMessageForCommand(event.getName())).setEphemeral(false).queue();
  }
}
