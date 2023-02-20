package me.Zrips.bottledexp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Locale.YmlMaker;

public class Language {
    public static FileConfiguration enlocale;
    public static FileConfiguration Customlocale;

    static {
        enlocale = new YmlMaker(BottledExp.plugin, "Locale_EN.yml").getConfig();
        Customlocale = new YmlMaker(BottledExp.plugin, "Locale_" + ConfigFile.Lang + ".yml").getConfig();
    }

    private Language() {
    }

    public static String getMessageListAsString(String key) {
        return ToString(getMessageList(key));
    }

    /**
     * Reloads the config
     */
    public static void reload() {
        enlocale = new YmlMaker(BottledExp.plugin, "Locale_EN.yml").getConfig();
        Customlocale = new YmlMaker(BottledExp.plugin, "Locale_" + ConfigFile.Lang + ".yml").getConfig();
    }

    /**
     * Get the message with the correct key
     * @param key - the key of the message
     * @return the message
     */
    public static String getMessage(String key) {
        String missing = "Missing locale for " + key + " ";
        if (Customlocale.isString(key))
            return CMIChatColor.translate(Customlocale.getString(key));
        return enlocale.isString(key) ? CMIChatColor.translate(enlocale.getString(key)) : missing;
    }

    private static String Colors(String text) {
        return CMIChatColor.translate(text);
    }

    private static String ToString(List<String> text) {
        String temp = "";
        for (String part : text) {
            temp += part + "/n";
        }
        return CMIChatColor.translate(temp);
    }

    private static List<String> ColorsArray(List<String> text, Boolean colorize) {
        List<String> temp = new ArrayList<String>();
        for (String part : text) {
            if (colorize)
                part = Colors(part);
            temp.add(Colors(part));
        }
        return temp;
    }

    /**
     * Get the message with the correct key
     * @param key - the key of the message
     * @return the message
     */
    public static List<String> getMessageList(String key) {
        String missing = "Missing locale for " + key + " ";
        if (Customlocale.isList(key))
            return ColorsArray(Customlocale.getStringList(key), true);
        return enlocale.getStringList(key).size() > 0 ? ColorsArray(enlocale.getStringList(key), true) : Arrays.asList(missing);
    }

    /**
     * Check if key exists
     * @param key - the key of the message
     * @return true/false
     */
    public static boolean containsKey(String key) {
        if (Customlocale.contains(key))
            return true;
        return enlocale.contains(key);
    }
}
