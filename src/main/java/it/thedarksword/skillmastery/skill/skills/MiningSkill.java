package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import it.thedarksword.skillmastery.skill.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MiningSkill implements Skill<BlockBreakEvent> {

    private static final SkillData skillData = new SkillData(50, 4, SkillType.MINING, Material.STONE_PICKAXE,
            "Minatore", List.of(ChatColor.GOLD + "  \u2618 Fortuna del Minatore",
            ChatColor.WHITE + "  Incrementa la tua possibilità", ChatColor.WHITE + "  di ottenere più minerali"),
            ChatColor.GOLD + "   \u2618 Fortuna del Minatore " + ChatColor.WHITE +
                    "Incrementa la tua possibilità di ottenere più minerali",
            "&7Scava i minerali per", "&7ottenere XP!");

    private int level;
    private int exp;

    private double x2;
    private double x3;

    public MiningSkill(int level, int exp) {
        this.level = level;
        this.exp = exp;
        recalculatePercentage();
    }

    @Override
    public int level() {
        return level;
    }

    @Override
    public void level(int level) {
        this.level = level;
        recalculatePercentage();
    }

    @Override
    public int exp() {
        return exp;
    }

    @Override
    public void exp(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean process(BlockBreakEvent event) {
        if(event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) return false;
        int take = random.nextInt(100);
        boolean result = false;
        if(x3 == 0) {
            if(take < x2) {
                doEvent(event);
                result = true;
            }
        } else {
            if(take < x3) {
                doEvent(event);
            }
            doEvent(event);
            result = true;
        }
        return result;
    }

    @Override
    public double percentageX2() {
        return x2;
    }

    @Override
    public double percentageX3() {
        return x3;
    }

    @Override
    public void percentageX2(double x2) {
        this.x2 = x2;
    }

    @Override
    public void percentageX3(double x3) {
        this.x3 = x3;
    }

    @Override
    public SkillData skillData() {
        return skillData;
    }

    private void doEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        for (ItemStack drop : event.getBlock().getDrops(tool)) {
            if(tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
               drop.setAmount(1);
            } else {
                drop.setAmount(getDropCount(tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
            }
            player.getInventory().addItem(drop);
        }
    }

    private int getDropCount(int fortuneLevel) {
        if (fortuneLevel > 0) {
            int drops = random.nextInt(fortuneLevel + 2) - 1;
            if (drops < 0) {
                drops = 0;
            }
            return drops + 1;
        }
        return 1;
    }
}
