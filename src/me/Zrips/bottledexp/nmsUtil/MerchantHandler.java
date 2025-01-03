package me.Zrips.bottledexp.nmsUtil;

import java.util.ArrayList;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;

public class MerchantHandler {

    public static void handle(MerchantRecipeList recipeList) {
        try {

            if (recipeList == null)
                return;

            for (MerchantRecipe one : new ArrayList<MerchantRecipe>(recipeList)) {

                CMIMaterial material = CMIMaterial.get(one.asBukkit().getResult().getType());

                if (!material.equals(CMIMaterial.EXPERIENCE_BOTTLE))
                    continue;

                recipeList.remove(one);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
