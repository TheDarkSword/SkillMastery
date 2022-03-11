package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import lombok.AllArgsConstructor;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@AllArgsConstructor
public class CombatSkill implements Skill<EntityDamageByEntityEvent> {

    private static final SkillData skillData = new SkillData(50, 1.5);

    private int level;
    private int exp;

    @Override
    public int level() {
        return level;
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
        return false;
    }

    @Override
    public SkillData skillData() {
        return skillData;
    }
}
