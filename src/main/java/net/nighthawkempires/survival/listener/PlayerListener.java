package net.nighthawkempires.survival.listener;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.nighthawkempires.core.NECore;
import net.nighthawkempires.core.events.UserDeathEvent;
import net.nighthawkempires.core.events.UserPreDeathEvent;
import net.nighthawkempires.core.language.Lang;
import net.nighthawkempires.core.utils.ItemUtil;
import net.nighthawkempires.core.utils.RandomUtil;
import net.nighthawkempires.survival.NESurvival;
import net.nighthawkempires.survival.user.UserModel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spigotmc.event.entity.EntityMountEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserModel user = NESurvival.getUserRegistry().getUser(player.getUniqueId());
    }

    @EventHandler
    public void onDeath(UserPreDeathEvent event) throws InvocationTargetException, IllegalAccessException {
        Player player = event.getPlayer();
        UserModel user = NESurvival.getUserRegistry().getUser(player.getUniqueId());

        if (event.getEntityKiller() != null) {
            if (event.getEntityKiller() instanceof Player) {
                Player killer = (Player) event.getEntityKiller();
                UserModel kuser = NESurvival.getUserRegistry().getUser(killer.getUniqueId());

                if (isSword(killer.getInventory().getItemInMainHand().getType())) {
                    int chance;
                    switch (killer.getInventory().getItemInMainHand().getType()) {
                        case WOOD_SWORD: chance = 10; break;
                        case STONE_SWORD: chance = 20; break;
                        case IRON_SWORD: chance = 35; break;
                        case GOLD_SWORD: chance = 45; break;
                        case DIAMOND_SWORD: chance = 65; break;
                        default: chance = 10; break;
                    }

                    if (RandomUtil.chance(chance)) {
                        if (killer.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                            String item = NECore.getCodeHandler().getItemStackInfo(killer.getInventory().getItemInMainHand());
                            String message = Lang.CHAT_TAG.getServerMessage(ChatColor.BLUE + player.getName() + ChatColor.GRAY +
                                            " was decapitated by " + ChatColor.BLUE + killer.getName() + ChatColor.GRAY + " using ");
                            TextComponent compMessage = new TextComponent(message);
                            BaseComponent[] itemComp = new BaseComponent[] {
                                    new TextComponent(item)
                            };
                            TextComponent itemHover = new TextComponent(killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
                            itemHover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemComp));
                            compMessage.addExtra(itemHover);
                            compMessage.addExtra(".");
                            event.setDeathComponent(compMessage);
                            player.getWorld().dropItemNaturally(player.getLocation(), ItemUtil.getSkull(player.getName(),
                                    player.getName() + "'s Head", 1));
                        } else {
                            event.setDeathMessage(Lang.CHAT_TAG.getServerMessage(ChatColor.BLUE + player.getName() + ChatColor.GRAY
                                    + " was decapitated by " + ChatColor.BLUE + killer.getName() + ChatColor.GRAY + "."));
                            player.getWorld().dropItemNaturally(player.getLocation(), ItemUtil.getSkull(player.getName(),
                                    player.getName() + "'s Head", 1));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(UserDeathEvent event) {
        Player player = event.getPlayer();
        UserModel user = NESurvival.getUserRegistry().getUser(player.getUniqueId());
        if (event.getEntityKiller() != null) {
            if (event.getEntityKiller() instanceof Player) {
                Player killer = (Player) event.getEntityKiller();
                UserModel kuser = NESurvival.getUserRegistry().getUser(killer.getUniqueId());

                user.setDeaths(user.getDeaths() + 1);
                kuser.setKills(kuser.getKills() + 1);
            } else if (event.getEntityKiller() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getEntityKiller();
                if (projectile.getShooter() instanceof Player) {
                    Player killer = (Player) event.getEntityKiller();
                    UserModel kuser = NESurvival.getUserRegistry().getUser(killer.getUniqueId());

                    user.setDeaths(user.getDeaths() + 1);
                    kuser.setKills(kuser.getKills() + 1);
                }
            }
        }
    }

    @EventHandler
    public void onSpawnerMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled())return;
        if (player.hasPermission("ne.mine.spawners")) {
            if (event.getBlock().getType() == Material.MOB_SPAWNER) {
                if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    player.sendMessage(Lang.CHAT_TAG.getServerMessage(ChatColor.RED + "You can not mine spawners with the fortune enchantment."));
                    event.setCancelled(true);
                    return;
                }

                if (!isPickaxe(player.getInventory().getItemInMainHand().getType())) {
                    player.sendMessage(Lang.CHAT_TAG.getServerMessage(ChatColor.RED + "You can only mine spawners with a pickaxe."));
                    event.setCancelled(true);
                    return;
                }

                if (!player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                    player.sendMessage(Lang.CHAT_TAG.getServerMessage(ChatColor.RED + "You can only mine spawners with a pickaxe that has silk touch."));
                    event.setCancelled(true);
                    return;
                }

                CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
                String type;
                switch (spawner.getSpawnedType()) {
                    case ZOMBIE:
                        type = ChatColor.DARK_GREEN + "Zombie";
                        break;
                    case SKELETON:
                        type = ChatColor.WHITE + "Skeleton";
                        break;
                    case PIG:
                        type = ChatColor.LIGHT_PURPLE + "Pig";
                        break;
                    case SPIDER:
                        type = ChatColor.DARK_PURPLE + "Spider";
                        break;
                    case CAVE_SPIDER:
                        type = ChatColor.DARK_PURPLE + "Cave Spider";
                        break;
                    case BLAZE:
                        type = ChatColor.GOLD + "Blaze";
                        break;
                    case SILVERFISH:
                        type = ChatColor.GRAY + "Silverfish";
                        break;
                    case HUSK:
                        type = ChatColor.YELLOW + "Husk";
                        break;
                    case ZOMBIE_VILLAGER:
                        type = ChatColor.DARK_GREEN + "Zombie Villager";
                        break;
                    default:
                        type = ChatColor.GRAY + spawner.getSpawnedType().getEntityClass().getName();
                        break;
                }

                ItemStack mobSpawner = new ItemStack(Material.MOB_SPAWNER, 1);
                ArrayList<String> lore = Lists.newArrayList(type + ChatColor.GRAY + " Spawner");
                ItemMeta meta = mobSpawner.getItemMeta();
                meta.setLore(lore);
                mobSpawner.setItemMeta(meta);

                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), mobSpawner);
                event.setExpToDrop(0);
            }
        }
    }

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled())return;

        ItemStack placed = player.getInventory().getItemInMainHand();
        if (player.getInventory().getItemInMainHand().getType().equals(Material.MOB_SPAWNER)) {
            placed = player.getInventory().getItemInMainHand();
        } else if (player.getInventory().getItemInOffHand().getType().equals(Material.MOB_SPAWNER)) {
            placed = player.getInventory().getItemInOffHand();
        }

        if (placed.getType().equals(Material.MOB_SPAWNER)) {
            if (placed.getItemMeta().hasLore()) {
                if (placed.getItemMeta().getLore().toString().toLowerCase().contains("spawner")) {
                    CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
                    EntityType entityType;
                    String lore = placed.getItemMeta().getLore().toString();
                    String type = ChatColor.stripColor(lore).replaceAll(" Spawner", "").replaceAll(" ", "_").replaceAll("]", "").replaceAll("\\[", "");
                    entityType = EntityType.valueOf(type.toUpperCase());
                    spawner.setSpawnedType(entityType);
                    spawner.update();
                }
            }
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (event.getMount() instanceof Donkey) {
            Donkey donkey = (Donkey) event.getMount();
            donkey.setJumpStrength(100.0);
            ((EntityLiving)((CraftEntity)donkey).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(3);
            ((EntityLiving)((CraftEntity)donkey).getHandle()).getAttributeInstance(GenericAttributes.maxHealth).setValue(1000.0);
        }
    }

    private boolean isPickaxe(Material material) {
        return material.name().toLowerCase().contains("pickaxe");
    }

    private boolean isSword(Material material) {
        return material.name().toLowerCase().contains("sword");
    }
}
