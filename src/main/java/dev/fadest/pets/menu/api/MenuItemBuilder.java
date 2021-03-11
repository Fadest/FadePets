package dev.fadest.pets.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MenuItemBuilder {

    private final ItemStack itemStack;
    private final Map<ClickType, Consumer<Player>> actions;

    /**
     * Creates a new builder from an ItemStack.
     *
     * @param itemStack The ItemStack this MenuItem will have.
     */
    public MenuItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.actions = new HashMap<>();
    }

    /**
     * Registers an action to be performed when this MenuItem is clicked.
     *
     * @param clickType The click type for this action.
     * @param action    The action to be performed on the player initiating the click.
     * @return The builder.
     */
    public MenuItemBuilder on(ClickType clickType, Consumer<Player> action) {
        this.actions.put(clickType, action);
        return this;
    }

    /**
     * Builds the MenuItem.
     *
     * @return The MenuItem object.
     */
    public MenuItem build() {
        MenuItem item = new MenuItem(this.itemStack);
        for (Map.Entry<ClickType, Consumer<Player>> entry : this.actions.entrySet()) {
            item.setAction(entry.getKey(), entry.getValue());
        }
        return item;
    }
}

