package dev.fadest.pets.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.stream.Collectors;

public class ItemUtils {

    public static ItemStack loadItem(ConfigurationSection configurationSection) {
        String materialName = configurationSection.getString("material", "PLAYER_HEAD");
        Material material = Material.getMaterial(Objects.requireNonNull(materialName));
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(material), configurationSection.getInt("amount", 1));

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (configurationSection.isSet("enchants")) {
            for (String s : configurationSection.getStringList("enchants")) {
                String[] splitEnchant = s.split(":");
                itemStack.addEnchantment(Objects.requireNonNull(Enchantment.getByName(splitEnchant[0])),
                        Integer.parseInt(splitEnchant[1]));
            }
        }
        if (configurationSection.contains("lore")) {
            itemMeta.setLore(configurationSection.getStringList("lore").stream().map(StringUtils::color).collect(Collectors.toList()));
        }
        if (configurationSection.contains("name")) {
            itemMeta.setDisplayName(StringUtils.color(configurationSection.getString("name")));
        }
        if (configurationSection.isSet("unbreakable")) {
            itemMeta.setUnbreakable(configurationSection.getBoolean("unbreakable"));
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
