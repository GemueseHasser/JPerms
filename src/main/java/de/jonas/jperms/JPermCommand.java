package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static de.jonas.JPerms.PREFIX;

/**
 * Der einzige Command, über den alles ausgeführt wird, wird hier implementiert.
 */
public class JPermCommand implements CommandExecutor {
    //<editor-fold desc="implementation">
    @Override
    public boolean onCommand(
        @NotNull final CommandSender sender,
        @NotNull final Command cmd,
        @NotNull final String label,
        @NotNull final String[] args
    ) {

        if (!sender.hasPermission("jperms")) {
            sender.sendMessage(PREFIX + "Dazu hast du keine Rechte!");
            return true;
        }

        if (!(args.length == 2 || args.length == 1)) {
            sender.sendMessage(PREFIX + "Bitte benutze /jperms <Player> <Group> | /jperms <Player> | /jperms rl");
            return true;
        }

        if (args[0].equalsIgnoreCase("rl")) {
            reloadConfig();
            sender.sendMessage(PREFIX + "Du hast die Config neu geladen!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(PREFIX + "Der Spieler ist nicht auf diesem Netzwerk online!");
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(PREFIX + "Der Spieler " + target.getName() + " befindet sich in der Gruppe"
                + " \"" + getGroup(target) + "\".");
            return true;
        }

        String group = args[1].toLowerCase();

        if (JPerms.getInstance().getConfig().get("Config.Groups." + group) == null) {
            sender.sendMessage(PREFIX + "Diese Gruppe existiert nicht. Registriere sie erst in der Config!");
            return true;
        }

        File file = new File("plugins/JPerms", "Users.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        for (final String groupName : JPerms.getInstance().getConfig().getStringList("Config.Groups.groupNames")) {
            List<String> list = cfg.getStringList("Users." + groupName);
            while (list.contains(target.getName())) {
                list.remove(target.getName());
            }
            cfg.set("Users." + groupName, list);
        }

        List<String> currentUsers = cfg.getStringList("Users." + group);

        currentUsers.add(target.getName());
        cfg.set("Users." + group, currentUsers);
        try {
            cfg.save(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(PREFIX + "Der Spieler " + target.getName() + " befindet sich nun in der Gruppe"
            + " \"" + group + "\"");

        target.kickPlayer(Objects.requireNonNull(JPerms.getInstance().getConfig().getString(
            "Config.Groups.getRankKickMessage")).replace(
            "%rank%",
            Objects.requireNonNull(JPerms.getInstance().getConfig().getString("Config.Groups." + group + ".name"))
        ).replace("&", "§"));
        return true;
    }
    //</editor-fold>

    /**
     * Gibt die jeweilige Permissions-Gruppe des jeweiligen {@link Player Spieler} zurück.
     *
     * @param player Der {@link Player Spieler}, dessen Permissions-Gruppe zurückgegeben wird.
     *
     * @return Die Gruppe des jeweiligen {@link Player Spieler}.
     */
    public static String getGroup(@NotNull final Player player) {
        File file = new File("plugins/JPerms", "Users.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (final String group : JPerms.getInstance().getConfig().getStringList("Config.Groups.groupNames")) {
            if (cfg.getStringList("Users." + group).contains(player.getName())) {
                return group;
            }
        }
        return null;
    }

    /**
     * Lädt die Config neu und kickt alle Spieler, sodass sie durch das Neu-Joinen von der neuen Konfiguration der
     * Permissions in der Config gebrauch machen.
     */
    private void reloadConfig() {
        File file = new File("plugins/JPerms", "config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        try {
            cfg.save(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        for (final Player all : Bukkit.getOnlinePlayers()) {
            all.kickPlayer(Objects.requireNonNull(JPerms.getInstance().getConfig().getString(
                "Config.Groups.getReloadKickMessage")).replace(
                "&", "§"));
        }
    }
}
