package net.jandie1505.bungeeaccesscontrol.commands;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.managers.data.Ban;
import net.jandie1505.bungeeaccesscontrol.utilities.Utilities;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ACCommand extends Command implements TabExecutor {
    private final AccessControl accessControl;
    private final String UNKNOWN_COMMAND;
    private final String TIME_FORMAT_ERROR;

    public ACCommand(AccessControl accessControl) {
        super(
                accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optString("command", "ac"),
                accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optString("permission", "accesscontrol.command"),
                Utilities.aliases(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()))
        );
        this.accessControl = accessControl;
        this.UNKNOWN_COMMAND = "Unknown command. Use /" + this.getName() + " help for help.";
        this.TIME_FORMAT_ERROR = "Please specify a valid time string:\n" +
                "Time format: <prefix><time>\n" +
                "<time> is a time in seconds.\n" +
                "<prefix> is r for relative time (r30: ban for 30 seconds) or a for absolute unix time (a1676390535: ban until 1676390535 seconds after 1st Jan 1970).";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("base", "accesscontrol.command"))) {
            
            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("ban")) {

                    if (sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("ban", "accesscontrol.command.ban"))) {

                        if (args.length > 1) {

                            if (args[1].equalsIgnoreCase("list")) {

                                // ban list [player]

                                List<Ban> bans;
                                String reply;

                                if (args.length > 2) {

                                    UUID target = Utilities.getPlayerIdFromString(this.accessControl, args[2]);

                                    if (target == null) {
                                        sender.sendMessage("Player not found");
                                        return;
                                    }

                                    bans = this.accessControl.getBanManager().getBans(target);
                                    reply = "Bans of " + target + ":\n";
                                } else {
                                    bans = this.accessControl.getBanManager().getBans();
                                    reply = "Bans of the Server:\n";
                                }

                                for (Ban ban : bans) {
                                    reply = reply + "id=" + ban.getId() + ";player=" + ban.getPlayer().toString() + ";endTime=" + ban.getEndTime() + "cancelled=" + ban.isCancelled() + "\n" ;
                                }

                                sender.sendMessage(reply);
                            } else if (args[1].equalsIgnoreCase("info")) {

                                // ban info <id>

                                if (args.length > 2) {

                                    try {

                                        Ban ban = this.accessControl.getBanManager().getBan(Long.parseLong(args[2]));

                                        if (ban == null) {
                                            sender.sendMessage("Please specify a valid ban id");
                                            return;
                                        }

                                        sender.sendMessage("----- BAN INFORMATION -----\n" +
                                                "ID: " + ban.getId() + "\n" +
                                                "Player: " + ban.getPlayer() + "\n" +
                                                "End time: " + ban.getEndTime() + "\n" +
                                                "Cancelled: " + ban.isCancelled() + "\n" +
                                                "Reason: " + ban.getReason() + "\n" +
                                                "Additional info: " + ban.exportAdditional().toString() + "\n" +
                                                "---------------------------");

                                    } catch (IllegalArgumentException e) {
                                        sender.sendMessage("Please specify a valid long value");
                                    }

                                } else {
                                    sender.sendMessage("Usage: /" + this.getName() + " ban info <id>");
                                }

                            } else if (args[1].equalsIgnoreCase("create")) {

                                // /ban create <player> [endTime] [reason]

                                if (args.length > 2) {

                                    UUID player = Utilities.getPlayerIdFromString(this.accessControl, args[2]);

                                    if (player != null) {

                                        if (args.length > 3) {

                                            String timeString = args[3];
                                            long time = -1;

                                            try {
                                                if (timeString.length() >= 2) {

                                                    if (timeString.startsWith("r")) {

                                                        time = Instant.now().getEpochSecond() + Long.parseLong(timeString.substring(1));

                                                    } else if (timeString.startsWith("a")) {

                                                        time = Long.parseLong(timeString.substring(1));

                                                    } else {
                                                        sender.sendMessage(this.TIME_FORMAT_ERROR);
                                                        return;
                                                    }

                                                } else {
                                                    sender.sendMessage(this.TIME_FORMAT_ERROR);
                                                    return;
                                                }
                                            } catch (IllegalArgumentException e) {
                                                sender.sendMessage(this.TIME_FORMAT_ERROR);
                                                return;
                                            }

                                            if (time >= 0) {

                                                if (args.length > 4) {

                                                    String reasonString = "";

                                                    for (int i = 4; i < args.length; i++) {

                                                        reasonString = reasonString + args[i];

                                                    }

                                                    long banId = this.accessControl.getBanManager().banPlayer(player, time, reasonString, null);

                                                    if (banId >= 0) {
                                                        sender.sendMessage("Ban with id " + banId + " was created");
                                                    } else {
                                                        sender.sendMessage("Error while creating ban");
                                                    }

                                                } else {

                                                    long banId = this.accessControl.getBanManager().banPlayer(player, time, null, null);

                                                    if (banId >= 0) {
                                                        sender.sendMessage("Ban with id " + banId + " was created");
                                                    } else {
                                                        sender.sendMessage("Error while creating ban");
                                                    }

                                                }

                                            } else {
                                                sender.sendMessage(this.TIME_FORMAT_ERROR);
                                            }

                                        } else {

                                            long banId = this.accessControl.getBanManager().banPlayer(player, null, null, null);

                                            if (banId >= 0) {
                                                sender.sendMessage("Ban with id " + banId + " was created");
                                            } else {
                                                sender.sendMessage("Error while creating ban");
                                            }

                                        }

                                    } else {
                                        sender.sendMessage("Player not found");
                                    }

                                } else {
                                    sender.sendMessage("Usage: /" + this.getName() + " ban create <player> [endTime] [reason]");
                                }

                            } else if (args[1].equalsIgnoreCase("modify")) {

                            } else if (args[1].equalsIgnoreCase("delete")) {

                            } else {
                                sender.sendMessage(this.UNKNOWN_COMMAND);
                            }

                        } else {
                            sender.sendMessage("Usage: /" + this.getName() + " ban <list/info/create/modify/delete> [...]");
                        }

                    } else {
                        sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("nopermission", "No permission"));
                    }

                } else if (args[0].equalsIgnoreCase("kick")) {
                    
                } else if (args[0].equalsIgnoreCase("maintenance")) {

                } else if (args[0].equalsIgnoreCase("lockdown")) {

                } else if (args[0].equalsIgnoreCase("help")) {

                } else {
                    sender.sendMessage(this.UNKNOWN_COMMAND);
                }

            } else {
                sender.sendMessage("BungeeAccessControl " + AccessControl.VERSION + "by jandie1505\nHelp command: /" + this.getName() + " help");
            }

        } else {
            sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("nopermission", "No permission"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && !sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("base", "accesscontrol.command"))) {
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
