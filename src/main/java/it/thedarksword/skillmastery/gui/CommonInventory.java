package it.thedarksword.skillmastery.gui;

import fr.minuskube.inv.ClickableItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class CommonInventory {

    protected static final ClickableItem fillItem = ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
    protected static final String PERCENTAGE_LITERAL = "-";
    protected static final DecimalFormat decimalFormat = new DecimalFormat("#.#");
}
