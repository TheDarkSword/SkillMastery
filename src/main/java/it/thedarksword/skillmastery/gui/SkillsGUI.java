package it.thedarksword.skillmastery.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.player.SkillPlayer;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import it.thedarksword.skillmastery.skill.SkillType;
import it.thedarksword.skillmastery.utils.FormatNumber;
import it.thedarksword.skillmastery.utils.RomanNumber;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SkillsGUI extends CommonInventory implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("skills")
            .provider(new SkillsGUI())
            .size(6, 9)
            .title(ChatColor.WHITE + "\uf000\u3F99")
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        SkillPlayer skillPlayer = SkillMastery.instance().playerManager().skillPlayer(player);

        //Foraging
        ClickableItem mining = buildItem(skillPlayer.skill(SkillType.MINING));
        contents.set(SlotPos.of(0, 0), mining);
        contents.set(SlotPos.of(0, 1), mining);
        contents.set(SlotPos.of(1, 0), mining);
        contents.set(SlotPos.of(1, 1), mining);

        //Farming
        ClickableItem farming = buildItem(skillPlayer.skill(SkillType.FARMING));
        contents.set(SlotPos.of(0, 3), farming);
        contents.set(SlotPos.of(0, 4), farming);
        contents.set(SlotPos.of(0, 5), farming);
        contents.set(SlotPos.of(1, 3), farming);
        contents.set(SlotPos.of(1, 4), farming);
        contents.set(SlotPos.of(1, 5), farming);

        //Combat
        ClickableItem combat = buildItem(skillPlayer.skill(SkillType.COMBAT));
        contents.set(SlotPos.of(0, 7), combat);
        contents.set(SlotPos.of(0, 8), combat);
        contents.set(SlotPos.of(1, 7), combat);
        contents.set(SlotPos.of(1, 8), combat);

        //Enchanting
        ClickableItem enchanting = buildItem(skillPlayer.skill(SkillType.ENCHANTING));
        contents.set(SlotPos.of(2, 0), enchanting);
        contents.set(SlotPos.of(2, 1), enchanting);
        contents.set(SlotPos.of(3, 0), enchanting);
        contents.set(SlotPos.of(3, 1), enchanting);
        contents.set(SlotPos.of(4, 0), enchanting);
        contents.set(SlotPos.of(4, 1), enchanting);

        //Alchemy
        ClickableItem alchemy = buildItem(skillPlayer.skill(SkillType.ALCHEMY));
        contents.set(SlotPos.of(2, 3), alchemy);
        contents.set(SlotPos.of(2, 4), alchemy);
        contents.set(SlotPos.of(2, 5), alchemy);
        contents.set(SlotPos.of(3, 3), alchemy);
        contents.set(SlotPos.of(3, 4), alchemy);
        contents.set(SlotPos.of(3, 5), alchemy);
        contents.set(SlotPos.of(4, 3), alchemy);
        contents.set(SlotPos.of(4, 4), alchemy);
        contents.set(SlotPos.of(4, 5), alchemy);

        //Combat
        ClickableItem foraging = buildItem(skillPlayer.skill(SkillType.FORAGING));
        contents.set(SlotPos.of(2, 7), foraging);
        contents.set(SlotPos.of(2, 8), foraging);
        contents.set(SlotPos.of(3, 7), foraging);
        contents.set(SlotPos.of(3, 8), foraging);
        contents.set(SlotPos.of(4, 7), foraging);
        contents.set(SlotPos.of(4, 8), foraging);

        contents.set(SlotPos.of(5, 4), close);
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    private ClickableItem buildItem(Skill<? extends Event> skill) {
        SkillData skillData = skill.skillData();
        ItemStack stack = skillData.paper()
                .addLore(buildLore(skill)).build();
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(9);
        stack.setItemMeta(meta);
        return ClickableItem.of(stack, event -> SkillGUI.inventory(skillData.skillType()).open((Player) event.getWhoClicked()));
    }

    private List<String> buildLore(Skill<? extends Event> skill) {
        SkillData skillData = skill.skillData();
        List<String> lore = new ArrayList<>();
        String roman = RomanNumber.toRoman(skill.level()+1);

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
        levelString.append(" &e").append(FormatNumber.format(exp)).append("&6/&e").append(FormatNumber.format(expRemaining));
        lore.add(levelString.toString());

        lore.add("&7Ricompense:");
        lore.add(" &e" + skillData.name() + " " + roman);
        double x2 = skill.percentageX2();
        if(x2 == 100) {
            double x3 = skill.percentageX3();
            lore.add("  &fConcede &8" + decimalFormat.format(x3) + "\u279C&a" + decimalFormat.format(x3 + skillData.incrementPerLevel()));
        } else {
            lore.add("  &fConcede &8" + decimalFormat.format(x2) + "\u279C&a" + decimalFormat.format(x2 + skillData.incrementPerLevel()));
        }
        lore.addAll(skillData.description());

        lore.add(" ");
        lore.add("&eClicca per vedere");
        return lore;
    }
}
