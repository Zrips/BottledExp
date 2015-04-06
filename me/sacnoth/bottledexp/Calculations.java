package me.sacnoth.bottledexp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Calculations {
	private static String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

	// config file Language management
	public static String LangConfig(FileConfiguration fileConfig, String configValue, String text, boolean colorize) {
		fileConfig.addDefault(configValue, text); // adding default values
		String langVariable = fileConfig.getString(configValue); // Getting value from config file
		if (colorize) {
			text = ChatColor.translateAlternateColorCodes('&', langVariable); // Making colored text
		}
		fileConfig.set(configValue, langVariable); // Writing values to config file
		return text;
	}

	// total xp calculation based by lvl
	public static int levelToExp(int level) {
		if (version.contains("1_7")) {
			if (level <= 15) {
				return 17 * level;
			} else if (level <= 30) {
				return (3 * level * level / 2) - (59 * level / 2) + 360;
			} else {
				return (7 * level * level / 2) - (303 * level / 2) + 2220;
			}
		} else {
			if (level <= 15) {
				return (int) (level * level + 6 * level);
			} else if (level <= 30) {
				return (int) (2.5 * level * level - 40.5 * level + 360);
			} else {
				return (int) (4.5 * level * level - 162.5 * level + 2220);
			}
		}

	}

	// xp calculation for one current lvl
	public static int deltaLevelToExp(int level) {
		if (version.contains("1_7")) {
			if (level <= 15) {
				return 17;
			} else if (level <= 30) {
				return 3 * level - 31;
			} else {
				return 7 * level - 155;
			}
		} else {
			if (level <= 15) {
				return 2 * level + 7;
			} else if (level <= 30) {
				return 5 * level - 38;
			} else {
				return 9 * level - 158;
			}
		}
	}

	// xp calculation for one current lvl
	public static int currentlevelxpdelta(Player player) {
		int levelxp = deltaLevelToExp(player.getLevel()) - ((Calculations.levelToExp(player.getLevel()) + (int) (Calculations.deltaLevelToExp(player.getLevel()) * player.getExp())) - levelToExp(player.getLevel()));
		return levelxp;
	}

	public static int xptobottles(float xp) {
		int bottles = (int) Math.ceil(xp / BottledExp.xpEarn);
		return bottles;
	}

	public static String variablereplace(String text, String Replace, String ReplaceWhat) {
		text = ReplaceWhat.replace(Replace, text);
		return text;
	}

	public static String codeCleaning(String inputString) {
		inputString = inputString.replaceAll("([0-9])", "");
		inputString = inputString.replace("Enchantment[, ", "");
		inputString = inputString.replace("]", "");
		inputString = inputString.replace("_", " ");
		return inputString;
	}

	public static String toSentenceCase(String inputString) {
		String result = "";
		if (inputString.length() == 0) {
			return result;
		}
		char firstChar = inputString.charAt(0);
		char firstCharToUpperCase = Character.toUpperCase(firstChar);
		result = result + firstCharToUpperCase;
		boolean terminalCharacterEncountered = false;
		char[] terminalCharacters = { '.', '?', '!' };
		for (int i = 1; i < inputString.length(); i++) {
			char currentChar = inputString.charAt(i);
			if (terminalCharacterEncountered) {
				if (currentChar == ' ') {
					result = result + currentChar;
				} else {
					char currentCharToUpperCase = Character.toUpperCase(currentChar);
					result = result + currentCharToUpperCase;
					terminalCharacterEncountered = false;
				}
			} else {
				char currentCharToLowerCase = Character.toLowerCase(currentChar);
				result = result + currentCharToLowerCase;
			}
			for (int j = 0; j < terminalCharacters.length; j++) {
				if (currentChar == terminalCharacters[j]) {
					terminalCharacterEncountered = true;
					break;
				}
			}
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static int countItems(Player player, int itemID) {
		PlayerInventory inventory = player.getInventory();

		int amount = 0;
		ItemStack curItem;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			curItem = inventory.getItem(slot);
			if (curItem != null && curItem.getTypeId() == itemID)
				amount += curItem.getAmount();
		}
		return amount;
	}

	public static int getPlayerExperience(Player player) {
		int bukkitExp = (Calculations.levelToExp(player.getLevel()) + (int) (Calculations.deltaLevelToExp(player.getLevel()) * player.getExp()));
		return bukkitExp;
	}

	@SuppressWarnings("deprecation")
	public static boolean checkInventory(Player player, int itemID, int amount) {
		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(itemID, amount)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean consumeItem(Player player, int itemID, int amount) {
		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(itemID, amount)) {
			ItemStack items = new ItemStack(itemID, amount);
			inventory.removeItem(items);
			return true;
		} else {
			return false;
		}
	}
}
