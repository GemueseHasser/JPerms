package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static de.jonas.jperms.JPermCommand.getGroup;

/**
 * Die Chat-Funktion wird hier implementiert.
 */
public class OnChat implements Listener {

    //<editor-fold desc="event-handling">
    @EventHandler
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public void onChat(@NotNull final AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        String message = e.getMessage();
        message = " " + message;
        for (final String code : JPerms.getInstance().getConfig().getStringList("Config.Groups." + getGroup(player)
            + ".allowedColors")
        ) {
            while (message.contains(code)) {
                String[] split = message.split("(?=" + code + ")", 2);
                // in split1 befindet sich nun der code am anfang
                message = split[0] + "§" + split[1].substring(1);
            }
        }

        e.setFormat(Objects.requireNonNull(
            JPerms.getInstance().getConfig().getString("Config.Groups." + getGroup(player) + ".prefix")
            ).replace("&", "§")
                .replace("%name%", player.getName())
                + Objects.requireNonNull(
            JPerms.getInstance().getConfig().getString("Config.Groups." + getGroup(player) + ".defaultChatColor")
            ).replace("&", "§") + message.replace("%", "Prozent")
                .replace(
                    "&",
                    (JPerms.getInstance().getConfig().getStringList("Config.Groups." + getGroup(player)
                        + ".allowedColors").contains("*")) ? "§" : "&"
                )
        );
    }
    //</editor-fold>
}
