package it.thedarksword.skillmastery.skill.provider;

import com.google.common.collect.Maps;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillType;
import org.bukkit.event.Event;

import java.util.Map;
import java.util.function.BiFunction;

public final class SkillProvider {

    private final Map<SkillType, BiFunction<Integer, Integer, ? extends Skill<? extends Event>>> skillProvider = Maps.newEnumMap(SkillType.class);

    public SkillProvider() {
        for (SkillType skill : SkillType.values()) {
            skillProvider.put(skill, skill.supplier());
        }
    }

    public Skill<? extends Event> provideSkill(SkillType skillType, int level, int exp) {
        return skillProvider.get(skillType).apply(level, exp);
    }

    public Skill<? extends Event> provideSkill(SkillType skillType) {
        return provideSkill(skillType, 1, 0);
    }
}
