package me.sacnoth.bottledexp;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class BottledExp extends JavaPlugin {
	static Logger log;
	private BottledExpCommandExecutor myExecutor;
	static int xpCost;
	static int xpEarn;
	static double bottleCost;
	static boolean usePermissions = false;
	static boolean useVaultEcon = true;
	static boolean useBottleMoney = true;
	static boolean useVaultPermissions = false;
	static boolean ShowEnchant = false;
	static PermissionManager pexPermissions;
	static Permission vaultPermissions;
	static String errAmount;
	static String errXP;
	static String errMoney;
	static String langCurrentXP;
	static String langCurrentXP2;
	static String langOrder;
	static String langRefund;
	static String langItemConsumer;
	static String langMoney;
	static String langEnchant;
	static String langMorexp;
	static String langNoperm;
	static String langMorethan;
	static String langUntil;
	static String langPlzuse;
	static String langBottlecost;
	static boolean settingUseItems;
	static int settingConsumedItem;
	static int amountConsumed;
	static double moneyCost;
	static Config config;
	public static Economy economy = null;

	public void onEnable() {
		log = this.getLogger();

		myExecutor = new BottledExpCommandExecutor(this);
		getCommand("bottle").setExecutor(myExecutor);
		
		getServer().getPluginManager().registerEvents(new EventListener(), this);

		config = new Config(this);
		config.load();

		if (!setupEconomy()) {
			log.info("Vault not found - Disabeling economy capabilities.");
			useVaultEcon = false;
		}

		log.info("You are now able to fill XP into Bottles");

		if (Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
			pexPermissions = PermissionsEx.getPermissionManager();
			usePermissions = true;
			log.info("Using PermissionsEx!");
		} else if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
			setupPermissions();
			useVaultPermissions = true;
			log.info("Using " + vaultPermissions.getName() + " via Vault.");
		} else {
			log.warning("Neither PEX nor Vault found, BottledExp will not work properly!");
		}
	}

	public void onDisable() {
		log.info("You are no longer able to fill XP into Bottles");
	}
	
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			vaultPermissions = permissionProvider.getProvider();
		}
		return (vaultPermissions != null);
	}

	@SuppressWarnings("deprecation")
	public static boolean checkPermission(String node, Player player) {
		if (usePermissions) {
			if (pexPermissions.has(player, node)) {
				return true;
			}
			player.sendMessage(ChatColor.RED+ BottledExp.langNoperm);
			return false;
		} else if (useVaultPermissions && vaultPermissions.isEnabled()) {
			if (vaultPermissions.playerHas(player.getWorld(), player.getName(),node)) {
				return true;
			}
			player.sendMessage(ChatColor.RED+ BottledExp.langNoperm);
			return false;
		}
		player.sendMessage(ChatColor.RED + "Neither PEX nor Vault found, BottledExp will not work properly!");
		return false;
	}


	public static int getPlayerExperience(Player player) {
		int bukkitExp = (Calculations.levelToExp(player.getLevel()) + (int) (Calculations.deltaLevelToExp(player.getLevel()) * player.getExp()));
		return bukkitExp;
	}

	@SuppressWarnings("deprecation")
	public static boolean checkInventory(Player player, int itemID, int amount) {
		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(itemID, amount)) {
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static boolean consumeItem(Player player, int itemID, int amount) {
		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(itemID, amount)) {
			ItemStack items = new ItemStack(itemID, amount);
			inventory.removeItem(items);
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public static int countItems(Player player, int itemID) {
		PlayerInventory inventory = player.getInventory();

		int amount = 0;
		ItemStack curItem;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			curItem = inventory.getItem(slot);
			if (curItem != null && curItem.getTypeId() == itemID)
				amount += curItem.getAmount();
		}
		return amount;
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	@SuppressWarnings("deprecation")
	public static double getBalance(Player player) {
		return BottledExp.economy.getBalance(player.getName());
	}

	@SuppressWarnings("deprecation")
	public static void withdrawMoney(Player player, double price) {
		BottledExp.economy.withdrawPlayer(player.getName(), price);
	}
}
