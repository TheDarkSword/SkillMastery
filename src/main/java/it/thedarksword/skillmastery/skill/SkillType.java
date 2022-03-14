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

    FARMING(FarmingSkill::new, "\uf000\u3F03"),
    MINING(MiningSkill::new, "\uf000\u3F05"),
    COMBAT(CombatSkill::new, "\uf000\u3F01"),
    FORAGING(ForagingSkill::new, "\uf000\u3F04"),
    ENCHANTING(EnchantingSkill::new, "\uf000\u3F02"),
    ALCHEMY(AlchemySkill::new, "\uf000\u3F00");

    private final BiFunction<Integer, Integer, ? extends Skill<? extends Event>> supplier;
    private final String inventoryName;
}
