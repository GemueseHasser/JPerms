package de.jonas.jperms;

import de.jonas.JPerms;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.jonas.jperms.JPermCommand.getGroup;

/**
 * Hier wird die {@link PermissibleBase} überschrieben, sodass die Permissions in der Config funktionieren und diese
 * überschriebene {@link PermissibleBase} wird dann jedem einzelnen Spieler beim Joinen injiziert.
 */
public class PermsBase extends PermissibleBase {

    //<editor-fold desc="LOCAL FIELDS">
    /** Der Spieler, auf den sich die {@link PermissibleBase} bezieht. */
    private final Player player;
    //</editor-fold>

    //<editor-fold desc="CONSTRUCTORS">
    /**
     * Erstellt eine neue Instanz der {@link PermsBase}, welche auf einen Spieler bezogen ist und dem diese dann beim
     * joinen injiziert wird.
     *
     * @param player Der Spieler, auf den sich diese {@link PermissibleBase} bezieht.
     */
    public PermsBase(@Nullable final Player player) {
        super(player);
        this.player = player;
    }
    //</editor-fold>

    //<editor-fold desc="implementation">
    @Override
    public boolean hasPermission(@NotNull final String permission) {
        assert player != null;
        List<String> permissions = JPerms.getInstance().getConfig().getStringList(
            "Config.Groups." + getGroup(player) + ".permissions"
        );
        for (final String perm : permissions) {
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
    //</editor-fold>
}
