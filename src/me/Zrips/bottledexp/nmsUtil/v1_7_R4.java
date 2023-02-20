package me.Zrips.bottledexp.nmsUtil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Zrips.bottledexp.NMS;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityVillager;
import net.minecraft.server.v1_7_R4.MerchantRecipe;
import net.minecraft.server.v1_7_R4.MerchantRecipeList;

public class v1_7_R4 implements NMS {
    @SuppressWarnings("unchecked")
    @Override
    public void disableTrade(Entity ent, Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	EntityVillager villager = ((CraftVillager) ent).getHandle();

	EntityHuman eHuman = (((CraftPlayer) player).getHandle());
	MerchantRecipeList recipeList = villager.getOffers(eHuman);

	if (recipeList == null)
	    return;

	List<MerchantRecipe> temp = new ArrayList<MerchantRecipe>();
	for (int i = 0; i < recipeList.size(); i++) {
	    if (((MerchantRecipe) recipeList.get(i)).getBuyItem3().getName() != null && ((MerchantRecipe) recipeList.get(i)).getBuyItem3().getName().equalsIgnoreCase(
		"Bottle o' enchanting"))
		temp.add(((MerchantRecipe) recipeList.get(i)));
	}
	recipeList.removeAll(temp);
    }
}
