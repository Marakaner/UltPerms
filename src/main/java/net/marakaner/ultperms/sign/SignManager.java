package net.marakaner.ultperms.sign;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.utils.SimpleLocation;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class SignManager {

    private final Gson gson = UltPerms.getInstance().getGson();
    private List<UltSign> signs = new ArrayList<>();
    private Type signListType = new TypeToken<List<UltSign>>() {}.getType();

    private BukkitTask bukkitTask;

    public SignManager() {
        loadConfig();
        initSigns();
        initTimer();
    }

    private void initTimer() {
        this.bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                for(UltSign sign : signs) {
                    sign.updateLines();
                }
            }
        }.runTaskTimer(UltPerms.getInstance(), 0, 20 * 5);
    }

    public void createSign(Sign sign, UUID player) {
        UltSign ultSign = new UltSign();
        ultSign.setLocation(new SimpleLocation(sign.getLocation()));
        ultSign.setPlayer(player);
        ultSign.initSign();
        ultSign.updateLines();
        this.signs.add(ultSign);
        saveSigns();
    }

    public void removeSign(Sign sign) {
        UltSign ultSign = getSign(new SimpleLocation(sign.getLocation()));
        this.signs.remove(ultSign);
        saveSigns();
    }

    public UltSign getSign(SimpleLocation simpleLocation) {
        for(UltSign sign : signs) {
            if(sign.getLocation().equals(simpleLocation)) {
                return sign;
            }
        }
        return null;
    }

    private void saveSigns() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    File file = new File("plugins/UltPerms/Signs.json");

                    if(!file.exists()) file.createNewFile();

                    FileWriter fileWriter = new FileWriter(file);
                    gson.toJson(new ArrayList<>(signs), fileWriter);
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }.runTaskAsynchronously(UltPerms.getInstance());
    }

    private void initSigns() {
        for(UltSign sign : signs) {
            sign.initSign();
            sign.updateLines();
        }
    }

    private void loadConfig() {

        File file = new File("plugins/UltPerms/Signs.json");

        if(file.exists()) {
            try {
                Reader reader = Files.newBufferedReader(file.toPath());
                List<UltSign> signList = gson.fromJson(reader, signListType);
                reader.close();

                this.signs = signList;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
