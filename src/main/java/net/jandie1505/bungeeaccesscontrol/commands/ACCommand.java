package net.jandie1505.bungeeaccesscontrol.commands;

import net.jandie1505.bungeeaccesscontrol.AccessControl;
import net.jandie1505.bungeeaccesscontrol.managers.data.Ban;
import net.jandie1505.bungeeaccesscontrol.utilities.Utilities;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
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
                                            Long time = null;

                                            if (!timeString.equalsIgnoreCase("null") && !timeString.equalsIgnoreCase("-") && !timeString.equalsIgnoreCase("permanent")) {
                                                time = Utilities.createTimeFromInput(timeString);
                                            }

                                            if (time != null && time >= 0) {

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

                                if (args.length > 4) {

                                    try {

                                        Ban ban = this.accessControl.getBanManager().getBan(Long.parseLong(args[2]));

                                        if (ban != null) {

                                            if (args[3].equalsIgnoreCase("player")) {

                                                UUID player = Utilities.getPlayerIdFromString(this.accessControl, args[4]);

                                                if (player != null) {
                                                    ban.setPlayer(player);
                                                } else {
                                                    sender.sendMessage("Player not found");
                                                    return;
                                                }

                                            } else if (args[3].equalsIgnoreCase("endTime")) {

                                                Long time = null;

                                                if (!args[4].equalsIgnoreCase("null") && !args[4].equalsIgnoreCase("-") && !args[4].equalsIgnoreCase("permanent")) {
                                                    time = Utilities.createTimeFromInput(args[4]);
                                                }

                                                if (time != null && time >= 0) {
                                                    ban.setEndTime(time);
                                                } else {
                                                    sender.sendMessage(this.TIME_FORMAT_ERROR);
                                                    return;
                                                }

                                            } else if (args[3].equalsIgnoreCase("cancelled")) {

                                                ban.setCancelled(Boolean.parseBoolean(args[4]));

                                            } else if (args[3].equalsIgnoreCase("reason")) {

                                                String reasonString = "";

                                                for (int i = 4; i < args.length; i++) {
                                                    reasonString = reasonString + args[i];
                                                }

                                                if (!reasonString.equalsIgnoreCase("null") && !reasonString.equalsIgnoreCase("-")) {
                                                    ban.setReason(reasonString);
                                                } else {
                                                    ban.setReason(null);
                                                }

                                            } else {
                                                sender.sendMessage("You can only modify player, endTime, cancelled and reason");
                                                return;
                                            }

                                            boolean success = this.accessControl.getBanManager().editBan(ban);

                                            if (success) {
                                                sender.sendMessage("Ban was successfully updated");
                                            } else {
                                                sender.sendMessage("Error while updating ban");
                                            }

                                        } else {
                                            sender.sendMessage("Please specify a valid ban id");
                                        }

                                    } catch (IllegalArgumentException e) {
                                        sender.sendMessage("Please specify a valid long value for the ban id");
                                    }

                                } else {
                                    sender.sendMessage("Usage: /" + this.getName() + " ban modify <id> <player/endTime/cancelled/reason> <data>");
                                }

                            } else if (args[1].equalsIgnoreCase("additional")) {

                                if (args.length > 3) {

                                    try {

                                        Ban ban = this.accessControl.getBanManager().getBan(Long.parseLong(args[3]));

                                        if (ban != null) {

                                            if (args[2].equalsIgnoreCase("get")) {

                                                if (args.length > 4) {

                                                    sender.sendMessage("Additional data for plugin " + args[4] + ": " + ban.getAdditionalData(args[4]).toString());

                                                } else {
                                                    sender.sendMessage("Usage: /" + this.getName() + " ban additional get <id> <plugin>");
                                                }

                                            } else if (args[2].equalsIgnoreCase("getFull")) {

                                                sender.sendMessage("Additional data: " + ban.exportAdditional().toString());

                                            } else if (args[2].equalsIgnoreCase("set")) {

                                                if (args.length > 5) {

                                                    String data = "";

                                                    for (int i = 5; i < args.length; i++) {
                                                        data = args[i];
                                                    }

                                                    try {
                                                        ban.setAdditionalData(args[4], new JSONObject(data));

                                                        boolean success = this.accessControl.getBanManager().editBan(ban);

                                                        if (success) {
                                                            sender.sendMessage("Additional data was updated");
                                                        } else {
                                                            sender.sendMessage("Error while updating additional data");
                                                        }
                                                    } catch (JSONException e) {
                                                        sender.sendMessage("Invalid JSON");
                                                    }

                                                } else {
                                                    sender.sendMessage("Usage: /" + this.getName() + " ban additional set <id> <plugin> <JSONObject>");
                                                }

                                            } else if (args[2].equalsIgnoreCase("remove")) {

                                                if (args.length > 4) {

                                                    ban.deleteAdditionalData(args[4]);

                                                    boolean success = this.accessControl.getBanManager().editBan(ban);

                                                    if (success) {
                                                        sender.sendMessage("Additional data was removed");
                                                    } else {
                                                        sender.sendMessage("Error while removing additional data");
                                                    }

                                                } else {
                                                    sender.sendMessage("Usage: /" + this.getName() + " ban additional remove <id> <plugin>");
                                                }

                                            } else if (args[2].equalsIgnoreCase("override")) {

                                                if (this.accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optBoolean("allowAdditionalOverrideCommand", false)) {

                                                    if (args.length > 4) {

                                                        String data = "";

                                                        for (int i = 4; i < args.length; i++) {
                                                            data = data + args[i];
                                                        }

                                                        try {

                                                            ban.importAdditional(new JSONObject(data));
                                                            this.accessControl.getBanManager().editBan(ban);

                                                            sender.sendMessage("Additional data was successfully overridden");

                                                        } catch (JSONException e) {
                                                            sender.sendMessage("Invalid JSON");
                                                        }

                                                    }

                                                } else {
                                                    sender.sendMessage("Enable allowAdditionalOverrideCommand in config to use this command");
                                                }

                                            } else {
                                                sender.sendMessage("Available subcommands: get, getFull, set, remove, override");
                                            }

                                        } else {
                                            sender.sendMessage("Ban with the specified id was not found");
                                        }

                                    } catch (IllegalArgumentException e) {
                                        sender.sendMessage("Please specify a valid long value for the ban id");
                                    }

                                } else {
                                    sender.sendMessage("Usage: /" + this.getName() + " ban additional <get/getFull/set/remove/override> <id> <plugin/-/plugin/plugin/JSONObject> <-/-/JSONObject/-/->");
                                }

                            } else if (args[1].equalsIgnoreCase("delete")) {

                                if (args.length > 3) {

                                    try {

                                        boolean success = this.accessControl.getBanManager().deleteBan(Long.parseLong(args[3]));

                                        if (success) {
                                            sender.sendMessage("Ban was successfully deleted");
                                        } else {
                                            sender.sendMessage("Error while deleting ban");
                                        }

                                    } catch (IllegalArgumentException e) {
                                        sender.sendMessage("Please specify a valid long value as ban id");
                                    }

                                } else {
                                    sender.sendMessage("Usage: /" + this.getName() + " ban delete <id>");
                                }

                            } else {
                                sender.sendMessage(this.UNKNOWN_COMMAND);
                            }

                        } else {
                            sender.sendMessage("Usage: /" + this.getName() + " ban <list/info/create/modify/additional/delete> [...]");
                        }

                    } else {
                        sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("nopermission", "No permission"));
                    }

                } else if (args[0].equalsIgnoreCase("kick")) {

                    if (sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("kick", "accesscontrol.command.kick"))) {

                        if (args.length > 1) {

                            ProxiedPlayer player;

                            try {
                                UUID playerUUid = UUID.fromString(args[1]);

                                player = this.accessControl.getProxy().getPlayer(playerUUid);
                            } catch (IllegalArgumentException e) {
                                player = this.accessControl.getProxy().getPlayer(args[1]);
                            }

                            if (player != null) {

                                if (args.length > 2) {

                                    String reason = "";

                                    for (int i = 2; i < args.length; i++) {
                                        reason = reason + args[i];
                                    }

                                    player.disconnect(reason);

                                } else {
                                    player.disconnect();
                                }

                            } else {
                                sender.sendMessage("Player is offline");
                            }

                        } else {
                            sender.sendMessage("Usage: /" + this.getName() + " kick <player>");
                        }

                    } else {
                        sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("nopermission", "No permission"));
                    }

                } else if (args[0].equalsIgnoreCase("maintenance")) {

                    if (sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("maintenance", "accesscontrol.command.maintenance"))) {

                        if (args.length > 1) {

                            if (args[1].equalsIgnoreCase("enable")) {

                                this.accessControl.setMaintenance(true);
                                sender.sendMessage("Maintenance mode enabled");

                            } else if (args[1].equalsIgnoreCase("disable")) {

                                this.accessControl.setMaintenance(false);
                                sender.sendMessage("Maintenance mode disabled");

                            } else if (args[1].equalsIgnoreCase("setReason")) {

                                if (args.length > 2) {

                                    String reason = "";

                                    for (int i = 2; i < args.length; i++) {
                                        reason = reason + args[i];
                                    }

                                    sender.sendMessage("Currently unsupported");

                                } else {
                                    sender.sendMessage("Usage: /" + this.getName() + " maintenance setReason <reason>");
                                }

                            } else {
                                sender.sendMessage("Invalid subcommand");
                            }

                        } else {
                            sender.sendMessage("Usage: /" + this.getName() + " maintenance <enable/disable/setReason>");
                        }

                    } else {
                        sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("nopermission", "No permission"));
                    }

                } else if (args[0].equalsIgnoreCase("lockdown")) {

                    if (sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("lockdown", "accesscontrol.command.lockdown"))) {

                        if (args.length > 1) {

                            if (args[1].equalsIgnoreCase("test")) {

                                this.accessControl.setLockdown(true);
                                sender.sendMessage("Testing lockdown (enabled)");

                            } else if (args[1].equalsIgnoreCase("reset")) {

                                this.accessControl.setLockdown(false);
                                sender.sendMessage("Lockdown was reset (disabled)");

                            } else if (args[1].equalsIgnoreCase("status")) {

                                sender.sendMessage("Current lockdown status: " + this.accessControl.isLockdown());

                            } else {
                                sender.sendMessage("Invalid subcommand");
                            }

                        } else {
                            sender.sendMessage("Usage: /" + this.getName() + " lockdown <test/reset/status>");
                        }

                    } else {
                        sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("nopermission", "No permission"));
                    }

                } else if (args[0].equalsIgnoreCase("help")) {

                } else {
                    sender.sendMessage(this.UNKNOWN_COMMAND);
                }

            } else {
                sender.sendMessage("BungeeAccessControl " + AccessControl.VERSION + " by jandie1505\nHelp command: /" + this.getName() + " help");
            }

        } else {
            sender.sendMessage(accessControl.getConfigManager().getConfig().optJSONObject("messages", new JSONObject()).optString("nopermission", "No permission"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> tabCompletions = new ArrayList<>();

        if (sender.hasPermission(accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optJSONObject("permissions", new JSONObject()).optString("base", "accesscontrol.command"))) {

            if (args.length == 1) {

                tabCompletions.add("ban");
                tabCompletions.add("kick");
                tabCompletions.add("maintenance");
                tabCompletions.add("lockdown");
                tabCompletions.add("help");

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("ban")) {

                    tabCompletions.add("list");
                    tabCompletions.add("info");
                    tabCompletions.add("create");
                    tabCompletions.add("modify");
                    tabCompletions.add("additional");
                    tabCompletions.add("delete");

                } else if (args[0].equalsIgnoreCase("kick")) {

                    for (ProxiedPlayer player : List.copyOf(this.accessControl.getProxy().getPlayers())) {
                        if (player != null) {
                            tabCompletions.add(player.getName());
                        }
                    }

                } else if (args[0].equalsIgnoreCase("maintenance")) {

                    tabCompletions.add("enable");
                    tabCompletions.add("disable");
                    tabCompletions.add("status");
                    tabCompletions.add("setReason");

                } else if (args[0].equalsIgnoreCase("lockdown")) {

                    tabCompletions.add("test");
                    tabCompletions.add("reset");
                    tabCompletions.add("status");

                }

            } else if (args.length == 3) {

                if (args[0].equalsIgnoreCase("ban")) {

                    if (args[1].equalsIgnoreCase("modify")) {

                        tabCompletions.add("player");
                        tabCompletions.add("endTime");
                        tabCompletions.add("cancelled");
                        tabCompletions.add("reason");

                    } else if (args[1].equalsIgnoreCase("additional")) {

                        tabCompletions.add("get");
                        tabCompletions.add("getFull");
                        tabCompletions.add("set");
                        tabCompletions.add("remove");
                        if (this.accessControl.getConfigManager().getConfig().optJSONObject("command", new JSONObject()).optBoolean("allowAdditionalOverrideCommand", false)) {
                            tabCompletions.add("override");
                        }

                    }

                }

            }

        }

        return List.copyOf(tabCompletions);
    }
}
