package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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
                    setCurrentPermissions(player, group);
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
        setCurrentPermissions(
            player,
            Objects.requireNonNull(JPerms.getInstance().getConfig().getString("Config.Groups.defaultGroup"))
        );
        setTablist(player);
    }

    private void setCurrentPermissions(@NotNull final Player player, @NotNull final String group) {
        List<String> permissions = JPerms.getInstance().getConfig().getStringList("Config.Groups." + group +
            ".permissions");
        for (final PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions()) {
            playerRemoveTransient(player, permissionAttachmentInfo.getPermission());
        }
        for (String permission : permissions) {
            playerAddTransient(player, permission);
        }
        for (final PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions()) {
            System.out.println(permissionAttachmentInfo.getPermission());
        }
    }

    public void playerRemoveTransient(Player player, String permission) {
        for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
            if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(JPerms.getInstance())) {
                paInfo.getAttachment().unsetPermission(permission);
                return;
            }
        }
    }

    public void playerAddTransient(Player player, String permission) {
        for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
            if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(JPerms.getInstance())) {
                paInfo.getAttachment().setPermission(permission, true);
                return;
            }
        }

        PermissionAttachment attach = player.addAttachment(JPerms.getInstance());
        attach.setPermission(permission, true);
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

}
