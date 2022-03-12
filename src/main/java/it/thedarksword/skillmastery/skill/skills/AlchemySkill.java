package it.thedarksword.skillmastery.skill.skills;

import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AlchemySkill implements Skill<InventoryClickEvent> {

    private static final SkillData skillData = new SkillData(50, 4, Material.BREWING_STAND, "Alchimia",
            "&7Prepara le pozioni per", "&7guadagnare XP!");

    private int level;
    private int exp;

    private int x2;
    private int x3;

    public AlchemySkill(int level, int exp) {
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

    //TODO Check if work
    @Override
    public boolean process(InventoryClickEvent event) {
        int take = random.nextInt(100);
        int amplifier;
        if(x3 == 0) {
            if(take < x2) {
                amplifier = 2;
            } else {
                amplifier = 1;
            }
        } else if(take < x3) {
            amplifier = 3;
        } else {
            amplifier = 2;
        }
        ItemStack potion = event.getCurrentItem();
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        PotionData potionData = meta.getBasePotionData();
        PotionEffectType type = potionData.getType().getEffectType();
        meta.addCustomEffect(new PotionEffect(type,
                SkillMastery.instance().potionManager().duration(type, potionData.isExtended(), potionData.isUpgraded()), amplifier), true);
        return amplifier != 1;
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
