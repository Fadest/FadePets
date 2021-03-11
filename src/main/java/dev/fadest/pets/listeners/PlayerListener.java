package dev.fadest.pets.listeners;

import dev.fadest.pets.manager.PetManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final PetManager petManager;

    public PlayerListener(PetManager petManager) {
        this.petManager = petManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        petManager.disable(event.getPlayer());
    }
}
