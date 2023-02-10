package net.jandie1505.bungeeaccesscontrol.commands;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.managers.data.Ban;
import net.jandie1505.bungeeaccesscontrol.utilities.Utilities;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("noPermission", "Unknown command usage. Use /ac help for help.")
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
            sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("unknownCommandUsage", "Unknown command usage. Use /ac help for help."));
        }
    }

    private void banSubcommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("permission", "accesscontrol.command.ban"))) {
            sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("noPermission", "Unknown command usage. Use /ac help for help."));
            return;
        }

        if (args.length <= 1) {
            sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("unknownCommandUsage", "Unknown command usage. Use /ac help for help."));
            return;
        }

        switch (args[1]) {
            case "list":

                List<Ban> banList;

                if (args.length == 3) {
                    UUID playerId = Utilities.getPlayerIdFromString(this.accessControl, args[2]);

                    if (playerId == null) {
                        sender.sendMessage("You need to specify a valid UUID or an online player");
                        return;
                    }

                    banList = this.accessControl.getBanManager().getBans(playerId);
                } else {
                    banList = this.accessControl.getBanManager().getBans();
                }

                if (banList.isEmpty()) {
                    sender.sendMessage("No bans found");
                    return;
                }

                for (Ban ban : banList) {
                    sender.sendMessage("id=" + ban.getId() + ";uuid=" + ban.getPlayer().toString() + ";endTime=" + ban.getEndTime() + ";reason=" + ban.getReason() + ";cancelled=" + ban.isCancelled());
                }

                break;
            case "info":
                if (args.length < 3) {
                    sender.sendMessage("You need to specify a valid ban id");
                    return;
                }

                long banId;

                try {
                    banId = Long.parseLong(args[2]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("You need to specify a valid ban id");
                    return;
                }

                Ban ban = this.accessControl.getBanManager().getBan(banId);

                if (ban == null) {
                    sender.sendMessage("Ban not found");
                    return;
                }

                sender.sendMessage("----- BAN INFORMATION -----\n" +
                        "ID: " + ban.getId() + "\n" +
                        "Player UUID: " + ban.getPlayer().toString() + "\n" +
                        "End time: " + ban.getEndTime() + "\n" +
                        "Reason: " + ban.getReason() + "\n" +
                        "Cancelled: " + ban.isCancelled() + "\n" +
                        "Additional data:" + ban.exportAdditional().toString() + "\n" +
                        "---------------------------");

                break;
            case "create":
                if (args.length < 3) {
                    sender.sendMessage("You need to specify a player");
                    return;
                }

                UUID playerId = Utilities.getPlayerIdFromString(this.accessControl, args[2]);

                if (playerId == null) {
                    sender.sendMessage("You need to specify a valid UUID or an online player");
                }

                Long endTime = null;
                String reason = null;
                JSONObject additionalData = null;

                if (args.length >= 4) {
                    try {
                        endTime = Long.parseLong(args[3]);
                    } catch (IllegalArgumentException e) {
                        if (!(args[3].equalsIgnoreCase("null") || args[3].equalsIgnoreCase("permanent") || args[3].equalsIgnoreCase("-"))) {
                            sender.sendMessage("You need to specify a valid ban unix time stamp or null/permanent/- for permanent");
                            return;
                        }
                    }
                }

                if (args.length >= 5) {
                    reason = args[4].replace("{SPACE}", " ");
                }

                if (args.length >= 6) {
                    try {
                        additionalData = new JSONObject(args[5].replace("{SPACE}", " "));
                    } catch (JSONException e) {
                        sender.sendMessage("Invalid additional data json string");
                        return;
                    }
                }

                long banId = this.accessControl.getBanManager().banPlayer(playerId, endTime, reason, additionalData);

                if (banId > 0) {
                    sender.sendMessage("Ban with id " + banId + " was created");
                } else {
                    sender.sendMessage("Error while creating ban");
                }

                break;
            default:
                break;
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
