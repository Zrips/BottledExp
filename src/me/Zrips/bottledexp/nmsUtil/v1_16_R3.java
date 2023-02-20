package me.Zrips.bottledexp.nmsUtil;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Zrips.bottledexp.NMS;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.minecraft.server.v1_16_R3.EntityVillager;
import net.minecraft.server.v1_16_R3.MerchantRecipe;
import net.minecraft.server.v1_16_R3.MerchantRecipeList;

public class v1_16_R3 implements NMS {
    @Override
    public void disableTrade(Entity ent, Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	EntityVillager villager = ((CraftVillager) ent).getHandle();
	MerchantRecipeList recipeList = villager.getOffers();
	if (recipeList == null)
	    return;

	for (MerchantRecipe one : recipeList) {
	    if (one.getSellingItem().getName() == null)
		continue;

	    String name = one.getSellingItem().getItem().getName();
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
