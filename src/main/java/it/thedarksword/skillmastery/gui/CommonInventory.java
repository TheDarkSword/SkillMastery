package it.thedarksword.skillmastery.gui;

import fr.minuskube.inv.ClickableItem;
import it.thedarksword.basement.api.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;

public class CommonInventory {

    protected static final String PERCENTAGE_LITERAL = "-";
    protected static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    protected static final ClickableItem close;

    static {
        ItemStack stack = new ItemBuilder(Material.PAPER)
                .setName("&7Chiudi").build();
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(6);
        stack.setItemMeta(meta);
        close = ClickableItem.of(stack, event -> event.getWhoClicked().closeInventory());
    }
}
