package it.thedarksword.skillmastery.skill;

import it.thedarksword.skillmastery.skill.skills.CombatSkill;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.event.Event;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum SkillType {

    COMBAT(CombatSkill::new);

    private final BiFunction<Integer, Integer, ? extends Skill<? extends Event>> supplier;
}
