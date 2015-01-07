package me.sacnoth.bottledexp;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ExpBottleEvent;

public class EventListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExpBottleEvent(ExpBottleEvent event) {
		event.setExperience(BottledExp.xpEarn);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantItem(EnchantItemEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getEnchanter();
		int leveltoleave = player.getLevel() - (event.whichButton() + 1);
		int levelxp = BottledExp.getPlayerExperience(player)-Calculations.levelToExp(player.getLevel());
		float percentage = levelxp * 100/ Calculations.deltaLevelToExp(player.getLevel());
		int newlevelpercentagexp = (int) (Calculations.deltaLevelToExp(leveltoleave) * percentage / 100);
		int xptoleave = Calculations.levelToExp(leveltoleave)+ newlevelpercentagexp;
		int takenxp = BottledExp.getPlayerExperience(player)- xptoleave;

		//Beta enchantment output
		if (BottledExp.ShowEnchant){
			Map<Enchantment, Integer> enchantmentName = event.getEnchantsToAdd();
			int i = event.getEnchantsToAdd().size();
			String enchantString = "";
			String enchantStringFull = "";
			for (Entry<Enchantment, Integer> entry : enchantmentName.entrySet()) {
				i--;
				enchantString = String.valueOf(entry.getKey());
				Integer enchantlevel = entry.getValue();
				enchantString = Calculations.codeCleaning(enchantString);
				enchantString = ChatColor.DARK_GREEN +Calculations.toSentenceCase(enchantString) + ": "+ ChatColor.DARK_AQUA + enchantlevel+ ChatColor.DARK_GREEN;
				if (i >= 2)
					enchantStringFull = enchantStringFull + enchantString + ", ";
				else if (i >= 1)
					enchantStringFull = enchantStringFull + enchantString + " and ";
				else
					enchantStringFull = enchantStringFull + enchantString;
			}
			player.sendMessage(ChatColor.DARK_GREEN + enchantStringFull);
		}
		

		String sentence = BottledExp.langEnchant.replace("{xp}",String.valueOf(takenxp));

		player.sendMessage(ChatColor.GREEN + sentence);

		player.setTotalExperience(xptoleave);
		event.setExpLevelCost(0);
	}
}