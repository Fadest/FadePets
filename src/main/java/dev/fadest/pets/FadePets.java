package dev.fadest.pets;

import dev.fadest.pets.commands.PetCommand;
import dev.fadest.pets.listeners.ArmorStandListener;
import dev.fadest.pets.listeners.PlayerListener;
import dev.fadest.pets.menu.PetMenu;
import dev.fadest.pets.manager.PetManager;
import dev.fadest.pets.menu.api.MenuListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public class FadePets extends JavaPlugin {

    private final PetManager petManager;
    private final PetMenu petMenu;

    public FadePets() {
        this.petManager = new PetManager(this);
        this.petMenu = new PetMenu(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        petManager.loadPets();
        petMenu.loadMenu();

        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new ArmorStandListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(petManager), this);

        Objects.requireNonNull(getCommand("pet")).setExecutor(new PetCommand(petManager, petMenu));
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(petManager::disable);
    }
}
