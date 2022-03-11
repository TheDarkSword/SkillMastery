package it.thedarksword.skillmastery.player;

import com.google.common.collect.Maps;
import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.sql.SQLException;
import java.util.Map;

@Getter
@Accessors(fluent = true)
public class SkillPlayer {

    private final Player player;
    @Getter(AccessLevel.PRIVATE)
    private final Map<SkillType, Skill<? extends Event>> skills = Maps.newEnumMap(SkillType.class);
    private final SkillMastery skillMastery;

    public SkillPlayer(Player player, SkillMastery skillMastery) {
        this.player = player;
        this.skillMastery = skillMastery;
        skillMastery.queryManager().skillPlayerQuery(player.getUniqueId()).execReturnAsync().thenAccept(resultSet -> {
            try {
                if(!resultSet.isBeforeFirst()) {
                    for (SkillType skill : SkillType.values()) {
                        skills.put(skill, skillMastery.skillProvider().provideSkill(skill));
                    }
                    return;
                }
                resultSet.next();
                skills.put(SkillType.COMBAT, skillMastery.skillProvider().provideSkill(SkillType.COMBAT, resultSet.getInt(0), resultSet.getInt(1)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Skill<? extends Event> skill(SkillType skillType) {
        return this.skills.get(skillType);
    }
}
