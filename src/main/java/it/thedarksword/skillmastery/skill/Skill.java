package it.thedarksword.skillmastery.skill;

import org.bukkit.event.Event;

import java.util.List;
import java.util.Random;

public interface Skill<E extends Event> {

    Random random = new Random();

    List<Integer> levelUpExp = List.of(50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500, 5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 200_000,
            300_000, 400_000, 500_000, 600_000, 700_000, 800_000, 900_000, 1_000_000, 1_100_000, 1_200_000, 1_300_000, 1_400_000, 1_500_000, 1_600_000, 1_700_000,
            1_800_000, 1_900_000, 2_000_000, 2_100_000, 2_200_000, 2_300_000, 2_400_000, 2_500_000, 2_600_000, 2_750_000, 2_900_000, 3_100_000, 3_400_000,
            3_700_000, 4_000_000);

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
    default boolean experience() {
        exp(level() < 30 ? exp() + 9 : exp() + 15);
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
