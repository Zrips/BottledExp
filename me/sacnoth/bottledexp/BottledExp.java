package me.sacnoth.bottledexp;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class BottledExp extends JavaPlugin {
	static Logger log;
	private BottledExpCommandExecutor myExecutor;
	static double bottleCost;
	static boolean usePermissions = false;
	static boolean useVaultEcon = true;
	static boolean useBottleMoney = true;
	static boolean useVaultPermissions = false;
	static boolean ShowEnchant = false;
	static boolean UseThreeButtonEnchant = true;
	static PermissionManager pexPermissions;
	static Permission vaultPermissions;
	static String errAmount, errXP, errMoney, langCurrentXP, langCurrentXP2,
			langOrder, langRefund, langItemConsumer, langMoney, langEnchant,
			langMorexp, langNoperm, langMorethan, langUntil, langPlzuse,
			langGivePlzUse, langBottlecost, langGiveDisabled, langnotOnline,
			langyourSelf, langnotEnough, langplzuse, langpositive, langsender,
			langreceiver;
	static boolean UseGiveCommand = true;
	static boolean settingUseItems, BlockInteractionUse,
			BlockInteractionUseRightClick;
	static int settingConsumedItem, amountConsumed, BlockInteractionBlockId,
			BlockInteractionHandItemId, BlockInteractionGiveEveryTime,
			BlockInteractionMultiplayer, LostDurringTransfer, xpCost, xpEarn;
	static double moneyCost;
	static Config config;
	static Recipes Recipes;
	public static Economy economy = null;

	public void onEnable() {
		log = this.getLogger();

		myExecutor = new BottledExpCommandExecutor(this);
		getCommand("bottle").setExecutor(myExecutor);

		getServer().getPluginManager().registerEvents(new EventListener(), this);
		config = new Config(this);
		config.load();
		
		Recipes = new Recipes(this);
		Recipes.Recipe();
		
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
			player.sendMessage(ChatColor.RED + BottledExp.langNoperm);
			return false;
		} else if (useVaultPermissions && vaultPermissions.isEnabled()) {
			if (vaultPermissions.playerHas(player.getWorld(), player.getName(), node)) {
				return true;
			}
			player.sendMessage(ChatColor.RED + BottledExp.langNoperm);
			return false;
		}
		player.sendMessage(ChatColor.RED + "Neither PEX nor Vault found, BottledExp will not work properly!");
		return false;
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
