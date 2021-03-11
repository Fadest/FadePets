package dev.fadest.pets.menu;

import dev.fadest.pets.FadePets;
import dev.fadest.pets.manager.Pet;
import dev.fadest.pets.manager.PetManager;
import dev.fadest.pets.menu.api.MenuItemBuilder;
import dev.fadest.pets.menu.api.SimpleMenu;
import dev.fadest.pets.util.ItemUtils;
import dev.fadest.pets.util.StringUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PetMenu {

    @Getter
    private SimpleMenu menu;

    private final FadePets plugin;
    private final PetManager petManager;

    public PetMenu(FadePets plugin) {
        this.plugin = plugin;
        this.petManager = plugin.getPetManager();
    }

    public void loadMenu() {
        FileConfiguration fileConfiguration = plugin.getConfig();
        ConfigurationSection menuConfigurationSection = fileConfiguration.getConfigurationSection("menu");
        if (menuConfigurationSection == null) return;

        String title = menuConfigurationSection.getString("title", "&cPets");
        int rows = menuConfigurationSection.getInt("rows", 3);

        ConfigurationSection itemsConfigurationSection = menuConfigurationSection.getConfigurationSection("items");
        if(itemsConfigurationSection == null) {
            throw new IllegalArgumentException("Pet Menu doesn't contains items");
        }

        SimpleMenu simpleMenu = new SimpleMenu(title, rows);

        ConfigurationSection fillConfigurationSection = menuConfigurationSection.getConfigurationSection("fill");
        if(fillConfigurationSection != null) {
            ItemStack fillItem = ItemUtils.loadItem(fillConfigurationSection);
            if(fillConfigurationSection.getBoolean("fill", true)) {
                simpleMenu.setFillItem(fillItem);
            }
        }

        for(String itemSlot : itemsConfigurationSection.getKeys(false)) {
            try {
                ConfigurationSection itemConfigurationSection = itemsConfigurationSection.getConfigurationSection(itemSlot);
                if(itemConfigurationSection == null) continue;

                int slot = Integer.parseInt(itemSlot);
                ItemStack itemStack = ItemUtils.loadItem(itemConfigurationSection);
                String petToOpen = itemConfigurationSection.getString("pet");

                MenuItemBuilder menuItemBuilder = new MenuItemBuilder(itemStack);

                if(petToOpen != null) {
                    Pet pet = petManager.fromName(petToOpen);
                    if(pet == null) {
                        throw new IllegalArgumentException("The pet '" + petToOpen + "' at '" + itemSlot + "'" +
                                "in Pet Menu does not exist.");
                    }

                    petManager.applyHeadTextures(itemStack, pet);
                    menuItemBuilder.on(ClickType.LEFT, player -> {
                        if(!player.hasPermission("pets.user.menu." + petToOpen)) {
                            player.sendMessage(ChatColor.RED + "You don't have enough permissions to use this pet");
                            return;
                        }

                        if(petManager.disable(player)) {
                            player.sendMessage(StringUtils.color("&aYou disabled your pet pet."));
                            return;
                        }

                        petManager.enablePet(player, pet);
                        player.sendMessage(StringUtils.color("&aYou enabled your &6" + pet.getName() + "&a pet."));
                        player.closeInventory();
                    });
                }

                simpleMenu.addItem(slot, menuItemBuilder.build());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Item '" + itemSlot + "' in Pet Menu is not an integer.");
            }
        }

        this.menu = simpleMenu;
    }

}
