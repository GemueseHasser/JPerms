package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class PermsBase extends PermissibleBase {

    private final Player player;

    public PermsBase(@Nullable final Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean hasPermission(@NotNull final String permission) {
        List<String> permissions = JPerms.getInstance().getConfig().getStringList("Config.Groups." + getGroup(player)
         + ".permissions");
        for (String perm : permissions) {
            if (perm.equalsIgnoreCase("*")) {
                return true;
            }
            if (perm.equalsIgnoreCase(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull final Permission permission) {
        return super.hasPermission(permission.getName());
    }

    private String getGroup(@NotNull final Player player) {
        File file = new File("plugins/JPerms", "Users.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String group : JPerms.getInstance().getConfig().getStringList("Config.Groups.groupNames")) {
            if (cfg.getStringList("Users." + group).contains(player.getName())) {
                return group;
            }
        }
        return null;
    }

}
