package me.sacnoth.bottledexp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class Recipes {
	private BottledExp plugin;

	public Recipes(BottledExp plugin) {
		this.plugin = plugin;
	}
	public void Recipe() {
		final FileConfiguration config = plugin.getConfig();
		int TotalCount = config.getConfigurationSection("Recipes").getKeys(false).size();
		for (int i = 1; i <= TotalCount; i++) {
			if (config.getBoolean("Recipes." + i + ".Enabled")) {
				int ResultId = config.getInt("Recipes." + i + ".ResultId");
				int ResultAmount = config.getInt("Recipes." + i + ".ResultAmount");
				int Meta = config.getInt("Recipes." + i + ".Meta");
				boolean ShapedRecipe = config.getBoolean("Recipes." + i + ".ShapedRecipe");
				List<String> Recipe = config.getStringList("Recipes." + i + ".Recipe");

				if (ShapedRecipe)
					MakeShapedRecipe(ResultId, ResultAmount, Recipe, Meta);
				else
					MakeShaplessRecipe(ResultId, ResultAmount, Recipe, Meta);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void MakeShapedRecipe(int ResultId, int ResultAmount, List<String> Recipe, int Meta) {
		ItemStack Item = new ItemStack(Material.getMaterial(ResultId), ResultAmount, (short) Meta);
		ShapedRecipe NewShapedRecipe = new ShapedRecipe(Item);
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
			if (!RecipeList.get(z).equalsIgnoreCase("0"))
				NewShapedRecipe.setIngredient(charId[z], Material.getMaterial(Integer.parseInt(RecipeList.get(z))));
		}
		Bukkit.getServer().addRecipe(NewShapedRecipe);
	}

	@SuppressWarnings("deprecation")
	public static void MakeShaplessRecipe(int ResultId, int ResultAmount, List<String> Recipe, int Meta) {
		ItemStack Item = new ItemStack(Material.getMaterial(ResultId), ResultAmount, (short) Meta);
		ShapelessRecipe NewShapelessRecipe = new ShapelessRecipe(Item);
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
			if (RecipeList.get(z).contains(":")){
				String[] Splited = RecipeList.get(z).split(":");
				RecipeList.set(z, Splited[0]);
				meta = Short.parseShort(Splited[1]);
			}
				
			NewShapelessRecipe.addIngredient(1, Material.getMaterial(Integer.parseInt(RecipeList.get(z))), (short) meta);
		}
		Bukkit.getServer().addRecipe(NewShapelessRecipe);
	}
}
