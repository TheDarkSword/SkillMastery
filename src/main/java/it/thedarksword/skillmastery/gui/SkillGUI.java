package it.thedarksword.skillmastery.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import it.thedarksword.basement.api.bukkit.item.ItemBuilder;
import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.player.SkillPlayer;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import it.thedarksword.skillmastery.skill.SkillType;
import it.thedarksword.skillmastery.utils.RomanNumber;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SkillGUI extends CommonInventory implements InventoryProvider {

    private final SkillType skillType;

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(fillItem);
        SkillPlayer skillPlayer = SkillMastery.instance().playerManager().skillPlayer(player);
        Skill<?> skill = skillPlayer.skill(skillType);

        contents.set(SlotPos.of(0, 0), ClickableItem.empty(skill.skillData().builder()
                .addLore(buildLore(skill)).build()));

        int row = 1;
        int column = 0;
        int level = 0;

        contents.set(SlotPos.of(row, column), ClickableItem.empty(levelItem(skill, level)));

        int[] result = buildHalfInventory(contents, row, column, skill, level);
        row = result[0];
        column = result[1];
        level = result[2];

        contents.set(SlotPos.of(row, column), ClickableItem.empty(levelItem(skill, level)));
        result = buildHalfInventory(contents, row, column, skill, level);
        row = result[0];
        column = result[1];
        level = result[2];

        contents.set(SlotPos.of(++row, column), ClickableItem.empty(levelItem(skill, ++level)));
        contents.set(SlotPos.of(++row, column), ClickableItem.empty(levelItem(skill, ++level)));
        contents.set(SlotPos.of(++row, column), ClickableItem.empty(levelItem(skill, ++level)));
        contents.set(SlotPos.of(++row, column), ClickableItem.empty(levelItem(skill, ++level)));

        contents.set(SlotPos.of(5, 3), ClickableItem.of(new ItemBuilder(Material.ARROW)
                .setName("&aIndietro").build(), event -> SkillsGUI.INVENTORY.open((Player) event.getWhoClicked())));
        contents.set(SlotPos.of(5, 4), ClickableItem.of(new ItemBuilder(Material.BARRIER)
                .setName("&cChiudi").build(), event -> event.getWhoClicked().closeInventory()));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    public static SmartInventory inventory(SkillType skillType) {
        return SmartInventory.builder()
                .provider(new SkillGUI(skillType))
                .size(6, 9)
                .title("Skill " + StringUtils.capitalize(skillType.name().toLowerCase()))
                .build();
    }

    //TODO: Build Lore in base at skill data
    // ---- <- are 20
    private List<String> buildLore(Skill<? extends Event> skill) {
        SkillData skillData = skill.skillData();
        return new ArrayList<>();
    }

    private ItemStack levelItem(Skill<? extends Event> skill, int level) {
        SkillData skillData =  skill.skillData();
        String roman = RomanNumber.toRoman(level+1);
        ItemBuilder builder;
        if(skill.level() == level) {
            builder = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE, level+1)
                    .setName("&e" + skillData.name() + " Livello " + roman)
                    .setLore(buildLore(skill, level, roman));
        } else if(skill.level() > level) {
            builder = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE, level+1)
                    .setName("&a" + skillData.name() + " Livello " + roman)
                    .setLore(buildLore(skill, level, roman));
        } else {
            builder = new ItemBuilder(Material.RED_STAINED_GLASS_PANE, level+1)
                    .setName("&c" + skillData.name() + " Livello " + roman)
                    .setLore(buildLore(skill, level, roman));
        }
        return builder.build();
    }

    private List<String> buildLore(Skill<? extends Event> skill, int level, String roman) {
        SkillData skillData =  skill.skillData();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("&7Ricompense:");
        lore.add(" " + skillData.name() + " " + roman);
        int x2 = Skill.calculatePercentage(level, skillData);
        if(x2 == 100) {
            int x3 = Skill.calculatePercentage(level-25, skillData);
            lore.add("  &fDa (x3) &a+&8" + x3 + "\u279C&a+" + (x3 + skillData.incrementPerLevel()));
        } else {
            lore.add("  &fDa (x2) &a+&8" + x2 + "\u279C&a+" + (x2 + skillData.incrementPerLevel()));
        }
        if(skill.level() == level) {
            lore.add(" ");
            int expRemaining = skill.expToNextLevel();
            int exp = skill.exp();
            float percentage = exp * 100f/expRemaining;
            lore.add("&7Progresso: &e" + decimalFormat.format(percentage) + "%");
            int green = Math.round(20 * percentage/100);
            StringBuilder levelString = new StringBuilder();
            if(green != 0) levelString.append("&a");
            levelString.append(PERCENTAGE_LITERAL.repeat(Math.max(0, green)));
            if(green != 20) levelString.append("&f");
            levelString.append(PERCENTAGE_LITERAL.repeat(Math.max(0, 20 - green)));
            levelString.append(" &e").append(exp).append("&6/&e").append(expRemaining);
            lore.add(levelString.toString());
        } else if(skill.level() > level) {
            lore.add(" ");
            lore.add("&aSBLOCCATO");
        }
        return lore;
    }

    private int[] buildHalfInventory(InventoryContents contents, int row, int column, Skill<? extends Event> skill, int level) {
        contents.set(SlotPos.of(++row, column), ClickableItem.empty(levelItem(skill, ++level)));
        contents.set(SlotPos.of(++row, column), ClickableItem.empty(levelItem(skill, ++level)));

        contents.set(SlotPos.of(row, ++column), ClickableItem.empty(levelItem(skill, ++level)));
        contents.set(SlotPos.of(row, ++column), ClickableItem.empty(levelItem(skill, ++level)));

        contents.set(SlotPos.of(--row, column), ClickableItem.empty(levelItem(skill, ++level)));
        contents.set(SlotPos.of(--row, column), ClickableItem.empty(levelItem(skill, ++level)));
        contents.set(SlotPos.of(--row, column), ClickableItem.empty(levelItem(skill, ++level)));

        contents.set(SlotPos.of(row, ++column), ClickableItem.empty(levelItem(skill, ++level)));
        contents.set(SlotPos.of(row, ++column), ClickableItem.empty(levelItem(skill, ++level)));

        contents.set(SlotPos.of(++row, column), ClickableItem.empty(levelItem(skill, ++level)));
        return new int[]{row, column, level};
    }
}
