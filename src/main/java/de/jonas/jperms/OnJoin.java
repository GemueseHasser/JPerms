package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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

/**
 * Die Permissions werden beim Joinen durch dieses {@link Event Event} jedem einzelnen Spieler injiziert und das
 * Permissions-System aktualisiert den Spieler, zufalls er sich schon mal auf dem Netzwerk befand, oder wenn dies nicht
 * der Fall ist, wird er im Permissions-System registriert.
 */
public class OnJoin implements Listener {

    //<editor-fold desc="CONSTANTS">
    /** Das {@link Scoreboard Scoreboard}, welches jedem Spieler in Form von der Tabliste gesetzt wird. */
    public static final Scoreboard SCOREBOARD = Objects.requireNonNull(
        Bukkit.getScoreboardManager()
    ).getNewScoreboard();
    //</editor-fold>

    //<editor-fold desc="event-handling">
    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public void onJoin(@NotNull final PlayerJoinEvent e) {
        Player player = e.getPlayer();
        File file = new File("plugins/JPerms", "Users.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (final String group : JPerms.getInstance().getConfig().getStringList("Config.Groups.groupNames")) {
            for (final String username : cfg.getStringList("Users." + group)) {
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
    //</editor-fold>

    /**
     * Der jeweilige {@link Player Spieler} wird im Scoreboard (welches in Form der Tabliste dargestellt wird)
     * registriert und jenes Scoreboard wird ihm dann gesetzt. Zudem wird es für alle anderen Spieler aktualisiert.
     *
     * @param player Der Spieler, der registriert wird und dem das Scoreboard gesetzt wird.
     */
    private void setTablist(@NotNull final Player player) {
        for (final String group : JPerms.getInstance().getConfig().getStringList("Config.Groups.groupNames")) {
            File file = new File("plugins/JPerms", "Users.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            for (final String name : cfg.getStringList("Users." + group)) {
                Objects.requireNonNull(
                    SCOREBOARD.getTeam(
                        JPerms.getInstance().getConfig().getInt("Config.Groups." + group + ".rank") + group
                    )
                ).addEntry(name);
            }
            player.setScoreboard(SCOREBOARD);
        }
    }

    /**
     * Dem jeweiligen Spieler werden die Permissions seiner jeweiligen Gruppe injiziert.
     *
     * @param player Der Spieler, dem die Permissions injiziert werden.
     */
    public void injectPermissibleBase(@NotNull final Player player) {
        try {
            Field field = CraftHumanEntity.class.getDeclaredField("perm");
            field.setAccessible(true);
            field.set(player, new PermsBase(player));
            field.setAccessible(false);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
