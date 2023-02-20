package me.Zrips.bottledexp;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;

public class BlockInteractionEvent implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItem(PlayerInteractEvent event) {

	if (!BottledExp.getConfigManager().BlockInteractionUse)
	    return;

	Player player = event.getPlayer();
	Action click = Action.RIGHT_CLICK_BLOCK;
	if (!ConfigFile.BlockInteractionUseRightClick)
	    click = Action.LEFT_CLICK_BLOCK;

	try {
	    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && event.getHand() != EquipmentSlot.HAND)
		return;
	} catch (Exception e) {
	}

	if (event.getAction() == click && CMIMaterial.get(event.getClickedBlock()) == ConfigFile.BlockInteractionBlock &&
	    CMIMaterial.get(event.getPlayer().getItemInHand()) == ConfigFile.BlockInteractionHandItem) {

	    if (!player.hasPermission("bottledexp.interact"))
		return;

	    int amount = ConfigFile.BlockInteractionGiveEveryTime;

	    if (player.isSneaking())
		amount = ConfigFile.BlockInteractionGiveEveryTime * ConfigFile.BlockInteractionMultiplayer;

	    Util.giveBoottles(player, amount);
	    event.setCancelled(true);
	}
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteractBlock(PlayerInteractEvent event) {

	if (!BottledExp.getConfigManager().CustomBlockInteractionUse)
	    return;

	Player player = event.getPlayer();

	Action click = Action.RIGHT_CLICK_BLOCK;
	if (!ConfigFile.CustomBlockInteractionUseRightClick)
	    click = Action.LEFT_CLICK_BLOCK;

	if (BottledExp.getEBlocks().getAllBlocks().isEmpty())
	    return;

	Block block = event.getClickedBlock();

	if (block == null)
	    return;

	try {
	    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && event.getHand() != EquipmentSlot.HAND)
		return;
	} catch (Exception e) {
	}

	Location loc = block.getLocation();

	if (loc == null)
	    return;

	EBlock b = BottledExp.getEBlocks().getBlock(loc);

	if (b == null)
	    return;

	if (event.getAction() == click && CMIMaterial.get(event.getPlayer().getItemInHand()) == ConfigFile.CustomBlockInteractionHandItem) {

	    if (!player.hasPermission("bottledexp.custominteract"))
		return;

	    int amount = ConfigFile.CustomBlockInteractionGiveEveryTime;

	    if (player.isSneaking())
		amount = ConfigFile.CustomBlockInteractionGiveEveryTime * ConfigFile.CustomBlockInteractionMultiplayer;

	    Util.giveBoottles(player, amount);
	    event.setCancelled(true);
	}
    }
}
