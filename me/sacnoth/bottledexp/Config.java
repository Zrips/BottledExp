package me.sacnoth.bottledexp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	private BottledExp plugin;

	public Config(BottledExp plugin) {
		this.plugin = plugin;
	}

	public void load() {
		final FileConfiguration config = plugin.getConfig();

		config.addDefault("bottle.xpCost", 10);
		BottledExp.xpCost = config.getInt("bottle.xpCost");
		config.set("bottle.xpCost", BottledExp.xpCost);

		config.addDefault("bottle.xpEarn", 10);
		BottledExp.xpEarn = config.getInt("bottle.xpEarn");
		config.set("bottle.xpEarn", BottledExp.xpEarn);

		config.addDefault("bottle.useItems", true);
		BottledExp.settingUseItems = config.getBoolean("bottle.useItems");
		config.set("bottle.useItems", BottledExp.settingUseItems);

		config.addDefault("bottle.consumedItem", 374);
		BottledExp.settingConsumedItem = config.getInt("bottle.consumedItem");
		config.set("bottle.consumedItem", BottledExp.settingConsumedItem);
		
		config.addDefault("bottle.amountConsumed", 1);
		BottledExp.amountConsumed = config.getInt("bottle.amountConsumed");
		config.set("bottle.amountConsumed", BottledExp.amountConsumed);
		
		config.addDefault("bottle.useMoney", false);
		BottledExp.useVaultEcon = config.getBoolean("bottle.useMoney");
		config.set("bottle.useMoney", BottledExp.useVaultEcon);
		
		config.addDefault("bottle.moneyCost", 100);
		BottledExp.moneyCost = config.getDouble("bottle.moneyCost");
		config.set("bottle.moneyCost", BottledExp.moneyCost);
		
		config.addDefault("bottle.useBottleMoney", false);
		BottledExp.useBottleMoney = config.getBoolean("bottle.useBottleMoney");
		config.set("bottle.useBottleMoney", BottledExp.useBottleMoney);
		
		config.addDefault("bottle.bottleCost", 3.25);
		BottledExp.bottleCost = config.getDouble("bottle.bottleCost");
		config.set("bottle.bottleCost", BottledExp.bottleCost);
		
		BottledExp.errAmount = Calculations.LangConfig(config, "language.errAmount", "&4The amount has to be a number!", true);
		BottledExp.errXP = Calculations.LangConfig(config, "language.errXP", "&4You don't have enough XP!", true);
		BottledExp.errMoney = Calculations.LangConfig(config, "language.errMoney", "&4You don't have enough money!", true);
		BottledExp.langCurrentXP = Calculations.LangConfig(config, "language.currentXP", "&2You currently have &3{xp} &2xp and you are at lvl &3{level}&2!", true);
		BottledExp.langCurrentXP2 = Calculations.LangConfig(config, "language.currentXP2", "&2You still need &3{xpdelta} &2xp or &3{bottles} &2bottles for next level!", true);
		BottledExp.langOrder = Calculations.LangConfig(config, "language.order", "&2You have ordered: &3{bottles} &2bottles!", true);
		BottledExp.langRefund = Calculations.LangConfig(config, "language.refund", "&2Refund issued! Amount: &3{amount}", true);
		BottledExp.langItemConsumer = Calculations.LangConfig(config, "language.itemConsume", "&4You don''t have enough items!", true);
		BottledExp.langMoney = Calculations.LangConfig(config, "language.money", "&eTransaction cost: &3{cost}", true);
		BottledExp.langEnchant = Calculations.LangConfig(config, "language.enchant", "&2You have spend &3{xp} &2xp for this enchantment", true);
		BottledExp.langMorexp = Calculations.LangConfig(config, "language.morexp", "&4Players earn more XP through XP bottles than they cost!", true);
		BottledExp.langNoperm = Calculations.LangConfig(config, "language.noperm", "&4You don't have permission to do this!", true);
		BottledExp.langMorethan = Calculations.LangConfig(config, "language.morethan", "&4This should be more than your level!", true);
		BottledExp.langUntil = Calculations.LangConfig(config, "language.until", "&2You need &3{xp} &2xp or &3{bottles} &2bottles to reach &3{level} &2level", true);
		BottledExp.langPlzuse = Calculations.LangConfig(config, "language.plzuse", "&4Plz use /bottle until [amount]", true);
		BottledExp.langBottlecost = Calculations.LangConfig(config, "language.bottlecost", "&2This will gonna cost you &3{money}", true);
		
		
		if (BottledExp.xpEarn > BottledExp.xpCost) {
			BottledExp.log.warning(BottledExp.langMorexp);
		}

		plugin.saveConfig();
	}
	
	public void reload(CommandSender sender) {
		plugin.reloadConfig();
		final FileConfiguration config = plugin.getConfig();

		BottledExp.xpCost = config.getInt("bottle.xpCost");
		BottledExp.xpEarn = config.getInt("bottle.xpEarn");
		BottledExp.settingUseItems = config.getBoolean("bottle.useItems");
		BottledExp.settingConsumedItem = config.getInt("bottle.consumedItem");
		BottledExp.amountConsumed = config.getInt("bottle.amountConsumed");
		BottledExp.useVaultEcon = config.getBoolean("bottle.useMoney");
		BottledExp.moneyCost = config.getDouble("bottle.moneyCost");
		BottledExp.useBottleMoney = config.getBoolean("bottle.useBottleMoney");
		BottledExp.bottleCost = config.getDouble("bottle.bottleCost");
		BottledExp.errAmount = ChatColor.translateAlternateColorCodes('&', config.getString("language.errAmount"));
		BottledExp.errXP = ChatColor.translateAlternateColorCodes('&', config.getString("language.errXP"));
		BottledExp.errMoney = ChatColor.translateAlternateColorCodes('&', config.getString("language.errMoney"));
		BottledExp.langCurrentXP = ChatColor.translateAlternateColorCodes('&', config.getString("language.currentXP"));
		BottledExp.langCurrentXP2 = ChatColor.translateAlternateColorCodes('&', config.getString("language.currentXP2"));
		BottledExp.langOrder = ChatColor.translateAlternateColorCodes('&', config.getString("language.order"));
		BottledExp.langRefund = ChatColor.translateAlternateColorCodes('&', config.getString("language.refund"));
		BottledExp.langItemConsumer = ChatColor.translateAlternateColorCodes('&', config.getString("language.itemConsume"));
		BottledExp.langMoney = ChatColor.translateAlternateColorCodes('&', config.getString("language.money"));
		BottledExp.langEnchant = ChatColor.translateAlternateColorCodes('&', config.getString("language.enchant"));
		BottledExp.langMorexp = ChatColor.translateAlternateColorCodes('&', config.getString("language.morexp"));
		BottledExp.langNoperm = ChatColor.translateAlternateColorCodes('&', config.getString("language.noperm"));
		BottledExp.langMorethan = ChatColor.translateAlternateColorCodes('&', config.getString("language.morethan"));
		BottledExp.langUntil = ChatColor.translateAlternateColorCodes('&', config.getString("language.until"));
		BottledExp.langPlzuse = ChatColor.translateAlternateColorCodes('&', config.getString("language.plzuse"));

		if (BottledExp.xpEarn > BottledExp.xpCost) {
			sender.sendMessage(BottledExp.langMorexp);
		}

		sender.sendMessage(ChatColor.YELLOW + "XP-Cost: " + BottledExp.xpCost);
		sender.sendMessage(ChatColor.YELLOW + "XP-Earn: " + BottledExp.xpEarn);
		sender.sendMessage(ChatColor.YELLOW + "Use items: " + BottledExp.settingUseItems);
		sender.sendMessage(ChatColor.YELLOW + "Item used: " + BottledExp.settingConsumedItem);
		sender.sendMessage(ChatColor.YELLOW + "Amount used: " + BottledExp.amountConsumed);
		sender.sendMessage(ChatColor.YELLOW + "Use money: " + BottledExp.useVaultEcon);

		String sentence = BottledExp.langMoney.replace("{cost}", String.valueOf(BottledExp.moneyCost));		
		sender.sendMessage(ChatColor.YELLOW + sentence);

		sender.sendMessage(ChatColor.YELLOW + "Use bottle price: " + BottledExp.useBottleMoney);
		sender.sendMessage(ChatColor.YELLOW + "Bottle price: " + BottledExp.bottleCost);
	}
}