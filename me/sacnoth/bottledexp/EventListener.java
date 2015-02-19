package me.sacnoth.bottledexp;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExpBottleEvent(ExpBottleEvent event) {
		event.setExperience(BottledExp.xpEarn);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantItem(PlayerInteractEvent event) {
		if (BottledExp.BlockInteractionUse) {
			String click = "RIGHT_CLICK_BLOCK";
			if (!BottledExp.BlockInteractionUseRightClick)
				click = "LEFT_CLICK_BLOCK";
			if (event.getPlayer().isSneaking() && event.getAction().toString().equalsIgnoreCase(click) && event.getClickedBlock().getTypeId() == BottledExp.BlockInteractionBlockId && event.getPlayer().getItemInHand().getTypeId() == BottledExp.BlockInteractionHandItemId) {
				Player player = event.getPlayer();
				Bukkit.dispatchCommand(player, "bottle " + (BottledExp.BlockInteractionGiveEveryTime * BottledExp.BlockInteractionMultiplayer));
			}else if (event.getAction().toString().equalsIgnoreCase(click) && event.getClickedBlock().getTypeId() == BottledExp.BlockInteractionBlockId && event.getPlayer().getItemInHand().getTypeId() == BottledExp.BlockInteractionHandItemId) {
				Player player = event.getPlayer();
				Bukkit.dispatchCommand(player, "bottle " + BottledExp.BlockInteractionGiveEveryTime);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantItem(EnchantItemEvent event) {
		if (event.isCancelled()) {
			return;
		}
		int EnchantLevel;
		Player player = event.getEnchanter();
		int leveltoleave;
		int newlevelpercentagexp;
		double percentage;
		int PlayerLevel = player.getLevel();
		int levelxp;
		int xptoleave;
		int takenxp;
		int getExpLevelCost = event.getExpLevelCost();

		if (BottledExp.UseThreeButtonEnchant) {
			EnchantLevel = event.whichButton() + 1;
		} else {
			EnchantLevel = getExpLevelCost - event.whichButton() - 1;
		}
		leveltoleave = PlayerLevel - EnchantLevel;
		levelxp = Calculations.getPlayerExperience(player) - Calculations.levelToExp(PlayerLevel);
		percentage = (double) (levelxp * 100 / Calculations.deltaLevelToExp(PlayerLevel));
		newlevelpercentagexp = (int) (Calculations.deltaLevelToExp(leveltoleave) * percentage / 100);
		if (BottledExp.UseThreeButtonEnchant) {
			xptoleave = Calculations.levelToExp(leveltoleave) + newlevelpercentagexp;
			takenxp = Calculations.getPlayerExperience(player) - xptoleave;
			// don't do anything, let Minecraft to do dirty job
		} else {
			xptoleave = Calculations.levelToExp(PlayerLevel - getExpLevelCost) + newlevelpercentagexp;
			takenxp = Calculations.getPlayerExperience(player) - xptoleave;
			player.setLevel(0);
			player.setExp(0);
			player.setLevel(leveltoleave);
			player.giveExp(newlevelpercentagexp);
			event.setExpLevelCost(0);
		}
		String sentence = BottledExp.langEnchant.replace("{xp}", String.valueOf(takenxp));
		player.sendMessage(ChatColor.GREEN + sentence);

		// Beta enchantment output
		if (BottledExp.ShowEnchant) {
			Map<Enchantment, Integer> enchantmentName = event.getEnchantsToAdd();
			int i = event.getEnchantsToAdd().size();
			String enchantString = "";
			String enchantStringFull = "";
			for (Entry<Enchantment, Integer> entry : enchantmentName.entrySet()) {
				i--;
				enchantString = String.valueOf(entry.getKey());
				Integer enchantlevel = entry.getValue();
				enchantString = Calculations.codeCleaning(enchantString);
				enchantString = ChatColor.DARK_GREEN + Calculations.toSentenceCase(enchantString) + ": " + ChatColor.DARK_AQUA + enchantlevel + ChatColor.DARK_GREEN;
				if (i >= 2)
					enchantStringFull = enchantStringFull + enchantString + ", ";
				else if (i >= 1)
					enchantStringFull = enchantStringFull + enchantString + " and ";
				else
					enchantStringFull = enchantStringFull + enchantString;
			}
			player.sendMessage(ChatColor.DARK_GREEN + enchantStringFull);
		}
	}
}