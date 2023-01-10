package net.marakaner.ultperms.sign;

import com.google.gson.Gson;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.database.DatabaseManager;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.language.ReplacementBuilder;
import net.marakaner.ultperms.player.PlayerManager;
import net.marakaner.ultperms.utils.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SignManager {

    private final Gson gson = UltPerms.getInstance().getGson();


    private final IDocument generalConfig;
    private final PlayerManager playerManager;
    private final DatabaseManager databaseManager;

    private final List<UltSign> cachedSigns = new ArrayList<>();

    public SignManager(PlayerManager playerManager, DatabaseManager databaseManager, IDocument generalConfig, Consumer<Boolean> finised) {
        this.playerManager = playerManager;
        this.databaseManager = databaseManager;
        this.generalConfig = generalConfig;
        new BukkitRunnable() {
            @Override
            public void run() {
                createTables();
                loadSigns();
                finised.accept(true);
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    private void loadSigns() {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("SELECT * FROM signs");
            rs = ps.executeQuery();
            while (rs.next()) {
                UltSign sign = new UltSign();

                sign.setId(UUID.fromString(rs.getString("id")));
                sign.setLocation(gson.fromJson(rs.getString("location"), SimpleLocation.class));
                sign.setUser(UUID.fromString(rs.getString("user")));

                Block block = Bukkit.getWorld(sign.getLocation().getWorld()).getBlockAt(sign.getLocation().getX(), sign.getLocation().getY(), sign.getLocation().getZ());
                if(!(block.getState() instanceof Sign)) {
                    deleteSign(sign);
                    return;
                }

                Sign bukkitSign = (Sign) block.getState();
                sign.setSign(bukkitSign);
                this.cachedSigns.add(sign);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void saveSigns() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(UltSign sign : cachedSigns) {
                    dataUpdateSign(sign);
                }
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    private void createSign(UltSign ultSign) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;

                try {
                    ps = databaseManager.getConnection().prepareStatement("INSERT INTO signs (id, location, user) VALUES (?,?,?)");
                    ps.setString(1, ultSign.getId().toString());
                    ps.setString(2, gson.toJson(ultSign.getLocation()));
                    ps.setString(3, ultSign.getUser().toString());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    private void saveSign(UltSign ultSign) {
        PreparedStatement ps = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("UPDATE signs SET location=?, user=? WHERE id=?");
            ps.setString(1, gson.toJson(ultSign.getLocation()));
            ps.setString(2, ultSign.getUser().toString());
            ps.setString(3, ultSign.getId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createSign(Location location, UUID player) {

        UltSign ultSign = new UltSign();
        ultSign.setId(UUID.randomUUID());
        ultSign.setLocation(new SimpleLocation(location));
        ultSign.setUser(player);
        ultSign.setSign((Sign) location.getWorld().getBlockAt(location).getState());

        this.cachedSigns.add(ultSign);
        updateSign(ultSign);
    }

    public UltSign getSignAtLocation(Location location) {
        return getSignAtLocation(new SimpleLocation(location));
    }

    public UltSign getSignAtLocation(SimpleLocation simpleLocation) {
        for(UltSign ultSign : this.cachedSigns) {
            if(ultSign.getLocation().getWorld().equals(simpleLocation.getWorld())
                    && ultSign.getLocation().getX() == simpleLocation.getX()
                    && ultSign.getLocation().getY() == simpleLocation.getY()
                    && ultSign.getLocation().getZ() == simpleLocation.getZ()) {
                return ultSign;
            }
        }
        return null;
    }

    public void removeSign(UltSign sign) {
        this.cachedSigns.remove(sign);
        deleteSign(sign);
    }

    private void deleteSign(UltSign sign) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;

                try {
                    ps = databaseManager.getConnection().prepareStatement("DELETE FROM signs WHERE id=?");
                    ps.setString(1, sign.getId().toString());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    ps.close();
                }
            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    private void updateSign(UltSign ultSign) {
        getAutoReplacement(ultSign.getUser(), replacements -> {
            Sign sign = ultSign.getSign();

            sign.setLine(0, translateLign(generalConfig.getString("sign.layout.first"), replacements));
            sign.setLine(1, translateLign(generalConfig.getString("sign.layout.second"), replacements));
            sign.setLine(2, translateLign(generalConfig.getString("sign.layout.third"), replacements));
            sign.setLine(3, translateLign(generalConfig.getString("sign.layout.fourth"), replacements));
        });
    }

    private String translateLign(String line, Map<String, String> replacements) {
        for(String replace : replacements.keySet()) {
            line = line.replaceAll(replace, replacements.get(replace));
        }

        line = ChatColor.translateAlternateColorCodes('&', line);

        return line;
    }

    private void getAutoReplacement(UUID uniqueId, Consumer<HashMap<String, String>> replacements) {
        playerManager.getPermissionPlayer(uniqueId, permissionPlayer -> {
            playerManager.getHighestPermissionGroup(uniqueId, group -> {

                long remainingTime = permissionPlayer.getGroups().get(group.getIdentifier()) - System.currentTimeMillis();

                long days = TimeUnit.MILLISECONDS.toDays(remainingTime);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime - TimeUnit.DAYS.toMillis(days));
                long seconds = TimeUnit.MINUTES.toSeconds(remainingTime - TimeUnit.DAYS.toMillis(days) - TimeUnit.MINUTES.toMillis(minutes));

                ReplacementBuilder replacementBuilder = new ReplacementBuilder()
                        .setGroupName(group.getDisplayName())
                        .setGroupColor(group.getColor())
                        .setGroupTime(remainingTime)
                        .setGroupTimeDays(days)
                        .setGroupTimeMinutes(minutes)
                        .setGroupTimeSeconds(seconds)
                        .setGroupTabPrefix(group.getTabPrefix())
                        .setGroupChatPrefix(group.getChatPrefix())
                        .setPlayerName(permissionPlayer.getName())
                        .setPrefix(UltPerms.getInstance().getPrefix());

                replacements.accept(replacementBuilder.build());

            });
        });
    }

    private void createTables() {

        PreparedStatement ps = null;

        try {
            ps = databaseManager.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS signs(id VARCHAR(100), location MEDIUMTEXT, user VARCHAR(100))");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
