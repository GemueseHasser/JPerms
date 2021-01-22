package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class OnJoin implements Listener {

    public static final Scoreboard SCOREBOARD = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(@NotNull final PlayerJoinEvent e) {
        Player player = e.getPlayer();
        File file = new File("plugins/JPerms", "Users.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String group : JPerms.getInstance().getConfig().getStringList("Config.Groups.groupNames")) {
            for (String username : cfg.getStringList("Users." + group)) {
                if (player.getName().equalsIgnoreCase(username)) {
                    injectPermissibleBase(player);
                    setTablist(player);
                    return;
                }
            }
        }
        List<String> currentUsers = cfg.getStringList("Users." + JPerms.getInstance().getConfig().getString(
            "Config.Groups"
                + ".defaultGroup"));
        currentUsers.add(player.getName());
        cfg.set("Users." + JPerms.getInstance().getConfig().getString("Config.Groups.defaultGroup"), currentUsers);
        try {
            cfg.save(file);
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
        injectPermissibleBase(player);
        setTablist(player);
    }

    private void setTablist(@NotNull final Player player) {
        for (String group : JPerms.getInstance().getConfig().getStringList("Config.Groups.groupNames")) {
            File file = new File("plugins/JPerms", "Users.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            for (String name : cfg.getStringList("Users." + group)) {
                Objects.requireNonNull(SCOREBOARD.getTeam(JPerms.getInstance().getConfig().getInt("Config.Groups." + group + ".rank")
                    + group))
                    .addEntry(name);
            }
            player.setScoreboard(SCOREBOARD);
        }
    }

    public void injectPermissibleBase(@NotNull final Player player) {
        try {
            Field field = CraftHumanEntity.class.getDeclaredField("perm");
            field.setAccessible(true);
            field.set(player, new PermsBase(player));
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
