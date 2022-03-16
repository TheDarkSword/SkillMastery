package it.thedarksword.skillmastery.skill;

import it.thedarksword.basement.api.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public record SkillData(int maxLevel, double incrementPerLevel, SkillType skillType, Material material,
                        String name, List<String> description, String chatDescription, String... lore) {

    public ItemBuilder builder() {
        return new ItemBuilder(material)
                .setName("&a" + name)
                .addLore(lore)
                .addLore("")
                .setFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    }

    public ItemBuilder paper() {
        return new ItemBuilder(Material.PAPER)
                .setName("&a" + name)
                .addLore(lore)
                .addLore("")
                .setFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
    }
}
