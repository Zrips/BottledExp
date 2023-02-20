package me.Zrips.bottledexp;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.Zrips.bottledexp.commands.BottledExpCommands;
import me.Zrips.bottledexp.commands.TabComplete;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Locale.YmlMaker;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class BottledExp extends JavaPlugin {

    public static BottledExp plugin;

    static Logger log;
    static boolean usePermissions = false;
    public static boolean useVaultEcon = true;
    static boolean useVaultPermissions = false;
    static PermissionManager pexPermissions;
    private static BottledExpCommands commands;
    static Permission vaultPermissions;
    static Recipes Recipes;
    public static Economy economy = null;
    private static NMS nms;

    private static ConfigFile cmanager;
    public static EBlockInfo EBlocks = new EBlockInfo();

    public static NMS getNms() {
        return nms;
    }

    public static ConfigFile getConfigManager() {
        return cmanager;
    }

    public static EBlockInfo getEBlocks() {
        return EBlocks;
    }

    public static BottledExp getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getLogger();

        cmanager = new ConfigFile(this);

        cmanager.LoadBlocks();
        cmanager.copyOverTranslations();

        String packageName = getServer().getClass().getPackage().getName();
        String[] packageSplit = packageName.split("\\.");
        String version = packageSplit[packageSplit.length - 1].substring(0, packageSplit[packageSplit.length - 1].length() - 3);
        version = packageSplit[packageSplit.length - 1];

        try {
            Class<?> nmsClass = Class.forName("me.Zrips.bottledexp.nmsUtil." + version);
            if (NMS.class.isAssignableFrom(nmsClass)) {
                nms = (NMS) nmsClass.getConstructor().newInstance();
            } else {
                CMIMessages.consoleMessage("Something went wrong, please note down version and contact author v:" + version);
                this.setEnabled(false);
            }
        } catch (Throwable e) {
            CMIMessages.consoleMessage("Your server version is not compatible with this plugins version! Plugin will be disabled: " + version);
            this.setEnabled(false);
            return;
        }
        commands = new BottledExpCommands();
        this.getCommand("bottle").setExecutor(commands);

        cmanager.reload(null, false);

        YmlMaker f = new YmlMaker(this, "recipes.yml");
        f.saveDefaultConfig();

        f = new YmlMaker(this, "blocks.yml");
        f.saveDefaultConfig();

        Recipes = new Recipes(this);
        Recipes.Recipe();

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        getServer().getPluginManager().registerEvents(new BlockInteractionEvent(), this);

        if (!setupEconomy())
            useVaultEcon = false;

        ConsoleCommandSender console = getServer().getConsoleSender();
        PluginDescriptionFile pdfFile = this.getDescription();

        TabComplete tab = new TabComplete(this);
        this.getCommand(BottledExpCommands.label).setTabCompleter(tab);

        console.sendMessage(ChatColor.DARK_AQUA + "[BottledExp]" + ChatColor.GOLD + " Version " + pdfFile.getVersion() + " has been enabled");

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
            pexPermissions = PermissionsEx.getPermissionManager();
            usePermissions = true;
            log.info("Using PermissionsEx!");
        } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
            setupPermissions();
            useVaultPermissions = true;
            log.info("Using " + vaultPermissions.getName() + " via Vault.");
        } else {
            log.warning("Neither PEX nor Vault found, BottledExp will not work properly!");
        }
    }

    public void onDisable() {
//	cmanager.saveBlocks();
        log.info("You are no longer able to fill XP into Bottles");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            vaultPermissions = permissionProvider.getProvider();
        }
        return (vaultPermissions != null);
    }

    @SuppressWarnings("deprecation")
    public static boolean checkPermission(String node, Player player) {
        if (usePermissions) {
            if (pexPermissions.has(player, node)) {
                return true;
            }
            LC.info_NoPermission.sendMessage(player);
            return false;
        } else if (useVaultPermissions && vaultPermissions.isEnabled()) {
            if (vaultPermissions.playerHas(player.getWorld(), player.getName(), node)) {
                return true;
            }
            LC.info_NoPermission.sendMessage(player);
            return false;
        }
        player.sendMessage(ChatColor.RED + "Neither PEX nor Vault found, BottledExp will not work properly!");
        return false;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public void consoleMessage(String message) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage((message == null ? null : CMIChatColor.translate(message)));
    }

    public static BottledExpCommands getCommandsManager() {
        return commands;
    }
}
