package me.sacnoth.bottledexp;

import org.bukkit.ChatColor;
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
		int leveltoleave= player.getLevel()-(event.whichButton()+1);
		int levelxp=player.getTotalExperience()-Calculations.levelToExp(player.getLevel());
		float percentage=levelxp*100/Calculations.deltaLevelToExp(player.getLevel());
		int newlevelpercentagexp=(int)(Calculations.deltaLevelToExp(leveltoleave)*percentage/100);
		int xptoleave= Calculations.levelToExp(leveltoleave)+newlevelpercentagexp;
		int takenxp= player.getTotalExperience()-xptoleave;

		String sentence = BottledExp.langEnchant.replace("{xp}", String.valueOf(takenxp));
		
		player.sendMessage(ChatColor.GREEN + sentence);

		player.setTotalExperience(xptoleave);
		event.setExpLevelCost(0);
	}
}