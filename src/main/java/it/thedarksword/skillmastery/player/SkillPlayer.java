package it.thedarksword.skillmastery.player;

import com.google.common.collect.Maps;
import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.sql.SQLException;
import java.util.Map;

@Getter
@Accessors(fluent = true)
public class SkillPlayer {

    private final Player player;
    private final Map<SkillType, Skill<? extends Event>> skills = Maps.newEnumMap(SkillType.class);
    private final SkillMastery skillMastery;
    @Setter private float multiplier;

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
                skills.put(SkillType.FARMING, skillMastery.skillProvider().provideSkill(SkillType.FARMING, resultSet.getInt(3), resultSet.getInt(4)));
                skills.put(SkillType.MINING, skillMastery.skillProvider().provideSkill(SkillType.MINING, resultSet.getInt(5), resultSet.getInt(6)));
                skills.put(SkillType.COMBAT, skillMastery.skillProvider().provideSkill(SkillType.COMBAT, resultSet.getInt(7), resultSet.getInt(8)));
                skills.put(SkillType.FORAGING, skillMastery.skillProvider().provideSkill(SkillType.FORAGING, resultSet.getInt(9), resultSet.getInt(10)));
                skills.put(SkillType.ENCHANTING, skillMastery.skillProvider().provideSkill(SkillType.ENCHANTING, resultSet.getInt(11), resultSet.getInt(12)));
                skills.put(SkillType.ALCHEMY, skillMastery.skillProvider().provideSkill(SkillType.ALCHEMY, resultSet.getInt(13), resultSet.getInt(14)));
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        if(player.hasPermission("skillmastery.multiplier.4")) {
            multiplier = 2.1f;
        } else if(player.hasPermission("skillmastery.multiplier.3")) {
            multiplier = 1.8f;
        } else if(player.hasPermission("skillmastery.multiplier.2")) {
            multiplier = 1.5f;
        } else if(player.hasPermission("skillmastery.multiplier.1")) {
            multiplier = 1.2f;
        } else {
            multiplier = 1;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> Skill<T> skill(SkillType skillType) {
        return (Skill<T>) this.skills.get(skillType);
    }

    public void save() {
        skillMastery.queryManager().skillPlayerSave(this).execAsync();
    }
}
