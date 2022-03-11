package it.thedarksword.skillmastery.skill;

import it.thedarksword.skillmastery.skill.skills.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.event.Event;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum SkillType {

    FARMING(FarmingSkill::new),
    MINING(MiningSkill::new),
    COMBAT(CombatSkill::new),
    FORAGING(ForagingSkill::new),
    FISHING(FishingSkill::new),
    ENCHANTING(EnchantingSkill::new),
    ALCHEMY(AlchemySkill::new);

    private final BiFunction<Integer, Integer, ? extends Skill<? extends Event>> supplier;
}
