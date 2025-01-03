package me.Zrips.bottledexp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.YmlMaker;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;

public class ConfigFile {
    public static boolean ShowNewVersion = true;
    static public String Prefix;
    static public String Lang = "EN";

    static public boolean useBottleMoney;
    static public boolean settingUseItems;
    static public boolean useVaultEcon;
    static public boolean ShowEnchantExp;
    static public boolean ShowEnchant;
    static public boolean UseThreeButtonEnchant;
    public boolean BlockInteractionUse;
    static public boolean BlockInteractionUseRightClick;
    public boolean CustomBlockInteractionUse;
    static public boolean CustomBlockInteractionUseRightClick;
    static public boolean CraftExpContainer;
    static public boolean DisableVillagerExpTrade;
    static public boolean DisableMobSpawnerExp;
    static public boolean DisableDispensers;

    static public int xpCost;
    static public int xpEarn;
    static public CMIMaterial settingConsumedItem;
    static public int amountConsumed;
    static public Double moneyCost;
    static public int LostDurringTransfer;
    static public int StoreMinimalAmount;
    static public int StoreMaxBottles;
    static public CMIMaterial BlockInteractionBlock;
    static public CMIMaterial BlockInteractionHandItem;
    static public int BlockInteractionGiveEveryTime;
    static public int BlockInteractionMultiplayer;

    static public int CustomBlockInteractionBlockId;
    static public CMIMaterial CustomBlockInteractionHandItem;
    static public int CustomBlockInteractionGiveEveryTime;
    static public int CustomBlockInteractionMultiplayer;

    static public double bottleCost;

    public BottledExp plugin;

    public ConfigFile(BottledExp plugin) {
        this.plugin = plugin;
    }

