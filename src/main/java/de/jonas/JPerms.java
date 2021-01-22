package de.jonas;

import de.jonas.jperms.JPermCommand;
import de.jonas.jperms.OnChat;
import de.jonas.jperms.OnJoin;
import de.jonas.jperms.TabComplete;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Die Haupt- und Main-Klasse des Plugins. Hier läuft alles zusammen und wird registriert.
 */
public class JPerms extends JavaPlugin {

    /** Der Prefix des Plugins, welcher vor allen Nachrichten steht, die von diesem Plugin versendet werden. */
    public static final String PREFIX = getPrefix();
    /**
     * Die {@link ConsoleCommandSender Konsole}, der die Nachrichten für die Aktivierung und Deaktivierung des
     * Plugins gesendet werden.
     */
    private static final ConsoleCommandSender CONSOLE = Bukkit.getConsoleSender();

    /** Die Instanz, mit der auf das Plugin zugegriffen werden kann. */
    @Getter
    private static JPerms instance;

    @Override
    public void onEnable() {
        instance = this;
        CONSOLE.sendMessage(PREFIX + "Das Plugin wurde erfolgreich aktiviert! by Gemuese_Hasser / Jonas0206");

        Objects.requireNonNull(getCommand("jperms")).setExecutor(new JPermCommand());
        Objects.requireNonNull(getCommand("jperms")).setTabCompleter(new TabComplete());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new OnJoin(), this);
        pm.registerEvents(new OnChat(), this);

        loadConfig();

        registerGroupsInScoreBoard();
    }

    @Override
    public void onDisable() {
        CONSOLE.sendMessage(PREFIX + "Das Plugin wurde deaktiviert! by Gemuese_Hasser / Jonas0206");
    }

    /**
     * Lädt die Config und dessen voreingestellte Werte.
     */
    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Gibt den Prefix zurück, der vor allen Nachrichten des Plugins stehen soll.
     */
    private static String getPrefix() {
        return ChatColor.GOLD + "" + ChatColor.BOLD + "["
            + ChatColor.WHITE + "J"
            + ChatColor.RED + "P"
            + ChatColor.WHITE + "e"
            + ChatColor.RED + "r"
            + ChatColor.WHITE + "m"
            + ChatColor.RED + "s"
            + ChatColor.GOLD + "" + ChatColor.BOLD + "]"
            + ChatColor.GRAY + " ";
    }

    /**
     * Registriert alle in der Config eingetragenen Permission-Gruppen, für das Scoreboard.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private void registerGroupsInScoreBoard() {
        for (final String group : JPerms.getInstance().getConfig().getStringList("Config.Groups.groupNames")) {
            OnJoin.SCOREBOARD.registerNewTeam(
                JPerms.getInstance().getConfig().getInt("Config.Groups." + group + ".rank") + group
            );
            Objects.requireNonNull(
                OnJoin.SCOREBOARD.getTeam(
                    JPerms.getInstance().getConfig().getInt("Config.Groups." + group + ".rank") + group
                )
            ).setPrefix(
                Objects.requireNonNull(
                    JPerms.getInstance().getConfig().getString("Config.Groups." + group + ".tablist")
                ).replace("&", "§")
            );
            String color = JPerms.getInstance().getConfig().getString("Config.Groups." + group + ".tablistNameColor");
            assert color != null;
            Objects.requireNonNull(
                OnJoin.SCOREBOARD.getTeam(
                    JPerms.getInstance().getConfig().getInt("Config.Groups." + group + ".rank") + group
                )
            ).setColor(org.bukkit.ChatColor.valueOf(color.toUpperCase()));
        }
    }

}
