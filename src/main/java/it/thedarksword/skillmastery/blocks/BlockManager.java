package it.thedarksword.skillmastery.blocks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BlockManager {

    private final CoreProtectAPI coreProtectAPI;
    private final Cache<Location, Block> placedBlocks = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private final Set<Material> logs = Sets.newEnumSet(Collections.emptySet(), Material.class);
    private final Set<Material> farm = Sets.newEnumSet(Collections.emptySet(), Material.class);

    public BlockManager(CoreProtectAPI coreProtectAPI) {
        this.coreProtectAPI = coreProtectAPI;

        loadLogs();
        loadFarm();
    }

    public boolean isPlaced(Block block) {
        if(placedBlocks.getIfPresent(block.getLocation()) != null) return true;
        List<String[]> results = coreProtectAPI.blockLookup(block, 0);
        for (String[] result : results) {
            if(coreProtectAPI.parseResult(result).getActionId() == 1) {
                placedBlocks.put(block.getLocation(), block);
                return true;
            }
        }
        return false;
    }

    public boolean isLog(Material material) {
        return logs.contains(material);
    }

    public boolean isFarm(Material material) {
        return farm.contains(material);
    }

    private void loadLogs() {
        logs.add(Material.OAK_LOG);
        logs.add(Material.SPRUCE_LOG);
        logs.add(Material.BIRCH_LOG);
        logs.add(Material.JUNGLE_LOG);
        logs.add(Material.ACACIA_LOG);
        logs.add(Material.DARK_OAK_LOG);

        logs.add(Material.STRIPPED_OAK_LOG);
        logs.add(Material.STRIPPED_SPRUCE_LOG);
        logs.add(Material.STRIPPED_BIRCH_LOG);
        logs.add(Material.STRIPPED_JUNGLE_LOG);
        logs.add(Material.STRIPPED_ACACIA_LOG);
        logs.add(Material.STRIPPED_DARK_OAK_LOG);
    }

    private void loadFarm() {
        farm.add(Material.WHEAT);
    }
}
