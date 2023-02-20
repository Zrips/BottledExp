package me.Zrips.bottledexp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;

public class Calculations {

    // total xp calculation based by lvl
    public static int levelToExp(int level) {
        if (Version.isCurrentLower(Version.v1_8_R1)) {
            if (level <= 15) {
                return 17 * level;
            } else if (level <= 30) {
                return (3 * level * level / 2) - (59 * level / 2) + 360;
            } else {
                return (7 * level * level / 2) - (303 * level / 2) + 2220;
            }
        }
        if (level <= 15) {
            return (int) (level * level + 6 * level);
        } else if (level <= 30) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }

    }

    // xp calculation for one current lvl
    public static int deltaLevelToExp(int level) {
        if (Version.isCurrentLower(Version.v1_8_R1)) {
            if (level <= 16) {
                return 17;
            } else if (level <= 31) {
                return 3 * level - 31;
            } else {
                return 7 * level - 155;
            }
        }
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    // xp calculation for one current lvl
    public static int currentlevelxpdelta(Player player) {
        return deltaLevelToExp(player.getLevel()) - ((levelToExp(player.getLevel()) + Math.round(deltaLevelToExp(player.getLevel()) * player.getExp())) - levelToExp(player.getLevel()));
    }

    public static int xptobottles(float xp) {
        return (int) Math.ceil(xp / ConfigFile.xpEarn);
    }

    public static int expToLevel(double sourceleftexp, double d) {
        if (d > 21863)
            d = 21863;
        for (int i = 1; i <= d + 1; i++) {
            double levelexp = levelToExp(i);
            if (levelexp > sourceleftexp)
                return i - 1;
        }
        return 0;
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

    public static int countItems(Player player, CMIMaterial mat) {
        PlayerInventory inventory = player.getInventory();

        int amount = 0;
        ItemStack curItem;
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            curItem = inventory.getItem(slot);
            if (curItem != null && CMIMaterial.get(curItem) == mat)
                amount += curItem.getAmount();
        }
        return amount;
    }

    public static int getPlayerExperience(Player player) {
        return (Calculations.levelToExp(player.getLevel()) + Math.round(Calculations.deltaLevelToExp(player.getLevel()) * player.getExp()));
    }

    public static boolean checkInventory(Player player, CMIMaterial mat, int amount) {
        PlayerInventory inventory = player.getInventory();
        try {
            if (inventory.contains(mat.getMaterial(), amount)) {
                return true;
            }
        } catch (Exception e) {
            try {
                Method meth = inventory.getClass().getMethod("contains", int.class, int.class);
                return (boolean) meth.invoke(inventory, mat.getId(), amount);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public static boolean consumeItem(Player player, CMIMaterial mat, int amount) {

        PlayerInventory inventory = player.getInventory();
        int i = -1;
        for (ItemStack one : inventory.getContents()) {
            i++;
            if (one == null)
                continue;
            if (mat.equals(one.getType()) && amount > 0) {
                if (one.getAmount() > amount) {
                    one.setAmount(one.getAmount() - amount);
                    break;
                }
                amount = amount - one.getAmount();
                inventory.setItem(i, null);
                if (amount > 0)
                    continue;
            }
        }
        return true;
    }
}
