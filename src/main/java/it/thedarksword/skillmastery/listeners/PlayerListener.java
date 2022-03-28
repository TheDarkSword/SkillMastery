package it.thedarksword.skillmastery.listeners;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.player.SkillPlayer;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillData;
import it.thedarksword.skillmastery.skill.SkillType;
import it.thedarksword.skillmastery.utils.FormatNumber;
import it.thedarksword.skillmastery.utils.RomanNumber;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final DecimalFormat decimalFormat = new DecimalFormat("#.#");

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBoneMeal(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null && event.getItem().getType() == Material.BONE_MEAL && event.getClickedBlock() != null) {
            event.getClickedBlock().setMetadata("bone", new FixedMetadataValue(skillMastery.plugin(), true));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        Location location = event.getBlock().getLocation();
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(event.getBlock().getWorld()))
                .getApplicableRegions(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        for (ProtectedRegion region : regions.getRegions()) {
            if (region.getId().startsWith("miner")) {
                SkillPlayer skillPlayer = skillMastery.playerManager().skillPlayer(event.getPlayer());
                Skill<BlockBreakEvent> skill = skillPlayer.skill(SkillType.MINING);
                experienceSkill(skillPlayer, skill);
                skill.process(event);
                return;
            }
        }
        Material type = event.getBlock().getType();
        if(skillMastery.blockManager().isLog(type)) {
            SkillPlayer skillPlayer = skillMastery.playerManager().skillPlayer(event.getPlayer());
            Skill<BlockBreakEvent> skill = skillPlayer.skill(SkillType.FORAGING);
            experienceSkill(skillPlayer, skill);
            skill.process(event);
        } else if(skillMastery.blockManager().isFarm(type) && !event.getBlock().hasMetadata("bone")) {
            Ageable ageable = (Ageable) event.getBlock().getBlockData();
            if(ageable.getAge() != ageable.getMaximumAge()) return;
            SkillPlayer skillPlayer = skillMastery.playerManager().skillPlayer(event.getPlayer());
            Skill<BlockBreakEvent> skill = skillPlayer.skill(SkillType.FARMING);
            experienceSkill(skillPlayer, skill);
            skill.process(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null) return;
        SkillPlayer skillPlayer = skillMastery.playerManager().skillPlayer(event.getEntity().getKiller());
        experienceSkill(skillPlayer, skillPlayer.<EntityDeathEvent>skill(SkillType.COMBAT));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player player) || (event.getEntity() instanceof Player)) return;
        skillMastery.playerManager().skillPlayer(player).<EntityDamageByEntityEvent>skill(SkillType.COMBAT).process(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        SkillPlayer skillPlayer = skillMastery.playerManager().skillPlayer(event.getEnchanter());
        experienceSkill(skillPlayer, skillPlayer.<EnchantItemEvent>skill(SkillType.ENCHANTING));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExp(PlayerExpChangeEvent event) {
        skillMastery.playerManager().skillPlayer(event.getPlayer()).<PlayerExpChangeEvent>skill(SkillType.ENCHANTING).process(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBrew(InventoryClickEvent event) {
        if(!(event.getClickedInventory() instanceof BrewerInventory) || event.getCurrentItem() == null ||
                event.getCurrentItem().getType() != Material.POTION) return;
        ItemStack itemStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
        NBTTagCompound compound = itemStack.t();
        if(compound.e("took")) return;
        Player player = (Player) event.getWhoClicked();
        SkillPlayer skillPlayer = skillMastery.playerManager().skillPlayer(player);

        Skill<InventoryClickEvent> skill = skillPlayer.skill(SkillType.ALCHEMY);
        experienceSkill(skillPlayer, skill);
        skill.process(event);

        compound.a("took", true);
        event.setCurrentItem(CraftItemStack.asBukkitCopy(itemStack));
    }

    private void experienceSkill(SkillPlayer skillPlayer, Skill<? extends Event> skill) {
        Player player = skillPlayer.player();
        if(skill.experience(skillPlayer)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_AQUA + "Level UP " + skill.skillData().name() +
                    " (" + skill.level() + "/" + skill.skillData().maxLevel() + ")"));
            levelUpMessage(player, skill);
            EconomyResponse r = skillMastery.economy().depositPlayer(player, skill.money());
            if(!r.transactionSuccess()) {
                player.sendMessage(ChatColor.RED + "Errore durante il pagamento. Fai uno screenshot e contatta un amministratore (" + r.errorMessage + ")");
            }
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_AQUA + "+" + (skill.level() < 30 ? 9 : 15) + " " +
                    skill.skillData().name() + " (" + skill.exp() + "/" + skill.expToNextLevel() + ")"));
        }
    }

    private void levelUpMessage(Player player, Skill<? extends Event> skill) {
        SkillData skillData = skill.skillData();
        String name = StringUtils.capitalize(skillData.name());
        String romanLevel = RomanNumber.toRoman(skill.level());
        String oldPercentage = percentage(skill.level()-1, skillData);
        String percentage = percentage(skill.level(), skillData);


        player.sendMessage(ChatColor.DARK_AQUA + "--------------------------------");
        player.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + " SKILL LEVEL UP " +
                ChatColor.DARK_AQUA + name + " " +
                ChatColor.DARK_GRAY + (skill.level()-1 == 0 ? "0" : RomanNumber.toRoman(skill.level()-1)) +
                "\u279C" + ChatColor.DARK_AQUA + romanLevel);
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + " REWARDS");
        player.sendMessage(ChatColor.YELLOW + "  " + name + " " + romanLevel);
        player.sendMessage(ChatColor.WHITE + "   Concede " + ChatColor.DARK_GRAY + oldPercentage + "\u279C" +
                ChatColor.GREEN + percentage);
        player.sendMessage(skillData.chatDescription());
        player.sendMessage(ChatColor.GOLD + "   +" + FormatNumber.format(skill.money()) + "€");
        player.sendMessage(ChatColor.DARK_AQUA + "--------------------------------");
    }

    private String percentage(int level, SkillData skillData) {
        double x2 = Skill.calculatePercentage(level, skillData);
        if(x2 == 100) {
            double x3 = Skill.calculatePercentage(level-25, skillData);
            return decimalFormat.format(x3);
        } else {
            return decimalFormat.format(x2);
        }
    }
}
