package dev.fadest.pets.manager;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.fadest.pets.FadePets;
import dev.fadest.pets.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class PetManager {

    private final FadePets plugin;
    private final Map<String, Pet> pets;
    private final Map<UUID, Pair<BukkitRunnable, String>> enabledPets;

    public PetManager(FadePets plugin) {
        this.plugin = plugin;
        this.pets = new HashMap<>();
        this.enabledPets = new HashMap<>();
    }

    public void loadPets() {
        pets.clear();

        FileConfiguration fileConfiguration = plugin.getConfig();
        ConfigurationSection petsConfigurationSection = fileConfiguration.getConfigurationSection("pets");
        if (petsConfigurationSection == null) return;
        for (String petName : petsConfigurationSection.getKeys(false)) {
            ConfigurationSection petConfigurationSection = petsConfigurationSection.getConfigurationSection(petName);
            if(petConfigurationSection != null) {
                String skin = petConfigurationSection.getString("skin", "Fadest");

                Set<PotionEffect> potionEffects = new HashSet<>();
                petConfigurationSection.getStringList("effects").forEach(s -> {
                    String[] potionEffectKeySplit = s.split(":");
                    if(potionEffectKeySplit.length == 2) {
                        potionEffects.add(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(potionEffectKeySplit[0])),
                                Integer.MAX_VALUE, Integer.parseInt(potionEffectKeySplit[1])));
                    }
                });

                Pet pet = new Pet(petName, skin, potionEffects);
                pets.put(petName, pet);
            }
        }
    }

    public void enablePet(Player player, Pet pet) {
        disable(player);

        ArmorStand armorStand = spawnPet(player, pet);

        pet.getPotionEffects().forEach(potionEffect -> potionEffect.apply(player));
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                armorStand.setRotation(player.getLocation().getYaw(), armorStand.getLocation().getPitch());
            }
        };
        bukkitRunnable.runTaskTimerAsynchronously(plugin, 5L, 1L);
        enabledPets.put(player.getUniqueId(), Pair.of(bukkitRunnable, pet.getName()));
    }

    private ArmorStand spawnPet(Player player, Pet pet) {
        ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        applyHeadTextures(itemStack, pet).whenComplete((unused, throwable) -> {
            Objects.requireNonNull(armorStand.getEquipment()).setHelmet(itemStack);
            player.addPassenger(armorStand);
        }).complete(null);
        return armorStand;
    }

    public boolean disable(Player player) {
        Pair<BukkitRunnable, String> pair = enabledPets.remove(player.getUniqueId());
        if(pair != null) {
            pair.getKey().cancel();

            player.getPassengers().stream()
                    .filter(entity -> entity.getType() == EntityType.ARMOR_STAND)
                    .findAny()
                    .ifPresent(Entity::remove);

            Pet pet = fromName(pair.getValue());
            if(pet != null) {
                pet.getPotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Pet fromName(String name) {
        AtomicReference<Pet> atomicReference = new AtomicReference<>(null);
        pets.forEach((petName, petObject) -> {
            if (petName.equalsIgnoreCase(name)) {
                atomicReference.set(petObject);
            }
        });
        return atomicReference.get();
    }

    public CompletableFuture<Void> applyHeadTextures(ItemStack itemStack, Pet pet) {
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        String skin = pet.getSkin();
        return CompletableFuture.runAsync(() -> {
            //Skin is a Player Name
            if(skin.length() <= 16) {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skin));
            } else {
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
                profile.getProperties().add(new ProfileProperty("textures", skin));
                skullMeta.setPlayerProfile(profile);
            }
            itemStack.setItemMeta(skullMeta);
        });
    }
}
