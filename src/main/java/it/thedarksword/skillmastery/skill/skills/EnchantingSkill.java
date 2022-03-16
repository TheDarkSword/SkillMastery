package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import it.thedarksword.skillmastery.skill.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.List;

public class EnchantingSkill implements Skill<PlayerExpChangeEvent> {

    private static final SkillData skillData = new SkillData(50, 4, SkillType.ENCHANTING, Material.ENCHANTING_TABLE,
            "Enchanting", List.of(ChatColor.WHITE + "  Guadagna più esperienza",
            ChatColor.WHITE + "  da tutte le fonti"),
            ChatColor.WHITE + "   Incrementa la tua possibilità di ottenere più minerali",
            "&7Enchanta gli item per", "&7guadagnare XP!");

    private int level;
    private int exp;

    private double x2;
    private double x3;

    public EnchantingSkill(int level, int exp) {
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
    public boolean process(PlayerExpChangeEvent event) {
        int take = random.nextInt(100);
        int finalAmount = event.getAmount();
        boolean result = false;
        if(x3 == 0) {
            if(take < x2) {
                finalAmount *= 2;
                result = true;
            }
        } else if(take < x3) {
            finalAmount *= 3;
            result = true;
        } else {
            finalAmount *= 2;
            result = true;
        }
        event.setAmount(finalAmount);
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
}
