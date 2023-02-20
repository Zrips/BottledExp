package me.Zrips.bottledexp.nmsUtil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Zrips.bottledexp.NMS;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.MerchantRecipe;
import net.minecraft.server.v1_8_R3.MerchantRecipeList;

public class v1_8_R3 implements NMS {
    @Override
    public void disableTrade(Entity ent, Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	EntityVillager villager = ((CraftVillager) ent).getHandle();

	EntityHuman eHuman = (((CraftPlayer) player).getHandle());
	MerchantRecipeList recipeList = villager.getOffers(eHuman);

	if (recipeList == null)
	    return;

	List<MerchantRecipe> temp = new ArrayList<MerchantRecipe>();
	for (MerchantRecipe one : recipeList) {
	    if (one.getBuyItem3().getName() != null && one.getBuyItem3().getName().equalsIgnoreCase("Bottle o' enchanting"))
		temp.add(one);
	}
	recipeList.removeAll(temp);
    }
}
