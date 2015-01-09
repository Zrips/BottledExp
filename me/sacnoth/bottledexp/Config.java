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

		config.addDefault("bottle.useItems", false);
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

		config.addDefault("bottle.ShowEnchant", false);
		BottledExp.ShowEnchant = config.getBoolean("bottle.ShowEnchant");
		config.set("bottle.ShowEnchant", BottledExp.ShowEnchant);

		config.addDefault("bottle.UseThreeButtonEnchant", true);
		BottledExp.UseThreeButtonEnchant = config.getBoolean("bottle.UseThreeButtonEnchant");
		config.set("bottle.UseThreeButtonEnchant", BottledExp.UseThreeButtonEnchant);

		config.addDefault("bottle.give.UseGiveCommand", false);
		BottledExp.UseGiveCommand = config.getBoolean("bottle.give.UseGiveCommand");
		config.set("bottle.give.UseGiveCommand", BottledExp.UseGiveCommand);

		config.addDefault("bottle.give.LostDurringTransfer", 0);
		BottledExp.LostDurringTransfer = config.getInt("bottle.give.LostDurringTransfer");
		config.set("bottle.give.LostDurringTransfer", BottledExp.LostDurringTransfer);

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
		
		//Give command locale
		BottledExp.langGiveDisabled = Calculations.LangConfig(config, "language.give.giveDisabled", "&4Give command is disabled!", true);
		BottledExp.langnotOnline = Calculations.LangConfig(config, "language.give.notOnline", "&4Player is not online!", true);
		BottledExp.langyourSelf = Calculations.LangConfig(config, "language.give.yourSelf", "&4You cant send exp for yourself", true);
		BottledExp.langnotEnough = Calculations.LangConfig(config, "language.give.notEnough", "&4You dont have enough exp to give", true);
		BottledExp.langplzuse = Calculations.LangConfig(config, "language.give.plzuse", "&4Plz use /bottle give [nick] [amount]", true);
		BottledExp.langpositive = Calculations.LangConfig(config, "language.give.positive", "&4Plz give positive number", true);
		BottledExp.langsender = Calculations.LangConfig(config, "language.give.sender", "&2You just send &3{amount} &2xp to &3{name} &2and &3{lost} &2xp was lost during transfer", true);
		BottledExp.langreceiver = Calculations.LangConfig(config, "language.give.receiver", "&2You just got &3{amount} &2xp from &3{name} &2and &3{lost} &2xp was lost during transfer", true);

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
		BottledExp.ShowEnchant = config.getBoolean("bottle.ShowEnchant");
		BottledExp.UseThreeButtonEnchant = config.getBoolean("bottle.UseThreeButtonEnchant");
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
		
		//Give command locale and settings
		BottledExp.UseGiveCommand = config.getBoolean("bottle.give.UseGiveCommand");
		BottledExp.LostDurringTransfer = config.getInt("bottle.give.LostDurringTransfer");
		BottledExp.langGiveDisabled = ChatColor.translateAlternateColorCodes('&', config.getString("language.give.giveDisabled"));
		BottledExp.langnotOnline = ChatColor.translateAlternateColorCodes('&', config.getString("language.give.notOnline"));
		BottledExp.langyourSelf = ChatColor.translateAlternateColorCodes('&', config.getString("language.give.yourSelf"));
		BottledExp.langnotEnough = ChatColor.translateAlternateColorCodes('&', config.getString("language.give.notEnough"));
		BottledExp.langplzuse = ChatColor.translateAlternateColorCodes('&', config.getString("language.give.plzuse"));
		BottledExp.langpositive = ChatColor.translateAlternateColorCodes('&', config.getString("language.give.positive"));
		BottledExp.langsender = ChatColor.translateAlternateColorCodes('&', config.getString("language.give.sender"));
		BottledExp.langreceiver = ChatColor.translateAlternateColorCodes('&', config.getString("language.give.receiver"));

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
		sender.sendMessage(ChatColor.YELLOW + "Use three button enchant: " + BottledExp.UseThreeButtonEnchant);
	}
}