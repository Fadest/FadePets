package dev.fadest.pets.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class MenuItem {

    private final ItemStack itemStack;
    private final Map<ClickType, Consumer<Player>> actions;

    public MenuItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.actions = new HashMap<>();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setAction(ClickType clickType, Consumer<Player> action) {
        this.actions.put(clickType, action);
    }

    protected void execute(ClickType clickType, Player player) {
        Optional.ofNullable(this.actions.get(clickType)).ifPresent(action -> action.accept(player));
    }

}
