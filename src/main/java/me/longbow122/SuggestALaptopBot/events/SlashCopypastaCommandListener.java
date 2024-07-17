package me.longbow122.SuggestALaptopBot.events;

import me.longbow122.SuggestALaptopBot.db.CopypastaDB;
import me.longbow122.SuggestALaptopBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SlashCopypastaCommandListener extends ListenerAdapter {
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
				TextInput commandName = TextInput.create("name", "Command Name:", TextInputStyle.SHORT).setPlaceholder("Enter the name of the command.").setMinLength(1).setMaxLength(32).build();
				TextInput commandDescription = TextInput.create("description", "Description:", TextInputStyle.PARAGRAPH).setPlaceholder("Enter a description of the command.").setMinLength(1).setMaxLength(100).build();
				TextInput commandMessage = TextInput.create("message", "Message:", TextInputStyle.PARAGRAPH).setPlaceholder("Enter the message to be sent.").setMinLength(1).setMaxLength(2000).build();
				Modal modal = Modal.create("copypastaAdd", "Add a new copypasta")
					.addActionRow(commandName)
					.addActionRow(commandDescription)
					.addActionRow(commandMessage)
					.build();
				event.replyModal(modal).queue();
				return;
			}
			case "copypasta remove": {
				// ? This might throw an NPE but we can ignore that risk since we enforce that the parameters must have a provided value.
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
				String nameEntered = event.getOption("name").getAsString();
				String fieldEntered = event.getOption("field").getAsString();
				String valueEntered = event.getOption("value").getAsString();
				List<String> fieldVals = Arrays.asList("name", "description", "message");
				if (!(fieldVals.contains(fieldEntered))) {
					event.reply("Looks like a field with that name does NOT exist. Try updating a field that exists. \n \n You need to enter one of: 'name', 'description','message'")
						.setEphemeral(true)
						.queue();
					return;
				}
				if (!(db.doesCopypastaExist(nameEntered))) {
					event.reply("Looks like a command with that name does NOT exist. Try updating a command that exists.").setEphemeral(true).queue();
					return;
				}
				// * We need to check that the provided values we were given for some field is valid before continuing.
				// * No way of finding out which field we were given without going through all of them, so we will see if they meet all of them in a switch case.
				switch (fieldEntered) {
					case "name": {
						if (!(Utils.checkCommandName(valueEntered))) {
							event.reply("Looks like you tried to enter a new command name that is not valid. Please enter a valid command name.").setEphemeral(true).queue();
							return;
						}
						if (db.doesCopypastaExist(valueEntered)) {
							event.reply("Looks like you tried to update an old command with a new name, that is the name of another command. Try changing the name to the name of a command that does not exist.").setEphemeral(true).queue();
							return;
						}
						break;
					}
					case "description": {
						if (!(valueEntered.length() <= 100)) {
							event.reply("Looks like you tried to enter a description that is too long. The description must be less than 100 characters long.").setEphemeral(true).queue();
						}
						break;
					}
					case "message": {
						if (!(valueEntered.length() <= 2000)) {
							event.reply("Looks like you tried to enter a message that is too long. The message must be less than 2000 characters long.").setEphemeral(true).queue();
							return;
						}
						break;
					}
				}
				// * Now that all checks have passed, we can update the copypasta.
				db.updateCopypasta(nameEntered, fieldVals.indexOf(fieldEntered), valueEntered);
				event.reply("Copypasta successfully updated! \n Name: **" + nameEntered + "** \n Field: **" + fieldEntered + "** \n Value: **" + valueEntered + "**").setEphemeral(true).queue();
				return;
			}
			default: {
				event.reply("SOMETHING HAS GONE WRONG WITH THE COPYPASTA COMMANDS. PLEASE CONTACT AN ADMIN ASAP.").setEphemeral(false).queue();
				return;
			}
		}
	}
}
