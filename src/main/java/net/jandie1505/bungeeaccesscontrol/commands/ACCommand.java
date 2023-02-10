package net.jandie1505.bungeeaccesscontrol.commands;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.utilities.Utilities;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ACCommand extends Command implements TabExecutor {
    private final AccessControl accessControl;

    public ACCommand(AccessControl accessControl) {
        super(
                accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optString("command", "ac"),
                accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optString("permission", "accesscontrol.command"),
                Utilities.aliases(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()))
        );
        this.accessControl = accessControl;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("permission", "accesscontrol.command"))) {
            return;
        }

        if (args.length >= 1) {

            switch (args[0]) {
                case "ban":
                    this.banSubcommand(sender, args);
                    break;
                case "kick":
                    this.kickSubcommand(sender, args);
                    break;
                case "maintenance":
                    this.maintenanceSubcommand(sender, args);
                    break;
                case "lockdown":
                    this.lockdownSubcommand(sender, args);
                    break;
                case "help":
                    this.helpSubcommand(sender, args);
                    break;
                default:
                    break;
            }

        } else {
            sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("unknownCommandUsage", new JSONObject()).optString("unknownCommandUsage", "Unknown command usage. Use /ac help for help."));
        }
    }

    private void banSubcommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("permission", "accesscontrol.command.ban"))) {
            return;
        }
    }

    private void kickSubcommand(CommandSender sender, String[] args) {

    }

    private void maintenanceSubcommand(CommandSender sender, String[] args) {

    }

    private void lockdownSubcommand(CommandSender sender, String[] args) {

    }

    private void helpSubcommand(CommandSender sender, String[] args) {

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && !sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("permission", "accesscontrol.command"))) {
            return List.of();
        }

        List<String> tabCompletions = new ArrayList<>();

        if (args.length == 1) {

            switch (args[0]) {
                case "ban":
                    tabCompletions.add("list");
                    tabCompletions.add("info");
                    tabCompletions.add("create");
                    tabCompletions.add("modify");
                    tabCompletions.add("delete");
                    break;
                case "kick":
                    for (ProxiedPlayer player : List.copyOf(this.accessControl.getProxy().getPlayers())) {
                        if (player != null) {
                            tabCompletions.add(player.getName());
                        }
                    }
                    break;
                case "maintenance":
                    tabCompletions.add("enable");
                    tabCompletions.add("disable");
                    tabCompletions.add("setReason");
                    break;
                case "lockdown":
                    tabCompletions.add("test");
                    tabCompletions.add("reset");
                    break;
                default:
                    break;
            }

        } else if (args.length == 2) {

            switch (args[0]) {
                case "ban":
                    switch (args[1]) {
                        case "create":
                            for (ProxiedPlayer player : List.copyOf(this.accessControl.getProxy().getPlayers())) {
                                if (player != null) {
                                    tabCompletions.add(player.getName());
                                }
                            }
                            break;
                        case "modify":
                            tabCompletions.add("setPlayer");
                            tabCompletions.add("setEndTime");
                            tabCompletions.add("setCancelled");
                            tabCompletions.add("setReason");
                            tabCompletions.add("setAdditionalInfo");
                        default:
                            break;
                    }
                    break;
                case "kick":
                    for (ProxiedPlayer player : List.copyOf(this.accessControl.getProxy().getPlayers())) {
                        if (player != null) {
                            tabCompletions.add(player.getName());
                        }
                    }
                    break;
                default:
                    break;
            }

        } else {

            tabCompletions.add("ban");
            tabCompletions.add("kick");
            tabCompletions.add("maintenance");
            tabCompletions.add("lockdown");
            tabCompletions.add("help");

        }

        return List.copyOf(tabCompletions);
    }
}
