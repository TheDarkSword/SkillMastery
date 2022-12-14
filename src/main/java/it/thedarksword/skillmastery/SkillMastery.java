package it.thedarksword.skillmastery;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import it.thedarksword.basement.api.bukkit.BasementBukkit;
import it.thedarksword.skillmastery.blocks.BlockManager;
import it.thedarksword.skillmastery.commands.SkillsCommand;
import it.thedarksword.skillmastery.config.SkillConfig;
import it.thedarksword.skillmastery.listeners.PlayerListener;
import it.thedarksword.skillmastery.mysql.manager.QueryManager;
import it.thedarksword.skillmastery.player.SkillPlayer;
import it.thedarksword.skillmastery.player.manager.PlayerManager;
import it.thedarksword.skillmastery.potions.PotionManager;
import it.thedarksword.skillmastery.skill.provider.SkillProvider;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@Getter
@Accessors(fluent = true)
public class SkillMastery {

    @Getter
    @Accessors(fluent = true)
    private static SkillMastery instance;

    private final JavaPlugin plugin;
    private final BasementBukkit basement;
    private final CoreProtectAPI coreProtectAPI;
    private final Economy economy;

    private final SettingsManager config;

    private final SkillProvider skillProvider;
    private final QueryManager queryManager;
    private final PlayerManager playerManager;
    private final BlockManager blockManager;
    private final PotionManager potionManager;

    public SkillMastery(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.basement = Objects.requireNonNull(plugin.getServer().getServicesManager().getRegistration(BasementBukkit.class)).getProvider();
        coreProtectAPI = getCoreProtect();
        economy = setupEconomy();

        this.config = SettingsManagerBuilder.withYamlFile(new File(plugin.getDataFolder(), "config.yml"))
                .configurationData(SkillConfig.class).useDefaultMigrationService().create();

        skillProvider = new SkillProvider();
        queryManager = new QueryManager(basement, config);
        playerManager = new PlayerManager();
        blockManager = new BlockManager(coreProtectAPI);
        potionManager = new PotionManager();
    }

    protected void enable() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(this), plugin);

        plugin.getCommand("skills").setExecutor(new SkillsCommand());

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            queryManager.skillPlayerAdd(player.getUniqueId()).exec();
            playerManager.skillPlayer(player, new SkillPlayer(player, this));
        }
    }

    protected void disable() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            playerManager.skillPlayer(player).save();
        }
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = plugin().getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that the API is enabled
        CoreProtectAPI coreProtectAPI = ((CoreProtect) Objects.requireNonNull(plugin)).getAPI();
        if (!coreProtectAPI.isEnabled()) {
            plugin.getLogger().severe("CoreProtectAPI are not enabled");
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (coreProtectAPI.APIVersion() < 8) {
            plugin.getLogger().severe("CoreProtectAPI API version is too low (" + coreProtectAPI.APIVersion() + ")");
            return null;
        }

        return coreProtectAPI;
    }

    private Economy setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }
        return rsp.getProvider();
    }
}
