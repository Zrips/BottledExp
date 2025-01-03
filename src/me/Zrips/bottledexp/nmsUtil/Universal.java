package me.Zrips.bottledexp.nmsUtil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import me.Zrips.bottledexp.NMS;
import net.Zrips.CMILib.Items.CMIMaterial;

public class Universal implements NMS {
    @Override
    public void disableTrade(Entity ent, Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        if (!(ent instanceof Villager))
            return;

        Villager villager = (Villager) ent;
        List<MerchantRecipe> list = new ArrayList<MerchantRecipe>(villager.getRecipes());
        for (MerchantRecipe one : new ArrayList<MerchantRecipe>(list)) {
            CMIMaterial material = CMIMaterial.get(one.getResult().getType());
            if (!material.equals(CMIMaterial.EXPERIENCE_BOTTLE))
                continue;
            list.remove(one);
        }
        villager.setRecipes(list);
    }
}
