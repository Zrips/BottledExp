package me.Zrips.bottledexp.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.Zrips.bottledexp.BottledExp;
import net.Zrips.CMILib.Logs.CMIDebug;

public class TabComplete implements TabCompleter {

    private BottledExp plugin;

    public TabComplete(BottledExp plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completionList = new ArrayList<>();
        completionList = get(sender, command.getName(), args);
        Collections.sort(completionList);
        return completionList;
    }

    public enum TabAction {
        na, playername, worlds, gamemode, nextLevel;

        public static TabAction getAction(String name) {
            for (TabAction one : TabAction.values()) {
                if (one.name().equalsIgnoreCase(name))
                    return one;
            }
            return TabAction.na;
        }
    }

    public List<String> get(CommandSender sender, String command, String[] args) {
        List<String> completionList = new ArrayList<>();

        if (args.length == 1) {
            String PartOfCommand = args[0];
            List<String> temp = new ArrayList<String>();

            for (Entry<String, BottleCMD> BCmd : BottledExp.getCommandsManager().getCommands(sender).entrySet()) {
                temp.add(BCmd.getKey());
            }

            Player p = null;
            if (sender instanceof Player)
                p = (Player) sender;
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (p != null && !p.canSee(players))
                    continue;
                temp.add(players.getName());
            }
            StringUtil.copyPartialMatches(PartOfCommand, temp, completionList);
        }

        BottleCMD cmd = BottledExp.getCommandsManager().getCommand(args[0]);

        if (args.length <= 1)
            return completionList;

        for (int i = 1; i <= args.length; i++) {
            if (args.length == i + 1) {

                String PartOfCommand = args[i];

                String tabs = null;

                if (cmd.getAnnotation().tab().length >= args.length - 1) {
                    tabs = cmd.getAnnotation().tab()[args.length - 2];
                }

                if (tabs == null)
                    continue;

                List<TabAction> actions = new ArrayList<TabAction>();

                List<String> temp = new ArrayList<String>();

                if (tabs.contains("%%")) {
                    String[] split = tabs.split("%%");
                    for (String oneS : split) {
                        if (TabAction.getAction(oneS) != TabAction.na) {
                            actions.add(TabAction.getAction(oneS));
                            continue;
                        }
                        temp.add(oneS);
                    }
                } else {
                    if (TabAction.getAction(tabs) != null)
                        actions.add(TabAction.getAction(tabs));
                    else
                        temp.add(tabs);
                }

                for (TabAction action : actions)
                    switch (action) {
                    case na:
                        break;
                    case playername:
                        Player p = null;
                        if (sender instanceof Player)
                            p = (Player) sender;
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            if (p != null && !p.canSee(players))
                                continue;
                            temp.add(players.getName());
                        }
                        break;
                    case nextLevel:
                        p = null;
                        if (sender instanceof Player)
                            p = (Player) sender;

                        if (p != null)
                            temp.add(String.valueOf(p.getLevel() + 1));

                        break;
                    default:
                        break;

                    }

                StringUtil.copyPartialMatches(PartOfCommand, temp, completionList);
            }
        }

        return completionList;
    }
}
