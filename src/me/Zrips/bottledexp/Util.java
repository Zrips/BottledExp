package me.Zrips.bottledexp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.NBT.CMINBT;

public class Util extends BottledExp {

    public static BottledExp plugin;
    public static Map<String, Long> DamageTimer = new HashMap<String, Long>();

    public static final String StoredBottledExp = "StoredBottledExp";

    public Util(BottledExp plugin) {
        Util.plugin = plugin;
    }

    public static boolean hasPermission(CommandSender sender, String permision, Boolean output) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (player.hasPermission(permision)) {
            return true;
        }
        if (output) {
            LC.info_NoPermission.sendMessage(player);
            LC.info_NoPermission.sendMessage(Bukkit.getServer().getConsoleSender());
        }

        return false;
    }

    public static double getBalance(Player player) {
        return BottledExp.economy.getBalance(player);
    }

    public static void withdrawMoney(Player player, double price) {
        BottledExp.economy.withdrawPlayer(player, price);
    }

    public static void giveStoredBottle(Player player, int take, int give, int bottleCount) {

        bottleCount = CMINumber.clamp(bottleCount, 1, ConfigFile.StoreMaxBottles);

        double perBottle = give / (double) bottleCount;

        if (perBottle < 1) {
            perBottle = 1;
            bottleCount = give;
        }

        int currentxp = CMIExp.getPlayerExperience(player);
        if (currentxp < take) {
            CMIMessages.sendMessage(player, Language.getMessage("command.get.info.noExp"));
            return;
        }

        boolean money = false;
        if (BottledExp.useVaultEcon && ConfigFile.useBottleMoney) {
            if (getBalance(player) > ConfigFile.moneyCost) {
                money = true;
            } else {
                CMIMessages.sendMessage(player, Language.getMessage("command.get.info.NoMoney"));
                return;
            }
        }

        boolean consumeItems = false;
        if (ConfigFile.settingUseItems) {
            consumeItems = Calculations.checkInventory(player, ConfigFile.settingConsumedItem, 1);
            if (!consumeItems) {
                CMIMessages.sendMessage(player, Language.getMessage("command.get.info.NoItems"));
                return;
            }
        }

        CMIExp.setTotalExperience(player, CMINumber.clamp(currentxp - take, 0, Integer.MAX_VALUE));

        int reminder = give - (((int) perBottle) * bottleCount) + ((int) perBottle);

        if (reminder == 0) {
            generateItems(player, (int) perBottle, bottleCount);
        } else if (reminder > 0) {
            generateItems(player, (int) perBottle, bottleCount - 1);
            generateItems(player, reminder, 1);
        }

        if (money && ConfigFile.useBottleMoney) {
            Util.withdrawMoney(player, ConfigFile.moneyCost);
            CMIMessages.sendMessage(player, Language.getMessage("command.get.info.Cost").replace("[cost]", String.valueOf(ConfigFile.moneyCost * 1)));
        }

        if (consumeItems && !Calculations.consumeItem(player, ConfigFile.settingConsumedItem, 1)) {
            CMIMessages.sendMessage(player, Language.getMessage("command.get.info.NoItems"));
            return;
        }

        CMIMessages.sendMessage(player, Language.getMessage("command.store.info.converted").replace("[exp]", String.valueOf(give)));
    }

    private static void generateItems(Player player, int perBottle, int bottleCount) {

        if (bottleCount <= 0)
            return;

        PlayerInventory inventory = player.getInventory();

        ItemStack item = CMIMaterial.EXPERIENCE_BOTTLE.newItemStack(1);

        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();

        int level = CMIExp.expToLevel(perBottle, 10000);

        lore.addAll(Arrays.asList(Language.getMessage("Store.BottleLore").replace("[exp]", String.valueOf(perBottle)).replace("[level]", String.valueOf(level)).replace("[lvl]", String.valueOf(level))
            .split("\\\\n")));
        meta.setDisplayName(Language.getMessage("Store.Name").replace("[exp]", String.valueOf(perBottle)).replace("[level]", String.valueOf(level)).replace("[lvl]", String.valueOf(level)));
        meta.setLore(lore);
        item.setItemMeta(meta);
        item.setAmount(bottleCount);

        CMINBT nbt = new CMINBT(item);
        item = (ItemStack) nbt.setInt(StoredBottledExp, perBottle);

        HashMap<Integer, ItemStack> leftoverItems = addItem(inventory, item);

        if (leftoverItems.isEmpty())
            return;

        for (Entry<Integer, ItemStack> one : leftoverItems.entrySet()) {
            Location loc = player.getLocation();
            Vector direction = loc.getDirection().clone();
            direction.multiply(0.4);
            player.getWorld().dropItem(loc.clone().add(0, 1, 0).add(direction), one.getValue());
        }
    }

    private static HashMap<Integer, ItemStack> addItem(Inventory inventory, ItemStack... items) {
        List<ItemStack> itemsList = new ArrayList<ItemStack>();
        if (items == null)
            return new HashMap<Integer, ItemStack>();

        for (ItemStack item : items) {
            int maxStackSize = item.getMaxStackSize();
            int amount = item.getAmount();

            while (amount > 0) {
                int splitAmount = amount < maxStackSize ? amount : maxStackSize;
                ItemStack splitItem = item.clone();
                splitItem.setAmount(splitAmount);
                itemsList.add(splitItem);
                amount -= splitAmount;
            }
        }

        if (!itemsList.isEmpty())
            return inventory.addItem(itemsList.toArray(new ItemStack[itemsList.size()]));

        return new HashMap<Integer, ItemStack>();
    }

    public static void giveBoottles(Player player, int amount) {

        int currentxp = CMIExp.getPlayerExperience(player);
        if (currentxp < (long) (amount) * ConfigFile.xpCost) {
            CMIMessages.sendMessage(player, Language.getMessage("command.get.info.noExp"));
            return;
        }

        boolean money = false;
        if (BottledExp.useVaultEcon && ConfigFile.useBottleMoney) {
            if (getBalance(player) >= ConfigFile.moneyCost * amount) {
                money = true;
            } else {
                if (getBalance(player) / ConfigFile.moneyCost < 1) {
                    CMIMessages.sendMessage(player, Language.getMessage("command.get.info.NoMoney"));
                    return;
                }
                amount = (int) (getBalance(player) / ConfigFile.moneyCost);
                money = true;
            }
        }

        boolean consumeItems = false;
        if (ConfigFile.settingUseItems) {
            consumeItems = Calculations.checkInventory(player, ConfigFile.settingConsumedItem, amount * ConfigFile.amountConsumed);
            if (!consumeItems) {
                CMIMessages.sendMessage(player, Language.getMessage("command.get.info.NoItems"));
                return;
            }
        }

        PlayerInventory inventory = player.getInventory();

        ItemStack items = CMIMaterial.EXPERIENCE_BOTTLE.newItemStack(amount);

        int stackCount = (int) Math.ceil(items.getAmount() / (double) items.getType().getMaxStackSize());

        ItemStack[] stacks = new ItemStack[stackCount];

        for (int i = 0; i < stackCount; i++) {
            ItemStack clone = items.clone();
            if (i < stackCount - 1) {
                clone.setAmount(items.getType().getMaxStackSize());
            } else {

                if (items.getAmount() % items.getType().getMaxStackSize() == 0)
                    clone.setAmount(items.getType().getMaxStackSize());
                else
                    clone.setAmount(items.getAmount() % items.getType().getMaxStackSize());
            }
            stacks[i] = clone;
        }

        HashMap<Integer, ItemStack> leftoverItems = addItem(inventory, stacks);

        CMIExp.setTotalExperience(player, currentxp - (amount * ConfigFile.xpCost));

        if (!leftoverItems.isEmpty()) {
            for (Entry<Integer, ItemStack> one : leftoverItems.entrySet()) {
                Location loc = player.getLocation();
                Vector direction = loc.getDirection().clone();
                direction.multiply(0.4);
                player.getWorld().dropItem(loc.clone().add(0, 1, 0).add(direction), one.getValue());
            }
        }

        if (money && ConfigFile.useBottleMoney) {
            Util.withdrawMoney(player, ConfigFile.moneyCost * amount);
            CMIMessages.sendMessage(player, Language.getMessage("command.get.info.Cost").replace("[cost]", String.valueOf(ConfigFile.moneyCost * amount)));
        }

        if (consumeItems) {
            if (!Calculations.consumeItem(player, ConfigFile.settingConsumedItem, amount * ConfigFile.amountConsumed)) {
                CMIMessages.sendMessage(player, Language.getMessage("command.get.info.NoItems"));
                return;
            }
        }

        CMIMessages.sendMessage(player, Language.getMessage("command.get.info.Order").replace("[bottles]", String.valueOf(amount)));
    }
}
