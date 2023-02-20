package me.Zrips.bottledexp.nmsUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.craftbukkit.v1_18_R1.entity.CraftVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Zrips.bottledexp.NMS;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;

public class v1_18_R1 implements NMS {
    @Override
    public void disableTrade(Entity ent, Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	EntityVillager villager = null;
	try {
	    villager = (EntityVillager) ((CraftVillager) ent).getClass().getMethod("getHandle").invoke(((CraftVillager) ent));
	} catch (Throwable e) {
	    e.printStackTrace();
	}
	if (villager == null)
	    return;

	EntityVillager v = (EntityVillager) villager.getBukkitEntity().getHandle();

	MerchantRecipeList recipeList = null;

	try {
	    recipeList = (MerchantRecipeList) v.getClass().getMethod("fA").invoke(v);
	} catch (Throwable e) {
	    e.printStackTrace();
	    return;
	}

	if (recipeList == null)
	    return;

	Method displayN = null;

	for (MerchantRecipe one : new ArrayList<MerchantRecipe>(recipeList)) {

	    try {
		if (displayN == null)
		    displayN = one.f().getClass().getMethod("v");

		Object display = displayN.invoke(one.f());

		String name = (String) display.getClass().getMethod("i").invoke(display);

		if (name == null)
		    continue;

		String[] split = name.split("\\.");
		if (split.length > 1)
		    name = split[split.length - 1];
		CMIMaterial material = CMIMaterial.get(name);

		if (!material.equals(CMIMaterial.EXPERIENCE_BOTTLE))
		    continue;

		recipeList.remove(one);
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	}

    }
}
