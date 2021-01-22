package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Hier wird geregelt, was als Tab-Complete vorgeschlagen wird, wenn der Spieler den {@link JPermCommand} ausführt.
 */
public class TabComplete implements TabCompleter {
    //<editor-fold desc="implementation">
    @Nullable
    @Override
    public List<String> onTabComplete(
        @NotNull final CommandSender sender,
        @NotNull final Command cmd,
        @NotNull final String label,
        @NotNull final String[] args
    ) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (final Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            names.add("rl");
            return names;
        } else if (args.length == 2) {
            return new ArrayList<>(JPerms.getInstance().getConfig().getStringList(
                "Config.Groups.groupNames"));
        }
        return null;
    }
    //</editor-fold>
}
