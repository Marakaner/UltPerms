package net.marakaner.ultperms.language;

import com.google.common.reflect.TypeToken;
import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.group.Group;
import net.marakaner.ultperms.language.config.LanguageConfig;
import net.marakaner.ultperms.language.config.LanguageMainConfig;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LanguageManager {

    private LanguageMainConfig mainConfig;
    private LanguageConfig defaultConfig;
    private LanguageConfig defaultGermanConfig;

    public LanguageManager() {

    }

    private void initMainConfig() {
        File file = new File("plugins/UltPerms/Languages.json");

        if(file.exists()) {
            try {
                Reader reader = Files.newBufferedReader(file.toPath());
                mainConfig = UltPerms.getInstance().getGson().fromJson(reader, LanguageMainConfig.class);
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

            mainConfig = new LanguageMainConfig();
            mainConfig.prefix = "&7[&5UltPerms&7] ";
            mainConfig.languages = Arrays.asList("en-US", "de-DE");

        }
    }

    private void initDefaultConfig() {

        File folder = new File("plugins/UltPerms/language/");

        if(!folder.exists()) folder.mkdir();

        HashMap<String, String> messages = new HashMap<>();
        messages.put("command.helpmap.first", "&3");

        defaultConfig = new LanguageConfig("en-US", messages);

    }



}
