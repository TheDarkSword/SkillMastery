package it.thedarksword.skillmastery.skill;

import it.thedarksword.skillmastery.player.SkillPlayer;
import org.bukkit.event.Event;

import java.util.List;
import java.util.Random;

public interface Skill<E extends Event> {

    Random random = new Random();

    List<Integer> levelUpExp = List.of(100, 250, 400, 600, 1000, 1500, 2000, 3000, 4000, 7000, 10000, 15000, 20000, 30000, 40000, 60000, 100000, 150000, 200000, 400_000,
            600_000, 800_000, 1_000_000, 1_200_000, 1_400_000, 1_600_000, 1_800_000, 2_000_000, 2_200_000, 2_400_000, 2_600_000, 2_800_000, 3_000_000, 3_200_000, 3_400_000,
            3_600_000, 3_800_000, 4_000_000, 4_200_000, 4_400_000, 4_600_000, 4_800_000, 5_000_000, 5_200_000, 5_500_000, 5_800_000, 6_200_000, 6_800_000,
            7_400_000, 8_000_000);

    static double calculatePercentage(int level, SkillData skillData) {
        return level * skillData.incrementPerLevel();
    }

    /**
     * Get the current level of the skill
     * @return the current level
     */
    int level();

    /**
     * Set the new level of the skill
     * @param level the new level
     */
    void level(int level);

    /**
     * Get the current experience of the skill
     * @return the current experience
     */
    int exp();

    /**
     * Set experience of the skill
     * @param exp new experience
     */
    void exp(int exp);

    /**
     * Increment experience of the skill, if max call levelUp
     * @return true if levelup
     */
    default boolean experience(SkillPlayer player) {
        exp(level() < 30 ? exp() + Math.round(9 * player.multiplier()): exp() + Math.round(15 * player.multiplier()));
        if(levelUpExp.get(level()) <= exp()) {
            levelUp();
            return true;
        }
        return false;
    }

    /**
     * Increment the level by 1 and reset the experience to 0
     */
    default void levelUp() {
        level(level()+1);
        exp(0);
    }

    /**
     * Process the event calculating the effect of the skill and apply it if necessary
     * @param event the event handled by skill
     * @return true if the effect is applied, false otherwise
     */
    boolean process(E event);

    default int expToNextLevel() {
        if(level() == skillData().maxLevel()) return -1;
        return levelUpExp.get(level());
    }

    default int money() {
        return level() <= 10 ? 5000 : 10000;
    }

    /**
     * Get the percentage of activation of skill x2
     * @return the percentage of activation of skill x2
     */
    double percentageX2();

    /**
     * Set the percentage of activation of skill x2
     * @param x2 the percentage of activation of skill x2
     */
    void percentageX2(double x2);

    /**
     * Get the percentage of activation of skill x3
     * @return the percentage of activation of skill x3
     */
    double percentageX3();

    /**
     * Set the percentage of activation of skill x3
     * @param x3 the percentage of activation of skill x3
     */
    void percentageX3(double x3);

    /**
     * Get the skill data of the skill that contains constant values
     * @return skill data
     */
    SkillData skillData();

    default void recalculatePercentage() {
        if(level() <= 25) {
            percentageX2(Skill.calculatePercentage(level(), skillData()));
        } else {
            percentageX2(Skill.calculatePercentage(25, skillData()));
            percentageX3(Skill.calculatePercentage(level()-25, skillData()));
        }
    }
}
