package me.sacnoth.bottledexp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockInteractionEvent implements Listener{
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantItem(PlayerInteractEvent event) {
		
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
