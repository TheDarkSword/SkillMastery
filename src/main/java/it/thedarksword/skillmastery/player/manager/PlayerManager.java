package it.thedarksword.skillmastery.player.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.thedarksword.skillmastery.player.SkillPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

    private final Map<Player, SkillPlayer> skillPlayers = new WeakHashMap<>();

    private final Cache<String, SkillPlayer> skillPlayerCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public SkillPlayer skillPlayer(Player player) {
        return skillPlayers.get(player);
    }

    public void skillPlayer(Player player, SkillPlayer skillPlayer) {
        skillPlayers.put(player, skillPlayer);
    }

    public SkillPlayer skillPlayer(String player) {
        return skillPlayerCache.getIfPresent(player);
    }

    public void restorePlayer(Player player) {
        skillPlayer(player, skillPlayer(player.getName()));
        skillPlayerCache.invalidate(player.getName());
    }

    public boolean hasCache(Player player) {
        return skillPlayer(player.getName()) != null;
    }

    public void cachePlayer(Player player) {
        skillPlayerCache.put(player.getName(), skillPlayer(player));
        skillPlayers.remove(player);
    }
}
