package me.Zrips.bottledexp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;

public class Recipes {
    private BottledExp plugin;

    public Recipes(BottledExp plugin) {
	this.plugin = plugin;
    }

    public void Recipe() {

	File f = new File(plugin.getDataFolder(), "recipes.yml");
	if (!f.exists())
	    return;
	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
	if (!config.isConfigurationSection("Recipes"))
	    return;
	Set<String> keys = config.getConfigurationSection("Recipes").getKeys(false);

	for (String one : keys) {
	    ConfigurationSection path = config.getConfigurationSection("Recipes." + one);
	    if (path.getBoolean("Enabled")) {
		int ResultId = path.getInt("ResultId");
		int ResultAmount = path.getInt("ResultAmount");
		int Meta = path.getInt("Meta");
		boolean ShapedRecipe = path.getBoolean("ShapedRecipe");
		List<String> Recipe = path.getStringList("Recipe");

		if (ShapedRecipe)
		    MakeShapedRecipe(ResultId, ResultAmount, Recipe, Meta);
		else
		    MakeShaplessRecipe(ResultId, ResultAmount, Recipe, Meta);
	    }
	}
    }

    @SuppressWarnings("deprecation")
    public static void MakeShapedRecipe(int ResultId, int ResultAmount, List<String> Recipe, int Meta) {
	
	ItemStack Item = CMIMaterial.get(ResultId, Meta).newItemStack();	
	Item.setAmount(ResultAmount);
	
	ShapedRecipe NewShapedRecipe = null;
	if (Version.isCurrentHigher(Version.v1_11_R1))
	    NewShapedRecipe = new ShapedRecipe(NamespacedKey.randomKey(), Item);
	else
	    NewShapedRecipe = new ShapedRecipe(Item);

	NewShapedRecipe.shape("123", "456", "789");
	List<String> RecipeList = new ArrayList<String>();
	for (int i = 0; i < 3; i++) {
	    String line = Recipe.get(i);
	    String[] lineSplit = line.split(",");
	    for (int y = 0; y < 3; y++) {
		RecipeList.add(lineSplit[y]);
	    }
	}
	char[] charId = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	for (int z = 0; z < 9; z++) {
	    if (!RecipeList.get(z).equalsIgnoreCase("0")) {
		int id = 0;
		int data = 0;
		try {
		    if (!RecipeList.get(z).contains(":")) {
			id = Integer.parseInt(RecipeList.get(z));
		    } else {
			id = Integer.parseInt(RecipeList.get(z).split(":")[0]);
			data = Integer.parseInt(RecipeList.get(z).split(":")[1]);
		    }

		} catch (Exception e) {

		}
		NewShapedRecipe.setIngredient(charId[z], CMIMaterial.get(id).getMaterial(), data);
	    }
	}

	Bukkit.getServer().addRecipe(NewShapedRecipe);
    }

    @SuppressWarnings("deprecation")
    public static void MakeShaplessRecipe(int ResultId, int ResultAmount, List<String> Recipe, int Meta) {
	ItemStack Item = CMIMaterial.get(ResultId, Meta).newItemStack();	
	Item.setAmount(ResultAmount);
	
//	ItemStack Item = new ItemStack(Material.getMaterial(ResultId), ResultAmount, (short) Meta);
	ShapelessRecipe NewShapelessRecipe = null;
	if (Version.isCurrentHigher(Version.v1_11_R1))
	    NewShapelessRecipe = new ShapelessRecipe(NamespacedKey.randomKey(), Item);
	else
	    NewShapelessRecipe = new ShapelessRecipe(Item);
	List<String> RecipeList = new ArrayList<String>();
	for (int i = 0; i < 3; i++) {
	    String line = Recipe.get(i);
	    String[] lineSplit = line.split(",");
	    for (int y = 0; y < 3; y++) {
		if (!lineSplit[y].equalsIgnoreCase("0")) {
		    RecipeList.add(lineSplit[y]);
		}
	    }
	}
	short meta = 0;
	for (int z = 0; z < RecipeList.size(); z++) {
	    if (RecipeList.get(z).contains(":")) {
		String[] Splited = RecipeList.get(z).split(":");
		RecipeList.set(z, Splited[0]);
		meta = Short.parseShort(Splited[1]);
	    }

	    NewShapelessRecipe.addIngredient(1, CMIMaterial.get(Integer.parseInt(RecipeList.get(z))).getMaterial(), meta);
	}
	Bukkit.getServer().addRecipe(NewShapelessRecipe);
    }
}
