package net.marakaner.ultperms.language.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.marakaner.ultperms.language.Language;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class LanguageConfig {

    public String prefix;
    public List<Language> languages;

}
