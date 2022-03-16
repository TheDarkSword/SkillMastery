package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import it.thedarksword.skillmastery.skill.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class CombatSkill implements Skill<EntityDamageByEntityEvent> {

    private static final SkillData skillData = new SkillData(50, 1.5,  SkillType.COMBAT, Material.STONE_SWORD,
            "Combattimento", List.of(ChatColor.WHITE + "  Infliggi più",
            ChatColor.WHITE + "  danno ai mob"),
            ChatColor.WHITE + "   Infliggi più danno ai mob",
            "&7Uccidi i mob per ottenere XP!");

    private int level;
    private int exp;

    private double x2;
    private double x3;

    public CombatSkill(int level, int exp) {
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
    public boolean process(EntityDamageByEntityEvent event) {
        int take = random.nextInt(100);
        double finalDamage = event.getFinalDamage();
        boolean result = false;
        if(x3 == 0) {
            if(take < x2) {
                finalDamage *= 2;
                result = true;
            }
        } else if(take < x3) {
            finalDamage *= 3;
            result = true;
        } else {
            finalDamage *= 2;
            result = true;
        }
        event.setDamage(finalDamage);
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
