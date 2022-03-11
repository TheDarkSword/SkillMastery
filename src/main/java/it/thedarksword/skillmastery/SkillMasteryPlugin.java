package it.thedarksword.skillmastery;

import org.bukkit.plugin.java.JavaPlugin;

public class SkillMasteryPlugin extends JavaPlugin {

    SkillMastery skillMastery;

    @Override
    public void onEnable() {
        skillMastery = new SkillMastery(this);
        skillMastery.enable();
    }

    @Override
    public void onDisable() {
        skillMastery.disable();
    }
}
