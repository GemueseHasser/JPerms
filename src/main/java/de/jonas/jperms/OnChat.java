package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class OnChat implements Listener {

    @EventHandler
    public void onChat(@NotNull final AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        String message = e.getMessage();
        message.replace("%", "Prozent");
        message = " " + message;
        for (String code : JPerms.getInstance().getConfig().getStringList("Config.Groups." + getGroup(player) +
            ".allowedColors")
        ) {
            while (message.contains(code)) {
                String[] split = message.split("(?=" + code + ")", 2);
                // in split1 befindet sich nun der code am anfang
                message = split[0] + "§" + split[1].substring(1);
            }
        }

        e.setFormat(JPerms.getInstance().getConfig().getString("Config.Groups." + getGroup(player) + ".prefix")
            .replace("&", "§")
            .replace("%name%", player.getName())
            + JPerms.getInstance().getConfig().getString("Config.Groups." + getGroup(player) + ".defaultChatColor")
            .replace("&", "§") + message);
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
