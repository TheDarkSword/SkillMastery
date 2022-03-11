package it.thedarksword.skillmastery;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import it.thedarksword.basement.api.bukkit.BasementBukkit;
import it.thedarksword.skillmastery.config.SkillConfig;
import it.thedarksword.skillmastery.mysql.manager.QueryManager;
import it.thedarksword.skillmastery.player.manager.PlayerManager;
import it.thedarksword.skillmastery.skill.provider.SkillProvider;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@Getter
@Accessors(fluent = true)
public class SkillMastery {

    private final JavaPlugin plugin;
    private final BasementBukkit basement;

    private final SettingsManager config;
    private final QueryManager queryManager;

    private final SkillProvider skillProvider;
    private final PlayerManager playerManager;


    public SkillMastery(JavaPlugin plugin) {
        this.plugin = plugin;
        this.basement = Objects.requireNonNull(plugin.getServer().getServicesManager().getRegistration(BasementBukkit.class)).getProvider();

        this.config = SettingsManagerBuilder.withYamlFile(new File(plugin.getDataFolder(), "config.yml"))
                .configurationData(SkillConfig.class).useDefaultMigrationService().create();

        queryManager = new QueryManager(basement, config);

        skillProvider = new SkillProvider();

        playerManager = new PlayerManager();
    }

    protected void enable() {

    }

    protected void disable() {

    }
}
