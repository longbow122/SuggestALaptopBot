package me.longbow122.SuggestALaptopBot;


import me.longbow122.SuggestALaptopBot.configuration.CopypastaCommandHandler;
import me.longbow122.SuggestALaptopBot.events.SlashCopypasta;
import me.longbow122.SuggestALaptopBot.configuration.ConfigHandler;
import me.longbow122.SuggestALaptopBot.events.SlashCopypastaAdmin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Main {

  static ConfigHandler configFile = new ConfigHandler("config.json");

  public static void main(String[] args) {
    System.out.println("Starting bot...");
    String token = configFile.getString("token").replace("\"", "");

    JDA jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
      .addEventListeners(new SlashCopypasta())
      .addEventListeners(new SlashCopypastaAdmin())
      .build();

    HashMap<String, String> commandInfo = new CopypastaCommandHandler().returnAllCommandRegisterInformation();
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
        .addOption(OptionType.STRING, "message", "The actual message behind the copypasta command. REQUIRED.", true)
        .addOption(OptionType.STRING, "description", "The description of the copypasta command to be added. OPTIONAL.", false))

      .addSubcommands(new SubcommandData("remove", "Remove a copypasta from the list of slash commands")
        .addOptions(new OptionData(OptionType.STRING, "name", "The name of the copypasta command to be removed. REQUIRED.", true, true)))
      //TODO NEED TO IMPLEMENT AUTOCOMPLETE LOGIC USING THE COMMAND LIST AND THE AUTOCOMPLETE EVENT

      .addSubcommands(new SubcommandData("list", "List the command copypastas and their messages"))
      .setGuildOnly(true));

    commands.queue();
    }
  }