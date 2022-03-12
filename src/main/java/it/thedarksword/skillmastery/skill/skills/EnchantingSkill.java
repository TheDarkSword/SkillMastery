package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class EnchantingSkill implements Skill<PlayerExpChangeEvent> {

    private static final SkillData skillData = new SkillData(50, 4, Material.ENCHANTING_TABLE, "Enchanting",
            "&7Enchanta gli item per", "&7guadagnare XP!");

    private int level;
    private int exp;

    private int x2;
    private int x3;

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
    public int percentageX2() {
        return x2;
    }

    @Override
    public int percentageX3() {
        return x3;
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
