package dev.fadest.pets.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface Menu extends InventoryHolder {

    void onClick(Player whoClicked, int slot, ItemStack clickedItemStack,
                 ClickType clickType);

    void open(Player player);

    void close(Player player, Inventory inventory);

}
