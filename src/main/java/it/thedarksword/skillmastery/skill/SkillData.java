package it.thedarksword.skillmastery.skill;

import it.thedarksword.basement.api.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public record SkillData(int maxLevel, double incrementPerLevel, Material material, String name, String... description) {

    public ItemBuilder builder() {
        return new ItemBuilder(material)
                .setName("&a" + name)
                .addLore(description)
                .addLore("")
                .setFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    }
}
