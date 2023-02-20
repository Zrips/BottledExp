package me.Zrips.bottledexp.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Zrips.bottledexp.BottledExp;
import me.Zrips.bottledexp.Calculations;
import me.Zrips.bottledexp.ConfigFile;
import me.Zrips.bottledexp.EBlock;
import me.Zrips.bottledexp.EBlockInfo;
import me.Zrips.bottledexp.Language;
import me.Zrips.bottledexp.Util;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;

public class BottledExpCommands implements CommandExecutor {
    public static final String label = "bottle";

    private static HashMap<String, BottleCMD> commands = new HashMap<String, BottleCMD>();

    static {
        for (Method m : BottledExpCommands.class.getMethods()) {
            if (!m.isAnnotationPresent(CAnnotation.class))
                continue;
            commands.put(m.getName().toLowerCase(), new BottleCMD(m));
        }
    }

    public BottleCMD getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public HashMap<String, BottleCMD> getCommands() {
        return commands;
    }

    public HashMap<String, BottleCMD> getCommands(CommandSender sender) {
        if (!(sender instanceof Player))
            return commands;

        HashMap<String, BottleCMD> temp = new HashMap<String, BottleCMD>();

        for (Entry<String, BottleCMD> one : commands.entrySet()) {
            if (!(hasCommandPermission(sender, one.getKey())))
                continue;
            temp.put(one.getKey(), one.getValue());
        }
        return temp;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0)
            return help(sender);

        String cmdName = args[0].toLowerCase();

        BottleCMD cmd = getCommand(cmdName);

        if (cmd == null) {
            return help(sender);
        }

        if (!hasCommandPermission(sender, cmdName)) {
            LC.info_NoPermission.sendMessage(sender);
            return true;
        }

        String[] myArgs = CMIArray.removeFirst(args);

        if (myArgs.length > 0 && myArgs[myArgs.length - 1].equals("?")) {
            sendUsage(sender, cmdName);
            return true;
        }

