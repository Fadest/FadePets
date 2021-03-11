package dev.fadest.pets.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if(event.getInventory().getHolder() instanceof Menu) {
                Menu menu = (Menu) event.getInventory().getHolder();
                event.setResult(Event.Result.DENY);
                if(event.getRawSlot() >= menu.getInventory().getSize()) {
                    return;
                }

                menu.onClick((Player) event.getWhoClicked(), event.getSlot(), event.getCurrentItem(), event.getClick());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Menu) {
            Menu menu = (Menu) event.getInventory().getHolder();
            menu.close((Player) event.getPlayer(), event.getInventory());
        }
    }

}
