package me.longbow122.SuggestALaptopBot.events;

import me.longbow122.SuggestALaptopBot.db.CopypastaDB;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CopypastaAutocompleteListener extends ListenerAdapter {
	private final List<Command.Choice> fieldChoices = Arrays.asList(new Command.Choice("name", "name"), new Command.Choice("description", "description"), new Command.Choice("message", "message"));

	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
		if (event.getFullCommandName().equals("copypasta update")) {
			switch (event.getFocusedOption().getName()) {
				case "name": {
					List<Command.Choice> foundChoices = new ArrayList<>();
					for (String name : new CopypastaDB().getAllCopypastaStartsWith(event.getOption("name").getAsString())) {
						foundChoices.add(new Command.Choice(name, name));
					}
					System.out.println(foundChoices);
					event.replyChoices(foundChoices).queue();
					return;
				}
				case "field": {
					event.replyChoices(fieldChoices).queue();
					return;
				}
			}
			}
		if (event.getFullCommandName().equals("copypasta remove") && event.getFocusedOption().getName().equals("name")) {
			List<Command.Choice> foundChoices = new ArrayList<>();
			for (String name : new CopypastaDB().getAllCopypastaStartsWith(event.getOption("name").getAsString())) {
				foundChoices.add(new Command.Choice(name, name));
			}
			event.replyChoices(foundChoices).queue();
			return;
		}
	}
}
