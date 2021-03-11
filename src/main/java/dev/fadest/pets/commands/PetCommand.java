package dev.fadest.pets.commands;

import dev.fadest.pets.manager.Pet;
import dev.fadest.pets.manager.PetManager;
import dev.fadest.pets.menu.PetMenu;
import dev.fadest.pets.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PetCommand implements CommandExecutor {

    private final PetManager petManager;
    private final PetMenu petMenu;

    public PetCommand(PetManager petManager, PetMenu petMenu) {
        this.petManager = petManager;
        this.petMenu = petMenu;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You need to be a player in order to use this command");
                return true;
            }

            Player player = (Player) sender;
            petMenu.getMenu().open(player);
        } else if(args[0].equalsIgnoreCase("enable")) {
            if(!sender.hasPermission("pets.admin.enable")) {
                sender.sendMessage(ChatColor.RED + "You don't have enough permissions to use this command");
                return true;
            }

            if(args.length < 3) {
                sender.sendMessage(StringUtils.color("&7/&6pets enable &a{player} {pet} &7- &3Enables the pet to that " +
                        "specific user and overrides his existing one (if he's using one)."));
                return true;
            }

            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if(player == null) {
                sender.sendMessage(ChatColor.RED + "The player " + playerName + " is not online.");
                return true;
            }

            String petName = args[2];
            Pet pet = petManager.fromName(petName);
            if(pet == null) {
                sender.sendMessage(ChatColor.RED + "The pet " + playerName + " doesn't exist.");
                return true;
            }

            petManager.enablePet(player, pet);
            sender.sendMessage(StringUtils.color("&aYou enabled &6" + pet.getName() + "&a pet to &6" + playerName + "&a."));
        } else if(args[0].equalsIgnoreCase("disable")) {
            if(!sender.hasPermission("pets.admin.disable")) {
                sender.sendMessage(ChatColor.RED + "You don't have enough permissions to use this command");
                return true;
            }

            if(args.length < 2) {
                sender.sendMessage(StringUtils.color("&7/&6pets disable &a{player} &7- &3Removes his current pet (if he's using one)"));
                return true;
            }

            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if(player == null) {
                sender.sendMessage(ChatColor.RED + "The player " + playerName + " is not online.");
                return true;
            }

            if(petManager.disable(player)) {
                sender.sendMessage(StringUtils.color("&aYou disabled &6" + playerName + "&a pet."));
            } else {
                sender.sendMessage(StringUtils.color("&6" + playerName + "&c doesn't have an active pet."));
            }
        } else {
            sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Correct Usage:");
        if(sender.hasPermission("pets.user.menu")) {
            sender.sendMessage(StringUtils.color("&7/&6pets &7- &3This displays a fully configurable GUI of all the" +
                    " pets in the server."));
        }
        if(sender.hasPermission("pets.admin.enable")) {
            sender.sendMessage(StringUtils.color("&7/&6pets enable &a{player} {pet} &7- &3Enables the pet to that " +
                    "specific user and overrides his existing one (if he's using one)."));
        }
        if(sender.hasPermission("pets.admin.disable")) {
            sender.sendMessage(StringUtils.color("&7/&6pets disable &a{player} &7- &3Removes his current pet (if he's using one)"));

        }
    }

}
