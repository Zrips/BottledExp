package me.Zrips.bottledexp.nmsUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Zrips.bottledexp.NMS;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;

public class v1_17_R1 implements NMS {

    private static Method met = null;
    private static Method itemMet = null;

    @Override
    public void disableTrade(Entity ent, Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	EntityVillager villager = ((CraftVillager) ent).getHandle();

	MerchantRecipeList recipeList = null;

	try {
	    recipeList = (MerchantRecipeList) villager.getClass().getMethod("getOffers").invoke(villager);
	} catch (Throwable e) {
	    e.printStackTrace();
	    return;
	}
	
	if (recipeList == null)
	    return;
	for (MerchantRecipe one : new ArrayList<MerchantRecipe>(recipeList)) {

	    if (itemMet == null) {
		try {
		    itemMet = one.getClass().getMethod("getSellingItem");
		} catch (Throwable e) {
		    e.printStackTrace();
		    return;
		}
	    }

	    ItemStack item = null;

	    try {
		item = (ItemStack) itemMet.invoke(one);
	    } catch (Throwable e) {
		e.printStackTrace();
	    }

	    if (item == null)
		continue;

	    if (met == null) {
		try {
		    met = item.getClass().getMethod("getItem").invoke(item).getClass().getMethod("getName");
		} catch (Throwable e) {
		    e.printStackTrace();
		}
	    }

	    String name = null;
	    try {
		name = (String) met.invoke(item.getClass().getMethod("getItem").invoke(item));
	    } catch (Throwable e) {
		e.printStackTrace();
	    }

	    if (name == null)
		continue;

	    String[] split = name.split("\\.");
	    if (split.length > 1)
		name = split[split.length - 1];

	    CMIMaterial material = CMIMaterial.get(name);

	    if (!material.equals(CMIMaterial.EXPERIENCE_BOTTLE))
		continue;

	    recipeList.remove(one);
	}

    }
}
