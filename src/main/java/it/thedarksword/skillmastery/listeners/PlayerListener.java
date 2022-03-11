package it.thedarksword.skillmastery.listeners;

import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.player.SkillPlayer;
import it.thedarksword.skillmastery.skill.SkillType;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener {

    private final SkillMastery skillMastery;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(skillMastery.playerManager().hasCache(player)) {
            skillMastery.playerManager().restorePlayer(player);
        } else {
            skillMastery.playerManager().skillPlayer(player, new SkillPlayer(player, skillMastery));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleQuit(event.getPlayer());
    }

    public void onKick(PlayerKickEvent event) {
        handleQuit(event.getPlayer());
    }

    private void handleQuit(Player player) {
        skillMastery.playerManager().cachePlayer(player);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null) return;
        skillMastery.playerManager().skillPlayer(event.getEntity().getKiller()).skill(SkillType.COMBAT).experience();
    }
}
