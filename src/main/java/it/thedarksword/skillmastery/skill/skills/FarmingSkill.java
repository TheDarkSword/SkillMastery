package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class FarmingSkill implements Skill<BlockBreakEvent> {

    private static final SkillData skillData = new SkillData(50, 4, Material.GOLDEN_HOE, "Farming",
            "&7Raccogli l'orto e tosa le pecore per", "&7ottenere XP!");

    private int level;
    private int exp;

    private int x2;
    private int x3;

    public FarmingSkill(int level, int exp) {
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

    private void doEvent(BlockBreakEvent event) {
        World world = event.getBlock().getWorld();
        Location location = event.getBlock().getLocation();
        for (ItemStack drop : event.getBlock().getDrops()) {
            world.dropItemNaturally(location, drop);
        }
    }
}
