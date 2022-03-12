package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingSkill implements Skill<PlayerFishEvent> {

    private static final SkillData skillData = new SkillData(50, 4, Material.FISHING_ROD, "Pesca",
            "&7Pesca per ottenere XP!");

    private int level;
    private int exp;

    private int x2;
    private int x3;

    public FishingSkill(int level, int exp) {
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
    public boolean process(PlayerFishEvent event) {
        int take = random.nextInt(100);
        Item item = (Item) event.getCaught();
        //item.setItemStack();
        return false;
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
