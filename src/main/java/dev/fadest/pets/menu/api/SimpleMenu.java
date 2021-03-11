package dev.fadest.pets.menu.api;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * Represents an Inventory GUI containing items that players can interact with.
 */
public class SimpleMenu implements Menu {

    private final String name;
    private final int rows;
    private final Map<Integer, MenuItem> contents;
    private BiConsumer<Player, Inventory> closeAction;

    private ItemStack fillItem = null;

    /**
     * Creates a new menu with a given display name and a default size of 6 rows.
     *
     * @param name The name of the menu (can contain '&' color codes).
     */
    public SimpleMenu(String name) {
        this(name, 6);
    }

    /**
     * Creates a new menu with a given display name and a size (in row numbers).
     *
     * @param name The name of the menu (can contain '&' color codes).
     * @param rows The number of rows for the menu (between 1 and 6 inclusive).
     */
    public SimpleMenu(String name, int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6!");
        }
        this.name = name;
        this.rows = rows;
        this.contents = new HashMap<>();
        this.closeAction = null;
    }

    /**
     * Adds a menu item to this menu.
     *
     * @param slot The slot for the item.
     * @param item The item to add.
     */
    public void addItem(int slot, MenuItem item) {
        this.contents.put(slot, item);
    }

    /**
     * Sets the fill item for this menu. When the menu is displayed to a player any empty slots will be filled with this item.
     *
     * @param itemStack The item used for filling empty slots.
     */
    public void setFillItem(ItemStack itemStack) {
        this.fillItem = itemStack;
    }

    public void setCloseAction(BiConsumer<Player, Inventory> closeAction) {
        this.closeAction = closeAction;
    }

    /**
     * Creates and returns the inventory using this custom holder.
     *
     * @return The inventory.
     */
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.rows * 9,
                ChatColor.translateAlternateColorCodes('&', this.name));

        for (Map.Entry<Integer, MenuItem> menuItem : this.contents.entrySet()) {
            inventory.setItem(menuItem.getKey(), menuItem.getValue().getItemStack());
        }

        if(fillItem != null) IntStream.rangeClosed(0, (rows * 9) -1).forEach(value -> inventory.setItem(value, fillItem));

        return inventory;
    }

    /**
     * Invoked whenever an item is clicked in the menu by a player.
     *
     * @param whoClicked       The player who clicked the item.
     * @param slot             The slot number of the item clicked.
     * @param clickedItemStack The item stack clicked.
     * @param clickType        The type of click performed.
     */
    @Override
    public void onClick(Player whoClicked, int slot, ItemStack clickedItemStack,
                        ClickType clickType) {
        if (clickedItemStack == null || clickedItemStack.getType() == Material.AIR) {
            return;
        }

        if (this.contents.containsKey(slot)) {
            this.contents.get(slot).execute(clickType, whoClicked);
        }
    }

    /**
     * Opens the inventory for a player.
     *
     * @param player The player to show the inventory to.
     */
    @Override
    public void open(Player player) {
        player.openInventory(this.getInventory());
        player.updateInventory();
    }

    /**
     * Close player inventory
     *
     * @param player    The player that closes the inventory
     * @param inventory The result inventory
     */
    @Override
    public void close(Player player, Inventory inventory) {
        if (closeAction != null) {
            closeAction.accept(player, inventory);
        }
    }

}

