package it.thedarksword.skillmastery.listeners;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.player.SkillPlayer;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillType;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.BrewerInventory;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final SkillMastery skillMastery;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(skillMastery.playerManager().hasCache(player)) {
            skillMastery.playerManager().restorePlayer(player);
        } else {
            skillMastery.queryManager().skillPlayerAdd(player.getUniqueId()).exec();
            skillMastery.playerManager().skillPlayer(player, new SkillPlayer(player, skillMastery));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        handleQuit(event.getPlayer());
    }

    private void handleQuit(Player player) {
        skillMastery.playerManager().cachePlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(event.getBlock().getWorld()))
                .getApplicableRegions(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        for (ProtectedRegion region : regions.getRegions()) {
            if (region.getId().startsWith("miner")) {
                Skill<BlockBreakEvent> skill = skillMastery.playerManager().skillPlayer(event.getPlayer()).skill(SkillType.MINING);
                skill.experience();
                skill.process(event);
                return;
            }
        }
        Material type = event.getBlock().getType();
        if(skillMastery.blockManager().isLog(type)) {
            Skill<BlockBreakEvent> skill = skillMastery.playerManager().skillPlayer(event.getPlayer()).skill(SkillType.FORAGING);
            skill.experience();
            skill.process(event);
        } else if(skillMastery.blockManager().isFarm(type)) {
            Skill<BlockBreakEvent> skill = skillMastery.playerManager().skillPlayer(event.getPlayer()).skill(SkillType.FARMING);
            skill.experience();
            skill.process(event);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null) return;
        skillMastery.playerManager().skillPlayer(event.getEntity().getKiller()).<EntityDeathEvent>skill(SkillType.COMBAT).experience();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player player) || (event.getEntity() instanceof Player)) return;
        skillMastery.playerManager().skillPlayer(player).<EntityDamageByEntityEvent>skill(SkillType.COMBAT).process(event);
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if(event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Skill<PlayerFishEvent> skill = skillMastery.playerManager().skillPlayer(event.getPlayer()).skill(SkillType.FISHING);
            skill.experience();
            skill.process(event);
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        skillMastery.playerManager().skillPlayer(event.getEnchanter()).<EnchantItemEvent>skill(SkillType.ENCHANTING).experience();
    }

    @EventHandler
    public void onExp(PlayerExpChangeEvent event) {
        skillMastery.playerManager().skillPlayer(event.getPlayer()).<PlayerExpChangeEvent>skill(SkillType.ENCHANTING).process(event);
    }

    @EventHandler
    public void onBrew(InventoryClickEvent event) {
        if(!(event.getClickedInventory() instanceof BrewerInventory) || event.getCurrentItem() == null ||
                event.getCurrentItem().getType() != Material.POTION) return;
        ItemStack itemStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
        NBTTagCompound compound = itemStack.t();
        if(compound.b("taked")) return;

        Skill<InventoryClickEvent> skill = skillMastery.playerManager().skillPlayer((Player) event.getWhoClicked()).skill(SkillType.ALCHEMY);
        skill.experience();
        skill.process(event);

        compound.a("taked", true);
        event.setCurrentItem(CraftItemStack.asBukkitCopy(itemStack));
    }
}
