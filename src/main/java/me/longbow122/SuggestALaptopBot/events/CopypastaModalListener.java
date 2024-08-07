package me.longbow122.SuggestALaptopBot.events;

import me.longbow122.SuggestALaptopBot.db.CopypastaDB;
import me.longbow122.SuggestALaptopBot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Objects;

public class CopypastaModalListener extends ListenerAdapter {

	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		CopypastaDB db = new CopypastaDB();
		// This seems like an odd way of recieving the modal event, but it makes sense if you're working with many different modals.
		// * The copypasta add modal will be recieved and handled here. Validation on input and addition of the command is to be handled here.
		if (event.getModalId().equals("copypastaAdd")) {
			String nameEntered = Objects.requireNonNull(event.getValue("name")).getAsString();
			String descriptionEntered = Objects.requireNonNull(event.getValue("description")).getAsString();
			String messageEntered = Objects.requireNonNull(event.getValue("message")).getAsString();
			if (db.doesCopypastaExist(nameEntered)) {
				event.reply("Looks like a command with that name already exists. Try again. \n Message: **" + messageEntered + "**" + "\n Description: **" + descriptionEntered + "**")
					.setEphemeral(true)
					.queue();
				return;
			}
			if (!(Utils.checkCommandName(nameEntered))) {
				event.reply("The name you have entered is not valid. A name must consist of only lowercase alphabetic characters, dashes, and underscores. Try again. \n Message: " + messageEntered + "\n Description: " + descriptionEntered).setEphemeral(true).queue();
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
