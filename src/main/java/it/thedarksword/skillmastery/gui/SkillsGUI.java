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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillsGUI extends CommonInventory implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("skills")
            .provider(new SkillsGUI())
            .size(6, 9)
            .title("Skills Menu")
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(fillItem);
        SkillPlayer skillPlayer = SkillMastery.instance().playerManager().skillPlayer(player);
        int column = 1;
        for (Map.Entry<SkillType, Skill<? extends Event>> entry : skillPlayer.skills().entrySet()) {
            contents.set(SlotPos.of(2, column++), ClickableItem.of(entry.getValue().skillData().builder()
                    .addLore(buildLore(entry.getValue())).build(), event -> SkillGUI.inventory(entry.getKey()).open((Player) event.getWhoClicked())));
        }

        contents.set(SlotPos.of(5, 4), ClickableItem.of(new ItemBuilder(Material.BARRIER)
                .setName("&cChiudi").build(), event -> event.getWhoClicked().closeInventory()));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    //TODO: Build Lore in base at skill data
    // ---- <- are 20
    private List<String> buildLore(Skill<? extends Event> skill) {
        SkillData skillData = skill.skillData();
        List<String> lore = new ArrayList<>();
        String roman = RomanNumber.toRoman(skill.level()+1);
        lore.add(" ");

        int expRemaining = skill.expToNextLevel();
        int exp = skill.exp();
        float percentage = exp * 100f/expRemaining;
        lore.add("&7Progresso per il livello " + roman + ": &3" + decimalFormat.format(percentage) + "%");
        int green = Math.round(20 * percentage/100);
        StringBuilder levelString = new StringBuilder();
        if(green != 0) levelString.append("&a");
        levelString.append(PERCENTAGE_LITERAL.repeat(Math.max(0, green)));
        if(green != 20) levelString.append("&f");
        levelString.append(PERCENTAGE_LITERAL.repeat(Math.max(0, 20 - green)));
        levelString.append(" &e").append(exp).append("&6/&e").append(expRemaining);
        lore.add(levelString.toString());

        lore.add("&7Ricompense:");
        lore.add(" " + skillData.name() + " " + roman);
        int x2 = skill.percentageX2();
        if(x2 == 100) {
            int x3 = skill.percentageX3();
            lore.add("  &fDa (x3) &a+&8" + x3 + "\u279C&a+" + (x3 + skillData.incrementPerLevel()));
        } else {
            lore.add("  &fDa (x2) &a+&8" + x2 + "\u279C&a+" + (x2 + skillData.incrementPerLevel()));
        }

        lore.add(" ");
        lore.add("&eClicca per vedere");
        return lore;
    }
}
