package it.thedarksword.skillmastery.potions;

import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PotionManager {

    private Map<PotionEffectType, Duration> durations = new HashMap<>();

    public PotionManager() {
        durations.put(PotionEffectType.LUCK, new Duration(300));

        durations.put(PotionEffectType.SPEED, new Duration(180));
        durations.put(PotionEffectType.SLOW, new Duration(180));
        durations.put(PotionEffectType.INCREASE_DAMAGE, new Duration(180));
        durations.put(PotionEffectType.JUMP, new Duration(180));
        durations.put(PotionEffectType.FIRE_RESISTANCE, new Duration(180));
        durations.put(PotionEffectType.WATER_BREATHING, new Duration(180));
        durations.put(PotionEffectType.INVISIBILITY, new Duration(180));
        durations.put(PotionEffectType.NIGHT_VISION, new Duration(180));

        durations.put(PotionEffectType.WEAKNESS, new Duration(90));
        durations.put(PotionEffectType.SLOW_FALLING, new Duration(90));

        durations.put(PotionEffectType.HEAL, new Duration(0));
        durations.put(PotionEffectType.HARM, new Duration(0));

        durations.put(PotionEffectType.REGENERATION, new Duration(45));
        durations.put(PotionEffectType.POISON, new Duration(45));
    }

    public int duration(PotionEffectType type) {
        return durations.get(type).normal();
    }

    public int duration(PotionEffectType type, boolean isExtended, boolean isUpgraded) {
        Duration duration = durations.get(type);
        if(duration == null) return 0;
        if(isUpgraded) return duration.upgraded();
        if(isExtended) return duration.extended();
        return duration.normal;
    }

    private record Duration(int normal, int extended, int upgraded) {

        public Duration(int normal) {
            this(normal, normal/3 * 8, normal/2);
        }
    }
}
