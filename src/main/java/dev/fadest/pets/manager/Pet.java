package dev.fadest.pets.manager;

import lombok.Data;
import org.bukkit.potion.PotionEffect;

import java.util.Set;

@Data
public class Pet {
    private final String name, skin;
    private final Set<PotionEffect> potionEffects;
}
