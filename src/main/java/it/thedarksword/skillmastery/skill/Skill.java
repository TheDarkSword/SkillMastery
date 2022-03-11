package it.thedarksword.skillmastery.skill;

import org.bukkit.event.Event;

public interface Skill<E extends Event> {

    /**
     * Get the current level of the skill
     * @return the current level
     */
    int level();

    /**
     * Get the current experience of the skill
     * @return the current experience
     */
    int exp();

    /**
     * Increment experience of the skill, if max increment level
     * @param exp new experience
     */
    void exp(int exp);

    default void experience() {
        exp(level() < 30 ? exp() + 9 : exp() + 15);
    }

    /**
     * Process the event calculating the effect of the skill and apply it if necessary
     * @param event the event handled by skill
     * @return true if the effect is applied, false otherwise
     */
    boolean process(E event);

    /**
     * Get the skill data of the skill that contains constant values
     * @return skill data
     */
    SkillData skillData();
}