    public void copyOverTranslations() {
        ArrayList<String> languages = new ArrayList<String>();
        try {
            languages.addAll(getClassesFromPackage("Translations", "Locale_"));
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        for (String one : languages) {
            File file = new File(plugin.getDataFolder(), "Locale_" + one + ".yml");
            YmlMaker f = new YmlMaker(plugin, "Translations" + File.separator + "Locale_" + one + ".yml");
            f.saveDefaultConfig();
            f.ConfigFile.renameTo(file);
            f.ConfigFile.delete();
        }
    }

    public static List<String> getClassesFromPackage(String pckgname, String cleaner) throws ClassNotFoundException {
        List<String> result = new ArrayList<String>();
        try {
            for (URL jarURL : ((URLClassLoader) BottledExp.class.getClassLoader()).getURLs()) {
                try {
                    result.addAll(getClassesInSamePackageFromJar(pckgname, jarURL.toURI().getPath(), cleaner));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
        }
        return result;
    }

    private static List<String> getClassesInSamePackageFromJar(String packageName, String jarPath, String cleaner) {
        JarFile jarFile = null;
        List<String> listOfCommands = new ArrayList<String>();
        try {
            jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry entry = en.nextElement();
                String entryName = entry.getName();
                packageName = packageName.replace(".", "/");
                if (entryName != null && entryName.endsWith(".yml") && entryName.startsWith(packageName)) {
                    String name = entryName.replace(packageName, "").replace(".yml", "").replace("/", "");
                    if (name.contains("$"))
                        name = name.split("\\$")[0];
                    if (cleaner != null)
                        name = name.replace(cleaner, "");
                    listOfCommands.add(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null)
                try {
                    jarFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return listOfCommands;
    }

    // Sign file
    public void LoadBlocks() {
        BottledExp.getEBlocks().getAllBlocks().clear();
        File file = new File(plugin.getDataFolder(), "blocks.yml");
        YamlConfiguration f = YamlConfiguration.loadConfiguration(file);
        if (!f.isConfigurationSection("Blocks")) {

            if (f.isList("Blocks")) {
                for (String one : f.getStringList("Blocks")) {

                    String[] split = one.split(":");
                    if (split.length != 4)
                        continue;

                    try {
                        EBlock newTemp = new EBlock();
                        newTemp.setWorld(split[0]);
                        newTemp.setX(Integer.parseInt(split[1]));
                        newTemp.setY(Integer.parseInt(split[2]));
                        newTemp.setZ(Integer.parseInt(split[3]));
                        BottledExp.getEBlocks().addBlock(newTemp);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            }

            return;
        }
        ConfigurationSection ConfCategory = f.getConfigurationSection("Blocks");
        ArrayList<String> categoriesList = new ArrayList<String>(ConfCategory.getKeys(false));
        if (categoriesList.size() == 0)
            return;
        for (String category : categoriesList) {
            ConfigurationSection QuestionNameSection = ConfCategory.getConfigurationSection(category);
            EBlock newTemp = new EBlock();
            newTemp.setWorld(QuestionNameSection.getString("World"));
            newTemp.setX((int) QuestionNameSection.getDouble("X"));
            newTemp.setY((int) QuestionNameSection.getDouble("Y"));
            newTemp.setZ((int) QuestionNameSection.getDouble("Z"));
            BottledExp.getEBlocks().addBlock(newTemp);
        }
    }

    // Signs save file
    public void saveBlocks() {

        ConfigReader conf = null;
        try {
            conf = new ConfigReader(plugin, "blocks.yml");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (conf == null)
            return;

        conf.addComment("Blocks", "DO NOT EDIT THIS FILE BY HAND!");
        List<String> ls = new ArrayList<String>();
        for (Entry<String, EBlock> one : BottledExp.getEBlocks().getAllBlocks().entrySet()) {
            ls.add(one.getKey());
        }
        conf.set("Blocks", ls);
        conf.save();
    }

    public void ChangeConfig(String path, Boolean stage) {
        File f = new File(BottledExp.plugin.getDataFolder(), "config.yml");
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
        conf.options().copyDefaults(true);
        Set<String> defaults = conf.getKeys(false);
        String CorrectKey = null;

        for (String key : defaults) {
            if (key.equalsIgnoreCase(path)) {
                CorrectKey = key;
                break;
            }
        }

        if (CorrectKey == null)
            return;

        conf.set(CorrectKey, stage);

        try {
            conf.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoadConfig();
    }

    public void ChangeConfig(String path, int stage) {
        File f = new File(BottledExp.plugin.getDataFolder(), "config.yml");
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
        conf.options().copyDefaults(true);
        Set<String> defaults = conf.getKeys(false);
        String CorrectKey = null;

        for (String key : defaults) {
            if (key.equalsIgnoreCase(path)) {
                CorrectKey = key;
                break;
            }
        }

        if (CorrectKey == null)
            return;

        conf.set(CorrectKey, stage);

        try {
            conf.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoadConfig();
    }

    // Language file
    public static void LoadLang(String lang) {

        ConfigReader conf = null;
        try {
            conf = new ConfigReader(BottledExp.getInstance(), "Locale_" + lang + ".yml");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (conf == null)
            return;

        conf.get("Prefix", "&e[&aBottledExp&e] ");

        conf.get("Self", "&4You cant send exp for yourself!");
        conf.get("Reload", "&eConfiguration files was reloaded!");
        conf.get("Enchant", "&2You have spend &3[xp] &2xp for this enchantment");
        conf.get("GotExp", "&2You have got &3[xp] &2xp");

        conf.get("Store.Name", "&6|   BottledExp   |");
        conf.get("Store.BottleLore", "&eStored Exp: &6[exp]");

        conf.get("Store.ExpLoreColor", "&6");

        conf.get("command.help.output.usage", "&eUsage: &6%usage%");
        conf.get("command.help.output.cmdInfoFormat", "[command] &f- &e[description]");
        conf.get("command.help.output.cmdFormat", "&6/[command] &f[arguments]");
        conf.get("command.help.output.helpPageDescription", "&e* [description]");
        conf.get("command.help.output.title", "&2----&e ==== &6BottledExp &e==== &2----");

        conf.get("command.stats.help.info", "&2Check your exp information");
        conf.get("command.stats.help.args", "(playername)");
        conf.get("command.stats.info.feedback", Arrays.asList("&2You currently have &3[xp] &2xp and you are at lvl &3[level]&2!",
            "&2You still need &3[xpdelta] &2xp or &3[bottles] &2bottles for next level!"));
        conf.get("command.stats.info.Moneyfeedback", "&2This will gonna cost you &3[money]");

        conf.get("command.until.help.info", "&2Check how many bottles you need to reach level");
        conf.get("command.until.help.args", "[level]");
        conf.get("command.until.info.moreThan", "&4This should be more than your level!");
        conf.get("command.until.info.feedback", "&2You need &3[xp] &2xp or &3[bottles] &2bottles to reach &3[level] &2level");
        conf.get("command.until.info.Moneyfeedback", "&2This will gonna cost you &3[money]");

        conf.get("command.give.help.info", "&2Give exp to another player");
        conf.get("command.give.help.args", "[playername] [exp]");
        conf.get("command.give.info.noExp", "&4You dont have enough exp to give");
        conf.get("command.give.info.sender", "&2You just send &3[amount] &2xp to &3[name] &2and &3[lost] &2xp was lost during transfer");
        conf.get("command.give.info.receiver", "&2You just got &3[amount] &2xp from &3[name] &2and &3[lost] &2xp was lost during transfer");

        conf.get("command.eblock.help.info", "&2Creates custom exp exchange block you are looking at");
        conf.get("command.eblock.help.args", "");
        conf.get("command.eblock.info.created", "&6Created");
        conf.get("command.eblock.info.removed", "&eRemoved");

        conf.get("command.reload.help.info", "&2Reload config files");
        conf.get("command.reload.help.args", "");

        conf.get("command.get.help.info", "&2Exchange exp to exp bottles");
        conf.get("command.get.help.args", "[amount/max]");
        conf.get("command.get.info.noExp", "&4You dont have enough exp");
        conf.get("command.get.info.NoMoney", "&4You don't have enough money!");
        conf.get("command.get.info.NoItems", "&4You don't have enough items!");
        conf.get("command.get.info.Cost", "&eTransaction cost: &3[cost]");
        conf.get("command.get.info.Order", "&2You have ordered: &3[bottles] &2bottles!");

        conf.get("command.store.help.info", "&2Store particular amount in a bottle");
        conf.get("command.store.help.args", "[exp/levelL/max]");
        conf.get("command.store.info.converted", "&2You have ordered: &3[exp] &2exp!");

        conf.get("command.consume.help.info", "&2Consume bottles from your hand");
        conf.get("command.consume.help.args", "[amount/all]");
        conf.get("command.consume.info.consumed", "&2Consumed &6[bottles] &2bottles and gained &6[exp] &2exp");

        // Write back config
        conf.save();
    }

    public void LoadConfig() {

        ConfigReader conf = null;
        try {
            conf = new ConfigReader(plugin, "config.yml");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (conf == null)
            return;

        conf.copyDefaults(true);

//	File f = new File(BottledExp.plugin.getDataFolder(), "config.yml");
//	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
//	CommentedYamlConfiguration writer = new CommentedYamlConfiguration();
//	conf.options().copyDefaults(true);

        conf.addComment("bottle.ShowNewVersion", "Shows if there is available new version on login with bottledexp.versioncheck permission node");
        ShowNewVersion = conf.get("bottle.ShowNewVersion", true);

        conf.addComment("bottle.Language", "Language file you want to use");
        Lang = conf.get("bottle.Language", "EN").toUpperCase();

        conf.addComment("bottle.xpCost", "How much player will spend converting his xp to bottles. This cant be lower than xpEarn");
        xpCost = conf.get("bottle.xpCost", 10);

        conf.addComment("bottle.xpEarn", "How much player will get xp from xp bottle", "Default value: 8",
            "Having this value lower than xpCost will mean that players will loose some amount of exp while converting, which can help out in preventing mindless conversions or possible exploits with other plugins which could give out extra exp");
        xpEarn = conf.get("bottle.xpEarn", 8);

        if (xpCost < xpEarn) {
            xpCost = xpEarn;
            CMIMessages.consoleMessage("&cValue for xpCost is lower than xpEarn in your config file. It needs to be same or higher");
        }

        conf.addComment("bottle.useItems", "Do you want to use custom items when converting xp to xp bottles");
        settingUseItems = conf.get("bottle.useItems", false);

        conf.addComment("bottle.consumedItemMaterial", "Material name for custom item, by default its empty glass bottle");
        settingConsumedItem = CMIMaterial.get(conf.get("bottle.consumedItemMaterial", CMIMaterial.GLASS_BOTTLE.toString()));

        conf.addComment("bottle.amountConsumed", "How many to consume when converting. This is for every bottle you will get");
        amountConsumed = conf.get("bottle.amountConsumed", 1);

        conf.addComment("bottle.useMoney", "Do you want to charge money for xp conversion");
        useVaultEcon = conf.get("bottle.useMoney", false);

        conf.addComment("bottle.moneyCost", "How much it will gonna cost");
        moneyCost = conf.get("bottle.moneyCost", 100D);

        conf.addComment("bottle.useBottleMoney", "Do you want to show extra information for how much its gonna cost to level up");
        useBottleMoney = conf.get("bottle.useBottleMoney", false);

        conf.addComment("bottle.bottleCost", "How much one xp bottle costs");
        bottleCost = conf.get("bottle.bottleCost", 3.25);

        conf.addComment("bottle.ShowEnchantExp", "Do you want to show exp consumed on enchant");
        ShowEnchantExp = conf.get("bottle.ShowEnchantExp", true);

        conf.addComment("bottle.ShowEnchant", "Do you want to show all enchantments player gets after enchanting item");
        ShowEnchant = conf.get("bottle.ShowEnchant", false);

        conf.addComment("bottle.UseThreeButtonEnchant", "Do you want to use new 1.8 enchant system");
        UseThreeButtonEnchant = conf.get("bottle.UseThreeButtonEnchant", true);

        conf.addComment("bottle.give.LostDurringTransfer", "How much exp will be lost during transfer in percentage");
        LostDurringTransfer = conf.get("bottle.give.LostDurringTransfer", 0);

        conf.addComment("bottle.store.MinimalAmount", "Minimal amount of exp player needs to have to be able to store it inside bottle", "Default: 7");
        StoreMinimalAmount = conf.get("bottle.store.MinimalAmount", 7);

        conf.addComment("bottle.store.MaxBottles", "Max amount of bottles player can get from store command when prividing desired amount", "Default: 2304");
        StoreMaxBottles = conf.get("bottle.store.MaxBottles", 2304);

        StoreMinimalAmount = CMINumber.clamp(StoreMinimalAmount, 1, StoreMinimalAmount);

        conf.addComment("bottle.BlockInteraction.Use", "");
        BlockInteractionUse = conf.get("bottle.BlockInteraction.Use", false);

        conf.addComment("bottle.BlockInteraction.UseRightClick",
            "Do you want to use right click, if false then left will be used, good for block with interactions like enchant table");
        BlockInteractionUseRightClick = conf.get("bottle.BlockInteraction.UseRightClick", false);

        conf.addComment("bottle.BlockInteraction.BlockMaterial", "Material of block will be used to convert xp to bottles");
        BlockInteractionBlock = CMIMaterial.get(conf.get("bottle.BlockInteraction.BlockMaterial", CMIMaterial.ENCHANTING_TABLE.toString()));

        conf.addComment("bottle.BlockInteraction.HandItemMaterial", "Material of item in hand when you want to convert xp by clicking block");
        BlockInteractionHandItem = CMIMaterial.get(conf.get("bottle.BlockInteraction.HandItemMaterial", CMIMaterial.GLASS_BOTTLE.toString()));

        conf.addComment("bottle.BlockInteraction.GiveEveryTime", "How many bottles to give everytime");
        BlockInteractionGiveEveryTime = conf.get("bottle.BlockInteraction.GiveEveryTime", 1);

        conf.addComment("bottle.BlockInteraction.Multiplayer", "How many times to multiply given bottles by clicking shift");
        BlockInteractionMultiplayer = conf.get("bottle.BlockInteraction.Multiplayer", 10);

        // custom block
        conf.addComment("bottle.CustomBlockInteraction.Use", "");
        CustomBlockInteractionUse = conf.get("bottle.CustomBlockInteraction.Use", false);

        conf.addComment("bottle.CustomBlockInteraction.UseRightClick",
            "Do you want to use right click, if false then left will be used, good for block with interactions like enchant table");
        CustomBlockInteractionUseRightClick = conf.get("bottle.CustomBlockInteraction.UseRightClick", false);

        conf.addComment("bottle.CustomBlockInteraction.HandItemMaterial", "Material of item in hand when you want to convert xp by clicking block");
        CustomBlockInteractionHandItem = CMIMaterial.get(conf.get("bottle.CustomBlockInteraction.HandItemMaterial", CMIMaterial.GLASS_BOTTLE.toString()));

        conf.addComment("bottle.CustomBlockInteraction.GiveEveryTime", "How many bottles to give everytime");
        CustomBlockInteractionGiveEveryTime = conf.get("bottle.CustomBlockInteraction.GiveEveryTime", 1);

        conf.addComment("bottle.CustomBlockInteraction.Multiplayer", "How many times to multiply given bottles by clicking shift");
        CustomBlockInteractionMultiplayer = conf.get("bottle.CustomBlockInteraction.Multiplayer", 10);

        conf.addComment("bottle.CraftExpContainer.Use",
            "When this set to true there will be option to craft special expbottle by placing empty glass bottle into crafting",
            "You can consume it by right clicking it");
        CraftExpContainer = conf.get("bottle.CraftExpContainer.Use", false);

        conf.addComment("bottle.DisableVillagerExpTrade",
            "When this set to true, all trades with villgers involving exp will be disabled");
        DisableVillagerExpTrade = conf.get("bottle.DisableVillagerExpTrade", false);

        conf.addComment("bottle.DisableMobSpawnerExp",
            "When this set to true, destrying mob spawners wont drop exp");
        DisableMobSpawnerExp = conf.get("bottle.DisableMobSpawnerExp", false);

        conf.addComment("bottle.DisableDispensers",
            "When this set to true exp bottles will not be dropped from dispensers");
        DisableDispensers = conf.get("bottle.DisableDispensers", false);

        if (CraftExpContainer) {
            ItemStack Item = CMIMaterial.EXPERIENCE_BOTTLE.newItemStack();

            ShapelessRecipe NewShapelessRecipe = null;
            if (Version.isCurrentHigher(Version.v1_11_R1))
                NewShapelessRecipe = new ShapelessRecipe(NamespacedKey.randomKey(), Item);
            else
                NewShapelessRecipe = new ShapelessRecipe(Item);

            NewShapelessRecipe.addIngredient(1, Material.GLASS_BOTTLE);
            Bukkit.getServer().addRecipe(NewShapelessRecipe);
        }

        conf.save();
    }

    public void reload(CommandSender player, Boolean feedback) {

        LoadLang("EN");
        if (!Lang.equalsIgnoreCase("EN"))
            LoadLang(Lang);

        LoadConfig();

        Language.reload();

        if (feedback)
            player.sendMessage(Language.getMessage("Prefix") + Language.getMessage("Reload"));
    }

}
