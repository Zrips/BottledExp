package me.sacnoth.bottledexp;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Calculations {

	//config file Language management 
	public static String LangConfig(FileConfiguration fileConfig, String configValue, String text, boolean colorize) {
		fileConfig.addDefault(configValue, text); //adding default values
		String langVariable = fileConfig.getString(configValue); //Getting value from config file
		if (colorize){
			text = ChatColor.translateAlternateColorCodes('&', langVariable); //Making colored text
		}
		fileConfig.set(configValue, langVariable); //Writing values to config file
		return text;
	}

	//total xp calculation based by lvl
	public static int levelToExp(int level) {
		int totalxp=0;
		if (level <= 15)
		{
			totalxp=(int)(level*level + 6*level);
			return totalxp;
		}
		else if (level <= 30)
		{
			totalxp=(int)(2.5*level*level-40.5*level+360);
			return totalxp;
		}
		else
		{
			totalxp=(int)(4.5*level*level-162.5*level+2220);
			return totalxp;
		}
	}

	//xp calculation for one current lvl 
	public static int deltaLevelToExp(int level) {
		if (level <= 15)
		{
			return 2 * level + 7;
		}
		else if (level <= 30)
		{
			return 5 * level - 38;
		}
		else {
			return 9 * level - 158;
		}
	}
	
	//xp calculation for one current lvl 
	public static int currentlevelxpdelta(Player player) {
		int levelxp=deltaLevelToExp(player.getLevel()) - (player.getTotalExperience()-levelToExp(player.getLevel()));
		return levelxp;
	}
	
	public static int xptobottles(float xp) {
		int bottles=(int) Math.ceil(xp/BottledExp.xpEarn);
		return bottles;
	}
	
	public static String variablereplace(String text, String Replace, String ReplaceWhat) {
		text = ReplaceWhat.replace(Replace, text);
		return text;
	}
}
