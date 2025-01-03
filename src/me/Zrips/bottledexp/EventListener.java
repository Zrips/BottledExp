package me.Zrips.bottledexp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Util.CMIVersionChecker;

public class EventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVillagerTrade(PlayerInteractEntityEvent event) {
        if (!ConfigFile.DisableVillagerExpTrade)
            return;
        if (event.getRightClicked().getType() != EntityType.VILLAGER)
            return;

        if (BottledExp.getInstance().isLimitedCompatability())
            return;

        Player player = event.getPlayer();
        try {
            BottledExp.getNms().disableTrade(event.getRightClicked(), player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void dispenserFireEvent(BlockDispenseEvent event) {

        if (event.isCancelled())
            return;
        if (!ConfigFile.DisableDispensers)
            return;

        ItemStack item = event.getItem();

        if (!CMIMaterial.get(event.getBlock()).equals(CMIMaterial.DISPENSER))
            return;

        if (!CMIMaterial.get(item).equals(CMIMaterial.EXPERIENCE_BOTTLE))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExpBottleEvent(ExpBottleEvent event) {

        if (event.getExperience() == 0)
            return;

        event.setExperience(ConfigFile.xpEarn);
        try {
            ItemStack item = event.getEntity().getItem();

            CMINBT nbt = new CMINBT(item);

            if (nbt.hasNBT(Util.StoredBottledExp) && nbt.getInt(Util.StoredBottledExp) != null) {
                Integer exp = nbt.getInt(Util.StoredBottledExp);
                if (exp > 0)
                    event.setExperience(exp);
            } else {

                // Old way, could be removed at some point
                if (!item.hasItemMeta())
                    return;
                ItemMeta meta = item.getItemMeta();
                if (!meta.hasDisplayName())
                    return;
                if (!meta.getDisplayName().equalsIgnoreCase(Language.getMessage("Store.Name")))
                    return;
                List<String> lore = item.getItemMeta().getLore();
                if (lore.size() != 2)
                    return;
                int exp = 0;
                try {
                    exp = Integer.parseInt(ChatColor.stripColor(lore.get(1)));
                } catch (NumberFormatException e) {
                    return;
                }
                if (exp < 1)
                    return;
                event.setExperience(exp);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!ConfigFile.DisableMobSpawnerExp)
            return;

        Block block = event.getBlock();
        if (!CMIMaterial.get(block).equals(CMIMaterial.SPAWNER))
            return;

        event.setExpToDrop(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onitemCraft(CraftItemEvent event) {
        if (!ConfigFile.CraftExpContainer)
            return;

        switch (event.getAction()) {
        case NOTHING:
        case PLACE_ONE:
        case PLACE_ALL:
        case PLACE_SOME:
            return;
        default:
            break;
        }
        if (!(event.getInventory() instanceof CraftingInventory) || !event.getSlotType().equals(SlotType.RESULT))
            return;

        ItemStack resultStack = event.getRecipe().getResult();

        if (resultStack == null)
            return;

        if (!CMIMaterial.get(resultStack).equals(CMIMaterial.EXPERIENCE_BOTTLE))
            return;

        ItemStack item = event.getCurrentItem();

        if (!item.hasItemMeta())
            return;

        CMINBT nbt = new CMINBT(item);

        if (!nbt.hasNBT(Util.StoredBottledExp))
            return;

        Player player = (Player) event.getWhoClicked();

        if (!Util.hasPermission(player, "bottledexp.expcontainer.craft", true)) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) {
            event.setCancelled(true);
            return;
        }

        if (player.getInventory().firstEmpty() == -1 && event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        int exp = CMIExp.getPlayerExperience((Player) event.getView().getPlayer());

        if (exp == 0) {
            event.setCancelled(true);
            return;
        }

        nbt = new CMINBT(resultStack);
        resultStack = (ItemStack) nbt.setInt(Util.StoredBottledExp, exp);

        ItemMeta meta = resultStack.getItemMeta();

        ArrayList<String> lore = new ArrayList<String>();

        int level = CMIExp.expToLevel(exp, 10000);

        lore.addAll(Arrays.asList(Language.getMessage("Store.BottleLore").replace("[exp]", String.valueOf(exp)).replace("[level]", String.valueOf(level)).replace("[lvl]", String.valueOf(level)).split(
            "\\\\n")));
        meta.setDisplayName(Language.getMessage("Store.Name").replace("[exp]", String.valueOf(exp)).replace("[level]", String.valueOf(level)).replace("[lvl]", String.valueOf(level)));

        meta.setLore(lore);

        resultStack.setItemMeta(meta);

        event.getInventory().setResult(resultStack);

        if (!(event.getWhoClicked() instanceof Player))
            return;

        if (!event.isLeftClick() && !event.isRightClick())
            return;

        CMIExp.setTotalExperience(player, 0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExpBottleThrow(PlayerInteractEvent event) {

        if (event.getAction().equals(Action.PHYSICAL))
            return;

        Player player = event.getPlayer();

        boolean mainHand = true;

        ItemStack item = CMIItemStack.getItemInMainHand(player);

        if (item == null || CMIMaterial.isAir(item.getType())) {
            try {
                if (event.getHand().equals(EquipmentSlot.HAND))
                    return;
            } catch (Throwable e) {
            }

            mainHand = false;

            item = CMIItemStack.getItemInOffHand(player);
            if (item == null || CMIMaterial.isAir(item.getType()))
                return;
        } else {

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                if (new CMINBT(item).hasNBT(Util.StoredBottledExp))
                    event.setCancelled(true);
                return;
            }

            if (!new CMINBT(item).hasNBT(Util.StoredBottledExp)) {
                mainHand = false;
                item = CMIItemStack.getItemInOffHand(player);

                if (item == null || CMIMaterial.isAir(item.getType()))
                    return;
            } else
                try {
                    if (event.getHand().equals(EquipmentSlot.OFF_HAND))
                        return;
                } catch (Throwable e) {
                }
        }

        int exp = 0;

        if (CMIMaterial.isAir(item.getType()) || !CMIMaterial.get(item).equals(CMIMaterial.EXPERIENCE_BOTTLE))
            return;

        CMINBT nbt = new CMINBT(item);

        if (nbt.hasNBT(Util.StoredBottledExp) && nbt.getInt(Util.StoredBottledExp) != null) {
            exp = nbt.getInt(Util.StoredBottledExp);
        } else {
            if (!item.hasItemMeta())
                return;

            if (!item.getItemMeta().hasDisplayName())
                return;

            if (!item.getItemMeta().hasLore())
                return;

            if (!item.getItemMeta().getDisplayName().equalsIgnoreCase(Language.getMessage("Store.Name")))
                return;

            List<String> lore = item.getItemMeta().getLore();

            if (lore.size() != 2)
                return;

            try {
                exp = Integer.parseInt(ChatColor.stripColor(lore.get(1)));
            } catch (NumberFormatException e) {
                return;
            }
        }

        if (exp <= 0)
            return;

        if (item.getAmount() > 1)
            item.setAmount(item.getAmount() - 1);
        else {
            if (mainHand)
                CMIItemStack.setItemInMainHand(player, null);
            else
                CMIItemStack.setItemInOffHand(player, null);
        }
        player.updateInventory();
        int newexp = CMIExp.getPlayerExperience(player) + exp;

        CMIExp.setTotalExperience(player, newexp);

        event.setCancelled(true);

        player.sendMessage(Language.getMessage("GotExp").replace("[xp]", "" + exp));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onitemCraft(PrepareItemCraftEvent e) {

        if (!ConfigFile.CraftExpContainer)
            return;

        ItemStack[] items = e.getInventory().getContents();
        int found = 0;

        for (int i = 1; i < items.length; i++) {
            if (items[i].getType() == Material.GLASS_BOTTLE)
                found++;
        }

        if (found != 1)
            return;

        ItemStack item = e.getInventory().getResult();
        if (item == null)
            return;

        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return;

        ArrayList<String> lore = new ArrayList<String>();

        int exp = CMIExp.getPlayerExperience((Player) e.getView().getPlayer());

        if (exp == 0) {
            e.getInventory().setResult(null);
            return;
        }

        int level = CMIExp.expToLevel(exp, 10000);

        meta.setDisplayName(Language.getMessage("Store.Name").replace("[exp]", String.valueOf(exp)).replace("[level]", String.valueOf(level)).replace("[lvl]", String.valueOf(level)));

        lore.addAll(Arrays.asList(Language.getMessage("Store.BottleLore").replace("[exp]", String.valueOf(exp)).replace("[level]", String.valueOf(level)).replace("[lvl]", String.valueOf(level)).split(
            "\\\\n")));

        meta.setLore(lore);
        item.setItemMeta(meta);

        CMINBT nbt = new CMINBT(item);
        item = (ItemStack) nbt.setInt(Util.StoredBottledExp, exp);

        e.getInventory().setResult(item);
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

        if (ConfigFile.UseThreeButtonEnchant) {
            EnchantLevel = event.whichButton() + 1;
        } else {
            EnchantLevel = getExpLevelCost - event.whichButton() - 1;
        }
        leveltoleave = PlayerLevel - EnchantLevel;
        levelxp = CMIExp.getPlayerExperience(player) - CMIExp.levelToExp(PlayerLevel);
        percentage = levelxp * 100 / CMIExp.deltaLevelToExp(PlayerLevel);
        newlevelpercentagexp = (int) (CMIExp.deltaLevelToExp(leveltoleave) * percentage / 100);
        if (ConfigFile.UseThreeButtonEnchant) {
            xptoleave = CMIExp.levelToExp(leveltoleave) + newlevelpercentagexp;
            takenxp = CMIExp.getPlayerExperience(player) - xptoleave;
            // don't do anything, let Minecraft to do dirty job
        } else {
            xptoleave = CMIExp.levelToExp(PlayerLevel - getExpLevelCost) + newlevelpercentagexp;
            takenxp = CMIExp.getPlayerExperience(player) - xptoleave;
//	    player.setLevel(0);
//	    player.setExp(0);
//	    player.setTotalExperience(0);
//	    player.setLevel(leveltoleave);
//	    player.giveExp(newlevelpercentagexp);
//	    event.setExpLevelCost(0);
        }

        if (ConfigFile.ShowEnchantExp)
            player.sendMessage(ChatColor.GREEN + Language.getMessage("Enchant").replace("[xp]", String.valueOf(takenxp)));

        // Beta enchantment output
        if (ConfigFile.ShowEnchant) {
            Map<Enchantment, Integer> enchantmentName = event.getEnchantsToAdd();
            int i = event.getEnchantsToAdd().size();
            String enchantString = "";
            String enchantStringFull = "";
            for (Entry<Enchantment, Integer> entry : enchantmentName.entrySet()) {
                i--;
                enchantString = entry.getKey().getName();
                Integer enchantlevel = entry.getValue();
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("bottledexp.versioncheck")) {
            CMIVersionChecker.VersionCheck(player, 2815, BottledExp.getInstance().getDescription());
        }
    }
}