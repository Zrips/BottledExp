package me.Zrips.bottledexp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
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

    public static void giveStoredBoottle(Player player, int take, int give) {

	int currentxp = Calculations.getPlayerExperience(player);
	if (currentxp < take) {
	    player.sendMessage(Language.getMessage("command.get.info.noExp"));
	    return;
	}

	boolean money = false;
	if (BottledExp.useVaultEcon && ConfigFile.useBottleMoney) {
	    if (getBalance(player) > ConfigFile.moneyCost) {
		money = true;
	    } else {
		player.sendMessage(Language.getMessage("command.get.info.NoMoney"));
		return;
	    }
	}

	boolean consumeItems = false;
	if (ConfigFile.settingUseItems) {
	    consumeItems = Calculations.checkInventory(player, ConfigFile.settingConsumedItem, 1);
	    if (!consumeItems) {
		player.sendMessage(Language.getMessage("command.get.info.NoItems"));
		return;
	    }
	}

	PlayerInventory inventory = player.getInventory();

	ItemStack item = CMIMaterial.EXPERIENCE_BOTTLE.newItemStack(1);

	ItemMeta meta = item.getItemMeta();
	ArrayList<String> lore = new ArrayList<String>();

	int level = Calculations.expToLevel(give, 10000);

	lore.addAll(Arrays.asList(Language.getMessage("Store.BottleLore").replace("[exp]", String.valueOf(give)).replace("[level]", String.valueOf(level)).replace("[lvl]", String.valueOf(level)).split(
	    "\\\\n")));
	
	meta.setDisplayName(Language.getMessage("Store.Name").replace("[exp]", String.valueOf(give)).replace("[level]", String.valueOf(level)).replace("[lvl]", String.valueOf(level)));
	meta.setLore(lore);
	item.setItemMeta(meta);

	player.setTotalExperience(0);
	player.setLevel(0);
	player.setExp(0);
	player.setTotalExperience(0);
	int leaveWith = currentxp - take;
	leaveWith = leaveWith < 0 ? 0 : leaveWith;
	player.giveExp(leaveWith);

	CMINBT nbt = new CMINBT(item);
	item = (ItemStack) nbt.setInt(StoredBottledExp, give);

	HashMap<Integer, ItemStack> leftoverItems = inventory.addItem(item);

	if (!leftoverItems.isEmpty()) {
	    for (Entry<Integer, ItemStack> one : leftoverItems.entrySet()) {
		Location loc = player.getLocation();
		Vector direction = loc.getDirection().clone();
		direction.multiply(0.4);
		player.getWorld().dropItem(loc.clone().add(0, 1, 0).add(direction), one.getValue());
	    }
	}

	if (money && ConfigFile.useBottleMoney) {
	    Util.withdrawMoney(player, ConfigFile.moneyCost * 1);
	    player.sendMessage(Language.getMessage("command.get.info.Cost").replace("[cost]", String.valueOf(ConfigFile.moneyCost * 1)));
	}

	if (consumeItems) {
	    if (!Calculations.consumeItem(player, ConfigFile.settingConsumedItem, 1)) {
		player.sendMessage(Language.getMessage("command.get.info.NoItems"));
		return;
	    }
	}

	player.sendMessage(Language.getMessage("command.store.info.converted").replace("[exp]", String.valueOf(give)));
    }

    public static void giveBoottles(Player player, int amount) {

	int currentxp = Calculations.getPlayerExperience(player);
	if (currentxp < (long) (amount) * ConfigFile.xpCost) {
	    player.sendMessage(Language.getMessage("command.get.info.noExp"));
	    return;
	}

	boolean money = false;
	if (BottledExp.useVaultEcon && ConfigFile.useBottleMoney) {
	    if (getBalance(player) >= ConfigFile.moneyCost * amount) {
		money = true;
	    } else {
		if (getBalance(player) / ConfigFile.moneyCost < 1) {
		    player.sendMessage(Language.getMessage("command.get.info.NoMoney"));
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
		player.sendMessage(Language.getMessage("command.get.info.NoItems"));
		return;
	    }
	}

	PlayerInventory inventory = player.getInventory();

	ItemStack items = CMIMaterial.EXPERIENCE_BOTTLE.newItemStack(amount);
	HashMap<Integer, ItemStack> leftoverItems = inventory.addItem(items);
	player.setTotalExperience(0);
	player.setLevel(0);
	player.setExp(0);
	player.setTotalExperience(0);
	player.giveExp(currentxp - (amount * ConfigFile.xpCost));

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
	    player.sendMessage(Language.getMessage("command.get.info.Cost").replace("[cost]", String.valueOf(ConfigFile.moneyCost * amount)));
	}

	if (consumeItems) {
	    if (!Calculations.consumeItem(player, ConfigFile.settingConsumedItem, amount * ConfigFile.amountConsumed)) {
		player.sendMessage(Language.getMessage("command.get.info.NoItems"));
		return;
	    }
	}

	player.sendMessage(Language.getMessage("command.get.info.Order").replace("[bottles]", String.valueOf(amount)));

    }
}
