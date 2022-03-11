package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatSkill implements Skill<EntityDamageByEntityEvent> {

    private static final SkillData skillData = new SkillData(50, 1.5);

    private int level;
    private int exp;

    private int x2;
    private int x3;

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
    public SkillData skillData() {
        return skillData;
    }

    private void recalculatePercentage() {
        if(level <= 25) {
            this.x2 = (int) Math.round(level * skillData.incrementPerLevel());
        } else {
            this.x2 = (int) Math.round(25 * skillData.incrementPerLevel());
            this.x3 = (int) Math.round(level-25 * skillData.incrementPerLevel());
        }
    }
}