        try {
            return (Boolean) cmd.getMethod().invoke(this, sender, myArgs);
        } catch (Throwable e) {
            return help(sender);
        }
    }

    private static boolean hasCommandPermission(CommandSender sender, String cmd) {
        return sender.hasPermission("bottledexp.command." + cmd);
    }

    private static String getUsage(String cmd) {
        String command = Language.getMessage("command.help.output.cmdFormat").replace("[command]", cmd);
        String key = "command." + cmd + ".help.args";
        if (Language.containsKey(key)) {
            command = command.replace("[arguments]", Language.getMessage(key));
        } else {
            command = command.replace("[arguments]", "");
        }
        return command;
    }

    public void sendUsage(CommandSender sender, String cmd) {
        String message = ChatColor.YELLOW + Language.getMessage("command.help.output.usage");
        message = message.replace("%usage%", getUsage(cmd));
        sender.sendMessage(message);
        sender.sendMessage(Language.getMessage("command.help.output.helpPageDescription").replace("[description]", Language.getMessage("command." + cmd + ".help.info")));
    }

    protected boolean help(CommandSender sender) {
        sender.sendMessage(Language.getMessage("command.help.output.title"));
        for (Entry<String, BottleCMD> m : commands.entrySet()) {
            String cmd = m.getKey();
            if (!hasCommandPermission(sender, cmd))
                continue;
            sender.sendMessage(Language.getMessage("command.help.output.cmdInfoFormat").replace("[command]", getUsage(cmd)).replace("[description]", Language.getMessage("command." + cmd + ".help.info")));
        }
        return true;
    }

    @CAnnotation(tab = { "playerName" })
    public boolean stats(final CommandSender sender, final String[] args) {

        if (!(sender instanceof Player) && args.length != 1)
            return false;

        if (args.length != 0 && args.length != 1) {
            sendUsage(sender, "stats");
            return false;
        }

        Player player = null;

        if (args.length == 0)
            player = (Player) sender;
        else if (Util.hasPermission(sender, "bottledexp.command.stats.others", true)) {
            player = Bukkit.getPlayer(args[0]);
        }

        if (player == null) {
            LC.info_NoPlayer.sendMessage(sender);
            return false;
        }

        // statistics for player
        List<String> msgList = Language.getMessageList("command.stats.info.feedback");
        for (String one : msgList) {
            one = one.replace("[xp]", "" + Calculations.getPlayerExperience(player))
                .replace("[level]", "" + player.getLevel())
                .replace("[xpdelta]", "" + Calculations.currentlevelxpdelta(player))
                .replace("[bottles]", "" + Calculations.xptobottles(Calculations.currentlevelxpdelta(player)));
            sender.sendMessage(one);
        }

        if (ConfigFile.useBottleMoney) {
            String msg = Language.getMessage("command.stats.info.Moneyfeedback").replace("[money]", "" + (ConfigFile.bottleCost * Calculations.currentlevelxpdelta(
                player)));
            sender.sendMessage(msg);
        }

        return true;
    }

    @CAnnotation(tab = { "nextLevel" })
    public boolean until(final CommandSender sender, final String[] args) {

        if (!(sender instanceof Player))
            return false;

        if (args.length != 1) {
            sendUsage(sender, "until");
            return false;
        }

        Player player = (Player) sender;

        int level = 0;

        try {
            level = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            LC.info_NoLessThan.sendMessage(sender, 0);
            return false;
        }

        if (level <= player.getLevel()) {
            sender.sendMessage(Language.getMessage("command.until.info.moreThan"));
        } else {
            int NeedXpToLevel = Calculations.levelToExp(level) - Calculations.getPlayerExperience(player);

            String msg = Language.getMessage("command.until.info.feedback")
                .replace("[xp]", "" + NeedXpToLevel)
                .replace("[bottles]", "" + Calculations.xptobottles(NeedXpToLevel))
                .replace("[level]", args[0]);
            sender.sendMessage(msg);

            if (ConfigFile.useBottleMoney) {
                msg = Language.getMessage("command.until.info.Moneyfeedback").replace("[money]", "" + (ConfigFile.bottleCost * Calculations.currentlevelxpdelta(
                    player)));
                sender.sendMessage(msg);
            }
        }
        return true;
    }

    @CAnnotation
    public boolean eblock(final CommandSender sender, final String[] args) {

        if (!(sender instanceof Player))
            return false;

        if (args.length != 0) {
            sendUsage(sender, "eblock");
            return false;
        }

        Player player = (Player) sender;

        Block block = player.getTargetBlock((Set<Material>) null, 10);
        Location loc = block.getLocation();

        EBlockInfo blocks = BottledExp.getEBlocks();

        EBlock b = blocks.getBlock(loc);
        if (b != null) {
            blocks.removeBlock(b);
            player.sendMessage(Language.getMessage("command.eblock.info.removed"));
            return false;
        }

        EBlock EB = new EBlock();

        EB.setWorld(loc.getWorld().getName());
        EB.setX(loc.getBlockX());
        EB.setY(loc.getBlockY());
        EB.setZ(loc.getBlockZ());

        blocks.addBlock(EB);

        BottledExp.getConfigManager().saveBlocks();

        player.sendMessage(Language.getMessage("command.eblock.info.created"));
        return true;
    }

    @CAnnotation(tab = { "60%%max" })
    public boolean get(final CommandSender sender, final String[] args) {

        if (args.length != 1 && args.length != 2) {
            sendUsage(sender, "get");
            return false;
        }

        int amount = 0;
        boolean max = false;

        String playerName = null;
        for (String one : args) {
            if (one.equalsIgnoreCase("max")) {
                max = true;
                continue;
            }
            if (amount == 0) {
                try {
                    amount = Integer.parseInt(one);
                    continue;
                } catch (NumberFormatException e) {
                }
            }
            playerName = one;
        }

        if (playerName != null && !playerName.equalsIgnoreCase(sender.getName()) && !Util.hasPermission(sender, "bottledexp.command.get.others", true)) {
            return false;
        }

        Player player = playerName == null && (sender instanceof Player) ? (Player) sender : Bukkit.getPlayer(playerName);

        if (player == null || !player.isOnline()) {
            LC.info_NoPlayer.sendMessage(sender);
            return false;
        }

        int currentxp = Calculations.getPlayerExperience(player);

        if (max) {

            if (!Util.hasPermission(player, "bottledexp.command.get.max", true))
                return false;

            amount = currentxp / ConfigFile.xpCost;
            if (ConfigFile.settingUseItems && Calculations.countItems(player, ConfigFile.settingConsumedItem) < amount) {
                player.sendMessage(Language.getMessage("command.get.info.NoItems"));
                return false;
            }

            if (ConfigFile.useVaultEcon && ConfigFile.useBottleMoney && Util.getBalance(player) / ConfigFile.moneyCost < 1) {
                player.sendMessage(Language.getMessage("command.get.info.NoMoney"));
                return false;
            }

            if (amount == 0) {
                player.sendMessage(Language.getMessage("command.get.info.noExp"));
                return false;
            }

            Util.giveBoottles(player, amount);

            return true;
        }

        if (amount < 1) {
            LC.info_NoLessThan.sendMessage(sender, 0);
            return false;
        }

        Util.giveBoottles(player, amount);

        return true;
    }

    @CAnnotation(tab = { "60%%3L%%max" })
    public boolean store(final CommandSender sender, final String[] args) {

        if (!(sender instanceof Player))
            return false;

        if (args.length < 1 || args.length > 2) {
            sendUsage(sender, "store");
            return false;
        }

        Player player = (Player) sender;

        int take = 0;
        boolean max = false;
        boolean levels = false;

        String value = args[0];

        if (args[0].toLowerCase().endsWith("l")) {
            levels = true;
            value = value.substring(0, value.length() - 1);
        }

        if (args[0].equals("max")) {
            if (!Util.hasPermission(player, "bottledexp.command.store.max", true))
                return true;
            max = true;
        } else {
            try {
                take = Integer.parseInt(value);
            } catch (Throwable e) {
            }
        }

        if (levels && take > player.getLevel())
            take = player.getLevel();

        if (levels) {
            int expTo1 = Calculations.levelToExp(player.getLevel() - take);
            int expTo2 = Calculations.levelToExp(player.getLevel());
            if (take == player.getLevel())
                take = Calculations.getPlayerExperience(player);
            else
                take = expTo2 - expTo1;
        }

        if (max) {
            take = Calculations.getPlayerExperience(player);
        }

        if (take < ConfigFile.StoreMinimalAmount || player.getLevel() == 0 && player.getExp() <= 0) {
            player.sendMessage(Language.getMessage("command.get.info.noExp"));
            return true;
        }

        int give = (int) Math.floor(Double.valueOf(take) / (Double.valueOf(ConfigFile.xpCost) / Double.valueOf(ConfigFile.xpEarn)));

        if (give <= 0) {
            sendUsage(sender, "store");
            return true;
        }

        if (ConfigFile.settingUseItems && Calculations.countItems(player, ConfigFile.settingConsumedItem) < 1) {
            player.sendMessage(Language.getMessage("command.get.info.NoItems"));
            return true;
        }

        if (ConfigFile.useVaultEcon && Util.getBalance(player) < ConfigFile.moneyCost) {
            player.sendMessage(Language.getMessage("command.get.info.NoMoney"));
            return true;
        }

        if (take <= 0) {
            player.sendMessage(Language.getMessage("command.get.info.noExp"));
            return true;
        }

        Util.giveStoredBoottle(player, take, give);

        return true;
    }

    @CAnnotation(tab = { "playerName", "%%10" })
    public boolean give(final CommandSender sender, final String[] args) {

        if (!(sender instanceof Player))
            return false;

        if (args.length != 2) {
            sendUsage(sender, "give");
            return false;
        }

        Player player = (Player) sender;

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            LC.info_NoPlayer.sendMessage(player);
            return false;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            player.sendMessage(Language.getMessage("Self"));
            return false;
        }

        int exp;

        try {
            exp = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            LC.info_NoLessThan.sendMessage(sender, 0);
            return false;
        }

        if (exp > Calculations.getPlayerExperience(player)) {
            Language.getMessage("command.give.info.noExp");
            return false;
        }

        if (exp < 1) {
            LC.info_NoLessThan.sendMessage(sender, 0);
            return false;
        }

        int giverExp = Calculations.getPlayerExperience(player) - exp;
        int expToReceive = ((exp * (100 - ConfigFile.LostDurringTransfer)) / 100);
        int lostExp = exp - expToReceive;
        int receiversExp = Calculations.getPlayerExperience(target) + expToReceive;

        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);
        player.giveExp(giverExp);

        target.setLevel(0);
        target.setExp(0);
        target.setTotalExperience(0);
        target.giveExp(receiversExp);

        String msg = Language.getMessage("command.give.info.sender").replace("[amount]", args[1])
            .replace("[name]", target.getName())
            .replace("[lost]", String.valueOf(lostExp));
        player.sendMessage(msg);

        msg = Language.getMessage("command.give.info.receiver").replace("[amount]", String.valueOf(expToReceive))
            .replace("[name]", sender.getName())
            .replace("[lost]", String.valueOf(lostExp));
        target.sendMessage(msg);

        return true;
    }

    @CAnnotation
    public boolean reload(final CommandSender sender, final String[] args) {
        BottledExp.getConfigManager().reload(sender, true);
        return true;
    }
}
