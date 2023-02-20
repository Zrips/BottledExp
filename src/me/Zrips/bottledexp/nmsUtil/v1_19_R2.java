package me.Zrips.bottledexp.nmsUtil;

import java.util.ArrayList;

import org.bukkit.craftbukkit.v1_19_R2.entity.CraftVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Zrips.bottledexp.NMS;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;

public class v1_19_R2 implements NMS {
    @Override
    public void disableTrade(Entity ent, Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        EntityVillager v = ((CraftVillager) ent).getHandle();

        MerchantRecipeList recipeList;
        try {
            recipeList = v.fP();

            if (recipeList == null)
                return;

            for (MerchantRecipe one : new ArrayList<MerchantRecipe>(recipeList)) {

                ItemStack item = one.d();
                // H -> getDisplayName
                if (item.I().getString() == null)
                    continue;

                String name = item.I().getString();
                String[] split = name.split("\\.");
                if (split.length > 1)
                    name = split[split.length - 1];

                name = name.replaceAll("\\[|\\]", "");

                CMIMaterial material = CMIMaterial.get(name);

                if (!material.equals(CMIMaterial.EXPERIENCE_BOTTLE))
                    continue;

                recipeList.remove(one);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
